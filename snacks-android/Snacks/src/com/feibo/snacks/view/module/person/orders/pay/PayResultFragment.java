package com.feibo.snacks.view.module.person.orders.pay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.person.orders.comment.OrdersCommentFragment;
import com.feibo.snacks.view.module.person.orders.ordersdetail.OrdersDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.UIUtil;

/**
 * Created by Jayden on 2015/7/17.
 */
public class PayResultFragment extends BaseTitleFragment implements View.OnClickListener {

    public static final String PAY_RESULT_TYPE = "result";
    public static final String PAY_RESULT_MONEY = "pay_money";
    public static final String PAY_ORDERS_ID = "pay_orders_id";
    public static final String NEED_REFRESH_ORDERS = "need_fresh_orders";
    public static final int PAY_SUCCESS = 1;
    public static final int PAY_FAILURE = 2;
    public static final int CONFIRM_ORDERS = 3;

    private View root;
    private TextView tradeResult;
    private TextView tradeResult1;
    private TextView orderDetail;
    private TextView commentOrHome;
    private TextView payMoney;
    private View payView;

    private int type;
    private double realPay;
    private String ordersId;
    private boolean needRefreshOrders = false;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_trade_success, null);
        setTitle();
        initWidget();
        showData();
        initListener();
        return root;
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.ordersSuccessFragment);
        super.onResume();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.ordersSuccessFragment);
        super.onPause();
    }

    private void setTitle() {
        Bundle bundle = getArguments();
        type = bundle.getInt(PAY_RESULT_TYPE, 0);
        realPay = bundle.getDouble(PAY_RESULT_MONEY);
        ordersId = bundle.getString(PAY_ORDERS_ID);
        needRefreshOrders = bundle.getBoolean(NEED_REFRESH_ORDERS, false);
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title_name);
        UIUtil.setViewGone(getTitleBar().rightPart);

        if (type == PAY_SUCCESS) {
            titleView.setText(getResources().getString(R.string.pay_success));
        } else if (type == PAY_FAILURE) {
            titleView.setText(getResources().getString(R.string.pay_failure));
        } else if (type == CONFIRM_ORDERS) {
            titleView.setText(getResources().getString(R.string.confirm_orders));
        }
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void showData() {
        payMoney.setText("￥" + realPay);
        if (type == PAY_SUCCESS) {
            tradeResult.setText(getResources().getString(R.string.pay_success));
            tradeResult1.setText(getResources().getString(R.string.pay_success_result));
            payMoney.setVisibility(View.VISIBLE);
            tradeResult.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_success), null, null, null);
        } else if (type == PAY_FAILURE) {
            tradeResult.setText(getResources().getString(R.string.pay_failure));
            tradeResult1.setText(getResources().getString(R.string.pay_failure_result));
            payMoney.setVisibility(View.GONE);
            payView.setVisibility(View.GONE);
            tradeResult.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_fail), null, null, null);
        } else if (type == CONFIRM_ORDERS) {
            tradeResult.setText(getResources().getString(R.string.confirm_orders));
            tradeResult1.setText(getResources().getString(R.string.confirm_orders_result));
            tradeResult.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_success), null, null, null);
            payMoney.setVisibility(View.GONE);
            payView.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        orderDetail.setOnClickListener(this);
        commentOrHome.setOnClickListener(this);
    }

    private void initWidget() {
        tradeResult = (TextView) root.findViewById(R.id.trade_result);
        tradeResult1 = (TextView) root.findViewById(R.id.trade_result1);
        orderDetail = (TextView) root.findViewById(R.id.order_detail);
        commentOrHome = (TextView) root.findViewById(R.id.back_home);
        payMoney = (TextView) root.findViewById(R.id.pay_money_success);
        payView = root.findViewById(R.id.pay_money_view);
        if (type == CONFIRM_ORDERS) {
            commentOrHome.setText(R.string.trade_success_comment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_detail: {
                launchOrdersDetailScence();
                break;
            }
            case R.id.back_home: {
                if (type != CONFIRM_ORDERS) {
                    returnHomeScence();
                } else {
                    launchCommentScence();
                }
                break;
            }
        }
    }

    private void launchCommentScence() {
        Bundle bundle = new Bundle();
        bundle.putString(OrdersCommentFragment.ORDERS_ID, ordersId);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, OrdersCommentFragment.class, bundle);
        getActivity().finish();
    }

    private void returnHomeScence() {
        LaunchUtil.launchMainActivity(getActivity(), MainActivity.HOME_SCENCE);
        getActivity().finish();
    }

    private void launchOrdersDetailScence() {
        Bundle bundle = new Bundle();
        bundle.putString(OrdersDetailFragment.ORDERS_DETAIL_ID, ordersId);
        bundle.putBoolean(OrdersDetailFragment.NEED_REFRESH_ORDERS, needRefreshOrders);
        bundle.putInt(OrdersDetailFragment.ORDERS_DETAIL_STATE, 1);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, OrdersDetailFragment.class, bundle);
        getActivity().finish();
    }
}
