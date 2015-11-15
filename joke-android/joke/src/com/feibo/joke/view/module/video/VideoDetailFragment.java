package com.feibo.joke.view.module.video;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.VideoDetailReportListener;
import com.feibo.joke.manager.detail.VideoDetailManager;
import com.feibo.joke.manager.list.CommentsVideoManager;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.ReportManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.Comment;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.ShareUtil.IShareListener;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.adapter.VideoCommentsAdapter;
import com.feibo.joke.view.dialog.ShareDialog;
import com.feibo.joke.view.dialog.ShareDialog.OnShareClickListener;
import com.feibo.joke.view.dialog.VideoDetailDialog;
import com.feibo.joke.view.dialog.VideoDetailDialog.OnBtnClickListener;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.VideoDetailGroup;
import com.feibo.joke.view.group.impl.VideoDetailGroup.IVideoDetailLoadListener;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;
import com.feibo.joke.view.widget.FocusStateView.OnStatuClicklistener;
import com.feibo.joke.view.widget.InputBarView;
import com.feibo.joke.view.widget.InputBarView.InputBarViewListener;
import com.feibo.joke.view.widget.VImageView;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshLoadmoreListView;
import com.feibo.joke.view.widget.videoControlView.VideoControlView;
import com.feibo.joke.view.widget.videoControlView.VideoControlView.VideoControlerViewCallback;
import com.feibo.social.base.Platform.Extra;
import com.umeng.analytics.MobclickAgent;

/**
 *
 * com.feibo.joke.view.module.video.VideoDetailFragment
 * @author LinMW<br/>
 * Creat at2015-5-30 下午12:17:12
 */
public class VideoDetailFragment extends BaseLoginFragment implements OnClickListener {
    private static final String TAG = VideoDetailFragment.class.getSimpleName();
    public final static String EXTRAS_KEY = "vedio_detail_show";

    /** 视频详情页的头布局视图 **/
    private View mHeadView;
    /** 视频详情页的底部评论视图 **/
    private View mCommentView;
    /** 视频播放模块 **/
    private VideoControlView videoControlView;
    /** 评论列表 **/
    private ListView mCommentsList;
    /** 关注按钮 **/
    private FocusStateView btnFocus;
    private View btnLike;//点赞按钮
    private ImageView loveSmallImg;//点赞小爱心
    private TextView loveCount;//被点赞数量
    private ImageView loveBigImg;//点赞大爱心
    private Button btnShare;//分享按钮
    private InputBarView sendBar;//发送评论文字按钮
    private VImageView ivAutherHead;//视频作者头像
    private TextView tvCommentsNumber;//显示评论数量
    private TextView tvWithoutComment;//当没有评论时要显示的文字
    private TextView tvPlayCounts;//播放量
    private List<Comment> comments;//评论
    /** 异步获取视频数据的管理类 **/
    private VideoDetailManager videoDetailManager;
    /** 用于异步获取视频评论列表的管理类 **/
    private CommentsVideoManager commentsManager;
    /** 发表视频评论监听 **/
    private CommentPostListener commPostListener;
    /** 点赞、播放量、删除、举报监听 **/
//    private VideoOptListener videoOptListener;
    
    private Comment beClickComm;// 被点击的条目相关
    private final static int NO_CLICK_ITEM = -1;
    private int beClickPosition = NO_CLICK_ITEM;
    private View beClickView;
    private int longClickIndex;

    private int globalCount;//onGlobalLayout()被调用次数

    private VideoCommentsAdapter adapter;
    private Video mVideo;
    private long videoId;
    private VideoDetailGroup mGroup;

    private final static int DEFAULT_COMMENT_ID = -1;//如果服务器没有传回的默认评论id

    /** 当前操作(喜欢,取消喜欢,举报 这三个中的一种) **/
//    private int currentOpt;
    private final static int OPT_CODE_LIKE = 2;//喜欢操作
    private final static int OPT_CODE_LIKE_BIG = 0;//喜欢操作
    private final static int OPT_CODE_UNLIKE = 3;//取消喜欢操作
    private final static int OPT_CODE_REPORT_VIDEO = 1;//举报删除视频操作
    private final static int OPT_CODE_DELETE_VIDEO = 4;
    private final static int OPT_CODE_REPORT_COMMENT = 5;//举报删除评论操作
    private final static int OPT_CODE_DELETE_COMMENT = 6;
    private final static int OPT_CODE_PLAY_VEDIO = 7;//播放视频

    /** 要回复的评论id ,为0时,评论的是视频 **/
    private long commentId = 0;
    private RelativeLayout rlLoadingView;
    private Context mContext;
    private Boolean isLogin=false;
    private Boolean autoPlay=false;
    private Boolean isWaitResult=false;

    private int adapterPosition;
    private int discoveryVideoPosition;
    private int likeFlag; //0表示未变化,1表示喜欢,-1表示取消喜欢

    private int attionChangeFlag;
    private Bundle lastBundle;

    private boolean hasPause = false;

    public static final Bundle buildBundle(long videoId, int originAdapterId) {
        return buildBundle(videoId, originAdapterId, 0);
    }

    public static final Bundle buildBundle(long videoId, int originAdapterId, int discoveryVideoPosition) {
        Bundle bundle = new Bundle();
        bundle.putLong(VideoDetailFragment.EXTRAS_KEY, videoId);
        bundle.putInt(BundleUtil.KEY_ADAPTER_POSITION, originAdapterId);
        bundle.putInt(BundleUtil.KEY_DSICOVERY_VIDEO_POSITION, discoveryVideoPosition);
        return bundle;
    }
    
    @Override
    public View containChildView() {
        mContext = getActivity();
       ((BaseActivity)mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);//设置音量键控制音量

        mGroup = new VideoDetailGroup(mContext) {

            @Override
            public void initListListener(PullToRefreshLoadmoreListView pullToRefreshListView) {
                pullToRefreshListView.setOnScrollListener(new OnScrollListener() {
                    int[] location = new int[2];//用于存储VideoView的位置
                    private int statusBarHeight = 0;//状态栏的高度
                    private int titleHeight;//标题栏的高度
                    private int height;//VideoControlView的高度
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 隐藏软键盘
                            sendBar.hideKeyboard();
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (videoControlView != null){//这里先不要和keepPause合并代码，有执行顺序优劣问题，待优化
//                            if(videoControlView.isPlaying()) {
//                                if (isNeedPause()) {
//                                    videoControlView.pause();
//                                }
//                            }else if(videoControlView.isNeedPreparedAutoPlay() && (videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_BUFFERING
//                                    || videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_ERROR)){
//                                if (isNeedPause()) {
//                                    videoControlView.onBufPause();
//                                }
//                            }
                            if (isNeedPause()) {
                                if(videoControlView.getVisibility()==View.VISIBLE){
                                    LogUtil.i("isNeedPause==========", "videoControlView.INVISIBLE");
                                    videoControlView.setVisibility(View.INVISIBLE);
                                }
                                if(videoControlView.isPlaying()) {
                                        videoControlView.pause();
                                }else if(videoControlView.isNeedPreparedAutoPlay() && (videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_BUFFERING
                                        || videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_ERROR)){
                                        videoControlView.onBufPause();
                                }
                            }else{
                                if(videoControlView.getVisibility()==View.INVISIBLE){
                                    LogUtil.i("isNeedPause==========", "videoControlView.VISIBLE");
                                    videoControlView.setVisibility(View.VISIBLE);
                                }
                                
//                                if(seekToIfNeed){
//                                    videoControlView.seekToIfNeed();
//                                    seekToIfNeed=false;
//                                }
                            }
                        }
                    }

                    private Boolean isNeedPause() {
                        if (statusBarHeight == 0) {
                            Rect frame = new Rect();
                            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                            statusBarHeight = frame.top;
                            titleHeight = getTitleBar().headView.getBottom();
                            height = videoControlView.getHeight();
                        }
                        videoControlView.getLocationOnScreen(location);
                        int pos = location[1] + height - titleHeight - statusBarHeight;
                        return pos <= 0;// 当视频的底部小于标题栏的底部 则暂停播放
                    }
                });
            }
        };
        mGroup.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VEDIO_DETAIL));
        mGroup.setFooterLoadMoreOverText(getString(R.string.load_more_over_text_video_detail_comment));

     // 获取要显示的Video的id
        Bundle bundle = getActivity().getIntent().getExtras();
        videoId = bundle.getLong(EXTRAS_KEY, 0);
        adapterPosition = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
        discoveryVideoPosition = bundle.getInt(BundleUtil.KEY_DSICOVERY_VIDEO_POSITION);

        // 根据videoId去服务器获取Video的详细信息
        videoDetailManager = new VideoDetailManager(videoId);
        commentsManager = new CommentsVideoManager(videoId);
        adapter = new VideoCommentsAdapter(mContext);

        mGroup.setListAdapter(adapter);
        mGroup.setListManager(commentsManager,videoDetailManager);
        mGroup.setLoadListener(new VideoDetailLoadListener());
        mGroup.onCreateView();
        initView();
        initDate();
        return mGroup.getRoot();
    }

    /**
     * 页面视图初始化
     */
    private void initView() {
        mCommentView = mGroup.getRoot();
        mCommentsList = mGroup.getListView();
        mHeadView=mGroup.getHeadView();
        mCommentsList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        // 各种findViewById
        btnFocus = (FocusStateView) mHeadView.findViewById(R.id.btn_focus);
        btnFocus.setOnStatuChangelistener(new OnStatuChangeListener() {

            @Override
            public void onStatuChange(boolean isAttention) {
                attionChangeFlag = isAttention ? ++attionChangeFlag : --attionChangeFlag;
                setFinishDataChange(false, false);
            }
        });

        ivAutherHead = (VImageView) mHeadView.findViewById(R.id.iv_auother_head);

//        etComment = (EditText) mCommentView.findViewById(R.id.et_say_something);
//        tvSend = (Button) mCommentView.findViewById(R.id.tv_send);
        sendBar = (InputBarView) mCommentView.findViewById(R.id.send_bar);


        tvCommentsNumber = (TextView) mHeadView.findViewById(R.id.tv_comments_number);
        tvWithoutComment = (TextView) mHeadView.findViewById(R.id.tv_without_comments);
    }

    /**
     * 页面数据初始化
     */
    private void initDate() {
        isLogin=UserManager.getInstance().isLogin();
        autoPlay=SPHelper.getPlayVideoOnWifi(mContext);
//        videoOptListener = new VideoOptListener();
        
        addViewListener();
    }

    /**
     * 为一些View设置点击监听
     */
    @SuppressLint("NewApi")
    private void addViewListener() {
        ivAutherHead.setOnClickListener(this);

        // 监听键盘弹出的布局改变，用来获取输入法的高度
        mCommentsList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private int margin;
            private int listHeight = 0; // list默认显示高度
            private int inputHeight = 0;// 输入法高度

            @Override
            public void onGlobalLayout() {
                globalCount++;
                if (listHeight == 0 || inputHeight == 0) {// 判断如果已经获取过了，就不再获取
                    Rect r = new Rect();
                    mCommentsList.getWindowVisibleDisplayFrame(r);

                    int screenHeight = mCommentsList.getRootView().getHeight();
                    if (screenHeight == r.bottom) {// 判断键盘是否弹出了，没弹出就获取list的初始高度
                        listHeight = r.height() - sendBar.getHeight() - getTitleBar().headView.getHeight();
                    } else {// 弹出就获取输入法的初始高度
                        inputHeight = screenHeight - r.bottom;
                    }
                }else{
//                    mCommentsList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                // 下方操作必须在这里执行，也就是确认布局改变后才移动条目
                // 如果不在这里执行，List的最后几个item无法正常移动到弹出输入法的输入框上方！
                // 注意Activity要有的配置：android:windowSoftInputMode="stateHidden|adjustUnspecified"
                if (beClickPosition != NO_CLICK_ITEM && sendBar.isKeyboardShow() && globalCount<=3) {
                        margin = listHeight - inputHeight - beClickView.getHeight();
                        if(margin!=beClickView.getTop()){
                            mCommentsList.setSelectionFromTop(beClickPosition,margin);
                        }
                }else if (beClickPosition != NO_CLICK_ITEM && !sendBar.isKeyboardShow()) {
                    if(TextUtils.isEmpty(sendBar.getText())){
                        sendBar.resetInputBar(true);
                    }
                }
            }
        });
        sendBar.setCurrentVideoId(videoId);
        sendBar.setListener(new InputBarViewListener() {

            @Override
            public void onEditTextFocusChange(boolean hasFocus) {
                if(hasFocus && !isLogin){
                    loginClick();
                    sendBar.hideKeyboard();
                }
                if(hasFocus) {
                    MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_INPUT);
                }
            }

            @Override
            public void onResetInputBar() {
                commentId = 0;// 重置信息
                beClickComm = null;
                beClickView = null;
                beClickPosition = NO_CLICK_ITEM;
            }

            @Override
            public void send(String content) {
                comment(content, commentId);

                MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_SEND);
            }

            @Override
            public boolean beforeClickSend() {
                if(!isLogin){
                    loginClick();
                    return true;
                }
                if(isWaitResult){
                    ToastUtil.showSimpleToast("网速慢了哟，评论正在提交...");
                    return true;
                }
                return false;
            }
        });
        // 监听条目点击事件
        mGroup.setOnItemClickListener(new OnItemClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalCount=0;
                if(!isLogin){
                    loginClick();
                    return;
                }
                if(comments.size()==0){//解决偶然出现的角标越界BUG
                    return;
                }
                beClickPosition = position + 1;
                beClickView = view;
                Comment newClick = comments.get(position - 1);
                if (newClick.id == DEFAULT_COMMENT_ID) {// 判断是不是自己刚刚回复的
                    sendBar.resetInputBar(false);
                    ToastUtil.showSimpleToast("这是您刚刚回复的哦！");
                    return;
                }
                if (beClickComm == null || beClickComm != newClick) {// 判断点击条目是不是前一次点击的，不是就改变回复对象
                    beClickComm = newClick;
                    String name = beClickComm.author.nickname;
                    commentId = beClickComm.id;
                    sendBar.setHint("回复 @" + name + " :");
                    sendBar.showKeyboard();

                    MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_COMMENT);
                } else {// 否则隐藏键盘，初始化输入栏
                    sendBar.hideKeyboard();
                    sendBar.resetInputBar(false);
                }
            }
        });
        mCommentsList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isLogin){
                    return false;
                }
                longClickIndex = position - 2;
                final Comment longClick = comments.get(longClickIndex);
                final Boolean isUserOwn = (longClick.author.id == UserManager.getInstance().getUser().id);
                final VideoDetailDialog moreBtnDialog = VideoDetailDialog.show(mContext, isUserOwn);
                moreBtnDialog.setOnBtnClickListener(new OnBtnClickListener() {

                    @Override
                    public void onConfirm() {
                        if (isUserOwn) {
                            if (longClick.id != DEFAULT_COMMENT_ID) {
                                OperateManager.deleteComment(longClick.id, new VideoOptListener(OPT_CODE_DELETE_COMMENT));
//                                currentOpt = OPT_CODE_DELETE_COMMENT;
                            } else {
                                comments.remove(longClickIndex);
                                adapter.notifyDataSetChanged();
                                mVideo.commentsCount--;
                                setCommentsCount();
                            }
                        } else {
                            ReportManager.reportComment(longClick.id, new VideoOptListener(OPT_CODE_REPORT_COMMENT));
//                            currentOpt = OPT_CODE_REPORT_COMMENT;
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                return false;
            }
        });
    }

    /**
     * 将Video的数据填充到视频详细信息的布局中
     */
    private void setData2HeadView() {
        if (mVideo != null) {

            TextView tvAutherName = (TextView) mHeadView.findViewById(R.id.tv_auther_name);
            TextView tvPublishDate = (TextView) mHeadView.findViewById(R.id.tv_publish_date);
            tvPlayCounts = (TextView) mHeadView.findViewById(R.id.tv_paly_counts);
            TextView tvVideoDesc = (TextView) mHeadView.findViewById(R.id.tv_video_desc);
            btnLike = mHeadView.findViewById(R.id.btn_is_like);
            loveSmallImg = (ImageView) mHeadView.findViewById(R.id.iv_love_small);
            loveCount = (TextView) mHeadView.findViewById(R.id.tv_love_count);
            loveBigImg = (ImageView) mHeadView.findViewById(R.id.iv_love_big_for_anim);
            btnShare = (Button) mHeadView.findViewById(R.id.btn_video_share);

            initVideoControlView();

            UIUtil.setVAvatar(mVideo.author.avatar, mVideo.author.isSensation(), ivAutherHead);
            
            tvAutherName.setText(mVideo.author.nickname);
            tvPublishDate.setText(TimeUtil.transformTime(mVideo.publishTime));
            tvPlayCounts.setText(mVideo.playCount + " 播放");
            tvVideoDesc.setText(mVideo.desc);
            btnLike.setOnClickListener(this);
            btnShare.setOnClickListener(this);
            setLikeButton(mVideo.beLike,false);
            btnFocus.setOnStatuClickListener(new OnStatuClicklistener() {
                @Override
                public void onPreperadLogin() {
                    loginClick(BaseLoginFragment.OPERATION_CODE_FOUCES);
                }
            });
            btnFocus.setUser(mVideo.author);
            if(isFromMe() || mVideo.author.relationship==User.RELATIONSHIP_ATTENTION || mVideo.author.relationship==User.RELATIONSHIP_BOTH_ATTENTION){
                btnFocus.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 初始化VideoControlView
     */
    private void initVideoControlView() {
        videoControlView = (VideoControlView) mHeadView.findViewById(R.id.vcv_video);
        videoControlView.setVideoImage(mVideo.oriImage.url);
        videoControlView.setPlayUrl(mVideo.url);
        final Runnable onWifiAutoPlay = new Runnable() {
            @Override
            public void run() {
                if(autoPlay && videoControlView!=null && videoControlView.isFirstPlay() && AppContext.isWifiActive()){
                    LogUtil.e(TAG, "initVideoControlView onSingleClick");
                    if (hasPause) {
                        LogUtil.e(TAG, "initVideoControlView onSingleClick return");
                        return;
                    }
                    videoControlView.onSingleClick();
                }
            }
        };

        videoControlView.postDelayed(onWifiAutoPlay, 1000);
        videoControlView.setCallback(new VideoControlerViewCallback() {
            @Override
            public void onPlay() {
                MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_PLAY);
            }

            @Override
            public void onPause() {
                //TODO
                if (onWifiAutoPlay != null) {
                    videoControlView.removeCallbacks(onWifiAutoPlay);
                }
            }

            @Override
            public void onFirstPlay() {
                OperateManager.addPlayVideoCount(videoId, new VideoOptListener(OPT_CODE_PLAY_VEDIO));
//                currentOpt = OPT_CODE_PLAY_VEDIO;
            }

            @Override
            public void onReplay() {
                OperateManager.addPlayVideoCount(videoId, new VideoOptListener(OPT_CODE_PLAY_VEDIO));
//                currentOpt = OPT_CODE_PLAY_VEDIO;
            }

            @Override
            public void onError() {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onBufferingStart() {
            }

            @Override
            public void onBufferingEnd() {
            }

            @Override
            public void onDoubleClick() {
                MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_DOUBLE_LIKE);
                if (hasNet()) {
                    if (!isLogin) {
                        loginClick();
                        return;
                    }
                    if (mVideo.beLike == 1) {
                        ToastUtil.showSimpleToast("你已经爱过啦！");
                        return;
                    }
                    LogUtil.i("onDoubleClick=================", "out");
                    if (!isWaitResult) {
                        LogUtil.i("onDoubleClick=================", "in");
                        mVideo.beLike = 1;//这里原来是动画播放完后才设置，但应该先设置，避免连续点击重复执行动画
                        isWaitResult = true;
                        btnLike.setEnabled(false);
                        showBigLove();
                        OperateManager.likeVideo(videoId, new VideoOptListener(OPT_CODE_LIKE_BIG));
//                        currentOpt = OPT_CODE_LIKE_BIG;
                    }
                }
            }
        });
    }

    /**
     * 根据用户是否喜欢过该视频来设置喜欢按钮的图片 "be_like":"int", // 视频是否被当前用户喜欢，0未喜欢，1喜欢
     *
     * @param likeCode 0未喜欢，1喜欢
     */
    @SuppressLint("NewApi")
    private void setLikeButton(int likeCode,Boolean isPlayAnimation) {
        Drawable d = null;
        switch (likeCode) {
        case 0:
            loveSmallImg.clearAnimation();
            d = mContext.getResources().getDrawable(R.drawable.btn_link_normal);
            loveSmallImg.setBackgroundDrawable(d);
            break;
        case 1:
            d = mContext.getResources().getDrawable(R.drawable.btn_link_selected);
            loveSmallImg.setBackgroundDrawable(d);
            if(isPlayAnimation){
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.love_anim_small);
                loveSmallImg.startAnimation(animation);
            }
            break;
        default:
            break;
        }
//        if(mVideo.beLikeCount>=0){
            loveCount.setText(mVideo.beLikeCount + "");
//        }else{
//            loveCount.setText("0");
//        }
    }

    private class VideoDetailLoadListener implements IVideoDetailLoadListener{

        @Override
        public void onDetailSuccess() {

            if(videoDetailManager==null){
                return;
            }
            mVideo = videoDetailManager.getVideo();
            setData2HeadView();
            getTitleBar().rightPart.setVisibility(View.VISIBLE);
            sendBar.setVisibility(View.VISIBLE);
            setCommentsCount();
        }

        @Override
        public void onDetailFail(int code) {

        }

        @Override
        public void onCommentsSuccess() {
            initComments();
        }

        @Override
        public void onCommentsFail(int code) {
            if(code==ReturnCode.RS_EMPTY_ERROR){
                initComments();
            }
        }

        private void initComments() {
            if(commentsManager==null){
                return;
            }
            comments = commentsManager.getDatas();
            adapter.notifyDataSetChanged();
            setCommentsCount();
        }
    }

    /**
     * 设置评论数量和是否显示无评论提示条目
     */
    private void setCommentsCount() {
        if (comments == null || comments.size() < 1) {
            tvWithoutComment.setVisibility(View.VISIBLE);
            tvCommentsNumber.setText("0");
        } else {
            tvWithoutComment.setVisibility(View.GONE);
            if(mVideo != null) {
                tvCommentsNumber.setText(mVideo.commentsCount + "");
            }
        }
    }

    private class CommentPostListener implements LoadListener {

        @SuppressLint("NewApi")
        @Override
        public void onSuccess() {
            Comment mComment = commentsManager.getComment();
            if(mComment==null || mComment.id==0){
                mComment.id = DEFAULT_COMMENT_ID;// 发表评论id直接添加到list，设置为默认，表示自己的评论
            }
            mComment.author = UserManager.getInstance().getUser();
            mComment.content = sendBar.getText();
            mComment.publishTime = TimeUtil.getPublishTime();
            sendBar.setLastComment(mComment.content, mComment.publishTime,mVideo.id);
            mComment.replyId = commentId;
            if (commentId != 0) {
                mComment.replyAuthor = beClickComm.author;
            }
//            if (beClickPosition != NO_CLICK_ITEM) {
//                comments.add(beClickPosition-1, mComment);//添加到回复条目的下一条
//            } else {
//                comments.add(0, mComment);
//                mCommentsList.smoothScrollToPosition(2);//pull2refresh的关系，第一条评论Position是2
//            }
            comments.add(0, mComment);
            adapter.notifyDataSetChanged();
            mCommentsList.smoothScrollToPosition(2);//pull2refresh的关系，第一条评论Position是2
            sendBar.resetInputBar(true);
            mVideo.commentsCount++;
            setCommentsCount();
            isWaitResult=false;
        }

        @SuppressLint("NewApi")
        @Override
        public void onFail(int code) {
            isWaitResult=false;
        }
    }
    
    private class VideoOptListener implements VideoDetailReportListener {
        private int currentOpt;
        
        private VideoOptListener(int opt) {
            this.currentOpt = opt;
        }
        
        @Override
        public void onSuccess() {
            switch (currentOpt) {
            case OPT_CODE_PLAY_VEDIO:// 当前用户正在播放视频播放数+1
                mVideo.playCount++;
                tvPlayCounts.setText(mVideo.playCount + " 播放");
                setFinishDataChange(false, false);
                break;
            case OPT_CODE_LIKE:// 当前用户正在添加喜欢视频,把爱心变成红色,喜欢个数+1
//                refreshLikeButton(true);
                break;
            case OPT_CODE_LIKE_BIG:// 当前用户双击喜欢视频,把爱心变成红色,喜欢个数+1
//                showBigLove();
                break;
            case OPT_CODE_UNLIKE:// 当前用户正在取消喜欢视频,把爱心变成灰色 , 喜欢个数-1
//                refreshLikeButton(false);
                break;
            case OPT_CODE_REPORT_COMMENT:// 当前用户正在举报评论
                ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.report_success));
                break;
            case OPT_CODE_REPORT_VIDEO:// 当前用户正在举报视频
                ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.report_success));
                setFinishDataChange(true, true);
                break;
            case OPT_CODE_DELETE_VIDEO:// 当前用户正在删除视频
                ToastUtil.showSimpleToast("删除视频成功");
                setFinishDataChange(true, true);
                break;
            case OPT_CODE_DELETE_COMMENT:// 当前用户正在删除评论
                ToastUtil.showSimpleToast("删除评论成功");
                comments.remove(longClickIndex);
                adapter.notifyDataSetChanged();
                mVideo.commentsCount--;
                setCommentsCount();
                break;
            default:
                break;
            }
            isWaitResult=false;
        }

        @Override
        public void onFail(int code) {
            if (code == ReturnCode.RS_REPECT_CLICK || code== ReturnCode.RS_NONE_OBJECT  //这两种状态下点赞不恢复状态
                    || code == ReturnCode.RS_POP_REPORT_HINT) {
                switch (currentOpt) {
                case OPT_CODE_REPORT_VIDEO:// 当前用户正在举报视频
                case OPT_CODE_REPORT_COMMENT:// 当前用户正在举报评论
                    if(code == ReturnCode.RS_REPECT_CLICK){//第二次以上举报显示相同提示
                        ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.report_success));
                        if(currentOpt==OPT_CODE_REPORT_VIDEO){
                            setFinishDataChange(true, true);
                        }
                    }
                    break;
                default:
//                    ToastUtil.showSimpleToast("重复点击");
                    break;
                }
            }else{//其他情况下点赞失败恢复原状态
                switch (currentOpt) {
                case OPT_CODE_UNLIKE:
                   refreshLikeButton(true);
                   break;
                case OPT_CODE_LIKE_BIG:
                case OPT_CODE_LIKE:
                   refreshLikeButton(false);
                   break;
                default:
                   break;
                }
            }
            isWaitResult=false;
        }

        @Override
        public void onReportFail(int code, String msg) {
            if (code == ReturnCode.RS_REPECT_CLICK || code== ReturnCode.RS_NONE_OBJECT  //这两种状态下点赞不恢复状态
                    || code == ReturnCode.RS_POP_REPORT_HINT) {
                switch (currentOpt) {
                    case OPT_CODE_REPORT_VIDEO:// 当前用户正在举报视频
                    case OPT_CODE_REPORT_COMMENT:// 当前用户正在举报评论
                        if(code == ReturnCode.RS_REPECT_CLICK){//第二次以上举报显示相同提示
                            ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.report_success));
                            if(currentOpt==OPT_CODE_REPORT_VIDEO){
                                setFinishDataChange(true, true);
                            }
                        } else if (code == ReturnCode.RS_POP_REPORT_HINT) {
                            if (StringUtil.isEmpty(msg)) {
                                msg = "该歇歇了";
                            }
    //                        RemindDialog.show((BaseActivity)mContext, new RemindSource(msg, "确定", ""), true);
                            ToastUtil.showSimpleToast(msg);
                        }
                        break;
                }
            } else{//其他情况下点赞失败恢复原状态
                switch (currentOpt) {
                    case OPT_CODE_UNLIKE:
                        refreshLikeButton(true);
                        break;
                    case OPT_CODE_LIKE_BIG:
                    case OPT_CODE_LIKE:
                        refreshLikeButton(false);
                        break;
                    default:
                        break;
                }
            }
            isWaitResult=false;
        }
    }

    private void refreshLikeButton(boolean likeOrCancel) {
        if(likeOrCancel){
            mVideo.beLikeCount++;
            mVideo.beLike = 1;
            likeFlag ++;
        }else{
            mVideo.beLikeCount--;
            mVideo.beLike = 0;
            likeFlag--;
        }
        setLikeButton(mVideo.beLike, true);
        setFinishDataChange(false, false);
    }

    private void showBigLove() {
//        MobclickAgent.onEvent(this.getActivity(), UmengConstant.VIDEO_DETAIL_DOUBLE_LIKE);
        loveBigImg.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.love_anim_big);
        loveBigImg.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            private Boolean isAllAnimationEnd = false;//用于判断动画是不是都播放完了

            @Override
            public void onAnimationStart(Animation animation) {
                refreshLikeButton(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loveBigImg.clearAnimation();
                loveBigImg.setVisibility(View.INVISIBLE);
                if (isAllAnimationEnd == false) {//这个动画结束监听会被调用2次，因为有2个动画，第一次进来不执行点赞，第二次才操作点赞访问网络
                    isAllAnimationEnd = true;
                    return;
                }
                btnLike.setEnabled(true);
                isAllAnimationEnd = false;
            }
        });
    }

    /**
     * 设置点赞和播放量的返回值
     */
    private void setFinishDataChange(boolean isDelete, boolean finishNow) {
        LogUtil.i("setFinishDataChange====", getActivity() == null ? "true":"false");
        if(getActivity() == null) {
            return;
        }

        lastBundle = lastBundle == null ? getFinishBundle() : lastBundle;
        
        lastBundle = BundleUtil.buildVideoDetailBundle(lastBundle,
                isDelete, adapterPosition, discoveryVideoPosition, likeFlag, attionChangeFlag, mVideo);

        super.setFinishBundle(lastBundle);
        if(finishNow) {
            super.setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE);
        } else {
            super.setChangeType(DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE);
        }
    }

    /**
     * 设置标题栏视图
     */
    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_is_like: // 点赞按钮
            if(hasNet()){
                if(!isLogin){
                    loginClick();
                    break;
                }
                if(isWaitResult){
                    ToastUtil.showSimpleToast("网速慢了哟，正在提交点赞或取消...");
                    break;
                }
                isWaitResult=true;
                if (mVideo.beLike == 1) {// 用户喜欢过当前视频 , 点击后取消喜欢
                    refreshLikeButton(false);
                    OperateManager.cancelLikeVideo(videoId, new VideoOptListener(OPT_CODE_UNLIKE));
//                    currentOpt = OPT_CODE_UNLIKE;
                } else {// 用户没有喜欢当前视频 , 点击后 ,添加喜欢
                    refreshLikeButton(true);
                    OperateManager.likeVideo(videoId, new VideoOptListener(OPT_CODE_LIKE));
//                    currentOpt = OPT_CODE_LIKE;
                }
            }

            MobclickAgent.onEvent(this.getActivity(), UmengConstant.VIDEO_DETAIL_LIKE);
            break;
        case R.id.btn_video_share: // 分享按钮

            ShareDialog.show(mContext, ShareDialog.ShareScene.SHARE_ONLY, new OnShareClickListener() {
                @Override
                public void onShare(Extra extra) {
                    User me = UserManager.getInstance().getUser();
                    boolean isFromMe = mVideo.author.id == me.id;
                    ShareUtil.onVideoDetialShare(getActivity(), isFromMe, mVideo, extra, null);
                }

                @Override
                public void onDelete() {
                }

                @Override
                public void onReport() {
                }
            }, mVideo.shareUrl);

            MobclickAgent.onEvent(this.getActivity(), UmengConstant.VIDEO_DETAIL_SHARE);
            break;
        case R.id.iv_auother_head: // 点击作者头像
//            LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class,
//                    UserDetailFragment.buildBundle(isFromMe(), mVideo.author.id));
            LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                    UserDetailFragment2.buildBundle(isFromMe(), mVideo.author.id));

            MobclickAgent.onEvent(this.getActivity(), UmengConstant.VIDEO_DETAIL_AVATAR);
            break;
        default:
            break;
        }
    }

    private boolean hasNet() {
        if (!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.not_network));
            return false;
        }
        return true;
    }

    /**
     * 点击更多按钮时的操作
     */
    private void clickMoreDo() {
        if(!isLogin){
            loginClick();
            return;
        }
        final boolean isUserOwn = isFromMe();
        final VideoDetailDialog moreBtnDialog = VideoDetailDialog.show(mContext, isUserOwn);
        moreBtnDialog.setOnBtnClickListener(new OnBtnClickListener() {

            @Override
            public void onConfirm() {
                if (isUserOwn) {
                    OperateManager.deleteVideo(videoId, new VideoOptListener(OPT_CODE_DELETE_VIDEO));
//                    currentOpt = OPT_CODE_DELETE_VIDEO;
                    MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_DELETE);
                } else {
                    ReportManager.reportVideo(videoId, new VideoOptListener(OPT_CODE_REPORT_VIDEO));
//                    currentOpt = OPT_CODE_REPORT_VIDEO;
                    MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_REPORT);
                }
//                moreBtnDialog.dismiss();
            }

            @Override
            public void onCancel() {}
        });
    }

    /**
     * 发表视频评论(回复评论)
     *
     * @param comment 视频评论内容
     * @param commentID 当评论ID为0时 则评论视频,否则回复对应的ID的评论
     */
    private void comment(String comment, long commentID) {
        isWaitResult=true;
        if (commPostListener == null) {
            commPostListener = new CommentPostListener();
            commentsManager.setLoadListener(commPostListener);
        }
        String text = StringUtil.encode2Utf(comment);
        if (commentID == 0) {
            commentsManager.commentVideo(text);
        } else {
            commentsManager.commentVideoComments(commentID, text);
        }
//        ToastUtil.showSimpleToast("正在提交评论...");
        sendBar.hideKeyboard();
    }


    private void keepPause() {
        if (videoControlView != null){
            if(videoControlView.isPlaying()) {
                    videoControlView.pause();
            }else if(videoControlView.isNeedPreparedAutoPlay() && (videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_BUFFERING
                    || videoControlView.getCurrentStatue()==VideoControlView.PLAY_STATUE_ERROR)){
                    videoControlView.onBufPause();
            }
        }
    }

    @Override
    public void onPause() {// 当Fragment不可见时暂停视频播放
        LogUtil.e(TAG, "onPause");
        keepPause();
        hasPause = true;
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtil.e(TAG, "onStop");
        keepPause();
        super.onStop();
    }
//    private boolean seekToIfNeed=false;
    @Override
    public void onResume() {
        LogUtil.e(TAG, "onResume");
        hasPause = false;
        keepPause();
        if (videoControlView != null && videoControlView.getVisibility()==View.INVISIBLE){
            videoControlView.replayTextureView();
            videoControlView.setVideoImageVisibility();
//            seekToIfNeed=true;
            LogUtil.i("onResume==========", "videoControlView.replayTextureView");
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        keepPause();
        super.onStart();
    }

    @Override
    public void loginClick(int operationCode) {//弹出登录框要求视频暂停
        keepPause();
        super.loginClick(operationCode);
    }

    //父类的OnDestory会回调这个方法
    @Override
    public void onReleaseView() {
        LogUtil.e("VideoDetailFragment", "onReleaseView");
        if (videoControlView != null) {
            videoControlView.release();
            videoControlView.destoryHandler();
            videoControlView=null;
        }
        if(mGroup != null) {
            mGroup.onDestroyView();
        }
        mContext=null;
        mCommentsList=null;
        mCommentView=null;
        mHeadView=null;
        mGroup=null;
        videoDetailManager=null;
        commentsManager=null;
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        // 设置标题栏的标题
        ((TextView) titleBar.title).setText("详情");
        // 显示标题栏右边的按钮
        View flRight = titleBar.rightPart;
        flRight.findViewById(R.id.tv_head_right).setVisibility(View.GONE);
        titleBar.ivHeadRight.setVisibility(View.VISIBLE);
        titleBar.tvHeadRight.setVisibility(View.GONE);
        titleBar.rightPart.setVisibility(View.INVISIBLE);
        titleBar.rightPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(VideoDetailFragment.this.getActivity(), UmengConstant.VIDEO_DETAIL_MORE);
                if(mVideo == null) {
                    if(mGroup != null && mGroup.getVideoIsDelete()) {
                        ToastUtil.showSimpleToast("该视频已被删除");
                    } else {
                        ToastUtil.showSimpleToast("正在加载视频");
                    }
                    return;
                }
                clickMoreDo();
            }
        });
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if(result) {
            //当前页面判断是否登录的变量
            isLogin=true;
            //登录后，继续之前的操作
            if(BaseLoginFragment.OPERATION_CODE_FOUCES == operationCode) {
                btnFocus.onStatuClick(false);
                ToastUtil.showSimpleToast("登录成功");
            } else {

            }
        }
    }

    private boolean isFromMe() {
        return mVideo.author.id == UserManager.getInstance().getUser().id;
    }

    @Override
    public void onDataChange(int code) {
        if(code == DataChangeEventCode.CHANGE_TYPE_ATTENTION_IN_USERDETAIL) {
            Bundle bundle = getFinishBundle();
            if(bundle != null) {
                long userId = bundle.getLong(BundleUtil.KEY_USERID);
                int attentionFlag = bundle.getInt(BundleUtil.KEY_ATTENTION_COUNT_CHANGE);
                if(userId == mVideo.author.id && attentionFlag != 0) {
                    btnFocus.onPostSuccessChangState();
                    btnFocus.setVisibility(View.VISIBLE);

                    attionChangeFlag += attentionFlag;
                    lastBundle = lastBundle == null ? new Bundle() : lastBundle;
                    lastBundle.putInt(BundleUtil.KEY_ATTENTION_COUNT_CHANGE, attionChangeFlag);
                }
            }
        }
        setFinishBundle(lastBundle);
    }
    
}
