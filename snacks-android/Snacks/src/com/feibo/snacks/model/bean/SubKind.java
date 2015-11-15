package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class SubKind {
    
    @SerializedName("title")
    public String title;
    
    @SerializedName("id")
    public int id;
    
    @SerializedName("price")
    public Price price;
    
    @SerializedName("surplus_num")
    public int surplusNum;
}
