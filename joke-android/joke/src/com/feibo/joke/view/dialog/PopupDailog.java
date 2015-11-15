package com.feibo.joke.view.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.feibo.joke.R;
import com.feibo.joke.manager.work.CountManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.BaseTopic;
import com.feibo.joke.model.InnerWebInfo;
import com.feibo.joke.model.Popup;
import com.feibo.joke.model.PopupButton;
import com.feibo.joke.view.WebFragment;
import com.feibo.joke.view.module.home.VideoTopicFragment2;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.module.setting.FeedbackFragment;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.google.gson.Gson;

import fbcore.log.LogUtil;

public class PopupDailog extends RemindDialog {

    private Popup popup;

    private Drawable lightBg;
    private Drawable greyBg;
    private Drawable defaultBg;
    private int textLightColor;
    private int textGreyColor;
    private int textDefaultColor;

    public PopupDailog(Context context, RemindSource source) {
        super(context, source, false);
        lightBg = getContext().getResources().getDrawable(R.drawable.bg_orange);
        greyBg = getContext().getResources().getDrawable(R.drawable.bg_grey);
        textLightColor = getContext().getResources().getColor(R.color.c9_white);
        textGreyColor = getContext().getResources().getColor(R.color.c4_black);
        defaultBg = greyBg;
        textDefaultColor = textGreyColor;
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.dialog_popup;
    }

    @Override
    protected void initContentView(View view, RemindSource source) {
        super.initContentView(view, source);
        PopupButton leftButton = popup.leftButton;
        PopupButton rightButton = popup.rightButton;

        //存在兼容性问题
        holdeView.confirm.setBackgroundDrawable(rightButton == null ? defaultBg : rightButton.isHighLight() ? lightBg : greyBg);
        holdeView.cancel.setBackgroundDrawable(leftButton == null ? defaultBg : leftButton.isHighLight() ? lightBg : greyBg);
        holdeView.cancel.setTextColor(leftButton == null ? textDefaultColor : leftButton.isHighLight() ? textLightColor : textGreyColor);
        holdeView.confirm.setTextColor(rightButton == null ? textDefaultColor : rightButton.isHighLight() ? textLightColor : textGreyColor);
    }

    public static PopupDailog show(final Context context, final Popup popup, boolean defaultDismiss) {
        PopupDailog dialog = null;
        try {
            if (popup.rightButton == null && popup.leftButton != null) {
                popup.rightButton = popup.leftButton;
                popup.leftButton = null;
            }
            String content = "\n" + popup.content; //用来占位
            RemindSource remindSource = new RemindSource(popup.title, content, popup.rightButton.lable,
                    popup.leftButton == null ? null : popup.leftButton.lable);
            dialog = new PopupDailog(context, remindSource);
            dialog.defaultDismiss = defaultDismiss;
            dialog.popup = popup;
            dialog.show();
            dialog.setOnDialogClickListener(new OnDialogClickListener() {

                @Override
                public void onConfirm() {
                    handlePop(context, popup, false);
                }

                @Override
                public void onCancel() {
                    handlePop(context, popup, true);
                }
            });

            CountManager.countPopDialog(popup.id, CountManager.Action.SHOW);
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
        return dialog;
    }

    protected static void handlePop(Context mContext, Popup popup, boolean isLeft) {
        CountManager.countPopDialog(popup.id, isLeft ? CountManager.Action.LEFT : CountManager.Action.RIGHT);
        PopupButton button = isLeft ? popup.leftButton : popup.rightButton;
        switch (button.action) {
            case PopupButton.ACTION_DETAIL_TOPIC:
                BaseTopic bt = new Gson().fromJson(button.info, BaseTopic.class);
//            Bundle b = VideoTopicFragment.buildBundle(bt.id, bt.title);
//            LaunchUtil.launchSubActivity(mContext, VideoTopicFragment.class, b);
                Bundle b = VideoTopicFragment2.buildBundle(bt.id, null, bt.title);
                LaunchUtil.launchSubActivity(mContext, VideoTopicFragment2.class, b);
                break;
            case PopupButton.ACTION_DETAIL_USER:
                long userID = Long.valueOf(button.info);
//                b = UserDetailFragment.buildBundle(UserManager.getInstance().isFromMe(userID), userID);
//                LaunchUtil.launchSubActivity(mContext, UserDetailFragment.class, b);
                b = UserDetailFragment2.buildBundle(UserManager.getInstance().isFromMe(userID), userID);
                LaunchUtil.launchSubActivity(mContext, UserDetailFragment2.class, b);
                break;
            case PopupButton.ACTION_DETAIL_VIDEO:
                userID = Long.valueOf(button.info);
                b = VideoDetailFragment.buildBundle(userID, -1, -1);
                LaunchUtil.launchSubActivity(mContext, VideoDetailFragment.class, b);
                break;
            case PopupButton.ACTION_FEEDBACK:
                LaunchUtil.launchSubActivity(mContext, FeedbackFragment.class, null);
                break;
            case PopupButton.ACTION_WEB:
                LaunchUtil.launchWebActivity(mContext, button.info);
                break;
            case PopupButton.ACTION_INNER_WEB:
                InnerWebInfo info = new Gson().fromJson(button.info, InnerWebInfo.class);
                Bundle bundle = WebFragment.buildWebArgs(info.title, info.url);
                LaunchUtil.launchSubActivity(mContext, WebFragment.class, bundle);
                break;
        }
    }


}
