package com.feibo.snacks.manager.global.orders.unpaid;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/1.
 */
public class ShoppingCartManager extends AbsLoadingPresenter {

    private AbsListHelper helper;

    public ShoppingCartManager(ILoadingView loadingView) {
        super(loadingView);
        helper = new AbsListHelper(BaseDataType.ShoppingCartDataType.SHOPPING_CART) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getCartListValid(listener);
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
            default:
                break;
        }
    }

    @Override
    public List<CartSuppliers> getData(BaseDataType type) {
        return (List<CartSuppliers>) helper.getData();
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.ShoppingCartDataType.SHOPPING_CART;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.ShoppingCartDataType.SHOPPING_CART;
    }

    public ArrayList<CartItem4Type> getCart() {
        return CartItem4Type.getCart(getData(getDataType()));
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
    }
}
