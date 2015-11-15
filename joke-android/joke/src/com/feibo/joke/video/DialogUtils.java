package com.feibo.joke.video;

import android.app.ProgressDialog;
import android.content.Context;

import fbcore.log.LogUtil;

public class DialogUtils {
    private static final String TAG = DialogUtils.class.getSimpleName();

    private static ProgressDialog sProgressDialog;

    public static void showProgressDialog(Context context, CharSequence msg, boolean cancelable) {
        sProgressDialog = new ProgressDialog(context);
        if (msg != null) {
            sProgressDialog.setMessage(msg);
        }
        sProgressDialog.setCancelable(cancelable);
        try {
            sProgressDialog.show();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public static void showProgressDialog(Context context) {
        showProgressDialog(context, null, false);
    }

    public static void hideProgressDialog() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }
}
