package com.feibo.snacks.view.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.view.base.RecGoodsContainer.ViewHolder;

import java.util.List;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by Jayden on 2015/7/2.
 */
public class BaseDoubleGoodsAdapter extends BaseSingleTypeAdapter<Goods> {

    private OnGoodsClickListener listener;

    public BaseDoubleGoodsAdapter(Context context) {
       super(context);
    }

    public void setListener(OnGoodsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = RecGoodsContainer.getRecView(mContext);
        }
        @SuppressWarnings("unchecked")
        List<RecGoodsContainer.ViewHolder> holders = (List<RecGoodsContainer.ViewHolder>) convertView.getTag();

        List<Goods> items = mItems;
        if (items != null && position * 2 < items.size()) {
            Goods leftGoods = items.get(position * 2);
            RecGoodsContainer.ViewHolder holder = holders.get(0);
            RecGoodsContainer.fillView(position * 2, leftGoods, holder, null);
            addListener(leftGoods,position * 2, holder);

            if (items.size() > (position * 2 + 1)) {
                Goods rightGoods = items.get(position * 2 + 1);
                RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                RecGoodsContainer.fillView(position * 2 + 1, rightGoods, rightholder, null);
                addListener(rightGoods,position * 2 + 1, rightholder);
                rightholder.viewGroup.setVisibility(View.VISIBLE);
            } else {
                RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                rightholder.viewGroup.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private void addListener(final Goods goods,final int position, ViewHolder holder) {
        holder.viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if(goods.status != 0) {
//                        RemindControl.showSimpleToast(mContext, R.string.goods_sell_empty);
                        return;
                    }
                    listener.onGoodsClick(position);
                }
            }
        });
    }

    public interface OnGoodsClickListener {
        void onGoodsClick(int position);
    }

    @Override
    public int getCount() {
        if(mItems == null) {
            return 0;
        }
        return mItems.size() % 2 == 0 ? mItems.size() / 2 : mItems.size() / 2 + 1;
    }
}
