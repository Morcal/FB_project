package com.feibo.snacks.view.module.person.orders.logistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/28.
 */
public class LogisticsAdapter  extends BaseSingleTypeAdapter<CartItem4Type> {

    public LogisticsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LogisticsDetailHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_logistics_detail, null);
            holder = new LogisticsDetailHolder();
            holder.storeName = (TextView) view.findViewById(R.id.item_orders_store_name);
            holder.iconIv = (ImageView) view.findViewById(R.id.item_orders_detail_goods_icon);
            holder.goodsTitle = (TextView) view.findViewById(R.id.item_orders_detail_goods_title);
            holder.goodsKinds = (TextView) view.findViewById(R.id.item_orders_kinds);
            holder.goodsPrice = (TextView) view.findViewById(R.id.item_orders_detail_price);
            holder.goodsCount = (TextView) view.findViewById(R.id.item_orders_detail_count);

            holder.headView = view.findViewById(R.id.item_orders_detail_head);
            view.setTag(holder);
        }
        holder = (LogisticsDetailHolder) view.getTag();
        CartItem4Type item4Type = getItem(i);
        UIUtil.setDefaultImage(item4Type.item.img.imgUrl, holder.iconIv);
        if (i == 0) {
            holder.headView.setVisibility(View.VISIBLE);
        } else {
            holder.headView.setVisibility(View.GONE);
        }
        holder.storeName.setText(item4Type.suppliers.name);
        holder.goodsTitle.setText(item4Type.item.goodsTitle);
        holder.goodsKinds.setText(item4Type.item.kinds);
        holder.goodsPrice.setText("￥" + item4Type.item.price.current);
        holder.goodsCount.setText("x" + item4Type.item.num);
        return view;
    }

    private static class LogisticsDetailHolder {
        public View headView;
        public TextView storeName;
        public TextView goodsTitle;
        public TextView goodsKinds;
        public ImageView iconIv;
        public TextView goodsPrice;
        public TextView goodsCount;
    }
}
