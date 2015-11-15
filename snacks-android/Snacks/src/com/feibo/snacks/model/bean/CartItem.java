package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItem  implements Serializable {

    public static final int NORMAL = 0;
    public static final int FAILURE = 1;
    public static final int EMPTY = 2;
    public static final int OFF_SHELF = 3;
    public static final int PRICE_CHANGE = 4;

    @SerializedName("id")
    public int id; //条目ID

    @SerializedName("goods_id")
    public int goodsId;  //商品ID

    @SerializedName("goods_title")
    public String goodsTitle;  //商品名称

    /**类型：0，正常状态；1，失效状态;2,已售空;  3,已下架;  4,价格变动*/
    @SerializedName("state")
    public int state;

    @SerializedName("num")
    public int num;

    @SerializedName("surplus_num")
    public int surplusNum;

    @SerializedName("kinds")
    public String kinds;

    @SerializedName("price")
    public Price price;

    @SerializedName("img")
    public Image img;

    @SerializedName("order_sn")
    public String orderSn;

    public boolean isSelect = false;

    public String commentContent;

    public boolean isFouce = false;

}
