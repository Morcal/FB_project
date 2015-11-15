package com.feibo.snacks.view.module.person.orders.ordersdetail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.orders.logistics.LogisticsFragment;
import com.feibo.snacks.view.util.LaunchUtil;

/**
 * Created by hcy on 2015/7/28.
 */
public class OrdersDetailHead {

    private Context context;
    private View headView;
    private TextView nameTv;
    private TextView phoneTv;
    private TextView addressTv;
    private TextView ordersStateTv;
    private TextView ordersPayTv;
    private TextView ordersSendPay;
    private View addressEdit;
    private TextView logisticsDesTv;
    private TextView logisticsTimeTv;
    private View logisticsParent;
    private View addressEmpty;
    private View addressParent;

    private String ordersId;

    public OrdersDetailHead(Context context, String ordersId) {
        this.context = context;
        this.ordersId = ordersId;
        initWidget();
        initListener();
    }

    public View getRoot() {
        return headView;
    }

    public void changeAddressEditState(int visibility) {
        addressEdit.setVisibility(View.GONE);
    }

    public void setOrdersState(int state) {
        ordersStateTv.setText(state);
    }

    public void refreshView(OrdersDetail ordersDetail) {
        if (ordersDetail.address != null) {
            nameTv.setText(ordersDetail.address.name);
            phoneTv.setText(ordersDetail.address.phone);
            addressTv.setText(ordersDetail.address.getFullAddress());
            addressEmpty.setVisibility(View.GONE);
        } else {
            nameTv.setVisibility(View.GONE);
            phoneTv.setVisibility(View.GONE);
            addressTv.setVisibility(View.GONE);
        }

        String ordersDes = context.getResources().getString(R.string.orders_pay_desc);
        ordersPayTv.setText(ordersDes + ordersDetail.finalSum);
        String sendDes = context.getResources().getString(R.string.send_pay_desc);
        ordersSendPay.setText(sendDes + ordersDetail.sendPay);

        if (ordersDetail.logistics == null || ordersDetail.type == BaseOrder.WAIT_PAY) {
            logisticsParent.setVisibility(View.GONE);
        } else {
            logisticsParent.setVisibility(View.VISIBLE);
            logisticsTimeTv.setText(ordersDetail.logistics.time);
            logisticsDesTv.setText(ordersDetail.logistics.desc);
        }
    }

    private void initListener() {
        logisticsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(LogisticsFragment.ORDERS_ID, ordersId);
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, LogisticsFragment.class, bundle);
            }
        });
    }

    public boolean canSubmitOrders() {
        return addressEmpty.getVisibility() == View.GONE;
    }

    private void initWidget() {
        headView = LayoutInflater.from(context).inflate(R.layout.layout_orders_detail_head, null);

        nameTv = (TextView) headView.findViewById(R.id.orders_detail_name);
        phoneTv = (TextView) headView.findViewById(R.id.orders_detail_phone);
        addressTv = (TextView) headView.findViewById(R.id.orders_detail_address);
        ordersStateTv = (TextView) headView.findViewById(R.id.orders_detail_orders_state);
        ordersPayTv = (TextView) headView.findViewById(R.id.orders_detail_orders_pay);
        ordersSendPay = (TextView) headView.findViewById(R.id.orders_detail_send_pay);
        addressEdit = headView.findViewById(R.id.orders_detail_entry_address);
        logisticsDesTv = (TextView) headView.findViewById(R.id.orders_detail_send_info);
        logisticsTimeTv = (TextView) headView.findViewById(R.id.orders_detail_send_time);
        logisticsParent = headView.findViewById(R.id.orders_detail_send_parent);
        addressEmpty = headView.findViewById(R.id.orders_detail_head_address_empty);
        addressParent = headView.findViewById(R.id.orders_detail_address_viewgroup);
        addressEdit.setVisibility(View.GONE);
    }
}
