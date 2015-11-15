package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Note implements Serializable {
    
    @SerializedName("id")
    public long id;

    @SerializedName("note")
    public String note;

}
