package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class Subject extends BaseObject {

    @SerializedName("hotindex")
    public int hotindex;

    @SerializedName("share_num")
    public int shareNum;

    @SerializedName("web_url")
    public String webUrl;

    @SerializedName("collect_status")
    public int collectStatus;
}
