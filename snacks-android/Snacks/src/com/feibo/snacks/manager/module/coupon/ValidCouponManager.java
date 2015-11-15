package com.feibo.snacks.manager.module.coupon;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class ValidCouponManager extends AbsLoadingPresenter {

    private String ids;
    private String addressId;
    private String couponId;

    private AbsListHelper validHelper;
    private AbsBeanHelper useHelper;

    public ValidCouponManager(ILoadingView loadingView) {
        super(loadingView);
        validHelper = new AbsListHelper(BaseDataType.DiscouponDataType.VALID_COUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<DiscountCoupon> goodsList = (List<DiscountCoupon>) getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getDiscouponListValid(addressId,ids,sinceId,curPage,listener);
            }
        };

        useHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.USE_COUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.commitCart2OrderUrl(addressId,ids,couponId,listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:{
                validHelper.refresh(true, validHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.VALID_COUPON;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.VALID_COUPON;
    }

    public void useCoupon(ILoadingListener listener) {
        useHelper.loadBeanData(false,useHelper.generateLoadingListener(listener));
    }

    public void clearData() {
        validHelper.clearData();
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public OrdersDetail getOrderDetail() {
        return (OrdersDetail) useHelper.getData();
    }

    public String getCouponName(int size) {
        return AppContext.getContext().getResources().getString(R.string.coupon_number,size);
    }
}
