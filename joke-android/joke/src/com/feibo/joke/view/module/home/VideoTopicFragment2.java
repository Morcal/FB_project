package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.list.VideosTopicManager;
import com.feibo.joke.model.Image;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.group.VideoFlowGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;

import fbcore.log.LogUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lidiqing on 15-10-22.
 */
public class VideoTopicFragment2 extends BaseLoginFragment{
    private static final String TAG = VideoTopicFragment2.class.getSimpleName();
    private static final String BUNDLE_KEY_TOPIC_ID = "topic_id";
    private static final String BUNDLE_KEY_TOPIC_NAME = "topic_name";
    private static final String BUNDLE_KEY_TOPIC_IMAGE = "topic_image";

    private static final int LOGIN_OPERATE = 0x2;

    private long topicId;
    private String topicName;
    private Image topicImage;

    private VideoFlowGroup videoFlowGroup;
    private PageGroup pageGroup;

    private VideosTopicManager manager;

    private VideoFlowGroup.OperateData operateData;
    private boolean isLoading = false;

    public static Bundle buildBundle(long topicId, Image image, String topicName) {
        Bundle args = new Bundle();
        args.putLong(BUNDLE_KEY_TOPIC_ID, topicId);
        args.putString(BUNDLE_KEY_TOPIC_NAME, topicName);
        args.putSerializable(BUNDLE_KEY_TOPIC_IMAGE, image);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        topicId = bundle.getLong(BUNDLE_KEY_TOPIC_ID, 0);
        topicName = bundle.getString(BUNDLE_KEY_TOPIC_NAME, "更多话题视频列表");
        topicImage = (Image) bundle.getSerializable(BUNDLE_KEY_TOPIC_IMAGE);
        manager = new VideosTopicManager(topicId);
        videoFlowGroup = new VideoFlowGroup(getActivity());
    }

    @Override
    public View containChildView() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_topic_detail, null);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        getTitleBar().rightPart.setVisibility(View.GONE);
        ((TextView)getTitleBar().title).setText(topicName);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        videoFlowGroup.setRecyclerView(recyclerView);
        pageGroup = new PageGroup(view);

        if (topicImage != null) {
            addTopicImage(topicImage);
        }


        videoFlowGroup.setOnOperateLoginListener(new VideoFlowGroup.OnOperateLoginListener() {
            @Override
            public void executeLogin(VideoFlowGroup.OperateData operateData) {
                VideoTopicFragment2.this.operateData = operateData;
                loginClick(LOGIN_OPERATE);
            }
        });

        videoFlowGroup.setOnLoadMoreListener(new VideoFlowGroup.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMoreData();
            }
        });

        pageGroup.refreshBoard.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                onRefreshData();
            }
        });

        pageGroup.showLoadPage();
        onRefreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoFlowGroup.enablePlay(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoFlowGroup != null) {
            videoFlowGroup.enablePlay(false);
            videoFlowGroup.stopVideo();
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        videoFlowGroup.enablePlay(true);
        if (result) {
            switch (operationCode) {
                case LOGIN_OPERATE:
                    videoFlowGroup.operateVideo(operateData);
                    break;
            }
        }
    }

    @Override
    public void onDataChange(int code) {
        videoFlowGroup.enablePlay(true);
        switch (code) {
            case DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE:
                pageGroup.showLoadPage();
                onRefreshData();
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
                Bundle bundle = ((BaseActivity) getActivity()).getFinishBundle();
                Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
                int adapterPosition = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
                if (adapterPosition == -1) {
                    return;
                }

                // 删除视频
                if (video == null) {
                    pageGroup.showLoadPage();
                    onRefreshData();
                    return;
                }

                // 改变视频状态
                videoFlowGroup.notifyVideo(video, adapterPosition);
                break;
        }
    }

    private void onRefreshData() {
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
                pageGroup.refreshComplete();
                if (manager.getDatas() != null && manager.getDatas().size() == 0) {
                    pageGroup.showErrorPage(getString(R.string.loading_more_no_data));
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
                pageGroup.refreshComplete();
                switch (code) {
                    case ReturnCode.NO_NET:
                        pageGroup.showErrorPage(getString(R.string.page_no_net));
                        break;
                    case ReturnCode.RS_EMPTY_ERROR:
                        pageGroup.showErrorPage(getString(R.string.loading_more_no_data));
                        break;
                    default:
                        pageGroup.showErrorPage("未知错误");
                        break;
                }
            }
        });
    }

    private void onLoadMoreData() {
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
                        videoFlowGroup.showFooterText(getString(R.string.load_more_over_text_default));
                        break;
                }
            }
        });
    }

    //　添加话题头
    private void addTopicImage(Image image) {

        if (TextUtils.isEmpty(image.url) || image.width == 0 || image.height == 0) {
            return;
        }

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = screenWidth;
        int imageHeight = (int) (screenWidth * (image.height / (float)image.width));
        ImageView imageView = new ImageView(getActivity());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageHeight));

        videoFlowGroup.addHeaderView(imageView);
        UIUtil.setImage(image.url, imageView, R.drawable.default_video_big, R.drawable.default_video_big);
    }

    private class PageGroup {
        private ViewGroup infoBoard;
        private PtrFrameLayout refreshBoard;

        public PageGroup(View view) {
            refreshBoard = (PtrFrameLayout) view.findViewById(R.id.refresh);
            infoBoard = (ViewGroup) view.findViewById(R.id.board_info);
        }

        private void showLoadPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load, null);
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
                    onRefreshData();
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

        private void refreshComplete() {
            refreshBoard.refreshComplete();
        }
    }

    // --------------------------废弃或无用的方法--------------------------------
    @Override
    public void onReleaseView() {

    }
}
