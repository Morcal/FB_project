package com.feibo.snacks.receiver;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.PushManager;
import com.feibo.snacks.util.NotificationUtil;
import com.igexin.sdk.PushConsts;

import org.json.JSONException;
import org.json.JSONObject;

public class GexinReceiver extends BroadcastReceiver {

    private static final String SP_TITLE = "title";
    private static final String SP_CONTENT = "content";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        int action = bundle.getInt(PushConsts.CMD_ACTION, 0);
        switch (action) {
        case PushConsts.GET_MSG_DATA:
            byte[] payload = bundle.getByteArray("payload");
            if (payload != null) {
                String data = null;
                try {
                    data = new String(payload);
                    JSONObject root = new JSONObject(data);
                    int type = root.getInt("type");
                    push(context, root, type);
                } catch (Exception e) {
                    if (data == null) {
                        return;
                    }
                    NotificationUtil.notifyNormal(context, (int) System.currentTimeMillis(), R.drawable.ic_launcher,
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), data, data,
                            data, null);
                }
            }
            break;
        case PushConsts.GET_CLIENTID:
            getClientID(context, bundle);
            break;
        default:
            break;
        }
    }

    @SuppressLint("NewApi")
    private void getClientID(Context context, Bundle bundle) {
        final String cid = bundle.getString("clientid", "0");
        PushManager.getInstance().pushCode(cid);
    }

    private void push(Context context, JSONObject root, int type) throws JSONException {
        String title = root.optString(SP_TITLE);
        String content = root.optString(SP_CONTENT);

        String tag = null;
        switch (type) {
        case GetuiConstant.OPEN_APP:// 启动应用
            break;
        case GetuiConstant.GOODS_DETAIL:// 打开商品详细页
            tag = root.getString("id");
            break;
        case GetuiConstant.OPEN_URL:// 打开链接
            tag = root.getString("url");
            break;
        case GetuiConstant.SUBJECT_DETAIL:// 打开专题详细页
            tag = root.getString("id");
            break;
        case GetuiConstant.SUBJECT_LIST:// 打开专题列表
            tag = root.getString("id");
            break;
        case GetuiConstant.GOODS_LIST:// 打开商品列表
            tag = root.getString("id");
            break;
        default:
            return;
        }

        putToNotification(context, type, title, content, tag);
    }

    private void putToNotification(Context context, int type, String title, String content, String tag) {
        Intent launcherIntent = new Intent(AppLauncherReceiver.ACTION);
        launcherIntent.putExtra(AppLauncherReceiver.IK_TYPE, type);
        launcherIntent.putExtra(AppLauncherReceiver.IK_TAG, tag);
        launcherIntent.putExtra(AppLauncherReceiver.IK_TITLE, title);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, launcherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationUtil
                .notifyNormal(context, (int) System.currentTimeMillis(), R.drawable.ic_launcher,
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), title, title,
                        content, pi);
    }
}
