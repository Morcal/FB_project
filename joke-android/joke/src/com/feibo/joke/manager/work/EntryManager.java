package com.feibo.joke.manager.work;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.SplashImage;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.StringUtil;

import java.io.File;

import fbcore.cache.image.ImageLoader;
import fbcore.security.Md5;

/**
 * 处理第一次进入APP的业务逻辑，如显示加载图
 *
 * @author Lidiqing
 */
public class EntryManager {

    public EntryManager() {
    }

    //获取屏幕分辨率:
    private String getDisplayMetrics(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int w = mDisplayMetrics.widthPixels;
        int h = mDisplayMetrics.heightPixels;
        return String.valueOf(w) + "x" + String.valueOf(h);
    }

    /**
     * 是否显示启动图
     */
    public SplashImage sholdShowLaunchImage(Context context) {
        SplashImage img = SPHelper.getLaunchImage(context);
        long currentTime = System.currentTimeMillis() / 1000;
        if (img != null && (img.startTime <= currentTime && img.endTime >= currentTime)) {
            return img;
        }
        if (img != null && img.endTime < currentTime) {
            SPHelper.setLaunchImage(context, null);
        }
        return null;
    }

    /**
     * 从网络读取启动图
     */
    public void readSplashImageFromNet(Activity activity) {
        String radio = getDisplayMetrics(activity);
        JokeDao.getSplashImage(radio, new IEntityListener<SplashImage>() {
            @Override
            public void result(Response<SplashImage> entity) {
                if (entity.rsCode == ReturnCode.RS_SUCCESS) {
                    if (AppContext.getContext() == null) {
                        return;
                    }
                    SplashImage image = entity.data;
                    String imgUrl = image.image.url;
                    if (image != null && image.image != null && !StringUtil.isEmpty(imgUrl)) {
                        SplashImage oldImage = SPHelper.getLaunchImage(AppContext.getContext());
                        if (oldImage == null || !oldImage.image.url.equals(imgUrl)) {
                            downloadImage(image);
                        }
                    } else {
                        SPHelper.setLaunchImage(AppContext.getContext(), null);
                    }
                } else {
                    SPHelper.setLaunchImage(AppContext.getContext(), null);
                }
            }

        }, true);
    }

    private void downloadImage(final SplashImage splashImage) {
        String fileName = Md5.digest32(splashImage.image.url);
        File dir = DirContext.getInstance().getDir(DirContext.DirEnum.DOWNLOAD);
        final String targetFilePath = new File(dir, fileName).getAbsolutePath();
        DataProvider.getInstance().getImageLoader().saveImage(targetFilePath, splashImage.image.url, new ImageLoader.OnSavedListener() {
            @Override
            public void onSaved(String srcPath, String destPath) {
                splashImage.imagePath = targetFilePath;
                SPHelper.setLaunchImage(AppContext.getContext(), splashImage);
            }

            @Override
            public void onFail() {

            }
        });
    }

    public Bitmap getLaunchBitmap(Context context, SplashImage splashImage) {
        if (splashImage != null && splashImage.imagePath != null && new File(splashImage.imagePath).exists()) {
            // 返回闪图
            return BitmapFactory.decodeFile(splashImage.imagePath);
        }

        String channel = null;
        try {
            ApplicationInfo appInfo = AppContext.getContext().getPackageManager()
                    .getApplicationInfo(AppContext.getContext().getPackageName(), PackageManager.GET_META_DATA);

            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
        }

        // 返回默认图
        int d = R.drawable.bg_launch;
        if (!StringUtil.isEmpty(channel) && SPHelper.showSplashLaunch(context)) {
            // 应用宝特殊启动图
            if(channel.equals("QQyingyongbao")) {
                SPHelper.cancelSplashLaunch(context);
                d = R.drawable.launch_yyb;
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), d);
    }

}