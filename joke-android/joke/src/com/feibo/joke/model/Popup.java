package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 消息弹窗
 * @author ml_bright
 * @version 2015-6-10  上午10:47:51
 */
public class Popup {

    /** 代表首页场景 */
    public static final int SCENE_TYPE = 1;
    
    @SerializedName("id")
    public long id;

    @SerializedName("title")
    public String title;

    @SerializedName("content")
    public String content;

    @SerializedName("start_time")
    public long startTime;

    @SerializedName("end_time")
    public long endTime;

    @SerializedName("scene_type")
    public int sceneType;

    @SerializedName("left_button")
    public PopupButton leftButton;

    @SerializedName("right_button")
    public PopupButton rightButton;
    
}
