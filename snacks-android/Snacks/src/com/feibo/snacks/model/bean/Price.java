package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Price implements Serializable {

    /**现价*/
    @SerializedName("current")
    public double current;

    /**原价*/
    @SerializedName("prime")
    public double prime;
    
}
