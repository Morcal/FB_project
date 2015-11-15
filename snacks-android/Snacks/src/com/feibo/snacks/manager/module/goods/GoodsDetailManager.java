package com.feibo.snacks.manager.module.goods;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.GoodsDetail;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.GoodsDetailDataType;

public class GoodsDetailManager extends AbsLoadingPresenter {

    private int goodsId;
    private int number;
    private int kindId;
    private int subKindId;

    private AbsBeanHelper detailHelper;
    private AbsSubmitHelper add2CartHelper;

    public GoodsDetailManager(ILoadingView iLoadingView) {
        super(iLoadingView);
        detailHelper = new AbsBeanHelper(GoodsDetailDataType.GOODS_DETAIL) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getGoodsDetail(goodsId,listener);
            }
        };

        add2CartHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.add2Cart(goodsId,kindId,subKindId,number,listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
        case LOAD_FIRST: {
            detailHelper.loadBeanData(true,detailHelper.generateLoadingListener(listener));
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
        return GoodsDetailDataType.GOODS_DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return null;
    }

    public void addToCart(ILoadingListener listener) {
        add2CartHelper.submitData(add2CartHelper.generateLoadingListener(listener));
    }

    public void clear() {
        detailHelper.clearData();
    }

    public void setKindId(int kindId) {
        this.kindId = kindId;
    }

    public void setSubKindId(int subKindId) {
        this.subKindId = subKindId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public GoodsDetail getDetail() {
        return (GoodsDetail) detailHelper.getData();
    }
}
