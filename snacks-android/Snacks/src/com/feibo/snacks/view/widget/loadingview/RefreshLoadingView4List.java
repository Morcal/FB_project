package com.feibo.snacks.view.widget.loadingview;

import android.view.View;

import com.feibo.snacks.manager.IRefreshLoadingView;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;

import fbcore.log.LogUtil;

/**
 * @author hcy
 */
public abstract class RefreshLoadingView4List extends LoadingMoreView4List implements IRefreshLoadingView {
    private static final String TAG = RefreshLoadingView4List.class.getSimpleName();
    private PullToRefreshListView pullToRefreshListView;

    public RefreshLoadingView4List(PullToRefreshListView pullToRefreshListView) {
        super(pullToRefreshListView.getRefreshableView());
        this.pullToRefreshListView = pullToRefreshListView;
    }

    @Override
    public void showLoadingView() {
        super.showLoadingView();
        pullToRefreshListView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadingView() {
        super.hideLoadingView();
        pullToRefreshListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefreshView() {
        LogUtil.i(TAG, "onRefreshComplete");
        pullToRefreshListView.onRefreshComplete();
    }
}
