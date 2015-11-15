package com.feibo.joke.manager;

public interface LoadListener {
    void onSuccess();

    void onFail(int code);
}