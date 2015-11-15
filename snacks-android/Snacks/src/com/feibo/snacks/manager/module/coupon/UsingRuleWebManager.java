package com.feibo.snacks.manager.module.coupon;

import android.content.Context;

import com.feibo.snacks.manager.AbsWebManager;
import com.feibo.snacks.manager.BaseJSBridge;
import com.feibo.snacks.manager.BaseWebViewListener;

/**
 * Created by Jayden on 2015/9/2.
 */
public class UsingRuleWebManager extends AbsWebManager {

    @Override
    public BaseJSBridge getBridge(Context context, BaseWebViewListener listener) {
        return new JSBridge(context, listener);
    }

    public interface WebViewListener extends BaseWebViewListener{

    }

    public static class JSBridge extends BaseJSBridge{

        WebViewListener listener;

        public JSBridge(Context context, BaseWebViewListener listener) {
            super(context, listener);
            this.listener = (WebViewListener)listener;
        }
    }
}
