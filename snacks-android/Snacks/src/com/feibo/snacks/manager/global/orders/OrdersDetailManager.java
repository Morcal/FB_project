package com.feibo.snacks.manager.global.orders;


import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.ArrayList;

/**
 * Created by hcy on 2015/7/20.
 */
public class OrdersDetailManager extends AbsLoadingPresenter {

    public static OrdersDetailManager sManager;
    public static String sId;
    public static ILoadingView sLoadingView;

    private ArrayList<CartItem4Type> list;

    private AbsBeanHelper helper;

    public static OrdersDetailManager instance() {
        if (sManager == null) {
            sManager = new OrdersDetailManager(sLoadingView);
        }
        return sManager;
    }

    public static void setLoadingView(ILoadingView loadingView) {
        sLoadingView = loadingView;
    }

    public static void setOrdersId(String OrdersId) {
        sId = OrdersId;
    }

    private OrdersDetailManager(ILoadingView loadingView) {
        super(sLoadingView);
        helper = new AbsBeanHelper(getDataType()) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getOrderDetail(sId, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type){
            case LOAD_FIRST: {
                helper.loadBeanData(false, helper.generateLoadingListener(listener));
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
        return BaseDataType.OrdersDetailDataType.ORDERS_DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.OrdersDetailDataType.ORDERS_DETAIL;
    }

    @Override
    public OrdersDetail getData(BaseDataType type) {
        return (OrdersDetail) DataPool.getInstance().getData(getDataType());
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
        sManager = null;
        sLoadingView = null;
        sId = null;
    }

    public ArrayList<CartItem4Type> getCart() {
        OrdersDetail detail = getData(getDataType());
        if (detail == null || detail.cartSuppliers == null) {
            return null;
        }
        list = CartItem4Type.getCart(detail.cartSuppliers);
        return list;
    }
}
