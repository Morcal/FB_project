package com.feibo.snacks.view.module.person.orders.shoppingcart;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.trade.CartService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.feibo.snacks.R;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.util.LaunchUtil;

/**
 * Created by hcy on 2015/8/4.
 */
public class CartEmptyPart {

    private View emptyView;
    private Context context;
    private View entryHomeBtn;
    private View taobaoBtn;

    public CartEmptyPart(View emptyView, Context context) {
        this.emptyView = emptyView;
        this.context = context;
        initWidget();
        initListener();
    }

    private void initListener() {
        entryHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchMainActivity(context, MainActivity.HOME_SCENCE);
                ((Activity) context).finish();

            }
        });
        taobaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCar();
            }
        });
    }

    private void initWidget() {
        entryHomeBtn = emptyView.findViewById(R.id.fragment_orders_empty_entry_home);
        TextView emptyRemind1 = (TextView) emptyView.findViewById(R.id.fragment_empty_remind_content1);
        TextView emptyRemind2 = (TextView) emptyView.findViewById(R.id.fragment_empty_remind_content2);
        ImageView emptyIcon = (ImageView) emptyView.findViewById(R.id.orders_empty_icon);
        emptyIcon.setBackgroundResource(R.drawable.icon_car_big);
        emptyRemind2.setVisibility(View.GONE);
        emptyRemind1.setText(R.string.cart_empty_list);

        View storeName = emptyView.findViewById(R.id.empty_orders_head);
        storeName.setVisibility(View.VISIBLE);
        taobaoBtn = storeName.findViewById(R.id.layout_cart_taobao_item);
    }

    private void showCar() {
        AlibabaSDK.getService(CartService.class).showCart(((Activity) context), new TradeProcessCallback() {
            @Override
            public void onPaySuccess(TradeResult tradeResult) {
            }
            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                } else {
                }
            }
        });
    }
}
