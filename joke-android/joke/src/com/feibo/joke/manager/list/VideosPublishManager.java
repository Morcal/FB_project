package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.data.BaseListData;

public class VideosPublishManager extends AbsListManager<Video>{

    private long userId;
    
    public VideosPublishManager(long userId){
        this.userId = userId;
    }

    @Override
    protected void refresh(IEntityListener<BaseListData<Video>> listener) {
        JokeDao.getPublishVideos(userId, 0, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Video>> listener) {
        long sinceId = (getDatas() == null || getDatas().size() == 0) ? 0 : getDatas().get(getDatas().size() - 1).id;
        JokeDao.getPublishVideos(userId, sinceId, listener, true);
    }

}
