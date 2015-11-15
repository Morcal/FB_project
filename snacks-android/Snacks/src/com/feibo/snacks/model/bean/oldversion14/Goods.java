package com.feibo.snacks.model.bean.oldversion14;

import com.feibo.snacks.model.bean.Image;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 商品信息
 * @author fuJun
 *
 */
public class Goods implements Serializable{

	public static final int NOTHING = 1;
	public static final int HOT_SELL = 2;
	public static final int REST_TIME = 3;
	public static final int LIMITED = 4;

	@SerializedName("id")
	public int id;//商品id

	/**
	 * 品类名称
	 */
	@SerializedName("category_id")
	public int categoryId;

	@SerializedName("name")
	public String name;

	@SerializedName("title")
	public String title;//商品标题

	@SerializedName("img")
	public Image img;//商品的图标

	@SerializedName("desc")
	public String desc;//简介

	@SerializedName("list_desc")
	public String list_desc;
	/**
	 * 1无、2热卖、3剩余时间
	 */
	@SerializedName("tag")
	public int tag;//商品类型

	@SerializedName("buy_url")
	public String buyUrl;//商品购买地址

	@SerializedName("cur_price")
	public double curPrice;//原价

	@SerializedName("ori_price")
	public double originalPrice;//原价

	@SerializedName("time")
	public int restTime;//剩余时间
	
	@SerializedName("end_time")
	public long endTime; //截止时间

	@SerializedName("discount")
	public String discount;//商品折扣

	@SerializedName("imgs")
	public List<Image> images;//商品详情图片数组
	
	@SerializedName("tag_name")
	public String tagName;
	
	@SerializedName("posters")
	public List<Image> posters;
	
	@SerializedName("tags")
	public Tag tags;
	
	@SerializedName("item_id")
	public long itemId;
	
	@SerializedName("item_type")
	public int itemType;//1：淘宝商品;2：天猫商品

}
