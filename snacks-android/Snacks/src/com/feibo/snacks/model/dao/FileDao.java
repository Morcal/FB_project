package com.feibo.snacks.model.dao;

import com.feibo.snacks.model.dao.upload.UploadFactory;
import com.feibo.snacks.model.dao.upload.UploadListener;

import java.io.File;

/**
 * 为操作加入异步框架
 * 将结果抛出到主线程
 * Created by lidiqing on 15-9-14.
 */
public class FileDao {

    // 上传头像
    public static void uploadAvatar(final File imageFile, final FileDaoListener listener) {
        new Thread(() -> {
                UploadFactory.avatar().put(imageFile, new UploadListener() {
                    @Override
                    public void success(Object object) {
                        listener.success(object);
                    }

                    @Override
                    public void progress(float progress) {
                    }

                    @Override
                    public void fail(ResultCode resultCode) {
                        listener.fail(resultCode);
                    }
                });
        }).start();
    }

    public static interface FileDaoListener {
        void success(Object object);
        void progress(float progress);
        void fail(ResultCode resultCode);
    }
}
