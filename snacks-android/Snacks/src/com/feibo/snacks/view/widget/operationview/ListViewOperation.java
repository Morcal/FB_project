package com.feibo.snacks.view.widget.operationview;

import android.widget.ListView;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.view.base.LoadMoreScrollListener;

public abstract class ListViewOperation {

    private ListView listView;
    protected AbsLoadingPresenter presenter;
    private LoadMoreScrollListener loadMoreScrollListener;

    public ListViewOperation(ListView listView, AbsLoadingPresenter presenter) {
        this.listView = listView;
        this.presenter = presenter;
        initListener();
    }

    public void initListData() {
        if (presenter == null) {
            return;
        }
        presenter.loadData();
    }

    public void refreshListView() {
        if (presenter == null) {
            return;
        }
        presenter.refreshLoad();
    }

    public void refreshListener() {
        loadMoreScrollListener.endLoading();
    }

    private void initListener() {
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void loadMore(LoadMoreScrollListener listener) {
                if (presenter == null) {
                    return;
                }
                if (!AppContext.isNetworkAvailable()) {
                    listener.endLoading();
                    presenter.hasNotNetwork(listener);
                    return;
                }
                if (!presenter.hasMore()) {
                    listener.endLoading();
                    return;
                }
                if (presenter.isLoading()) {
                    listener.endLoading();
                    return;
                }
                presenter.loadMore(listener);
            }

            @Override
            public void firstVisibleItem(int position) {
                operationItemAtPosition(position);
            }

            @Override
            public boolean canLoadMore() {
                return listView.getAdapter() != null;
            }
        };
        listView.setOnScrollListener(loadMoreScrollListener);
    }

    public abstract void operationItemAtPosition(int position);
}
