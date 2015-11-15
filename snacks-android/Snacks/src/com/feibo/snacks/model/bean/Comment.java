package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class Comment extends BaseComment {

    @SerializedName("nickname")
    public String nickname;
    
    @SerializedName("avatar")
    public Image avatar;
}
