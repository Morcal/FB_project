package com.feibo.joke.view.widget.videoControlView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.videoControlView.manager.MediaPlayerManager;
import com.feibo.joke.view.widget.videoControlView.manager.MediaPlayerManager.MediaStateListener;

/**
 * 
 * com.feibo.joke.view.widget.videoControlView.VideoControlView
 * @author LinMW<br/>
 * Creat at2015-5-30 下午12:17:45
 */
public class VideoControlView extends RelativeLayout {
    private int mRatioWidth;
    private int mRatioHeight;
    
    /** 视频播放的回调 **/
    private VideoControlerViewCallback mCallback;
    private FrameLayout framelayout;
    private MediaPlayerManager playerManager;
    private ImageView mVideoImage;
    private ImageView btnPlay;
//    private ImageView loadingImg;
    private View progressView;

//    private View loadProgress;
    private String remoteUrl;
    private int errorCount;
    private Boolean isFirstPlay=true;
    private Boolean isLooperPlay=true;
    private Boolean isNeedPreparedAutoPlay=true;
    
    /** 正在播放状态 **/
    public final static int PLAY_STATUE_PALYING = 0;
    /** 暂停状态 **/
    public final static int PLAY_STATUE_PAUSE = 1;
    /** 播放结束 **/
    public final static int PLAY_STATUE_END = 2;
    /** 正在缓冲 **/
    public final static int PLAY_STATUE_BUFFERING = 3;
    /** 缓存完成准备播放 **/
    public final static int PLAY_STATUE_READY = 4;
    /** 默认还未播放 **/
    public final static int PLAY_STATUE_DEFAULT = 5;
    /** 播放出错 **/
    public final static int PLAY_STATUE_ERROR = 6;
    /** 当前状态 **/
    private int currentStatue = PLAY_STATUE_DEFAULT;
    
    
    public VideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mRatioWidth = typedArray.getInteger(R.styleable.ScaleView_ratioWidth, 1);
        mRatioHeight = typedArray.getInteger(R.styleable.ScaleView_ratioHeight, 1);
        typedArray.recycle();
        
        findViews();
        
    }
    public VideoControlView(Context context) {
        this(context,null);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float mRatio = mRatioHeight / (float) mRatioWidth;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) Math.ceil(widthSize * mRatio);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }
   
    public void setPlayUrl(String url){
        this.remoteUrl =url;
        init();
    }
    public void setVideoImage(String imgUrl){
        UIUtil.setImage(imgUrl, mVideoImage, R.drawable.default_video_big, R.drawable.default_video_big);
    }
    public void setCallback(VideoControlerViewCallback callback){
        if(callback!=null){
            mCallback=callback;
        }
    }
    public int getCurrentStatue(){
        return currentStatue;
    }
    public Boolean isFirstPlay(){
        return isFirstPlay;
    }
    public Boolean isNeedPreparedAutoPlay(){
        return isNeedPreparedAutoPlay;
    }
    
    private void findViews() {
        View  view=this.inflate(getContext(), R.layout.layout_video_control_view, null);
        this.framelayout = (FrameLayout) view.findViewById(R.id.bbvideoview);
        this.mVideoImage = (ImageView) view.findViewById(R.id.iv_video_image);
        this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
//        this.loadingImg = (ImageView) view.findViewById(R.id.loading_img);
        progressView = view.findViewById(R.id.progress);
//        this.loadProgress = view.findViewById(R.id.loading_progress);
        this.addView(view);
    }
    
    @SuppressLint("NewApi") 
    private void init() {
        playerManager=MediaPlayerManager.getInstance();
        playerManager.setIsNeedGetVideoSize(true);
        playerManager.setMediaStateListener(new MediaStateListener() {

            @Override
            public void onPrepare(int progress) {
                if(!isPlaying() && isNeedPreparedAutoPlay && progress==100){
                    LogUtil.i("progress=======100", "play()");
                    play();
                }
            }

            @Override
            public void onError(MediaPlayer mp,String path) {
                errorCount++;
                mCallback.onError();
                currentStatue = PLAY_STATUE_ERROR;
                mVideoImage.setVisibility(View.VISIBLE);
//                videoPause(true);

                btnPlay.setVisibility(View.VISIBLE);
                dismissProgress();
                removeCallbacks(hidVideoImage);
//                playerManager.reset
                // TODO 以下为旧代码


/**
                doPause();

                requestLayout();
                invalidate();
                LogUtil.e("VideoControlView", "onError");
                if(isNeedPreparedAutoPlay && errorCount<5){//错误自动重置次数
//                    videoPlay(true);
                    seekTo(0);
                    play();
                    LogUtil.i("onError==============", "onErrorplay");
                }
 **/
            }

            @Override
            public void onCompletion(String path) {
                mCallback.onComplete();
                currentStatue = PLAY_STATUE_END;
                btnPlay.setVisibility(View.VISIBLE);
                videoPause(false);
                if(isLooperPlay){
                    replay();
                }
            }

            @Override
            public void onPrepared(String path) {
                currentStatue = PLAY_STATUE_READY;
                btnPlay.setVisibility(View.VISIBLE);
                dismissProgress();
                videoPause(false);
//                currentStatue = PLAY_STATUE_PAUSE;
                if(isNeedPreparedAutoPlay){
                    play();
                }
            }

            @Override
            public void onInfo(String key, int what) {
              //监听播放卡、监听开始拖动、监听卡完成、监听拖动缓冲完成。
                switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    //监听播放卡、监听开始拖动
                    mCallback.onBufferingStart();
                    showProgress();
                    removeCallbacks(hidVideoImage);
                    currentStatue = PLAY_STATUE_BUFFERING;
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    //监听卡完成、监听拖动缓冲完成
                    mCallback.onBufferingEnd();
                    dismissProgress();
                    hidVideoImage();
                    currentStatue = PLAY_STATUE_READY;
                    break;

                default:
                    break;
                }
            }

            @Override
            public void onGetVideoSize(int mVideoWidth, int mVideoHeight) {
                int width = framelayout.getWidth();
                int height = framelayout.getHeight();
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    if ( mVideoWidth * height  > width * mVideoHeight ) {
                        LogUtil.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth;
                    } else if ( mVideoWidth * height  < width * mVideoHeight ) {
                        LogUtil.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight;
                    } else {
                        //LogUtil.i("@@@", "aspect ratio is correct: " +
                                //width+"/"+height+"="+
                                //mVideoWidth+"/"+mVideoHeight);
                    }
                }
                
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                framelayout.setLayoutParams(params);
                playerManager.setIsNeedGetVideoSize(false);
            }
        });
    }

    private void showProgress() {
        if(mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressView == null) {
                    return;
                }
                btnPlay.setVisibility(View.INVISIBLE);
                progressView.setVisibility(View.VISIBLE);
//                loadProgress.setVisibility(View.VISIBLE);
//                AnimationDrawable animation = (AnimationDrawable) loadingImg.getBackground();
//                animation.start();

            }
        });
    }

    private void dismissProgress() {
        if(mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressView == null) {
                    return;
                }
                progressView.setVisibility(GONE);
//                if(loadProgress == null) {
//                    return;
//                }
//                loadProgress.setVisibility(View.INVISIBLE);
//                AnimationDrawable animation = (AnimationDrawable) loadingImg.getBackground();
//                animation.stop();
            }
        });
    }

    public Boolean isPlaying(){
            return playerManager.isPlaying(remoteUrl);
    }
    private void playVideo() {
        if(!AppContext.isNetworkAvailable()) {
              ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.not_network));
              return;
        }
        
        if(!StringUtil.isEmpty(remoteUrl) && playerManager != null) {
            if(isFirstPlay){
                mCallback.onFirstPlay();
                isFirstPlay=false;
            }
            videoPlay(true);
            currentStatue=PLAY_STATUE_BUFFERING;
            showProgress();
        }
    }

    private Handler mHandler = new Handler() ;

    public void onSingleClick() {
        LogUtil.i("currentStatue=========", currentStatue+"");
        switch (currentStatue) {
        case PLAY_STATUE_BUFFERING:
//            if(isNeedPreparedAutoPlay){
                onBufPause();
                release();
                currentStatue=PLAY_STATUE_DEFAULT;
//            }else{
//                onBufPlay();
//            }
            break;
        case PLAY_STATUE_READY:
            if(isPlaying()){
                pause();
            }else{
                play();
            }
            break;
        case PLAY_STATUE_PALYING:
            pause();
            break;
        case PLAY_STATUE_PAUSE:
            play();
            break;
        case PLAY_STATUE_END:
            replay();
            break;
        case PLAY_STATUE_ERROR:
        case PLAY_STATUE_DEFAULT:
            release();
            playVideo();
            if(mCallback==null){
                mCallback=new  VideoControlerViewCallback() {
                    @Override
                    public void onPlay() {}
                    @Override
                    public void onPause() {}
                    @Override
                    public void onError() {}
                    @Override
                    public void onComplete() {}
                    @Override
                    public void onFirstPlay() {}
                    @Override
                    public void onBufferingStart() {}
                    @Override
                    public void onBufferingEnd() {}
                    @Override
                    public void onDoubleClick() {}
                    @Override
                    public void onReplay() {}
                };
            }
            break;
        default:
            break;
        }
        
    }
    /**已播放过，再一次从头播放
     * 
     */
    public void replay() {
        mCallback.onReplay();
        videoPlay(true);
        onPlayDo();
            
    }
    
    public void play(){
        mCallback.onPlay();
        videoPlay(false);
        onPlayDo();
    }
    
    public int getCurrentPos(){
        return playerManager.getCurrentPosition(remoteUrl);
    }
    
    public void seekTo(int i) {
        playerManager.seekTo(i, remoteUrl);
    }
    public void seekToIfNeed() {
        int pos=playerManager.getCurrentPosition(remoteUrl);
        if(pos!=0){
            seekTo(pos);
        }
        
    }
    
    
    public void setVideoImageVisibility(){
        mVideoImage.setVisibility(View.VISIBLE);
    }
    private Runnable hidVideoImage=new Runnable() {//减少视频图像加载瞬间的黑屏
        
        @Override
        public void run() {
            mVideoImage.setVisibility(View.INVISIBLE);
        }
    };
        
    private void onPlayDo() {
        isNeedPreparedAutoPlay=true;
        btnPlay.setVisibility(View.INVISIBLE);
        currentStatue = PLAY_STATUE_PALYING;
        dismissProgress();
        hidVideoImage();
    }
    /**
     * 
     */
    private void hidVideoImage() {
        if(mVideoImage.getVisibility()==View.VISIBLE){
            postDelayed(hidVideoImage, 500);
        }
    }
    
    public void pause(){
        currentStatue = PLAY_STATUE_PAUSE;
        onBufPause();
    }
    public void onBufPlay(){
        isNeedPreparedAutoPlay=true;
        btnPlay.setVisibility(View.INVISIBLE);
        showProgress();
//        play();
    }
    public void onBufPause(){
        isNeedPreparedAutoPlay=false;
        mCallback.onPause();
        doPause();
    }
    private void doPause() {
        videoPause(false);
        btnPlay.setVisibility(View.VISIBLE);
        dismissProgress();
        removeCallbacks(hidVideoImage);
    }
    
    public void release(){
        isNeedPreparedAutoPlay=true;
        playerManager.releaseMediaPlayer(remoteUrl);
        LogUtil.e("release===============", "errorCount=" + errorCount);
        errorCount=0;
    }
    
    public interface VideoControlerViewCallback{
        public void onPlay();
        public void onReplay();
        public void onFirstPlay();
        public void onComplete();
        public void onError();
        public void onPause();
        public void onBufferingStart();
        public void onBufferingEnd();
        public void onDoubleClick();
    }
    
    public void replayTextureView(){
        playerManager.replaceTextureView(getContext(), remoteUrl);
    }
    
    public void destoryHandler(){
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
    
    /**
     * 以下是playManager相关======================================================
     */
//    private static final int SHOW_VIDEO = 4;
//    private void delayForPlayVideo(long delayTime) {
//        Message msg = mHandler.obtainMessage();
//        msg.what = 0;
//        mHandler.sendMessageDelayed(msg, delayTime);
//    }
    private void videoPlay(boolean fromStart){
        if(playerManager==null){
            return;
        }
        if (framelayout.getVisibility() != View.VISIBLE) {
            framelayout.setVisibility(View.VISIBLE);
        }
        if (fromStart) {
            playerManager.playFromStar(framelayout, getContext(), remoteUrl);
        }else{
            playerManager.play(framelayout, getContext(), remoteUrl);
        }
    }
    private void videoPause(boolean seekTo0){
        if(playerManager==null){
            return;
        }
//        if (seekTo0) {
//            currentStatue=PLAY_STATUE_DEFAULT;
//        }
        playerManager.pause(remoteUrl, seekTo0);
    }
    
    /**
     * 以上是playManager相关======================================================
     */
    
    /**
     * 以下是判断单击双击事件的相关======================================================
     */
    private static final int MAX_INTERVAL_FOR_CLICK = 250;
    private static final int MAX_DISTANCE_FOR_CLICK = 100;
    private static final int MAX_DOUBLE_CLICK_INTERVAL = 500;
    private int mDownX = 0;
    private int mDownY = 0;
    private int mTempX = 0;
    private int mTempY = 0;
    boolean mIsWaitUpEvent = false;
    boolean mIsWaitDoubleClick = false;
    
    private Runnable mTimerForUpEvent = new Runnable() {
        public void run() {
            if (mIsWaitUpEvent) {
                mIsWaitUpEvent = false;
            } 
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsWaitUpEvent && event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDownX = (int) event.getX();
            mDownY = (int) event.getY();
            mIsWaitUpEvent = true;
            postDelayed(mTimerForUpEvent, MAX_INTERVAL_FOR_CLICK);
            break;
        case MotionEvent.ACTION_MOVE:
            mTempX = (int) event.getX();
            mTempY = (int) event.getY();
            if (Math.abs(mTempX - mDownX) > MAX_DISTANCE_FOR_CLICK
                    || Math.abs(mTempY - mDownY) > MAX_DISTANCE_FOR_CLICK) {
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
            }
            break;
        case MotionEvent.ACTION_UP:
            mTempX = (int) event.getX();
            mTempY = (int) event.getY();
            if (Math.abs(mTempX - mDownX) > MAX_DISTANCE_FOR_CLICK
                    || Math.abs(mTempY - mDownY) > MAX_DISTANCE_FOR_CLICK) {
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
                break;
            } else {
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
                onFirstClick();
                return super.onTouchEvent(event);
            }
        case MotionEvent.ACTION_CANCEL:
            mIsWaitUpEvent = false;
            removeCallbacks(mTimerForUpEvent);
            break;
        default:
        }
        return super.onTouchEvent(event);
    }

    private Runnable mTimerForSecondClick = new Runnable() {
        @Override
        public void run() {
            if (mIsWaitDoubleClick) {
                mIsWaitDoubleClick = false;
                onSingleClick();
                LogUtil.i("onSingleClick=================", "onSingleClick");
            } 
        }
    };

    public void onFirstClick() {
        LogUtil.i("onFirstClick=================", "mIsWaitDoubleClick="+mIsWaitDoubleClick);
        if (mIsWaitDoubleClick) {
            onDoubleClick();
            mIsWaitDoubleClick = false;
            removeCallbacks(mTimerForSecondClick);
        } else {
            mIsWaitDoubleClick = true;
            postDelayed(mTimerForSecondClick, MAX_DOUBLE_CLICK_INTERVAL);
        }
    }

    public void onDoubleClick() {
        //这里双击操作
        mCallback.onDoubleClick();
        LogUtil.i("onDoubleClick=================", "onDoubleClick");
    }
    /**
     * 以上是判断单击双击事件的相关======================================================
     */
    
}
