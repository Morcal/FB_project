package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 推送消息
 * @author ml_bright
 * @version 2015-5-13  上午10:27:01
 */
public class Notification {

    /************************ 推送类型 START****************************/
    /** 版本更新 */
    public static final int VERSION_REMIND = 1;
    /** 好友消息 */
    public static final int FRIENDS_MESSGAE = 2;
    /** 系统通知 */
    public static final int SYSTEM_MESSAGE = 3;
    /** 搞笑达人推荐 */
    public static final int FUNNY_WEIBO_MASTAR = 4;
    /** 微博好友 */    
    public static final int FRIENDS_WEIBO = 5;
    /** 手机好友 */
    public static final int FRIENDS_PHONE = 6;
    /** 贵圈动态 */
    public static final int DYNANIC = 7;
    /************************ 推送类型 END****************************/
    
    /************************ 消息类型 START****************************/
    /** 关注(用户消息类型) */
    public static final int TYPE_USER_ATTENTION = 1;
    /** 评论与回复(用户消息类型) */
    public static final int TYPE_USER_COMMENT_OR_REPLEY = 2;
    /** 喜欢(用户消息类型) */
    public static final int TYPE_USER_LIKE = 3;

    /** 评论与回复(系统消息类型) */
    public static final int TYPE_SYSTEM_VIDEO = 1;
    /** 消息(系统消息类型) */
    public static final int TYPE_SYSTEM_MESSAGE = 2;
    /************************ 消息类型 END****************************/

    /**
     * 推送类型(1:版本更新 2:好友消息 3:系统通知 4:搞笑达人推荐 5:微博好友 6:手机好友 7:贵圈动态 )
     */
    @SerializedName("type")
    public int type;
    
    /** 右上角显示的徽章 */
    @SerializedName("badge")
    public int badge;
    
    /**
     * 评论、回复、喜欢某个视频时为视频的id，被关注时为对方用户id，
     * 系统通知类型为视频时为视频id，系统通知类型是消息时为消息id
     */
    @SerializedName("id")
    public int id;
    
    /** 
     * 消息类型，推送类型为用户消息时:关注(1)、评论与回复(2)、喜欢(3)， 推送类型为系统消息时消息时:视频(1)、消息(2)
     */
    @SerializedName("msg_type")
    public int messageType;
    
}
