package com.feibo.snacks.view.module.person.orders.ordersdetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.view.widget.SimpleItemView;

import java.util.List;

/**
 * Created by hcy on 2015/7/28.
 */
public class OrdersDetailFooter {

    private ViewGroup footerFlowParent;
    private View root;
    private ViewHolder viewHolder;
    private Context context;

    public OrdersDetailFooter(Context context) {
        this.context = context;
        root = LayoutInflater.from(context).inflate(R.layout.layout_orders_detail_footer, null);
        footerFlowParent = (ViewGroup) root.findViewById(R.id.orders_detail_footer_pay_flow);
        View payParent = root.findViewById(R.id.orders_detail_footer_pay);
        viewHolder = new ViewHolder(payParent);
    }

    public View getRoot() {
        return root;
    }

    public void refreshView(OrdersDetail ordersDetail) {
        List<String> infos = ordersDetail.infos;
        for (String info : infos) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_orders_info, null);
            TextView tv = (TextView) view.findViewById(R.id.item_orders_info_tv);
            tv.setText(info);
            footerFlowParent.addView(view);
        }

        viewHolder.costGoodsItem.setValueText("￥" + ordersDetail.sumGoods);
        viewHolder.costPostItem.setValueText("￥" + ordersDetail.sendPay);
        if (ordersDetail.validationAmount == 0) {
            viewHolder.couponItem.setVisibility(View.GONE);
        } else {
            viewHolder.couponItem.setVisibility(View.VISIBLE);
            viewHolder.couponItem.setValueText("￥" + ordersDetail.validationAmount);
        }
        if (ordersDetail.discountAmount == 0) {
            viewHolder.couponAmocunt.setVisibility(View.GONE);
        } else {
            viewHolder.couponAmocunt.setVisibility(View.VISIBLE);
            viewHolder.couponAmocunt.setValueText("￥" + ordersDetail.discountAmount);
        }
        viewHolder.costAllItem.setValueText("￥" + ordersDetail.finalSum);
    }
    
    private static class ViewHolder {
        private View root;
        public SimpleItemView costPostItem;
        public SimpleItemView costAllItem;
        public SimpleItemView couponItem;
        public SimpleItemView costGoodsItem;
        public SimpleItemView couponAmocunt;

        public ViewHolder(View root) {
            this.root = root;
            initWidget();
        }

        private void initWidget() {
            costPostItem = (SimpleItemView) root.findViewById(R.id.item_cost_post);
            costAllItem = (SimpleItemView) root.findViewById(R.id.item_cost_all);
            couponItem = (SimpleItemView) root.findViewById(R.id.item_coupon);
            costGoodsItem = (SimpleItemView) root.findViewById(R.id.item_cost_goods);
            couponAmocunt = (SimpleItemView) root.findViewById(R.id.item_coupon_amount);
        }
    }
}
