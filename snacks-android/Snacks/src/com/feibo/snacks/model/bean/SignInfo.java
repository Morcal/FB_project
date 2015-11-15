package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class SignInfo {
    
    @SerializedName("order_sn")
    public String orderId;

    /**
     * 支付类型：0，支付宝；1，微信；2，银联
     */
    @SerializedName("type")
    public int type;

    /**
     * 支付宝需要的签名后的支付字符串;或银联流水号；或微信json串
     */
    @SerializedName("pay_info")
    public String payInfo;

}
