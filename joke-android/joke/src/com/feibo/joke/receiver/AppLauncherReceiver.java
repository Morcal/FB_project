package com.feibo.joke.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.feibo.joke.app.Constant;
import com.feibo.joke.model.Notification;
import com.feibo.joke.view.module.mine.FriendFindFunnyFragment;
import com.feibo.joke.view.module.mine.FriendFindWeiboFragment;
import com.feibo.joke.view.module.mine.MessageFragment;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;

public class AppLauncherReceiver extends BroadcastReceiver {
    public static final String IK_TYPE = "type";
    public static final String IK_MESSAGE_TYPE = "message_type";
    public static final String IK_TAG = "tag";
    public static final String ACTION = "com.feibo.joke.AppLauncherReceiver";
    public static boolean isExit = true;

    private static void launcherJokeApp(Context context, int type, int messageType, int id) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent
                .setComponent(new ComponentName("com.feibo.joke", "com.feibo.joke.view.module.LaunchActivity"));
        launcherIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launcherIntent.putExtra(IK_TYPE, type);
        launcherIntent.putExtra(IK_MESSAGE_TYPE, messageType);
        launcherIntent.putExtra(IK_TAG, id);
        startActivity(context, launcherIntent);
    }

    public static void launcherSwitch(Context context, int type, int messageType, int id) {
        boolean isNeedNewTask = true;
        switch (type) {
            case Notification.VERSION_REMIND://版本更新
                break;
            case Notification.FRIENDS_MESSGAE://好友消息
                if (messageType == Notification.TYPE_USER_COMMENT_OR_REPLEY
                        || messageType == Notification.TYPE_USER_LIKE) {
                    jump2VideoDetail(context, id, isNeedNewTask);
                } else if (messageType == Notification.TYPE_USER_ATTENTION) {
                    jump2UserDetail(context, id, isNeedNewTask);
                }
                break;
            case Notification.SYSTEM_MESSAGE://系统通知
                if (messageType == Notification.TYPE_SYSTEM_VIDEO) {
                    jump2VideoDetail(context, id, isNeedNewTask);
                } else if (messageType == Notification.TYPE_SYSTEM_MESSAGE) {
                    LaunchUtil.launchSubActivity(context, MessageFragment.class, null, isNeedNewTask);
                }
                break;
            case Notification.FUNNY_WEIBO_MASTAR://搞笑达人推荐
                LaunchUtil.launchSubActivity(context, FriendFindFunnyFragment.class, null);
                break;
            case Notification.FRIENDS_WEIBO://微博好友
                LaunchUtil.launchSubActivity(context, FriendFindWeiboFragment.class, null);
                break;
            case Notification.FRIENDS_PHONE://手机好友
                break;
            case Notification.DYNANIC://贵圈动态
                break;
        }
    }

    private static void jump2VideoDetail(Context context, int id, boolean isNeedNewTask) {
        if (id != 0) {
            Bundle bundle = VideoDetailFragment.buildBundle(id, -1);
            LaunchUtil.launchSubActivity(context, VideoDetailFragment.class, bundle, isNeedNewTask);
        }
    }

    private static void jump2UserDetail(Context context, int id, boolean isNeedNewTask) {
        if (id != 0) {
//            Bundle bundle= UserDetailFragment.buildBundle(false, id);
//            LaunchUtil.launchSubActivity(context, UserDetailFragment.class, bundle, isNeedNewTask);
            Bundle bundle = UserDetailFragment2.buildBundle(false, id);
            LaunchUtil.launchSubActivity(context, UserDetailFragment2.class, bundle, isNeedNewTask);
        }
    }

    private static boolean startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            if (Constant.DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra(IK_TYPE, 1);
        int messageType = intent.getIntExtra(IK_MESSAGE_TYPE, 1);

        switch (type) {
            case Notification.FRIENDS_MESSGAE://用户通知
            case Notification.SYSTEM_MESSAGE://系统通知
                String idStr = intent.getStringExtra(IK_TAG);
                int id = 0;
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    return;
                }
                launch(context, type, messageType, id);
                break;
            case Notification.FUNNY_WEIBO_MASTAR://搞笑达人推荐
            case Notification.FRIENDS_WEIBO://微博好友
            case Notification.FRIENDS_PHONE://手机好友
            case Notification.DYNANIC://贵圈动态
//            launch(context, type, messageType, 0);
                break;
            case Notification.VERSION_REMIND://版本更新
                break;
        }
    }

    private void launch(Context context, int type, int messageType, int id) {
        if (isExit) {
            launcherJokeApp(context, type, messageType, id);
        } else {
            launcherJokeApp(context);
            launcherSwitch(context, type, messageType, id);
        }
    }

    private void launcherJokeApp(Context context) {
        launcherJokeApp(context, 0, 0, 0);
    }

}
