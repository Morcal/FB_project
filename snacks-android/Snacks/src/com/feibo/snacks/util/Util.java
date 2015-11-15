package com.feibo.snacks.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Base64;

import com.feibo.snacks.app.AppContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("deprecation")
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

    public static void copyText(Context context,String text) {
        ClipboardManager copy = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(text);
    }

    private static IWeiXinResult listener;

    public interface IWeiXinResult {
        public void result(String result);
    }

    public static void notify(String result) {
        if (listener == null) {
            return;
        }
        listener.result(result);
    }

    public static void setListener(IWeiXinResult listener) {
        Util.listener = listener;
    }

    /**
     * 将bitmap转换成base64字符串
     *
     * @param bitmap
     * @return base64 字符串
     */
    public static String bitmaptoString(Bitmap bitmap, int bitmapQuality) {
// 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
    /**
     * 将base64转换成bitmap图片
     *
     * @param string base64字符串
     * @return bitmap
     */
    public static Bitmap stringtoBitmap(String string) {
// 将字符串转换成Bitmap类型
        if(TextUtils.isEmpty(string)){
            return null;
        }
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static <E> String createIdString(List<E> ids) {
        if (ids == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int endIndex = ids.size() - 1;
        for (int x = 0; x < ids.size(); x++) {
            builder.append(ids.get(x));
            if (x != endIndex) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
