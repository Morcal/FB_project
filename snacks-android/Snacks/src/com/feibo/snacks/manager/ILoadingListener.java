package com.feibo.snacks.manager;

public interface ILoadingListener {

    void onSuccess();

    void onFail(String failMsg);
}
