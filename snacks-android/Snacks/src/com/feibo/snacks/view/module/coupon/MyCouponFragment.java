package com.feibo.snacks.view.module.coupon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.module.coupon.MyCouponManager;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class MyCouponFragment extends BaseTitleFragment {

    private static final String DICOUPON_DETAIL_TITLE = "我的优惠券";
    private static final String DICOUPON_RIGHT_TITLE = "使用规则";

    private View rootView;
    private ListView listView;
    private TextView rightTitle;

    private MyCouponManager manager;
    private MyCouponAdapter adapter;
    private List<DiscountCoupon> coupons;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_valid_coupon, null);

        initTitle();
        initListener();
        initWidget();
        showData();
        return rootView;
    }

    private void initTitle() {
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        rightTitle = (TextView) getTitleBar().headView.findViewById(R.id.head_right);
        titleView.setText(DICOUPON_DETAIL_TITLE);
        UIUtil.setViewVisible(rightTitle);
        rightTitle.setText(DICOUPON_RIGHT_TITLE);
        rightTitle.setTextColor(getResources().getColor(R.color.c1));
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        rightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchAppActivity(getActivity(), BaseSwitchActivity.class, UsingRuleFragment.class, null);
            }
        });
    }

    private void initWidget() {
        listView = (ListView) rootView.findViewById(R.id.list);
        createAdapter();
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }

        coupons = manager.getCoupons();
        if (coupons == null) {
            manager.loadData();
            return;
        }
        hasData();
    }

    private void hasData() {
        adapter.setItems(coupons);
        adapter.notifyDataSetChanged();
    }

    private void initManager() {
        AbsLoadingView refreshLoadingView4List = new AbsLoadingView(listView) {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                showData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() != null) {
                    setData(data);
                }
            }

            @Override
            public void showFailView(String failMsg) {
                if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
                    super.showFailView(failMsg);
                    return;
                }
                showCouponFailView(getResources().getString(R.string.no_my_coupon));
            }
        };
        manager = new MyCouponManager(refreshLoadingView4List);
    }

    private void setData(Object data) {
        if (data != null && data instanceof List) {
            coupons = (List<DiscountCoupon>) data;
            hasData();
        }
    }

    private void createAdapter() {
        adapter = new MyCouponAdapter(getActivity());
        adapter.setListener(new MyCouponAdapter.IClickCouponDetail() {
            @Override
            public void clickCouponDetail(long couponId, String price) {
                if (couponId == -1) {
                    return;
                }
                turnToDiscouponDetail(couponId);
            }
        });
        listView.setAdapter(adapter);
    }

    private void turnToDiscouponDetail(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(CouponDetailFragment.DISCOUPON_ID, id);
        LaunchUtil.launchAppActivity(getActivity(), BaseSwitchActivity.class, CouponDetailFragment.class, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clear();
        manager = null;
    }
}