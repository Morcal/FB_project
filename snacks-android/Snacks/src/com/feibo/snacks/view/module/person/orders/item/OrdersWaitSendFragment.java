package com.feibo.snacks.view.module.person.orders.item;

import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.manager.global.orders.paid.OrdersWaitSendManager;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.view.module.person.orders.OrdersBaseFragment;
import com.feibo.snacks.view.module.person.orders.util.OrdersOptHelper;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersWaitSendFragment extends OrdersBaseFragment {

    private OrdersWaitSendManager manager;
    @Override
    public AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView) {
        manager = new OrdersWaitSendManager(iLoadingView);
        return manager;
    }

    @Override
    public OrdersAdapter.OrdersOptListener getOrderOptListener() {
        return new OrdersAdapter.OrdersOptListener() {
            @Override
            public void onLeftBtnClick(int position) {
                //申请退款
                ServiceContact contact = ContactManager.getInstance().getServiceContact();
                OrdersOptHelper.refoundOrder(getActivity(), contact);
            }

            @Override
            public void onRightBtnClick(int position) {
                OrdersOptHelper.remindSendOut(getActivity(), manager.getData(manager.getDataType()).get(position).id);
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
