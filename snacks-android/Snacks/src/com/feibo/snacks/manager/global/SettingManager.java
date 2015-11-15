package com.feibo.snacks.manager.global;

import android.os.Handler;
import android.os.Looper;

import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.model.dao.cache.DataPool;


/**
 * Created by lidiqing on 15-9-2.
 */
public class SettingManager {
    private static SettingManager settingManager;

    public static SettingManager getInstance() {
        if(settingManager == null){
            settingManager = new SettingManager();
        }
        return settingManager;
    }

    private Handler mainThreadHandler;
    private SettingManager(){
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // 获取缓存
    public void getDiskSize(final ResultListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String size = DataDiskProvider.getInstance().getDiskSize();
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(true, "", size);
                    }
                });
            }
        }).start();
    }

    // 清除缓存
    public void clearDiskCache(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataDiskProvider.getInstance().clearAllCache();
            }
        }).start();
    }

    public interface ResultListener {
        void onResult(boolean success, String failMsg, Object result);
    }
}
