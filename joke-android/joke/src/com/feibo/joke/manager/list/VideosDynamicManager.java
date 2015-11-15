package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.data.BaseListData;

public class VideosDynamicManager extends AbsListManager<Video> {

    @Override
    protected void refresh(IEntityListener<BaseListData<Video>> listener) {
        JokeDao.getDynamicVideos(0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Video>> listener) {
        long sinceId = getDatas().get(getDatas().size() - 1).id;
        JokeDao.getDynamicVideos(sinceId, listener, true);
    }
}
