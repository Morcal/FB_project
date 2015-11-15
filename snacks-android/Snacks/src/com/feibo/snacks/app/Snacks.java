package com.feibo.snacks.app;

import android.app.Application;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.Share4GiftConfigManager;

import fbcore.log.LogUtil;

public class Snacks extends Application {

    private static final String TAG = "snacks";
    private boolean showShare4GiftView = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setDebuggable(Constant.DEBUG);
        LogUtil.trace(Constant.TRACE);

        AppContext.init(this);

        AlibabaSDK.asyncInit(this, new InitResultCallback() {

            @Override
            public void onSuccess() {
                LogUtil.d("Snack", "success");
            }

            @Override
            public void onFailure(int code, String message) {
                LogUtil.d("Snack", "code" + code + "message:" + message);
            }

        });

        Share4GiftConfigManager.loadShare4GiftConfig(new ILoadingListener() {
            @Override
            public void onSuccess() {
                showShare4GiftView = Share4GiftConfigManager.showShare4GiftView();

            }

            @Override
            public void onFail(String failMsg) {

            }
        });
    }

    public boolean needShowShare4GiftView() {
        return showShare4GiftView;
    }
}
