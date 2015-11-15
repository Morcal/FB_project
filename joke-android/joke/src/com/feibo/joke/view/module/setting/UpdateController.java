package com.feibo.joke.view.module.setting;

import android.app.ProgressDialog;
import android.content.Context;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.UpdateManager;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.Util;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.util.RemindControl;
import com.feibo.joke.view.util.RemindControl.OnRemindListener;
import com.feibo.joke.view.util.ToastUtil;

public class UpdateController {

    private ProgressDialog dialog = null;

    private ProgressDialog downLoadAPKDialog = null;

    private UpdateManager updateManager;

    public UpdateController() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
    }

    public interface OnUpdateListener {
        void onStart();

        void onFinish();

        void onFail(String failMsg);
    }

    public void checkUpdate(final Context context, final boolean isAuto, final OnUpdateListener listener) {
        if(isAuto && !SPHelper.getCanRemideVersion(context)) {
            return;
        }
        if (!isAuto) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("检查更新中...");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        updateManager.updateApk(new LoadListener() {
            
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onFinish();
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if(StringUtil.isEmpty(updateManager.getUpdate().url)) {
                    MessageHintManager.setHasNewVersion(context, false);
                    if(!isAuto) {
                        RemindDialog.show(context, new RemindSource("目前已经是最新版本啦", "确定", ""), true);
                    }
                    return;
                }
                MessageHintManager.setHasNewVersion(context, true);
                //判断本地是否有安装包
                if (hasUpdateApk()) {
                    if(isAuto && !SPHelper.getCanRemideVersion(context)) {
                        return;
                    }
                    final RemindDialog d= RemindDialog.show(context, new RemindSource("本地有新版本", "马上安装", "暂不安装"), true);
                    d.setOnDialogClickListener(new OnDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            if(isAuto) {
                                SPHelper.setRemindeVersion(context, true);
                            }
                            Util.installApk(context, updateManager.updatePath());
                            MessageHintManager.setHasNewVersion(context, false);
                        }
                        
                        @Override
                        public void onCancel() {
                            if(isAuto) {
                                SPHelper.setRemindeVersion(context, false);
                            }
                        }
                    });
                    return;
                }
                if (AppContext.isWifiActive()) {
                    downloadApk(context, isAuto, true);
                } else {
                    showDownloadDialog(context, isAuto);
                }
            }
            
            @Override
            public void onFail(int code) {
                if (listener != null) {
                    listener.onFail("错误代码" + code);
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                showNoUpdateDialog(context, isAuto, listener);
            }
        });
    }

    private void showNoUpdateDialog(Context context, boolean isAuto, final OnUpdateListener listener) {
        if (isAuto) {
            return;
        }
        ToastUtil.showSimpleToast(context, "你已经拥有最新版本啦，么么哒");
        listener.onFinish();
    }

    protected void showDownloadDialog(final Context context, final boolean isAuto) {
        if(isAuto && !SPHelper.getCanRemideVersion(context)) {
            return;
        }
        RemindSource source = new RemindSource();
        source.title = context.getResources().getString(R.string.dialog_download_content);
        source.confirm = context.getResources().getString(R.string.dialog_download_confirm);
        source.cancel = context.getResources().getString(R.string.dialog_download_cancle);
        RemindControl.showDownloadRemind(context, source, new OnRemindListener() {
            @Override
            public void onConfirm() {
                if(isAuto) {
                    SPHelper.setRemindeVersion(context, true);
                }
                downloadApk(context, isAuto, false);
            }

            @Override
            public void onCancel() {
                if(isAuto) {
                    SPHelper.setRemindeVersion(context, false);
                }
            }
        });

    }

    protected void downloadApk(final Context context, final boolean isAuto, final boolean hasWifi) {
        if(!isAuto && downLoadAPKDialog == null) {
            downLoadAPKDialog = new ProgressDialog(context);
            downLoadAPKDialog.setMessage("下载新版本中...");
            downLoadAPKDialog.setCancelable(true);
            downLoadAPKDialog.setCanceledOnTouchOutside(true);
            downLoadAPKDialog.show();
        } else {
            if(!isAuto) {
                ToastUtil.showSimpleToast("正在下载中...");
            }
        }
        
        updateManager.downloadApk(new LoadListener() {
            @Override
            public void onSuccess() {
                if(downLoadAPKDialog != null) {
                    downLoadAPKDialog.dismiss();
                    downLoadAPKDialog = null;
                }
                if (hasUpdateApk()) {
                    if(hasWifi) {
                        Util.installApk(context, updateManager.updatePath());
                    } else {
                        RemindDialog rd = RemindDialog.show(context, new RemindSource("刚刚有Wifi， 帮你下载好啦", "马上安装", "稍后安装"));
                        rd.setOnDialogClickListener(new OnDialogClickListener() {
                            
                            @Override
                            public void onConfirm() {
                                Util.installApk(context, updateManager.updatePath());
                                ToastUtil.showSimpleToast("更新完成");
                                MessageHintManager.setHasNewVersion(context, false);
                            }
                            
                            @Override
                            public void onCancel() {
                                ToastUtil.showSimpleToast("暂不安装");
                            }
                        });
                    }
                } else {
                }
            }

            @Override
            public void onFail(int code) {
                if(downLoadAPKDialog != null) {
                    downLoadAPKDialog.dismiss();
                    downLoadAPKDialog = null;
                }
                if(!isAuto) {
                    ToastUtil.showSimpleToast("下载失败");
                }
            }
            
        });
    }

    protected boolean hasUpdateApk() {
        return updateManager.hasUpdateApk();
    }
}
