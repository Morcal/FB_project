package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Goods extends BaseObject {
    
    /**0，普通商品；1，特卖商品(显示剩余)；2，特卖商品(不显示剩余)*/
    @SerializedName("type")
    public int type;
    
    @SerializedName("guide_type")
    public int guideType;//类型：0，普通商品；1：淘宝商品;2：天猫商品。
    
    @SerializedName("status")
    public int status;//0，正常销售；1，失效；2，已下架
    
    @SerializedName("sold_num")
    public int soldNum;
    
    @SerializedName("surplus_num")
    public int surplusNum;

    @SerializedName("fav_num")
    public int favNum;

    @SerializedName("freight")
    public double freight;
    
    @SerializedName("price")
    public Price price;
    
    @SerializedName("tag")
    public Tag tag;
    
    @SerializedName("time")
    public long time;
    
    @SerializedName("posters")
    public List<Image> posters;

    /**
     * 品类名称
     */
    @SerializedName("sub_classify_id")
    public int categoryId;
    
}
