package com.feibo.joke.video.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fbcore.log.LogUtil;

import com.feibo.joke.dao.UrlBuilder;
import com.feibo.joke.video.util.HttpMultipartPost;
import com.feibo.joke.video.util.HttpMultipartPost.OnPostListener;

public class UploadManager {
    private static final String TAG = "UploadManager";

    public static void uploadVideo(final String desc, final File thumbFile, final File videoFile, final OnPostListener listener) {
        new Thread() {
            @Override
            public void run() {
                //is_water=0 代表后台处理水印
                uploadVideo(UrlBuilder.getPublicParamUrl().append("&srv=2008&is_water=0").toString(), desc, thumbFile, videoFile, listener);
            }
        }.start();
    }

    /**
     * 使用httpclient实现
     *
     * @param url
     * @param desc
     * @param thumbFile
     * @param videoFile
     * @param listener
     */
    public static void uploadVideo(String url, String desc, File thumbFile, File videoFile, OnPostListener listener) {
        LogUtil.i(TAG, url);
        LogUtil.i(TAG, "desc:" + desc);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("desc", desc);
        params.put("thumbnail", thumbFile);
        params.put("video", videoFile);
        HttpMultipartPost post = new HttpMultipartPost(url, params, listener);
        post.execute();
    }

    public static interface OnUploadVideoListener {
        void onProgress(long currentBytes, long totalBytes);
    }
}
