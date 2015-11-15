package com.feibo.snacks.model.dao.upload;

import com.feibo.snacks.model.dao.ResultCode;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import fbcore.log.LogUtil;
import main.java.com.UpYun;

/**
 * 用户头像上传类, 使用UPYUN云服务
 * Created by lidiqing on 15-9-14.
 */
class AvatarUpload implements IUpload {
    private static final String TAG = AvatarUpload.class.getSimpleName();

    // UPYUN配置
    // 空间, 操作员, 密码
    private static final String BUCKET_NAME = "lingshi";
    private static final String OPERATOR_NAME = "lingshi";
//    private static final String OPERATOR_PWD = "Aqf+b+vLwhG8koIW89t2fCAycmw=";
    private static final String OPERATOR_PWD = "lingshi498j";

    // 域名
    private static final String URL = "http://img.lingshi.cccwei.com";

    // 根目录
    private static final String DIR_ROOT = "/lingshi/avatar";

    AvatarUpload() {
    }

    @Override
    public void put(File file) {
        put(file, new EmptyUploadListener());
    }

    @Override
    public void put(File file, UploadListener listener) {
        if (!file.exists()) {
            listener.fail(ResultCode.RS_FILE_NOT_EXIST);
            return;
        }

        try {
            // 要传到upyun后的文件路径
            String filePath = createAvatarFilePath(file);
            LogUtil.i(TAG, "put filePath:" + filePath);


            // 设置待上传文件的 Content-MD5 值
            // 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
            UpYun upYun = createUpYun();
            String md5Str = UpYun.md5(file);
            upYun.setContentMD5(md5Str);
            LogUtil.i(TAG, "put md5:" + md5Str + md5Str);
            boolean result = upYun.writeFile(filePath, file, true);
            LogUtil.i(TAG, "put result:" + result);

            if (!result) {
                listener.fail(ResultCode.RS_UPLOAD_IMAGE_FAIL);
                return;
            }

            if (upYun.getPicType() == null) {
                listener.fail(ResultCode.RS_UPLOAD_IMAGE_FAIL);
                return;
            }

            String accessPath = URL + filePath;
            LogUtil.i(TAG, "put accessPath:" + accessPath);
            listener.success(accessPath);
        } catch (IOException e) {
            e.printStackTrace();
            listener.fail(ResultCode.RS_UPLOAD_IMAGE_FAIL);
        }
    }

    private String createAvatarFilePath(File file) {
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String name = UUID.randomUUID().toString();
        return new StringBuilder(DIR_ROOT).
                append("/").append(year).
                append("/").append(month).
                append("/").append(name).
                append(".").append(fileType).toString();
    }

    private UpYun createUpYun() {
        UpYun upYun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);
        upYun.setDebug(true);
        upYun.setTimeout(30);
        return upYun;
    }

    class EmptyUploadListener implements UploadListener {

        @Override
        public void success(Object object) {
        }

        @Override
        public void progress(float progress) {
        }

        @Override
        public void fail(ResultCode resultCode) {
        }
    }

}
