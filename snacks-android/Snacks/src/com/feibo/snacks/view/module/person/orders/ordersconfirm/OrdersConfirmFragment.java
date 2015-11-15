package com.feibo.snacks.view.module.person.orders.ordersconfirm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.orders.unpaid.OrdersConfirmManager;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.util.Util;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.coupon.UsingCouponActivity;
import com.feibo.snacks.view.module.person.address.AddAddressFragment;
import com.feibo.snacks.view.module.person.address.AddressFragment;
import com.feibo.snacks.view.module.person.address.SelectAddressAdapter;
import com.feibo.snacks.view.module.person.address.SelectAddressFragment;
import com.feibo.snacks.view.module.person.orders.ordersdetail.OrdersDetailFragment;
import com.feibo.snacks.view.module.person.orders.pay.PayHelper;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.SimpleItemView;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.utils.Strings;

/**
 * Created by hcy on 2015/7/22.
 */
public class OrdersConfirmFragment extends BaseTitleFragment {
    private static final String TAG = OrdersConfirmFragment.class.getSimpleName();

    private static final int REQUEST_ADDRESS_SELECT = 0x100;
    private static final int REQUEST_ADDRESS_ADD = 0x200;
    private static final int USING_COUPON_REQUEST_CODE = 0X111;

    public static final String ORDERRS_CONFIRM_LIST = "orders_confirm_list";
    public static final String CURRENT_ADDRESS_ID = "current_address_id";
    public static final String COUPON_ORDER_DETAIL = "coupon_order_detail";
    public static final String COUPON_ORDER_ID = "coupon_order_id";
    public static final String COUPON_NAME = "couponName";
    public static final String VALID_COUPON_NUMBER = "validNumber";

    private View rootView;
    private EditText remarkView;

    @Bind(R.id.fragment_confirm_orders_pay_count)
    TextView ordersCostTv;

    @Bind(R.id.fragment_orders_confirm_list)
    ListView orderListView;

    @Bind(R.id.item_orders_pay_orders_btn)
    View payBtn;

    ListHeadHolder listHeadHolder;
    ListFooterHolder listFooterHolder;

    private ArrayList<CartItem4Type> orderList;
    private OrdersConfirmManager ordersConfirmManager;
    private List<Integer> ordersArr;
    private OrdersConfirmAdapter adapter;

    private long addressId;
    private String couponName;
    private OrdersDetail ordersDetail;

    private PayHelper helper;
    private long couponId;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_confirm_orders, null);
        ButterKnife.bind(this, rootView);
        initWidget();
        initLoading();
        return rootView;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        ((TextView) titleBar.title).setText("确认订单");
        titleBar.leftPart.setOnClickListener(view -> {
            handleQuitNormal();
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ordersArr = getArguments().getIntegerArrayList(ORDERRS_CONFIRM_LIST);
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.ordersConfirmFragment);
        super.onResume();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.ordersConfirmFragment);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        listHeadHolder.onDestroy();
        listFooterHolder.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ordersConfirmManager.release();
        ordersConfirmManager = null;
        if (helper != null) {
            helper.release();
            helper = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        // 选择地址
        if (requestCode == REQUEST_ADDRESS_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                addressId = data.getLongExtra(SelectAddressFragment.SELECT_ADDRESS_ID, SelectAddressAdapter.DEFAULT_SELECTED_ID);
                handleSelectAddressSuccess(addressId);
            }
        }

        // 添加地址
        if (requestCode == REQUEST_ADDRESS_ADD) {
            if (resultCode == AddressFragment.RESULT_CODE_ADD_ADDRESS) {
                addressId = ((Address) data.getSerializableExtra(AddressFragment.RESULT_KEY_FOR_ADDRESS)).id;
                handleSelectAddressSuccess(addressId);
            }
        }

        // 使用优惠券
        if (requestCode == USING_COUPON_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                updateOrderInfo((OrdersDetail)data.getSerializableExtra(COUPON_ORDER_DETAIL),data.getStringExtra(COUPON_NAME),data.getLongExtra(COUPON_ORDER_ID,0),data.getIntExtra(VALID_COUPON_NUMBER,0));
            }
        }
    }

    private void updateOrderInfo(OrdersDetail orderDetail,String name,long couponId,int number) {
        ordersConfirmManager.setDiscouponId(couponId);
        this.couponId = couponId;
        couponName = name;
        if (orderDetail == null) {
            this.ordersDetail.discouponNum = number;
            listFooterHolder.couponItem.setValueText(number + "张可用");
        } else {
            this.ordersDetail = orderDetail;
            initData();
        }
    }

    private void initWidget() {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_orders_confirm_head, null);
        listHeadHolder = new ListHeadHolder(headView);
        orderListView.addHeaderView(headView);

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_orders_confirm_footer, null);
        listFooterHolder = new ListFooterHolder(footerView);
        orderListView.addFooterView(footerView);
        orderListView.setFooterDividersEnabled(false);
    }

    // 初始化数据，填充界面
    private void initData() {
        // 地址
        if (ordersDetail.address == null) {
            listHeadHolder.noAddressBoard.setVisibility(View.VISIBLE);
        } else {
            listHeadHolder.noAddressBoard.setVisibility(View.GONE);
            listHeadHolder.nameText.setText(ordersDetail.address.name);
            listHeadHolder.phoneText.setText(ordersDetail.address.phone);
            listHeadHolder.addressText.setText(ordersDetail.address.getFullAddress());
            addressId = ordersDetail.address.id;
        }

        // 商品价格详情
        listFooterHolder.costGoodsItem.setValueText("￥" + ordersDetail.sumGoods);
        listFooterHolder.costPostItem.setValueText("￥" + ordersDetail.sendPay);
        listFooterHolder.couponItem.setValueText(Strings.isMeaningful(couponName) ? couponName : ordersDetail.discouponNum + "张可用");
        listFooterHolder.couponItem.setValueColor(ordersDetail.discouponNum == 0 ? getResources().getColor(R.color.c7) : getResources().getColor(R.color.c1));
        listFooterHolder.costAllItem.setValueText("￥" + ordersDetail.finalSum);
        listFooterHolder.couponAmount.setValueText("-￥" + ordersDetail.discountAmount);
        ordersCostTv.setText("￥" + ordersDetail.finalSum);

    }

    private void createAdapter() {
        adapter = new OrdersConfirmAdapter(getActivity());
        adapter.setOnEditCommentListener(new OrdersConfirmAdapter.OnEditCommentListener() {
            @Override
            public void editComment(final int i) {
                remarkView = RemindControl.showEditTextRemind(getActivity(), getString(R.string.str_order_confirm_edit_dialog_title), orderList.get(i).note, getString(R.string.orders_confirm_note), new RemindControl.OnRemindListener() {
                    @Override
                    public void onConfirm() {
                        if (remarkView == null) {
                            return;
                        }
                        orderList.get(i).note = remarkView.getText().toString().trim();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        remarkView.setFocusableInTouchMode(true);
                        remarkView.requestFocus();
                        InputMethodManager m = (InputMethodManager) remarkView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }, 300);
            }
        });

        adapter.setItems(orderList);
        orderListView.setAdapter(adapter);
    }

    private void initLoading() {
        RefreshLoadingView refreshLoadingView = new RefreshLoadingView() {
            @Override
            public void hideRefreshView() {
                RemindControl.hideProgressDialog();
            }

            @Override
            public void reFillData(Object data) {

            }

            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                ordersConfirmManager.loadData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                orderList = ordersConfirmManager.getCart();
                createAdapter();
                ordersDetail = ordersConfirmManager.getData(ordersConfirmManager.getDataType());
                initData();
            }

            @Override
            public void hideLoadingView() {
                super.hideLoadingView();
                RemindControl.hideProgressDialog();
            }
        };
        refreshLoadingView.setLauncherPositon(2);
        ordersConfirmManager = new OrdersConfirmManager(ordersArr, refreshLoadingView);
        ordersConfirmManager.loadData();
    }

    private boolean hasFailOrders() {
        for (CartItem4Type cartItem4Type : orderList) {
            if (cartItem4Type.item.state != CartItem.NORMAL) {
                return true;
            }
        }
        return false;
    }

    // 选择地址
    public void handleSelectAddress() {
        if (addressId == SelectAddressAdapter.DEFAULT_SELECTED_ID) {
            // 添加地址
            LaunchUtil.launchActivityForResult(REQUEST_ADDRESS_ADD, getActivity(), BaseSwitchActivity.class, AddAddressFragment.class, null);
        } else {
            // 选择地址
            Bundle bundle = new Bundle();
            bundle.putLong(CURRENT_ADDRESS_ID, addressId);
            LaunchUtil.launchActivityForResult(REQUEST_ADDRESS_SELECT, getActivity(), BaseSwitchActivity.class, SelectAddressFragment.class, bundle);
        }
    }

    // 选择地址成功
    public void handleSelectAddressSuccess(long addressId) {
        if (addressId == -1) {
            return;
        }
        ordersConfirmManager.setAddressId(addressId);
        ordersConfirmManager.loadData();
    }

    // 选择优惠券
    public void handleSelectCoupon() {
        Bundle bundle = new Bundle();
        bundle.putString(UsingCouponActivity.GOODS_ID, Util.createIdString(ordersArr));
        bundle.putString(UsingCouponActivity.ADDRESS_ID, String.valueOf(addressId));
        bundle.putInt(UsingCouponActivity.COUPON_TYPE, ordersDetail.discouponNum == 0 ? UsingCouponActivity.COUPON_INVALID : UsingCouponActivity.COUPON_VALID);
        LaunchUtil.launchActivityForResult(USING_COUPON_REQUEST_CODE, getActivity(),
                UsingCouponActivity.class,
                null,
                bundle);
    }

    // 支付订单
    @OnClick(R.id.item_orders_pay_orders_btn)
    public void handlePayOrder() {
        // 未选择地址
        if (listHeadHolder.noAddressBoard.getVisibility() == View.VISIBLE) {
            RemindControl.showSimpleToast(getActivity(), R.string.no_select_address);
            handleSelectAddress();
            return;
        }

        // 有失效订单
        if (hasFailOrders()) {
            RemindControl.showSimpleToast(getActivity(), R.string.orders_confirm_orders_fail);
            handleQuitChange();
            return;
        }

        // 启动支付
        payBtn.setClickable(false);
        final PayParams payParams = ordersConfirmManager.createParams(ordersDetail, orderList);
        payParams.setCouponId(couponId);
        //TODO 这边需要服务器还回，到时候就可以删除
        if (ordersDetail.posterIds == null) {
            ordersDetail.posterIds = "";
        }
        payParams.setPosterIds(ordersDetail.posterIds);
        helper = new PayHelper(getActivity(), payParams, ordersDetail) {
            @Override
            public void onCancleOrders(String ordersId) {
                handlePayCancel(ordersId);
            }

            @Override
            public void onTurnResult() {
                handleQuitChange();
            }

            @Override
            public void onRefreshView() {
                if (ordersConfirmManager == null) {
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                couponName = null;
                ordersConfirmManager.clear();
                ordersConfirmManager = null;
                payBtn.setClickable(true);
                initLoading();
            }
        };
        helper.payOrders();
    }

    // 取消订单
    public void handlePayCancel(String ordersId) {
        Bundle bundle = new Bundle();
        bundle.putString(OrdersDetailFragment.ORDERS_DETAIL_ID, ordersId);
        bundle.putInt(OrdersDetailFragment.ORDERS_DETAIL_STATE, 0);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, OrdersDetailFragment.class, bundle);
        handleQuitChange();
    }

    // 订单状态未改变，退出
    public void handleQuitNormal() {
        getActivity().setResult(Activity.RESULT_CANCELED, null);
        getActivity().finish();
    }

    // 订单状态改变，退出
    public void handleQuitChange() {
        getActivity().setResult(Activity.RESULT_OK, null);
        getActivity().finish();
    }

    class ListHeadHolder {

        @Bind(R.id.orders_confirm_name)
        TextView nameText;

        @Bind(R.id.orders_confirm_phone)
        TextView phoneText;

        @Bind(R.id.orders_confirm_no_select_address)
        View noAddressBoard;

        @Bind(R.id.orders_confirm_address)
        TextView addressText;

        public ListHeadHolder(View view) {
            ButterKnife.bind(ListHeadHolder.this, view);
        }

        @OnClick(R.id.orders_confirm_address_viewgroup)
        public void onClickNoAddressBoard() {
            handleSelectAddress();
        }

        public void onDestroy() {
            ButterKnife.unbind(ListHeadHolder.this);
        }
    }

    class ListFooterHolder {

        @Bind(R.id.item_cost_goods)
        SimpleItemView costGoodsItem;

        @Bind(R.id.item_cost_post)
        SimpleItemView costPostItem;

        @Bind(R.id.item_coupon)
        SimpleItemView couponItem;

        @Bind(R.id.item_cost_all)
        SimpleItemView costAllItem;

        @Bind(R.id.item_coupon_amount)
        SimpleItemView couponAmount;

        public ListFooterHolder(View view) {
            ButterKnife.bind(ListFooterHolder.this, view);
        }

        @OnClick(R.id.item_coupon)
        public void onClickCoupon() {
            handleSelectCoupon();
        }

        public void onDestroy() {
            ButterKnife.unbind(ListFooterHolder.this);
        }
    }
}