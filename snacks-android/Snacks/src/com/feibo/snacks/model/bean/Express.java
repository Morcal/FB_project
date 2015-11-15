package com.feibo.snacks.model.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * User: LinMIWi(80383585@qq.com)
 * Time: 2015-07-28  10:15
 * FIXME
 */
public class Express{

    @SerializedName("name")
    public String name;

    @SerializedName("logo")
    public Image logo;

    @SerializedName("number")
    public String number;

    @SerializedName("detail")
    public List<Logistics> detail;
}