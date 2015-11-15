package com.feibo.snacks.model.bean.group;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import com.feibo.snacks.model.bean.Brand;
import com.feibo.snacks.model.bean.Goods;

public class BrandDetail {
    
    @SerializedName("brand")
    public Brand brand;
    
    @SerializedName("goodses")
    public List<Goods> goodses;
}
