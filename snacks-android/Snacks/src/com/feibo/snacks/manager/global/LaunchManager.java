package com.feibo.snacks.manager.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.app.DirContext;
import com.feibo.snacks.app.DirContext.DirEnum;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.model.bean.SplashImage;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType.AppDataType;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.util.SPHelper;

import java.io.File;
import java.util.Date;

import fbcore.cache.image.ImageLoader.OnSavedListener;
import fbcore.log.LogUtil;
import fbcore.security.Md5;

public class LaunchManager {
    private static final String TAG = LaunchManager.class.getSimpleName();
    private static LaunchManager sManager;

    public static LaunchManager getInstance() {
        if (sManager == null) {
            sManager = new LaunchManager();
        }
        return sManager;
    }

    private AbsBeanHelper beanHelper;
    private SplashImage splashImage;

    private LaunchManager() {
        beanHelper = new AbsBeanHelper(AppDataType.LAUNCH) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getSplashImage(AppContext.SCREEN_WIDTH + "x" + AppContext.SCREEN_HEIGHT, listener);
            }
        };
    }

    // 获取闪图
    public SplashInfo getLaunchBitmap(Context context) {
        long currentTime = (new Date().getTime() / 1000);
        long startTime = SPHelper.getLaunchStartTime();
        long endTime = SPHelper.getLaunchEndTime();
        String imagePath = SPHelper.getLaunchPath();

        SplashInfo splashInfo = new SplashInfo();

        LogUtil.i(TAG, "currentTime:" + currentTime +
                "startTime:" + startTime +
                "endTime:" + endTime +
                "imagePath" + imagePath);

        // 如果闪图存在并且在指定的时间内
        if ((currentTime >= startTime) && (endTime >= currentTime) && (imagePath != null) && new File(imagePath)
                .exists()) {
            LogUtil.i(TAG, "getLaunchBitmap:" + imagePath);
            // 返回闪图
            splashInfo.type = SplashInfo.TYPE_NEW;
            splashInfo.bitmap = BitmapFactory.decodeFile(imagePath);
            return splashInfo;
        }
        // 返回默认图
        splashInfo.type = SplashInfo.TYPE_DEFAULT;

        if (AppContext.CHANNEL_NAME.equals("QQyingyongbao")) {
            splashInfo.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_yingyongbao_launcher);
        } else {
            splashInfo.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_launcher);
        }
        return splashInfo;
    }

    // 从网络加载闪图
    public void loadLaunchBitmap() {
        beanHelper.loadBeanData(true, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                splashImage = (SplashImage) beanHelper.getData();
                if (splashImage == null || TextUtils.isEmpty(splashImage.img.imgUrl)) {
                    return;
                }
                saveSplashImage();
            }

            @Override
            public void onFail(String failMsg) {
            }
        });
    }

    /**
     * 保存闪图
     */
    private void saveSplashImage() {
        LogUtil.i(TAG, "saveSplashImage");
        String fileName = Md5.digest32(splashImage.img.imgUrl);
        File dir = DirContext.getInstance().getDir(DirEnum.IMAGE);
        final String targetFilePath = new File(dir, fileName).getAbsolutePath();
        DataDiskProvider.getInstance().getImageLoader().saveImage(targetFilePath, splashImage.img.imgUrl, new OnSavedListener() {
            @Override
            public void onSaved(String srcPath, String destPath) {
                SPHelper.setLaunchStartTime(splashImage.startTime);
                SPHelper.setLaunchEndTime(splashImage.endTime);
                SPHelper.setLaunchPath(targetFilePath);
            }

            @Override
            public void onFail() {

            }
        });
    }

    public static class SplashInfo {
        public static final int TYPE_DEFAULT = 0;
        public static final int TYPE_NEW = 1;
        public static final int TYPE_YINGYONGBAO = 2;   // 应用宝首发启动图

        public int type;
        public Bitmap bitmap;
    }
}
