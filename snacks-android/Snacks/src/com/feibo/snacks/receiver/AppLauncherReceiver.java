package com.feibo.snacks.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.feibo.snacks.app.Constant;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.module.goods.goodslist.BannerGoodsListFragment;
import com.feibo.snacks.view.module.subject.H5SubjectDetailFragment;
import com.feibo.snacks.view.module.subject.SubjectListFragment;
import com.feibo.snacks.view.util.LaunchUtil;

public class AppLauncherReceiver extends BroadcastReceiver {
    public static final String IK_TYPE = "type";
    public static final String IK_TAG = "tag";
    public static final String IK_TITLE = "title";
    public static final String ACTION = "com.feibo.snacks.AppLauncherReceiver";

    private static void launcherYuleApp(Context context, int type, int id) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent.setComponent(new ComponentName("com.feibo.snacks",
                "com.feibo.snacks.view.module.LaunchActivity"));
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launcherIntent.putExtra(IK_TYPE, type);
        launcherIntent.putExtra(IK_TAG, id);
        startActivity(context, launcherIntent);
    }

    public static void launcherSwitch(final Context context, final int type, final int id, final String pushTitle) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launcherSwitchPrivate(context, type, id, pushTitle);
            }
        }, 2000);
    }

    private static void launcherSwitchPrivate(Context context, int type, int id, String pushTitle) {
        switch (type) {
        case GetuiConstant.OPEN_APP:
            break;
        case GetuiConstant.GOODS_LIST:
            if (id != 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(BannerGoodsListFragment.BANNER_ID, id);
                bundle.putString(BannerGoodsListFragment.BANNER_TITLE, pushTitle);
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, BannerGoodsListFragment.class, bundle);
            }
            break;
        case GetuiConstant.GOODS_DETAIL:
            if (id != 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.NOTIFICATION);
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
            break;
        case GetuiConstant.OPEN_URL:
            break;
        case GetuiConstant.SUBJECT_LIST:
            if (id != 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(SubjectListFragment.ID, id);
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, SubjectListFragment.class, bundle);
            }
            break;
        case GetuiConstant.SUBJECT_DETAIL:
            if (id != 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5SubjectDetailFragment.ID, id);
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, H5SubjectDetailFragment.class, bundle);
            }
            break;
        default:
            break;
        }
    }

    private static boolean startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra(IK_TYPE, 1);
        switch (type) {
        case GetuiConstant.OPEN_APP:
            launcherYuleApp(context);
            break;
        case GetuiConstant.SUBJECT_DETAIL:
        case GetuiConstant.GOODS_DETAIL:
        case GetuiConstant.GOODS_LIST:
        case GetuiConstant.SUBJECT_LIST:
            String idStr = intent.getStringExtra(IK_TAG);
            String pushTitle = intent.getStringExtra(IK_TITLE);
            int id = 0;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
            }
            launcherYuleApp(context);
            launcherSwitch(context, type, id, pushTitle);
            break;
        case GetuiConstant.OPEN_URL:
            String url = intent.getStringExtra(IK_TAG);
            Intent webIntent = new Intent(context, WebAcitivity.class);
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            webIntent.putExtra(WebAcitivity.TITLE, "");
            webIntent.putExtra(WebAcitivity.SHOPPING_URL, url);
            context.startActivity(webIntent);
            break;
        default:
            break;
        }
    }

    private void launcherYuleApp(Context context) {
        launcherYuleApp(context, 0, 0);
    }
}
