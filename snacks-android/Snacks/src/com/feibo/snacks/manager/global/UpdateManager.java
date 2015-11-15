package com.feibo.snacks.manager.global;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.app.DirContext;
import com.feibo.snacks.app.DirContext.DirEnum;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.UpdateInfo;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.download.DownloadTask;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType.AppDataType;
import com.feibo.snacks.view.module.person.setting.UpdateController.OnUpdateListener;

import java.io.File;

import fbcore.security.Md5;
import fbcore.task.AsyncTaskManager;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;

public class UpdateManager {

    private final static String FORMAT = ".apk";

    private AbsBeanHelper updateHelper; //应用更新

    private String url;
    private String saveDir;
    private String filepath;
    private String saveFileName;

    private UpdateInfo update;

    public UpdateManager() {
        updateHelper = new AbsBeanHelper(AppDataType.UPDATE) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getUpdateInfo(listener);
            }
        };
    }

    public String updatePath() {
        return filepath;
    }

    public void updateApk(final OnUpdateListener listener) {
        listener.onStart();
        updateHelper.loadBeanData(false, new AbsLoadHelper.HelperListener() {

            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onFinish();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        });
    }

    public boolean hasUpdateApk() {
        url = update.url;
        saveFileName = Md5.digest32(url) + FORMAT;
        saveDir = DirContext.getInstance().getDir(DirEnum.DOWNLOAD).getAbsolutePath();
        filepath = saveDir + File.separator + saveFileName;
        File file = new File(saveDir, saveFileName);
        return file.exists();
    }

    public void downloadApk(final ILoadingListener listener) {
        final DownloadTask task = new DownloadTask(url, saveDir, saveFileName);
        AsyncTaskManager.INSTANCE.execute(task, new TaskHandler() {
            @Override
            public void onSuccess(Object result) {
                listener.onSuccess();
            }

            @Override
            public void onProgressUpdated(Object... params) {

            }

            @Override
            public void onFail(TaskFailure failure) {
                listener.onFail(task.getFailMsg());
            }
        });
    }

    public UpdateInfo getUpdate() {
        update = (UpdateInfo) updateHelper.getData();
        return update;
    }

    public String getVersionCode() {
        return AppContext.APP_VERSION_NAME;
    }
}
