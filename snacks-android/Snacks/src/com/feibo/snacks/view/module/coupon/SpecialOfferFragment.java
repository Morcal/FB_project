package com.feibo.snacks.view.module.coupon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.coupon.SpecialOfferManager;
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
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

/**
 * 商品促销与特殊优惠页面（优惠券列表页）
 * Created by Jayden on 2015/9/2.
 */
public class SpecialOfferFragment extends BaseTitleFragment {

    private static final String SPECIAL_OFFER_TITLE = "商品促销与特殊优惠";
    private static final String COUPON_LIST_TITLE = "优惠券列表";
    private static final int REQUEST_LOGIN_SPECIAL = 0x210;
    private static final int REQUEST_COUPON_DETAIL = 0x220;
    private static final int RESULT_COUPON_DETAIL = 0x230;
    public static final String TYPE = "type"; //0：商品id, 1：供应商id,2：优惠券集合id（首页、专题页的轮播图使用）
    public static final String ID = "id";

    private View rootView;
    private WebView webView;

    private int type;
    private long id;
    private int discouponId;
    private String webUrl;
    private SpecialOfferManager manager;
    private SpecialOfferWebManager webManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        type = bundle.getInt(TYPE, 0);
        id = bundle.getLong(ID, 0);

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
        initManager();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录成功刷新页面
        if (requestCode == REQUEST_LOGIN_SPECIAL && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            refreshData();
        }
        if (requestCode == REQUEST_COUPON_DETAIL && resultCode == getActivity().RESULT_OK) {
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
        titleView.setText(type != 2 ? SPECIAL_OFFER_TITLE : COUPON_LIST_TITLE);
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
        manager = new SpecialOfferManager(absLoadingView);
        manager.setType(type);
        manager.setId(id);
        manager.loadData();
    }

    private void initWebManager() {
        if (webManager == null) {
            webManager = new SpecialOfferWebManager();
            webManager.setup(webView, new SpecialOfferWebManager.WebViewListener() {
                @Override
                public void onClickFullMail(final int type, final String info) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    handleFullMail(type, info);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onClickDiscoupon(final int id) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            turnToDiscouponDetail(id);
                        }
                    });
                }

                @Override
                public void onCouponBtnOnTapAction(int id, int type, int status) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            handleClickReceive(id, type,status);
                        }
                    });
                }

                @Override
                public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int type) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            handleClickShare(id, desc, title, imgUrl, contentUrl,type);
                        }
                    });
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

    private void handleClickReceive(int id, int type, int status) {
        switch (status) {
            case SpecialOfferWebManager.STATUS_NO_PAID:
            case SpecialOfferWebManager.STATUS_RECEIVE: {
                manager.setCouponType(type);
                discouponId = id;
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
        manager.getDiscouponFromNet(discouponId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }

                handleStatus(manager.getStatusBean());
            }

            @Override
            public void onFail(String failMsg) {

            }
        });
    }

    /**
     * 处理领取优惠券返回的结果
     *
     * @param statusBean
     */
    private void handleStatus(StatusBean statusBean) {
        switch (statusBean.status) {
            case SpecialOfferWebManager.STATUS_RECEIVE:
            case SpecialOfferWebManager.STATUS_HAS_RECEIVED: {
                RemindControl.showSimpleToast(getActivity(), getString(R.string.receive_success));
                setBtnStatus(SpecialOfferWebManager.STATUS_HAS_RECEIVED);
                break;
            }
            case SpecialOfferWebManager.STATUS_ROB: {
                RemindControl.showSimpleToast(getActivity(), getString(R.string.rob_finish));
                setBtnStatus(SpecialOfferWebManager.STATUS_ROB);
                break;
            }
            case SpecialOfferWebManager.STATUS_END: {
                RemindControl.showSimpleToast(getActivity(), getString(R.string.grant_time_end));
                setBtnStatus(SpecialOfferWebManager.STATUS_END);
                break;
            }
            case SpecialOfferWebManager.STATUS_NO_PAID: {
                RemindControl.showSimpleToast(getActivity(), getString(R.string.no_paid));
                setBtnStatus(SpecialOfferWebManager.STATUS_NO_PAID);
                break;
            }
            default:
                break;
        }
    }

    private void setBtnStatus(final int type) {
        Log.d("wangfujnu", " setBtnStatus type = " + type);
        webView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("wangfujnu", "javascript:setBtnStatus(" + type + "," + discouponId + ")");
                webView.loadUrl("javascript:setBtnStatus(" + type + "," + discouponId + ")");
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
                LaunchUtil.launchActivityForResult(REQUEST_LOGIN_SPECIAL, getActivity(),
                        BaseSwitchActivity.class,
                        LoginFragment.class,
                        null);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void turnToDiscouponDetail(int id) {
        Bundle bundle = new Bundle();
        bundle.putLong(CouponDetailFragment.DISCOUPON_ID, id);
        LaunchUtil.launchActivityForResult(REQUEST_COUPON_DETAIL, getActivity(),
                BaseSwitchActivity.class,
                CouponDetailFragment.class,
                bundle);
    }

    private void handleFullMail(int type, String info) {
        switch (type) {
            case SpecialOfferWebManager.SPECIAL_SELLING: {
                LaunchUtil.launchMainActivity(getActivity(), MainActivity.SPECIAL_SELLING_SCENCE);
                break;
            }
            case SpecialOfferWebManager.GOODS_LIST: {
                Bundle bundle = new Bundle();
                bundle.putInt(SupplierGoodsListFragment.SUPPLIER_ID, Integer.parseInt(info));
                bundle.putString(SupplierGoodsListFragment.SUPPLIER_TITLE, SupplierGoodsListFragment.TITLE);
                LaunchUtil.launchAppActivity(getActivity(), BaseSwitchActivity.class, SupplierGoodsListFragment.class, bundle);
                break;
            }
        }
    }
}
