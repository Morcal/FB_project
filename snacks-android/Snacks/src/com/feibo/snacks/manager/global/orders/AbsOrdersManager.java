package com.feibo.snacks.manager.global.orders;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.List;


/**
 * Created by hcy on 2015/7/13.
 */
public abstract class AbsOrdersManager extends AbsLoadingPresenter {

    private AbsListHelper helper;
    private BaseDataType dataType;

    public AbsOrdersManager(ILoadingView loadingView) {
        super(loadingView);
        this.dataType = generateDataType();
        helper = new AbsListHelper(dataType) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<ItemOrder> list = (List<ItemOrder>)getData();
                String sinceId = curPage == 1 ? "0" : (list == null ? "0" : list.get(list.size()-1).id);
                load(curPage, sinceId, needCache, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST: {
                helper.refresh(false, helper.generateLoadingListener(listener));
                break;
            }
            case LOAD_MORE: {
                helper.loadMore(false, helper.generateLoadingListener(listener));
                break;
            }
            case REFRESH: {
                helper.refresh(false, helper.generateLoadingListener(listener));
                break;
            }
        }
    }

    @Override
    public List<ItemOrder> getData(BaseDataType type) {
        return (List<ItemOrder>) DataPool.getInstance().getData(getDataType());
    }

    @Override
    public boolean hasMore() {
        return helper.hasMoreData();
    }

    public BaseDataType getDataType() {
        return dataType;
    }

    public BaseDataType getMoreDataType() {
        return dataType;
    }

    public void removeOrders(int position) {
        getData(getDataType()).remove(position);
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
    }

    protected abstract BaseDataType generateDataType();

    protected abstract void load(int curPage, String sinceId, boolean needCache, DaoListener listener);
}
