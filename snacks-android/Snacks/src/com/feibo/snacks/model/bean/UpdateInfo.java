package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class UpdateInfo {
    
    @SerializedName("title")
    public String title;
    
    @SerializedName("desc")
    public String desc;
    
    @SerializedName("upd_url")
    public String url;
}
