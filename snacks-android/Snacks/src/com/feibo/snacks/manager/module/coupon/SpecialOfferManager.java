package com.feibo.snacks.manager.module.coupon;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.StatusBean;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

/**
 * Created by Jayden on 2015/9/2.
 */
public class SpecialOfferManager extends AbsLoadingPresenter {

    private int type;
    private long id;
    private int couponType;
    private int discouponId;

    private AbsBeanHelper specialOfferHelper;
    private AbsBeanHelper receiveDiscouponHelper;

    public SpecialOfferManager(ILoadingView loadingView) {
        super(loadingView);
        specialOfferHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.SPECIAL_OFFER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getSpecialOffers(type, id,listener);
            }
        };

        receiveDiscouponHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.RECEIVE_DISCOUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscoupon(couponType,discouponId,listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST: {
                specialOfferHelper.loadBeanData(true, specialOfferHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.SPECIAL_OFFER;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.SPECIAL_OFFER;
    }

    public void getDiscouponFromNet(int discouponId,ILoadingListener listener) {
        this.discouponId = discouponId;
        receiveDiscouponHelper.loadBeanData(true,receiveDiscouponHelper.generateLoadingListener(listener));
    }

    public StatusBean getStatusBean() {
        return (StatusBean) receiveDiscouponHelper.getData();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public void clearData() {
        specialOfferHelper.clearData();
    }
}
