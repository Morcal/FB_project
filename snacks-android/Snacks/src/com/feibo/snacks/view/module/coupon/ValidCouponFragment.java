package com.feibo.snacks.view.module.coupon;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.module.coupon.ValidCouponManager;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class ValidCouponFragment extends BaseFragment {

    private String ids;
    private String addressId;

    private View rootView;
    private ListView listView;

    private ValidCouponAdapter adapter;
    private ValidCouponManager manager;

    public static ValidCouponFragment getInstance(String addressId, String ids) {
        ValidCouponFragment fragment = new ValidCouponFragment();
        Bundle args = new Bundle();
        args.putString(UsingCouponActivity.ADDRESS_ID, addressId);
        args.putString(UsingCouponActivity.GOODS_ID, ids);
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
        rootView = inflater.inflate(R.layout.fragment_valid_coupon, null);
        listView = (ListView) rootView.findViewById(R.id.list);
        initAdapter();
        initManager();
        return (ViewGroup) rootView;
    }

    private void initAdapter() {
        adapter = new ValidCouponAdapter(getActivity());
        adapter.setListener(new AbsCouponAdapter.IClickCouponDetail() {
            @Override
            public void clickCouponDetail(final long id, final String name) {
                if (getActivity() == null) {
                    return;
                }
                manager.setAddressId(addressId);
                manager.setCouponId(String.valueOf(id));
                manager.useCoupon(new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() == null) {
                            return;
                        }
                        //处理可以使用当前优惠券
                        usingCouponSuccess(id,name);
                    }

                    @Override
                    public void onFail(String failMsg) {
                        if (getActivity() == null) {
                            return;
                        }
                        RemindControl.showSimpleToast(getActivity(), getResources().getString(R.string.cannot_use_coupon));
                        usingCouponSuccess(0,null);
                        reFreshData();
                        ((UsingCouponActivity) getActivity()).reFreshData();
                    }
                });
            }
        });
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
                if (getActivity() != null && data != null) {
                    setData(data);
                }
            }

            @Override
            public void showFailView(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
                if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
                    super.showFailView(failMsg);
                    return;
                }
                showCouponFailView(getResources().getString(R.string.no_valid_coupon));
            }
        };
        manager = new ValidCouponManager(loadingView);
        manager.setAddressId(addressId);
        manager.setIds(ids);
        manager.loadData();
    }

    /**
     * 使用优惠券返回订单结算页，要对订单结算页的金额和可用优惠券view进行修改
     *
     * @param name
     */
    private void usingCouponSuccess(long id,String name) {
        ((UsingCouponActivity) getActivity()).usingCouponSuccess(manager.getOrderDetail(),name,id);
    }

    private void reFreshData() {
        manager.clearData();
        initManager();
    }

    private void setData(Object data) {
        if (data instanceof List) {
            adapter.setItems((List<DiscountCoupon>) data);
            adapter.notifyDataSetChanged();
            ((UsingCouponActivity)getActivity()).setValidNumber(((List<DiscountCoupon>) data).size());
        }
    }
}
