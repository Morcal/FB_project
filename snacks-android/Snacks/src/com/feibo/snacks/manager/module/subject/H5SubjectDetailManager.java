package com.feibo.snacks.manager.module.subject;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

public class H5SubjectDetailManager extends AbsLoadingPresenter {

    private int subjectId;

    private AbsBeanHelper detailHelper;

    public H5SubjectDetailManager(ILoadingView iLoadingView) {
        super(iLoadingView);
        detailHelper = new AbsBeanHelper(BaseDataType.SubjectDataType.SUBJECT_DETAIL) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getSubjectH5Detail(subjectId,listener);
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
        return BaseDataType.SubjectDataType.SUBJECT_DETAIL;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return BaseDataType.SubjectDataType.SUBJECT_DETAIL;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Subject getDetail() {
        return (Subject) detailHelper.getData();
    }
}
