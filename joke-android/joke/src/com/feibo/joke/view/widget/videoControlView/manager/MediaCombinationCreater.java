package com.feibo.joke.view.widget.videoControlView.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup.LayoutParams;

import fbcore.log.LogUtil;

class MediaCombinationCreater {

    private static final String TAG = MediaCombinationCreater.class.getSimpleName();

    public static class MediaCombination {
        private TextureView textureView;
        private MediaPlayer mediaPlayer;
        private boolean isPrepared = false;

        private MediaCombination(TextureView textureView, MediaPlayer mediaPlayer) {
            this.textureView = textureView;
            this.mediaPlayer = mediaPlayer;
        }

        public TextureView getTextureView() {
            return textureView;
        }
        public void replaceTextureView(Context context, String path,
                MediaPlayerStateListener listener) {
            ViewGroup viewGroup = (ViewGroup) this.textureView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.textureView);
            }
            this.textureView=createTextureView(context,path,listener);
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public boolean isPrepared() {
            return isPrepared;
        }

        public void setPrepared(boolean isPrepared) {
            this.isPrepared = isPrepared;
        }
    }

    public static MediaCombination create(Context context, final String path, final MediaPlayerStateListener listener) {
        TextureView textureView = createTextureView(context, path, listener);
        MediaPlayer mediaPlayer = createMediaPlayer(path, listener);
//        mediaPlayer.setLooping(true);
        if (textureView == null || mediaPlayer == null) {
            return null;
        }
        return new MediaCombination(textureView, mediaPlayer);
    }

    private static MediaPlayer createMediaPlayer(final String path, final MediaPlayerStateListener listener) {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            setMediaPlayer(path, listener, mediaPlayer);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    private static void setMediaPlayer(final String path, final MediaPlayerStateListener listener,
            MediaPlayer mediaPlayer) {
        mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                LogUtil.i(TAG, "percent : " +percent);
                if (listener != null) {
                    listener.onPrepare(path,percent);
                }
            }
        });
        mediaPlayer.setOnInfoListener(new OnInfoListener() {
            
            @Override
            public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
                if (listener != null) {
                    listener.onInfo(path,arg1);
                }
                return true;
            }
        });
        mediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.i(TAG, "on error what : " + what + " extra : " + extra);
                if (listener != null) {
                    listener.onError(mp,path);
                }
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.i(TAG, "on completion");
                if (listener != null) {
                    listener.onCompletion(path);
                }
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                LogUtil.i(TAG, "on Prepared");
                if (listener != null) {
                    listener.onPrepared(path);
                }
            }
        });
    }

    @SuppressLint("NewApi") 
    private static TextureView createTextureView(Context context, final String path,
            final MediaPlayerStateListener listener) {
        TextureView textureView = new TextureView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textureView.setLayoutParams(params);
        setSurfaceTextureListener(path, listener, textureView);
        return textureView;
    }

    @SuppressLint("NewApi") 
    private static void setSurfaceTextureListener(final String path, final MediaPlayerStateListener listener,
            TextureView textureView) {
        textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                LogUtil.i(TAG, "SurfaceTextureUpdated");
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                LogUtil.i(TAG, "SurfaceTextureChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                LogUtil.i(TAG, "SurfaceTexturedestroy");
                if (listener != null) {
                    listener.onViewDestory(path);
                }
                return true;
            }

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                LogUtil.i(TAG, "SurfaceTextureavilable");
                Surface surface = new Surface(surfaceTexture);
                if (listener != null) {
                    listener.onViewAvailable(surface, path);
                }
            }
        });
    }

    public static interface MediaPlayerStateListener {
        void onPrepared(String key);
        void onPrepare(String key, int percent);
        void onInfo(String key,int what);
        void onCompletion(String key);
        void onError(MediaPlayer mp, String key);
        void onViewAvailable(Surface surface, String key);
        void onViewDestory(String key);
    }
}
