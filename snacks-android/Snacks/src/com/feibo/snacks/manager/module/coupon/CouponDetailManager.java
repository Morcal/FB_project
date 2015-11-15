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
public class CouponDetailManager extends AbsLoadingPresenter {

    private long discouponId;
    private int type;
    private int jumpType;//跳转类型，jumpType=8跳转大礼包，=6跳转优惠券详情页

    private AbsBeanHelper detailHelper;
    private AbsBeanHelper receiveDiscouponHelper;

    public CouponDetailManager(ILoadingView loadingView) {
        super(loadingView);
        detailHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.DETAIL) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscouponDetail(jumpType,discouponId,listener);
            }
        };

        receiveDiscouponHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.RECEIVE_DISCOUPON) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscoupon(type,discouponId, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST: {
                detailHelper.loadBeanData(true, detailHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.DETAIL;
    }

    public void getDiscouponFromNet(ILoadingListener listener) {
        receiveDiscouponHelper.loadBeanData(true,receiveDiscouponHelper.generateLoadingListener(listener));
    }

    public StatusBean getStatusBean() {
        return (StatusBean) receiveDiscouponHelper.getData();
    }


    public void setDiscouponId(long discouponId) {
        this.discouponId = discouponId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public void clearData() {
        detailHelper.clearData();
    }
}
