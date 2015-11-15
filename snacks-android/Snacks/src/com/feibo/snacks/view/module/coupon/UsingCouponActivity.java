package com.feibo.snacks.view.module.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.orders.ordersconfirm.OrdersConfirmFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.widget.SlidingFinishView;

import java.util.ArrayList;

/**
 * Created by Jayden on 2015/9/2.
 */
public class UsingCouponActivity extends BaseActivity implements View.OnClickListener {

    private static final String DICOUPON_DETAIL_TITLE = "使用优惠券";
    private static final String DICOUPON_RIGHT_TITLE = "使用规则";
    public static final String GOODS_ID = "goodsId";
    public static final String COUPON_TYPE = "couponType";
    public static final String ADDRESS_ID = "addressId";
    public static final int COUPON_VALID = 0;
    public static final int COUPON_INVALID = 1;

    private TextView title;
    private TextView rightTitle;
    private ImageView backImage;
    private RadioButton validRb;
    private RadioButton invalidRb;
    private ViewPager mViewPager;


    //以下三个参数是从确认订单页传过来的
    private int couponType;       //0,有效优惠券页面，1，无效优惠券页面
    private String goodsId;
    private String addressId;       //地址条目id,为-1时使用默认地址。

    //以下四个参数是退出使用优惠券，需要返回给确认订单页
    private OrdersDetail ordersDetail;
    private int resultType; //0:不做任何操作，1：点击了使用优惠券
    private String couponName;  //优惠券名称
    private long couponId;      //优惠券id
    private int validNumber;    //有效优惠券数量

    private ValidCouponFragment validCouponFragment;
    private InValidCouponFragment inValidCouponFragment;
    private CouponViewpagerAdapter fragmentAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        SlidingFinishView layout = (SlidingFinishView) LayoutInflater.from(this).inflate(
                R.layout.layout_sliding_finish, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_using_coupon);

        initParams();
        initWidget();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.valid_coupon: {
                mViewPager.setCurrentItem(COUPON_VALID);
                break;
            }
            case R.id.invalid_coupon: {
                mViewPager.setCurrentItem(COUPON_INVALID);
                break;
            }
            case R.id.head_left: {
                exitActivity();
                break;
            }
            case R.id.head_right: {
                LaunchUtil.launchAppActivity(this, BaseSwitchActivity.class, UsingRuleFragment.class, null);
                break;
            }
        }
    }

    private void initParams() {
        Bundle bundle = getIntent().getExtras();
        goodsId = bundle.getString(GOODS_ID);
        addressId = bundle.getString(ADDRESS_ID);
        couponType = bundle.getInt(COUPON_TYPE, COUPON_VALID);
    }

    private void initWidget() {
        title = (TextView) findViewById(R.id.head_title);
        rightTitle = (TextView) findViewById(R.id.head_right);
        backImage = (ImageView) findViewById(R.id.head_left);
        title.setText(DICOUPON_DETAIL_TITLE);
        rightTitle.setText(DICOUPON_RIGHT_TITLE);
        backImage.setOnClickListener(this);
        rightTitle.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        validRb = (RadioButton) findViewById(R.id.valid_coupon);
        validRb.setOnClickListener(this);
        invalidRb = (RadioButton) findViewById(R.id.invalid_coupon);
        invalidRb.setOnClickListener(this);

        initFragments();
        fragmentAdapter = new CouponViewpagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);

        mViewPager.setCurrentItem(couponType);
        setIndicator(couponType);
        OnMainPageChangeListener listener = new OnMainPageChangeListener();
        mViewPager.setOnPageChangeListener(listener);
    }

    private void initFragments() {
        fragments.clear();
        validCouponFragment = ValidCouponFragment.getInstance(addressId,goodsId);
        inValidCouponFragment = InValidCouponFragment.getInstance(addressId,goodsId);
        fragments.add(validCouponFragment);
        fragments.add(inValidCouponFragment);
    }

    private void exitActivity() {
        Intent intent = new Intent();
        intent.putExtra(OrdersConfirmFragment.COUPON_ORDER_DETAIL,ordersDetail);
        intent.putExtra(OrdersConfirmFragment.COUPON_NAME,couponName);
        intent.putExtra(OrdersConfirmFragment.COUPON_ORDER_ID,couponId);
        intent.putExtra(OrdersConfirmFragment.VALID_COUPON_NUMBER,validNumber);
        setResult(resultType, intent);
        finish();
    }

    public void setValidNumber(int validNumber) {
        this.validNumber = validNumber;
    }

    //使用优惠券失败
    public void reFreshData() {
        inValidCouponFragment.reFreshData();
    }

    //使用优惠券成功
    public void usingCouponSuccess(OrdersDetail ordersDetail,String couponName,long couponId) {
        this.ordersDetail = ordersDetail;
        this.couponName = couponName;
        this.couponId = couponId;
        resultType = RESULT_OK;
        if (couponName != null && couponId != 0) {
            exitActivity();
        }
    }

    private class OnMainPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setIndicator(arg0);
        }
    }

    private void setIndicator(int arg0) {
        if (arg0 == COUPON_VALID) {
            validRb.setChecked(true);
            invalidRb.setChecked(false);
        } else {
            validRb.setChecked(false);
            invalidRb.setChecked(true);
        }
    }
}
