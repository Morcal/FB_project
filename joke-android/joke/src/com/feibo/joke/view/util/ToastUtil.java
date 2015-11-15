package com.feibo.joke.view.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.utils.StringUtil;

public class ToastUtil {
    private static Toast toast;

    private static void showToast(Context context, String content) {
        cancelToast();

        toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }

    public static void showSimpleToast(int stringRes) {
        showToast(AppContext.getContext(), AppContext.getContext().getResources().getString(stringRes));
    }

    public static void showSimpleToast(String string) {
        showToast(AppContext.getContext(), string);
    }

    public static void showSimpleToast(Context context, String string) {
        if(StringUtil.isEmpty(string)) {
            return;
        }
        showToast(context, string.trim());
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
