package com.feibo.snacks.model.bean.oldversion14;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Tag implements Serializable{
    
    @SerializedName("disc")
    public String disc;
    
    @SerializedName("postage")
    public String postage;
    
    @SerializedName("other")
    public String other;
}
