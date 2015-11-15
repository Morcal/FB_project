package com.feibo.joke.manager.work;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SimpleEntityListener;
import com.feibo.joke.manager.VideoDetailReportListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.Video;

public class ReportManager extends BaseManager {

//    private static ReportManager manager;
//    private Response<Object> response;
//    private IEntityListener<Object> iEntityListener;
    public static final int OPT_REPORT = 1;
    
    /**
     * 举报视频
     * @param videoId
     * @param listener
     */
    public static void reportVideo(long videoId, final VideoDetailReportListener listener){
        JokeDao.operateVideo(OPT_REPORT, videoId, new IEntityListener<Object>() {

            @Override
            public void result(Response<Object> response) {
                if(response.isSuccess()) {
                    listener.onSuccess();
                } else {
                    listener.onReportFail(response.rsCode, response.rsMsg);
                }
            }
        });
    }
    
    
    /**
     * 举报评论
     * @param commentId
     * @param listener
     */
    public static void reportComment(long commentId, final VideoDetailReportListener listener){
        JokeDao.operateComment(commentId, OPT_REPORT, new IEntityListener<Object>() {
            @Override
            public void result(Response<Object> response) {
                if(response.isSuccess()) {
                    listener.onSuccess();
                } else {
                    listener.onReportFail(response.rsCode, response.rsMsg);
                }
            }
        });
    }

}
