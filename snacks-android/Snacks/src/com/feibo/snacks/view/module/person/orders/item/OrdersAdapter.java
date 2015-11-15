package com.feibo.snacks.view.module.person.orders.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.view.module.person.orders.util.OrdersAdapterUtil;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/14.
 */
public class OrdersAdapter extends BaseSingleTypeAdapter<ItemOrder>{

    private static final int TYPE_ORDERS_COMPLEX = 1;
    private static final int TYPE_ORDERS_SIMPLE = 0;

    private OrdersOptListener listener;

    public OrdersAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ItemOrder itemOrder = getItem(position);
        if (itemOrder == null) {
            return super.getItemViewType(position);
        }
        if (itemOrder.multi != null && itemOrder.multi.size() > 0) {
            return TYPE_ORDERS_COMPLEX;
        } else {
            return TYPE_ORDERS_SIMPLE;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        ItemOrder order = getItem(i);
        if (TYPE_ORDERS_SIMPLE == type) {
            return OrdersAdapterUtil.getOrders4Simple(mContext, i, view, order, listener);
        } else if (TYPE_ORDERS_COMPLEX == type) {
            return OrdersAdapterUtil.getOrders4Complex(mContext, i, view, order, listener);
        }
        return null;
    }

    public void setOrdersOptListener(OrdersOptListener listener) {
        this.listener = listener;
    }

    public static interface OrdersOptListener {

        void onLeftBtnClick(int position);

        void onRightBtnClick(int position);

        void onItemClick(int position);

        void onExtraBtnClick(int position);

    }
}
