package com.feibo.snacks.manager.module.subject;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.SubjectDataType;

import java.util.List;

public class SubjectManager extends AbsLoadingPresenter {

    private int subjectId;
    private AbsListHelper subjectNewHelper;

    public SubjectManager(ILoadingView loadingView) {
        super(loadingView);
        subjectNewHelper = new AbsListHelper(SubjectDataType.SUBJECT_NEW) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Subject> subjectList = (List<Subject>) getData();
                long sinceId = curPage == 1 ? 0 : (subjectList == null ? 0 : subjectList.get(subjectList.size() - 1).id);
                SnacksDao.getSubjectList(subjectId, sinceId, curPage, listener);
            }
        };
    }

    @Override
    public boolean hasMore() {
        return subjectNewHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return SubjectDataType.SUBJECT_NEW;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return SubjectDataType.SUBJECT_NEW;
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
        case LOAD_FIRST: {
            subjectNewHelper.refresh(true, subjectNewHelper.generateLoadingListener(listener));
            break;
        }
        case LOAD_MORE: {
            subjectNewHelper.loadMore(true, subjectNewHelper.generateLoadingListener(listener));
            break;
        }
        default:
            break;
        }
    }

    public void clear() {
        subjectNewHelper.clearData();
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public List<Subject> getDatas() {
        return (List<Subject>) subjectNewHelper.getData();
    }
}
