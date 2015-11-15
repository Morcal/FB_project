package com.feibo.snacks.manager.module.coupon;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class MyCouponManager extends AbsLoadingPresenter{

    private AbsListHelper listHelper;

    public MyCouponManager(ILoadingView loadingView) {
        super(loadingView);
        listHelper = new AbsListHelper(BaseDataType.DiscouponDataType.MY_COUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscouponAboutMy(listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:{
                listHelper.refresh(true, listHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.MY_COUPON;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.MY_COUPON;
    }

    public List<DiscountCoupon> getCoupons() {
        return (List<DiscountCoupon>) listHelper.getData();
    }

    public void clear() {
        listHelper.clearData();
    }
}
