package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class RedPointInfo {

    /**
     * 购物车数量
     */
    @SerializedName("cart_num")
    public int cartNum;

    /**
     * 订单未支付数量
     */
    @SerializedName("wpay_num")
    public int waitPayNum;

    @SerializedName("wsend_num")
    public int waitSendNum;

    @SerializedName("wreceive_num")
    public int waitReceiveNum;

    @SerializedName("wcomment_num")
    public int waitCommentNum;
}
