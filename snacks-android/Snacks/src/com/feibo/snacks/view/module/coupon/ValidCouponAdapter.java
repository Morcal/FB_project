package com.feibo.snacks.view.module.coupon;

import android.content.Context;
import android.view.View;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.util.TimeUtil;

/**
 * Created by Jayden on 2015/9/6.
 */
public class ValidCouponAdapter extends AbsCouponAdapter {

    public ValidCouponAdapter(Context context) {
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
    }

    @Override
    public void clickCouponDetail(final int position, ViewHolder viewHolder) {
        viewHolder.couponDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscountCoupon coupon = getItem(position);
                getListener().clickCouponDetail(coupon.id,coupon.title);
            }
        });
    }

    @Override
    public void handleCouponItem(int position, ViewHolder viewHolder) {
    }
}
