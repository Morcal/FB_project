package com.feibo.snacks.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by lidiqing on 15-9-17.
 */
public class IOUtil {

    public static File uri2FileImage(Context context, Uri bitmapUri) {
        String bitmapPath = "";
        if (!bitmapUri.toString().startsWith("file://")) {
            //前缀为content://
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(bitmapUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            bitmapPath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            //前缀为file://
            bitmapPath = bitmapUri.toString().replace("file://", "");
        }
        return new File(bitmapPath);
    }


}
