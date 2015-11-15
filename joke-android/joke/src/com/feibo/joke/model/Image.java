package com.feibo.joke.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/26.
 */
public class Image implements Serializable{

    @SerializedName("url")
    public String url;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

}

