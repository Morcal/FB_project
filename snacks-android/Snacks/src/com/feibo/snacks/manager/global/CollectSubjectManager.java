package com.feibo.snacks.manager.global;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType.PersonDataType;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.util.SPHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectSubjectManager {

    private static CollectSubjectManager sManager;

    public static CollectSubjectManager getInstance() {
        if (sManager == null) {
            sManager = new CollectSubjectManager();
        }
        return sManager;
    }


    private AbsListHelper listHelper;
    private AbsSubmitHelper deleteHelper;
    private AbsSubmitHelper addHelper;
    private AbsSubmitHelper shareSubjectHelper;

    // 传递的参数
    private long shareSubjectId;
    private List<Integer> addFavIds;
    private List<Integer> deleteFavIds;
    private int type=0; //type=0,专题id；type=1，N元任意购；

    private CollectSubjectManager() {

        addFavIds = new ArrayList<>();
        deleteFavIds = new ArrayList<>();

        listHelper = new AbsListHelper(PersonDataType.COLLECT_SUBJECT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                int curPage = getCurPage();
                List<Subject> subjectList = (List<Subject>) getData();
                long sinceId = curPage == 1 ? 0 : (subjectList == null ? 0 : subjectList.get(subjectList.size() - 1).id);
                SnacksDao.getCollectSuject(sinceId, curPage, listener);
            }
        };

        addHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.addCollectSubject(addFavIds, listener);
            }
        };

        deleteHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.deleteCollectSubject(deleteFavIds, listener);
            }
        };

        shareSubjectHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.addSubjectShare(shareSubjectId,type, listener);
            }
        };
    }

    public void loadCollect(boolean isFirst, final ILoadingListener listener) {
        if (isFirst) {
            listHelper.resetCurPage();
        }

        listHelper.loadMore(true, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                List<Subject> subjects = getCollectSubjects();
                for (Subject subject : subjects) {
                    SPHelper.addCollectSubject(subject.id);
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public void addCollect(final int subjectId, final ILoadingListener listener) {
        addFavIds.clear();
        addFavIds.add(subjectId);
        addHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                SPHelper.addCollectSubject(subjectId);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public void removeOneCollect(final int subjectId, final ILoadingListener listener) {
        deleteFavIds.clear();
        deleteFavIds.add(subjectId);
        deleteHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                SPHelper.removeCollectSubject(subjectId);
                listener.onSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                listener.onFail(failMsg);
            }
        });
    }

    public void removeCollects(final int[] index, final ILoadingListener listener) {
        if (index == null || index.length == 0) {
            return;
        }

        List<Subject> subjectList = getCollectSubjects();
        deleteFavIds.clear();
        for (int i = 0; i < index.length; i++) {
            deleteFavIds.add(subjectList.get(index[i]).id);
        }

        deleteHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                List<Subject> subjectList = getCollectSubjects();
                for (int i = index.length - 1; i >= 0; i--) {
                    subjectList.remove(index[i]);
                }

                for (int id : deleteFavIds) {
                    SPHelper.removeCollect(id);
                }

                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if(listener != null){
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public void setType(int type) {
        this.type = type;
    }

    @Deprecated
    public void addShareNumber(long id, ILoadingListener listener) {
        shareSubjectId = id;
        shareSubjectHelper.submitData(shareSubjectHelper.generateLoadingListener(listener));
    }

    public List<Subject> getCollectSubjects() {
        return (List<Subject>) listHelper.getData();
    }

    public boolean isLoadingMore() {
        return listHelper.isLoading();
    }
}
