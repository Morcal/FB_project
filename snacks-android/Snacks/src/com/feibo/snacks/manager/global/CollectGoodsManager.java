package com.feibo.snacks.manager.global;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType.PersonDataType;
import com.feibo.snacks.util.SPHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectGoodsManager {

    private static CollectGoodsManager sManager;
    public static CollectGoodsManager getInstance() {
        if (sManager == null) {
            sManager = new CollectGoodsManager();
        }
        return sManager;
    }

    private AbsListHelper listHelper;
    private AbsSubmitHelper addHelper;
    private AbsSubmitHelper deleteHelper;

    private List<Integer> addFavIds;
    private List<Integer> deleteFavIds;

    private CollectGoodsManager() {

        addFavIds = new ArrayList<>();
        deleteFavIds = new ArrayList<>();

        listHelper = new AbsListHelper(PersonDataType.COLLECT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Goods> goodsList = (List<Goods>)getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getCollectGoodsList(sinceId, curPage, listener);
            }
        };

        addHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.addCollectGoods(addFavIds, listener);
            }
        };

        deleteHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.deleteCollectGoods(deleteFavIds, listener);
            }
        };
    }

    public void loadCollect(boolean isFirst, final ILoadingListener listener) {
        if (isFirst) {
            listHelper.resetCurPage();
        }
        listHelper.loadMore(true, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                List<Goods> goodsList = getCollectGoodsList();
                if (goodsList != null && goodsList.size() > 0) {
                    for (Goods goods : goodsList) {
                        SPHelper.addCollect(goods.id);
                    }
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public void addCollect(final int goodsId, final ILoadingListener listener) {
        addFavIds.clear();
        addFavIds.add(goodsId);
        addHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                SPHelper.addCollect(goodsId);
                if(listener != null){
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if(listener != null){
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public void removeOneCollect(final int goodsId, final ILoadingListener listener) {
        deleteFavIds.clear();
        deleteFavIds.add(goodsId);
        deleteHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                SPHelper.removeCollect(goodsId);
                listener.onSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                listener.onFail(failMsg);
            }
        });
    }

    public void removeCollects(final int[] index, final ILoadingListener listener) {
        if (index == null || index.length == 0) {
            return;
        }

        List<Goods> goodsList = getCollectGoodsList();
        deleteFavIds.clear();
        for (int i = 0; i < index.length; i++) {
            deleteFavIds.add(goodsList.get(index[i]).id);
        }

        deleteHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                List<Goods> goodsList = getCollectGoodsList();
                for (int i = index.length - 1; i >= 0; i--) {
                    goodsList.remove(index[i]);
                }

                for (int id : deleteFavIds) {
                    SPHelper.removeCollect(id);
                }

                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if(listener != null){
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public List<Goods> getCollectGoodsList() {
        return (List<Goods>) listHelper.getData();
    }

    public boolean isLoadingMore() {
        return listHelper.isLoading();
    }
}
