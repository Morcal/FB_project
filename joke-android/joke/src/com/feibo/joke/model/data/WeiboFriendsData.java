package com.feibo.joke.model.data;


import com.google.gson.annotations.SerializedName;

import com.feibo.joke.model.User;

public class WeiboFriendsData {

    @SerializedName("friends")
    public BaseListData<User> friends;
    
    @SerializedName("invitation")
    public BaseListData<User> invitations;
    
}
