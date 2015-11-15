package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jayden on 2015/8/25.
 */
public class Activity  implements Serializable {

    @SerializedName("icon_title")
    public String iconTitle;        //如：满包邮，满减，满赠等

    @SerializedName("title")
    public String title;        //如：满包邮，满减等的描述

    @SerializedName("action")
    public Action action;
}
