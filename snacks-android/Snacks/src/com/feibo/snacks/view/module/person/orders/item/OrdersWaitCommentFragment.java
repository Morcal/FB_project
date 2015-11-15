package com.feibo.snacks.view.module.person.orders.item;

import android.os.Bundle;

import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.manager.global.orders.paid.OrdersWaitCommentManager;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.orders.OrdersBaseFragment;
import com.feibo.snacks.view.module.person.orders.comment.OrdersCommentFragment;
import com.feibo.snacks.view.module.person.orders.logistics.LogisticsFragment;
import com.feibo.snacks.view.util.LaunchUtil;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersWaitCommentFragment extends OrdersBaseFragment {

    private OrdersWaitCommentManager manager;
    @Override
    public AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView) {
        manager = new OrdersWaitCommentManager(iLoadingView);
        return manager;
    }

    @Override
    public OrdersAdapter.OrdersOptListener getOrderOptListener() {
        return new OrdersAdapter.OrdersOptListener() {
            @Override
            public void onLeftBtnClick(int position) {
                String ordersId = manager.getData(manager.getDataType()).get(position).id;
                Bundle bundle = new Bundle();
                bundle.putString(LogisticsFragment.ORDERS_ID, ordersId);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LogisticsFragment.class, bundle);
            }

            @Override
            public void onRightBtnClick(int position) {
                String ordersId = manager.getData(manager.getDataType()).get(position).id;
                Bundle bundle = new Bundle();
                bundle.putString(OrdersCommentFragment.ORDERS_ID, ordersId);
                LaunchUtil.launchSubDetailForResult(LaunchUtil.REQUEST_ORDERS_COMMENT, getActivity(), BaseSwitchActivity.class, OrdersCommentFragment.class, bundle);
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
