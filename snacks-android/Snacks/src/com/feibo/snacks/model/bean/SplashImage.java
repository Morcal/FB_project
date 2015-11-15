package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class SplashImage {
    
    @SerializedName("start_time")
    public int startTime;
    
    @SerializedName("end_time")
    public int endTime;
    
    @SerializedName("img")
    public Image img;
}
