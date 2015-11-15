package com.feibo.snacks.view.module.coupon;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.module.coupon.CouponUsingRuleManager;
import com.feibo.snacks.manager.module.coupon.UsingRuleWebManager;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.ShareUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

/**
 * Created by Jayden on 2015/9/2.
 */
public class UsingRuleFragment extends BaseTitleFragment {

    private static final String USING_RULE_TITLE = "使用规则";

    private View rootView;
    private WebView webView;

    private String webUrl;
    private CouponUsingRuleManager manager;
    private UsingRuleWebManager webManager;

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
        initManager();
        return rootView;
    }

    private void setTitle() {
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        titleView.setText(USING_RULE_TITLE);
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
    }

    private void initManager() {
        AbsLoadingView absLoadingView = new AbsLoadingView() {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                initManager();
            }

            @Override
            public void fillData(Object detail) {
                if (detail != null && detail instanceof UrlBean) {
                    webUrl = ((UrlBean) detail).url;
                    initWebManager();
                }
            }
        };
        absLoadingView.setLauncherPositon(2);
        manager = new CouponUsingRuleManager(absLoadingView);
        manager.loadData();
    }

    private void initWebManager() {
        if (webManager == null) {
            webManager = new UsingRuleWebManager();
            webManager.setup(webView, new UsingRuleWebManager.WebViewListener() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (getActivity() == null) {
                        return;
                    }
                    RemindControl.showProgressDialog(getActivity(), "", null);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(getActivity() == null){
                        return;
                    }
                    RemindControl.hideProgressDialog();
                }

                @Override
                public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int type) {
                    webView.post(() -> handleClickShare(id, desc, title, imgUrl, contentUrl,type));
                }


            });
            webView.loadUrl(webUrl);
        }
    }

    private void handleClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int type) {
        ShareUtil shareUtil = new ShareUtil();
        shareUtil.share(getActivity(), desc, title, imgUrl, contentUrl, new ShareUtil.IShareResult() {
            @Override
            public void onShareResult(int result) {
                //TODO 统计分享
            }
        });
    }
}
