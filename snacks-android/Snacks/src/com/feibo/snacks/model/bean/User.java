package com.feibo.snacks.model.bean;

import android.text.TextUtils;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.google.gson.annotations.SerializedName;

import fbcore.utils.Strings;

public class User {

    public static final String DEFAULT_USER_ID = "0";

    /** 登陆类型 */
    public static final int PLATFORM_TYPE_DEFAULT = 0;
    public static final int PLATFORM_TYPE_QQ = 1;
    public static final int PLATFORM_TYPE_WX = 2;
    public static final int PLATFORM_TYPE_WB = 3;

    /**
     * 该ID为应用对应的用户系统ID. 如冷笑话精选后台对应的ID. 如果飞博全平台唯一用户系统， 则指该平台. 不是各个第三方平台返回的openId.
     */
    @SerializedName("uid")
    private String uid = DEFAULT_USER_ID;// 0表示没有注册

    @SerializedName("wskey")
    private String wskey;

    @SerializedName("mobi_num")
    private String mobiNum;

    /** 用户名 */
    @SerializedName("nickname")
    private String nickname;

    /** 用户头像 */
    @SerializedName("avatar")
    private String avatar;

    /** 用户的登录平台 */
    private int platform;

    public boolean isNeedBind() {
        return TextUtils.isEmpty(mobiNum)
                || !mobiNum.matches(AppContext.getContext().getString(R.string.login_phone_rule));
    }

    public boolean isLogin() {
        return Strings.toInt(uid) > 0 && !TextUtils.isEmpty(mobiNum);
//        return true;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobiNum() {
        return mobiNum;
    }

    public void setMobiNum(String mobiNum) {
        this.mobiNum = mobiNum;
    }

    public String getWSKey() {
        return wskey;
    }

    public void setWSKey(String wsKey) {
        this.wskey = wsKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
