package com.feibo.snacks.view.module.person.orders.ordersdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.manager.global.orders.OrdersDetailManager;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.person.orders.comment.OrdersCommentFragment;
import com.feibo.snacks.view.module.person.orders.logistics.LogisticsFragment;
import com.feibo.snacks.view.module.person.orders.pay.PayResultFragment;
import com.feibo.snacks.view.module.person.orders.util.OrdersOptHelper;
import com.feibo.snacks.view.module.person.orders.pay.PayHelper;
import com.feibo.snacks.view.util.LaunchUtil;

import java.util.ArrayList;

/**
 * Created by hcy on 2015/7/20.
 */
public class OrdersDetailFragment extends BaseTitleFragment {


    public static final String ORDERS_DETAIL_ID  = "orders_detail_id";
    public static final String ORDERS_DETAIL_STATE = "orders_detail_state";
    public static final String NEED_REFRESH_ORDERS = "need_refresh_orders";

    public static boolean needRefreshList = false;

    private View root;
    private ListView listView;
    private Button rightOptBtn;
    private Button leftOptBtn;
    private Button extraOptBtn;
    private OrdersDetailAdapter adapter;

    private OrdersDetailHead head;
    private OrdersDetailFooter footer;

    private OrdersDetailManager manager;
    private AbsLoadingView absLoadingView;
    private PayHelper payHelper;
    private String ordersId;
    private int ordersState;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_detail, null);
        Bundle arg = getArguments();
        ordersId = arg.getString(ORDERS_DETAIL_ID);
        needRefreshList = arg.getBoolean(NEED_REFRESH_ORDERS, false);
        ordersState = arg.getInt(ORDERS_DETAIL_STATE, 0);
        initTitle();
        initWidget();
        initLoading(arg);
        initListener();
        return root;
    }

    private void initTitle() {
        TextView title = (TextView) getTitleBar().title;
        int state;
        state = getState();
        title.setText(state);

        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private int getState() {
        int state;
        switch (ordersState) {
            case ItemOrder.WAIT_PAY: {
                state = R.string.orders_state_wait_pay;
                break;
            }
            case ItemOrder.WAIT_SEND: {
                state = R.string.orders_state_wait_send;
                break;
            }
            case ItemOrder.WAIT_GET: {
                state = R.string.orders_state_wait_get;
                break;
            }
            case ItemOrder.TRADE_SUCCESS: {
                state = R.string.orders_state_trade_success;
                break;
            }
            case ItemOrder.TRADE_FAIL: {
                state = R.string.orders_state_trade_fail;
                break;
            }
            case ItemOrder.WAIT_COMMENT: {
                state = R.string.orders_state_trade_success;
                break;
            }
            case  ItemOrder.RETURN_GOODS: {
                state = R.string.orders_state_wait_out_orders;
                break;
            }
            default: {
                state = R.string.orders_state_wait_pay;
                break;
            }
        }
        return state;
    }

    private void initListener() {
        rightOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightBtnOptResult();
            }
        });
        leftOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftBtnOptResult();
            }
        });
        extraOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraOptBtnOptResult();

            }
        });
    }

    private void extraOptBtnOptResult() {
        turnToLogisticsScence(ordersId);
    }

    private void rightBtnOptResult() {
        OrdersDetail ordersDetail = manager.getData(manager.getDataType());

        int type = ordersDetail.type;
        final String ordersId = ordersDetail.id;
        switch (type) {
            case ItemOrder.WAIT_PAY: {
                payOrders();
                break;
            }
            case ItemOrder.WAIT_SEND: {
                OrdersOptHelper.remindSendOut(getActivity(), ordersId);
                break;
            }
            case ItemOrder.WAIT_GET: {
                confirmOrders(ordersDetail);
                break;
            }
            case ItemOrder.TRADE_SUCCESS: {
                deleteOrders(ordersId);
                break;
            }
            case ItemOrder.TRADE_FAIL: {
                deleteOrders(ordersId);
                break;
            }
            case ItemOrder.WAIT_COMMENT: {
                turnToComment();
                break;
            }
            case ItemOrder.RETURN_GOODS_CHECK:
            case ItemOrder.RETURN_GOODS_DOING: {
                deleteOrders(ordersId);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void turnToComment() {
        Bundle bundle = new Bundle();
        bundle.putString(OrdersCommentFragment.ORDERS_ID, ordersId);
        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_COMMENT, getActivity(), BaseSwitchActivity.class, OrdersCommentFragment.class, bundle);
    }

    private void payOrders() {
        OrdersDetail confirmInfo = manager.getData(manager.getDataType());
        PayParams payParams = new PayParams();
        payParams.setOrderSn(confirmInfo.id);
        payHelper = new PayHelper(getActivity(), payParams, confirmInfo) {
            @Override
            public void onCancleOrders(String ordersId) {

            }

            @Override
            public void onTurnResult() {
                needRefreshList = true;
                getActivity().finish();
            }

            @Override
            public void onRefreshView() {

            }
        };
        payHelper.payOrders4WaitPay();
    }

    private void leftBtnOptResult() {
        OrdersDetail ordersDetail = manager.getData(manager.getDataType());
        int type = ordersDetail.type;
        final String ordersId = ordersDetail.id;
        switch (type) {
            case ItemOrder.WAIT_PAY: {
                OrdersOptHelper.cancelOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                    @Override
                    public void onRefresh() {
                        needRefreshList = true;
                        head.setOrdersState(R.string.orders_state_trade_fail);
                        manager.getData(manager.getDataType()).type = BaseOrder.TRADE_FAIL;
                        setOrdersState();
                    }
                });
                break;
            }
            case ItemOrder.WAIT_SEND: {
                ServiceContact contact = ContactManager.getInstance().getServiceContact();
                OrdersOptHelper.refoundOrder(getActivity(), contact);
                break;
            }
            case ItemOrder.WAIT_GET: {
                ServiceContact contact = ContactManager.getInstance().getServiceContact();
                OrdersOptHelper.refoundOrder(getActivity(), contact);
                break;
            }
            case ItemOrder.TRADE_SUCCESS: {
                turnToLogisticsScence(ordersId);
                break;
            }
            case ItemOrder.TRADE_FAIL: {
                break;
            }
            case ItemOrder.WAIT_COMMENT: {
                turnToLogisticsScence(ordersId);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void confirmOrders(final OrdersDetail ordersDetail) {
        OrdersOptHelper.affirmOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
            @Override
            public void onRefresh() {
                needRefreshList = true;
                Bundle bundle = new Bundle();
                bundle.putString(PayResultFragment.PAY_ORDERS_ID, ordersDetail.id);
                bundle.putDouble(PayResultFragment.PAY_RESULT_MONEY, ordersDetail.finalSum);
                bundle.putInt(PayResultFragment.PAY_RESULT_TYPE, PayResultFragment.CONFIRM_ORDERS);
                bundle.putBoolean(PayResultFragment.NEED_REFRESH_ORDERS, true);
                LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_CONFIRM_PAY, getActivity(), BaseSwitchActivity.class, PayResultFragment.class, bundle);
                getActivity().finish();
            }
        });
    }

    private void deleteOrders(String ordersId) {
        OrdersOptHelper.deleteOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
            @Override
            public void onRefresh() {
                needRefreshList = true;
                getActivity().finish();
            }
        });
    }

    private void turnToLogisticsScence(String ordersId) {
        Bundle bundle = new Bundle();
        bundle.putString(LogisticsFragment.ORDERS_ID, ordersId);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LogisticsFragment.class, bundle);
    }

    private void initLoading(Bundle arg) {
        absLoadingView = new AbsLoadingView(listView) {
            @Override
            public View getLoadingParentView() {
                return root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                manager.loadData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                setOrdersState();
                refreshView();
                ArrayList<CartItem4Type> list = manager.getCart();
                createAdapter();
                adapter.setItems(list);
                listView.setAdapter(adapter);
            }
        };
        absLoadingView.setLauncherPositon(2);
        OrdersDetailManager.setLoadingView(absLoadingView);
        OrdersDetailManager.setOrdersId(ordersId);
        manager = OrdersDetailManager.instance();
        ListViewOperation operation = new ListViewOperation(listView, manager) {
            @Override
            public void operationItemAtPosition(int position) {

            }
        };
        operation.initListData();
    }

    private void refreshView() {
        OrdersDetail ordersDetail = manager.getData(manager.getDataType());
        head.refreshView(ordersDetail);
        footer.refreshView(ordersDetail);
    }

    private void createAdapter() {
        adapter = new OrdersDetailAdapter(getActivity());
        adapter.setOnOrdersDetailOptListener(new OrdersDetailAdapter.OnOrdersDetailOptListener() {
            @Override
            public void onServiceClick(int position) {
                //申请售后
                ServiceContact contact = ContactManager.getInstance().getServiceContact();
                OrdersOptHelper.refoundOrder(getActivity(), contact);
            }
        });
    }

    private void initWidget() {
        listView = (ListView) root.findViewById(R.id.fragment_orders_detail_list);
        rightOptBtn = (Button) root.findViewById(R.id.item_orders_detail_btn_first);
        leftOptBtn = (Button) root.findViewById(R.id.item_orders_detail_btn_second);
        extraOptBtn = (Button) root.findViewById(R.id.item_orders_detail_btn_third);

        head = new OrdersDetailHead(getActivity(), ordersId);
        listView.addHeaderView(head.getRoot());

        footer = new OrdersDetailFooter(getActivity());
        listView.addFooterView(footer.getRoot());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listView = null;
        head = null;
        footer = null;
        absLoadingView = null;
        root = null;
        manager.release();
        manager = null;
        if (payHelper != null) {
            payHelper.release();
            payHelper = null;
        }
    }

    public void setOrdersState() {
        OrdersDetail ordersDetail = manager.getData(manager.getDataType());
        int ordersState = ordersDetail.type;
        int state = R.string.orders_state_wait_pay;
        rightOptBtn.setVisibility(View.VISIBLE);
        rightOptBtn.setTextColor(getResources().getColor(R.color.c6));
        leftOptBtn.setVisibility(View.VISIBLE);
        leftOptBtn.setTextColor(getResources().getColor(R.color.c8));
        extraOptBtn.setVisibility(View.GONE);
        extraOptBtn.setTextColor(getResources().getColor(R.color.c8));
        switch (ordersState) {
            case BaseOrder.WAIT_PAY: {
                state =  R.string.orders_state_wait_pay;
                head.changeAddressEditState(View.VISIBLE);
                rightOptBtn.setText(R.string.account);
                rightOptBtn.setBackgroundResource(R.drawable.btn_account_select);
                leftOptBtn.setText(R.string.cancel_orders);
                leftOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                break;
            }
            case BaseOrder.WAIT_SEND:{
                state =  R.string.orders_state_wait_send;
                rightOptBtn.setText(R.string.remind_orders);
                rightOptBtn.setBackgroundResource(R.drawable.btn_delivery);
                rightOptBtn.setTextColor(getResources().getColor(R.color.c1));
                leftOptBtn.setText(R.string.refund_orders);
                leftOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                break;
            }
            case BaseOrder.WAIT_GET:{
                state =  R.string.orders_state_wait_get;
                rightOptBtn.setText(R.string.affirm_orders);
                rightOptBtn.setBackgroundResource(R.drawable.btn_account_select);
                leftOptBtn.setText(R.string.refund_orders);
                leftOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                extraOptBtn.setVisibility(View.VISIBLE);
                extraOptBtn.setText(R.string.exmind_logistics);
                extraOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                break;
            }
            case BaseOrder.WAIT_COMMENT: {
                state = R.string.orders_state_trade_success;
                rightOptBtn.setText(R.string.orders_comment);
                rightOptBtn.setTextColor(getResources().getColor(R.color.c8));
                rightOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                leftOptBtn.setText(R.string.exmind_logistics);
                leftOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                break;
            }
            case BaseOrder.TRADE_SUCCESS:{
                state =  R.string.orders_state_trade_success;
                rightOptBtn.setText(R.string.delete_orders);
                rightOptBtn.setTextColor(getResources().getColor(R.color.c8));
                rightOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                leftOptBtn.setVisibility(View.VISIBLE);
                leftOptBtn.setText(R.string.exmind_logistics);
                leftOptBtn.setTextColor(getResources().getColor(R.color.c8));
                leftOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                break;
            }
            case BaseOrder.TRADE_FAIL:{
                state =  R.string.orders_state_trade_fail;
                rightOptBtn.setText(R.string.delete_orders);
                rightOptBtn.setTextColor(getResources().getColor(R.color.c8));
                rightOptBtn.setBackgroundResource(R.drawable.btn_orders_cancle);
                leftOptBtn.setVisibility(View.GONE);
                break;
            }
            case ItemOrder.RETURN_GOODS_CHECK:
            case ItemOrder.RETURN_GOODS_DOING: {
                state =  R.string.orders_state_wait_out_orders;
                //TODO
                break;
            }
        }
        head.setOrdersState(state);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LaunchUtil.REQUEST_ORDERS_COMMENT && data != null) {
            needRefreshList = data.getBooleanExtra(OrdersCommentFragment.ORDERS_COMMENT_RESULT, false);
            if (needRefreshList) {
                getActivity().finish();
            }
        }
    }
}
