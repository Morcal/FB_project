package com.feibo.joke.manager;

import android.os.Handler;
import android.os.Looper;

public class BaseManager {

    private Handler handler;

    public BaseManager(){
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 将失败信息抛出到UI线程
     *
     * @param listener
     * @param rsCode
     */
    protected void handleFail(final LoadListener listener, final int rsCode) {
        if(handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFail(rsCode);
            }
        });
    }

    /**
     * 将成功信息抛出到UI线程
     *
     * @param listener
     */
    protected void handleSuccess(final LoadListener listener) {
        if(handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess();
            }
        });
    }

    public void onDestroy(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
