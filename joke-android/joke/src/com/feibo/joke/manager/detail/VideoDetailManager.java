package com.feibo.joke.manager.detail;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.Video;

public class VideoDetailManager extends BaseManager {

    private Video video;
    private long videoId;

    public VideoDetailManager(long videoId) {
        this.videoId = videoId;
    }

    public void readVideoDetail(final LoadListener listener) {
        JokeDao.getVideoDetail(videoId, new IEntityListener<Video>() {
            
            @Override
            public void result(Response<Video> entity) {
                if (entity.rsCode != ReturnCode.RS_SUCCESS) {
                    handleFail(listener, entity.rsCode);
                    return;
                }
                
                video = entity.data;
                handleSuccess(listener);
            }
        }, true);
    }

    public Video getVideo() {
        return video;
    }
}
