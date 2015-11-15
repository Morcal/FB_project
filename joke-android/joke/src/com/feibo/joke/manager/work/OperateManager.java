package com.feibo.joke.manager.work;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SimpleEntityListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.view.util.ToastUtil;

public class OperateManager extends BaseManager{

    public static final int OPT_DELETE = 0;
    public static final int OPT_REPORT = 1;
    public static final int OPT_LIKE = 2;
    public static final int OPT_CANCEL_LIKE = 3;

    public static final int OPT_ATTENTION = 1;
    public static final int OPT_CANCEL_ATTENTION = 0;


    /**
     * 删除视频
     * @param videoId
     * @param listener
     */
    public static void deleteVideo(long videoId, LoadListener listener){
        JokeDao.operateVideo(OPT_DELETE, videoId, new SimpleEntityListener(listener));
    }

    /**
     * 举报视频
     * @param videoId
     * @param listener
     */
    public static void reportVideo(long videoId, LoadListener listener) {
        JokeDao.operateVideo(OPT_REPORT, videoId, new SimpleEntityListener(listener));
    }

    public static void reportVideo(long videoId, OperateListener listener) {
        if (!AppContext.isNetworkAvailable()) {
            listener.fail(ReturnCode.NO_NET, AppContext.getContext().getResources().getString(R.string.not_network));
            return;
        }
        JokeDao.operateVideo(OPT_REPORT, videoId, new OperateEntityListener(listener));
    }

    /**
     * 喜欢视频
     * @param videoId
     * @param listener
     */
    public static void likeVideo(long videoId, LoadListener listener){
        JokeDao.operateVideo(OPT_LIKE, videoId, new SimpleEntityListener(listener));
    }

    /**
     * 取消喜欢视频
     * @param videoId
     * @param listener
     */
    public static void cancelLikeVideo(long videoId, LoadListener listener){
        JokeDao.operateVideo(OPT_CANCEL_LIKE, videoId, new SimpleEntityListener(listener));
    }

    /**
     * 关注好友
     * @param userId
     * @param listener
     */
    public static void attentionUser(long userId, LoadListener listener){
        JokeDao.operateFriend(userId, OPT_ATTENTION, new SimpleEntityListener(listener));
    }

    /**
     * 取消关注好友
     * @param userId
     * @param listener
     */
    public static void cancelAttentionUser(long userId, LoadListener listener){
        JokeDao.operateFriend(userId, OPT_CANCEL_ATTENTION, new SimpleEntityListener(listener));
    }

    /**
     * 删除评论
     * @param commentId
     * @param listener
     */
    public static void deleteComment(long commentId, LoadListener listener){
        JokeDao.operateComment(commentId, OPT_DELETE, new SimpleEntityListener(listener));
    }

    /**
     * 提交反馈
     * @param content
     * @param listener
     */
    public static void putFeedback(String content, LoadListener listener){
        JokeDao.putFeedback(content, new SimpleEntityListener(listener));
    }

    /**
     * 一键关注微博好友
     * @param listener
     */
    public static void attentAllWeiboFriends(LoadListener listener){
        JokeDao.attentAllWeiboFriends(new SimpleEntityListener(listener));
    }

    /**
     * 一键关注推荐达人和微博
     * @param listener
     */
    public static void attentAllUser(boolean isWeibo, final OnAttentionOneKeyListener listener){
        if(listener != null) {
            listener.onStart();
        }
        JokeDao.attentAllUser(isWeibo, new SimpleEntityListener(new LoadListener() {

            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onFinish(true);
                }
            }

            @Override
            public void onFail(int code) {
                if (code == ReturnCode.RS_REPECT_CLICK) {
                    ToastUtil.showSimpleToast("重复点击");
                    return;
                }
                if (listener != null) {
                    listener.onFinish(false);
                }
            }
        }));
    }

    /**
     * 发送个推ID
     * @param clientId
     * @param listener
     */
    public static void sendGetuiClientID(String clientId, LoadListener listener){
        JokeDao.sendGetuiClientId(clientId, new SimpleEntityListener(listener));
    }

    /**
     * 统计视频播放次数
     * @param videoId
     * @param listener
     */
    public static void addPlayVideoCount(long videoId, LoadListener listener) {
        JokeDao.addPlayVideoCount(videoId, new SimpleEntityListener(listener));
    }


    public static void invalidateText(InvalidateType type, String msg, final LoadListener listener) {
        int t = type == InvalidateType.NAME ? 1 : 2;
        JokeDao.invalidateTextd(t, msg, new IEntityListener<Object>() {
            @Override
            public void result(Response<Object> response) {
                if(response.isSuccess()) {
                    listener.onSuccess();
                } else {
                    ToastUtil.showSimpleToast(response.rsMsg);
                    listener.onFail(response.rsCode);
                }
            }
        });
    }

    public enum InvalidateType {
        NAME, SIGNATURE
    }

    public interface OnAttentionOneKeyListener {
        public void onStart();
        public void onFinish(boolean success);
    }

    public static interface OperateListener {
        void success();
        void fail(int code, String msg);
    }

    public static class OperateEntityListener implements IEntityListener<Object> {

        private OperateListener listener;

        public OperateEntityListener(OperateListener listener) {
            this.listener = listener;
        }

        @Override
        public void result(Response<Object> entity) {
            if(entity.rsCode == ReturnCode.RS_SUCCESS){
                listener.success();
                return;
            }
            listener.fail(entity.rsCode, entity.rsMsg);
        }
    }
}
