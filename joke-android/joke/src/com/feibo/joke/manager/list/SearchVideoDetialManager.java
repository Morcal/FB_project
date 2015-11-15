package com.feibo.joke.manager.list;

import com.feibo.joke.app.Joke;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.data.BaseListData;

/**
 * Created by Administrator on 2015/11/10.
 */
public class SearchVideoDetialManager extends AbsListManager<Video> {
    private String msg;
    private int page_id;

    public SearchVideoDetialManager(String msg) {
        this.msg = msg;

    }
    @Override
    protected void refresh(IEntityListener<BaseListData<Video>> listener) {
        page_id=1;
        JokeDao.getSearchVedioMsg(page_id, msg, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Video>> listener) {
        page_id++;
        JokeDao.getSearchVedioMsg(page_id, msg, listener, true);
    }
}
