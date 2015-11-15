package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoodsDetail extends BaseObject {

    /**
     * //类型：0，非导购商品；1：导购淘宝商品;2：导购天猫商品。
     */
    @SerializedName("guide_type")
    public int guideType;

    /**
     * H5页面地址
     */
    @SerializedName("url")
    public String url;

    /**
     * //0，正常销售；1，失效；2，已下架
     */
    @SerializedName("status")
    public int status;

    /**
     * 0，未收藏；1.已收藏
     */
    @SerializedName("collect_status")
    public int collectStatus;

    @SerializedName("kinds")
    public List<Kind> kinds;

    @SerializedName("guide_info")
    public GuideInfo guideInfo;

    @SerializedName("active_info")
    public ActiviteInfo activiteInfo;

    public static class GuideInfo{

        /**
         * 0,百川；1，淘客链接
         */
        @SerializedName("open_type")
        public int openType;

        @SerializedName("real_url")
        public String realUrl;

        @SerializedName("real_id")
        public long realId;
    }
}
