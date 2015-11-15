package com.feibo.joke.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class UserDetail implements Serializable {

    public static final int MAN = 1;
    
    public static final int WOMAN = 0;
    
    /** 位置性别 */
    public static final int UN_KNOW = 2;

    @SerializedName("signature")
    public String signature;

    @SerializedName("followers_count")
    public int followersCount;

    @SerializedName("works_count")
    public int worksCount;

    @SerializedName("friends_count")
    public int friendsCount;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("be_like_count")
    public int beLikeCount;

    @SerializedName("gender")
    public int gender;

    @SerializedName("birth")
    public String birth;

    @SerializedName("province")
    public String province;

    @SerializedName("city")
    public String city;

    @SerializedName("share_url")
    public String shareUrl;
    
    @SerializedName("download_url")
    public String downloadUrl;
    

}

