package com.feibo.snacks.manager;

public interface IRequestListener {

    void onStart();
    
    void onSuccess();
    
    void onFail(String failMsg);

}
