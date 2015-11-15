package com.feibo.joke.manager.list;

import fbcore.log.LogUtil;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.VideoTopicItem;
import com.feibo.joke.model.data.BaseListData;

public class VideosEssenseManager extends AbsListManager<VideoTopicItem>{

    public VideosEssenseManager() {
        super(true);
    }
    
    @Override
    protected void refresh(IEntityListener<BaseListData<VideoTopicItem>> listener) {
        JokeDao.getEssenseVideos(getSinceId(), listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<VideoTopicItem>> listener) {
        JokeDao.getEssenseVideos(getSinceId(), listener, true);
    }

    private long getSinceId() {
        long sinceId = 0;

        VideoTopicItem item = getLastItem();
        if(item != null) {
            sinceId = item.getId();
        }

        LogUtil.e("", "sinceId = " + sinceId);
        return sinceId;
    }
    
}
