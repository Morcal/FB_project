package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

public class PushSetting {

    @SerializedName("user_message")
    public int newUserMessage;

    @SerializedName("belike")
    public int like;

    @SerializedName("new_follower")
    public int newFans;

    @SerializedName("system_message")
    public int newSystemMessage;
    
}
