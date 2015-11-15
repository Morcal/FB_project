package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemOrder extends BaseOrder{
    
    @SerializedName("name")
    public String name;
    
    @SerializedName("num")
    public int num;

    @SerializedName("freight")
    public double freight;

    @SerializedName("single")
    public CartItem single;

    @SerializedName("multi")
    public List<Image> multi;

}
