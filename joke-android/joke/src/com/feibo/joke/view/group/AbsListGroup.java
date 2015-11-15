package com.feibo.joke.view.group;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.IListManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.BaseActivity;

/**
 * 基于ListView或者GridView组件基础模块
 * 
 * @author ml_bright
 * @version 2015-4-1 上午9:36:26
 */
public abstract class AbsListGroup<E> extends AbsLoadingGroup {

    private IListManager<E> mManager;
    private BaseSingleTypeAdapter<E> mAdapter;
    
    private ViewGroup preparedView;
    private Context context;
    public boolean needRefresh = false;
    private boolean needLoadDataOnCreated = true;

    private View afterView;
    
    private OnRefreshDataListener onRefreshDataListener;
    
    private static final String DEFAULT_LOAD_MORE_OVER_TEXT = "没有更多了";
    private String loadMoreOverText;

    public AbsListGroup(Context context) {
        this.context = context;
        this.loadMoreOverText = DEFAULT_LOAD_MORE_OVER_TEXT;
    }

    public AbsListGroup(Context context, boolean needLoadDataOnCreated) {
        this.context = context;
        this.loadMoreOverText = DEFAULT_LOAD_MORE_OVER_TEXT;
        this.needLoadDataOnCreated = needLoadDataOnCreated;
    }

    @Override
    public void onLoadingHelperFailClick() {
        needRefresh = true;
        initData();
    }

    @Override
    public void onCreateView() {
        onCreateView(false);
    }
    
    private void onCreateView(boolean flesh) {
        if(flesh) {
            setRoot(null);
        }
        if(getRoot() == null) { 
            preparedView = onPrepareView();
            if(preparedView == null) {
                setRoot(containChildView());
                initWidget();
                initListener();
                initView();
                if (needLoadDataOnCreated) {
                    initData();
                }
            } else {
                setRoot(preparedView);
            }
        }
    }
    
    @Override
    public void onResetView() {
        onCreateView(true);
    }

    private void initView() {
        if (mManager == null || !mManager.hasData()) {
            needRefresh = true;
            return;
        }
    }

    public void initData() {
        if (!needRefresh) {
            return;
        }
        launchLoadHelper();
        getPreparedDataIfRefreshData();
        refreshData();
    }

    /** 获取列表数据前的准备数据,比如个人主页中。要先获取完头部数据才获取下面的列表数据 */
    public boolean getPreparedDataIfRefreshData() {
        return false;
    }
    
    public void showView(final boolean isRefresh) {
        mAdapter.setItems(mManager.getDatas());
        mAdapter.notifyDataSetChanged();

        if (mAdapter != null && mAdapter.getCount() > 0) {
            hideLoadHelper();
        }

        if(onRefreshDataListener != null) {
            onRefreshDataListener.onFinish(isRefresh, true);
        }
    }
    
    public void localRefreshData(int positionId, E t) {
        mManager.refreshLocal(positionId, t);
        mAdapter.notifyDataSetChanged();
        if(!mManager.hasData()) {
            refreshDataFail(ReturnCode.RS_EMPTY_ERROR);
        }
    }

    public void refreshData() {
        if(onRefreshDataListener != null) {
            onRefreshDataListener.onStart(true);
        }
        mManager.refresh(new LoadListener() {
            @Override
            public void onSuccess() {
                hideRefreshLoading();
                showView(true);
            }

            @Override
            public void onFail(int code) {
                hideRefreshLoading();
                
                if(onErrorConsumption(code)) {
                    return;
                }
                if (code == ReturnCode.RS_EMPTY_ERROR || code == ReturnCode.NO_NET) {
                    refreshDataFail(code);
                }
                if(onRefreshDataListener != null) {
                    onRefreshDataListener.onFinish(true, false);
                }
            }
        });
    }
    
    /** 子类复写消费掉onFail() */
    public boolean onErrorConsumption(int code) {
        return false;
    }
    
    private void refreshDataFail(int code) {
        if(!isShowLoading()) {
            launchLoadHelper();
        }
        if(code == ReturnCode.NO_NET) {
            showFailMessage(code);
        } else {
            if(code == ReturnCode.RS_EMPTY_ERROR) {
                afterView = onAfterView();
                if(afterView != null) {
                    showEmptyView();
                } else {
                    showFailMessage(code);
                }
            } else {
                showFailMessage(code);
            }
        }
    }
    
    private void showEmptyView() {
        if(afterView == null) {
            afterView = onAfterView();
        }
        getLoadingRootView().removeAllViews();
        getLoadingRootView().addView(afterView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
    public void loadMoreData() {
        if(onRefreshDataListener != null) {
            onRefreshDataListener.onStart(false);
        }
        mManager.loadMore(new LoadListener() {
            @Override
            public void onSuccess() {
                hideLoadMoreLoading(false);
                showView(false);
            }

            @Override
            public void onFail(int code) {
                if (code == ReturnCode.RS_LOCAL_NO_MORE_DATA || code == ReturnCode.RS_EMPTY_ERROR) {
                    // 加载成功，没有更多数据
                    hideLoadMoreLoading(true);
                    return;
                }
                // 其他错误
                hideLoadMoreLoading(false);
                
                if(onRefreshDataListener != null) {
                    onRefreshDataListener.onFinish(false, false);
                }
            }
        });
    }
    
    // 准备View。比如在动态页当中要先判断是否登陆
    public ViewGroup onPrepareView() {
        return null;
    }
    
    //加载失败后不想直接显示Loading页，想显示部分信息
    public ViewGroup onAfterView() {
        return null;
    }

    public void setListManager(IListManager<E> listManager){
        mManager = listManager;
    }

    public void setListAdapter(BaseSingleTypeAdapter<E> adapter){
        mAdapter = adapter;
    }

    public BaseSingleTypeAdapter<E> getListAdapter(){
        return mAdapter;
    }
    
    protected Context getContext(){
        return context;
    }
    protected IListManager<E> getListManager(){
        return mManager;
    }
    
    public void setOnRefreshDataListener(OnRefreshDataListener onRefreshDataListener) {
        this.onRefreshDataListener = onRefreshDataListener;
    }
    
    public Bundle getFinishBundle() {
        if(getContext() == null) {
            return null;
        }
        Bundle bundle = ((BaseActivity) getContext()).getFinishBundle();
        return bundle;
    }
    
    public void setFinishBundle(Bundle bundle) {
        if(getContext() == null) {
            return;
        }
        ((BaseActivity)getContext()).setFinishBundle(bundle);
    }

    public void setChangeTypeAndFinish(int changeType) {
        if(getContext() == null) {
            return;
        }
        ((BaseActivity)getContext()).setChangeTypeAndFinish(changeType);
    }
    
    public void setChangeType(int changeType) {
        if(getContext() == null) {
            return;
        }
        ((BaseActivity)getContext()).setChangeType(changeType);
    }
    
    public void cancleChangeType() {
        setChangeType(0);
    }

    public void setFooterLoadMoreOverText(String loadMoreOverText) {
        if(!StringUtil.isEmpty(loadMoreOverText)) {
            this.loadMoreOverText = loadMoreOverText;
        }
    }
    
    public String getFooterLoadMoreOverText() {
        return this.loadMoreOverText;
    }
    
    public abstract ViewGroup containChildView();

    public abstract void initWidget();

    public abstract void initListener();

    public abstract void hideRefreshLoading();

    public abstract void hideLoadMoreLoading(boolean loadMoreSuccess);
    
    public interface OnRefreshDataListener{
        /**
         * 下拉刷新或者加载更多状态 开始
         * @param isRefresh  是否是下拉刷新，否则是加载更多
         */
        public void onStart(boolean isRefresh);
        
        /**
         * 下拉刷新或者加载更多状态 结束
         * @param isRefresh 是否是下拉刷新，否则是加载更多
         * @param succuss 加载数据成功还是失败的标记
         */
        public void onFinish(boolean isRefresh, boolean succuss);
    }
}
