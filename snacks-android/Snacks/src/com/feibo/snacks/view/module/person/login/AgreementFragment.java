package com.feibo.snacks.view.module.person.login;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用户协议页
 * Created by lidiqing on 15-9-10.
 */
public class AgreementFragment extends BaseTitleFragment {

    public static final String PARAM_URL = "url";

    @Bind(R.id.web)
    WebView webView;

    private String url;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_agreement, null);
        ButterKnife.bind(this, view);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                RemindControl.showProgressDialog(getActivity(), "加载中...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                RemindControl.hideProgressDialog();
            }
        });
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        super.initTitleBar(titleBar);
        ((TextView) titleBar.title).setText("用户协议");
        titleBar.leftPart.setOnClickListener((View view) -> {
            getActivity().finish();
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getActivity().getIntent().getExtras();
        url = bundle.getString(PARAM_URL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
