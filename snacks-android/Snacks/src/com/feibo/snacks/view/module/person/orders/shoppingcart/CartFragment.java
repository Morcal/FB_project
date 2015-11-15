package com.feibo.snacks.view.module.person.orders.shoppingcart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.trade.CartService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.global.orders.opteration.CartOperationManager;
import com.feibo.snacks.manager.global.orders.unpaid.NShoppingCartManager;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.coupon.SpecialOfferFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.module.person.orders.ordersconfirm.OrdersConfirmFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;

import java.util.ArrayList;
import java.util.Date;

import fbcore.log.LogUtil;

/**
 * Created by hcy on 2015/7/7.
 */
public class CartFragment extends BaseTitleFragment {

    public static final String REFRESH_DATA = "refresh_data";

    public static final int TYPE_LIST_GOODS = 0;
    public static final int TYPE_SPECIAL_SELLING = 1;
    public static final int TYPE_DICOUNT_DETAIL = 2;

    private static final int REFRESH_LIST = 1;

    private View rootView;
    private ListView listView;
    private ImageView selectAllBtn;
    private TextView accountNumber;
    private Button accountBtn;
    private View taobaoBtn;
    private View headView;
    private TextView allGoodsCountTv;
    private View emptyView;
    private View operationMenuView;

    private ArrayList<CartItem4Type> cartItemlist;
    private CartAdapter adapter;
    private NShoppingCartManager shoppingCartManager;

    private View footerView;
    private Button clearAllNoAvaibleBtn;
    private ViewGroup noAvaibleViewParent;
    private ArrayList<CartItem> noAvaibleList;
    private CartNoAvaibleAdapter noAvaibleAdapter;

    private boolean isAllSelect = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (shoppingCartManager == null) {
                return;
            }
            if (getActivity() == null) {
                return;
            }
            if (msg.what == REFRESH_LIST) {
                shoppingCartManager.loadData();
            }
        }
    };

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_shopping_cart, null);
        initWidget();
        initListener();
        initLoadingView();
        return rootView;
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.cartFragment);
        super.onResume();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.cartFragment);
        super.onPause();
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        ((TextView) titleBar.title).setText("购物车");
        titleBar.leftPart.setOnClickListener((View v) -> {
            handleQuit();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RedPointManager.getInstance().loadRedPoint();
        shoppingCartManager.release();
        shoppingCartManager = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LaunchUtil.REQUEST_ORDERS_CONFIRM_PAY && resultCode == Activity.RESULT_OK) {
            handler.sendEmptyMessage(REFRESH_LIST);
        } else if (requestCode == LaunchUtil.REQUEST_LOGIN && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            handler.sendEmptyMessage(REFRESH_LIST);
        }
    }

    private void initWidget() {
        listView = (ListView) rootView.findViewById(R.id.cart_list);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_cart_head, null);
        listView.addHeaderView(headView);
        selectAllBtn = (ImageView) rootView.findViewById(R.id.cart_select_all);
        accountNumber = (TextView) rootView.findViewById(R.id.cart_account_number);
        accountBtn = (Button) rootView.findViewById(R.id.item_orders_account);
        taobaoBtn = headView.findViewById(R.id.layout_cart_taobao_item);
        allGoodsCountTv = (TextView) headView.findViewById(R.id.layout_cart_head_xiaomiao_count);
        emptyView = rootView.findViewById(R.id.fragment_cart_empty);
        new CartEmptyPart(emptyView, getActivity());
        emptyView.setVisibility(View.GONE);

        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_cart_footer, null);
        noAvaibleViewParent = (ViewGroup) footerView.findViewById(R.id.cart_footer_parent);
        clearAllNoAvaibleBtn = (Button) footerView.findViewById(R.id.layout_cart_footer_delete_all);
        listView.addFooterView(footerView);
        operationMenuView = rootView.findViewById(R.id.cart_operation_menu);
        operationMenuView.setVisibility(View.GONE);
    }

    private void initListener() {
        selectAllBtn.setOnClickListener((view) -> {
            handleSelectAll();
        });
        accountBtn.setOnClickListener((view) -> {
            handleAccount();
        });
        taobaoBtn.setOnClickListener((view) -> {
            handleShowTaobaoCar();
        });
        clearAllNoAvaibleBtn.setOnClickListener((view) -> {
            handleDeleteAllNoAvaibleItems();
        });
    }

    private void handleDeleteAllNoAvaibleItems() {
        RemindControl.showDeleteAllNoAvaibleCartItem(getActivity(), new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                ArrayList<Long> list = new ArrayList<Long>();
                for (CartItem item : noAvaibleList) {
                    list.add((long) item.id);
                }
                CartOperationManager.deleteCart(list, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        responseDeleteSuccess();
                        needShowEmptyView();
                    }

                    @Override
                    public void onFail(String failMsg) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(failMsg)) {
                            return;
                        }
                        RemindControl.showSimpleToast(getActivity(), failMsg);
                    }
                });
            }

            private void responseDeleteSuccess() {
                if (getActivity() == null) {
                    return;
                }
                noAvaibleList = null;
                footerView.setVisibility(View.GONE);
                listView.removeFooterView(footerView);
                RemindControl.showSimpleToast(getActivity(), R.string.cart_delete_remind_success);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void initLoadingView() {
        AbsLoadingView absLoadingView = new AbsLoadingView(listView) {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                shoppingCartManager.loadData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                cartItemlist = shoppingCartManager.getCart();
                noAvaibleList = shoppingCartManager.getCartNoAvalible();
                if (needShowEmptyView()) {
                    return;
                }
                operationMenuView.setVisibility(View.VISIBLE);
                createAdapter();
                if (noAvaibleList == null || noAvaibleList.size() == 0) {
                    footerView.setVisibility(View.GONE);
                } else {
                    footerView.setVisibility(View.VISIBLE);
                    createNoAvaibleAdapter();
                    noAvaibleAdapter.setItems(noAvaibleList);
                    noAvaibleViewParent.requestLayout();
                }
            }

            private void createNoAvaibleAdapter() {
                noAvaibleAdapter = new CartNoAvaibleAdapter(getActivity(), noAvaibleViewParent) {
                    @Override
                    public void onItemDelete(final int position) {
                        handleDeleteNoAvaibleCart(position);
                    }

                    private void handleDeleteNoAvaibleCart(final int position) {
                        if (getActivity() == null) {
                            return;
                        }
                        RemindControl.showDeleteCartItem(getActivity(), new RemindControl.OnRemindListener() {
                            @Override
                            public void onConfirm() {
                               int id = noAvaibleList.get(position).id;
                                ArrayList<Long> list = new ArrayList<Long>();
                                list.add((long) id);
                                CartOperationManager.deleteCart(list, new ILoadingListener() {
                                    @Override
                                    public void onSuccess() {
                                        responseDeleteSuccess();
                                    }

                                    @Override
                                    public void onFail(String failMsg) {
                                        if (getActivity() == null) {
                                            return;
                                        }
                                        if (TextUtils.isEmpty(failMsg)) {
                                            return;
                                        }
                                        RemindControl.showSimpleToast(getActivity(), failMsg);
                                    }
                                });
                            }

                            private void responseDeleteSuccess() {
                                if (getActivity() == null) {
                                    return;
                                }
                                noAvaibleList.remove(position);
                                if (noAvaibleList == null || noAvaibleList.size() == 0) {
                                    footerView.setVisibility(View.GONE);
                                    listView.removeFooterView(footerView);
                                } else {
                                    noAvaibleAdapter.setItems(noAvaibleList);
                                }
                                needShowEmptyView();
                                RemindControl.showSimpleToast(getActivity(), R.string.cart_delete_remind_success);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                };
            }

            @Override
            public void showFailView(String failMsg) {
                super.showFailView(failMsg);
                if (NetResult.NOT_DATA_STRING.equals(failMsg)) {
                    cartItemlist = null;
                    adapter = null;
                    listView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void hideLoadingView() {
                super.hideLoadingView();
                RemindControl.hideProgressDialog();
            }
        };
        shoppingCartManager = new NShoppingCartManager(absLoadingView);
        ListViewOperation operation = new ListViewOperation(listView, shoppingCartManager) {
            @Override
            public void operationItemAtPosition(int position) {

            }
        };
        operation.initListData();
    }

    private void createAdapter() {
        isAllSelect = false;
        selectAllBtn.setSelected(isAllSelect);
        updateAccountNumber();
        adapter = new CartAdapter(getActivity());
        adapter.setItems(cartItemlist);
        adapter.setCartItemOptListener(new CartAdapter.CartItemOptListener() {
            @Override
            public void onChangeSelectState(int i) {
                cartItemlist.get(i).item.isSelect = !cartItemlist.get(i).item.isSelect;
                adapter.notifyDataSetChanged();
                goodsIsAllSelect();
                updateAccountNumber();
            }

            @Override
            public void addCart(final int i) {
                CartFragment.this.addCart(i);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void reduceCart(final int i) {
                CartFragment.this.reduceCart(i);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void deleteCart(final int i) {
                if (getActivity() == null) {
                    return;
                }
                RemindControl.showDeleteCartItem(getActivity(), new RemindControl.OnRemindListener() {
                    @Override
                    public void onConfirm() {
                        long id = cartItemlist.get(i).item.id;
                        ArrayList<Long> list = new ArrayList<Long>();
                        list.add((long) id);
                        CartOperationManager.deleteCart(list, new ILoadingListener() {
                            @Override
                            public void onSuccess() {
                                if (getActivity() == null) {
                                    return;
                                }
                                CartItem4Type.removeItem(i, cartItemlist);
                                adapter.setItems(cartItemlist);
                                adapter.notifyDataSetChanged();
                                updateAccountNumber();
                                RemindControl.showSimpleToast(getActivity(), R.string.cart_delete_remind_success);
                                needShowEmptyView();
                            }

                            @Override
                            public void onFail(String failMsg) {
                                if (getActivity() == null) {
                                    return;
                                }
                                if (TextUtils.isEmpty(failMsg)) {
                                    return;
                                }
                                RemindControl.showSimpleToast(getActivity(), failMsg);
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                updateAccountNumber();
            }

            @Override
            public void submitUpdateInfo(int i) {

            }

            @Override
            public void longTouchAddCart(int i, CartAdapter.CartHolder holder) {
                long nowTime = new Date().getTime();
                if (adapter.touchLongAddPreT == -1) {
                    adapter.touchLongAddPreT = nowTime;
                    return;
                } else {
                    if (nowTime - adapter.touchLongAddPreT >= 200 && !isOutNumber(i)) {
                        adapter.touchLongAddPreT = nowTime;
                        CartFragment.this.addCart(i);
                        holder.optNumber.setText(cartItemlist.get(i).item.num + "");
                    }
                }
            }

            @Override
            public void longTouchReduceCart(int i, CartAdapter.CartHolder holder) {
                long nowTime = new Date().getTime();
                if (adapter.touchLongReducePreT == -1) {
                    adapter.touchLongReducePreT = nowTime;
                    return;
                } else {
                    if (nowTime - adapter.touchLongReducePreT >= 200 && !isMinCarNumber(i)) {
                        adapter.touchLongReducePreT = nowTime;
                        CartFragment.this.reduceCart(i);
                        holder.optNumber.setText(cartItemlist.get(i).item.num + "");
                    }
                }
            }

            @Override
            public void handlerDiscount(int type, String info,long supplierId) {
                handlerDiscountToResult(type, info,supplierId);
            }
        });
        listView.setAdapter(adapter);
    }

    private boolean needShowEmptyView() {
        if ((cartItemlist == null ||cartItemlist.size() == 0) && (noAvaibleList == null || noAvaibleList.size() == 0)) {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    //处理点击满包邮跳转事件
    private void handlerDiscountToResult(int type, String info,long supplierId) {
        switch (type) {
            case TYPE_LIST_GOODS:
            case TYPE_SPECIAL_SELLING: {//促销与优惠信息页面
                Bundle bundle = new Bundle();
                bundle.putInt(SpecialOfferFragment.TYPE, type);
                bundle.putLong(SpecialOfferFragment.ID, Integer.parseInt(info));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, SpecialOfferFragment.class, bundle);
                break;
            }
        }
    }

    private boolean isMinCarNumber(int i) {
        if (cartItemlist.get(i).item.num == 1) {
            RemindControl.showSimpleToast(getActivity(), R.string.cart_can_not_reduce);
            return true;
        } else {
            return false;
        }
    }

    private void reduceCart(int i) {
        if (getActivity() == null) {
            return;
        }
        if (isMinCarNumber(i)) {
            return;
        }
        cartItemlist.get(i).item.num -= 1;
        updateData(i);
        updateAccountNumber();
    }

    private void updateData(int i) {
        CartOperationManager.updateCartNumber(cartItemlist.get(i).item.id, cartItemlist.get(i).item.num, new ILoadingListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String failMsg) {

            }
        });
    }

    private boolean isOutNumber(int i) {
        if (cartItemlist.get(i).item.surplusNum <= cartItemlist.get(i).item.num) {
            RemindControl.showSimpleToast(getActivity(), R.string.cart_number_out);
            return true;
        } else {
            return false;
        }
    }

    private void addCart(int i) {
        if (getActivity() == null) {
            return;
        }
        if (isOutNumber(i)) {
            return;
        }
        cartItemlist.get(i).item.num += 1;
        updateData(i);
        updateAccountNumber();
    }

    private void goodsIsAllSelect() {
        boolean tempAllSelect = true;
        for (CartItem4Type cartItem4Type : cartItemlist) {
            if (!cartItem4Type.item.isSelect) {
                tempAllSelect = false;
            }
        }
        isAllSelect = tempAllSelect;
        selectAllBtn.setSelected(isAllSelect);
    }

    // 结算
    public void handleAccount() {
        if (!UserManager.getInstance().isLogin()) {
            RemindControl.showSimpleToast(getActivity(), R.string.orders_account_user_no_login);
            LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_LOGIN, getActivity(), BaseSwitchActivity.class, LoginFragment.class, null);
            return;
        }
        ArrayList<Integer> orders = getSelectOrders();
        if (orders == null || orders.size() < 1) {
            RemindControl.showSimpleToast(getActivity(), R.string.orders_account_count_empty);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(OrdersConfirmFragment.ORDERRS_CONFIRM_LIST, orders);
        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_CONFIRM_PAY, getActivity(), BaseSwitchActivity.class, OrdersConfirmFragment.class, bundle);
    }

    // 显示淘宝购物车
    public void handleShowTaobaoCar() {
        AlibabaSDK.getService(CartService.class).showCart(getActivity(), new TradeProcessCallback() {
            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                LogUtil.d("taoke", "添加购物车成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    LogUtil.d("taoke", "打开购物车失败");
                } else {
                    LogUtil.d("taoke", "取消购物车失败");
                }
            }
        });
    }

    // 选择所有商品
    public void handleSelectAll() {
        if (cartItemlist == null || cartItemlist.size() <= 0) {
            return;
        }
        boolean isAllGoodsNotUse = true;
        for (CartItem4Type cartItem4Type : cartItemlist) {
            if (cartItem4Type.item.state == CartItem.NORMAL) {
                isAllGoodsNotUse = false;
            }
        }
        if (isAllGoodsNotUse) {
            return;
        }
        isAllSelect = !isAllSelect;
        selectAllBtn.setSelected(isAllSelect);
        for (CartItem4Type cartItem4Type : cartItemlist) {
            cartItem4Type.item.isSelect = isAllSelect;
        }
        adapter.notifyDataSetChanged();
        updateAccountNumber();
    }

    // 退出
    public void handleQuit() {
        getActivity().finish();
    }

    private void updateAccountNumber() {
        double price = 0;
        int number = 0;
        int goodsNumber = 0;
        if (cartItemlist == null || cartItemlist.size() == 0) {
            return;
        }
        for (CartItem4Type carItem4Type : cartItemlist) {
            if (carItem4Type.item.state == CartItem.NORMAL) {
                goodsNumber += carItem4Type.item.num;
            }
            if (carItem4Type.item.isSelect && carItem4Type.item.state == CartItem.NORMAL) {
                price += carItem4Type.item.num * carItem4Type.item.price.current;
                number += carItem4Type.item.num;
            }
        }
        CharSequence cost = getActivity().getResources().getString(R.string.show_cur_price, price);
        accountNumber.setText(cost);
        accountBtn.setText("结算(" + number + ")");
        allGoodsCountTv.setText("共" + goodsNumber + "件商品");
    }

    public ArrayList<Integer> getSelectOrders() {
        if (cartItemlist == null) {
            return null;
        }
        ArrayList<Integer> selectList = new ArrayList<Integer>();
        for (CartItem4Type item : cartItemlist) {
            if (item.item.isSelect && item.item.state == CartItem.NORMAL) {
                Integer id = (Integer) item.item.id;
                selectList.add(id);
            }
        }
        return selectList;
    }
}
