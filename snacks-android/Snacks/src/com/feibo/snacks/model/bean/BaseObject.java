package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import com.feibo.snacks.model.bean.Image;

public class BaseObject {
    
    @SerializedName("title")
    public String title;
    
    @SerializedName("id")
    public int id;
    
    @SerializedName("desc")
    public String desc;
    
    @SerializedName("img")
    public Image img;
}
