package com.feibo.snacks.manager.global.orders;

import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersListAllManager extends AbsOrdersManager {

    public OrdersListAllManager(ILoadingView loadingView) {
        super(loadingView);
    }

    @Override
    protected BaseDataType generateDataType() {
        return BaseDataType.OrdersDataType.ALL_ORDERS;
    }

    @Override
    protected void load(int curPage, String sinceId, boolean needCache, DaoListener listener) {
        SnacksDao.getOrderListAll(sinceId, curPage, listener);
    }
}
