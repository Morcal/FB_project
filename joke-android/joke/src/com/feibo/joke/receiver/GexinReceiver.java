package com.feibo.joke.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import org.json.JSONException;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.Constant;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.PushManager;
import com.feibo.joke.model.Push;
import com.feibo.joke.utils.SPHelper;

public class GexinReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        int action = bundle.getInt(PushConsts.CMD_ACTION);
        switch (action) {
        case PushConsts.GET_MSG_DATA:
            byte[] payload = bundle.getByteArray("payload");
            if (payload != null) {
                handleMessage(context, payload);
            }
            break;
        case PushConsts.GET_CLIENTID:
            getClientID(context, bundle);
            break;
        default:
            break;
        }
    }

    private void handleMessage(Context context, byte[] payload) {
        String data = null;
        try {
            data = new String(payload);
            LogUtil.i("", "push = " + data);
            Push push = new Gson().fromJson(data, Push.class);
            pushContent(context, push);
        } catch (Exception e) {
            if (Constant.DEBUG) {
                if(data == null) {
                    return;
                }
                NotificationUtil.notifyNormal(context, (int) System.currentTimeMillis(), R.drawable.ic_launcher,
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), data, data,
                        data, null);
            }
        }
    }

    private void pushContent(Context context, Push push) throws JSONException {
        String title = "冷笑话精选";
        String content = push.content;
        if(PushManager.handlePushMessage(context, push.notification)) {
            //不需要显示在状态栏
            return;
        }
        putToNotification(context, push.notification.type ,push.notification.messageType, title, content, push.notification.id+"");
    }

    private void putToNotification(Context context, int type, int messageType, String title, String content, String tag) {
        Intent launcherIntent = new Intent(AppLauncherReceiver.ACTION);
        launcherIntent.putExtra(AppLauncherReceiver.IK_TYPE, type);
        launcherIntent.putExtra(AppLauncherReceiver.IK_TAG, tag);
        launcherIntent.putExtra(AppLauncherReceiver.IK_MESSAGE_TYPE, messageType);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, launcherIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationUtil.notifyNormal(context, (int) System.currentTimeMillis(), R.drawable.ic_launcher,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), title, title,
                content, pi);
    }

    private void getClientID(final Context context, Bundle bundle) {
        try {
            final String cid = bundle.getString("clientid");
            if (cid != null && !cid.equals(SPHelper.getGetuiClientID(context))) {
                OperateManager.sendGetuiClientID(cid, new LoadListener() {
                    @Override
                    public void onSuccess() {
                        SPHelper.saveGetuiClientID(context, cid);
                    }

                    @Override
                    public void onFail(int code) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
