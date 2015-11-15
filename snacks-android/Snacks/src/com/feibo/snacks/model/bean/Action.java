package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jayden on 2015/8/25.
 */
public class Action  implements Serializable {

    /**
     * type 0:商品促销与特殊优惠页面：若是供应商满包邮活动，跳转到相应的供应商商品列表
     * |(2)购物车页面：商品id跳转商品促销和特殊优惠页面
     *
     * type 1	商品促销与特殊优惠页面：若满包邮是平台全场活动，跳转到特卖
     * |(2)购物车页面：供应商id跳转商品促销和特殊优惠页面
     *
     * type 0、1	        优惠券详情页：使用同商品促销与特殊优惠页面
     */
    @SerializedName("type") //0:列表(优惠券和满额包邮都用)，1：特卖（满额包邮），2：优惠券详情
    public int type;

    @SerializedName("info") //供应商id
    public String info;
}
