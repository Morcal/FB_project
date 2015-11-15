package com.feibo.snacks.view.module.person.orders.item;

import android.os.Bundle;

import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.manager.global.orders.paid.OrdersWaitReceiptManager;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.orders.OrdersBaseFragment;
import com.feibo.snacks.view.module.person.orders.logistics.LogisticsFragment;
import com.feibo.snacks.view.module.person.orders.pay.PayResultFragment;
import com.feibo.snacks.view.module.person.orders.util.OrdersOptHelper;
import com.feibo.snacks.view.util.LaunchUtil;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersWaitReiptFragment extends OrdersBaseFragment {

    private OrdersWaitReceiptManager manager;
    @Override
    public AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView) {
        manager = new OrdersWaitReceiptManager(iLoadingView);
        return manager;
    }

    @Override
    public OrdersAdapter.OrdersOptListener getOrderOptListener() {
        return new OrdersAdapter.OrdersOptListener() {
            @Override
            public void onLeftBtnClick(int position) {
                ServiceContact contact = ContactManager.getInstance().getServiceContact();
                OrdersOptHelper.refoundOrder(getActivity(), contact);
            }

            private void turnToLogisticScence(int position) {
                String ordersId = manager.getData(manager.getDataType()).get(position).id;
                Bundle bundle = new Bundle();
                bundle.putString(LogisticsFragment.ORDERS_ID, ordersId);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LogisticsFragment.class, bundle);
            }

            @Override
            public void onRightBtnClick(final int position) {
                affirmOrders(position);
            }

            private void affirmOrders(final int position) {
                final ItemOrder orders = manager.getData(manager.getDataType()).get(position);
                final String ordersId = orders.id;
                final double sum = orders.finalSum;
                OrdersOptHelper.affirmOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                    @Override
                    public void onRefresh() {
                        manager.removeOrders(position);
                        adapter.notifyDataSetChanged();
                        Bundle bundle = new Bundle();
                        bundle.putString(PayResultFragment.PAY_ORDERS_ID, ordersId);
                        bundle.putDouble(PayResultFragment.PAY_RESULT_MONEY, sum);
                        bundle.putInt(PayResultFragment.PAY_RESULT_TYPE, PayResultFragment.CONFIRM_ORDERS);
                        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_CONFIRM_PAY, getActivity(), BaseSwitchActivity.class, PayResultFragment.class, bundle);
                    }
                });
            }

            @Override
            public void onItemClick(int position) {
                launchOrdersDetail(position);
            }

            @Override
            public void onExtraBtnClick(int position) {
                turnToLogisticScence(position);
            }
        };
    }
}
