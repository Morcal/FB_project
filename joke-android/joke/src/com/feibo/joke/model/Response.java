package com.feibo.joke.model;

import com.feibo.joke.dao.ReturnCode;
import com.google.gson.annotations.SerializedName;

public class Response<T>{
    
    @SerializedName("rs_code")
    public int rsCode;
    
    @SerializedName("rs_msg")
    public String rsMsg;
    
    @SerializedName("data")
    public T data;
    
    public Response(int rsCode) {
        this.rsCode = rsCode;
    }
    
    public Response(int rsCode, String rsMsg) {
        this.rsCode = rsCode;
        this.rsMsg = rsMsg;
    }

    public boolean isSuccess() {
        return rsCode == ReturnCode.RS_SUCCESS;
    }
    
}
