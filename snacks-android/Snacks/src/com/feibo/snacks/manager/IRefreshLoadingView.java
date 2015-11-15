package com.feibo.snacks.manager;

public interface IRefreshLoadingView extends ILoadingView {

    void hideRefreshView();

    void reFillData(Object data);

}
