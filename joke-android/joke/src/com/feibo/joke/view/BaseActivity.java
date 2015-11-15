package com.feibo.joke.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

import fbcore.log.LogUtil;

import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.model.MainEvent;

import de.greenrobot.event.EventBus;

public class BaseActivity extends FragmentActivity {

    public static final int REQUEST_CODE = 1001;
    public static final int RESULT_CODE = 1002;
    
    public static final String CHANGE_TYPE = "change_type";
    public static final String CHANGE_BUNDLE = "change_bundle";
    
    private int changeType;
    private Bundle dataBundle;
    
    private Handler handler;
    
    //当changeType等于0时也返回onDataChange
    private boolean changeAllTime;

    public BaseActivity() {
        this(false);
    }
    
    public BaseActivity(boolean changeAllTime) {
        handler = new Handler(Looper.getMainLooper());
        this.changeAllTime = changeAllTime;
    }
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }
    
    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }
    
    public void cancleChangeType() {
        setChangeType(0);
    }
    
    public void setChangeTypeAndFinish(int changeType) {
        setChangeType(changeType);
        finish();
    }
    
    public Bundle getFinishBundle() {
        return dataBundle;
    }

    public void setFinishBundle(Bundle bundle) {
        this.dataBundle = bundle;
    }

    @Override
    public void finish() {
        if(changeType != 0) {
            Intent forResultIntent = new Intent();
            forResultIntent.putExtra(CHANGE_TYPE, changeType);
            forResultIntent.putExtra(CHANGE_BUNDLE, dataBundle);
            setResult(RESULT_CODE, forResultIntent);
        }
        super.finish();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SocialManager.getInstance(this).onNewIntent(this, intent);
    }
    
    @Override
    public void startActivity(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SocialManager.getInstance(this).onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
        	int changeType = data.getIntExtra(CHANGE_TYPE, 0);
        	if(changeType == 0 && !changeAllTime) {
        	    return;
        	}
        	dataBundle = data.getBundleExtra(CHANGE_BUNDLE);
        	onDataChange(changeType);
        }
    }

    /**
     * 使用onEventMainThread来接收事件，那么不论分发事件在哪个线程运行，接收事件永远在UI线程执行，
     * @param event
     */
    public void onEventMainThread(MainEvent event) {
        LogUtil.d("event", "onEventMainThread-->" + Thread.currentThread().getId());
        onDataChange(event.code);
    }
    
    public void onDataChange(int code) {}

    @Override
    protected void onPause() {
        super.onPause();
        // 取消注册EventBus
        EventBus.getDefault().unregister(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 注册EventBus
        EventBus.getDefault().register(this);
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onPause(this);
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
    
    public void postDelayed(Runnable run, int delayMillis) {
        if(handler == null) {
            return;
        }
        handler.postDelayed(run, delayMillis);
    }
    
    public void postOnUiHandle(Runnable run) {
        if(handler == null) {
            return;
        }
        handler.post(run);
    }
    
    public void removeHandle(Runnable run) {
        if(handler == null || run == null) {
            return;
        }
        handler.removeCallbacks(run);
    }
    
}
