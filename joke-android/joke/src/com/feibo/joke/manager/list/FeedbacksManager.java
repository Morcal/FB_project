package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.model.data.BaseListData;

public class FeedbacksManager extends AbsListManager<Feedback>{

    @Override
    protected void refresh(IEntityListener<BaseListData<Feedback>> listener) {
        JokeDao.getFeedback(listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Feedback>> listener) {
        JokeDao.getFeedback(listener, true);
    }

}
