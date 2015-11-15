package com.feibo.snacks.manager.global.orders.unpaid;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/1.
 */
public class NShoppingCartManager extends AbsLoadingPresenter {

    private AbsListHelper helper;

    private AbsListHelper noAvailableHelper;
    private ILoadingView loadingView;
    private ILoadingListener iLoadingListener;

    public NShoppingCartManager(ILoadingView loadingView) {
        super(loadingView);
        this.loadingView = loadingView;
        helper = new AbsListHelper(BaseDataType.ShoppingCartDataType.SHOPPING_CART) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getCartListValid(listener);
            }
        };
        noAvailableHelper = new AbsListHelper(BaseDataType.ShoppingCartDataType.SHOPING_CART_NO_AVAILABLE) {
            @Override
            public void loadData(boolean needCache, Object paramsDao, DaoListener listener) {
                SnacksDao.getCartListInValid(listener);
            }
        };
    }

    @Override
    public void loadData() {
        if (isLoading()) {
            return;
        }
        if (loadingView == null) {
            return;
        }
        loadingView.showLoadingView();
        ILoadingListener listener = new ILoadingListener() {
            @Override
            public void onSuccess() {
                loadingNoAvaibleData();
            }

            private void loadingNoAvaibleData() {
                noAvailableHelper.refresh(false, noAvailableHelper.generateLoadingListener(new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        if (loadingView == null) {
                            return;
                        }
                        loadingView.hideLoadingView();
                        loadingView.fillData(getData(getDataType()));
                        if (NShoppingCartManager.this.iLoadingListener == null) {
                            return;
                        }
                        NShoppingCartManager.this.iLoadingListener.onSuccess();
                        NShoppingCartManager.this.iLoadingListener = null;
                    }

                    @Override
                    public void onFail(String failMsg) {
                        if (loadingView == null) {
                            return;
                        }
                        loadingView.showToast(failMsg);
                        loadingView.showFailView(failMsg);
                        if (NShoppingCartManager.this.iLoadingListener == null) {
                            return;
                        }
                        NShoppingCartManager.this.iLoadingListener.onSuccess();
                        NShoppingCartManager.this.iLoadingListener = null;
                    }
                }));
            }

            @Override
            public void onFail(String failMsg) {
                if (failMsg.equals(NetResult.NOT_DATA_STRING)) {
                    loadingNoAvaibleData();
                } else {
                    if (loadingView == null) {
                        return;
                    }
                    loadingView.showToast(failMsg);
                    loadingView.showFailView(failMsg);
                    NShoppingCartManager.this.iLoadingListener = null;
                }
            }
        };
        this.iLoadingListener = listener;
        generateLoad(LoadType.LOAD_FIRST, this.iLoadingListener);
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST: {
                helper.refresh(false, helper.generateLoadingListener(listener));
                break;
            }
            case LOAD_MORE: {

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

    public ArrayList<CartItem> getCartNoAvalible() {
        return (ArrayList<CartItem>) noAvailableHelper.getData();
    }



    public void release() {
        DataPool.getInstance().removeData(getDataType());
    }
}
