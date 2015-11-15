package com.feibo.snacks.view.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.feibo.snacks.model.bean.ActiviteInfo;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.module.person.orders.shoppingcart.CartActivity;

public class LaunchUtil {

    public static final String FRAGMENT = "fragment";
    public static final int COLLECT_REQUEST_CODE = 0x222;
    public static final int SUBJECT_PAGE_REQUEST_CODE = 0x333;
    public static final int COLLECT_SUB_REQUEST_CODE = 0x666;
    public static final int COUPON_REQUEST_CODE = 0x999;
    public static final int LOGIN_REQUEST_CODE = 0x120;
    public static final int SUBJECT_REQUEST_CODE = 0x777;
    public static final int ADDRESS_SELECT_CODE = 0X888;
    public static final int REQUEST_ORDERS_CONFIRM_PAY = 0X111;

    public static final int RESULT_ORDERS_CONFIRM_PAY = 0X111;

    public static final int REQUEST_ORDERS_DETAIL = 0x444;
    public static final int RESULT_ORDERS_DETAIL = 0x444;

    public static final int REQUEST_LOGIN = 0x555;
    public static final int RESULT_LOGIN = 0x555;

    public static final int REQUEST_ORDERS_COMMENT = 0X123;

    private static final String TITLE = "美味详情";

    //TODO此部分需要优化
    public static void launchActivityForResult(Context context, Class<? extends BaseActivity> cls,
                                      Class<? extends BaseFragment> clsFragment, Bundle bundle) {
        launchActivityForResult(COLLECT_REQUEST_CODE,context,cls,clsFragment,bundle);
    }

    public static void launchActivityForResultByIntent(int requestCode, Context context, Class<? extends BaseActivity> cls,
                                               Class<? extends BaseFragment> clsFragment, Intent intent){
        if(!canClick()){
            return;
        }

        intent.setClass(context, cls);
        intent.putExtra(FRAGMENT, clsFragment);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void launchActivityForResult(int requestCode,Context context, Class<? extends BaseActivity> cls,
                                      Class<? extends BaseFragment> clsFragment, Bundle bundle) {
    	 if(!canClick()){
             return;
         }

        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(FRAGMENT, clsFragment);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void launchActivityForResult(Context context, Class<? extends BaseActivity> cls,
                                               Class<? extends BaseFragment> clsFragment, Bundle bundle, int resultCode) {
        if(!canClick()){
            return;
        }

        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(FRAGMENT, clsFragment);
        ((Activity)context).startActivityForResult(intent, resultCode);
    }

    public static void launchSubDetailForResult(int requestCode,Context context, Class<? extends BaseActivity> cls,
                                               Class<? extends BaseFragment> clsFragment, Bundle bundle) {
    	 if(!canClick()){
             return;
         }

        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(FRAGMENT, clsFragment);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void launchActivity(Context context, Class<? extends BaseActivity> cls,
            Class<? extends BaseFragment> clsFragment, Bundle bundle) {
    	 if(!canClick()){
             return;
         }

        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(FRAGMENT, clsFragment);
        context.startActivity(intent);
    }

    public static void launchAppActivity(Context context, Class<? extends BaseActivity> cls,
            Class<? extends BaseFragment> clsFragment, Bundle bundle) {
    	 if(!canClick()){
             return;
         }

        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FRAGMENT, clsFragment);
        context.startActivity(intent);
    }
    
    public static void launchShoppingActivity(Context context, String ShoppingUrl, ActiviteInfo info) {
    	 if(!canClick()){
             return;
         }
        Intent intent = new Intent(context, WebAcitivity.class);
        intent.putExtra(WebAcitivity.TITLE, TITLE);
        intent.putExtra(WebAcitivity.SHOPPING_URL, ShoppingUrl);
        intent.putExtra(WebAcitivity.ACTIVITE_INFO, info);
        context.startActivity(intent);
    }
    
    private static long lastClickTime = 0;

    //判断是否重复点击
    private static boolean canClick(){
        if(lastClickTime == 0){
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if((currentClickTime - lastClickTime) < 500){
                return false;
            }
            lastClickTime = currentClickTime;
        }
        return true;
    }

    public static void launchCartActivityForResult(int requestCode,Context context) {
        if(!canClick()){
            return;
        }
        Intent intent = new Intent(context, CartActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void launchCartActivity(Context context) {
        if(!canClick()){
            return;
        }
        Intent intent = new Intent(context, CartActivity.class);
        context.startActivity(intent);
    }

    public static void launchActivityForResult(int requestCode,Context context, Class<? extends BaseActivity> cls) {
        if(!canClick()){
            return;
        }

        Intent intent = new Intent(context,cls);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void launchMainActivity(Context context, int scenceLocation) {
        if(!canClick()){
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.SCENCE_LOCATION, scenceLocation);
        context.startActivity(intent);
    }

}
