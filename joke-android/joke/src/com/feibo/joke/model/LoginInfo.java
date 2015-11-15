package com.feibo.joke.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import com.feibo.joke.utils.SPHelper;

/**
 *
 * @author kcode(ml_bright)
 * @version 2015/03/26.
 */
public class LoginInfo extends User{

    /** 新浪 */
    public static final int BANDING_SINA = 1;
    /** QQ */
    public static final int BANDING_QQ = 2;
    /** 微信 */
    public static final int BANDING_WEIXIN = 4;
    /** 手机 */
    public static final int BANDING_PHONE = 8;
    
    /** 已注册用户 */
    public static final int HAS_REGISTER = 0;  
    /** 新注册用户 */
    public static final int NEW_REGISTER = 1;  
    
    @SerializedName("is_new")
    /** 是否是新注册用户 */
    public int isNewUser; 

    @SerializedName("wskey")
    public String wskey;

    /** 是否绑定QQ(0为未绑定，1为绑定) */
    @SerializedName("band_relationship")
    public int bandRelationship;
    
    /** 判断是否昵称跟后台昵称起冲突，导致昵称被后台修改的标记 */
    @SerializedName("nickname_repeat")
    public int nicknameRepeat;
    
    /**
     * 获取是否绑定平台
     * 
     * @param platformType 代表 1:微博 2:QQ 3:微信 4:手机通讯录
     */
    public static boolean getBandingRelationship(Context context, int platformType) {
        int ship = SPHelper.getBanding(context);
        if(ship == 0) {
            return false;
        }
        return (ship & platformType) == platformType;
    }
    
    /**
     * 解绑平台
     * 
     * @param platformType 代表 1:微博 2:QQ 3:微信 4:手机通讯录
     */
    public static void unBandingRelationship(Context context, int platformType) {
        int ship = SPHelper.getBanding(context);
        if(ship == 0) {
            return;
        }
        int newShip = ship & platformType;
        SPHelper.saveBanding(context, newShip);
    }

}

