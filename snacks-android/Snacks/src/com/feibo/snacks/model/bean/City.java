package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayden on 2015/8/25.
 */
public class City extends BasePlace {

    @SerializedName("proper")
    public List<Proper> cityList;
}
