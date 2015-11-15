package com.feibo.snacks.view.widget.operationview;

import android.widget.ListView;

import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshBase;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;


public abstract class RefreshListViewOperation extends ListViewOperation{

    private PullToRefreshListView pullToRefreshListView;

    public RefreshListViewOperation(PullToRefreshListView pullToRefreshListView, AbsLoadingPresenter presenter) {
        super(pullToRefreshListView.getRefreshableView(), presenter);
        this.pullToRefreshListView = pullToRefreshListView;
        initListener();
    }

    private void initListener() {
        pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (presenter == null) {
                    return;
                }
                if (presenter.isLoading()) {
                    return;
                }
                refreshListener();
                presenter.refreshLoad();
            }
        });
    }
}
