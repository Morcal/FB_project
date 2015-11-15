package com.feibo.snacks.view.module.coupon;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.module.coupon.InValidCouponManager;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class InValidCouponFragment extends BaseFragment {

    private String ids;
    private String addressId;

    private View rootView;
    private ListView listView;

    private InValidCouponAdapter adapter;
    private InValidCouponManager manager;

    public static InValidCouponFragment  getInstance(String addressId,String ids) {
        InValidCouponFragment fragment = new InValidCouponFragment();
        Bundle args = new Bundle();
        args.putString(UsingCouponActivity.ADDRESS_ID, addressId);
        args.putString(UsingCouponActivity.GOODS_ID,ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        ids = args.getString(UsingCouponActivity.GOODS_ID);
        addressId = args.getString(UsingCouponActivity.ADDRESS_ID);
    }

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_valid_coupon,null);
        listView = (ListView) rootView.findViewById(R.id.list);
        initAdapter();
        initManager();
        return (ViewGroup) rootView;
    }

    private void initAdapter() {
        adapter = new InValidCouponAdapter(getActivity());
        listView.setAdapter(adapter);
    }

    private void initManager() {
        AbsLoadingView loadingView = new AbsLoadingView() {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {

            }

            @Override
            public void fillData(Object data) {
                if(getActivity() != null) {
                    setData(data);
                }
            }

            @Override
            public void showFailView(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
                UIUtil.setViewGone(listView);
                if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
                    super.showFailView(failMsg);
                    return;
                }
                showCouponFailView(getResources().getString(R.string.no_my_coupon));
            }
        };
        manager = new InValidCouponManager(loadingView);
        manager.setAddressId(addressId);
        manager.setIds(ids);
        manager.loadData();
    }


    private void setData(Object data) {
        if (data != null && data instanceof List) {
            adapter.setItems((List<DiscountCoupon>) data);
            adapter.notifyDataSetChanged();
        }
    }

    public void reFreshData() {
        manager.clearData();
        UIUtil.setViewGone(((ViewGroup) rootView).getChildAt(0));
        UIUtil.setViewVisible(listView);
        initManager();
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
