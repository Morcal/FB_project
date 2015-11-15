package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class Brand {

    @SerializedName("provider")
    public String provider;

    @SerializedName("title")
    public String title;

    @SerializedName("id")
    public int id;

    @SerializedName("img")
    public Image img;

    @SerializedName("time")
    public long time;

    @SerializedName("discount")
    public String discount;
}
