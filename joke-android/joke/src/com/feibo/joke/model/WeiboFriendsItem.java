package com.feibo.joke.model;

public class WeiboFriendsItem {

    public static final int TYPE_DIVISION_HEADER = 0;
    public static final int TYPE_CAN_ADD = 1;
    public static final int TYPE_DIVISION_CENTER = 2;
    public static final int TYPE_CAN_INVITA = 3;
    
    public int type;
    
    public User user;
    
    public WeiboFriendsItem(int type, User user) {
        this.type = type;
        this.user = user;
    }
    
}