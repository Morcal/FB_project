package com.feibo.snacks.manager;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * Created by Jayden on 2015/8/31.
 */
public interface BaseWebViewListener {

    void onPageStarted(WebView view, String url, Bitmap favicon);

    void onPageFinished(WebView view, String url);

    void onClickShare(int id,String desc,String title,String imgUrl,String contentUrl,int type);
}
