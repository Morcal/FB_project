package com.feibo.snacks.model.bean.group;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import com.feibo.snacks.model.bean.Brand;
import com.feibo.snacks.model.bean.Classify;
import com.feibo.snacks.model.bean.Special;

public class HomePageHead {
    
    @SerializedName("topics")
    public List<Special> topics;
    
    @SerializedName("classifies")
    public List<Classify> classifies;
    
    @SerializedName("brands")
    public List<Brand> brands;
    
    @SerializedName("specials")
    public List<Special> specials;

    @SerializedName("brands_title_big")
    public String brandsTitleBig;

    @SerializedName("brands_title_sml")
    public String brandsTitleSml;

    @SerializedName("new_title_big")
    public String newTitleBig;

    @SerializedName("new_title_sml")
    public String newTitleSml;
}
