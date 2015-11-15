package com.feibo.snacks.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.loadingview.LoadingMoreView4List;
import com.feibo.snacks.view.base.LoadMoreScrollListener;

import java.util.List;

import fbcore.log.LogUtil;

public abstract class AbsLoadingPresenter {
    private static final String TAG = AbsLoadingPresenter.class.getSimpleName();

    private static final int NO_SHOW_CUE_COUNT = 2;

    private ILoadingView loadingView;
    private ILoadingListener iLoadingListener;
    private Handler handler = new Handler(Looper.getMainLooper());

    public enum LoadType {
        REFRESH, LOAD_MORE, LOAD_FIRST;
    }

    public AbsLoadingPresenter(ILoadingView loadingView) {
        this.loadingView = loadingView;
    }

    public void refreshLoad() {
        LogUtil.i(TAG, "refreshLoad");
        if (!(loadingView instanceof IRefreshLoadingView)) {
            return;
        }
        if (isLoading()) {
            return;
        }
        ILoadingListener listener = new ILoadingListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "success");
                if (loadingView == null) {
                    return;
                }
                ((IRefreshLoadingView) loadingView).hideRefreshView();
                ((IRefreshLoadingView) loadingView).reFillData(getData(getDataType()));
                AbsLoadingPresenter.this.iLoadingListener = null;
            }

            @Override
            public void onFail(String failMsg) {
                if (loadingView == null) {
                    return;
                }
                LogUtil.i(TAG, failMsg);
                handler.postDelayed(() -> {
                    ((IRefreshLoadingView) loadingView).hideRefreshView();
                }, 1000);
                loadingView.showToast(failMsg);
                loadingView.showFailView(failMsg);
                AbsLoadingPresenter.this.iLoadingListener = null;
            }
        };
        this.iLoadingListener = listener;
        generateLoad(LoadType.REFRESH, this.iLoadingListener);
    }

    public void loadData() {
        LogUtil.i(TAG, "loadData");
        if (isLoading()) {
            return;
        }
        if (loadingView == null) {
            return;
        }
        loadingView.showLoadingView();
        ILoadingListener listener = new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (loadingView == null) {
                    return;
                }
                loadingView.hideLoadingView();
                loadingView.fillData(getData(getDataType()));
                AbsLoadingPresenter.this.iLoadingListener = null;
            }

            @Override
            public void onFail(String failMsg) {
                if (loadingView == null) {
                    return;
                }
                loadingView.showToast(failMsg);
                loadingView.showFailView(failMsg);
                AbsLoadingPresenter.this.iLoadingListener = null;
            }
        };
        this.iLoadingListener = listener;
        generateLoad(LoadType.LOAD_FIRST, this.iLoadingListener);
    }

    public void loadMore(final LoadMoreScrollListener listener) {
        LogUtil.i(TAG, "loadMore");
        if (!(loadingView instanceof ILoadMoreView)) {
            return;
        }
        if (isLoading()) {
            return;
        }
        ((ILoadMoreView) loadingView).showLoadMoreView();
        ILoadingListener iLoadingListener = new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (loadingView == null) {
                    return;
                }
                ((ILoadMoreView) loadingView).fillMoreData(getData(getMoreDataType()));
                listener.endLoading();
                AbsLoadingPresenter.this.iLoadingListener = null;
            }

            @Override
            public void onFail(String failMsg) {
                if (loadingView == null) {
                    return;
                }
                handleShowFootView(failMsg, listener);

                loadingView.showToast(failMsg);
                listener.endLoading();
                AbsLoadingPresenter.this.iLoadingListener = null;
            }
        };
        this.iLoadingListener = iLoadingListener;
        generateLoad(LoadType.LOAD_MORE, this.iLoadingListener);
    }

    private void handleShowFootView(String failMsg, LoadMoreScrollListener listener) {
        ((ILoadMoreView) loadingView).hideLoadMoreView(listener, failMsg);
        if ((getData(getMoreDataType())) != null && getData(getMoreDataType()) instanceof List) {
            if (((List) getData(getMoreDataType())).size() <= NO_SHOW_CUE_COUNT) {
                ((LoadingMoreView4List) loadingView).hideLoadMoreNoFootview(listener, failMsg);
            }
        } else if ((getData(getMoreDataType())) != null && getData(getMoreDataType()) instanceof BrandDetail) {
            //备注：这边处理特殊情况， 数据类型不是list集合，而是具体的品牌团详情
            if (((BrandDetail) getData(getMoreDataType())).goodses.size() <= NO_SHOW_CUE_COUNT) {
                ((LoadingMoreView4List) loadingView).hideLoadMoreNoFootview(listener, failMsg);
            }
        }
        if ((getData(getMoreDataType())) == null) {
            ((LoadingMoreView4List) loadingView).hideLoadMoreNoFootview(listener, failMsg);
        }
    }

    public void hasNotNetwork(LoadMoreScrollListener listener) {
        ((ILoadMoreView) loadingView).hideLoadMoreView(listener, NetResult.NOT_NETWORK_STRING);
    }

    public Object getData(BaseDataType type) {
        return DataPool.getInstance().getData(type);
    }

    public synchronized boolean isLoading() {
        return iLoadingListener != null;
    }

    public abstract void generateLoad(LoadType type, ILoadingListener listener);

    public abstract boolean hasMore();

    public abstract BaseDataType getDataType();

    public abstract BaseDataType getMoreDataType();

}
