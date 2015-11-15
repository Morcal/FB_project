package com.feibo.snacks.model.dao.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.feibo.snacks.util.Util;

import java.io.IOException;

import fbcore.cache.DiskCache;
import fbcore.cache.MemoryCache;
import fbcore.cache.image.ImageLoader;
import fbcore.cache.image.impl.ImageDiskCache;
import fbcore.cache.image.impl.LruMemoryCache;
import fbcore.log.LogUtil;
import fbcore.security.Md5;

/**
 * Created by lidiqing on 15-9-7.
 */
public class DataDiskProvider {

    private static final String APP_PATH = "snacks";

    private Disker disker;
    private ImageLoader imageLoader;

    private static DataDiskProvider sProvider;
    private static Context sContext;

    public synchronized static DataDiskProvider getInstance() {
        if (sProvider == null) {
            sProvider = new DataDiskProvider();
        }
        return sProvider;
    }

    public static void init(Context context){
        sContext = context;
    }

    private void initImageCache(Context context) {
        MemoryCache<String, Bitmap> memoryCache = new LruMemoryCache(1024 * 1024 * 4);
        final DiskCache<String, byte[]> diskCache = new ImageDiskCache(disker.imageDir.getAbsolutePath(),
                1024 * 1024 * 10);
        imageLoader = new ImageLoader(context.getResources(), memoryCache, diskCache);
    }

    private DataDiskProvider() {
        disker = new Disker(sContext, APP_PATH);
        initImageCache(sContext);
    }

    public void putStringToDisker(String content, String url) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(url)) {
            return;
        }
        int startIndex = url.indexOf("srv");
        if (startIndex == -1) {
            return;
        }
        String key = Md5.digest32(url.substring(startIndex));
        LogUtil.i("Data", "put key : " + key + "startIndex : " + startIndex);

        disker.putStringToDisk(content, key);
    }

    public String getCacheFromDisker(String url) {
        try {
            int startIndex = url.indexOf("srv");
            if (startIndex == -1) {
                return null;
            }
            String key = Md5.digest32(url.substring(startIndex));
            return disker.getStringFromDisk(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCacheFromDiskByKey(String key) {
        try {
            return disker.getStringFromDisk(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putStringToDiskByKey(String content, String key) {
        disker.putStringToDisk(content, key);
    }

    public void clearCacheFromDiskByKey(String key) {
        disker.deleteDiskString(key);
    }

    public void clearAllCache() {
        disker.deleteCache();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public String getDiskSize() {
        return Util.bytes2Convert(disker.getSize());
    }
}
