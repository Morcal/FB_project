package com.feibo.joke.view.util;

import android.content.Context;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.MainEvent;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.widget.BaseItemLayout;
import com.feibo.joke.view.widget.MainTabButton;
import com.feibo.joke.view.widget.MessageButton;
import com.feibo.joke.view.widget.TabViewGroup;

import de.greenrobot.event.EventBus;

public class MessageHintManager {

    private static final String SP_DYNAMIC = "hint_dynamic";
    private static final String SP_MESSAGE_MESSAGE = "hint_message_message";
    private static final String SP_MESSAGE_NOTICE = "hint_message_notice";
    private static final String SP_FRIEND_MASTER = "hint_friend_master";
    private static final String SP_FRIEND_SINA = "hint_friend_sina";
    private static final String SP_VERSION = "hint_version";
    private static final String SP_DRAFT = "hint_draft";

    private static long getUserId() {
        return UserManager.getInstance().getUser().id;
    }
    
    //////////////////////////// 主界面 //////////////////////////////////////
    public static void initMain(Context context, MainTabButton home, MainTabButton mine) { 
        if(home != null) {
            home.setRedHintVisible(getMainHomeHint(context));
        }
        if(mine != null) {
            mine.setRedHintVisible(getMainMineHint(context));
        }
    }
    
    /** 首页红点提示 */
    private static boolean getMainHomeHint(Context context) {
        return getDynamicHint(context);
    }
    
    /** 首页我的红点提示 */
    private static boolean getMainMineHint(Context context) {
        return getDraftCount(context) > 0 ||  getMineVersionHint(context) || getMineFriendCountHint(context) > 0 || getMineFriendCountHint(context) > 0 || getMineMessageCountHint(context) > 0;
    }
    
    /////////////////////////////// 我的界面 //////////////////////////////////////////
    public static void initMine(Context context, BaseItemLayout messageItem, BaseItemLayout friendItem, BaseItemLayout draftItem, BaseItemLayout settingItem) {
        if(messageItem != null) {
            messageItem.setHintNum(getMineMessageCountHint(context));
        }
        if(friendItem != null) {
            friendItem.setHintNum(getMineFriendCountHint(context));
        }
        if(draftItem != null) {
            if(getDraftCount(context) > 0) {
                draftItem.setMessageHintTitle(true, context.getResources().getString(R.string.setting_hint_has_draft));
            } else {
                draftItem.setMessageHintTitle(false, "");
            }
        }
        if(settingItem != null) {
            if(getMineVersionHint(context)) {
                settingItem.setMessageHintTitle(true, context.getResources().getString(R.string.setting_check_new_version));
            } else {
                settingItem.setMessageHintTitle(false, "");
            }
        }
    }

    /** 获取是否有草稿的数量 */
    public static int getDraftCount(Context context) {
        return SPHelper.getMessageHint(context, SP_DRAFT);
    }
    
    /** 设置草稿的数量 */
    public static void setDraftCount(Context context, int draftsCount) {
        if(context == null) {
            context = AppContext.getContext();
        }
        if(getDraftCount(context) == draftsCount) {
            return;
        }
        SPHelper.setMessageHint(context, SP_DRAFT, draftsCount);
        postEvent();
    }
    
    /** 当前草稿的数量加一或者减一 */
    public static void addOrDeleteDraft(Context context, boolean isAdd) {
        if(context == null) {
            context = AppContext.getContext();
        }
        setDraftCount(context, getDraftCount(context) + (isAdd ? 1 : -1));
    }
    
    /** 获取我的页面里面的消息数量 */
    public static int getMineMessageCountHint(Context context) {
        return getMessageMessageHint(context) + getMessageNoticeHint(context);
    }
    
    /** 获取我的页面里面的找朋友数量 */
    public static int getMineFriendCountHint(Context context) {
        return getFriendMasterHint(context) + getFriendSinaHint(context);
    }
    
    /** 我的界面里面显示是否有新版本提示 */
    public static boolean getMineVersionHint(Context context) {
        return getHasNewVersion(context);
    }
    
    /////////////////////////////// 消息界面  ///////////////////////////////////////////
    public static void initMessage(Context context, MessageButton messageButton) {
        if(messageButton != null) {
            messageButton.setMessageHint(MessageButton.TYPE_NOTICE , getMessageNoticeHint(context) != 0);
            messageButton.setMessageHint(MessageButton.TYPE_MESSAGE , getMessageMessageHint(context) != 0);
        }
    }

    /** 设置消息页里面的消息的数量 */
    public static void setMessageMessageHint(Context context, int count) {
        if(getUserId() == 0) {
            return;
        }
        if(getMessageMessageHint(context) == count) {
            return;
        }
        SPHelper.setMessageHint(context, getUserId() + SP_MESSAGE_MESSAGE, count);
        postEvent();
    }

    /** 获取是否有消息页里面的消息 */
    public static int getMessageMessageHint(Context context) {
        if(getUserId() == 0) {
            return 0;
        }
        return SPHelper.getMessageHint(context, getUserId() + SP_MESSAGE_MESSAGE);
    }
    
    /** 设置消息页里面的通知的数量 */
    public static void setMessageNoticeHint(Context context, int count) {
        if(getUserId() == 0) {
            return;
        }
        if(getMessageNoticeHint(context) == count) {
            return;
        }
        SPHelper.setMessageHint(context, getUserId() + SP_MESSAGE_NOTICE, count);
        postEvent();
    }

    /** 获取是否有消息页通知的消息 */
    public static int getMessageNoticeHint(Context context) {
        if(getUserId() == 0) {
            return 0;
        }
        return SPHelper.getMessageHint(context, getUserId() + SP_MESSAGE_NOTICE);
    }
    
    ////////////////////////////// 找朋友界面 //////////////////////////////////////////
    public static void initFindFriend(Context context, BaseItemLayout funnyItem, BaseItemLayout weiboItem) {
        if(funnyItem != null) {
            funnyItem.setHintNum(getFriendMasterHint(context));
        } 
        if(weiboItem != null) {
            weiboItem.setHintNum(getFriendSinaHint(context));
        }
    }
    
    /** 获取搞笑微博达人的数量 */
    public static int getFriendMasterHint(Context context) {
        if(getUserId() == 0) {
            return 0;
        }
        return SPHelper.getMessageHint(context, getUserId() + SP_FRIEND_MASTER);
    }
    
    /** 设置搞笑微博达人的数量 */
    public static void setFriendMasterHint(Context context, int count) {
        if(getUserId() == 0) {
            return;
        }
        if(getFriendMasterHint(context) == count) {
            return;
        }
        SPHelper.setMessageHint(context, getUserId() + SP_FRIEND_MASTER, count);
        postEvent();
    }
    
    /** 获取新浪微博好友的数量 */
    public static int getFriendSinaHint(Context context) {
        if(getUserId() == 0) {
            return 0;
        }
        return SPHelper.getMessageHint(context, getUserId() + SP_FRIEND_SINA);
    }
    
    /** 设置新浪微博好友的数量 */
    public static void setFriendSinaHint(Context context, int count) {
        if(getUserId() == 0) {
            return;
        }
        if(getFriendSinaHint(context) == count) {
            return;
        }
        SPHelper.setMessageHint(context, getUserId() + SP_FRIEND_SINA, count);
        postEvent();
    }
    
    /////////////////////////// 设置界面 ///////////////////////////////////////
    public static void initSetting(Context context, BaseItemLayout itemVersion, String versionName) { 
        if(itemVersion != null) {
            if(getHasNewVersion(context)) {
                itemVersion.setMessageHintTitle(true, context.getResources().getString(R.string.setting_has_new_version));
            } else {
                itemVersion.setMessageHintTitle(false, "当前版本：v" + versionName);
            }
        }
    }
    
    /** 获取是否有新版本 */
    public static boolean getHasNewVersion(Context context) {
        return SPHelper.getMessageHint(context, SP_VERSION) != 0;
    }
    
    /** 设置是否有新版本 */
    public static void setHasNewVersion(Context context, boolean b) {
        if(getHasNewVersion(context) == b) {
            return;
        }
        SPHelper.setMessageHint(context, SP_VERSION, b ? 1 : 0);
        postEvent();
    }
    
    /////////////////////////// 首页界面 /////////////////////////////////////////
    public static void initHome(Context context, TabViewGroup group) {
        if(group != null) {
            group.setRedHintVisible(TabViewGroup.STATU_DYNAMIC, getDynamicHint(context));
        }
    }
    
    /** 设置是否有贵圈动态 */
    public static void setDynamicHint(Context context, boolean b) {
        if(getUserId() == 0) {
            return;
        }
        if(getDynamicHint(context) != b) {
            SPHelper.setMessageHint(context, getUserId() + SP_DYNAMIC, b ? 1 : 0);
            postEvent();
        }
    }

    /** 获取是否有贵圈动态 */
    public static boolean getDynamicHint(Context context) {
        if(getUserId() == 0) {
            return false;
        }
        return SPHelper.getMessageHint(context, getUserId() + SP_DYNAMIC) != 0;
    }
    
   ////////////////////////////////////////////////////////////////////////////////
    private static void postEvent() {
        EventBus.getDefault().post(new MainEvent(DataChangeEventCode.CODE_EVENT_BUS_REDHINT));
    }
}
