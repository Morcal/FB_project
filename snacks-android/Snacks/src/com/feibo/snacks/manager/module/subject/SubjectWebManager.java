package com.feibo.snacks.manager.module.subject;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;

/**
 * Created by Jayden on 15-07-21.
 */
public class SubjectWebManager extends AbsWebManager {

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener {

        void onClickItem(int id);

        void onClickCollect();
    }

    public static class JSBridge extends BaseJSBridge {

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener) listener;
        }

        @JavascriptInterface
        public void clickCollect() {
            //下面是处理点击收藏
            if (listener != null) {
                listener.onClickCollect();
            }
        }

        @JavascriptInterface
        public void clickItem(int id) {
            //下面是处理点击每个Item跳转详情页
            if (listener != null) {
                listener.onClickItem(id);
            }
        }

        @JavascriptInterface
        public void clickShare() {
            //下面是处理点击分享
        }
    }

}
