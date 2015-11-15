package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/26.
 */
public class UpdateInfo {

    @SerializedName("title")
    public String title;

    @SerializedName("desc")
    public String desc;

    @SerializedName("url")
    public String url;

}

