package com.feibo.snacks.manager.global.orders.paid;

import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersWaitCommentManager extends AbsOrdersManager {

    public OrdersWaitCommentManager(ILoadingView loadingView) {
        super(loadingView);
    }

    @Override
    protected BaseDataType generateDataType() {
        return BaseDataType.OrdersDataType.WAIT_COMMENT;
    }

    @Override
    protected void load(int curPage, String sinceId, boolean needCache, DaoListener listener) {
        SnacksDao.getOrderListToBeCommented(sinceId, curPage, listener);
    }
}
