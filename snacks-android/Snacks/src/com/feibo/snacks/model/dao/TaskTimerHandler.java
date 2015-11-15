package com.feibo.snacks.model.dao;


import android.os.Handler;

import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;

public class TaskTimerHandler implements TaskHandler {

    private static final int TIME_OUT_LIMIT = 15 * 1000;
    private static final int TIME_OUT_SIGN = 1;

    private Handler handler = null;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(TIME_OUT_SIGN);
        }
    };

    public TaskTimerHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                case TIME_OUT_SIGN:
                    onFail(TaskFailure.ERROR);
                    break;
                default:
                    break;
                }
            };
        };
        handler.postDelayed(runnable, TIME_OUT_LIMIT);
    }

    @Override
    public void onSuccess(Object result){
        removeMessage();
    }

    @Override
    public void onFail(TaskFailure failure){
        removeMessage();
    }

    @Override
    public void onProgressUpdated(Object... params){

    }

    private void removeMessage() {
        handler.removeCallbacks(runnable);
    }

}