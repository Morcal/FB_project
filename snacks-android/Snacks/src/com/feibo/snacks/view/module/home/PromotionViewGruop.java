package com.feibo.snacks.view.module.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Brand;
import com.feibo.snacks.model.bean.oldversion14.Goods;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.util.GoodsHelper;
import com.feibo.snacks.view.util.UIUtil;

import java.util.List;

public class PromotionViewGruop {

    private List<Goods> list;

    private Context context;

    public PromotionViewGruop(Context context, List<Goods> list) {
        this.list = list;
        this.context = context;
    }

    public static void filterData(Context context, Brand brand, View view, NewProductAdapter.ISellingEndListener listener) {
        PromotionViewHolder holder = (PromotionViewHolder) view.getTag();
        if (brand != null) {
            holder.discount.setText(brand.discount);
            holder.des.setText(brand.title);
            GoodsHelper.showRestTime(holder.timeTv,brand.time,listener);
            UIUtil.setImage(brand.img.imgUrl, holder.imageView, R.drawable.default_class_brand612_250, R.drawable.default_class_brand612_250);
            if(TimeUtil.isEnd(brand.time)) {//特卖结束
                UIUtil.setViewVisible(holder.sellEmpty);
                UIUtil.setViewGone(holder.timeTv);
                holder.des.setTextColor(context.getResources().getColor(R.color.c3));
                holder.discount.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_sales_empty));
                holder.imageView.setAlpha(128);
            } else {
                UIUtil.setViewGone(holder.sellEmpty);
                UIUtil.setViewVisible(holder.timeTv);
                holder.des.setTextColor(context.getResources().getColor(R.color.c2));
                holder.discount.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_sales));
                holder.imageView.setAlpha(255);
            }
        }
    }

    public static View generateView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_promotion, null);
        PromotionViewHolder holder = new PromotionViewHolder();
        holder.discount = (TextView) view.findViewById(R.id.item_home_promotion_discount);
        holder.imageView = (ImageView) view.findViewById(R.id.item_home_promotion_img);
        holder.timeTv = (TextView) view.findViewById(R.id.item_home_promotion_tag_time);
        holder.des = (TextView) view.findViewById(R.id.item_home_promotion_desc);
        holder.line = view.findViewById(R.id.line);
        holder.sellEmpty = (ImageView) view.findViewById(R.id.item_goods_empty);
        view.setTag(holder);
        return view;
    }

    public static class PromotionViewHolder {
        public TextView discount;
        public TextView timeTv;
        public TextView des;
        public ImageView imageView;
        public View line;
        public ImageView sellEmpty;
    }
}
