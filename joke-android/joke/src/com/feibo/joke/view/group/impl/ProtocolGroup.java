package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fbcore.task.AsyncTaskManager;
import fbcore.task.SyncTask;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;
import fbcore.task.toolbox.GetStringTask;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.view.group.AbsLoadingGroup;
import com.feibo.joke.view.util.ToastUtil;

public class ProtocolGroup extends AbsLoadingGroup{

    private WebView webView;
    private Context context;
    private String webUrl;

    public ProtocolGroup(Context context, String webUrl) {
        this.context = context;
        this.webUrl = webUrl;
    }
    
    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {
        webView.setVisibility(loadingHelpVisible ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onLoadingHelperFailClick() {
        initData();
    }

    @Override
    public void onCreateView() {
        ViewGroup parent = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.fragment_protocol, null);
        setRoot(parent);
        
        webView = (WebView) parent.findViewById(R.id.webview);
        
        setWebView();
        initData();
    }
    
    private void initData() {
        launchLoadHelper();
        if (!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.not_network));
            showFailMessage(ReturnCode.NO_NET);
            return;
        }
        loadData(new TaskHandler() {
            
            @Override
            public void onSuccess(Object result) {
                if(result == null) {
                    showFailMessage(ReturnCode.RS_INTERNAL_ERROR);
                    return;
                }
                ProtocolGroup.this.hideLoadHelper();
                webView.loadDataWithBaseURL("about:blank", result.toString(), "text/html",
                        "utf-8", null);
            }
            
            @Override
            public void onProgressUpdated(Object... params) {
            }
            
            @Override
            public void onFail(TaskFailure failure) {
                showFailMessage(ReturnCode.RS_INTERNAL_ERROR);
            }
        });
    }
    
    private void loadData(TaskHandler task) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
                return new GetStringTask(webUrl).execute();
            }
        }, task);
    }
    
    private void setWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(PluginState.ON);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        webView.setOnKeyListener(new OnKeyListener() {
            boolean backFlag;
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        backFlag = true;
                        return true;
                    }
                }
                if(backFlag){
                    backFlag = false;
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroyView() {
    }
    
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

    }

}
