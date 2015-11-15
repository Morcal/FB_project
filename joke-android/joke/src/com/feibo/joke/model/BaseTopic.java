package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 话题基础类
 * @author ml_bright
 * @version 2015-6-11  上午11:56:24
 */
public class BaseTopic implements Serializable {

    @SerializedName("id")
    public long id;

    @SerializedName("title")
    public String title;
}
