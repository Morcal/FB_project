package com.feibo.joke.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/26.
 */
public class Video implements Serializable{
    
    /** 该视频不被当前用户喜欢*/
    public static final int VIDEO_UNLIKE = 0;
    /** 当前用户喜欢该视频 */
    public static final int VIDEO_LIKE = 1;  

    @SerializedName("id")
    public long id;

    @SerializedName("thumbnail")
    public Image thumbnail;

    @SerializedName("ori_image")
    public Image oriImage;

    @SerializedName("desc")
    public String desc;

    @SerializedName("url")
    public String url;

    @SerializedName("play_count")
    public int playCount;

    /** 视频是否被当前用户喜欢 */
    @SerializedName("be_like")
    public int beLike;
    
    @SerializedName("be_like_count")
    public int beLikeCount;

    @SerializedName("publish_time")
    public long publishTime;

    @SerializedName("author")
    public User author;

    @SerializedName("comments_count")
    public int commentsCount;
    
    @SerializedName("share_url")
    public String shareUrl;
    
}

