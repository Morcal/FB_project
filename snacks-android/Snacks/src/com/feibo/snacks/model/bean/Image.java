package com.feibo.snacks.model.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 图片格式
 * @author fuJun
 *
 */
public class Image implements Serializable{

	@SerializedName("img_url")
	public String imgUrl;

	@SerializedName("img_w")
	public int imgWidth;

	@SerializedName("img_h")
	public int imgHeight;

}
