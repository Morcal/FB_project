package com.feibo.joke.manager.work;

import android.content.Context;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SimpleEntityListener;
import com.feibo.joke.model.Notification;
import com.feibo.joke.model.Push;
import com.feibo.joke.model.PushSetting;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.data.BaseListData;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.util.MessageHintManager;

public class PushManager {

    /**
     * 设置推送消息设置
     * @param clientId
     * @param listener
     */
    public static void setPushNotice(int pushType, boolean open, LoadListener listener){
        JokeDao.setPushNotice(pushType, open ? 1 : 0, new SimpleEntityListener(listener));
    }
    
    /**
     * 获取推送设置
     * @param context
     */
    public static void getPushNoticeSetting(final Context context) {
        JokeDao.getPushNotice(new IEntityListener<PushSetting>() {
            
            @Override
            public void result(Response<PushSetting> response) {
                if(response.rsCode == ReturnCode.RS_SUCCESS){
                    PushSetting ps = response.data;
                    if(ps != null) {
                        SPHelper.setPushNotice(context, Push.NOTICE_NEW_MESSGAE, ps.newUserMessage == 1);
                        SPHelper.setPushNotice(context, Push.NOTICE_LIKE, ps.like == 1);
                        SPHelper.setPushNotice(context, Push.NOTICE_FANS, ps.newFans == 1);
                        SPHelper.setPushNotice(context, Push.NOTICE_NEW_SYSTEM_MESSAGE, ps.newSystemMessage == 1);
                    }
                    return;
                }
            }
        });
    }
 
    /**
     * 获取所有的推送消息
     */
    public static void getAllPushMessage(final Context context) {
        if(!UserManager.getInstance().isLogin()) {
            return;
        }
        JokeDao.getPushMassages(new IEntityListener<BaseListData<Notification>>() {
            @Override
            public void result(Response<BaseListData<Notification>> response) {
                if(response != null && response.data != null && response.data.items != null 
                        && response.data.items.size() > 0) {
                    boolean hasNewVersion = false;
//                    boolean phoneFriend = false;
                    boolean dynamic = false;
                    int funnyMasterCount = 0;
                    int weiboFriendCount = 0;
                    int userMassageCount = 0;
                    int systemMassageCount = 0;
                    for(Notification notification : response.data.items) {
                        switch (notification.type) {
                        case Notification.VERSION_REMIND:
                            hasNewVersion = true;
                            break;
                        case Notification.FRIENDS_MESSGAE:
                            userMassageCount += notification.badge;
                            break;
                        case Notification.SYSTEM_MESSAGE:
                            systemMassageCount += notification.badge;
                            break;
                        case Notification.FUNNY_WEIBO_MASTAR:
                            funnyMasterCount += notification.badge;
                            break;
                        case Notification.FRIENDS_WEIBO:
                            weiboFriendCount += notification.badge;
                            break;
//                        case Notification.FRIENDS_PHONE:
//                            phoneFriend = true;
//                            break;
                        case Notification.DYNANIC:
                            dynamic = true;
                            break;
                        }
                    }
                    if(hasNewVersion) {
                        MessageHintManager.setHasNewVersion(context, true);
                    }
                    if(dynamic) {
                        MessageHintManager.setDynamicHint(context, true);
                    }
                    if(funnyMasterCount != 0) {
                        MessageHintManager.setFriendMasterHint(context, funnyMasterCount);
                    }
                    if(weiboFriendCount != 0) {
                        MessageHintManager.setFriendSinaHint(context, weiboFriendCount);
                    }
                    if(userMassageCount != 0) {
                        MessageHintManager.setMessageMessageHint(context, userMassageCount);
                    }
                    if(systemMassageCount != 0) {
                        MessageHintManager.setMessageNoticeHint(context, systemMassageCount);
                    }
                }
            }
        });
    }

    public static boolean handlePushMessage(Context context, Notification notification) {
        boolean shouldBeClose = false;
        int messageType = notification.messageType;
        switch (notification.type) {
        case Notification.VERSION_REMIND:
            //版本更新
            shouldBeClose = true;
            MessageHintManager.setHasNewVersion(context, true);
            break;
        case Notification.FRIENDS_MESSGAE:
            //好友消息
            if(!UserManager.getInstance().isLogin() || isUserMessageHintShouldBeClose(context, messageType)) {
                //为登陆情况，不让用户选择
                shouldBeClose = true;
                break;
            }
            if(messageType == Notification.TYPE_USER_ATTENTION || 
                    messageType == Notification.TYPE_USER_COMMENT_OR_REPLEY ||
                    messageType == Notification.TYPE_USER_LIKE) {
                MessageHintManager.setMessageMessageHint(context, notification.badge);
            }
            break;
        case Notification.SYSTEM_MESSAGE:
            //系统通知
            if(messageType == Notification.TYPE_SYSTEM_MESSAGE 
                    && !SPHelper.getPushNotice(context, Push.NOTICE_NEW_SYSTEM_MESSAGE)) {
                shouldBeClose = true;
                break;
            }
            shouldBeClose = false;
            if(messageType == Notification.TYPE_SYSTEM_MESSAGE
                    || messageType == Notification.TYPE_SYSTEM_VIDEO) {
                MessageHintManager.setMessageNoticeHint(context, notification.badge);
            }
            break;
        case Notification.FUNNY_WEIBO_MASTAR:
            //搞笑达人推荐
            shouldBeClose = true;
            MessageHintManager.setFriendMasterHint(context, notification.badge);
            break;
        case Notification.FRIENDS_WEIBO:
            //微博好友
            shouldBeClose = true;
            MessageHintManager.setFriendSinaHint(context, notification.badge);
            break;
        case Notification.FRIENDS_PHONE:
            shouldBeClose = true;
            //手机好友
            break;
        case Notification.DYNANIC:
            shouldBeClose = true;
            //贵圈动态
            MessageHintManager.setDynamicHint(context, true);
            break;
        }
        return shouldBeClose;
    }
    
    /** 用户消息是否显示在通知栏 */
    private static boolean isUserMessageHintShouldBeClose(Context context, int messageType) {
        boolean shouldBeClose = false;
        switch (messageType) {
        case Notification.TYPE_USER_ATTENTION:
            if(!SPHelper.getPushNotice(context, Push.NOTICE_FANS)) {
                shouldBeClose = true;
            }
            break;
        case Notification.TYPE_USER_COMMENT_OR_REPLEY:
            if(!SPHelper.getPushNotice(context, Push.NOTICE_NEW_MESSGAE)) {
                shouldBeClose = true;
            }
            break;
        case Notification.TYPE_USER_LIKE:
            if(!SPHelper.getPushNotice(context, Push.NOTICE_LIKE)) {
                shouldBeClose = true;
            }
            break;
        }
        return shouldBeClose;
    }
    
}
