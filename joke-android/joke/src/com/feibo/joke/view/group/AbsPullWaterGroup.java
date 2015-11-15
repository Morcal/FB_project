package com.feibo.joke.view.group;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.view.widget.waterpull.CListView;
import com.feibo.joke.view.widget.waterpull.CListView.OnLoadMoreListener;
import com.feibo.joke.view.widget.waterpull.CListView.OnRefreshListener;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView.OnItemClickListener;

/**
 * 视频瀑布流组件
 *
 * @author ml_bright
 * @version 2015-4-1 下午1:02:41
 */
public abstract class AbsPullWaterGroup<T> extends AbsListGroup<T> {

    private CListView listview;
    private boolean refreshEnable;
    private boolean loadmoreEnable;
    private boolean isDetail;
    
    private OnItemClickListener onItemClickListener;

    public AbsPullWaterGroup(Context context) {
        super(context);
        this.refreshEnable = true;
        this.loadmoreEnable = true;
        this.isDetail = false;
    }

    public AbsPullWaterGroup(Context context, boolean isDetail) {
        this(context);
        this.isDetail = isDetail;
    }

    public AbsPullWaterGroup(Context context, boolean isDetail, boolean refreshEnable, boolean loadmoreEnable) {
        this(context, isDetail);
        this.refreshEnable = refreshEnable;
        this.loadmoreEnable = loadmoreEnable;
    }

    @Override
    public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        int layout = isDetail ? R.layout.main_waterpull_userdetail : R.layout.main_waterpull;
        return (ViewGroup) inflater.inflate(layout, null);
    }

    @Override
    public void initWidget() {
        listview = (CListView) getRoot().findViewById(R.id.list);
        listview.setFooterLoadMoreOverText(getFooterLoadMoreOverText());
        listview.setRefreshAble(refreshEnable);
        listview.setLoadMoreAble(loadmoreEnable);
        listview.setAdapter(getListAdapter());
    }

    @Override
    public void onScollToTop(boolean refresh) {
        if(getListAdapter() == null || getListAdapter().getCount() == 0) {
            return;
        }
        if (!listview.isStackFromBottom()) {//用来回滚到顶部
            listview.setStackFromBottom(true);
            listview.setStackFromBottom(false);
        }
        if(refresh){
            listview.showRefresh();
        }
    }

    @Override
    public void initListener() {
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
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
        listview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        listview.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
    }
    
    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        if(code == DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE) {
            Bundle bundle = getFinishBundle();
            if(bundle == null) {
                return;
            }
            @SuppressWarnings("unchecked")
            T video = (T) bundle.getSerializable(BundleUtil.KEY_VIDEO);
            int positionId = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
            if(positionId == -1) {
                return;
            }
            localRefreshData(positionId, video);
        } else if(code == DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE) {
            refreshData();
        }
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void hideRefreshLoading() {
        if (listview.isRefreshing()) {
            listview.stopRefresh();
        }
    }

    @Override
    public void hideLoadMoreLoading(boolean loadMoreSuccess) {
        listview.stopLoadMore(loadMoreSuccess);
    }

    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {
        listview.setVisibility(loadingHelpVisible ? View.INVISIBLE : View.VISIBLE);
    }

    public CListView getListView() {
        return listview;
    }


}
