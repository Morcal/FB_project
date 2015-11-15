package com.feibo.joke.view.group;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.dialog.ShareDialog;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.AutoPlayView;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.VImageView;
import com.feibo.social.base.Platform;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import fbcore.log.LogUtil;
import fbcore.utils.Utils;

/**
 * 信息流组件 已经集成加载更多, 后期考虑把加载更多分离成配置的方式 Created by lidiqing on 15-10-21.
 */
public class VideoFlowGroup {
    private static final String TAG = VideoFlowGroup.class.getSimpleName();

    private Context context;
    private RecyclerView recyclerView;
    private VideoAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private DividerItemDecoration itemDecoration;
    private VideoMobclickAgent videoMobclickAgent;

    private OnOperateLoginListener loginListener;
    private OnLoadMoreListener loadMoreListener;
    private OperateResultListener resultListener;

    private ProgressDialog loadingDialog;

    public VideoFlowGroup(Context context) {
        this.context = context;
        layoutManager = new LinearLayoutManager(context);
        recyclerAdapter = new VideoAdapter(context, new VideoAdapter.OnOperateListener() {
            @Override
            public void operate(OperateData operateData) {
                operateVideo(operateData);
            }
        });
        itemDecoration = new DividerItemDecoration(context, R.drawable.bg_divider);
        videoMobclickAgent = new VideoMobclickAgent(context);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        // 自动播放的实现
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtil.i(TAG, "onScrollStateChange:" + newState);
                if (newState != 0) {
                     return;
                }
                autoPlayVideo();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // 加载更多的实现
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != 0) {
                    return;
                }

                // 判断是否最后一项
                int position = layoutManager.findLastVisibleItemPosition();
                if (position == recyclerAdapter.getItemCount() - 1) {
                    if (loadMoreListener != null) {
                        loadMoreListener.loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    // 添加友盟统计
    public void setVideoMobclickAgent(VideoMobclickAgent agent) {
        videoMobclickAgent = agent;
        recyclerAdapter.setVideoMobclickAgent(videoMobclickAgent);
    }

    // 操作视频
    public void operateVideo(OperateData operateData) {
        final Video video = operateData.video;
        final int position = recyclerAdapter.getVideoAdapterPosition(video);
        final User user = video.author;
        final long userId = user.id;
        final boolean isFromMe = UserManager.getInstance().isFromMe(userId);

        switch (operateData.operateType) {
        case OperateData.OPERATE_FAVOR: // 点赞
            if (!Utils.isNetworkAvailable(context)) {
                ToastUtil.showSimpleToast(R.string.not_network);
                return;
            }
            if (!checkLogin()) {
                if (loginListener != null) {
                    loginListener.executeLogin(operateData);
                }
                return;
            }
            doFavor(video);
            videoMobclickAgent.onEvent(videoMobclickAgent.clickFavor);
            break;
        case OperateData.OPERATE_DOUBLE_FAVOR:
            if (video.beLike == 1) {
                ToastUtil.showSimpleToast("你已经爱过了啦！");
                return;
            }

            if (!Utils.isNetworkAvailable(context)) {
                ToastUtil.showSimpleToast(R.string.not_network);
                return;
            }

            if (!checkLogin()) {
                if (loginListener != null) {
                    loginListener.executeLogin(operateData);
                }
                return;
            }
            doDoubleFavor(video);
            videoMobclickAgent.onEvent(videoMobclickAgent.doubleClickFavor);
            break;
        case OperateData.OPERATE_COMMENT: // 　评论
            Bundle bundle = VideoDetailFragment.buildBundle(video.id, position);
            LaunchUtil.launchSubActivity(context, VideoDetailFragment.class, bundle);
            videoMobclickAgent.onEvent(videoMobclickAgent.clickComment);
            break;
        case OperateData.OPERATE_PLAY_COUNT: // 　统计视频播放次数
            if (!Utils.isNetworkAvailable(context)) {
                return;
            }

            if (video == null) {
                return;
            }

            doPlayCount(video);
            break;
        case OperateData.OPERATE_FOCUS: // Todo 关注，目前控件内部实现了关注的逻辑，之后重构并重写该控件
            if (!checkLogin()) {
                if (loginListener != null) {
                    loginListener.executeLogin(operateData);
                }
                return;
            }
            doFocus(video);
            break;
        case OperateData.OPERATE_ENTER_USER_DETAIL: // 点击头像或者用户昵称进入个人主页
            if (!Utils.isNetworkAvailable(context)) {
                ToastUtil.showSimpleToast(R.string.not_network);
                return;
            }
            LaunchUtil.launchSubActivity(context, UserDetailFragment2.class,
                    UserDetailFragment2.buildBundle(isFromMe, userId));
            videoMobclickAgent.onEvent(videoMobclickAgent.clickHeader);
            break;
        case OperateData.OPERATE_MORE: // 点击更多弹窗
            ShareDialog.ShareScene type = isFromMe ? ShareDialog.ShareScene.VIDEO_DELETE
                    : ShareDialog.ShareScene.VIDEO_REPORT;
            ShareDialog.show(context, type, new ShareDialog.OnShareClickListener() {
                @Override
                public void onShare(Platform.Extra extra) {
                    User me = UserManager.getInstance().getUser();
                    boolean isFromMe = video.author.id == me.id;
                    ShareUtil.onVideoDetialShare((Activity) context, isFromMe, video, extra, null);
                }

                @Override
                public void onDelete() {
                    // 删除前， 先清除之前视频的播放信息
                    recyclerAdapter.stopVideo();
                    doDelete(video);
                }

                @Override
                public void onReport() {
                    if (!checkLogin()) {
                        if (loginListener != null) {
                            loginListener.executeLogin(new OperateData(video, OperateData.OPERATE_REPORT));
                        }
                        return;
                    }
                    doReport(video);
                }
            }, video.shareUrl);
            videoMobclickAgent.onEvent(videoMobclickAgent.clickMore);
            break;
        case OperateData.OPERATE_REPORT:
            if (!checkLogin()) {
                if (loginListener != null) {
                    loginListener.executeLogin(new OperateData(video, OperateData.OPERATE_REPORT));
                }
                return;
            }
            doReport(video);
            break;
        }
    }

    // 点赞
    private void doFavor(final Video video) {
        if (video.beLike == 1) {
            // 取消点赞
            video.beLike = 0;
            video.beLikeCount -= 1;
            VideoAdapter.VideoViewHolder holder = getViewHolder(video);
            if (holder != null) {
                holder.favorText.setText(video.beLikeCount + "");
                recyclerAdapter.hideSmallLove(holder);
            }

            OperateManager.cancelLikeVideo(video.id, new LoadListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(int code) {
                    if (code == ReturnCode.RS_NONE_OBJECT) {
                        return;
                    }

                    video.beLike = 1;
                    video.beLikeCount += 1;
                    final VideoAdapter.VideoViewHolder vHolder = getViewHolder(video);
                    if (vHolder != null) {
                        vHolder.favorText.setText(video.beLikeCount + "");
                        recyclerAdapter.showSmallLove(vHolder, false);
                    }
                }
            });
        } else {
            // 进行点赞
            video.beLike = 1;
            video.beLikeCount += 1;
            VideoAdapter.VideoViewHolder holder = getViewHolder(video);
            if (holder != null) {
                holder.favorText.setText(video.beLikeCount + "");
                recyclerAdapter.showSmallLove(holder, true);
            }
            OperateManager.likeVideo(video.id, new LoadListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(int code) {
                    if (code == ReturnCode.RS_REPECT_CLICK) {
                        ToastUtil.showSimpleToast("你已经爱过了啦！");
                        return;
                    }
                    video.beLike = 0;
                    video.beLikeCount -= 1;
                    VideoAdapter.VideoViewHolder vHolder = getViewHolder(video);
                    if (vHolder != null) {
                        vHolder.favorText.setText(video.beLikeCount + "");
                        recyclerAdapter.hideSmallLove(vHolder);
                    }
                }
            });
        }
    }

    // 双击点赞
    private void doDoubleFavor(final Video video) {
        video.beLike = 1;
        video.beLikeCount += 1;
        VideoAdapter.VideoViewHolder holder = getViewHolder(video);
        if (holder != null) {
            holder.favorText.setText(video.beLikeCount + "");
            holder.favorImage.setImageResource(R.drawable.btn_dianzan_selected);
        }
        OperateManager.likeVideo(video.id, new LoadListener() {
            @Override
            public void onSuccess() {
                VideoAdapter.VideoViewHolder vHolder = getViewHolder(video);
                if (vHolder != null) {
                    recyclerAdapter.showBigLove(vHolder);
                }
            }

            @Override
            public void onFail(int code) {
                if (code == ReturnCode.RS_REPECT_CLICK) {
                    ToastUtil.showSimpleToast("你已经爱过了啦！");
                    return;
                }
                video.beLike = 0;
                video.beLikeCount -= 1;
                VideoAdapter.VideoViewHolder vHolder = getViewHolder(video);
                if (vHolder != null) {
                    vHolder.favorText.setText(video.beLikeCount + "");
                    vHolder.favorImage.setImageResource(R.drawable.btn_dianzan_normal);
                }
            }
        });
    }

    // 统计播放数
    private void doPlayCount(final Video video) {
        video.playCount += 1;
        VideoAdapter.VideoViewHolder holder = getViewHolder(video);
        if (holder != null) {
            holder.countText.setText(video.playCount + "播放");
        }
        OperateManager.addPlayVideoCount(video.id, new LoadListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail(int code) {
            }
        });
    }

    // 关注
    private void doFocus(Video video) {
        VideoAdapter.VideoViewHolder holder = getViewHolder(video);
        if (holder != null) {
            holder.focusBtn.onStatuClick(false);
        }
    }

    // 删除
    private void doDelete(final Video video) {
        showDialog(true);
        OperateManager.deleteVideo(video.id, new LoadListener() {
            @Override
            public void onSuccess() {
                showDialog(false);
                ToastUtil.showSimpleToast("删除成功");
                int adapterPosition = recyclerAdapter.getVideoAdapterPosition(video);
                if (adapterPosition != -1) {
                    recyclerAdapter.deleteVideo(adapterPosition);
                    recyclerAdapter.notifyItemRemoved(adapterPosition);
                }

                if (resultListener != null) {
                    resultListener.result(OperateResultListener.TYPE_DELETE, true);
                }
            }

            @Override
            public void onFail(int code) {
                showDialog(false);
                if (code == ReturnCode.RS_REPECT_CLICK) {
                    ToastUtil.showSimpleToast("删除成功");
                    int adapterPosition = recyclerAdapter.getVideoAdapterPosition(video);
                    if (adapterPosition != -1) {
                        recyclerAdapter.deleteVideo(adapterPosition);
                        recyclerAdapter.notifyItemRemoved(adapterPosition);
                    }

                    if (resultListener != null) {
                        resultListener.result(OperateResultListener.TYPE_DELETE, true);
                    }
                    return;
                }

                if (resultListener != null) {
                    resultListener.result(OperateResultListener.TYPE_DELETE, false);
                }
            }
        });
    }

    // 举报
    private void doReport(Video video) {
        OperateManager.reportVideo(video.id, new OperateManager.OperateListener() {
            @Override
            public void success() {
                ToastUtil.showSimpleToast("成功举报，我们会尽快处理");
            }

            @Override
            public void fail(int code, String msg) {
                if (code == ReturnCode.RS_REPECT_CLICK) {
                    ToastUtil.showSimpleToast("重复举报");
                    return;
                }
                ToastUtil.showSimpleToast(msg);
            }
        });
    }

    private void showDialog(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(context);
                loadingDialog.setMessage("正在删除...");
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(true);
                loadingDialog.show();
            } else {
                if (!loadingDialog.isShowing()) {
                    loadingDialog.show();
                }
            }
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    // 获取指定视频的ViewHolder
    private VideoAdapter.VideoViewHolder getViewHolder(Video video) {
        int pos = recyclerAdapter.getVideoAdapterPosition(video);
        if (pos >= 0) {
            return (VideoAdapter.VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
        } else {
            return null;
        }
    }

    private boolean checkLogin() {
        return UserManager.getInstance().isLogin();
    }

    // 更新指定位置的视频
    public void notifyVideo(Video video, int adapterPosition) {
        recyclerAdapter.stopVideo();
        recyclerAdapter.replaceVideo(video, adapterPosition);
        recyclerAdapter.notifyItemChanged(adapterPosition);
    }

    public void addHeaderView(View view) {
        recyclerAdapter.addHeader(view);
        itemDecoration.setHasHeader(true);
    }

    public boolean isEmpty() {
        return recyclerAdapter.isEmpty();
    }

    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    // 是否允许进入个人主页, 默认允许
    public void enableEnterDetail(boolean enable) {
        recyclerAdapter.enableEnterDetail(enable);
    }

    public void enableFocus(boolean enable) {
        recyclerAdapter.enableFocus(enable);
    }

    public void enablePlay(boolean enable) {
        recyclerAdapter.enablePlay(enable);
    }

    // 关闭当前播放的视频
    public void stopVideo() {
        recyclerAdapter.stopVideo();
    }

    // 播放视频
    public void autoPlayVideo() {
        LogUtil.e(TAG, "autoPlayVideo");

        // 设置不自动播放
        if (!SPHelper.getPlayVideoOnWifi()) {
            return;
        }

        // wifi不可用
        if (!(Utils.isWiFiActive(context) && Utils.isNetworkAvailable(context))) {
            return;
        }

        int firstPos = layoutManager.findFirstVisibleItemPosition();
        // 如果第一项为零
        if (firstPos < 0) {
            return;
        }

        LogUtil.i(TAG, "firstPos:" + firstPos);

        if (!(recyclerAdapter.getItemViewType(firstPos) == VideoAdapter.ITEM_TYPE_NORMAL)) {

            if (layoutManager.findLastVisibleItemPosition() == 0) {
                return;
            }

            if (firstPos == 0 && recyclerAdapter.hasHeader()) {
                firstPos = 1;
            } else {
                return;
            }
        }

        View firstView = layoutManager.findViewByPosition(firstPos);
        // 列表只有一个视频播放单元, 直接播放
        LogUtil.i(TAG, "lastVisibleItem:" + layoutManager.findLastVisibleItemPosition());
        if (firstPos == layoutManager.findLastVisibleItemPosition()) {
            recyclerAdapter.startVideo((VideoAdapter.VideoViewHolder) recyclerView.getChildViewHolder(firstView),
                    firstPos);
            return;
        }

        int lastPos = firstPos + 1;
        View lastView = layoutManager.findViewByPosition(lastPos);
        if (!(recyclerAdapter.getItemViewType(lastPos) == VideoAdapter.ITEM_TYPE_NORMAL)) {
            recyclerAdapter.startVideo((VideoAdapter.VideoViewHolder) recyclerView.getChildViewHolder(firstView),
                    firstPos);
            return;
        }

        // 列表有多个视频单元
        int firstVisibleHeight = layoutManager.getDecoratedBottom(firstView);
        int lastVisibleHeight = recyclerView.getHeight() - layoutManager.getDecoratedTop(lastView);

        LogUtil.i(TAG, "scroll");
        if (firstVisibleHeight > lastVisibleHeight) {
            recyclerAdapter.startVideo((VideoAdapter.VideoViewHolder) recyclerView.getChildViewHolder(firstView),
                    firstPos);
        } else {
            recyclerAdapter.startVideo((VideoAdapter.VideoViewHolder) recyclerView.getChildViewHolder(lastView),
                    lastPos);
        }
    }

    // 添加刷新的视频
    public void setRefreshVideos(List<Video> videos) {
        LogUtil.i(TAG, "setRefreshVideos");
        recyclerAdapter.stopVideo();
        recyclerAdapter.setItems(videos);
        recyclerAdapter.showFooterImage();
        recyclerAdapter.notifyDataSetChanged();
    }


    // 添加新加载的视频
    public void setLoadVideos(List<Video> videos) {
        LogUtil.i(TAG, "setLoadVideos");
        int lastVideoPosition = recyclerAdapter.getLastVideoPosition();
        recyclerAdapter.addItems(videos);
        recyclerAdapter.notifyItemRangeInserted(lastVideoPosition + 1, videos.size());
        // recyclerAdapter.notifyDataSetChanged();
    }

    public void showFooterText(String text) {
        recyclerAdapter.showFooterText(text);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public void setOperateResultListener(OperateResultListener listener) {
        this.resultListener = listener;
    }

    public void setOnOperateLoginListener(OnOperateLoginListener listener) {
        loginListener = listener;
    }

    public static class VideoAdapter extends RecyclerView.Adapter<ViewHolder> {

        private static final int ITEM_TYPE_NORMAL = 0x0;
        private static final int ITEM_TYPE_FOOTER = 0x1;
        private static final int ITEM_TYPE_HEADER = 0x2;

        private VideoViewHolder currentPlayHolder;
        private FooterViewHolder footerViewHolder;
        private HeaderViewHolder headerViewHolder;
        private VideoMobclickAgent videoMobclickAgent;

        private List<Video> videos;
        private int currentPlayPosition;
        private Context context;

        private boolean enableFocus = true;
        private boolean enableEnterDetail = true;
        private boolean enablePlay = true;

        private int headerCount = 0;
        private int footerCount = 1;

        private OnOperateListener operateListener;

        public VideoAdapter(Context context, OnOperateListener operateListener) {
            super();
            this.context = context;
            this.operateListener = operateListener;
            videos = new ArrayList<>();
            currentPlayPosition = -1;
            videoMobclickAgent = new VideoMobclickAgent(context);

            // header的容器
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            headerViewHolder = new HeaderViewHolder(frameLayout);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
            case ITEM_TYPE_NORMAL:
                LogUtil.i(TAG, "onCreateViewHolder normal");
                return new VideoViewHolder(View.inflate(context, R.layout.item_list_video, null));
            case ITEM_TYPE_FOOTER:
                FooterViewHolder footerViewHolder = new FooterViewHolder(LayoutInflater.from(context).inflate(
                        R.layout.footer_load, parent, false));
                this.footerViewHolder = footerViewHolder;
                return footerViewHolder;
            case ITEM_TYPE_HEADER:
                return headerViewHolder;
            default:
                return new VideoViewHolder(View.inflate(context, R.layout.item_list_video, null));
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            int type = getItemViewType(position);
            switch (type) {
            case ITEM_TYPE_HEADER:
                break;
            case ITEM_TYPE_FOOTER:
                break;
            case ITEM_TYPE_NORMAL:
            default:
                LogUtil.i(TAG, "onBindViewHolder normal");
                onBindViewHolderVideo((VideoViewHolder) viewHolder, position);
                ((VideoViewHolder) viewHolder).playView.setTag("" + position);
                break;
            }
        }

        @Override
        public void onViewAttachedToWindow(final ViewHolder viewHolder) {
            if (viewHolder instanceof FooterViewHolder) {
                ((FooterViewHolder) viewHolder).startAnimate();
                return;
            }

            if (viewHolder instanceof HeaderViewHolder) {
                return;
            }

            final VideoViewHolder holder = (VideoViewHolder) viewHolder;
            final int pos = holder.getAdapterPosition();
            // LogUtil.e(TAG, "onViewAttachedToWindow " + pos);
            holder.videoBtn.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
            final Video video = getVideo(pos);
            holder.playView.initVideo(video.url, video.oriImage.width, video.oriImage.height,
                    new AutoPlayView.PlayCycleListener() {
                        @Override
                        public void onState(int state) {
                            switch (state) {
                            case AutoPlayView.PlayCycleListener.STATE_START_FIRST:
                                operateListener.operate(new OperateData(video, OperateData.OPERATE_PLAY_COUNT));
                                break;
                            case AutoPlayView.PlayCycleListener.STATE_STARTED:
                                holder.videoBtn.setVisibility(View.GONE);
                                holder.videoImage.setVisibility(View.GONE);
                                holder.progressBar.setVisibility(View.GONE);
                                break;
                            case AutoPlayView.PlayCycleListener.STATE_PAUSED:
                                holder.videoBtn.setVisibility(View.VISIBLE);
                                holder.videoImage.setVisibility(View.GONE);
                                holder.progressBar.setVisibility(View.GONE);
                                break;
                            case AutoPlayView.PlayCycleListener.STATE_ERROR:
                                holder.videoBtn.setVisibility(View.VISIBLE);
                                holder.videoImage.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                break;
                            case AutoPlayView.PlayCycleListener.STATE_START_PREPARING:
                                holder.videoBtn.setVisibility(View.GONE);
                                holder.videoImage.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    });
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder viewHolder) {
            if (viewHolder instanceof FooterViewHolder) {
                ((FooterViewHolder) viewHolder).stopAnimate();
                return;
            }

            if (viewHolder instanceof HeaderViewHolder) {
                return;
            }

            final VideoViewHolder holder = (VideoViewHolder) viewHolder;
            hideBigLove(holder);
            int adapterPosition = viewHolder.getAdapterPosition();
            // LogUtil.e(TAG, "onViewAttachedToWindow " + adapterPosition);
            if (adapterPosition == currentPlayPosition) {
                currentPlayPosition = -1;
                currentPlayHolder = null;
            }
            holder.videoBtn.setVisibility(View.VISIBLE);
            holder.videoImage.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return videos.size() + headerCount + footerCount;
        }

        @Override
        public int getItemViewType(int position) {

            // header
            if (headerCount != 0 && position == 0) {
                LogUtil.i(TAG, "pos:" + position + " type:header");
                return ITEM_TYPE_HEADER;
            }

            // footer
            if (position == getItemCount() - 1) {
                LogUtil.i(TAG, "pos:" + position + " type:footer");
                return ITEM_TYPE_FOOTER;
            }

            // normal
            LogUtil.i(TAG, "pos:" + position + " type:normal");
            return ITEM_TYPE_NORMAL;
        }

        public void replaceVideo(Video video, int position) {
            videos.set(getVideoPosition(position), video);
        }

        public int getVideoAdapterPosition(Video video) {
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).id == video.id) {
                    return i + headerCount;
                }
            }
            return -1;
        }

        public void deleteVideo(int position) {
            videos.remove(getVideoPosition(position));
        }

        public Video getVideo(int position) {
            int videoPos = getVideoPosition(position);
            if (videoPos < 0 || videoPos > videos.size() - 1) {
                return null;
            } else {
                return videos.get(videoPos);
            }
        }

        public int getVideoPosition(int position) {
            return position - headerCount;
        }

        public void showFooterImage() {
            if (footerViewHolder != null) {
                footerViewHolder.loadImage.setVisibility(View.VISIBLE);
                footerViewHolder.loadText.setVisibility(View.GONE);
            }
        }

        public void showFooterText(String text) {
            if (footerViewHolder != null) {
                footerViewHolder.loadImage.setVisibility(View.GONE);
                footerViewHolder.loadImage.clearAnimation();
                footerViewHolder.loadText.setVisibility(View.VISIBLE);
                footerViewHolder.loadText.setText(text);
            }
        }

        private void onBindViewHolderVideo(final VideoViewHolder holder, final int position) {
            // 填充视频信息
            LogUtil.e(TAG, "onBindViewHolderVideo");
            final Video video = getVideo(position);
            UIUtil.setImage(video.oriImage.url, holder.videoImage, R.drawable.default_video_big,
                    R.drawable.default_video_big);
            UIUtil.setVAvatar(video.author.avatar, video.author.isSensation(), holder.avatarImage);
            holder.nameText.setText(video.author.nickname);
            holder.dateText.setText(TimeUtil.transformTime(video.publishTime));
            holder.countText.setText(video.playCount + "播放");
            holder.focusBtn.setUser(video.author);
            if (video.commentsCount > 0) {
                holder.commentText.setText(video.commentsCount + "");
            } else {
                holder.commentText.setText("评论");
            }

            if (!enableFocus || video.author.id == UserManager.getInstance().getUser().id
                    || video.author.relationship == User.RELATIONSHIP_ATTENTION
                    || video.author.relationship == User.RELATIONSHIP_BOTH_ATTENTION) {
                holder.focusBtn.setVisibility(View.GONE);
            }
            if (video.desc == null || video.desc.length() == 0) {
                holder.contentText.setVisibility(View.GONE);
            } else {
                holder.contentText.setText(video.desc);
                holder.contentText.setVisibility(View.VISIBLE);
            }
            holder.favorText.setText(video.beLikeCount + "");
            if (video.beLike == 1) {
                holder.favorImage.setImageResource(R.drawable.btn_dianzan_selected);
            } else {
                holder.favorImage.setImageResource(R.drawable.btn_dianzan_normal);
            }

            final VideoGestureDetector detector = new VideoGestureDetector(context);
            detector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    // 暂停/播放
                    int holderAdapterPosition = holder.getAdapterPosition();
                    if (holderAdapterPosition == currentPlayPosition) {
                        pauseOrStartVideo(holder);
                        return true;
                    }
                    // 播放
                    LogUtil.e(TAG, "startVideo:" + holderAdapterPosition);
                    startVideo(holder, holderAdapterPosition);
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // 双击点赞
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_DOUBLE_FAVOR));
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }
            });

            holder.videoBoard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }
            });

            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 视频详情
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_COMMENT));

                }
            });

            holder.nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入用户个人主页
                    if (!enableEnterDetail) {
                        return;
                    }
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_ENTER_USER_DETAIL));
                }
            });

            holder.avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入用户个人主页
                    if (!enableEnterDetail) {
                        return;
                    }
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_ENTER_USER_DETAIL));
                }
            });

            holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 评论
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_COMMENT));
                }
            });

            holder.favorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点赞
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_FAVOR));
                }
            });

            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更多弹窗
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_MORE));
                }
            });

            holder.focusBtn.setOnStatuClickListener(new FocusStateView.OnStatuClicklistener() {
                @Override
                public void onPreperadLogin() {
                    operateListener.operate(new OperateData(video, OperateData.OPERATE_FOCUS));
                }
            });
        }

        public boolean isEmpty() {
            return videos.size() == 0;
        }

        public void setItems(List<Video> videos) {
            LogUtil.i(TAG, "setItems");
            this.videos = videos;
        }

        public boolean hasHeader() {
            return headerCount == 1;
        }

        public void addItems(List<Video> videos) {
            videos.addAll(videos);
        }


        public synchronized void startVideo(VideoViewHolder holder, int position) {
            if (position == currentPlayPosition || !enablePlay) {
                return;
            }

            // 关闭前一个视频
            if (currentPlayPosition != -1 && currentPlayHolder != null
                    && (currentPlayPosition == position + 1 || currentPlayPosition == position - 1)) {
                stopVideo();
            }

            currentPlayPosition = position;
            currentPlayHolder = holder;
            // holder.videoBtn.setVisibility(View.GONE);
            // holder.progressBar.setVisibility(View.VISIBLE);
            holder.playView.startVideo();
            // LogUtil.e(TAG, "startVideo");
            videoMobclickAgent.onEvent(videoMobclickAgent.clickPlay);
        }

        public VideoAdapter() {
            super();
        }

        public synchronized void stopVideo() {
            // LogUtil.e(TAG, "stopVideo");
            currentPlayPosition = -1;
            if (currentPlayHolder == null) {
                return;
            }
            currentPlayHolder.progressBar.setVisibility(View.GONE);
            currentPlayHolder.videoBtn.setVisibility(View.VISIBLE);
            currentPlayHolder.videoImage.setVisibility(View.VISIBLE);
            currentPlayHolder.playView.stopVideo();
        }

        public void pauseOrStartVideo(VideoViewHolder holder) {
            holder.playView.pauseOrStartVideo();
        }

        public void showBigLove(final VideoViewHolder holder) {
            holder.loveImage.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.love_anim_big);
            holder.loveImage.startAnimation(animation);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    holder.loveImage.clearAnimation();
                    holder.loveImage.setVisibility(View.GONE);
                }
            });
        }

        public void hideBigLove(final VideoViewHolder holder) {
            holder.loveImage.clearAnimation();
            holder.loveImage.setVisibility(View.GONE);
        }

        public void showSmallLove(final VideoViewHolder holder, boolean anim) {
            holder.favorImage.setImageResource(R.drawable.btn_dianzan_selected);
            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.love_anim_small);
                holder.favorImage.startAnimation(animation);
            }
        }

        public void hideSmallLove(final VideoViewHolder holder) {
            holder.favorImage.clearAnimation();
            holder.favorImage.setImageResource(R.drawable.btn_dianzan_normal);
        }

        public void addHeader(View view) {
            headerCount = 1;
            headerViewHolder.addHeader(view);
        }

        public int getLastVideoPosition() {
            return getItemCount() - footerCount - 1;
        }

        public void setVideoMobclickAgent(VideoMobclickAgent agent) {
            videoMobclickAgent = agent;
        }

        public void enableFocus(boolean enable) {
            enableFocus = enable;
        }

        public void enableEnterDetail(boolean enable) {
            enableEnterDetail = enable;
        }

        public void enablePlay(boolean enable) {
            enablePlay = enable;
        }

        public static class VideoGestureDetector extends GestureDetector {

            public VideoGestureDetector(Context context) {
                super(context, new OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        return false;
                    }
                });
            }
        }

        public static class VideoViewHolder extends ViewHolder {
            View rootView;
            AutoPlayView playView;
            View videoBtn;
            View favorBtn;
            View commentBtn;
            View moreBtn;
            View progressBar;
            ImageView videoImage;
            ImageView favorImage;
            ImageView loveImage;
            VImageView avatarImage;
            TextView nameText;
            TextView dateText;
            TextView countText;
            TextView contentText;
            TextView favorText;
            TextView commentText;
            FocusStateView focusBtn;
            View videoBoard;

            public VideoViewHolder(View view) {
                super(view);
                rootView = view.findViewById(R.id.root);
                playView = (AutoPlayView) view.findViewById(R.id.video);
                // playBtn = view.findViewById(R.id.btn_play);
                videoBtn = view.findViewById(R.id.btn_video);
                favorBtn = view.findViewById(R.id.btn_favor);
                moreBtn = view.findViewById(R.id.btn_more);
                commentBtn = view.findViewById(R.id.btn_comment);
                progressBar = view.findViewById(R.id.progress);
                videoImage = (ImageView) view.findViewById(R.id.image_video);
                avatarImage = (VImageView) view.findViewById(R.id.image_avatar);
                favorImage = (ImageView) view.findViewById(R.id.image_favor);
                loveImage = (ImageView) view.findViewById(R.id.image_love);
                nameText = (TextView) view.findViewById(R.id.text_name);
                dateText = (TextView) view.findViewById(R.id.text_date);
                countText = (TextView) view.findViewById(R.id.text_count);
                contentText = (TextView) view.findViewById(R.id.text_content);
                favorText = (TextView) view.findViewById(R.id.text_favor);
                commentText = (TextView) view.findViewById(R.id.text_comment);
                focusBtn = (FocusStateView) view.findViewById(R.id.btn_focus);
                videoBoard = view.findViewById(R.id.board_video);
            }
        }

        public static class FooterViewHolder extends ViewHolder {

            ImageView loadImage;
            TextView loadText;
            Animation rotateAnim;
            private final int ROTATE_ANIM_DURATION = 500;

            public FooterViewHolder(View view) {
                super(view);
                loadImage = (ImageView) view.findViewById(R.id.image);
                loadText = (TextView) view.findViewById(R.id.text);
                loadImage.setVisibility(View.VISIBLE);
                loadText.setVisibility(View.GONE);

                rotateAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotateAnim.setDuration(ROTATE_ANIM_DURATION);
                rotateAnim.setRepeatCount(-1);
                rotateAnim.setInterpolator(new LinearInterpolator());// 设置匀速，无加速度不卡顿
                rotateAnim.setRepeatMode(Animation.RESTART);

            }

            public void startAnimate() {
                if (loadImage.getVisibility() == View.VISIBLE) {
                    loadImage.startAnimation(rotateAnim);
                }
            }

            public void stopAnimate() {
                loadImage.clearAnimation();
            }
        }

        public static class HeaderViewHolder extends ViewHolder {
            private ViewGroup viewGroup;

            public HeaderViewHolder(ViewGroup view) {
                super(view);
                viewGroup = view;
            }

            public void addHeader(View view) {
                viewGroup.removeAllViews();
                viewGroup.addView(view);
            }
        }

        public interface OnOperateListener {
            void operate(OperateData operateData);
        }
    }

    // 信息流列表的间隔线
    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable dividerDrawable;
        private boolean hasHeader = false;

        public DividerItemDecoration(Context context, int resId) {
            dividerDrawable = ContextCompat.getDrawable(context, resId);
        }

        public void setHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }

            int top = dividerDrawable.getIntrinsicHeight();
            int itemCount = state.getItemCount();
            LogUtil.i(TAG, "itemCount:" + itemCount);

            if (itemPosition == 0) {
                top = 0;
            }

            if (hasHeader && itemPosition == 1) {
                top = 0;
            }

            int footerPos = itemCount - 1;
            if (itemPosition == footerPos) {
                top = 0;
            }

            outRect.set(0, top, 0, 0);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int bottom = child.getTop() - params.topMargin;
                int top = bottom - dividerDrawable.getIntrinsicHeight();
                dividerDrawable.setBounds(left, top, right, bottom);
                dividerDrawable.draw(c);
            }
        }
    }

    // 事件统计
    public static class VideoMobclickAgent {
        private String clickPlay = null;
        private String clickHeader = null;
        private String clickMore = null;
        private String clickComment = null;
        private String clickFavor = null;
        private String doubleClickFavor = null;
        private Context context;

        private VideoMobclickAgent(Context context) {
            this.context = context;
        }

        public void onEvent(String var) {
            if (var != null) {
                MobclickAgent.onEvent(context, var);
            }
        }

        public static class Builder {
            VideoMobclickAgent agent;

            public Builder(Context context) {
                agent = new VideoMobclickAgent(context);
            }

            public Builder doPlay(String play) {
                agent.clickPlay = play;
                return this;
            }

            public Builder doHeader(String header) {
                agent.clickHeader = header;
                return this;
            }

            public Builder doMore(String more) {
                agent.clickMore = more;
                return this;
            }

            public Builder doComment(String comment) {
                agent.clickComment = comment;
                return this;
            }

            public Builder doFavor(String favor) {
                agent.clickFavor = favor;
                return this;
            }

            public Builder doDoubleFavor(String doubleFavor) {
                agent.doubleClickFavor = doubleFavor;
                return this;
            }

            public VideoMobclickAgent create() {
                return agent;
            }
        }
    }

    public static interface OnOperateLoginListener {
        void executeLogin(OperateData operateData);
    }

    public static interface OnLoadMoreListener {
        void loadMore();
    }

    public static class OperateData {
        static final int OPERATE_FOCUS = 0; // 关注
        static final int OPERATE_FAVOR = 1; // 点赞
        static final int OPERATE_DELETE = 2; // 删除
        static final int OPERATE_REPORT = 3; // 举报
        static final int OPERATE_COMMENT = 4; // 评论
        static final int OPERATE_PLAY_COUNT = 5; // 统计播放
        static final int OPERATE_ENTER_USER_DETAIL = 6; // 点击头像与昵称进入到个人主页
        static final int OPERATE_MORE = 7;
        static final int OPERATE_DOUBLE_FAVOR = 8; // 双击点赞

        Video video;
        int operateType;

        public OperateData(Video video, int operateType) {
            this.video = video;
            this.operateType = operateType;
        }
    }

    public interface OperateResultListener {
        static final int TYPE_DELETE = 0x2;

        void result(int type, boolean success);
    }
}
