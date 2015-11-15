package com.feibo.snacks.view.module.person.orders.ordersconfirm;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/20.
 */
public class OrdersConfirmAdapter extends BaseSingleTypeAdapter<CartItem4Type> {

    private int touchedPosition = -1;
    private OnEditCommentListener listener;

    public OrdersConfirmAdapter(Context context) {
        super(context);
    }

    public void setOnEditCommentListener(OnEditCommentListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        OrdersDetailHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_orders_confirm, null);
            holder = new OrdersDetailHolder();
            holder.storeName = (TextView) view.findViewById(R.id.item_orders_store_name);
            holder.iconIv = (ImageView) view.findViewById(R.id.item_orders_detail_goods_icon);
            holder.goodsTitle = (TextView) view.findViewById(R.id.item_orders_detail_goods_title);
            holder.goodsKinds = (TextView) view.findViewById(R.id.item_orders_kinds);
            holder.goodsPrice = (TextView) view.findViewById(R.id.item_orders_detail_price);
            holder.goodsPrePrice = (TextView) view.findViewById(R.id.orders_confirm_pre_price);
            holder.goodsCount = (TextView) view.findViewById(R.id.item_orders_detail_count);
            holder.ordersSendPay = (TextView) view.findViewById(R.id.item_orders_detail_send_pay);
            holder.ordersPay = (TextView) view.findViewById(R.id.item_orders_detail_account);
            holder.noteEdt = (TextView) view.findViewById(R.id.item_orders_confirm_note_edit);
            holder.goodsFailure = (TextView) view.findViewById(R.id.item_orders_confirm_goods_failure);
            holder.totalGoods = (TextView) view.findViewById(R.id.item_orders_confirm_totle_goods);

            holder.headView = view.findViewById(R.id.item_orders_detail_head);
            holder.footerView = view.findViewById(R.id.item_orders_detail_footer);
            view.setTag(holder);
        }
        holder = (OrdersDetailHolder) view.getTag();
        CartItem4Type cartItem4Type = getItem(i);
        CartItem4Type.getCartType(cartItem4Type);
        if (cartItem4Type.type == CartItem4Type.CART_ITEM_HEAD) {
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.GONE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_FOOTER) {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.VISIBLE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_ONLY_ONE){
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.VISIBLE);
        } else {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.GONE);
        }

        if (cartItem4Type.item.state == CartItem.FAILURE) {
            holder.goodsFailure.setVisibility(View.VISIBLE);
            holder.goodsFailure.setText(R.string.orders_goods_no_use);
            holder.goodsTitle.setEnabled(false);
            holder.goodsPrice.setEnabled(false);
        } else if (cartItem4Type.item.state == CartItem.NORMAL) {
            holder.goodsFailure.setVisibility(View.GONE);
            holder.goodsTitle.setEnabled(true);
            holder.goodsPrice.setEnabled(true);
        } else if (cartItem4Type.item.state == CartItem.EMPTY) {
            holder.goodsFailure.setVisibility(View.VISIBLE);
            holder.goodsFailure.setText(R.string.orders_goods_empty);
        } else if (cartItem4Type.item.state == CartItem.OFF_SHELF) {
            holder.goodsFailure.setVisibility(View.VISIBLE);
            holder.goodsFailure.setText(R.string.off_shelf);
        }

        holder.position = i;
        holder.storeName.setText(cartItem4Type.suppliers.name);
        holder.goodsTitle.setText(cartItem4Type.item.goodsTitle);
        holder.goodsKinds.setText(cartItem4Type.item.kinds);
        CharSequence curPrice = mContext.getResources().getString(R.string.show_cur_price, cartItem4Type.item.price.current);
        holder.goodsPrice.setText(curPrice);
        CharSequence primePrice = mContext.getResources().getString(R.string.show_cur_price, cartItem4Type.item.price.prime);
        holder.goodsPrePrice.setText(primePrice);
        holder.goodsPrePrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        CharSequence freightPrice = mContext.getResources().getString(R.string.show_cur_price, cartItem4Type.suppliers.freight);
        holder.ordersSendPay.setText(freightPrice);
        CharSequence sumPrice = mContext.getResources().getString(R.string.show_cur_price, cartItem4Type.suppliers.sumPrice);
        holder.ordersPay.setText(sumPrice);
        holder.goodsCount.setText("x" + cartItem4Type.item.num);
        holder.totalGoods.setText("共" + getOrdersCount(cartItem4Type) + "件商品");
        UIUtil.setDefaultImage(cartItem4Type.item.img.imgUrl, holder.iconIv);
        if(cartItem4Type.note != null){
            holder.noteEdt.setText(cartItem4Type.note);
        } else {
            holder.noteEdt.setText("");
        }
        holder.noteEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.editComment(i);
            }
        });
        return view;
    }

    public int getOrdersCount(CartItem4Type cartItem4Type) {
        int num = 0;
        for (CartItem item : cartItem4Type.suppliers.items) {
            if (item.state == CartItem.NORMAL) {
                num += item.num;
            }
        }
        return num;
    }

    private static class OrdersDetailHolder {
        public View headView;
        public View footerView;
        public TextView storeName;
        public TextView goodsTitle;
        public TextView goodsKinds;
        public ImageView iconIv;

        public TextView totalGoods;
        public TextView goodsPrice;
        public TextView goodsPrePrice;
        public TextView goodsCount;
        public TextView ordersSendPay;
        public TextView ordersPay;
        public TextView goodsFailure;
        public TextView noteEdt;
        public int position;
    }

    public static interface OnEditCommentListener {
        void editComment(int i);
    }
}
