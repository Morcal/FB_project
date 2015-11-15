package com.feibo.joke.view.widget.waterpull;

import android.content.Context;
import android.util.AttributeSet;

import com.feibo.joke.R;
import com.feibo.joke.view.widget.waterpull.XListView.IXListViewListener;

public class CListView extends XListView implements IXListViewListener{

    private OnRefreshListener refreshListener;
    private OnLoadMoreListener loadMoreListener;
    
    private boolean hasSetListener = false;
    
    public CListView(Context context) {
        super(context);
        init(context, null, 0);
    }
    
    public CListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
    
    public CListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
    }
    
    public void setLoadMoreAble(boolean enable) {
        super.setPullLoadEnable(enable);
        if(enable && !hasSetListener) {
            hasSetListener = true;
            super.setXListViewListener(this);
        }
    }
    
    public void setRefreshAble(boolean enable) {
        super.setPullRefreshEnable(enable);
        if(enable && !hasSetListener) {
            hasSetListener = true;
            super.setXListViewListener(this);
        }
    }
    
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.refreshListener = listener;
    }
    
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public int getFooterLayout() {
        return R.layout.xlistview_footer;
    }

    @Override
    public int getHeaderView() {
        if(isCustomHeaderView) {
            return R.layout.header_userdetail;
        } else {
            return R.layout.xlistview_header;
        }
    }
    
    public interface OnRefreshListener{
        public void onRefresh();
    }
    
    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    @Override
    public void onListRefresh() {
        if(refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    @Override
    public void onListLoadMore() {
        if(loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }
    }
    
}
