package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class BaseComment {

    @SerializedName("id")
    public long id;

    @SerializedName("content")
    public String content;

    @SerializedName("order_sn")
    public String order_sn;
}
