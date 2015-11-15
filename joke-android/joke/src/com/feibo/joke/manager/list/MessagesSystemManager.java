package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Message;
import com.feibo.joke.model.data.BaseListData;

public class MessagesSystemManager extends AbsListManager<Message>{

    @Override
    protected void refresh(IEntityListener<BaseListData<Message>> listener) {
        JokeDao.getSystemMessages(0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Message>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        JokeDao.getSystemMessages(sinceId, listener, true);
    }

}
