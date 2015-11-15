package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class AboutInfo {

    /**
     * 零食小喵描述
     */
    @SerializedName("desc")
    public String desc;

    /**
     * 客服信息
     */
    @SerializedName("service")
    public String service;
}
