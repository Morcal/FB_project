package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 全局配置
 * 
 * @author ml_bright
 * @version 2015-6-10  上午10:53:13
 */
public class GlobalConfiguration {

    @SerializedName("hot_topic")
    public int isShowTopic;
    
    @SerializedName("popup")
    public Popup popup;
    
}
