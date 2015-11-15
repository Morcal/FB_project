package com.feibo.snacks.manager.global.orders.paid;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.group.ExpressDetail;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/28.
 */
public class LogisticsManager extends AbsLoadingPresenter {

    private AbsBeanHelper helper;
    private String ordersId;
    private ArrayList<CartItem4Type> list;

    public LogisticsManager(ILoadingView loadingView, String ordersId) {
        super(loadingView);
        this.ordersId = ordersId;
        helper = new AbsBeanHelper(getDataType()) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getExpressDetail(LogisticsManager.this.ordersId, listener);
            }
        };
    }

    public ExpressDetail getExpressDetail() {
        return (ExpressDetail) getData(getDataType());
    }

    public List<CartItem4Type> getCartList() {
        if (list == null) {
            list = new ArrayList<CartItem4Type>();
            CartItem4Type.getCartItem(list, getExpressDetail().cartSuppliers);
        }
        return list;
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        if (type == LoadType.LOAD_FIRST) {
            helper.loadBeanData(false, helper.generateLoadingListener(listener));
        }
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.OrdersDataType.LOGISTICS_DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return null;
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
        list = null;
    }
}
