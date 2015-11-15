package com.feibo.joke.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * -----------------------------------------------------------
 * 版 权 ： BigTiger 版权所有 (c) 2015
 * 作 者 : BigTiger
 * 版 本 ： 1.0
 * 创建日期 ：2015/7/12 14:50
 * 描 述 ：
 * <p>
 * -------------------------------------------------------------
 */
public class ScreenShot {

    public static Bitmap screenshot(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(false);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //Bitmap bmp = view.getDrawingCache();
        //Bitmap bmp = convertViewToBitmap(view);
        Bitmap bitmap = view.getDrawingCache(true);
        Bitmap bmp = duplicateBitmap(bitmap);
        return UIUtil.comPoseBitmap(bmp);
    }

    public static void saveScreenshot(Bitmap bitmap, String filePath) {
        if (bitmap == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);

                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap convertViewToBitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);

        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            canvas.clipRect(rect);
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }
        return bmpDest;
    }
}
