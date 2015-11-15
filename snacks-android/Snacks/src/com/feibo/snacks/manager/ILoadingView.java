package com.feibo.snacks.manager;

public interface ILoadingView {

    void showLoadingView();

    void hideLoadingView();

    void fillData(Object data);

    void showToast(String msg);

    void showFailView(String failMsg);
}
