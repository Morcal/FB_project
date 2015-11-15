package com.feibo.joke.view.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshLoadmoreListView;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase.OnRefreshListener;

/**
 * 下拉刷新基础部分
 *
 * @author ml_bright
 * @version 2015-4-3 下午1:03:08
 */
public class BasePullListGroup<T> extends AbsListGroup<T> {

    protected PullToRefreshLoadmoreListView pullToRefreshListView;
    private boolean needPullRefresh;
    private boolean needLoadMore;
    
    private OnItemClickListener onItemClickListener;
    
    public BasePullListGroup(Context context){
        this(context,true,true);
    }

    public BasePullListGroup(Context context, boolean needPullRefresh, boolean needLoadMore) {
        super(context);
        this.needPullRefresh = needPullRefresh;
        this.needLoadMore = needLoadMore;
    }

    public BasePullListGroup(Context context, boolean needLoadDataOnCreated, boolean needPullRefresh, boolean needLoadMore) {
        super(context, needLoadDataOnCreated);
        this.needPullRefresh = needPullRefresh;
        this.needLoadMore = needLoadMore;
    }

    @Override
        public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.main_pull_listview, null);
        return parent;
    }

    @Override
    public void initWidget() {
        pullToRefreshListView = (PullToRefreshLoadmoreListView) getRoot().findViewById(R.id.list);
        pullToRefreshListView.setRefreshLoadMoreEnable(needPullRefresh, needLoadMore);
        pullToRefreshListView.setFooterLoadMoreOverText(getFooterLoadMoreOverText());
        pullToRefreshListView.initView(initHeaderView(), initFooterView());
        pullToRefreshListView.setAdapter(getListAdapter());
    }
    
    @Override
    public void initListener() {
        if (needPullRefresh) {
            pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    pullToRefreshListView.setLoadMoreStatus();
                    refreshData();
                }
            });
        }
        if (needLoadMore) {
            pullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    if(!pullToRefreshListView.isNoMoreDataStatus()) {
                        pullToRefreshListView.setLoadMoreSatu(PullToRefreshLoadmoreListView.STATU_LOAD_MORE_START);
                        loadMoreData();
                    }
                }
            });
        }
        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position - 1 < 0) {
                    return;
                }
                if(getListAdapter().getCount() + 1 <= position) {
                    return;
                }
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position - 1, id);
                } 
            }
        });
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void hideRefreshLoading() {
        if (pullToRefreshListView.isRefreshing()) {
            pullToRefreshListView.onRefreshComplete();
        }
    }
    
    @Override
    public void onScollToTop(boolean refresh) {
        if(getListAdapter() == null || getListAdapter().getCount() == 0) {
            return;
        }
        pullToRefreshListView.getListView().setSelection(0);
        if(refresh){
            this.pullToRefreshListView.setRefreshing();
        }
    }

    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {
        pullToRefreshListView.setVisibility(loadingHelpVisible ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void hideLoadMoreLoading(boolean loadMoreSuccess) {
        if (loadMoreSuccess) {
            pullToRefreshListView.setLoadMoreSatu(PullToRefreshLoadmoreListView.STATU_LOAD_MORE_NO_DATA);
        } else {
            pullToRefreshListView.setLoadMoreSatu(PullToRefreshLoadmoreListView.STATU_LOAD_MORE_SUCCESS);
        }
    }

    @Override
    public void setListAdapter(BaseSingleTypeAdapter<T> adapter) {
        super.setListAdapter(adapter);
    }

    public void setBackgroundColor(int color){
        pullToRefreshListView.setBackgroundColor(color);
    }

    public View initHeaderView() {
        return null;
    }

    public View initFooterView() {
        return null;
    }
}
