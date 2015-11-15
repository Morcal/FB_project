package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class Comment {

    @SerializedName("id")
    public long id;

    @SerializedName("author")
    public User author;

    @SerializedName("content")
    public String content;

    @SerializedName("publish_time")
    public long publishTime;

    @SerializedName("reply_id")
    public long replyId;

    @SerializedName("reply_author")
    public User replyAuthor;

}

