package com.feibo.joke.utils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;

import com.feibo.joke.app.AppContext;

public class Util {

    public static int getAppVersionCode() {
        Context context = AppContext.getContext();
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 1).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 安装一个apk的安装包
     */
    public static void installApk(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    /**
     * 字节转换成相应大小的MB,KB
     *
     * @param bytes
     * @return
     */
    public static String bytes2Convert(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal gbyte = new BigDecimal(1024 * 1024 * 1024);
        float returnValue = filesize.divide(gbyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue >= 1) {
            return (returnValue + "GB");
        }
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue >= 1) {
            return (returnValue + "MB");
        }
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte).intValue();
        return (returnValue + "KB");
    }

    /**生日转换年龄
     *
     * @param s 生日，仅支持四位数字年份开头的生日，如“1980”
     * @return
     */
    public static int birth2Age(String s) {
        if(!TextUtils.isEmpty(s)){
            return 0;
        }
        Calendar mycalendar = Calendar.getInstance();// 获取现在时间
        String year = String.valueOf(mycalendar.get(Calendar.YEAR));
        int now = Integer.parseInt(year);
        int birthYear = Integer.parseInt(s.substring(0, 4));
        if (now < birthYear) {
            return 0;
        }
        return now - birthYear;

    }
}
