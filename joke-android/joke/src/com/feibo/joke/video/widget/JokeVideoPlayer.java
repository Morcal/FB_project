package com.feibo.joke.video.widget;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout.LayoutParams;

public class JokeVideoPlayer implements OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPreparedListener, OnSeekCompleteListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback {

	private int mScreenWidth;
	private int mPlayerWidth;
	private int mPlayerHeight;

	private MediaPlayer mMediaPlayer;
	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mSurfaceView;

	private int mDuration;
	private String mVideoPath;
	private OnVideoListener mListener;

	public JokeVideoPlayer(Context context, String path, OnVideoListener listener) {
 		mSurfaceView = new SurfaceView(context);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mDuration = -1;
		mVideoPath = path;
		mListener = listener;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 当SurfaceView中的Surface被创建的时候被调用
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		try {
			mMediaPlayer.setDataSource(mVideoPath);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 在这里我们指定MediaPlayer在当前的Surface中进行播放
		mMediaPlayer.setDisplay(holder);
		// 在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
		mMediaPlayer.prepareAsync();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("Surface Change:::", "surfaceChanged called");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("Surface Destory:::", "surfaceDestroyed called");
		mMediaPlayer.reset();
		mMediaPlayer.release();

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// 当video大小改变时触发
		// 这个方法在设置player的source后至少触发一次
		Log.v("Video Size Change", "onVideoSizeChanged called");
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// seek操作完成时触发
		Log.v("Seek Completion", "onSeekComplete called");
		System.out.println("seek completion");
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// 当prepare完成后，该方法触发，在这里我们播放视频

		// 首先取得video的宽和高
		int mediaWidth = mMediaPlayer.getVideoWidth();
		int mediaHeight = mMediaPlayer.getVideoHeight();

		LayoutParams surfaceParams;
		if (mediaWidth > mediaHeight) {
			 mPlayerWidth = mScreenWidth;
			 mPlayerHeight= (int) (mScreenWidth * (mediaHeight /(float)mediaWidth));
			surfaceParams = new LayoutParams(mPlayerWidth, mPlayerHeight);
			surfaceParams.topMargin = mScreenWidth/2 - mPlayerHeight/2;
		} else if (mediaWidth == mediaHeight) {
			surfaceParams = new LayoutParams(mScreenWidth, mScreenWidth);
		}else{
			mPlayerHeight= mScreenWidth;
			mPlayerWidth = (int) (mScreenWidth * (mediaWidth / (float)mediaHeight));
			surfaceParams = new LayoutParams(mPlayerWidth, mPlayerHeight);
			surfaceParams.leftMargin = mScreenWidth/2 - mPlayerWidth/2;
		}

		mSurfaceView.setLayoutParams(surfaceParams);
		// GOOGLE N5上需要先start才能拿到画面
		mMediaPlayer.start();
		mMediaPlayer.pause();
		mMediaPlayer.seekTo(0);
		mDuration = mMediaPlayer.getDuration();

		if(mListener != null){
			mListener.onPrepare(mMediaPlayer);
		}
	}


	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		 // 当一些特定信息出现或者警告时触发
        switch(what){
        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
            break;
        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
            break;
        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
            break;
        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
            break;
        }
        return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		 Log.e("Play Error:::", "onError called");
	        switch (what) {
	        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
	            Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
	            break;
	        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
	            Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
	            break;
	        default:
	            break;
	        }
	        return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// 当MediaPlayer播放完成后触发
        Log.v("Play Over:::", "onComletion called");
//        this.finish();
	}

	public void seekTo(float progress){
		if(mDuration == -1){
			return;
		}

		int offsetDur = (int) (mDuration*progress);
		mMediaPlayer.seekTo(offsetDur);
	}

	public int getVideoDuration(){
		return mMediaPlayer.getDuration();
	}

	public SurfaceView getSurfaceView(){
		return mSurfaceView;
	}

	public MediaPlayer getMediaPlayer(){
		return mMediaPlayer;
	}

	public void setOnVideoListener(OnVideoListener listener){
		mListener = listener;
	}

	public static interface OnVideoListener{
		void onPrepare(MediaPlayer mediaPlayer);
	}
}
