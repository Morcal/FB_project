package com.feibo.snacks.manager.module.coupon;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

/**
 * Created by Jayden on 2015/9/2.
 */
public class CouponUsingRuleManager extends AbsLoadingPresenter {

    private AbsBeanHelper usingRuleHelper;

    public CouponUsingRuleManager(ILoadingView loadingView) {
        super(loadingView);
        usingRuleHelper = new AbsBeanHelper(BaseDataType.DiscouponDataType.USING_RULE) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscouponUsingRule(listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST: {
                usingRuleHelper.loadBeanData(true, usingRuleHelper.generateLoadingListener(listener));
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
        return BaseDataType.DiscouponDataType.USING_RULE;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.DiscouponDataType.USING_RULE;
    }
}
