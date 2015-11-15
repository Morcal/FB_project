package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Comment;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.data.BaseListData;

public class CommentsVideoManager extends AbsListManager<Comment>{

    private long videoId;
    private Comment comment;//评论后的评论对象
    private LoadListener listener;//评论视频的listner，要先set
    
    public CommentsVideoManager(long videoId){
        this.videoId = videoId;
    }
    
    @Override
    protected void refresh(IEntityListener<BaseListData<Comment>> listener) {
        JokeDao.getVideoComments(videoId, 0, listener);
        
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<Comment>> listener) {
        long sinceId=0;
        if(getDatas().size()>0){
            sinceId = getDatas().get(getDatas().size() - 1).id;
        }
        JokeDao.getVideoComments(videoId, sinceId, listener);
    }
    /**
     * 设置评论提交后的监听
     * @param listener
     */
    public void setLoadListener(LoadListener listener){
        this.listener=listener;
    }
    
    /**
     * 评论视频评论
     * @param videoId
     * @param commentId
     * @param content
     * @param listener
     */
    
    public void commentVideoComments(long commentId, String content){
        JokeDao.commentVideo(videoId, commentId, content, new IEntityListener<Comment>() {
                    
                    @Override
                    public void result(Response<Comment> response) {
                        if(response.rsCode != ReturnCode.RS_SUCCESS){
                            handleFail(listener, response.rsCode);
                            return;
                        }
                        comment = response.data;
                        handleSuccess(listener);
                    }
                });
    }

    /**
     * 评论视频
     * @param videoId
     * @param content
     * @param listener
     */
    public void commentVideo(String content){
        commentVideoComments(0,content);
    }
    /**
     * 获得评论后的评论对象
     * @return
     */
    public Comment getComment(){
        return comment;
    }
}
