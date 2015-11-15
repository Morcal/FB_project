package com.feibo.snacks.model.dao;

/**
 * 错误返回类型
 * 后面可进行拓展
 * Created by lidiqing on 15-9-14.
 */
public enum  ResultCode {

    // 网络相关
    RS_NET_NOT_EXIST(0x0001, "没有网络，请检查网络"),

    // 本地文件相关
    RS_FILE_NOT_EXIST(0x0002, "文件不存在"),
    RS_FILE_EXCEPTION(0x0003, "打开文件异常"),

    // 上传相关
    RS_UPLOAD_IMAGE_FAIL(0x0004, "图片上传失败");

    public int code;
    public String msg;
    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
