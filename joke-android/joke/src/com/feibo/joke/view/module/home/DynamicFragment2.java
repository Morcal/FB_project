package com.feibo.joke.view.module.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.list.VideosDynamicManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.video.manager.UploadVideoManager;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.group.VideoFlowGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.mine.FriendFindFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import fbcore.log.LogUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lidiqing on 15-10-21.
 */
public class DynamicFragment2 extends BaseLoginFragment implements GroupOperator {
    private static final String TAG = DynamicFragment2.class.getSimpleName();

    private static final int LOGIN_REFRESH = 0x1;
    private static final int LOGIN_OPERATE = 0x2;

    private VideosDynamicManager manager;

    private ListViewGroup listGroup;
    private PageViewGroup pageGroup;
    private UploadViewGroup uploadGroup;

    private VideoFlowGroup.OperateData operateData;
    private UploadVideoManager uploadVideoManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new VideosDynamicManager();
        uploadVideoManager = UploadVideoManager.getInstance(getActivity());
        listGroup = new ListViewGroup(getActivity());
    }

    @Override
    public View containChildView() {
        return View.inflate(getActivity(), R.layout.fragment_dynamic2, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        pageGroup = new PageViewGroup(view);
        uploadGroup = new UploadViewGroup(view);
        listGroup.init(view);

        // 视频上传监听
        uploadVideoManager.setOnUploadListener(new UploadVideoManager.OnUploadListener() {
            @Override
            public void onStart(int progress) {
                listGroup.stopVideo();
                uploadGroup.showProgress();
                uploadGroup.setProgress(progress);
            }

            @Override
            public void onFail(int code) {
                ToastUtil.showSimpleToast("上传失败");
                if (getActivity() == null) {
                    return;
                }
                uploadGroup.hideProgress();
                uploadVideoManager.release();
            }

            @Override
            public void onSuccuss(Video video) {
                if (getActivity() == null) {
                    return;
                }
                uploadGroup.showDialog(video);
                onDataChange(DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE);
            }

            @Override
            public void onProgress(int progress) {
                if (getActivity() == null) {
                    return;
                }
                uploadGroup.setProgress(progress);
            }

            @Override
            public void onSaveDraft(boolean succuss) {
                ToastUtil.showSimpleToast(succuss ? "成功保存到草稿箱" : "保存到草稿箱失败");
            }

            @Override
            public void onShareSina(Video video) {
                if (getActivity() == null) {
                    return;
                }
                uploadGroup.shareSina(video);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        listGroup.videoFlowGroup.enablePlay(true);
        uploadVideoManager.uploadVideo(getActivity());

        // 未登录, 显示登录页
        if (!UserManager.getInstance().isLogin()) {
            pageGroup.showLoginPage();
            return;
        }

        // 无数据, 重新加载数据
        if (listGroup.isEmpty()) {
            pageGroup.showLoadPage();
            listGroup.onRefreshData();
            return;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.i(TAG, "setUserVisibleHint:" + isVisibleToUser);
        if (!isVisibleToUser && listGroup != null) {
            listGroup.videoFlowGroup.enablePlay(false);
            listGroup.stopVideo();
            return;
        }

        if (isVisibleToUser && pageGroup != null) {
            listGroup.videoFlowGroup.enablePlay(true);
            // 上传逻辑
            uploadVideoManager.uploadVideo(getActivity());

            // 未登录, 显示登录页
            if (!UserManager.getInstance().isLogin()) {
                pageGroup.showLoginPage();
                return;
            }

            if (listGroup.isEmpty()) {
                // 无数据, 重新加载数据
                pageGroup.showLoadPage();
                listGroup.onRefreshData();
                return;
            } else {
                // 有数据, 播放视频
                //TODO 自动播放视频，需要重新处理
                if (!uploadVideoManager.isUploading()) {
                    listGroup.autoPlayVideo();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
        listGroup.videoFlowGroup.enablePlay(false);
        if (listGroup != null) {
            listGroup.stopVideo();
        }
    }

    @Override
    public void onScrollToTop(boolean refresh) {
        listGroup.scrollToTop(refresh);
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if (result) {
            switch (operationCode) {
                // 刷新
                case LOGIN_REFRESH:
                    listGroup.onRefreshData();
                    break;

                // 操作视频
                case LOGIN_OPERATE:
                    listGroup.operateVideo(operateData);
                    break;
            }
        }
    }

    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        switch (code) {
            case DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE:
                pageGroup.showLoadPage();
                listGroup.onRefreshData();
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
                uploadGroup.hideDialog(true);

                Bundle bundle = ((BaseActivity) getActivity()).getFinishBundle();
                Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
                int adapterPosition = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
                if (adapterPosition == -1) {
                    return;
                }

                // 删除视频
                if (video == null) {
                    pageGroup.showLoadPage();
                    listGroup.onRefreshData();
                    return;
                }

                // 视频信息更新
                listGroup.notifyVideo(video, adapterPosition);
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
                if (getActivity() != null && uploadVideoManager != null) {
                    uploadVideoManager.uploadVideo(getActivity());
                }
                break;
        }
    }

    // 列表页面
    private class ListViewGroup {
        private PtrFrameLayout refreshLayout;
        private VideoFlowGroup videoFlowGroup;
        private boolean isLoading = false;

        public ListViewGroup(Context context) {
            videoFlowGroup = new VideoFlowGroup(context);
        }

        public void init(View view) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            refreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh);

            videoFlowGroup.setRecyclerView(recyclerView);

            // 视频播放单元统计需求
            videoFlowGroup.setVideoMobclickAgent(new VideoFlowGroup.VideoMobclickAgent.Builder(getActivity()).
                    doPlay(UmengConstant.DYNAMIC_PLAY).
                    doHeader(UmengConstant.DYNAMIC_USER_HEAD).
                    doMore(UmengConstant.DYNAMIC_MORE).
                    doComment(UmengConstant.DYNAMIC_COMMENT).
                    doFavor(UmengConstant.DYNAMIC_LIKE).
                    doDoubleFavor(UmengConstant.DYNAMIC_DOUBLE_LIKE).create());

            // 信息流请求登录
            videoFlowGroup.setOnOperateLoginListener(new VideoFlowGroup.OnOperateLoginListener() {
                @Override
                public void executeLogin(VideoFlowGroup.OperateData operateData) {
                    DynamicFragment2.this.operateData = operateData;
                    loginClick(LOGIN_OPERATE);
                }
            });

            // 视频播放单元操作结果的监听
            videoFlowGroup.setOperateResultListener(new VideoFlowGroup.OperateResultListener() {
                @Override
                public void result(int type, boolean success) {
                    if (type == TYPE_DELETE && success) {
                        if (videoFlowGroup.isEmpty()) {
                            pageGroup.showNoAttentionPage();
                        }
                    }
                }
            });

            // 加载更多
            videoFlowGroup.setOnLoadMoreListener(new VideoFlowGroup.OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    onLoadMoreData();
                }
            });

            // 下拉刷新
            refreshLayout.setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }

                @Override
                public void onRefreshBegin(final PtrFrameLayout frame) {
                    onRefreshData();
                }
            });
        }

        public void onRefreshData() {
            LogUtil.i(TAG, "onRefreshData");
            videoFlowGroup.stopVideo();

            if (isLoading) {
                return;
            }
            isLoading = true;
            manager.refresh(new LoadListener() {
                @Override
                public void onSuccess() {
                    LogUtil.i(TAG, "onSuccess");
                    isLoading = false;
                    if (getActivity() == null) {
                        return;
                    }
                    refreshLayout.refreshComplete();
                    if (manager.getDatas() != null && manager.getDatas().size() == 0) {
                        pageGroup.showNoAttentionPage();
                        return;
                    }
                    pageGroup.showListPage();
                    videoFlowGroup.setRefreshVideos(manager.getDatas());
                }

                @Override
                public void onFail(int code) {
                    LogUtil.i(TAG, "onFail");
                    isLoading = false;
                    if (getActivity() == null) {
                        return;
                    }
                    refreshLayout.refreshComplete();
                    switch (code) {
                        case ReturnCode.NO_NET:
                            pageGroup.showErrorPage(getString(R.string.page_no_net));
                            break;
                        case ReturnCode.RS_EMPTY_ERROR:
                            pageGroup.showNoAttentionPage();
                            break;
                        default:
                            if (videoFlowGroup.isEmpty()) {
                                pageGroup.showErrorPage("未知错误,错误码:" + code);
                            }
                            break;
                    }
                }
            });
        }

        public void onLoadMoreData() {
            if (isLoading) {
                return;
            }
            isLoading = true;

            manager.loadMore(new LoadListener() {
                @Override
                public void onSuccess() {
                    if (getActivity() == null) {
                        return;
                    }
                    videoFlowGroup.setLoadVideos(manager.getLoadMoreDatas());
                    isLoading = false;
                }

                @Override
                public void onFail(int code) {
                    isLoading = false;
                    if (getActivity() == null) {
                        return;
                    }
                    switch (code) {
                        case ReturnCode.NO_NET:
                            videoFlowGroup.showFooterText(getString(R.string.loading_default_fail_text));
                            break;
                        case ReturnCode.RS_EMPTY_ERROR:
                        default:
                            videoFlowGroup.showFooterText(getString(R.string.load_more_over_text_dynamic));
                            break;
                    }
                }
            });
        }

        public void operateVideo(VideoFlowGroup.OperateData operateData) {
            videoFlowGroup.operateVideo(operateData);
        }

        public void notifyVideo(Video video, int adapterPosition) {
            videoFlowGroup.notifyVideo(video, adapterPosition);
        }

        public void scrollToTop(boolean refresh) {
            if (refresh) {
                videoFlowGroup.scrollToPosition(0);
                refreshLayout.autoRefresh();
                onRefreshData();
            } else {
                videoFlowGroup.scrollToPosition(0);
            }
        }

        public void stopVideo() {
            videoFlowGroup.stopVideo();
        }

        public void autoPlayVideo() {
            videoFlowGroup.autoPlayVideo();
        }

        public boolean isEmpty() {
            return videoFlowGroup.isEmpty();
        }
    }

    // 上传相关页面
    private class UploadViewGroup {
        private View mUploadView;
        private View mUploadSuccessView;

        public UploadViewGroup(View view) {
            mUploadView = view.findViewById(R.id.progress);
            mUploadSuccessView = view.findViewById(R.id.dialog);

            mUploadSuccessView.setVisibility(View.GONE);
            mUploadView.setVisibility(View.GONE);
            initGroupListener();
        }

        private void initGroupListener() {
            mUploadSuccessView.findViewById(R.id.btn_weibo).setOnClickListener(uploadSuccessClick);
            mUploadSuccessView.findViewById(R.id.btn_weixin).setOnClickListener(uploadSuccessClick);
            mUploadSuccessView.findViewById(R.id.btn_weixin_friend).setOnClickListener(uploadSuccessClick);
            mUploadSuccessView.findViewById(R.id.btn_qzone).setOnClickListener(uploadSuccessClick);
            mUploadSuccessView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog(false);
                }
            });
        }

        private View.OnClickListener uploadSuccessClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video video = uploadVideoManager.getVideo();
                switch (v.getId()) {
                    case R.id.btn_weibo:
                        ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.SINA, null);
                        break;
                    case R.id.btn_weixin:
                        ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.WX_SESSION, shareListener);
                        break;
                    case R.id.btn_weixin_friend:
                        ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.WX_TIMELINE, shareListener);
                        break;
                    case R.id.btn_qzone:
                        ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.QQ_QZONE, shareListener);
                        break;
                }
                hideDialog(false);
            }
        };

        @Deprecated
        private ShareUtil.IShareListener shareListener = new ShareUtil.IShareListener() {
            @Override
            public void onFail(int code) {
                if (code == SocialManager.WEIBO_TOKEN_TIMEOUT) {
                }
            }
        };

        public void showProgress() {
            mUploadSuccessView.setVisibility(View.GONE);
            mUploadView.startAnimation(new AlphaAnimation(0.0f, 1.0f));
            mUploadView.setVisibility(View.VISIBLE);
        }

        public void hideProgress() {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(1000);
            mUploadView.startAnimation(alphaAnimation);
            mUploadView.setVisibility(View.GONE);
        }

        public void showDialog(Video video) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(1000);
            mUploadView.startAnimation(alphaAnimation);
            mUploadView.setVisibility(View.GONE);
            mUploadSuccessView.setVisibility(View.VISIBLE);
            UIUtil.setImage(video.thumbnail.url, (ImageView) mUploadSuccessView.findViewById(R.id.img),
                    R.drawable.default_video_small, R.drawable.default_video_small);
        }

        public void hideDialog(boolean rightNow) {
            if (mUploadSuccessView.getVisibility() == View.VISIBLE) {
                if (!rightNow) {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(750);
                    mUploadSuccessView.startAnimation(alphaAnimation);
                }
                mUploadSuccessView.setVisibility(View.GONE);
            }
        }

        public void setProgress(int progress) {
            if (getActivity() == null || mUploadView == null) {
                return;
            }
            ((ProgressBar) mUploadView.findViewById(R.id.progress_upload)).setProgress(progress);
        }

        public void shareSina(Video video) {
            ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.SINA, shareListener);
        }
    }

    // 一些页面切换的控制, 如加载页, 登录页, 失败页
    private class PageViewGroup {
        private ViewGroup infoBoard;
        private View refreshBoard;

        public PageViewGroup(View view) {
            refreshBoard = view.findViewById(R.id.refresh);
            infoBoard = (ViewGroup) view.findViewById(R.id.board_info);
        }

        private void showLoginPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_no_login, null);
            view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginClick(LOGIN_REFRESH);
                    MobclickAgent.onEvent(DynamicFragment2.this.getActivity(), UmengConstant.DYNAMIC_LOGIN);
                }
            });
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            refreshBoard.setVisibility(View.GONE);
        }

        private void showLoadPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load, null);
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            refreshBoard.setVisibility(View.GONE);
        }

        private void showNoAttentionPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_no_attention, null);
            view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaunchUtil.launchSubActivity(getActivity(), FriendFindFragment.class, null);
                }
            });
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            refreshBoard.setVisibility(View.GONE);
        }

        private void showErrorPage(String error) {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load_error, null);
            ((TextView) view.findViewById(R.id.text)).setText(error);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listGroup.onRefreshData();
                }
            });
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            refreshBoard.setVisibility(View.GONE);
        }

        private void showListPage() {
            infoBoard.setVisibility(View.GONE);
            refreshBoard.setVisibility(View.VISIBLE);
        }
    }

    // -------------------------废弃或者不用的方法-------------------------------
    @Override
    public int setTitleLayoutId() {
        return 0;
    }

    @Override
    public void setTitlebar() {
    }

    @Override
    public void onReleaseView() {
    }
}
