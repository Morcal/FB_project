package com.feibo.joke.cache;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import fbcore.cache.DiskCache;
import fbcore.cache.MemoryCache;
import fbcore.cache.image.ImageLoader;
import fbcore.cache.image.impl.ImageDiskCache;
import fbcore.cache.image.impl.LruMemoryCache;
import fbcore.log.LogUtil;
import fbcore.security.Md5;

import com.feibo.joke.utils.Util;

public class DataProvider {

    private static final String APP_PATH = "joke";
    private Disker disker;
    private ImageLoader imageLoader;

    private static DataProvider provider;
    private static Context context;

    public static void init(Context context) {
        DataProvider.context = context;
    }

    public synchronized static DataProvider getInstance() {
        if (provider == null) {
            provider = new DataProvider();
        }
        return provider;
    }

    private DataProvider() {
        disker = new Disker(context, APP_PATH);
        initImageCache(context);
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

        disker.putStringToDisker(content, key);
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

    public void clearAllCache() {
        disker.deleteCache();
    }

    private void initImageCache(Context context) {
        MemoryCache<String, Bitmap> memoryCache = new LruMemoryCache(1024 * 1024 * 4);
        final DiskCache<String, byte[]> diskCache = new ImageDiskCache(disker.imageDir.getAbsolutePath(),
                1024 * 1024 * 10);
        imageLoader = new ImageLoader(context.getResources(), memoryCache, diskCache);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public String getDiskSize() {
        return Util.bytes2Convert(disker.getSize());
    }
}
