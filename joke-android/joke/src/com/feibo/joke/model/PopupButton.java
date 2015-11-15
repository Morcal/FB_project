package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 弹窗按钮结构体定义
 *
 * @author ml_bright
 * @version 2015-6-10  上午10:49:22
 */
public class PopupButton {

    /** 切换到一个外部链接的地址，用浏览器打开 */
    public static final int ACTION_WEB = 2;
    /** 话题详情页 */
    public static final int ACTION_DETAIL_TOPIC = 3;
    /** 视频详情页 */
    public static final int ACTION_DETAIL_VIDEO = 4;
    /** 个人主页 */
    public static final int ACTION_DETAIL_USER = 5;
    /** 意见反馈 */
    public static final int ACTION_FEEDBACK = 6;
    /** 切换到一个内部链接的地址，用内部浏览器打开 */
    public static final int ACTION_INNER_WEB = 7;

    @SerializedName("label")
    public String lable;

    @SerializedName("action")
    public int action;

    @SerializedName("info")
    public String info;

    @SerializedName("is_highlight")
    public int isHighLight;

    public boolean isHighLight() {
        return isHighLight == 1;
    }

}
