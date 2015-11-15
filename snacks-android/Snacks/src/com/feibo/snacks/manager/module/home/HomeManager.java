package com.feibo.snacks.manager.module.home;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.group.HomePageHead;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.HomeDataType;

import java.util.List;

import fbcore.log.LogUtil;

public class HomeManager extends AbsLoadingPresenter {

    private static final String TAG = HomeManager.class.getSimpleName();

    private AbsBeanHelper aboveHelper;
    private AbsListHelper newProductsHelper;

    public HomeManager(ILoadingView iLoadingView) {
        super(iLoadingView);
        aboveHelper = new AbsBeanHelper(HomeDataType.HOME_ABOVE) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                LogUtil.i(TAG, "loadData aboveHelper");
                SnacksDao.getHomePageHead(listener);
            }
        };
        newProductsHelper = new AbsListHelper(HomeDataType.NEW_PRODUCTS) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                LogUtil.i(TAG, "loadData goods");
                int curPage = getCurPage();
                List<Goods> goodsList = (List<Goods>)getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getNewGoods(sinceId, curPage, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
        case LOAD_FIRST: {
            LogUtil.i(TAG, "load_first");
            aboveHelper.loadBeanData(true, aboveHelper.generateLoadingListener(listener));
            break;
        }
        case LOAD_MORE: {
            LogUtil.i(TAG, "load_more");
            newProductsHelper.loadMore(true, newProductsHelper.generateLoadingListener(listener));
            break;
        }
        case REFRESH: {
            LogUtil.i(TAG, "refresh");
            newProductsHelper.resetCurPage();
            aboveHelper.loadBeanData(true, aboveHelper.generateLoadingListener(listener));
            break;
        }
        default:
            break;
        }
    }

    @Override
    public boolean hasMore() {
        LogUtil.i(TAG, "hasMore:" + newProductsHelper.hasMoreData());
        return newProductsHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return HomeDataType.HOME_ABOVE;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return HomeDataType.NEW_PRODUCTS;
    }

    public HomePageHead getHomeAbove() {
        LogUtil.i(TAG, "getHomeAbove");
        return (HomePageHead) aboveHelper.getData();
    }

    public List<Goods> getNewProducts() {
        LogUtil.i(TAG, "getNewProducts");
        return (List<Goods>) newProductsHelper.getData();
    }
}