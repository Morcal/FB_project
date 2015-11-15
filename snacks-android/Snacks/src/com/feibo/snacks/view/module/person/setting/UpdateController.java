package com.feibo.snacks.view.module.person.setting;

import android.app.ProgressDialog;
import android.content.Context;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.UpdateInfo;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.UpdateManager;
import com.feibo.snacks.util.Util;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.RemindControl.OnRemindListener;
import com.feibo.snacks.view.dialog.RemindDialog.RemindSource;

public class UpdateController {

    private ProgressDialog dialog = null;

    private UpdateManager updateManager;

    public UpdateController() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
    }

    private void showUpdateDialog(final Context context, UpdateInfo updateInfo, final boolean isAuto,
            final OnUpdateListener listener) {
        RemindControl.showUpdateRemind(context, updateInfo, new OnRemindListener() {
            @Override
            public void onConfirm() {
                if (hasUpdateApk()) {
                    Util.installApk(context, updateManager.updatePath());
                    return;
                }
                if (AppContext.isWifiActive()) {
                    downloadApk(context);
                } else {
                    showDownloadDialog(context);
                }
                if (listener != null) {
                    listener.onFinish();
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });
    }

    private void showNoUpdateDialog(Context context, boolean isAuto, final OnUpdateListener listener) {
        if (isAuto) {
            return;
        }
        RemindControl.showSimpleToast(context, R.string.update_no_new_version);
        listener.onFinish();
    }

    protected void downloadApk(final Context context) {
        updateManager.downloadApk(new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (hasUpdateApk()) {
                    Util.installApk(context, updateManager.updatePath());
                }
            }

            @Override
            public void onFail(String failMsg) {
                RemindControl.showSimpleToast(context, failMsg);
            }
        });
    }

    protected void showDownloadDialog(final Context context) {
        RemindSource source = new RemindSource();
        source.contentStr = context.getResources().getString(R.string.dialog_download_content);
        source.confirm = context.getResources().getString(R.string.dialog_download_confirm);
        source.cancel = context.getResources().getString(R.string.dialog_download_cancle);
        RemindControl.showRemindDialog(context, source, new OnRemindListener() {
            @Override
            public void onConfirm() {
                downloadApk(context);
            }

            @Override
            public void onCancel() {

            }
        });

    }

    protected boolean hasUpdateApk() {
        return updateManager.hasUpdateApk();
    }

    public void checkUpdate(final Context context, final boolean isAuto, final OnUpdateListener listener) {
        updateManager.updateApk(new OnUpdateListener() {
            @Override
            public void onStart() {
                if (!isAuto) {
                    dialog = new ProgressDialog(context, R.style.CustomProgressDialog);
                    dialog.setMessage(context.getString(R.string.checking_version));
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onFinish();
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                UpdateInfo updateInfo = updateManager.getUpdate();
                showUpdateDialog(context, updateInfo, isAuto, listener);
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                showNoUpdateDialog(context, isAuto, listener);
            }
        });
    }

    public interface OnUpdateListener {
        void onStart();

        void onFinish();

        void onFail(String failMsg);
    }
}
