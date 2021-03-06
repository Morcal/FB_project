package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.FunnyMaster;
import com.feibo.joke.model.data.BaseListData;

public class UsersFunnyMasterManager extends AbsListManager<FunnyMaster>{

    @Override
    protected void refresh(IEntityListener<BaseListData<FunnyMaster>> listener) {
        JokeDao.getFunnyMaters(0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<FunnyMaster>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        JokeDao.getFunnyMaters(sinceId, listener, true);
    }

}
