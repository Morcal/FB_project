package com.feibo.snacks.manager.module.goods;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;


/**
 * Created by Jayden on 2015/10/14.
 */
public class H5WebManager extends AbsWebManager {

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener{
        void onStartLottery(int type);

        void setTitle(String title);
    }

    public class JSBridge extends BaseJSBridge{

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener)listener;
        }

        @JavascriptInterface
        public void startLottery(int type) {
            if (listener != null) {
                listener.onStartLottery(type);
            }
        }

        @JavascriptInterface
        public void setAppTitle(String title) {
            if (listener != null) {
                listener.setTitle(title);
            }
        }
    }
}
