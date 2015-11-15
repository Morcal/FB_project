package com.feibo.snacks.model.bean;

import java.util.List;

/**
 * Created by Jayden on 2015/7/22.
 */
public class PayParams {

    private List<Integer> ids;

    private long addId;

    private int type = 0; //支付类型：0，支付宝；1，微信；3，银联

    private List<Note> notes;//json串格式

    private String orderSn;  //订单sn

    private long couponId;    //优惠券id

    private String posterIds;   //平台或供应商包邮id

    public void setPosterIds(String posterIds) {
        this.posterIds = posterIds;
    }

    public String getPosterIds() {
        return posterIds;
    }

    public PayParams() {

    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public long getAddId() {
        return addId;
    }

    public void setAddId(long addId) {
        this.addId = addId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public long getCouponId() {
        return couponId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
}
