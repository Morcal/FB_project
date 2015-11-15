package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class Special extends BaseObject {
    
    @SerializedName("action")
    public Action action;
    
    public static class Action{
        @SerializedName("type")
        public int type; //1:跳转到专题详情； 2:跳转到专题列表；3：跳转到商品详情；4：跳转到商品列表；5: 跳转到指定html5；6；跳转到优惠券详情页， 7:跳转到优惠券列表
        
        @SerializedName("info")
        public String info;
    }
}
