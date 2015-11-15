package com.feibo.snacks.manager.module.subject;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.SubjectDataType;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

import java.util.List;

import fbcore.log.LogUtil;

public class SubjectPageManager extends AbsLoadingPresenter {
    private static final String TAG = SubjectPageManager.class.getSimpleName();
    private AbsListHelper subjectHelper;
    private AbsListHelper bannerHelper;

    public SubjectPageManager(AbsLoadingView absLoadingView) {
        super(absLoadingView);
        bannerHelper = new AbsListHelper(SubjectDataType.BANNER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getDiscoveryHead(listener);
            }
        };

        subjectHelper = new AbsListHelper(SubjectDataType.SUBJECT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Subject> subjectList = (List<Subject>) getData();
                long sinceId = curPage == 1 ? 0 : (subjectList == null ? 0 : subjectList.get(subjectList.size() - 1).id);
                SnacksDao.getDiscoverySubjectList(sinceId, curPage, listener);
            }
        };

    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:
            case REFRESH:{
                LogUtil.i(TAG, "refresh");
                bannerHelper.refresh(true, bannerHelper.generateLoadingListener(listener));
                subjectHelper.resetCurPage();
                break;
            }
            case LOAD_MORE: {
                LogUtil.i(TAG, "loadMore");
                subjectHelper.loadMore(true, subjectHelper.generateLoadingListener(listener));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean hasMore() {
        return subjectHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return SubjectDataType.BANNER;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return SubjectDataType.SUBJECT;
    }

    public void clear() {
        subjectHelper.clearData();
    }

    @SuppressWarnings("unchecked")
    public List<Special> getBanSubjects() {
        return (List<Special>) bannerHelper.getData();
    }

    @SuppressWarnings("unchecked")
	public List<Subject> getDatas() {
        return (List<Subject>) subjectHelper.getData();
    }
}
