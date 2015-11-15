package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.data.BaseListData;

public class UsersFansManager extends AbsListManager<User> {

    public long userId;

    public UsersFansManager(long userId) {
        this.userId = userId;
    }

    @Override
    protected void refresh(IEntityListener<BaseListData<User>> listener) {
        JokeDao.getFans(userId, 0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<User>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        JokeDao.getFans(userId, sinceId, listener, true);
    }

}
