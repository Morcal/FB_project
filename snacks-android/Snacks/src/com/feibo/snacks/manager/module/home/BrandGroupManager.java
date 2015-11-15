package com.feibo.snacks.manager.module.home;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingView;

import java.util.List;

/**
 * Created by Jayden on 2015/7/17.
 */
public class BrandGroupManager extends AbsLoadingPresenter {

    private long brandId;
    private AbsListHelper brandHelper;

    public BrandGroupManager(ILoadingView loadingView, long branchId) {
        super(loadingView);
        this.brandId = branchId;
        brandHelper = new AbsListHelper(BaseDataType.HomeDataType.BRAND_GROUP_DETAIL) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                long sinceId = 0;
                BrandDetail brandDetail = (BrandDetail) getData();
                int currentPage = getCurPage();
                if (brandDetail != null) {
                    List<Goods> brandGoodsList = brandDetail.goodses;
                    sinceId = currentPage == 1 ? 0 : (brandGoodsList == null ? 0 : brandGoodsList.get(brandGoodsList.size() - 1).id);
                }
                SnacksDao.getBrandDetail(brandId, sinceId, currentPage, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        brandHelper.loadMore(true, brandHelper.generateLoadingListener(listener));
    }

    @Override
    public boolean hasMore() {
        return brandHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.HomeDataType.BRAND_GROUP_DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.HomeDataType.BRAND_GROUP_DETAIL;
    }

    public BrandDetail getBrandDetail() {
        return (BrandDetail) brandHelper.getData();
    }

    public void clear() {
        brandHelper.clearData();
    }
}
