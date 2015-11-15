package com.feibo.joke.view.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.feibo.joke.R;
import com.feibo.joke.video.VideoShareFragment;
import com.feibo.joke.view.BaseFragment;
import com.feibo.joke.view.BaseSwitchActivity;
import com.feibo.joke.view.KeyboardSwitchActivity;
import com.feibo.joke.view.module.mine.edit.NicknameFragment;
import com.feibo.joke.view.module.mine.edit.SignatureFragment;

public class LaunchUtil {

    public static final String FRAGMENT = "fragment";
    
    private static long lastClickTime = 0;

    //判断是否重复点击
    private static boolean canClick(){
        if(lastClickTime == 0){
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if((currentClickTime - lastClickTime) < 1100){
                return false;
            }
            lastClickTime = currentClickTime;
        }
        return true;
    }
    
    public static void clearClickTime() {
        lastClickTime = 0;
    }
    
    public static void launchSubActivity(Context context, Class<? extends BaseFragment> clsFragment, Bundle bundle) {
        launchSubActivity(context, clsFragment, false, bundle);
    }
    
    public static void launchSubActivity(Context context, Class<? extends BaseFragment> clsFragment, boolean resetClickTime, Bundle bundle) {
        launchSubActivity(context, clsFragment, bundle, false);
        if(resetClickTime) {
            clearClickTime();
        }
    }

    public static void launchSubActivity(Context context, Class<? extends BaseFragment> clsFragment, Bundle bundle, boolean isNeedNewTask) {
        if(!canClick()){
            return;
        }
        Intent intent = new Intent(context, BaseSwitchActivity.class);/*|| clsFragment == NicknameFragment.class || clsFragment == SignatureFragment.class*/
        if(clsFragment == VideoShareFragment.class ) {
            intent = new Intent(context, KeyboardSwitchActivity.class);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(FRAGMENT, clsFragment);
        if (isNeedNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        ((Activity)context).overridePendingTransition(R.anim.fragment_in,R.anim.fragment_out);
        context.startActivity(intent);
    }

    public static void launchWebActivity(Context context, String url) {
        if(!url.startsWith("http")) {
            url = "http://" + url;
        }
        Uri uri = Uri.parse(url);  
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
        context.startActivity(intent);  
    }
    
}