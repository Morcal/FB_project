package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrdersDetail extends BaseOrder {

    @SerializedName("cart_suppliers")
    public List<CartSuppliers> cartSuppliers;

    /**
     * 合计运费
     */
    @SerializedName("sum_freight")
    public double sendPay;

    @SerializedName("sum_goods")
    public double sumGoods; //合计费用，不包括运费和优惠券金额

    @SerializedName("discoupon_num")
    public int discouponNum;    //可使用优惠券数量

    @SerializedName("discount_amount")
    public double discountAmount;

    /**
     * 订单编号信息
     */
    @SerializedName("infos")
    public List<String> infos;  //包括订单编号、成交时间等，每个String含标题和值，如“成交时间：2015-06-07 20:28:01”

    @SerializedName("logistics")
    public Logistics logistics; //最新物流信息描述

    @SerializedName("validation_amount")
    public double validationAmount;    //优惠券金额

    @SerializedName("address")
    public Address address;

    @Override
    public String toString() {
        return "semdPay = " + sendPay + "infos =" + infos.toString() + "id = " + id;
    }
}
