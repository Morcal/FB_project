package com.feibo.snacks.view.module.coupon;

import android.content.Context;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.util.UIUtil;

/**
 * Created by Jayden on 2015/9/6.
 */
public class InValidCouponAdapter extends AbsCouponAdapter {

    public InValidCouponAdapter(Context context) {
        super(context);
    }

    @Override
    public void setData(int position, ViewHolder viewHolder) {
        DiscountCoupon coupon = getItem(position);
        viewHolder.couponPrice.setText(String.valueOf(((int) coupon.value)));
        viewHolder.couponName.setText(coupon.title);
        viewHolder.couponValidPrice.setText(coupon.fillValue);
        viewHolder.couponUseRange.setText(coupon.msg);
        viewHolder.couponValidTime.setText(TimeUtil.getTimeRange(coupon.startTime, coupon.endTime));
        viewHolder.couponDetail.setText(viewHolder.couponDetail.getResources().getString(R.string.coupon_using));
        viewHolder.couponInvalidCause.setText(coupon.invalidateDesc);
    }

    @Override
    public void clickCouponDetail(final int position, ViewHolder viewHolder) {
    }

    @Override
    public void handleCouponItem(int position, ViewHolder viewHolder) {
        UIUtil.setViewVisible(viewHolder.couponInvalidCauseBoard);
        viewHolder.rmbSymbol.setTextColor(viewHolder.rmbSymbol.getResources().getColor(R.color.c3));
        viewHolder.couponPrice.setTextColor(viewHolder.couponPrice.getResources().getColor(R.color.c3));
        viewHolder.couponName.setTextColor(viewHolder.couponName.getResources().getColor(R.color.c3));
        viewHolder.couponValidPrice.setTextColor(viewHolder.couponValidPrice.getResources().getColor(R.color.c3));
        viewHolder.couponUseRange.setTextColor(viewHolder.couponUseRange.getResources().getColor(R.color.c3));
        viewHolder.couponValidTime.setTextColor(viewHolder.couponValidTime.getResources().getColor(R.color.c3));
        viewHolder.couponDetailView.setBackgroundDrawable(viewHolder.couponDetail.getResources().getDrawable(R.drawable.bg_gray_coupon));
    }
}
