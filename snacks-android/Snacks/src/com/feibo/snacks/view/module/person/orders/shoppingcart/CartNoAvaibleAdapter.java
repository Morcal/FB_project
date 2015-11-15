package com.feibo.snacks.view.module.person.orders.shoppingcart;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.ratioroundimageview.RoundedImageView;

import java.util.List;

/**
 * Created by hcy on 2015/9/10.
 */
public abstract class CartNoAvaibleAdapter  {

    private Context mContext;
    private List<CartItem> list;
    private ViewGroup viewGroup;

    public CartNoAvaibleAdapter(Context context, ViewGroup viewGroup) {
        this.mContext = context;
        this.viewGroup = viewGroup;
    }

    public void setItems(List<CartItem> list) {
        this.list = list;
        fillChildView(viewGroup);
    }

    private void fillChildView(ViewGroup viewParent) {
        viewParent.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = getView(i);
            viewParent.addView(view);
        }
    }

    private View getView(int i) {
        ViewHolder holder;
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_orders_no_avalible, null);
        holder = new ViewHolder(view);
        view.setTag(holder);
        holder = (ViewHolder) view.getTag();
        CartItem item = list.get(i);
        holder.itemcartgoodstitle.setText(item.goodsTitle);
        holder.itemcartkinds.setText(item.kinds);
        holder.itemcartpricenow.setText("￥" + item.price.current);
        holder.itemcartpriceorign.setText("￥" + item.price.prime);
        holder.itemcartpriceorign.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        UIUtil.setDefaultImage(item.img.imgUrl, holder.itemcartgoodsicon);
        if (item.state == CartItem.NORMAL) {
            holder.itemcartgoodsfailure.setVisibility(View.GONE);
        } else if (item.state == CartItem.FAILURE){
            holder.itemcartgoodsfailure.setVisibility(View.VISIBLE);
            holder.itemcartgoodsfailure.setText(R.string.orders_goods_no_use);
        } else if (item.state == CartItem.EMPTY) {
            holder.itemcartgoodsfailure.setVisibility(View.VISIBLE);
            holder.itemcartgoodsfailure.setText(R.string.orders_goods_empty);
        } else if (item.state == CartItem.OFF_SHELF) {
            holder.itemcartgoodsfailure.setVisibility(View.VISIBLE);
            holder.itemcartgoodsfailure.setText(R.string.orders_goods_off_set);
        } else {
            holder.itemcartgoodsfailure.setVisibility(View.GONE);
        }

        holder.itemcartdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelete(i);
            }
        });
        return view;
    }

    public abstract void onItemDelete(int position);

    private static class ViewHolder {
        public final ImageView itemcartselect;
        public final RoundedImageView itemcartgoodsicon;
        public final TextView itemcartgoodsfailure;
        public final TextView itemcartpricenow;
        public final TextView itemcartpriceorign;
        public final ImageView itemcartdelete;
        public final TextView itemcartgoodstitle;
        public final TextView itemcartkinds;
        public final View root;

        public ViewHolder(View root) {
            itemcartselect = (ImageView) root.findViewById(R.id.item_cart_select);
            itemcartgoodsicon = (RoundedImageView) root.findViewById(R.id.item_cart_goods_icon);
            itemcartgoodsfailure = (TextView) root.findViewById(R.id.item_cart_goods_failure);
            itemcartpricenow = (TextView) root.findViewById(R.id.item_cart_price_now);
            itemcartpriceorign = (TextView) root.findViewById(R.id.item_cart_price_orign);
            itemcartdelete = (ImageView) root.findViewById(R.id.item_cart_delete);
            itemcartgoodstitle = (TextView) root.findViewById(R.id.item_cart_goods_title);
            itemcartkinds = (TextView) root.findViewById(R.id.item_cart_kinds);
            this.root = root;
        }
    }
}
