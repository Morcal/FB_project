package com.feibo.snacks.model.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable{

    public static final int TYPE_NORMAL = 0; // 非默认地址
    public static final int TYPE_DEFAULT = 1;   // 默认地址

    @SerializedName("id")
    public long id;
    
    @SerializedName("phone")
    public String phone;
    
    @SerializedName("name")
    public String name;

    /**
     * 省会
     */
    @SerializedName("province")
    public String province;

    /**
     * 城市
     */
    @SerializedName("city")
    public String city;

    /**
     * 区域
     */
    @SerializedName("proper")
    public String proper;

    /**
     * 完整地址
     */
    @SerializedName("full_add")
    public String street;
    
    @SerializedName("type")
    public int type; //0为非默认地址，1为默认地址

    private String fllAddress;

    public String getFullAddress(){
        if(TextUtils.isEmpty(fllAddress)){
            fllAddress = street ;
        }
        return fllAddress;
    }

    public void resetFullAddress(){
        fllAddress = province + city + proper + street ;
    }
}
