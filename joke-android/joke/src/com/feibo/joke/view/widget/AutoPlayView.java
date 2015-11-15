package com.feibo.joke.view.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fbcore.log.LogUtil;

/**
 * Created by lidiqing on 15-10-15.
 */
public class AutoPlayView extends ViewGroup {
    private String TAG = AutoPlayView.class.getSimpleName();
    private static Executor releaseExecutor = Executors.newCachedThreadPool();

    private VideoPlayView videoView;
    private String videoPath;
    private int videoWidth;
    private int videoHeight;

    public AutoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        videoView = new VideoPlayView(context);
        addViewInLayout(videoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setTag(String tag) {
        TAG = AutoPlayView.class.getSimpleName() + "#" + tag;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (videoPath == null) {
            return;
        }

        int width = r - l;
        int height = b - t;
        int actualWidth;
        int actualHeight;
        float offsetX;
        float offsetY;
        if (videoWidth > videoHeight) {
            actualWidth = width;
            actualHeight = (int) (videoHeight / (float) videoWidth * width);
            offsetX = 0;
            offsetY = height / 2.0f - actualHeight / 2.0f;
        } else {
            actualHeight = height;
            actualWidth = (int) (videoWidth / (float) videoHeight * height);
            offsetY = 0;
            offsetX = width / 2.0f - actualWidth / 2.0f;
        }

        videoView.layout((int) Math.floor(offsetX), (int) Math.floor(offsetY), (int) Math.ceil(offsetX + actualWidth), (int) Math.ceil(offsetY + actualHeight));
    }

    public void initVideo(String videoPath, int videoWidth, int videoHeight, PlayCycleListener listener) {
        this.videoPath = videoPath;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        videoView.initVideo(listener);
        requestLayout();
    }

    public void startVideo() {
        videoView.startVideo();
    }

    public void pauseOrStartVideo() {
        videoView.pauseOrStartVideo();
    }

    public void stopVideo() {
        videoView.stopVideo();
    }

    public class VideoPlayView extends TextureView implements TextureView.SurfaceTextureListener {
        // 动作标记，对应5个动作，分别为初始化，开始，暂停，结束和回收
        private static final int FLAG_INIT = 0;
        private static final int FLAG_START = 2;
        private static final int FLAG_PAUSE = 3;
        private static final int FLAG_STOP = 4;
        private static final int FLAG_RELEASE = 5;

        // MediaPlay的状态
        private static final int STATE_IDLE = 0;
        private static final int STATE_PREPARING = 1;
        private static final int STATE_PREPARED = 2;
        private static final int STATE_STARTED = 3;
        private static final int STATE_PAUSED = 4;
        private static final int STATE_STOPPED = 5;


        private MediaPlayerHolder mediaPlayerHolder;
        private PlayCycleListener cycleListener;
        private Surface surface;
        private int flag;
        private int state;

        private boolean hasInit = false;
        private Object lock = new Object();

        public VideoPlayView(Context context) {
            super(context);
            setSurfaceTextureListener(this);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            LogUtil.e(TAG, "onAttachedToWindow");
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            LogUtil.e(TAG, "onDetachFromWindow");
            releaseVideo();
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            LogUtil.i(TAG, "onSurfaceTextureAvailable");
            if (mediaPlayerHolder != null) {
                surface = new Surface(surfaceTexture);
                mediaPlayerHolder.mediaPlayer.setSurface(surface);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

        public void initVideo(PlayCycleListener listener) {
            synchronized (lock) {
                LogUtil.e(TAG, "initVideo");
                flag = FLAG_INIT;
                state = STATE_IDLE;
                hasInit = true;
                cycleListener = listener;
                mediaPlayerHolder = new MediaPlayerHolder(new MediaPlayStateListener() {
                    @Override
                    public void onPrepared() {
                        synchronized (lock) {
                            state = STATE_PREPARED;
                            switch (flag) {
                                case FLAG_START:
                                    LogUtil.e(TAG, "onPrepared start");
                                    mediaPlayerHolder.mediaPlayer.start();
                                    state = STATE_STARTED;
                                    cycleListener.onState(PlayCycleListener.STATE_START_FIRST);
                                    cycleListener.onState(PlayCycleListener.STATE_STARTED);
                                    break;
                                case FLAG_PAUSE:
                                    LogUtil.e(TAG, "onPrepared pause");
                                    state = STATE_PAUSED;
                                    cycleListener.onState(PlayCycleListener.STATE_PAUSED);
                                    break;
                                case FLAG_STOP:
                                    LogUtil.e(TAG, "onPrepared stop");
//                                    mediaPlayerHolder.mediaPlayer.reset();
//                                    state = STATE_IDLE;
                                    state = STATE_STOPPED;
                                    cycleListener.onState(PlayCycleListener.STATE_STOPPED);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCompletion() {
                        LogUtil.e(TAG, "onCompletion");
                        if (flag == FLAG_START) {
                            startVideo();
                            if (state != STATE_IDLE) {
                                cycleListener.onState(PlayCycleListener.STATE_START_FIRST);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        LogUtil.e(TAG, "onError");
                        // 出错后重置
                        synchronized (lock) {
                            mediaPlayerHolder.mediaPlayer.reset();
                            state = STATE_IDLE;
                            cycleListener.onState(PlayCycleListener.STATE_ERROR);
                        }
                    }
                });
            }
        }

        public void startVideo() {
            synchronized (lock) {
                LogUtil.e(TAG, "startVideo");
                if (!hasInit) {
                    return;
                }
                flag = FLAG_START;
                try {

                    if (state == STATE_IDLE) {
                        LogUtil.e(TAG, "startVideo first");
                        LogUtil.e(TAG, "videoPath:" + videoPath);
                        mediaPlayerHolder.mediaPlayer.setDataSource(videoPath);
                        mediaPlayerHolder.mediaPlayer.prepareAsync();
                        state = STATE_PREPARING;
                        mediaPlayerHolder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        cycleListener.onState(PlayCycleListener.STATE_START_PREPARING);
                        return;
                    }

                    if (state == STATE_PREPARING) {
                        LogUtil.e(TAG, "startVideo wait");
                        return;
                    }

                    if (state == STATE_PREPARED) {
                        LogUtil.e(TAG, "startVideo start:" + state);
                        mediaPlayerHolder.mediaPlayer.start();
                        cycleListener.onState(PlayCycleListener.STATE_START_FIRST);
                        cycleListener.onState(PlayCycleListener.STATE_STARTED);
                        state = STATE_STARTED;
                        return;
                    }

                    if (state == STATE_STOPPED || state == STATE_PAUSED || state == STATE_STARTED) {
                        LogUtil.e(TAG, "startVideo start:" + state);
                        mediaPlayerHolder.mediaPlayer.start();
                        state = STATE_STARTED;
                        cycleListener.onState(PlayCycleListener.STATE_STARTED);
                        return;
                    }

                    LogUtil.e(TAG, "nothing");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopVideo() {
            synchronized (lock) {
                LogUtil.e(TAG, "stopVideo");
                if (!hasInit) {
                    return;
                }
                flag = FLAG_STOP;
//                mediaPlayerHolder.mediaPlayer.reset();
//                state = STATE_IDLE;
//                cycleListener.onState(PlayCycleListener.STATE_STOPPED);

                if (state == STATE_IDLE || state == STATE_PREPARING) {
                    LogUtil.e(TAG, "stopVideo wait");
                    return;
                }

                if (state == STATE_PREPARED || state == STATE_STARTED || state == STATE_PAUSED) {
                    LogUtil.e(TAG, "stopVideo stop:" + state);
//                mediaPlayerHolder.mediaPlayer.seekTo(0);
//                    mediaPlayerHolder.mediaPlayer.pause();
                    mediaPlayerHolder.mediaPlayer.reset();
//                    state = STATE_STOPPED;
                    state = STATE_IDLE;
                    cycleListener.onState(PlayCycleListener.STATE_STOPPED);
                    return;
                }

                LogUtil.e(TAG, "stop nothing:" + state);
            }
        }

        public void pauseOrStartVideo() {
            synchronized (lock) {
                LogUtil.e(TAG, "pauseOrStart flag:" + flag + "; state:" + state);
                if (!hasInit) {
                    return;
                }

                if (flag == FLAG_START) {
                    // 暂停
                    flag = FLAG_PAUSE;
                    if (state == STATE_PREPARING) {
                        return;
                    }
                    if (state == STATE_STARTED) {
                        LogUtil.e(TAG, "pause");
                        mediaPlayerHolder.mediaPlayer.pause();
                        state = STATE_PAUSED;
                        cycleListener.onState(PlayCycleListener.STATE_PAUSED);
                    }
                } else if (flag == FLAG_PAUSE) {
                    // 播放
                    flag = FLAG_START;
                    if (state == STATE_PREPARING) {
                        return;
                    }
                    if (state == STATE_PAUSED) {
                        LogUtil.e(TAG, "start");
                        mediaPlayerHolder.mediaPlayer.start();
                        state = STATE_STARTED;
                        cycleListener.onState(PlayCycleListener.STATE_STARTED);
                    }
                }
            }
        }

        public void releaseVideo() {
            synchronized (lock) {
                flag = FLAG_RELEASE;
                state = STATE_IDLE;
                hasInit = false;

                if (mediaPlayerHolder == null) {
                    return;
                }
                MediaPlayer mediaPlayer = mediaPlayerHolder.mediaPlayer;
                mediaPlayerHolder = null;
                releaseExecutor.execute(new ReleaseRunnable(mediaPlayer));
            }
        }
    }

    public static class ReleaseRunnable implements Runnable {

        private MediaPlayer mediaPlayer;

        public ReleaseRunnable(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void run() {
            mediaPlayer.release();
        }
    }

    public static class MediaPlayerHolder implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener {

        public MediaPlayer mediaPlayer;
        private MediaPlayStateListener listener;

        public MediaPlayerHolder(MediaPlayStateListener listener) {
            try {
                this.listener = listener;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setOnInfoListener(this);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnSeekCompleteListener(this);
                mediaPlayer.setOnVideoSizeChangedListener(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                listener.onError();
            } catch (SecurityException e) {
                e.printStackTrace();
                listener.onError();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                listener.onError();
            }
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            listener.onPrepared();
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            // 当一些特定信息出现或者警告时触发
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.v("Play Error:::", "MEDIA_INFO_BAD_INTERLEAVING");
                    listener.onError();
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.v("Play Error:::", "MEDIA_INFO_METADATA_UPDATE");
                    listener.onError();
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.v("Play Error:::", "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    listener.onError();
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.v("Play Error:::", "MEDIA_INFO_NOT_SEEKABLE");
                    listener.onError();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.v("Play Error:::", "onError called");
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                    listener.onError();
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                    listener.onError();
                    break;
                default:
                    listener.onError();
                    break;
            }
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.v("Play Over:::", "onComletion called");
            listener.onCompletion();
        }
    }

    public interface MediaPlayStateListener {
        void onPrepared();

        void onCompletion();

        void onError();
    }

    public interface PlayCycleListener {
        int STATE_STARTED = 0;
        int STATE_PAUSED = 1;
        int STATE_STOPPED = 2;
        int STATE_ERROR = 3;
        int STATE_START_FIRST = 4;  // 从头播放
        int STATE_START_PREPARING = 5; // 播放缓冲准备

        void onState(int state);
    }
}
