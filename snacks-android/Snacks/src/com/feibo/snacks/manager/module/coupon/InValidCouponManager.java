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
public class InValidCouponManager extends AbsLoadingPresenter {

    private String ids;
    private AbsListHelper inValidHelper;
    private String addressId;

    public InValidCouponManager(ILoadingView loadingView) {
        super(loadingView);
        inValidHelper = new AbsListHelper(BaseDataType.DiscouponDataType.INVALID_COUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<DiscountCoupon> goodsList = (List<DiscountCoupon>) getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0 : goodsList.get(goodsList.size() - 1).id);
                SnacksDao.getDiscouponListInValid(addressId,ids,sinceId,curPage,listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:{
                inValidHelper.refresh(true, inValidHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.INVALID_COUPON;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.INVALID_COUPON;
    }

    public void clearData() {
        inValidHelper.clearData();
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
