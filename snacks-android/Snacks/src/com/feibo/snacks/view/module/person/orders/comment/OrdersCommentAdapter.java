package com.feibo.snacks.view.module.person.orders.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/21.
 */
public class OrdersCommentAdapter extends BaseSingleTypeAdapter<CartItem> {


    public OrdersCommentAdapter(Context context) {
        super(context);
    }

    private OnEditCommentListener listener;

    public void setOnEditCommentListener(OnEditCommentListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        OdersCommentHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_orders_comment, null);
            holder = new OdersCommentHolder();
            holder.goodsIcon = (ImageView) view.findViewById(R.id.item_orders_goods_icon);
            holder.goodsTitle = (TextView) view.findViewById(R.id.item_orders_goods_title);
            holder.goodsKinds = (TextView) view.findViewById(R.id.item_orders_kinds);
            holder.goodsCount = (TextView) view.findViewById(R.id.item_orders_goods_count);
            holder.commentEdit = (TextView) view.findViewById(R.id.item_orders_comment_edit);
            holder.goodsPrices = (TextView) view.findViewById(R.id.item_orders_price);
            view.setTag(holder);
        }
        holder = (OdersCommentHolder) view.getTag();
        CartItem cartItem = getItem(i);
        UIUtil.setDefaultImage(cartItem.img.imgUrl, holder.goodsIcon);
        CharSequence curPrice = mContext.getResources().getString(R.string.show_cur_price, cartItem.price.current);
        holder.goodsPrices.setText(curPrice);
        holder.goodsTitle.setText(cartItem.goodsTitle);
        holder.goodsKinds.setText(cartItem.kinds);
        holder.goodsCount.setText("X" + cartItem.num);
        if (!TextUtils.isEmpty(cartItem.commentContent)) {
            holder.commentEdit.setText(cartItem.commentContent);
        } else {
            holder.commentEdit.setText("");
        }

        holder.commentEdit.setOnClickListener(new View.OnClickListener() {
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

    public static class OdersCommentHolder {
        public ImageView goodsIcon;
        public TextView goodsTitle;
        public TextView goodsKinds;
        public TextView goodsPrices;
        public TextView goodsCount;
        public TextView commentEdit;
        public int position;
    }

    public static interface OnEditCommentListener {
        void editComment(int i);
    }
}