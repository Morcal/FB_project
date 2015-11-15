package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class SplashImage {

    @SerializedName("start_time")
    public long startTime;

    @SerializedName("end_time")
    public long endTime;

    @SerializedName("image")
    public Image image;

    //本地路径
    public String imagePath;

}

