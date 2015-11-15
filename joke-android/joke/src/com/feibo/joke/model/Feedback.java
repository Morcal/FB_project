package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class Feedback {
    
    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;

    @SerializedName("id")
    public long id;

    @SerializedName("type")
    public int type;

    @SerializedName("author")
    public User author;

    @SerializedName("content")
    public String content;

    @SerializedName("publish_time")
    public long publishTime;

}

