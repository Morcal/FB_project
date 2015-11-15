package com.feibo.snacks.model.bean;

public class AuthParameter {
	public String sig;
	public String time;
	public String wssig;
	
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWssig() {
		return wssig;
	}
	public void setWssig(String wssig) {
		this.wssig = wssig;
	}
}
