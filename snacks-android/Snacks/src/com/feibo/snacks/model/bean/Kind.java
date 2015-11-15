package com.feibo.snacks.model.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Kind {
    
    @SerializedName("title")
    public String title;
    
    @SerializedName("id")
    public int id;
    
    @SerializedName("kinds")
    public List<SubKind> kinds;
    
}
