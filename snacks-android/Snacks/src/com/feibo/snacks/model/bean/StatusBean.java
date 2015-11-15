package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayden on 2015/9/2.
 */
public class StatusBean {

    @SerializedName("status")
    public int status;  //0：领取成功 1：已领取, 2：优惠券被抢光了 3：发放时间结束了 4:优惠券还没开始发放
}
