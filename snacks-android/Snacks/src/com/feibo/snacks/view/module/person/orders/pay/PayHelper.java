package com.feibo.snacks.view.module.person.orders.pay;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.orders.unpaid.PayManager;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.bean.SignInfo;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

/**
 * Created by hcy on 2015/8/2.
 */
public abstract class PayHelper {

    public static boolean payResult = false;
    private static final String FAIL_PAY_BECAUSE_INVALID_COUPON = "6006";

    private Context context;
    private PayParams payParams;
    private BaseOrder orders;

    private static final int SHOW_PROGRESS_VIEW = 2;
    private static final int HIDE_PROGRESS_VIEW = 3;
    private static final int GET_PAY_RESULT = 4;
    private static final int SHOW_PAY_FAIL = 5;
    private static final int FINISH_VIEW = 6;
    private static final int SHOW_INVALID_COUPON = 7;
    private static final int REFRESH_VIEW = 8;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (context == null) {
                return;
            }
            switch (msg.what) {
                case SHOW_PROGRESS_VIEW: {
                    RemindControl.showProgressDialog(context, R.string.trade_success_confirm, null);
                    break;
                }
                case HIDE_PROGRESS_VIEW: {
                    RemindControl.hideProgressDialog();
                    break;
                }
                case GET_PAY_RESULT: {
                    getPayResult();
                    break;
                }
                case SHOW_PAY_FAIL: {
                    RemindControl.showSimpleToast(context, "您的订单内有商品信息失效，请重新购买");
                    break;
                }
                case FINISH_VIEW: {
                    onTurnResult();
                    break;
                }
                case SHOW_INVALID_COUPON: {
                    RemindControl.showSimpleToast(context, "订单内促销信息发生变化，请重新支付");
                    break;
                }
                case REFRESH_VIEW: {
                    onRefreshView();
                    break;
                }
            }
        }
    };

    public PayHelper(Context context, PayParams payParams, BaseOrder orders) {
        this.context = context;
        this.payParams = payParams;
        this.orders = orders;
        payResult = false;
    }

    public void payOrders() {
        handler.sendEmptyMessage(SHOW_PROGRESS_VIEW);
        PayManager.getInstance().getSign(payParams, new ILoadingListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                aliPay(PayManager.getInstance().getTobePayData());
            }

            @Override
            public void onFail(String failMsg) {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                if (FAIL_PAY_BECAUSE_INVALID_COUPON.equals(failMsg)) {
                    handler.sendEmptyMessage(SHOW_INVALID_COUPON);
                    handler.sendEmptyMessageDelayed(REFRESH_VIEW, 1000);
                } else {
                    handler.sendEmptyMessage(SHOW_PAY_FAIL);
                    handler.sendEmptyMessageDelayed(FINISH_VIEW, 1000);
                }
            }
        });
    }

    public void payOrders4WaitPay() {
        handler.sendEmptyMessage(SHOW_PROGRESS_VIEW);
        PayManager.getInstance().getSign4WaitPay(payParams, new ILoadingListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                aliPay(PayManager.getInstance().getPayOrderData());
            }

            @Override
            public void onFail(String failMsg) {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                turnToPayResult(PayResultFragment.PAY_FAILURE);
            }
        });
    }

    private void aliPay(SignInfo signInfo) {
        PayManager.getInstance().aliPay(context, signInfo, new PayManager.IPayListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(SignInfo info) {
                handler.sendEmptyMessage(SHOW_PROGRESS_VIEW);
                handler.sendEmptyMessageDelayed(GET_PAY_RESULT, 1000);
            }

            @Override
            public void onFail(String failMsg) {
                turnToPayResult(PayResultFragment.PAY_FAILURE);
            }

            @Override
            public void onCancel(String ordersId) {
                onCancleOrders(ordersId);
            }
        });
    }

    private void getPayResult() {
        SignInfo info = PayManager.getInstance().getPayOrderData();
        if (info == null) {
            return;
        }
        handler.sendEmptyMessage(SHOW_PROGRESS_VIEW);
        PayManager.getInstance().payResult(info.orderId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                if (PayManager.getInstance().getPayResultData().isSuccess()) {
                    turnToPayResult(PayResultFragment.PAY_SUCCESS);
                } else {
                    handler.sendEmptyMessageDelayed(GET_PAY_RESULT, 1000);
                }
            }

            @Override
            public void onFail(String failMsg) {
                handler.sendEmptyMessage(HIDE_PROGRESS_VIEW);
                turnToPayResult(PayResultFragment.PAY_FAILURE);
            }
        });
    }

    private void turnToPayResult(int result) {
        Bundle bundle = new Bundle();
        if (result == PayResultFragment.PAY_SUCCESS) {
            SignInfo signInfo = PayManager.getInstance().getPayOrderData();
            bundle.putString(PayResultFragment.PAY_ORDERS_ID, signInfo.orderId);
        }

        bundle.putDouble(PayResultFragment.PAY_RESULT_MONEY, orders.finalSum);
        bundle.putInt(PayResultFragment.PAY_RESULT_TYPE, result);
        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_CONFIRM_PAY, context, BaseSwitchActivity.class, PayResultFragment.class, bundle);
        onTurnResult();
    }

    public void release() {
        handler.removeCallbacks(null);
        handler = null;
        PayManager.getInstance().release();
    }

    public abstract void onCancleOrders(String ordersId);

    public abstract void onTurnResult();

    public abstract void onRefreshView();
}
