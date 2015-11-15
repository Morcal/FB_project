package com.feibo.snacks.view.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.module.home.NewProductAdapter;
import com.feibo.snacks.view.util.GoodsHelper;
import com.feibo.snacks.view.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import fbcore.utils.Strings;

public class RecGoodsContainer {

    public static View getTodayNewProduct(Context context) {
        if (context == null) {
            return null;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_new_product, null);
        ViewHolder holder = new ViewHolder();
        holder.img = (ImageView) view.findViewById(R.id.item_home_promotion_img);
        holder.tag = (TextView) view.findViewById(R.id.item_home_product_tag);
        holder.name = (TextView) view.findViewById(R.id.item_home_promotion_desc);
        holder.curPrice = (TextView) view.findViewById(R.id.item_goods_cur_price);
        holder.sellEmpty = (ImageView) view.findViewById(R.id.item_goods_empty);
        view.setTag(holder);
        return view;
    }

    public static View getRecView(Context context) {
        if (context == null) {
            return null;
        }
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_home_product, null);

        View leftRoot = convertView.findViewById(R.id.item_home_product_left);
        View rightRoot = convertView.findViewById(R.id.item_home_product_right);

        List<ViewHolder> holders = new ArrayList<ViewHolder>();
        ViewHolder lefthHolder = new ViewHolder();
        ViewHolder rightHolder = new ViewHolder();

        generateHolder(leftRoot, lefthHolder);
        generateHolder(rightRoot, rightHolder);

        holders.add(lefthHolder);
        holders.add(rightHolder);
        convertView.setTag(holders);
        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void fillCollectGoodsView(final int position, final Goods goods, final ViewHolder holder,
            final OnItemClickListener listener) {
        if (goods != null) {
            holder.name.setText(goods.title);

            holder.curPrice.setText("￥" + goods.price.current);
            holder.oriPrice.setText("￥" + goods.price.prime);
            holder.oriPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if (goods.tag != null && !"".equals(goods.tag.title) && !Strings.isEmpty(goods.tag.title)) {
                UIUtil.setViewVisible(holder.discount);
                holder.discount.setText(goods.tag.title);
            }
            UIUtil.setDefaultImage(goods.img.imgUrl, holder.img);
            if (goods.status != 0) {// 商品售空, 下架
                if (goods.status == 2) {
                    holder.sellEmpty.setImageResource(R.drawable.icon_off_shelf);
                } else {
                    holder.sellEmpty.setImageResource(R.drawable.icon_sold);
                }
                UIUtil.setViewVisible(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c3));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c3));
                UIUtil.setBackGround(holder.discount, holder.name.getResources().getDrawable(R.drawable.bg_sales_empty));
                holder.img.setAlpha(0.5f);
            } else {
                UIUtil.setViewGone(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c1));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c2));
                UIUtil.setBackGround(holder.discount, holder.name.getResources().getDrawable(R.drawable.bg_sales));
                holder.img.setAlpha(1f);
            }
        }
        if (listener == null) {
            return;
        }
        holder.viewGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void fillView(final int position, final Goods goods, final ViewHolder holder,
            final OnItemClickListener listener) {
        if (goods != null) {
            holder.name.setText(goods.title);

            holder.curPrice.setText(holder.curPrice.getResources().getString(R.string.show_cur_price, goods.price.current));
            holder.oriPrice.setText(holder.oriPrice.getResources().getString(R.string.show_cur_price, goods.price.prime));
            holder.oriPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if (goods.tag != null && !"".equals(goods.tag.title) && !Strings.isEmpty(goods.tag.title)) {
                UIUtil.setViewVisible(holder.discount);
                holder.discount.setText(goods.tag.title);
            } else {
                UIUtil.setViewGone(holder.discount);
            }
            UIUtil.setDefaultImage(goods.img.imgUrl, holder.img);
            if (goods.status != 0) {// 商品售空
                UIUtil.setViewVisible(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c3));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c3));
                UIUtil.setBackGround(holder.discount, holder.name.getResources().getDrawable(R.drawable.bg_sales_empty));
                holder.img.setAlpha(0.5f);
            } else {
                UIUtil.setViewGone(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c1));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c2));
                UIUtil.setBackGround(holder.discount, holder.name.getResources().getDrawable(R.drawable.bg_sales));
                holder.img.setAlpha(1f);
            }
        }
    }

    public static void fillViewNew(Goods goods, View view, NewProductAdapter.ISellingEndListener listener) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (goods != null) {
            holder.name.setText(goods.title);
            holder.curPrice.setText(holder.name.getResources().getString(R.string.show_cur_price, goods.price.current));
            UIUtil.setHomeTodayImage(goods.img.imgUrl, holder.img);
            GoodsHelper.showRestTime(holder.tag, goods.time, listener);
            if (TimeUtil.isEnd(goods.time)) {
                UIUtil.setViewVisible(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c8));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c8));
                holder.img.setAlpha(128);
            } else {
                UIUtil.setViewGone(holder.sellEmpty);
                holder.curPrice.setTextColor(holder.name.getResources().getColor(R.color.c1));
                holder.name.setTextColor(holder.name.getResources().getColor(R.color.c2));
                holder.img.setAlpha(255);
            }
        }
    }

    private static void generateHolder(View root, ViewHolder holder) {
        holder.viewGroup = root;
        holder.name = (TextView) root.findViewById(R.id.item_goods_name);
        holder.img = (ImageView) root.findViewById(R.id.item_goods_img);
        holder.curPrice = (TextView) root.findViewById(R.id.item_goods_cur_price);
        holder.oriPrice = (TextView) root.findViewById(R.id.item_goods_ori_price);
        holder.collectSelectImageView = (ImageView) root.findViewById(R.id.item_collect_goods_select);
        holder.discount = (TextView) root.findViewById(R.id.item_home_promotion_discount);
        holder.sellEmpty = (ImageView) root.findViewById(R.id.item_goods_empty);
    }

    public static class ViewHolder {
        public View viewGroup;
        public TextView name;
        public TextView curPrice;
        public ImageView img;
        public TextView oriPrice;
        public TextView tag;
        public ImageView collectSelectImageView;
        public TextView discount;
        public ImageView sellEmpty;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
