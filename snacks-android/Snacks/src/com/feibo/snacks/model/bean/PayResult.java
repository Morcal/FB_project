package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class PayResult {

    /**
     * 0，不成功；1，成功
     */
    @SerializedName("type")
    public int type;

    /**
     * 实付款值
     */
    @SerializedName("real_pay")
    public double realPay;

    public boolean isSuccess() {
        return type == 1;
    }
}
