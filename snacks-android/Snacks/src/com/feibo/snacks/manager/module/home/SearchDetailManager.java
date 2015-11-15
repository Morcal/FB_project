package com.feibo.snacks.manager.module.home;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.SearchDataType;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingView;

import java.util.List;

import fbcore.log.LogUtil;

public class SearchDetailManager extends AbsLoadingPresenter {

    private static final String TAG = SearchDetailManager.class.getSimpleName();

    private AbsListHelper searchDetailHelper;
    private String keyWord;

    public SearchDetailManager(ILoadingView loadingView) {
        super(loadingView);
        searchDetailHelper = new AbsListHelper(SearchDataType.SEARCH) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                LogUtil.i(TAG, "loadData");
                List<Goods> goodsList = (List<Goods>)getData();
                int currentPage = getCurPage();
                long sinceId = currentPage == 1 ? 0: (goodsList == null ? 0: goodsList.get(goodsList.size()-1).id);
                SnacksDao.getSearchResult(keyWord,sinceId,currentPage,listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        LogUtil.i(TAG, "generateLoad");
         searchDetailHelper.loadMore(true,searchDetailHelper.generateLoadingListener(listener));
    }

    @Override
    public boolean hasMore() {
        return searchDetailHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return SearchDataType.SEARCH;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return SearchDataType.SEARCH;
    }

    public List<Goods> getGoodsList() {
        return (List<Goods>)searchDetailHelper.getData();
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void clear() {
        searchDetailHelper.clearData();
    }
}
