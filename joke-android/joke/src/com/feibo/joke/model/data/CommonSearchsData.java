package com.feibo.joke.model.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;

public class CommonSearchsData {
    
    @SerializedName("users")
    public List<User> users;
    
    @SerializedName("videos")
    public List<Video> videos;
}
