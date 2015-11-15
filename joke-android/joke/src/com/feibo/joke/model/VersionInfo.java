package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class VersionInfo {

    @SerializedName("force")
    public int force;

    @SerializedName("title")
    public String title;

    @SerializedName("desc")
    public String desc;

    @SerializedName("url")
    public String url;

}

