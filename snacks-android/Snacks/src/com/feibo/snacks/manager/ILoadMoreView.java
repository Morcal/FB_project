package com.feibo.snacks.manager;

import com.feibo.snacks.view.base.LoadMoreScrollListener;

public interface ILoadMoreView extends ILoadingView {

    void showLoadMoreView();

    void hideLoadMoreView(LoadMoreScrollListener listener, String failMsg);

    void fillMoreData(Object data);

    void loadMoreData(LoadMoreScrollListener listener);

    void setFootView();

}
