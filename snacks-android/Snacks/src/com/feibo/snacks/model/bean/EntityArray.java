package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntityArray<E> {
	
	@SerializedName("count")
	public int count;
	
	@SerializedName("items")
	public List<E> items;
}
