package com.feibo.joke.manager.list;

import java.util.ArrayList;
import java.util.List;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.data.BaseListData;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.video.manager.VideoDraftManager;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.video.manager.VideoDraftManager.OnDraftReadListener;
import com.feibo.joke.view.util.MessageHintManager;

public class DraftListManager extends AbsListManager<Draft>{

    @Override
    protected void refresh(final IEntityListener<BaseListData<Draft>> listener) {
        VideoDraftManager.getInstance().readDrafts(new OnDraftReadListener() {
            
            @Override
            public void onResult(List<Draft> drafts) {
                int rsCode = ReturnCode.RS_SUCCESS;
                BaseListData<Draft> listData = getVideoDraftList(drafts);
                if(listData == null) {
                    rsCode = ReturnCode.RS_EMPTY_ERROR;
                }
                MessageHintManager.setDraftCount(null, listData != null ? (listData.items != null ? listData.items.size() : 0) : 0);
                Response<BaseListData<Draft>> response = new Response<BaseListData<Draft>>(rsCode);
                response.data = listData;
                listener.result(response);
            }
        });
    }
    
    private BaseListData<Draft> getVideoDraftList(List<Draft> drafts) {
        if(drafts == null || drafts.size() == 0) {
            return null;
        }
        List<Draft> newList = new ArrayList<Draft>();
        for(Draft draft : drafts) {
            if(!StringUtil.isEmpty(draft.videoPath) && !StringUtil.isEmpty(draft.coverPath) && !StringUtil.isEmpty(draft.jsonPath)) {
                newList.add(draft);
            }
        }
        if(newList.size() == 0) {
            return null;
        }
        
        BaseListData<Draft> videoList = new BaseListData<Draft>();
        videoList.items = newList;
        videoList.count = newList.size();
        
        return videoList;
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Draft>> listener) {
        
    }

}
