package com.feibo.snacks.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Jayden on 2015/8/31.
 */
public abstract class AbsWebManager {

    private static final String JS_ANDROID = "bridge";

    private SimpleWebChromeClient chromeClient;
    private BaseWebViewListener listener;
    private WebView view;

    public void setup(final WebView view, BaseWebViewListener listener) {
        this.listener = listener;
        this.view = view;
        setupWebView();
    }

    @SuppressLint("JavascriptInterface")
    @SuppressWarnings("deprecation")
    private void setupWebView() {
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);

        chromeClient = new SimpleWebChromeClient();
        view.setWebViewClient(new SimpleWebViewClient());
        view.setWebChromeClient(chromeClient);
        view.addJavascriptInterface(getBridge(view.getContext(), listener), JS_ANDROID);
    }

    public void loadData(String data) {
        view.loadUrl(data);
    }

    public abstract BaseJSBridge getBridge(Context context, BaseWebViewListener listener);


    private class SimpleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            if (listener != null) {
                listener.onPageFinished(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (listener != null) {
                listener.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    private class SimpleWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }
}
