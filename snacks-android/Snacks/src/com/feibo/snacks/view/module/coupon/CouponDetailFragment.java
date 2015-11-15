package com.feibo.snacks.view.module.coupon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.coupon.CouponDetailManager;
import com.feibo.snacks.manager.module.coupon.CouponDetailWebManager;
import com.feibo.snacks.manager.module.coupon.SpecialOfferWebManager;
import com.feibo.snacks.model.bean.StatusBean;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.dialog.RemindDialog;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodslist.SupplierGoodsListFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.ShareUtil;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

/**
 * Created by Jayden on 2015/9/2.
 */
public class CouponDetailFragment extends BaseTitleFragment {

    private static final String DICOUPON_DETAIL_TITLE = "优惠券";
    private static final String DICOUPON_RIGHT_TITLE = "使用规则";
    public static final String DISCOUPON_TITLE = "title";
    public static final String DISCOUPON_ID = "discouponId";
    public static final String ENTER_SOURCE = "enterType";
    private static final int REQUEST_LOGIN_COUPON_DETAIL = 0x220;

    private View rootView;
    private WebView webView;
    private TextView rightTitle;

    private long discouponId;
    private int jumpType;
    private String title;
    private String webUrl;
    private int resultCode;
    private CouponDetailManager manager;
    private CouponDetailWebManager webManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        discouponId = bundle.getLong(DISCOUPON_ID, 0);
        jumpType = bundle.getInt(ENTER_SOURCE, 0);
        title = bundle.getString(DISCOUPON_TITLE);
    }

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail_subject_h5, null);
        setTitle();
        initWidget();
        initListener();
        initManager();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录成功刷新页面
        if (requestCode == REQUEST_LOGIN_COUPON_DETAIL && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            refreshData();
        }
    }

    private void refreshData() {
        manager.clearData();
        webManager = null;
        webView.clearView();
        initManager();
    }

    private void setTitle() {
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        rightTitle = (TextView) getTitleBar().headView.findViewById(R.id.head_right);
        titleView.setText(TextUtils.isEmpty(title) ? DICOUPON_DETAIL_TITLE : title);
        UIUtil.setViewVisible(rightTitle);
        rightTitle.setText(DICOUPON_RIGHT_TITLE);
        rightTitle.setTextColor(getResources().getColor(R.color.c1));
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(v -> {
            getActivity().setResult(resultCode);
            getActivity().finish();
        });

        rightTitle.setOnClickListener(v -> LaunchUtil.launchAppActivity(getActivity(), BaseSwitchActivity.class, UsingRuleFragment.class, null));
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
        manager = new CouponDetailManager(absLoadingView);
        manager.setDiscouponId(discouponId);
        manager.setJumpType(jumpType);
        manager.loadData();
    }

    private void initWebManager() {
        if (webManager == null) {
            webManager = new CouponDetailWebManager();
            webManager.setup(webView, new CouponDetailWebManager.WebViewListener() {
                @Override
                public void onLookSpecialGoods(final int type, final String info) {
                    Log.d("wangfujun", "type = " + type + " info = " + info);
                    webView.post(() -> handleLookSpecialFullGoods(type, info));
                }

                @Override
                public void onCouponBtnOnTapAction(int id, int type, int status) {
                    webView.post(() -> handleClickReceive(id, type, status));
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if(getActivity() == null){
                        return;
                    }
                    RemindControl.showProgressDialog(getActivity(), "", null);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (getActivity() == null) {
                        return;
                    }
                    RemindControl.hideProgressDialog();
                }

                @Override
                public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl, int type) {
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
                //TODO 统计分享 type = 0什么都不做，type = 1调用
            }
        });
    }

    private void handleClickReceive(int id, int type, int status) {
        switch (status) {
            case SpecialOfferWebManager.STATUS_NO_PAID:
            case SpecialOfferWebManager.STATUS_RECEIVE: {
                manager.setType(type);
                handleReceive();
                break;
            }
            case SpecialOfferWebManager.STATUS_HAS_RECEIVED: {
                LaunchUtil.launchActivityForResult(0, getActivity(), MyCouponActivity.class);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 处理点击领取事件
     */
    private void handleReceive() {
        if (showNoNetWork())
            return;
        if (!UserManager.getInstance().isLogin()) {
            showLoginDialog();
            return;
        }
        manager.getDiscouponFromNet(new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                //领取成功设置标志，返回的时候要刷新优惠券列表页
                resultCode = getActivity().RESULT_OK;
                handleStatus(manager.getStatusBean());
            }

            @Override
            public void onFail(String failMsg) {

            }
        });
    }

    private boolean showNoNetWork() {
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(getActivity(), getActivity().getResources().getString(R.string.no_network));
            return true;
        }
        return false;
    }

    private void showLoginDialog() {
        RemindDialog.RemindSource source = new RemindDialog.RemindSource();
        source.contentStr = getResources().getString(R.string.no_login);
        source.confirm = getResources().getString(R.string.dialog_login_confirm);
        source.cancel = getResources().getString(R.string.dialog_login_cancle);
        RemindControl.showRemindDialog(getActivity(), source, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                LaunchUtil.launchActivityForResult(REQUEST_LOGIN_COUPON_DETAIL, getActivity(),
                        BaseSwitchActivity.class,
                        LoginFragment.class,
                        null);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 处理领取优惠券返回的结果
     * @param statusBean
     */
    private void handleStatus(StatusBean statusBean) {
        switch (statusBean.status) {
            case SpecialOfferWebManager.STATUS_RECEIVE:
            case SpecialOfferWebManager.STATUS_HAS_RECEIVED: {
                RemindControl.showSimpleToast(getActivity(),getString(R.string.receive_success));
                setBtnStatus(SpecialOfferWebManager.STATUS_HAS_RECEIVED);
                break;
            }
            case SpecialOfferWebManager.STATUS_ROB: {
                RemindControl.showSimpleToast(getActivity(),getString(R.string.rob_finish));
                setBtnStatus(SpecialOfferWebManager.STATUS_ROB);
                break;
            }
            case SpecialOfferWebManager.STATUS_END: {
                RemindControl.showSimpleToast(getActivity(),getString(R.string.grant_time_end));
                setBtnStatus(SpecialOfferWebManager.STATUS_END);
                break;
            }
            case SpecialOfferWebManager.STATUS_NO_PAID: {
                RemindControl.showSimpleToast(getActivity(),getString(R.string.no_paid));
                setBtnStatus(SpecialOfferWebManager.STATUS_NO_PAID);
                break;
            }
            default:
                break;
        }
    }

    private void setBtnStatus(final int type) {
        webView.post(() -> webView.loadUrl("javascript:setBtnStatus('" + type + "','"+ discouponId +"')"));
    }


    private void handleLookSpecialFullGoods(int type, String info) {
        switch (type) {
            case SpecialOfferWebManager.SPECIAL_SELLING: {
                LaunchUtil.launchMainActivity(getActivity(), MainActivity.SPECIAL_SELLING_SCENCE);
                break;
            }
            case SpecialOfferWebManager.GOODS_LIST: {
                Bundle bundle = new Bundle();
                bundle.putInt(SupplierGoodsListFragment.SUPPLIER_ID, Integer.parseInt(info));
                bundle.putString(SupplierGoodsListFragment.SUPPLIER_TITLE,SupplierGoodsListFragment.TITLE);
                LaunchUtil.launchAppActivity(getActivity(), BaseSwitchActivity.class, SupplierGoodsListFragment.class, bundle);
                getActivity().finish();
                break;
            }
        }
    }
}
