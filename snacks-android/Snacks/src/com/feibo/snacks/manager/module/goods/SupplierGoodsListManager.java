package com.feibo.snacks.manager.module.goods;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

import java.util.List;

/**
 * Created by Jayden on 2015/7/14.
 */
public class SupplierGoodsListManager extends AbsLoadingPresenter {

    private int keyId;

    private AbsListHelper supplierGoodsHelper;

    public SupplierGoodsListManager(ILoadingView iLoadingView) {
        super(iLoadingView);
        supplierGoodsHelper = new AbsListHelper(BaseDataType.OrdersDataType.SUPPLIER_GOODS_LIST) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Goods> goodsList = (List<Goods>) getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getSupplierGoodsList(keyId, sinceId, curPage, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:
            case REFRESH:{
                supplierGoodsHelper.refresh(true, supplierGoodsHelper.generateLoadingListener(listener));
                break;
            }
            case LOAD_MORE: {
                supplierGoodsHelper.loadMore(true, supplierGoodsHelper.generateLoadingListener(listener));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean hasMore() {
        return supplierGoodsHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.OrdersDataType.SUPPLIER_GOODS_LIST;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.OrdersDataType.SUPPLIER_GOODS_LIST;
    }

    public List<Goods> getBannerGoodsList() {
        return (List<Goods>) supplierGoodsHelper.getData();
    }

    public void clearData() {
        supplierGoodsHelper.clearData();
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }
}
