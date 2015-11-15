package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseOrder implements Serializable{

    public static final int WAIT_PAY = 0;
    public static final int WAIT_SEND = 1;
    public static final int WAIT_GET = 2;
    public static final int WAIT_COMMENT = 3;
    public static final int TRADE_SUCCESS = 4;
    public static final int RETURN_GOODS = 5;
    public static final int RETURN_GOODS_CHECK = 5;
    public static final int RETURN_GOODS_DOING = 6;
    public static final int TRADE_FAIL = 7;

    @SerializedName("order_sn")
    public String id;
    
    @SerializedName("final_sum")
    public double finalSum;

    /**
     *  0 未付款（客户端显示：待付款）
     *	1 付款（客户端显示：待发货）
     *	2 已发货（客户端显示：待收货）
     *	3 交易成功（客户端显示：1、待评价）
     *  4 交易成功（客户端显示：1、交易成功）
     *	5 售后处理中(客户端显示：售后处理中)
     *	6 交易关闭 (客户端显示：交易关闭) 后台为用户退款后显示此状态
     *	7 结算完成（与供应商结算，客户端不显示）
     */
    @SerializedName("type")
    public int type;

    @SerializedName("poster_ids")
    public String posterIds;
}
