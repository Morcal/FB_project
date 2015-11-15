package com.feibo.snacks.manager.module.coupon;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;

/**
 * Created by Jayden on 2015/9/2.
 */
public class CouponDetailWebManager extends AbsWebManager {

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener{

        void onLookSpecialGoods(int type,String info);

        void onCouponBtnOnTapAction(int id,int type,int status);
    }

    public static class JSBridge extends BaseJSBridge{

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener)listener;
        }

        @JavascriptInterface
        public void clickLookSpecialGoods(int type,String info) {
            //下面是处理点击查看全部促销商品
            //type: 0:跳转供应商商品列表，info：代表供应商id；1、跳转平台特卖页面；
            if (listener != null) {
                listener.onLookSpecialGoods(type,info);
            }
        }

        @JavascriptInterface
        public void clickReceive(int type,String info) {
        }

        @JavascriptInterface
        public void achieveCouponAction(int id,int type,int status) {
            //type 0 领取单张优惠券，1 领取优惠券集合
            //status下面是处理点击领取、已领取、被抢光、时间结束、还没有开始发放，type分别对应0，1，2，3，4
            if (listener != null) {
                listener.onCouponBtnOnTapAction(id, type, status);
            }
        }
    }
}
