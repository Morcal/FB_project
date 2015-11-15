package com.feibo.snacks.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * -----------------------------------------------------------
 * 版 权 ： BigTiger 版权所有 (c) 2015
 * 作 者 : BigTiger
 * 版 本 ： 1.0
 * 创建日期 ：2015/7/12 14:22
 * 描 述 ：
 * <p>
 * -------------------------------------------------------------
 */
public class FunctionUtil {
    /**
     * 提醒系统到指定路径扫描文件 ,将文件添加到媒体库
     * @param context
     * @param path
     */
    public static void notifyMediaScanner(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 让View获得焦点
     * @param view
     */
    public static void getFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.requestFocusFromTouch();
    };
}
