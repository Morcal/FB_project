package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class AuthDevice {

    @SerializedName("cid")
    private int cId; // 设备 ID

    @SerializedName("key")
    private String key; // 签名密钥

    private String uid = "0"; // 用户ID

    private String wskey = "0"; // 用户认证Key

    public int getCid() {
        return cId;
    }

    public void setCid(int cid) {
        this.cId = cid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWskey() {
        return wskey;
    }

    public void setWskey(String wskey) {
        this.wskey = wskey;
    }
}