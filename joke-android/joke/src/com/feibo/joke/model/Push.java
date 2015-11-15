package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 推送结果
 * @author ml_bright
 * @version 2015-5-12  上午10:37:49
 */
public class Push {
    
    /** 新消息开关 */
    public static final int NOTICE_NEW_MESSGAE = 1;
    /** 喜欢开关 */
    public static final int NOTICE_LIKE = 2;
    /** 新粉丝开关 */
    public static final int NOTICE_FANS = 3;
    /** 系统开关 */
    public static final int NOTICE_NEW_SYSTEM_MESSAGE = 4;
    
    @SerializedName("alert")
    public String content;
    
    /** 桌面icon右上角显示的徽章 */
    @SerializedName("badge")
    public int badge; 
    
    @SerializedName("sound")
    public String sound; 
    
    @SerializedName("userInfo")
    public Notification notification; 

}
