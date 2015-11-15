package com.feibo.joke.view.module.home;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.list.VideosDynamicManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.video.manager.UploadVideoManager;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.DynamicListGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform;
import com.umeng.analytics.MobclickAgent;

import fbcore.log.LogUtil;

/**
 * 贵圈
 *
 * @author lidiqing
 */
@Deprecated
public class DynamicFragment extends BaseLoginFragment implements GroupOperator, OnClickListener {

    private VideosDynamicManager dynamicManager;
    private DynamicListGroup listGroup;
    private VideoListAdapter dynamicAdapter;

    private ViewGroup viewGroup;

    //视频上传相关
    private View mUploadView;
    private View mUploadSuccessView;
    private UploadVideoManager mUploadVideoManager;

    public DynamicFragment() {

    }

    public static DynamicFragment newInstance() {
        return new DynamicFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dynamicAdapter = new VideoListAdapter(getActivity());
        dynamicManager = new VideosDynamicManager();

        // 视频上传相关管理类
        mUploadVideoManager = UploadVideoManager.getInstance(this.getActivity());
    }

    @Override
    public View containChildView() {
        return View.inflate(getActivity(), R.layout.fragment_dynamic, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listGroup = new DynamicListGroup(getActivity()) {

            @Override
            public void onLoginClick() {
                loginClick();

                MobclickAgent.onEvent(DynamicFragment.this.getActivity(), UmengConstant.DYNAMIC_LOGIN);
            }
        };
        listGroup.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VIDEO_DYNAMIC));
        listGroup.setFooterLoadMoreOverText(getString(R.string.load_more_over_text_dynamic));

        listGroup.setListAdapter(dynamicAdapter);
        listGroup.setListManager(dynamicManager);

        listGroup.onCreateView();

        mUploadVideoManager.setOnUploadListener(onUploadListener);
        mUploadVideoManager.uploadVideo(getActivity());

        viewGroup = (ViewGroup) view;
        viewGroup.removeAllViews();

        ViewGroup contentRootView = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(R.layout.group_layout, null);
        View top = getTopView();
        LinearLayout topLayout = (LinearLayout) contentRootView.findViewById(R.id.top_layout);
        topLayout.addView(top, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout contentLayout = (LinearLayout) contentRootView.findViewById(R.id.content_layout);
        contentLayout.addView(listGroup.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View popView = getPopView();
        contentRootView.addView(popView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        viewGroup.addView(contentRootView);
    }

    public View getTopView() {
        mUploadView = LayoutInflater.from(this.getActivity()).inflate(R.layout.progressbar_upload_video, null);
        mUploadView.setVisibility(View.GONE);
        return mUploadView;
    }

    public View getPopView() {
        mUploadSuccessView = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_submit_success, null);
        mUploadSuccessView.findViewById(R.id.btn_weibo).setOnClickListener(this);
        mUploadSuccessView.findViewById(R.id.btn_weixin).setOnClickListener(this);
        mUploadSuccessView.findViewById(R.id.btn_weixin_friend).setOnClickListener(this);
        mUploadSuccessView.findViewById(R.id.btn_qzone).setOnClickListener(this);
        mUploadSuccessView.setVisibility(View.GONE);
        mUploadSuccessView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSharePop(false);
            }
        });
        return mUploadSuccessView;
    }

    private void closeSharePop(boolean rightNow) {
        if (mUploadSuccessView.getVisibility() == View.VISIBLE) {
            if (!rightNow) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(750);
                mUploadSuccessView.startAnimation(alphaAnimation);
            }
            mUploadSuccessView.setVisibility(View.GONE);
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if (result) {
            // 登陆成功,重新加载布局
            listGroup.onResetView();
            viewGroup.removeAllViews();
            viewGroup.addView(listGroup.getRoot());
        }
    }

    @Override
    public void onScrollToTop(boolean refresh) {
        listGroup.onScollToTop(true);
    }

    @Override
    public void onDataChange(int code) {
        listGroup.onDataChange(code);

        switch (code) {
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
                mUploadVideoManager.uploadVideo(getActivity());
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
                closeSharePop(true);
                break;
        }
    }

    protected void shareVideoToSina(Video video) {
        ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.SINA, null);
    }

    @Override
    public void onClick(View v) {
        Video video = mUploadVideoManager.getVideo();
        switch (v.getId()) {
            case R.id.btn_weibo:
                shareVideoToSina(video);
                closeSharePop(false);
                break;
            case R.id.btn_weixin:
                ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.WX_SESSION, shareListener);
                closeSharePop(false);
                break;
            case R.id.btn_weixin_friend:
                ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.WX_TIMELINE, shareListener);
                closeSharePop(false);
                break;
            case R.id.btn_qzone:
                ShareUtil.onVideoDetialShare(getActivity(), true, video, Platform.Extra.QQ_QZONE, shareListener);
                closeSharePop(false);
                break;
        }
    }


    UploadVideoManager.OnUploadListener onUploadListener = new UploadVideoManager.OnUploadListener() {

        private Context getContext() {
            return DynamicFragment.this.getActivity();
        }

        @Override
        public void onStart(int progress) {
            mUploadSuccessView.setVisibility(View.GONE);
            setProgress(progress);
            mUploadView.startAnimation(new AlphaAnimation(0.0f, 1.0f));
            mUploadView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccuss(Video video) {
            if (getContext() == null) {
                return;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(1000);
            mUploadView.startAnimation(alphaAnimation);
            mUploadView.setVisibility(View.GONE);

            onDataChange(DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE);

            mUploadSuccessView.setVisibility(View.VISIBLE);
            UIUtil.setImage(video.thumbnail.url, (ImageView) mUploadSuccessView.findViewById(R.id.img),
                    R.drawable.default_video_small, R.drawable.default_video_small);
        }

        @Override
        public void onShareSina(Video video) {
            LogUtil.d("ShareSian", "getContext:" + getContext());
            if (getContext() == null) {
                return;
            }
            ShareUtil.onVideoDetialShare((Activity) getContext(), true, video, Platform.Extra.SINA, shareListener);
        }

        @Override
        public void onSaveDraft(final boolean succuss) {
            showToast(succuss ? "成功保存到草稿箱" : "保存到草稿箱失败");
        }

        @Override
        public void onProgress(int progress) {
            if (getContext() == null) {
                return;
            }
            setProgress(progress);
        }

        @Override
        public void onFail(int code) {
            if (getContext() == null) {
                return;
            }
            showToast("上传失败");
            ToastUtil.showSimpleToast(getContext(), "上传失败");
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(1000);
            mUploadView.startAnimation(alphaAnimation);
            mUploadView.setVisibility(View.GONE);

            mUploadVideoManager.release();
        }

        private void setProgress(int progress) {
            if (getContext() == null || mUploadView == null) {
                return;
            }
            ((ProgressBar) mUploadView.findViewById(R.id.progress_upload)).setProgress(progress);
        }

        private void showToast(final String str) {
            if (getContext() == null) {
                return;
            }

            ((Activity) getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ToastUtil.showSimpleToast(str);
                }
            });
        }
    };

    ShareUtil.IShareListener shareListener = new ShareUtil.IShareListener() {

        @Override
        public void onFail(int code) {
            if (code == SocialManager.WEIBO_TOKEN_TIMEOUT) {}
        }
    };


    // ------------------过时或废弃的方法--------------------------
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
