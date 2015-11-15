package com.feibo.joke.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.joke.R;

public class WebFragment extends BaseTitleFragment{

    public static final String TITLE = "title";
    public static final String WEB_URL = "web_url";

    private View root;
    private ProgressBar progressBar;
    private WebView webView;

    public static Bundle buildWebArgs(String title, String web) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(WEB_URL, web);
        return bundle;
    }

    @Override
    public View containChildView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_webview, null);
        initWidget();
        Bundle args = getArguments();
        String url = args.getString(WEB_URL);
        initWebView(url);
        return root;
    }


    private void initWidget() {
        progressBar = (ProgressBar) root.findViewById(R.id.webview_progress);
        webView = (WebView) root.findViewById(R.id.fragment_webview);
    }

    private void initWebView(String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(url);
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitlebar() {
        Bundle args = getArguments();
        String title = args.getString(TITLE);
        getTitleBar().rightPart.setVisibility(View.GONE);
        ((TextView)getTitleBar().title).setText(title.isEmpty() ? "" : title);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }


    @Override
    public void onReleaseView() {
        // TODO Auto-generated method stub

    }
}
