package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class Message {
    
    /** 关注 */
    public static final int TYPE_ATTENTION = 1;
    /** 评论 */
    public static final int TYPE_COMMENT = 2;
    /** 喜欢 */
    public static final int TYPE_LIKE = 3;

    @SerializedName("id")
    public long id;
    
    @SerializedName("type")
    public int type;

    @SerializedName("content")
    public String content;
    
    @SerializedName("publish_time")
    public long publishTime;

    @SerializedName("user")
    public User user;

    @SerializedName("video")
    public Video video;
    
    @SerializedName("comment")
    public Comment comment;

}

