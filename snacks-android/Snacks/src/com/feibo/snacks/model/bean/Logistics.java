package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Logistics  implements Serializable {
    
    @SerializedName("desc")
    public String desc;
    
    @SerializedName("time")
    public String time;
    
}
