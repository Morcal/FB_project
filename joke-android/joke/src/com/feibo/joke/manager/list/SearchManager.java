package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.data.BaseListData;

/**
 * Created by Administrator on 2015/11/6.
 */
public class SearchManager extends AbsListManager<String> {

    private String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }



    @Override
    protected void refresh(IEntityListener<BaseListData<String>> listener) {
        JokeDao.getSearchMsg(msg, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<String>> listener) {
        // JokeDao.getSearchMsg(msg, listener, true);
    }
}
