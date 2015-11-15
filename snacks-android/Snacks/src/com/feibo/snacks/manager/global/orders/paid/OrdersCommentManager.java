package com.feibo.snacks.manager.global.orders.paid;


import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.List;

/**
 * Created by hcy on 2015/7/20.
 */
public class OrdersCommentManager extends AbsLoadingPresenter {

    public static OrdersCommentManager sManager;
    public static ILoadingView sLoadingView;
    public static String sId;
    private AbsListHelper helper;

    public static OrdersCommentManager instance() {
        if (sManager == null) {
            sManager = new OrdersCommentManager(sLoadingView);
        }
        return sManager;
    }

    public static void setLoadingView(ILoadingView loadingView) {
        sLoadingView = loadingView;
    }

    public static void setOrdersId(String OrdersId) {
        sId = OrdersId;
    }

    private OrdersCommentManager(ILoadingView loadingView) {
        super(sLoadingView);
        helper = new AbsListHelper(getDataType()) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getSendCommentList(sId, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type){
            case LOAD_FIRST: {
                helper.refresh(false, helper.generateLoadingListener(listener));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.OrdersDetailDataType.ORDERS_COMMENT;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.OrdersDetailDataType.ORDERS_COMMENT;
    }

    @Override
    public List<CartItem> getData(BaseDataType type) {
        return (List<CartItem>) DataPool.getInstance().getData(getDataType());
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
        sManager = null;
        sId = null;
    }
}
