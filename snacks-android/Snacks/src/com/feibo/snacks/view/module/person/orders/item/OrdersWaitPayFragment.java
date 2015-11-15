package com.feibo.snacks.view.module.person.orders.item;

import android.util.Log;

import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.manager.global.orders.unpaid.OrdersWaitPayManager;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.view.module.person.orders.OrdersBaseFragment;
import com.feibo.snacks.view.module.person.orders.util.OrdersOptHelper;
import com.feibo.snacks.view.module.person.orders.pay.PayHelper;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersWaitPayFragment extends OrdersBaseFragment {

    private OrdersWaitPayManager manager;
    @Override
    public AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView) {

        Log.i("OrderWaitPayFragment","fragment---->geneateOrderMAnager");
        manager = new OrdersWaitPayManager(iLoadingView);
        return manager;
    }

    @Override
    public OrdersAdapter.OrdersOptListener getOrderOptListener() {
        Log.i("OrderWaitPayFragment","fragment---->getOrderOptListener");

        return new OrdersAdapter.OrdersOptListener() {
            @Override
            public void onLeftBtnClick(final int position) {
                String ordersId = manager.getData(manager.getDataType()).get(position).id;
                OrdersOptHelper.cancelOrders(ordersId, getActivity(), new OrdersOptHelper.OnRefreshOrdersLintener() {
                    @Override
                    public void onRefresh() {
                        if (adapter == null) {
                            return;
                        }
                        manager.removeOrders(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onRightBtnClick(int position) {
                payOrders(position);
            }

            private void payOrders(int position) {
                BaseOrder orders = manager.getData(manager.getDataType()).get(position);
                PayParams params = new PayParams();
                params.setOrderSn(orders.id);
                PayHelper payHelper = new PayHelper(getActivity(), params, orders) {
                    @Override
                    public void onCancleOrders(String ordersId) {

                    }

                    @Override
                    public void onTurnResult() {
                        manager.loadData();
                    }

                    @Override
                    public void onRefreshView() {

                    }
                };
                payHelper.payOrders4WaitPay();
            }

            @Override
            public void onItemClick(int position) {
                launchOrdersDetail(position);
            }

            @Override
            public void onExtraBtnClick(int position) {

            }
        };
    }
}
