package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartSuppliers extends Note{

    @SerializedName("num")
    public long num;        //小计数量
    
    @SerializedName("name")
    public String name;     //供货商名称
    
    @SerializedName("freight")
    public double freight;  //供货商合计运费，可选

    @SerializedName("sum_price")
    public double sumPrice; //小计价格

    @SerializedName("items")
    public List<CartItem> items;    //订单条目列表，有效的商品

    @SerializedName("activity")
    public Activity activity;   //优惠券，（**满包邮**）使用
}
