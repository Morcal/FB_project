package com.feibo.snacks.view.module.goods.goodsdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.SubKind;

import java.util.List;

/**
 * Created by Jayden on 2015/7/13.
 */
public class FlavorAdpter extends RecyclerView.Adapter<FlavorAdpter.ViewHolder> {

    public static final int DETAULT_FLAVOR = -1;

    private Context context;
    private List<SubKind> subKinds;
    private int selectId = DETAULT_FLAVOR;

    public FlavorAdpter(Context context, List<SubKind> kinds) {
        this.context = context;
        subKinds = kinds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flavor,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.flavor.setText(subKinds.get(position).title);
        holder.flavor.setTextOn(subKinds.get(position).title);
        holder.flavor.setTextOff(subKinds.get(position).title);
        setItemBackGround(holder.flavor,position);
        if (subKinds.get(position).surplusNum <= 0) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setClickable(false);
        } else {
            holder.itemView.setAlpha(1);
            holder.itemView.setClickable(true);
        }
        holder.flavor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.flavor.setChecked(isChecked);
                if (selectId == position) {
                    selectId = DETAULT_FLAVOR;
                    listener.onItemClick(DETAULT_FLAVOR);
                    holder.flavor.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_taste_normal2x));
                } else {
                    selectId = position;
                    listener.onItemClick(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void setItemBackGround(View view,int pos) {
        if(selectId == pos) {
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_taste_selected2x));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_taste_normal2x));
        }
    }

    private void setBackGroundByIsCheck(View view,boolean pos) {
        if(pos) {
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_taste_selected2x));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_taste_normal2x));
        }
    }

    @Override
    public int getItemCount() {
        return subKinds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ToggleButton flavor;

        public ViewHolder(View view) {
            super(view);
            flavor = (ToggleButton) view.findViewById(R.id.item_flavor);
        }
    }

    public int getSelectId() {
        return selectId;
    }

    private OnKindItemClickListener listener;

    public void setListener(OnKindItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnKindItemClickListener {
        void onItemClick(int pos);
    }
}
