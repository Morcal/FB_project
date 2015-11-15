package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

public class ImgCodeImage {

    /**
     * 图片验证码的Base64编码字符串
     */
    @SerializedName("img_bytes")
    public String imgBytes;
}
