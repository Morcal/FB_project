package com.feibo.snacks.view.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        MobclickAgent.openActivityDurationTrack(false);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
