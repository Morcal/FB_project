package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayden on 2015/8/25.
 */
public class DiscountCouponDetail extends DiscountCoupon{

    @SerializedName("desc")
    public String desc; //优惠券的活动规则

    @SerializedName("remaining_count")
    public int remainingCount;  //优惠券剩余数量

    @SerializedName("total_count")
    public int totalCount;  //优惠券总数

    @SerializedName("action")
    public Action action;
}
