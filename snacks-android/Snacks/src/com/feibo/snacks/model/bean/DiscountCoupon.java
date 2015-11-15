package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayden on 2015/8/24.
 */
public class DiscountCoupon {

    @SerializedName("id")
    public long id;

    @SerializedName("title")
    public String title;    //优惠券用到

    @SerializedName("is_valid")
    public int validate;        //优惠券是否可用,1:可用，  0：不可用

    @SerializedName("status")
    public int status;   //0：领取成功 1：已领取, 2：优惠券被抢光了 3：发放时间结束了 4: 优惠券还没开始发放

    @SerializedName("start_time")
    public long startTime;  //优惠券可用起始时间

    @SerializedName("end_time")
    public long endTime;    //优惠券可用截止时间

    @SerializedName("validation_amount")
    public float value; //优惠券金额

    @SerializedName("using_range")
    public String fillValue;    //如：满159可用，考虑后期可能不是满多少可用，所以直接返回字符串

    @SerializedName("msg")
    public String msg;//（1）优惠券的使用说明：如指定商家可用，全平台使用等（使用优惠券的时候用到）,（2）我的优惠券部分：改字段为“指定商家可用”、全平台使用等描述（详细见需求文档）

    @SerializedName("invalid_desc")
    public String invalidateDesc;   //不可用的描述。使用优惠券部分用到，如：所结算商品中，没有符合该活动的商品
}
