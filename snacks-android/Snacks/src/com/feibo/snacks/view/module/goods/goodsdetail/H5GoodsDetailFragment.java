package com.feibo.snacks.view.module.goods.goodsdetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.trade.ItemService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.CollectGoodsManager;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.ShareManager;
import com.feibo.snacks.manager.global.StatisticsManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.goods.GoodsDetailManager;
import com.feibo.snacks.manager.module.goods.WebViewManager;
import com.feibo.snacks.model.bean.GoodsDetail;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.dialog.AddToCartDialog;
import com.feibo.snacks.view.dialog.RemindDialog.RemindSource;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.coupon.SpecialOfferFragment;
import com.feibo.snacks.view.module.person.PersonFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.RemindControl.OnRemindListener;
import com.feibo.snacks.view.util.ShareUtil;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.Executors;

import fbcore.log.LogUtil;
import fbcore.utils.Strings;

@SuppressLint("InflateParams")
public class H5GoodsDetailFragment extends BaseTitleFragment implements OnClickListener {

    public static final String GOODS_TYPE = "goodsType";
    public static final String ENTER_SOURCE = "enterSource";
    public static final String GOODS_ID = "goodsId";
    public static final String ENTER_LOCATION = "enterLocation";
    private static final int SUCCESS = 1;
    private static final int TRADE_FAILURE = 2;
    private static final int TRADE_CANCLE = 3;

    private GoodsDetail data;
    private GoodsDetailManager goodsDetailManager;
    private WebViewManager webViewManager;
    private RedPointManager redPointManager;

    private RedPointManager.RedPointObserver redPointObserver;

    private View rootView;
    private WebView webView;

    // bottom
    private View detailBottom;
    private View bottomCart;
    private View bottomGuide;
    private View carNumberView;
    private TextView taobaoBuy;// 立即到淘宝购买
    private TextView carNumber;// 购物车数量
    private TextView addToCart;// 加入购物车

    private int position = 0;// 收藏商品的索引
    private int goodsId = 0;
    private int goodsType = 0;
    private long stayTime = 0;
    private long resumeTime = 0;
    private String enterPosition;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_goodsdetail_title;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods_detail, null);
        initWidget(rootView);
        setListener();
        showData();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        goodsId = args.getInt(H5GoodsDetailFragment.GOODS_ID, 0);
        goodsType = args.getInt(GOODS_TYPE, 0);
        position = args.getInt(POSITION);
        enterPosition = args.getString(ENTER_LOCATION);
        StatisticsManager.getInstance().statisticsVisitQuantity(goodsId, Constant.GOODS_DETAIL, args.getInt(ENTER_SOURCE));
        redPointManager = RedPointManager.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                showCartNum();
            }
        };
        redPointManager.addObserver(redPointObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
    }

    private void setListener() {
        carNumberView.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        taobaoBuy.setOnClickListener(this);
        getTitleBar().rightPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setShareListener();
            }
        });
        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (goodsDetailManager == null) {
            initManager();
        }
        goodsDetailManager.loadData();
    }

    private void hasData() {
        if (getActivity() != null) {
            isShowBottom();
            initWebViewManager();
        }
    }

    private void isShowBottom() {
        detailBottom.setVisibility(View.VISIBLE);
        if (data.guideType != 0) {// 导购商品
            UIUtil.setViewVisible(bottomGuide);
            UIUtil.setViewGone(bottomCart);
        } else {
            UIUtil.setViewVisible(bottomCart);
            showCartNum();
            UIUtil.setViewGone(bottomGuide);
        }
    }

    private void showCartNum() {
        redPointManager.setRedNumberView(carNumber);
    }

    private void clickGuessLikeItem(int id) {
        if (getActivity() == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(H5GoodsDetailFragment.GOODS_ID, id);
        bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.GUESS_LIKE);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_guess_like));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    TradeResult tradeResult = (TradeResult) msg.obj;
                    LogUtil.d("taoke", "支付成功" + tradeResult.paySuccessOrders + " fail:" + tradeResult.payFailedOrders);
                    break;
                case TRADE_FAILURE:
                    LogUtil.d("taoke", "确认交易订单失败");
                    break;
                case TRADE_CANCLE:
                    LogUtil.d("taoke", "交易取消" + msg.obj);
                    break;
            }
        }
    };

    private void initManager() {
        AbsLoadingView absLoadingView = new AbsLoadingView() {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                showData();
            }

            @Override
            public void fillData(Object detail) {
                if (detail != null && detail instanceof GoodsDetail) {
                    data = (GoodsDetail) detail;
                    hasData();
                }
            }
        };
        absLoadingView.setLauncherPositon(2);
        goodsDetailManager = new GoodsDetailManager(absLoadingView);
        goodsDetailManager.setGoodsId(goodsId);
    }

    private void initWidget(View rootView) {
        detailBottom = rootView.findViewById(R.id.detail_bottom);
        bottomCart = rootView.findViewById(R.id.goods_detail_cart_operation);
        bottomGuide = rootView.findViewById(R.id.goods_detail_operation);
        carNumberView = bottomCart.findViewById(R.id.head_right);
        taobaoBuy = (TextView) bottomGuide.findViewById(R.id.goods_detail_buy);
        carNumber = (TextView) bottomCart.findViewById(R.id.home_car_number);
        addToCart = (TextView) bottomCart.findViewById(R.id.goods_detail_addtocart);

        webView = (WebView) rootView.findViewById(R.id.webview);
    }

    private void initWebViewManager() {
        if (webViewManager == null) {
            webViewManager = new WebViewManager();
            webViewManager.setup(webView, new WebViewManager.WebViewListener() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(getActivity() == null){
                        return;
                    }
                    if (data.status == 1) {
                        // 已售空
                        showEmptyDialog(getResources().getString(R.string.sell_empty), getResources().getString(R.string.dialog_empty_confirm),
                                getResources().getString(R.string.dialog_bargain_cancle));
                    } else if (data.status == 2) {
                        // 已下架
                        showEmptyDialog(getResources().getString(R.string.off_shelf), getResources().getString(R.string.dialog_empty_confirm),
                                getResources().getString(R.string.dialog_bargain_cancle));
                    } else {
                        boolean isCollect = data.collectStatus == 1 ? true : false;
                        if (isCollect) {
                            SPHelper.addCollect(goodsId);
                        }
                        setCollectState(isCollect);
                    }
                }

                @Override
                public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int type) {

                }

                @Override
                public void onClickCollect() {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            addCollect();
                        }
                    });
                }

                @Override
                public void onClickGuessLike(final int id) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            clickGuessLikeItem(id);
                        }
                    });
                }

                @Override
                public void onClickMoreComment(final String moreUrl, final int totalComment) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            turnToMoreComment(moreUrl, totalComment);
                        }
                    });
                }

                @Override
                public void onPromotionEnd() {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            showEmptyDialog(getResources().getString(R.string.dialog_empty_content),
                                    getResources().getString(R.string.dialog_bargain_cancle), getResources().getString(R.string.dialog_empty_cancle));
                        }
                    });
                }

                @Override
                public void onClickFullMail(final int type, final int supplierId) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            handleFullMail(type, supplierId);
                        }
                    });
                }
            });
            webView.loadUrl(data.url);
        }
    }

    private void handleFullMail(int type, int supplierId) {
        if (getActivity() == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(SpecialOfferFragment.TYPE, type);
        bundle.putLong(SpecialOfferFragment.ID, supplierId);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, SpecialOfferFragment.class, bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_right: {
                LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
                break;
            }
            case R.id.goods_detail_addtocart: {
                selectToAddCart();
                StatisticsManager.getInstance().statisticsBehavior(goodsId, Constant.TAOBAO_ORDER, "");
                break;
            }
            case R.id.goods_detail_buy: {
                if (!AppContext.isNetworkAvailable()) {
                    RemindControl.showSimpleToast(getActivity(),
                            getActivity().getResources().getString(R.string.not_network));
                    return;
                }
                if (data.guideType == 0 || data == null || data.guideInfo.realId == 0 || isValidUrl(data.guideInfo.realUrl)) {
                    return;
                }
                if (isTaokeUrl()) {
                    // taoke url
                    turnToTaoke();
                } else {
                    // baichuan
                    showDetail();
                }
                if (enterPosition != null) {
                    MobclickAgent.onEvent(getActivity(), enterPosition);
                }
                StatisticsManager.getInstance().statisticsBehavior(goodsId, Constant.TAOBAO_ORDER, "");
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_taobao_order));
                break;
            }

            default:
                break;
        }
    }

    private void turnToTaoke() {
        LaunchUtil.launchShoppingActivity(getActivity(), data.guideInfo.realUrl, data.activiteInfo);
    }

    private boolean isTaokeUrl() {
        return data.guideInfo.openType == 0 ? false : true;
    }

    private boolean isValidUrl(String realUrl) {
        return "".equals(realUrl) || Strings.isEmpty(realUrl);
    }

    private void turnToMoreComment(String url, int total) {
        Bundle bundle = new Bundle();
        bundle.putString(WebFragment.URL, url);
        bundle.putString(WebFragment.TITLE, getResources().getString(R.string.comment, total));
        LaunchUtil.launchAppActivity(getActivity(),BaseSwitchActivity.class,WebFragment.class,bundle);
    }

    private void selectToAddCart() {
        if (data == null) {
            return;
        }
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_to_cart));
        if (showNoNetWork())
            return;
        if (data.status != 0 || data.kinds == null || data.kinds.get(0).kinds == null) {
            showEmptyDialog(getResources().getString(R.string.sell_empty), getResources().getString(R.string.dialog_empty_confirm),
                    getResources().getString(R.string.dialog_bargain_cancle));
            return;
        }
        final AddToCartDialog dialog = AddToCartDialog.show(getActivity(), data.kinds, data.img.imgUrl);
        dialog.setOnClickListener(new AddToCartDialog.OnClickListener() {
            @Override
            public void onShare(int number, int subKindId, int kindId) {
                if (showNoNetWork())
                    return;
                goodsDetailManager.setGoodsId(goodsId);
                goodsDetailManager.setKindId(kindId);
                goodsDetailManager.setSubKindId(subKindId);
                goodsDetailManager.setNumber(number);
                goodsDetailManager.addToCart(new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        RemindControl.showSimpleToast(getActivity(),
                                getResources().getString(R.string.add_to_cart_success));
                        redPointManager.loadRedPoint();
                        dialog.cancel();
                        showCartNum();
                    }

                    @Override
                    public void onFail(String failMsg) {
                        showEmptyDialog(getResources().getString(R.string.sell_empty), getResources().getString(R.string.dialog_empty_confirm),
                                getResources().getString(R.string.dialog_bargain_cancle));
                        dialog.cancel();
                    }
                });
            }
        });
    }

    private boolean showNoNetWork() {
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(getActivity(), getActivity().getResources().getString(R.string.not_network));
            return true;
        }
        return false;
    }

    private void showDetail() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                TaokeParams taokeParams = new TaokeParams();
                taokeParams.pid = Constant.TAOKE_PID;
                Log.d("wangfujun","data.guideInfo.realId = " + data.guideInfo.realId);
                Log.d("wangfujun","data.guideType" + data.guideType);
                Log.d("wangfujun","taokeParams" + taokeParams.pid);
                AlibabaSDK.getService(ItemService.class).showTaokeItemDetailByItemId(getActivity(),
                        new TradeProcessCallback() {
                            @Override
                            public void onPaySuccess(TradeResult tradeResult) {
                                Message message = new Message();
                                message.what = SUCCESS;
                                message.obj = tradeResult;
                                handler.sendMessage(message);

                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                                    handler.sendEmptyMessage(TRADE_FAILURE);
                                } else {
                                    Message message = new Message();
                                    message.what = TRADE_CANCLE;
                                    message.obj = code + msg;
                                    handler.sendMessage(message);
                                }
                            }
                        }, null, data.guideInfo.realId, data.guideType, null, taokeParams);
            }
        });
    }

    private void addCollect() {
        if (showNoNetWork())
            return;
        if (!UserManager.getInstance().isLogin()) {
            showLoginDialog();
            return;
        }
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_collect));
        StatisticsManager.getInstance().statisticsBehavior(goodsId, Constant.COLLECT, "");

        boolean isCollect = SPHelper.isCollect(goodsId);
        if (isCollect) {
            cancelCollect();
            return;
        }
        CollectGoodsManager.getInstance().addCollect(goodsId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                handleCollectSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
                if (NetResult.HAS_COLLECTED_STRING.equals(failMsg)) {
                    handleCollectSuccess();
                }
            }
        });
    }

    private void handleCollectSuccess() {
        RemindControl.showSimpleToast(getActivity(), getResources().getString(R.string.add_collect_success));
        setCollectState(true);
        forResult(false);
    }

    private void setCollectState(final boolean b) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:changeCollect('" + b + "')");
            }
        });
    }

    private void cancelCollect() {
        CollectGoodsManager.getInstance().removeOneCollect(goodsId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                setCollectState(false);
                forResult(true);
            }

            @Override
            public void onFail(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
            }
        });
    }

    private void showLoginDialog() {
        RemindSource source = new RemindSource();
        source.contentStr = getResources().getString(R.string.dialog_login_content);
        source.confirm = getResources().getString(R.string.dialog_login_confirm);
        source.cancel = getResources().getString(R.string.dialog_login_cancle);
        RemindControl.showRemindDialog(getActivity(), source, new OnRemindListener() {
            @Override
            public void onConfirm() {
                Bundle bundle = new Bundle();
                bundle.putBoolean(PersonFragment.PARAM_NEED_SHOW, false);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LoginFragment.class, bundle);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void showEmptyDialog(String msg, final String cancel, final String confirm) {
        RemindControl.cancelToast();
        RemindSource source = new RemindSource();
        source.contentStr = msg;
        source.cancel = cancel;
        source.confirm = confirm;
        RemindControl.showRemindDialog(getActivity(), source, new OnRemindListener() {
            @Override
            public void onConfirm() {
                if (confirm.equals(getResources().getString(R.string.dialog_bargain_cancle))) {
                    returnHomeScene();
                } else if (confirm.equals(getResources().getString(R.string.dialog_empty_cancle))) {
                    refreshData();
                }

            }

            @Override
            public void onCancel() {
                if (cancel.equals(getResources().getString(R.string.dialog_bargain_cancle))) {
                    returnHomeScene();
                } else if (cancel.equals(getResources().getString(R.string.dialog_empty_confirm))) {
                    getActivity().finish();
                }
            }
        });
    }

    //活动结束，点击继续购买，刷新本页面数据
    private void refreshData() {
        goodsDetailManager.clear();
        data = null;
        webViewManager = null;
        initManager();
        goodsDetailManager.loadData();
    }

    private void returnHomeScene() {
        LaunchUtil.launchMainActivity(getActivity(), MainActivity.HOME_SCENCE);
        getActivity().finish();
    }

    private void setShareListener() {
        if (data == null) {
            return;
        }
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_share));
        StatisticsManager.getInstance().statisticsBehavior(goodsId, Constant.SHARE, "");
        ShareUtil shareUtil = new ShareUtil();
        shareUtil.share(getActivity(), data.desc, data.title, data.img.imgUrl, ShareManager.buildSnacksWebUrl(goodsId), new ShareUtil.IShareResult() {
            @Override
            public void onShareResult(int type) {
                //TODO 统计分享
            }
        });
    }

    @Override
    public void onDestroy() {
        StatisticsManager.getInstance().statisticsTimeLength(goodsId, stayTime / 1000);
        super.onDestroy();
        goodsDetailManager.clear();
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(rootView);
        }
        rootView = null;
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.goodsDetailFragment);
        super.onResume();
        showCartNum();
        resumeTime = System.currentTimeMillis();
        RemindControl.cancelToast();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.goodsDetailFragment);
        super.onPause();
        stayTime += (System.currentTimeMillis() - resumeTime);
    }

    public static final String ISCANCEL = "isCancel";
    public static final String POSITION = "position";
    public static final int RESULT_CODE = 0x111;

    private void forResult(boolean isCancel) {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putBoolean(ISCANCEL, isCancel);
        b.putInt(POSITION, position);
        intent.putExtras(b);
        getActivity().setResult(RESULT_CODE, intent);
    }
}