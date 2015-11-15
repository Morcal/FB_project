package com.feibo.joke.view.widget.videoControlView.manager;

import java.util.Collection;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import fbcore.log.LogUtil;

import com.feibo.joke.view.widget.videoControlView.manager.MediaCombinationCreater.MediaCombination;
import com.feibo.joke.view.widget.videoControlView.manager.MediaCombinationCreater.MediaPlayerStateListener;
import com.feibo.joke.view.widget.videoControlView.memory.BaseMemoryCache;
import com.feibo.joke.view.widget.videoControlView.memory.SoftMemoryCache;


@SuppressLint("NewApi") 
public class MediaPlayerManager {

    private static final String TAG = MediaPlayerManager.class.getSimpleName();

    private MediaStateListener listener;
    private Boolean isNeedGetVideoSize=true;

    private static BaseMemoryCache<MediaCombination> cache = new SoftMemoryCache<MediaCombination>();

    private static MediaPlayerManager manager;

    public static MediaPlayerManager getInstance() {
        if (manager == null) {
            manager = new MediaPlayerManager();
        }
        return manager;
    }

    public void play(FrameLayout frameLayout,Context context,String path) {
        showVideoCanvas(frameLayout, context, path, false);
    }
    public void playFromStar(FrameLayout frameLayout,Context context,String path) {
        showVideoCanvas(frameLayout, context, path, true);
    }
    
    private void showVideoCanvas(FrameLayout frameLayout, Context context, final String path,Boolean seekToO) {
        MediaCombination mediaCombination = getFromCache(path);
        if (mediaCombination != null) {
            MediaPlayer mediaPlayer = mediaCombination.getMediaPlayer();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return;
            }
            useExitCanvas(frameLayout, mediaCombination);
            if (!mediaCombination.isPrepared()) {
                return;
            }
            playVideo(mediaPlayer,seekToO);
            return;
        }

        mediaCombination = createMediaCombination(context, path);
        if (mediaCombination == null) {
            return;
        }
        TextureView textureView = mediaCombination.getTextureView();
        if (textureView == null) {
            return;
        }
        addVideoCanvas(frameLayout, path, mediaCombination, textureView);
    }
    
    public void seekTo(int position,String path) {
        MediaPlayer mediaPlayer=getMediaPlayer(path);
        if (mediaPlayer != null){
            mediaPlayer.seekTo(position);
        }
    }
    
    public void pause(String path,Boolean seekTo0) {
        MediaPlayer mediaPlayer=getMediaPlayer(path);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                if(seekTo0){
                    mediaPlayer.seekTo(0);
                }
                mediaPlayer.pause();
            }
    }
    public int getCurrentPosition(String path) {
        MediaPlayer mediaPlayer=getMediaPlayer(path);
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    private void playVideo(MediaPlayer mediaPlayer,Boolean isSeekToO) {
        stopAllMediaPlayer();
        if(isSeekToO){
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();
    }

    public void replaceTextureView(Context context, String path) {
        MediaCombination combition = getFromCache(path);
        if (combition != null && combition.getMediaPlayer() != null) {
            combition.replaceTextureView(context, path, addMediaPlayerStateListener(path));
        }
    }
    
    private void useExitCanvas(FrameLayout frameLayout, MediaCombination mediaCombination) {
        TextureView textureView = mediaCombination.getTextureView();
        if (textureView.getParent() != frameLayout || frameLayout.getChildCount() == 0) {
            ViewGroup viewGroup = (ViewGroup) textureView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(textureView);
            }
            frameLayout.addView(textureView);
        }
    }

    private void addVideoCanvas(FrameLayout frameLayout, final String path, MediaCombination mediaCombination,
            TextureView textureView) {
        frameLayout.removeAllViews();
        cache.put(path, mediaCombination);
        if (textureView.getParent() != null) {
            ((ViewGroup)textureView.getParent()).removeView(mediaCombination.getTextureView());
        }
        frameLayout.addView(textureView);
    }

    private MediaCombination createMediaCombination(Context context, final String path) {
        MediaCombination mediaCombination;
        mediaCombination = MediaCombinationCreater.create(context, path, addMediaPlayerStateListener(path));
        if (mediaCombination == null) {
            return null;
        }
        return mediaCombination;
    }

    private MediaPlayerStateListener addMediaPlayerStateListener(final String path) {
        return new MediaPlayerStateListener() {
            int pos;
            @Override
            public void onViewDestory(String key) {
                MediaCombination combition = getFromCache(path);
                if (combition != null && combition.getMediaPlayer() != null) {
                    MediaPlayer mp=combition.getMediaPlayer();
                    pos=mp.getCurrentPosition();
//                    LogUtil.i(TAG, "pos---"+pos);
                    combition.getMediaPlayer().pause();
                }
            }

            @Override
            public void onViewAvailable(Surface surface, String key) {
                MediaCombination combition = getFromCache(key);
                if (combition != null && combition.getMediaPlayer() != null) {
                    MediaPlayer mp=combition.getMediaPlayer();
                    mp.setSurface(surface);
//                    int pos1=mp.getCurrentPosition();
//                    LogUtil.i(TAG, "pos1---"+pos1);
                    if(pos!=0){//切换其他应用时避免画面变全黑，但是因为10s和seekto偏移，所以画面会不一样
                        mp.seekTo(pos);
                        LogUtil.i(TAG, "seekTo pos---"+pos);
                    }
                }
            }

            @Override
            public void onPrepared(String key) {
                MediaCombination combition = cache.get(key);
                
                if (combition == null || combition.getMediaPlayer() == null) {
                    return;
                }
                if(isNeedGetVideoSize){
                    int mVideoWidth = combition.getMediaPlayer().getVideoWidth();
                    int mVideoHeight = combition.getMediaPlayer().getVideoHeight();
                    if (listener != null) {
                        listener.onGetVideoSize(mVideoWidth,mVideoHeight);
                    }
                }
                
                stopAllMediaPlayer();
                combition.setPrepared(true);
                if (listener != null) {
                    listener.onPrepared(path);
                }
            }

            @Override
            public void onError(MediaPlayer mp,String key) {
//                cache.remove(key);
                //TODO
                LogUtil.e(TAG, "onError");
                if (listener != null) {
                    listener.onError(mp,path);
                }
            }

            @Override
            public void onCompletion(String key) {
//                MediaCombination combition = cache.get(key);
//                if (combition == null || combition.getMediaPlayer() == null) {
//                    return;
//                }
//                MediaPlayer mp = combition.getMediaPlayer();
//                mp.seekTo(0);
//                mp.pause();
                if (listener != null) {
                    listener.onCompletion(key);
                }
            }

            @Override
            public void onInfo(String key, int what) {
                if (listener != null) {
                    listener.onInfo(key,what);
                }
            }

            @Override
            public void onPrepare(String key,int percent) {
                if (listener != null) {
                    listener.onPrepare(percent);
                }
            }
        };
    }

    /**
     * 
     * @param is 为true才会回调onGetVideoSize()
     */
    public void setIsNeedGetVideoSize(Boolean is){
        isNeedGetVideoSize=is;
    }
    
    public void releaseMediaPlayer(String path) {
        LogUtil.e(TAG, "releaseMediaPlayer:" + path);
        MediaPlayer mediaPlayer=getMediaPlayer(path);
        if (mediaPlayer != null) {
            LogUtil.e(TAG, "releaseMediaPlayer: ok");
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        cache.remove(path);
        isNeedGetVideoSize=true;
    }
    
    private MediaPlayer getMediaPlayer(String path){
        MediaCombination combination = getFromCache(path);
        MediaPlayer mediaPlayer=null;
        if(combination!=null){
            mediaPlayer = combination.getMediaPlayer();
        }
        return mediaPlayer;
    }
    
    private void stopAllMediaPlayer() {
        Collection<String> keys = cache.keys();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            MediaPlayer mediaPlayer=getMediaPlayer(key);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
//                mediaPlayer.seekTo(0);
            }
        }
    }

    public boolean isPlaying(String path) {
        MediaPlayer mediaPlayer=getMediaPlayer(path);
        if (mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private MediaCombination getFromCache(String path) {
        if (path.isEmpty()) {
            return null;
        }
        MediaCombination combination = cache.get(path);
        return combination;
    }

    public void setMediaStateListener(MediaStateListener listener) {
        this.listener = listener;
    }

    public static interface MediaStateListener {
        void onPrepare(int progress);

        void onGetVideoSize(int mVideoWidth, int mVideoHeight);

        void onPrepared(String path);

        void onInfo(String key, int what);

        void onCompletion(String path);

        void onError(MediaPlayer mp, String path);
    }
}
