package com.feibo.snacks.view.module.goods.goodsdetail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.LoadingViewHelper;

/**
 * Created by Jayden on 2015/9/2.
 */
public class WebFragment extends BaseTitleFragment {

    public static final String URL = "url";
    public static final String TITLE = "title";

    private View rootView;
    private WebView webView;

    private String webUrl;
    private String title;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        title = args.getString(TITLE);
        webUrl = args.getString(URL);
    }

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail_subject_h5, null);

        setTitle();
        initListener();
        initWidget();
        return rootView;
    }

    private void setTitle() {
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        titleView.setText(title);
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initWidget() {
        webView = (WebView) rootView.findViewById(R.id.subject_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(webUrl);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            RemindControl.showProgressDialog(getActivity(), "", null);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            RemindControl.hideProgressDialog();
        }

        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            webView.clearView();
            UIUtil.setViewGone(webView);
            LoadingViewHelper loadingViewHelper = LoadingViewHelper.generateOnParentAtPosition(0, (ViewGroup) rootView, 0);
            loadingViewHelper.fail(null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppContext.isNetworkAvailable()) {
                        RemindControl.showSimpleToast(getActivity(), getString(R.string.not_network));
                        return;
                    }
                    webView.loadUrl(webUrl);
                    UIUtil.setViewVisible(webView);
                }
            });

        }
    }
}
