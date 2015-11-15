package com.feibo.snacks.view.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.LaunchManager;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.igexin.sdk.PushManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import fbcore.log.LogUtil;

public class LaunchActivity extends BaseActivity {
    private static final String TAG = LaunchActivity.class.getSimpleName();
    private static final int DELAY_MILLIS = 1500;


    @Bind(R.id.image_new)
    ImageView newImageView;

    @Bind(R.id.image_default)
    ImageView defaultImageView;

    @Bind(R.id.board_default)
    View defaultBoardView;

    private LaunchManager launchManager;
    private Handler handler = new Handler();
    private String luckyURL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        // 推送初始化
        PushManager.getInstance().initialize(this.getApplicationContext());
        // 启动图
        launchManager = LaunchManager.getInstance();
        initLuckyUrl();
        setSplashImage();


        launchManager.loadLaunchBitmap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMainActivity();
    }

    // 初始化Lucky URL
    private void initLuckyUrl() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                luckyURL = uri.getQueryParameter("url");
            }
        }
    }

    // 设置启动图
    private void setSplashImage() {
        LaunchManager.SplashInfo splashInfo = launchManager.getLaunchBitmap(LaunchActivity.this);
        if(splashInfo.type == LaunchManager.SplashInfo.TYPE_NEW) {
            LogUtil.i(TAG, "setSplashImage new");
            newImageView.setImageBitmap(splashInfo.bitmap);
            newImageView.setVisibility(View.VISIBLE);
            defaultBoardView.setVisibility(View.GONE);
        } else {
            LogUtil.i(TAG, "setSplashImage default");
            defaultImageView.setImageBitmap(splashInfo.bitmap);
            defaultBoardView.setVisibility(View.VISIBLE);
            newImageView.setVisibility(View.GONE);
        }
    }

    private void startMainActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);

                if (!TextUtils.isEmpty(luckyURL)) {
                    Intent intent3 = new Intent(LaunchActivity.this, WebAcitivity.class);
                    intent3.putExtra(WebAcitivity.SHOPPING_URL, luckyURL);
                    startActivity(intent3);
                }

                if (SPHelper.needGuide()) {
                    Intent intent2 = new Intent(LaunchActivity.this, GuideActivity.class);
                    startActivity(intent2);
                }

                finish();
            }
        }, DELAY_MILLIS);
    }
}
