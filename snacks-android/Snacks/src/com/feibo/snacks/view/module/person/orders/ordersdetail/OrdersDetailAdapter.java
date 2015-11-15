package com.feibo.snacks.view.module.person.orders.ordersdetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.BaseOrder;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/20.
 */
public class OrdersDetailAdapter extends BaseSingleTypeAdapter<CartItem4Type> {

    private OnOrdersDetailOptListener listener;
    public OrdersDetailAdapter(Context context) {
        super(context);
    }

    public void setOnOrdersDetailOptListener(OnOrdersDetailOptListener listener) {
        this.listener = listener;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OrdersDetailHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_orders_detail, null);
            holder = new OrdersDetailHolder();
            holder.storeName = (TextView) view.findViewById(R.id.item_orders_store_name);
            holder.iconIv = (ImageView) view.findViewById(R.id.item_orders_detail_goods_icon);
            holder.goodsTitle = (TextView) view.findViewById(R.id.item_orders_detail_goods_title);
            holder.goodsKinds = (TextView) view.findViewById(R.id.item_orders_kinds);
            holder.goodsPrice = (TextView) view.findViewById(R.id.item_orders_detail_price);
            holder.goodsCount = (TextView) view.findViewById(R.id.item_orders_detail_count);
            holder.ordersSendPay = (TextView) view.findViewById(R.id.item_orders_detail_send_pay);
            holder.ordersPay = (TextView) view.findViewById(R.id.item_orders_detail_account);
            holder.diviView = view.findViewById(R.id.imageView6);
            holder.goodsState = (TextView) view.findViewById(R.id.item_orders_detail_goods_no_use);
            holder.serviceBtn = (Button) view.findViewById(R.id.item_orders_detail_service);
            holder.footerView = view.findViewById(R.id.item_orders_detail_footer);
            holder.headView = view.findViewById(R.id.item_orders_detail_head);
            view.setTag(holder);
        }
        holder = (OrdersDetailHolder) view.getTag();
        CartItem4Type cartItem4Type = getItem(i);

        if (cartItem4Type.type == CartItem4Type.CART_ITEM_HEAD) {
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.GONE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_FOOTER) {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.VISIBLE);
            holder.diviView.setVisibility(View.GONE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_ONLY_ONE){
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.VISIBLE);
            holder.diviView.setVisibility(View.GONE);
        } else {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.GONE);
        }

        if (cartItem4Type.item.state == CartItem.NORMAL) {
            holder.goodsState.setVisibility(View.GONE);
        } else {
            holder.goodsState.setVisibility(View.VISIBLE);
            if (cartItem4Type.item.state == CartItem.FAILURE) {
                holder.goodsState.setText(R.string.orders_goods_no_use);
            } else if (cartItem4Type.item.state == CartItem.EMPTY) {
                holder.goodsState.setText(R.string.orders_goods_empty);
            } else {
                holder.goodsState.setVisibility(View.GONE);
            }
        }
        UIUtil.setDefaultImage(cartItem4Type.item.img.imgUrl, holder.iconIv);

        setServiceState(i, holder, cartItem4Type);

        holder.storeName.setText(cartItem4Type.suppliers.name);
        holder.goodsTitle.setText(cartItem4Type.item.goodsTitle);
        holder.goodsKinds.setText(cartItem4Type.item.kinds);
        holder.goodsPrice.setText("￥" + cartItem4Type.item.price.current);
      //  holder.ordersSendPay.setText("￥" + cartItem4Type.suppliers.freight);
      //  holder.ordersPay.setText("￥" + cartItem4Type.suppliers.sumPrice);
        holder.goodsCount.setText("x" + cartItem4Type.item.num);
        return view;
    }

    private void setServiceState(final int i, OrdersDetailHolder holder, CartItem4Type cartItem4Type) {
        if (cartItem4Type.type == BaseOrder.TRADE_SUCCESS) {
            holder.serviceBtn.setVisibility(View.GONE);
        } else {
            holder.serviceBtn.setVisibility(View.GONE);
        }
        holder.serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onServiceClick(i);
            }
        });
    }

    private static class OrdersDetailHolder {
        public View headView;
        public View footerView;
        public TextView storeName;
        public TextView goodsTitle;
        public TextView goodsKinds;
        public ImageView iconIv;
        public TextView goodsState;
        public Button serviceBtn;

        public TextView goodsPrice;
        public TextView goodsCount;
        public TextView ordersSendPay;
        public TextView ordersPay;
        public View diviView;
    }

    public static interface OnOrdersDetailOptListener {
        void onServiceClick(int position);
    }
}
