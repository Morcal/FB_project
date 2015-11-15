package com.feibo.snacks.model.dao.upload;

/**
 * 上传类工厂类
 * 上传类可分为上传普通文件, 上传图片文件
 * 以后可支持断点续传等功能
 * 具体功能由Iupload的实现类提供
 * Created by lidiqing on 15-9-14.
 */
public class UploadFactory {

    private static IUpload avatarUpload;

    static {
        avatarUpload = new AvatarUpload();
    }

    private UploadFactory() {
    }

    public static IUpload avatar() {
        return avatarUpload;
    }
}
