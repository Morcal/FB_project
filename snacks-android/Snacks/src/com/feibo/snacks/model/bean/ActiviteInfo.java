package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jayden on 2015/10/24.
 */
public class ActiviteInfo implements Serializable {
    public static int GONE = 0;
    public static int VISIBLE = 1;
    /**
     * 拍下立减的价格
     */
    @SerializedName("real_price")
    public String price;

    /**
     * 截止时间
     */
    @SerializedName("end_time")
    public long endTime;

    /**
     * 0:不用显示
     * 1：显示
     */
    @SerializedName("status")
    public int status;
}