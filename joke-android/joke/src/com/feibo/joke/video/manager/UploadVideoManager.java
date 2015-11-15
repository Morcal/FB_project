package com.feibo.joke.video.manager;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fbcore.log.LogUtil;
import fbcore.utils.Files;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.video.manager.VideoManager.OnSaveDraftListener;
import com.feibo.joke.video.util.HttpMultipartPost.OnPostListener;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.util.ToastUtil;

/**
 * 上传视频管理类
 *
 * @author ml_bright
 * @version 2015-5-29  下午3:40:18
 */
public class UploadVideoManager {

    /**
     * 处于空闲状态
     */
    private static final int STATE_FREE = 0;
    /**
     * 正在加载中状态
     */
    private static final int STATE_LOADING = 1;
    /**
     * 上传成功状态
     */
    private static final int STATE_SUCESS = 2;
    /**
     * 上传失败状态
     */
    private static final int STATE_FAILURE = 3;

    private static UploadVideoManager manager;

    private VideoManager mVideoManager;

    private OnUploadListener onUploadListener;

    /**
     * 当前进度状态
     */
    private int mProgress;

    private int mCurrentStatu;

    private Video mVideo;

    private UploadVideoManager(Context context) {
        setCurrentStatu(STATE_FREE);
        this.mVideoManager = VideoManager.getInstance(context);
    }

    public static UploadVideoManager getInstance(Context context) {
        if (manager == null) {
            manager = new UploadVideoManager(context);
        }
        return manager;
    }

    public synchronized void uploadVideo(Context context) {
        /**
         * 当上次上传视频到一半的过程中出现中断上传时才会做以下处理
         */
        if (getCurrentState() == STATE_LOADING) {
            //上次上传的视频还没上传完
            if (onUploadListener != null) {
                onUploadListener.onStart(mProgress);
            }
            return;
        }
        if (getCurrentState() == STATE_FAILURE) {
            //上次上传的视频失败
            if (onUploadListener != null) {
                onUploadListener.onFail(ReturnCode.RS_POST_ERROR);
            }
        }
        if (getCurrentState() == STATE_SUCESS) {
            if (onUploadListener != null) {
                onUploadListener.onSuccuss(mVideo);
            }
        }
        if (mVideoManager.shouldUpload()) {
            setCurrentStatu(STATE_LOADING);
            startUploadVideo();
        }
    }

    public boolean isUploading() {
        return mVideoManager.shouldUpload() || getCurrentState() == STATE_LOADING;
    }

    private void startUploadVideo() {
        // 开始上传
        final File file2Upload = mVideoManager.getVideoFile();
        UploadManager.uploadVideo(mVideoManager.getIntroduce(), mVideoManager.getCoverFile(),
                file2Upload, new OnPostListener() {

                    @Override
                    public void onStart() {
                        LogUtil.d("startUploadVideo", "startUploadVideo->onStart()");
                        mVideoManager.setShouldUpload(false);
                        if (onUploadListener != null) {
                            onUploadListener.onStart(0);
                        }
                    }

                    @Override
                    public void onResult(String result) {
                        LogUtil.d("startUploadVideo", "startUploadVideo->onResult first");
                        // 上传结束
                        Response<Video> response = null;
                        try {
                            response = new Gson().fromJson(result, (new TypeToken<Response<Video>>() {
                            }).getType());
                        } catch (Exception e) {
                            Log.e("upload video", "upload video : " + result);
                            e.printStackTrace();
                            ToastUtil.showSimpleToast("网络错误");
                            uploadFail();
                            return;
                        }

                        LogUtil.d("startUploadVideo", "startUploadVideo->onResult second");
                        // 数据返回错误
                        if (response == null || response.rsCode != ReturnCode.RS_SUCCESS) {
                            uploadFail();
                            return;
                        }
                        LogUtil.d("startUploadVideo", "startUploadVideo->onResult third");

                        mVideo = response.data;
                        if (mVideo == null || mVideo.thumbnail == null || StringUtil.isEmpty(mVideo.thumbnail.url)) {
                            uploadFail();
                            return;
                        }
                        LogUtil.d("startUploadVideo", "startUploadVideo->onResult fourth");

                        save2SystemCaremaPathIfNeed(file2Upload.getAbsolutePath());

                        mVideoManager.deleteDraft(file2Upload.getAbsolutePath());

                        // 成功
                        setCurrentStatu(STATE_SUCESS);
                        if (onUploadListener != null) {
                            onUploadListener.onSuccuss(mVideo);
                        }
                        LogUtil.d("ShareSina", "isNeedShareToSina:" + (mVideoManager == null ? false : mVideoManager.isNeedShareToSina()));
                        if (mVideoManager != null && mVideoManager.isNeedShareToSina()) {
                            if (onUploadListener != null) {
                                onUploadListener.onShareSina(mVideo);
                            }
                        }
                        release();
                        LogUtil.d("startUploadVideo", "startUploadVideo->onResult end");
                    }

                    private void uploadFail() {
                        LogUtil.d("startUploadVideo", "startUploadVideo->uploadFail");
                        setCurrentStatu(STATE_FAILURE);
                        if (onUploadListener != null) {
                            savaDraft();
                            onUploadListener.onFail(ReturnCode.RS_POST_ERROR);
                        }
                    }

                    @Override
                    public void onProgress(int progress) {
                        LogUtil.d("startUploadVideo", "startUploadVideo->onProgress:"+progress);
                        // 广播进度
                        mProgress = progress;
                        if (onUploadListener != null) {
                            onUploadListener.onProgress(progress);
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("startUploadVideo", "startUploadVideo->onCancel");
                    }
                });
    }

    /**
     * 上传失败后保存到草稿箱
     */
    private void savaDraft() {
        mVideoManager.saveDraft(new OnSaveDraftListener() {

            @Override
            public void onSuccess() {
                LogUtil.d("startUploadVideo", "startUploadVideo->saveDraft onSuccess");
                mVideoManager.setShouldUpload(false);
                if (onUploadListener != null) {
                    onUploadListener.onSaveDraft(true);
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFail() {
                LogUtil.d("startUploadVideo", "startUploadVideo->saveDraft onFail");
                mVideoManager.setShouldUpload(true);
                if (onUploadListener != null) {
                    onUploadListener.onSaveDraft(false);
                }
            }
        });
    }

    public boolean checkCanStartUpload() {
        if (mVideoManager.shouldUpload()) {
            startUploadVideo();
            return true;
        }
        return false;
    }

    public void release() {
        setCurrentStatu(STATE_FREE);
        LogUtil.d("ShareSina", "release");
        // 释放资源
        mVideoManager.setShouldUpload(false);
        mVideoManager.setNeedShareToSina(false);
//        mVideo = null;
    }

    public void setCurrentStatu(int state) {
        this.mCurrentStatu = state;
    }

    public int getCurrentState() {
        return mCurrentStatu;
    }

    private void save2SystemCaremaPathIfNeed(String src) {
        if (AppContext.getContext() == null) {
            return;
        }
        if (!SPHelper.getSaveVideoLocal(AppContext.getContext())) {
            return;
        }
        String dest = Environment.getExternalStorageDirectory() + File.separator
                + "DCIM/Camera/LXH_" + System.currentTimeMillis() + ".mp4";
        if (Files.copy(src, dest)) {
            File file = new File(dest);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            AppContext.getContext().sendBroadcast(intent);
        }
    }

    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    public Video getVideo() {
        return mVideo;
    }

    public interface OnUploadListener {
        public void onStart(int progress);

        public void onFail(int code);

        public void onSuccuss(Video video);

        public void onProgress(int progress);

        public void onSaveDraft(boolean succuss);

        public void onShareSina(Video video);
    }

}
