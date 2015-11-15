package com.feibo.snacks.manager.module.coupon;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;

/**
 * Created by Jayden on 2015/9/2.
 */
public class SpecialOfferWebManager extends AbsWebManager {

    public static final int GOODS_LIST = 0;
    public static final int SPECIAL_SELLING = 1;

    public static final int STATUS_RECEIVE = 0; //领取
    public static final int STATUS_HAS_RECEIVED = 1;    //已领取
    public static final int STATUS_ROB = 2;    //抢光
    public static final int STATUS_END = 3; //结束
    public static final int STATUS_NO_PAID = 4; //未发放

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener{

        void onClickFullMail(int type,String info);

        void onClickDiscoupon(int id);

        void onCouponBtnOnTapAction(int id,int type,int status);
    }

    public static class JSBridge extends BaseJSBridge{

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener)listener;
        }

        @JavascriptInterface
        public void clickFullMail(int type,String info) {
            //下面是处理点击满包邮
            //type: 0:跳转供应商商品列表，info：代表供应商id；1、跳转平台特卖页面；
            if (listener != null) {
                listener.onClickFullMail(type,info);
            }
        }

        @JavascriptInterface
        public void clickClickDiscoupon(int id) {
            //下面是处理点击某张优惠券
            if (listener != null) {
                listener.onClickDiscoupon(id);
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
