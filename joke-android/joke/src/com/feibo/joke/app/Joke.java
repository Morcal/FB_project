package com.feibo.joke.app;

import android.app.Application;

import fbcore.log.LogUtil;
import fbcore.task.AsyncTaskManager;
import fbcore.task.SyncTask;

import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.dao.UrlBuilder;
import com.feibo.joke.receiver.AppLauncherReceiver;

public class Joke extends Application {

    /** 平台类型 3：Android */
    public static final String OS_TYPE = "3";

    public static Joke APP;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setDebuggable(Constant.DEBUG);
        LogUtil.trace(Constant.TRACE);
        AppContext.init(this);
        DataProvider.init(this);
        APP = this;
        refreshCid();
    }

    private void refreshCid() {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
                UrlBuilder.refreshCid();
                return null;
            }
        }, null);
    }

    public static Joke app() {
        return APP;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        quit();
    }

    public void quit() {
        AppLauncherReceiver.isExit = true;
    }

}
