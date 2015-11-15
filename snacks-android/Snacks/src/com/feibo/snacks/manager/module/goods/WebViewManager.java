package com.feibo.snacks.manager.module.goods;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;

/**
 * Created by Jayden on 15-07-21.
 */
public class WebViewManager extends AbsWebManager{

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener{

        void onClickCollect();

        void onClickGuessLike(int id);

        void onClickMoreComment(String moreUrl,int totalComment);

        void onPromotionEnd();

        void onClickFullMail(int type,int supplierId);
    }

    public static class JSBridge extends BaseJSBridge{

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener)listener;
        }

        @JavascriptInterface
        public void clickFullMail(int type,int id) {
            //下面是处理点击满包邮
            if (listener != null) {
                listener.onClickFullMail(type,id);
            }
        }

        @JavascriptInterface
        public void clickCollect() {
            //下面是处理点击收藏
            if (listener != null) {
                listener.onClickCollect();
            }
        }

        @JavascriptInterface
        public void clickMoreComment(String moreUrl,int totalComment) {
            //下面是处理点击查看更多评论
            if (listener != null) {
                listener.onClickMoreComment(moreUrl,totalComment);
            }
        }

        @JavascriptInterface
        public void clickGuessLike(int id) {
            //下面是处理点击猜你喜欢
            if (listener != null) {
                listener.onClickGuessLike(id);
            }
        }
        @JavascriptInterface
        public void promotionEnd() {
            //下面是处理点击猜你喜欢
            if (listener != null) {
                listener.onPromotionEnd();
            }
        }

    }
}
