package com.feibo.snacks.manager.global.orders.unpaid;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.bean.PayResult;
import com.feibo.snacks.model.bean.SignInfo;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

/**
 * Created by Jayden on 2015/7/22.
 */
public class PayManager {

    private static PayManager sPayManager;

    public static PayManager getInstance() {
        if (sPayManager == null) {
            sPayManager = new PayManager();
        }
        return sPayManager;
    }

    public void getSign4WaitPay(PayParams params, final ILoadingListener listener) {
        OrdersSignHelper helper = new OrdersSignHelper();
        helper.setParams(params);
        helper.loadBeanData(false, helper.generateLoadingListener(listener));
    }

    public void aliPay(Context context, final SignInfo signInfo, final IPayListener listener) {
        //成功获取签名
        //然后调用支付宝付款
        alipy(context, signInfo, new IPayResultListener() {
            @Override
            public void onResult(String result) {
                handleResult(result, listener, signInfo.orderId);
            }
        });
    }

    public void getSign(PayParams params, final ILoadingListener listener) {
        ConfirmOrdersGetSignHelper helper = new ConfirmOrdersGetSignHelper();
        helper.setPayParams(params);
        helper.loadBeanData(false, helper.generateLoadingListener(listener));
    }

    public void payResult(String orderSn, ILoadingListener listener) {
        PayResultHelper helper = new PayResultHelper(orderSn);
        helper.loadBeanData(false, helper.generateLoadingListener(listener));
    }

    public SignInfo getPayOrderData() {
        return (SignInfo) DataPool.getInstance().getData(BaseDataType.PaymentDataType.PAY_ORDER_TYPE);
    }

    public SignInfo getTobePayData() {
        return (SignInfo) DataPool.getInstance().getData(BaseDataType.PaymentDataType.PAY_ORDER_TYPE);
    }

    public PayResult getPayResultData() {
        return (PayResult) DataPool.getInstance().getData(BaseDataType.PaymentDataType.PAY_RESULT_TYPE);
    }

    public void release() {
        DataPool.getInstance().removeData(BaseDataType.PaymentDataType.PAY_ORDER_TYPE);
        DataPool.getInstance().removeData(BaseDataType.PaymentDataType.PAY_RESULT_TYPE);
    }

    private void handleResult(String result, final IPayListener listener, final String ordersId) {
        com.feibo.snacks.view.util.PayResult payResult = new com.feibo.snacks.view.util.PayResult(result);
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000")) {
            listener.onSuccess(getPayOrderData());
        } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                listener.onFail("");
            } else {
                listener.onCancel(ordersId);
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
            }
        }
    }

    private void alipy(final Context context,final SignInfo signInfo, final IPayResultListener listener) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                String result = alipay.pay(signInfo.payInfo);
                listener.onResult(result);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static class OrdersSignHelper extends AbsBeanHelper {
        private PayParams params;
        public OrdersSignHelper() {
            super(BaseDataType.PaymentDataType.PAY_ORDER_TYPE);
        }

        @Override
        public void loadData(boolean needCache, Object obj, DaoListener listener) {
            SnacksDao.getOrderSignInfo(params.getOrderSn(), listener);
        }

        public void setParams(PayParams params) {
            this.params = params;
        }
    }

    private static class ConfirmOrdersGetSignHelper extends AbsBeanHelper {
        private PayParams params;

        public ConfirmOrdersGetSignHelper() {
            super(BaseDataType.PaymentDataType.PAY_ORDER_TYPE);
        }

        @Override
        public void loadData(boolean needCache, Object obj, DaoListener listener) {
            SnacksDao.confirmOrderByAlipay(params.getPosterIds(),params.getCouponId(),params.getIds(), params.getAddId(), params.getNotes(), listener);
        }

        public void setPayParams(PayParams payParams) {
            this.params = payParams;
        }
    }

    private static class PayResultHelper extends AbsBeanHelper {

        public String ordersSn;
        public PayResultHelper(String ordersSn) {
            super(BaseDataType.PaymentDataType.PAY_RESULT_TYPE);
            this.ordersSn = ordersSn;
        }

        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            SnacksDao.getPayResult(ordersSn, listener);
        }
    }

    public interface IPayResultListener {
        void onResult(String result);
    }

    public interface  IPayListener {
        void onStart();
        void onSuccess(SignInfo signInfo);
        void onFail(String failMsg);
        void onCancel(String ordersId);
    }
}
