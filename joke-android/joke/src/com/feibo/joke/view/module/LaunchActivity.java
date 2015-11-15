package com.feibo.joke.view.module;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.feibo.joke.app.Constant;
import com.feibo.joke.app.Joke;
import com.igexin.sdk.PushManager;

import com.feibo.joke.R;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.EntryManager;
import com.feibo.joke.manager.work.GlobalConfigurationManager;
import com.feibo.joke.model.SplashImage;
import com.feibo.joke.receiver.AppLauncherReceiver;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.BaseActivity;

@SuppressLint("NewApi")
public class LaunchActivity extends BaseActivity {

    private static final int DELAY_MILLIS = 3000;

    private ImageView launchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化个推推送
        PushManager.getInstance().initialize(this.getApplicationContext());
        AppLauncherReceiver.isExit = false;
        
        setContentView(R.layout.activity_launch);
        launchView = (ImageView)findViewById(R.id.launch_bg);

        setBackground();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startMainActivity();
        //获取推送设置
        com.feibo.joke.manager.work.PushManager.getPushNoticeSetting(this);
        //获取全局配置
        GlobalConfigurationManager.getInstance().getGlobalConfiguration(this);
    }

    private void startMainActivity() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);

                if (SPHelper.needGuide(LaunchActivity.this)) {
                    SPHelper.cancelGuide(LaunchActivity.this);
                    Intent intent2 = new Intent(LaunchActivity.this, GuideActivity.class);
                    startActivity(intent2);
                } else {
                    int type = getIntent().getIntExtra(AppLauncherReceiver.IK_TYPE, -1);
                    int messageType = getIntent().getIntExtra(AppLauncherReceiver.IK_MESSAGE_TYPE, -1);
                    int id = getIntent().getIntExtra(AppLauncherReceiver.IK_TAG, -1);
                    if (type != -1 && messageType != -1 && id != -1) {
                        AppLauncherReceiver.launcherSwitch(LaunchActivity.this, type, messageType, id);
                    }
                }
                finish();
            }
        }, DELAY_MILLIS);
    }

    private void setBackground() {
        final EntryManager launchManager = new EntryManager();
        SplashImage img = launchManager.sholdShowLaunchImage(this.getApplication());
        launchView.setImageBitmap(launchManager.getLaunchBitmap(getApplicationContext(), img));

    	launchManager.readSplashImageFromNet(this);
    }
}
