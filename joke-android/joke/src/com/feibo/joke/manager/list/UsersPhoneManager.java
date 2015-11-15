package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.data.BaseListData;

public class UsersPhoneManager extends AbsListManager<User>{

    @Override
    protected void refresh(IEntityListener<BaseListData<User>> listener) {
        JokeDao.getPhoneFriends(0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<User>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        JokeDao.getPhoneFriends(sinceId, listener, true);
    }

}
