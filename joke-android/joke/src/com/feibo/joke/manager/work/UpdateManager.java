package com.feibo.joke.manager.work;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import fbcore.security.Md5;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;

import com.feibo.joke.app.DirContext;
import com.feibo.joke.app.DirContext.DirEnum;
import com.feibo.joke.dao.Dao;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.UpdateInfo;

public class UpdateManager {

    private final static String FORMAT = ".apk";
    private String url;
    private String saveFileName;
    private String saveDir;
    private static String filepath;
    private UpdateInfo update;
    private String version = null;

    public static final int TYPE_DEFAULT = 0; //自动
    public static final int TYPE_MANUAL = 1; //手动
    
    public UpdateManager() {
        
    }

    public void updateApk(final LoadListener listener) {
        JokeDao.getUpdateInfo(TYPE_DEFAULT, new IEntityListener<UpdateInfo>() {
            
            @Override
            public void result(Response<UpdateInfo> response) {
                update = response.data;
                if(response.rsCode == ReturnCode.RS_SUCCESS) {
                    listener.onSuccess();
                } else {
                    listener.onFail(response.rsCode);
                }
            }
        });
        
    }

    public String updatePath() {
        return filepath;
    }

    public boolean hasUpdateApk() {
        url = update.url;
        saveFileName = Md5.digest32(url) + FORMAT;
        saveDir = DirContext.getInstance().getDir(DirEnum.DOWNLOAD).getAbsolutePath();
        filepath = saveDir + File.separator + saveFileName;
        File file = new File(saveDir, saveFileName);
        return file.exists();
    }

    public UpdateInfo getUpdate() {
        return update;
    }

    public String getVersionCode(Context context) {
        if (version == null) {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(context.getPackageName(), 0);
            } catch (NameNotFoundException e1) {
            }
            version = info.versionName;
        }
        return version;
    }

    public void downloadApk(final LoadListener listener) {
        Dao.download(url, saveDir, saveFileName, new TaskHandler() {
            
            @Override
            public void onSuccess(Object result) {
                listener.onSuccess();
            }
            
            @Override
            public void onProgressUpdated(Object... params) {
                
            }
            
            @Override
            public void onFail(TaskFailure failure) {
                listener.onFail(0);
            }
        });
    }
}
