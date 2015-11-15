package com.feibo.joke.video;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import fbcore.log.LogUtil;
import fbcore.utils.Files;

public class CleanService extends Service {
    public static final String TAG = CleanService.class.getSimpleName();

    private static final String ACTION_CLEAN_FILE = "clean_file";

    private static final String PARAM_FILE_PATH = "file_path";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (ACTION_CLEAN_FILE.equals(action)) {
            String filePath = intent.getStringExtra(PARAM_FILE_PATH);
            if (!TextUtils.isEmpty(filePath)) {
                cleanFile(filePath);
            }
        }


        return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void cleanFile(String path) {
        LogUtil.d(TAG, "clean file:" + path);
        Files.delete(path);
        stopSelf();
    }

    public static void cleanFile(Context context, String path) {
        Intent service = new Intent(context, CleanService.class);
        service.setAction(ACTION_CLEAN_FILE);
        service.putExtra(PARAM_FILE_PATH, path);
        context.startService(service);
    }
}
