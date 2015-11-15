package com.feibo.joke.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class DiscoveryTopicItem {

    @SerializedName("topic")
    public Topic topic;

    @SerializedName("videos")
    public List<Video> videos;

}

