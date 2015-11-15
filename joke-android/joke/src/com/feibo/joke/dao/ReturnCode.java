package com.feibo.joke.dao;

public class ReturnCode {
    
    /**
     * 无网络
     */
    public static final int NO_NET = -1;
    
    /**
     * 没有更多数据
     */
    public static final int RS_LOCAL_NO_MORE_DATA = 1;
    
    /**
     * 上传数据出错
     */
    public static final int RS_POST_ERROR = 2;
    
    /**
     * 取消手势
     */
    public static final int RS_GESTURE_CANCEL = 3;
    
    /**
     * 返回数据成功
     */
    public static final int RS_SUCCESS = 1000;
    
    /**
     * 内部错误
     */
    public static final int RS_INTERNAL_ERROR = 1001;
    
    /**
     * 空数据错误
     */
    public static final int RS_EMPTY_ERROR = 1004;
    
    /**
     * 传参错误
     */
    public static final int RS_PARAMETER_ERROR = 1006;
    
    /**
     * 验证错误
     */
    public static final int RS_VALIDATE_ERROR = 1007;
    
    /**
     * 无权限
     */
    public static final int RS_NO_PRIVILEGE = 1008;
    
    /**
     * 账号已绑定
     */
    public static final int RS_ACCOUNT_HAS_BE_BANDING= 2001;
    
    /**
     * 太过广告
     */
    public static final int RS_AD_WORD_EXISTS = 2002;
    
    /**
     * 敏感词
     */
    public static final int RS_SENSITIVE_WORD_EXISTS = 2003;

    /**
     * 重复点击
     */
    public static final int RS_REPECT_CLICK = 2004;
    
    /**
     *  错误操作对象
     */
    public static final int RS_NONE_OBJECT = 2005;

    /**
     * 重复评论
     */
    public static final int RS_UP_REPECTED_TEXT = 2006;
    

    /**
     * 视频被举报
     */
    public static final int RS_VIDEO_HAS_REPORT = 2007;

    /**
     * 微博授权过期
     */
    public static final int RS_WEIBO_OAUTH_TIMEOUT = 2017;
    
    /**
     * 举报错误返回码
     */
    public static final int RS_POP_REPORT_HINT = 2015;

    /**
     * 验证昵称、签名有效性错误返回码
     */
    public static final int RS_REPERT_NAEM = 2019;
    
}

