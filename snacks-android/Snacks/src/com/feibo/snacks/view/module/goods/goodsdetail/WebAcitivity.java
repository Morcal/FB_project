package com.feibo.snacks.view.module.goods.goodsdetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.CollectSubjectManager;
import com.feibo.snacks.manager.global.Share4GiftConfigManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.goods.H5WebManager;
import com.feibo.snacks.model.bean.ActiviteInfo;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.coupon.MyCouponFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.ShareUtil;
import com.feibo.snacks.view.widget.SlidingFinishView;
import com.feibo.social.manager.SocialComponent;

/**
 * @author Jayden
 */
public class WebAcitivity extends BaseActivity {

    protected static final String TAG = WebAcitivity.class.getSimpleName();
    public static final String SHOPPING_URL = "url";
    public static final String TITLE = "title";
    public static final String ACTIVITE_INFO = "activite_info";//拍下立减信息

    private WebView webView;
    private ProgressBar progressBar;

    private String url;

    private View acitivityParent;
    private TextView acitivityNote;
    private TextView acitivityEndTime;
    private  TextView titleTv;
    private int type = 0 ;// 0表示大转盘，1表示大礼包

    private ActiviteInfo activiteInfo;
    private long endTime;
    private int REDUCE_TIME = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REDUCE_TIME) {
                endTime--;
                if (endTime >= 0) {
                    setEndTimeContent();
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        SlidingFinishView layout = (SlidingFinishView) LayoutInflater.from(this).inflate(
                R.layout.layout_sliding_finish, null);
        layout.attachToActivity(this);
        setContentView(R.layout.fragment_webview);
        webView = (WebView) findViewById(R.id.fragment_webview);
        progressBar = (ProgressBar) findViewById(R.id.webview_progress);
        acitivityParent = findViewById(R.id.webview_footer);
        acitivityNote = (TextView) findViewById(R.id.taobao_footer_activite_note);
        acitivityEndTime = (TextView) findViewById(R.id.taobao_footer_end_time);

        View headerView = findViewById(R.id.webview_title);
        View leftView = headerView.findViewById(R.id.head_left);

        leftView.setOnClickListener(v -> finish());
        String title = getIntent().getStringExtra(TITLE);
        titleTv = (TextView) headerView.findViewById(R.id.head_title);
        titleTv.setText(TextUtils.isEmpty(title) ? "" : title);

        url = getIntent().getStringExtra(SHOPPING_URL);
        activiteInfo = (ActiviteInfo) getIntent().getSerializableExtra(ACTIVITE_INFO);
        refreshactivityView();
        setWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        handler = null;
    }

    private void refreshactivityView() {
        if (activiteInfo == null ||  activiteInfo.status == ActiviteInfo.GONE) {
            acitivityParent.setVisibility(View.GONE);
        } else {
            acitivityParent.setVisibility(View.VISIBLE);
            String note = "拍下自动变成" + activiteInfo.price + "元哦~";
            acitivityNote.setText(note);
            endTime = Long.parseLong(TimeUtil.getTime(activiteInfo.endTime));
            setEndTimeContent();
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private void setEndTimeContent() {
        String endTimeContent = TimeUtil.transDetailTime(endTime);
        acitivityEndTime.setText("仅剩" + endTimeContent);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void setWebView() {
        H5WebManager h5WebManager = new H5WebManager();
        h5WebManager.setup(webView, new H5WebManager.WebViewListener() {
            @Override
            public void onStartLottery(int pageType) {
                type = pageType;
                webView.post(() -> isUserLogin());
            }

            @Override
            public void setTitle(String title) {
                webView.post(() -> titleTv.setText(TextUtils.isEmpty(title) ? "" : title));
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (WebAcitivity.this == null) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (WebAcitivity.this == null) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                //打包价任意购，已进入H5要设置给H5
                setUid();
            }

            @Override
            public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl, int type) {
                webView.post(() -> handleClickShare(id, desc, title, imgUrl, contentUrl,type));
            }
        });

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
                if (backFlag) {
                    backFlag = false;
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }

    private void setUid() {
        String uid = UserManager.getInstance().getUser().getUid() + "";
        webView.post(() -> webView.loadUrl("javascript:setValueToH5('" + uid + "','" + 1 + "')"));
    }

    private void isUserLogin() {
        UserManager.getInstance().getLoginState(this, isLogin -> setIsLogin(isLogin));
    }

    private void setIsLogin(boolean b) {
        webView.post(() -> webView.loadUrl("javascript:isLogin(" + b + ")"));
    }

    private void handleClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int result) {
        ShareUtil shareUtil = new ShareUtil();
        shareUtil.share(this, desc, title, imgUrl, contentUrl, type -> {
            //result=0大转盘,result=1"分享有礼",result = 2任意购
            if (result == 1) {
                new Share4GiftConfigManager().postAuthUrl();
            }
            webView.post(() -> webView.loadUrl("javascript:setBtnStatus('" + id + "','" + type + "')"));

            if (result == 2) {
                CollectSubjectManager.getInstance().setType(1);
                CollectSubjectManager.getInstance().addShareNumber(id, new ILoadingListener() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String failMsg) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SocialComponent.onActivityResult(this, requestCode, resultCode, data);
        //result=0大转盘,result=1"分享有礼",result = 2任意购
        if (type != 0 && requestCode == LaunchUtil.LOGIN_REQUEST_CODE  && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            //H5交互，登录成功设置登录成功和用户id给H5
            setIsLogin(true);
        }
        if (type != 1 && requestCode == LaunchUtil.LOGIN_REQUEST_CODE  && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            //type = 1 表示分享有礼，其他表示大礼包和大转盘，这两个页面登录成功需要设置用户id
            setUid();
        }
        if (requestCode == LaunchUtil.COUPON_REQUEST_CODE && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            LaunchUtil.launchActivity(this, BaseSwitchActivity.class, MyCouponFragment.class, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SocialComponent.onNewIntent(this, intent);
    }

}
