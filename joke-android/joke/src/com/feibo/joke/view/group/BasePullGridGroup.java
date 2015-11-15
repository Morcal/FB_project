package com.feibo.joke.view.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.feibo.joke.R;
import com.feibo.joke.model.Topic;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshGridView;

/**
 * 话题列表组件
 * @author ml_bright
 * @version 2015-4-2  下午2:08:03
 */
public class BasePullGridGroup extends AbsListGroup<Topic> {

    private PullToRefreshGridView mPullGridView;
    private GridView gridView;
    
    private OnItemClickListener listener;
    
    public BasePullGridGroup(Context context) {
        super(context);
    }

    @Override
    public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.main_topics, null);
        return parent;
    }

    @Override
    public void initWidget() {
        mPullGridView = (PullToRefreshGridView) getRoot().findViewById(R.id.list);
        gridView = mPullGridView.getRefreshableView();
        gridView.setAdapter(getListAdapter());
    }

    @Override
    public void initListener() {
        mPullGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                refreshData();
            }
        });
        mPullGridView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                
            }
        });
        
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener != null) {
                    listener.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {
        mPullGridView.setVisibility(loadingHelpVisible ? View.INVISIBLE : View.VISIBLE);
    }
    
    @Override
    public void hideRefreshLoading() {
        if (mPullGridView.isRefreshing()) {
            mPullGridView.onRefreshComplete();
        }
    }

    @Override
    public void hideLoadMoreLoading(boolean loadMoreSuccess) {
        
    }
    
    public void setOnItemClickListerner(OnItemClickListener listener) {
        this.listener = listener;
    }

}
