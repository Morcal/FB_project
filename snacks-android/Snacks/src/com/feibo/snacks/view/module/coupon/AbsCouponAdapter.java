package com.feibo.snacks.view.module.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.DiscountCoupon;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by Jayden on 2015/9/6.
 */
public abstract class AbsCouponAdapter extends BaseSingleTypeAdapter<DiscountCoupon>{

    private IClickCouponDetail listener;

    public AbsCouponAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_coupons, null);
            viewHolder = new ViewHolder();
            viewHolder.rmbSymbol = (TextView) convertView.findViewById(R.id.rmb_symbol);
            viewHolder.couponPrice = (TextView) convertView.findViewById(R.id.coupon_price);
            viewHolder.couponName = (TextView) convertView.findViewById(R.id.coupon_name);
            viewHolder.couponValidPrice = (TextView) convertView.findViewById(R.id.coupon_valid_price);
            viewHolder.couponUseRange = (TextView) convertView.findViewById(R.id.coupon_use_range);
            viewHolder.couponDetail = (TextView) convertView.findViewById(R.id.coupon_detail);
            viewHolder.couponValidTime = (TextView) convertView.findViewById(R.id.coupon_valid_time);
            viewHolder.invalidImg = (ImageView) convertView.findViewById(R.id.invalid_img);
            viewHolder.couponInvalidCause = (TextView) convertView.findViewById(R.id.coupon_cause);
            viewHolder.couponDetailView = convertView.findViewById(R.id.coupon_detail_view);
            viewHolder.couponInvalidCauseBoard = convertView.findViewById(R.id.coupon_cause_board);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setData(position, viewHolder);
        handleCouponItem(position, viewHolder);
        clickCouponDetail(position, viewHolder);
        return convertView;
    }

    public abstract void setData(int position, ViewHolder viewHolder);

    public abstract void clickCouponDetail(int position, ViewHolder viewHolder);

    public abstract void handleCouponItem(int position, ViewHolder viewHolder);

    public void setListener(IClickCouponDetail listener) {
        this.listener = listener;
    }

    public IClickCouponDetail getListener() {
        return listener;
    }

    public static class ViewHolder {
        public ImageView invalidImg;
        public TextView rmbSymbol;
        public TextView couponPrice;
        public TextView couponName;
        public TextView couponValidPrice;
        public TextView couponUseRange;
        public TextView couponValidTime;
        public TextView couponDetail;
        public TextView couponInvalidCause;
        public View couponInvalidCauseBoard;
        public View couponDetailView;
    }

    public interface IClickCouponDetail {
        void clickCouponDetail(long id,String name);
    }
}
