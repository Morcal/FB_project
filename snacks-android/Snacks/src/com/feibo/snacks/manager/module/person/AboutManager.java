package com.feibo.snacks.manager.module.person;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.UrlBuilder;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.bean.AboutInfo;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingView;

/**
 * User: LinMIWi(80383585@qq.com)
 * Time: 2015-07-29  07:57
 */
public class AboutManager extends AbsLoadingPresenter {

    private AbsBeanHelper loadingHelper;

    public AboutManager(ILoadingView loadingView){
        super(loadingView);
        loadingHelper = new AbsBeanHelper(BaseDataType.PersonDataType.ABOUT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getAboutInfo(listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        if (type == LoadType.LOAD_FIRST) {
            loadingHelper.loadBeanData(true, loadingHelper.generateLoadingListener(listener));
        }
    }

        @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public BaseDataType getDataType() {
         return BaseDataType.PersonDataType.ABOUT;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return null;
    }

    @Override
    public AboutInfo getData(BaseDataType type) {
        return (AboutInfo) loadingHelper.getData();
    }
}
