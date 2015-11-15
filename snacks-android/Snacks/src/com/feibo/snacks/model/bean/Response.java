package com.feibo.snacks.model.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response<T> {
	@SerializedName("rs_code")
	public String code;
	@Expose
	@SerializedName("rs_msg")
	public String msg;
	@Expose
	@SerializedName("data")
	public T data;

	public Response(String rsCode) {
		this.code = rsCode;
	}

	public Response(String rsCode, String rsMsg) {
		this.code = rsCode;
		this.msg = rsMsg;
	}
}