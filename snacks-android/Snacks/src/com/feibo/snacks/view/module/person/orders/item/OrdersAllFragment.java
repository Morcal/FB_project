package com.feibo.snacks.view.module.person.orders.item;

import android.os.Bundle;

import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.manager.global.orders.OrdersListAllManager;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.orders.OrdersBaseFragment;
import com.feibo.snacks.view.module.person.orders.comment.OrdersCommentFragment;
import com.feibo.snacks.view.module.person.orders.logistics.LogisticsFragment;
import com.feibo.snacks.view.module.person.orders.pay.PayResultFragment;
import com.feibo.snacks.view.module.person.orders.util.OrdersOptHelper;
import com.feibo.snacks.view.module.person.orders.pay.PayHelper;
import com.feibo.snacks.view.util.LaunchUtil;

import java.util.List;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersAllFragment extends OrdersBaseFragment {

    private OrdersListAllManager manager;
    @Override
    public AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView) {
        manager = new OrdersListAllManager(iLoadingView);
        return manager;
    }

    @Override
    public OrdersAdapter.OrdersOptListener getOrderOptListener() {
        return new OrdersAdapter.OrdersOptListener() {
            @Override
            public void onLeftBtnClick(final int position) {
                ItemOrder itemOrder = manager.getData(manager.getDataType()).get(position);
                int type = itemOrder.type;
                String ordersId = itemOrder.id;
                switch (type) {
                    case ItemOrder.WAIT_PAY: {
                        OrdersOptHelper.cancelOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                            @Override
                            public void onRefresh() {
                                if (adapter == null) {
                                    return;
                                }
                                if (manager == null) {
                                    return;
                                }
                                manager.removeOrders(position);
                                adapter.notifyDataSetChanged();
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

            @Override
            public void onRightBtnClick(final int position) {
                List<ItemOrder> data = manager.getData(manager.getDataType());
                if(data == null || data.size() < 1){
                    return;
                }
                ItemOrder itemOrder = data.get(position);
                int type = itemOrder.type;
                final String ordersId = itemOrder.id;
                switch (type) {
                    case ItemOrder.WAIT_PAY: {
                        payOrders(position);
                        break;
                    }
                    case ItemOrder.WAIT_SEND: {
                        OrdersOptHelper.remindSendOut(getActivity(), data.get(position).id);
                        break;
                    }
                    case ItemOrder.WAIT_GET: {
                        confirmOrders(position, itemOrder);
                        break;
                    }
                    case ItemOrder.TRADE_SUCCESS: {
                        deleteOrders(position, ordersId);
                        break;
                    }
                    case ItemOrder.TRADE_FAIL: {
                        deleteOrders(position, ordersId);
                        break;
                    }
                    case ItemOrder.WAIT_COMMENT: {
                        Bundle bundle = new Bundle();
                        bundle.putString(OrdersCommentFragment.ORDERS_ID, ordersId);
                        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_COMMENT, getActivity(), BaseSwitchActivity.class, OrdersCommentFragment.class, bundle);
                        break;
                    }
                    case ItemOrder.RETURN_GOODS_CHECK:
                    case ItemOrder.RETURN_GOODS_DOING: {
                        deleteOrders(position, ordersId);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            private void deleteOrders(final int position, String ordersId) {
                OrdersOptHelper.deleteOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                    @Override
                    public void onRefresh() {
                        if (adapter == null) {
                            return;
                        }
                        if (manager == null) {
                            return;
                        }
                        manager.removeOrders(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            private void confirmOrders(final int position, final ItemOrder itemOrder) {
                OrdersOptHelper.affirmOrders(itemOrder.id, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                    @Override
                    public void onRefresh() {
                        if (adapter == null) {
                            return;
                        }
                        if (manager == null) {
                            return;
                        }
                        manager.removeOrders(position);
                        adapter.notifyDataSetChanged();
                        Bundle bundle = new Bundle();
                        bundle.putString(PayResultFragment.PAY_ORDERS_ID, itemOrder.id);
                        bundle.putDouble(PayResultFragment.PAY_RESULT_MONEY, itemOrder.finalSum);
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
                ItemOrder itemOrder = manager.getData(manager.getDataType()).get(position);
                int type = itemOrder.type;
                String ordersId = itemOrder.id;
                switch (type) {
                    case ItemOrder.WAIT_GET: {
                        turnToLogisticsScence(ordersId);
                        break;
                    }
                    default:
                        break;
                }
            }
        };
    }

    private void payOrders(int position) {
        BaseOrder orders = manager.getData(manager.getDataType()).get(position);
        PayParams params = new PayParams();
        params.setOrderSn(orders.id);
        params.setPosterIds(orders.posterIds);
        PayHelper payHelper = new PayHelper(getActivity(), params, orders) {
            @Override
            public void onCancleOrders(String ordersId) {

            }

            @Override
            public void onTurnResult() {
                if (manager == null) {
                    return;
                }
                manager.loadData();
            }

            @Override
            public void onRefreshView() {

            }
        };
        payHelper.payOrders4WaitPay();
    }

    private void turnToLogisticsScence(String ordersId) {
        Bundle bundle = new Bundle();
        bundle.putString(LogisticsFragment.ORDERS_ID, ordersId);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LogisticsFragment.class, bundle);
    }
}
