package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 获取到的搜索引导词
 * @author fuJun
 */
public class SearchGuide {

	@SerializedName("guide_word")
	public String guideWord;//搜索引导词
	
	public SearchGuide(String guideWord) {
	    this.guideWord = guideWord;
	}
}
