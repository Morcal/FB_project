package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Topic;
import com.feibo.joke.model.data.BaseListData;

public class TopicsManager extends AbsListManager<Topic>{

    long topicId;
    
    public TopicsManager(long topicId) {
        this.topicId = topicId;
    }
    
    @Override
    protected void refresh(IEntityListener<BaseListData<Topic>> listener) {
        if(topicId == 0) {
            JokeDao.getHotTopics(0, listener, true);
        } else {
            JokeDao.getTopics(topicId, 0, listener, true);
        }
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Topic>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        if(topicId == 0) {
            JokeDao.getHotTopics(sinceId, listener, true);
        } else {
            JokeDao.getTopics(topicId, sinceId, listener, true);
        }
    }

}
