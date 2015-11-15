package com.feibo.snacks.manager.module.person;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.Feedback;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.UrlBuilder;
import com.feibo.snacks.model.dao.cache.BaseDataType.PersonDataType;

import java.util.List;

public class FeedbackManager {

    private AbsListHelper feedbackListHelper;
    private AbsSubmitHelper feedbackSubmitHelper;

    private String submitContent;

    public FeedbackManager() {
        feedbackListHelper = new AbsListHelper(PersonDataType.FEEDBACK) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getFeedback(listener);
            }
        };

        feedbackSubmitHelper = new AbsSubmitHelper(PersonDataType.FEEDBACK) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.addFeedback(submitContent, listener);
            }
        };
    }

    public void submit(String content, ILoadingListener listener) {
        submitContent = content;
        feedbackSubmitHelper.submitData(feedbackSubmitHelper.generateLoadingListener(listener));
    }

    public void loadData(ILoadingListener listener) {
        feedbackListHelper.loadMore(true, feedbackListHelper.generateLoadingListener(listener));
    }

    public List<Feedback> getFeedbacks() {
        return (List<Feedback>) feedbackListHelper.getData();
    }

    public void clear() {
        feedbackListHelper.clearData();
    }

}
