package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.DiscoveryTopicItem;
import com.feibo.joke.model.data.BaseListData;

public class TopicsDiscoveryManager extends AbsListManager<DiscoveryTopicItem>{

    @Override
    protected void refresh(IEntityListener<BaseListData<DiscoveryTopicItem>> listener) {
        JokeDao.getDiscoveryTopics(listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<DiscoveryTopicItem>> listener) {
        JokeDao.getDiscoveryTopics(listener, true);
    }

    @Override
    public void refreshLocal(int position, DiscoveryTopicItem e) {
        
        
        
    }
   

}
