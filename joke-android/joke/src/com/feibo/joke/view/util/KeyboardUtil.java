package com.feibo.joke.view.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import fbcore.utils.Utils;

public class KeyboardUtil {

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(EditText view) {
        Utils.hideInputKeyboard(view);
    }
    /**
     * 显示软键盘
     */
    public static void showKeyboard(EditText view) {
        Utils.showInputKeyboard(view);
    }
    
    /**
     * 获取输入法是否显示
     * @return
     */
    public static Boolean isKeyboardShow(EditText view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)){  
             imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);  
             return true;
        }
        return false;
    }
    
}
