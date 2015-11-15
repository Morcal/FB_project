package com.feibo.snacks.manager.module.specialsell;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingView;

import java.util.List;

/**
 * Created by Jayden on 2015/7/14.
 */
public class SpecialSellManager extends AbsLoadingPresenter {

    private AbsListHelper listHelper;

    public SpecialSellManager(ILoadingView iLoadingView) {
        super(iLoadingView);
        listHelper = new AbsListHelper(BaseDataType.TodaySpecialDataType.SPECIAL_GOODS_LIST) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Goods> goodsList = (List<Goods>) getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getDiscountList(sinceId, curPage, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:
            case REFRESH:{
                listHelper.refresh(true, listHelper.generateLoadingListener(listener));
                break;
            }
            case LOAD_MORE: {
                listHelper.loadMore(true, listHelper.generateLoadingListener(listener));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean hasMore() {
        return listHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.TodaySpecialDataType.SPECIAL_GOODS_LIST;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.TodaySpecialDataType.SPECIAL_GOODS_LIST;
    }

    public List<Goods> getTodaySpecial() {
        return (List<Goods>) listHelper.getData();
    }
}
