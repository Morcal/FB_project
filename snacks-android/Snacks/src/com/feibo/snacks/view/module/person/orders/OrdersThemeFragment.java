package com.feibo.snacks.view.module.person.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.view.base.BaseFragment;

/**
 * Created by hcy on 2015/7/26.
 */
public class OrdersThemeFragment extends BaseFragment {

    public static final String ORDERS_CLASS = "orders_class";
    public static final String ORDERS_NAME = "orders_name";
    private String ordersName;
    private String ordersClass;
    private OrdersBaseFragment fragment;
    private View root;

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_orders, null);
        ordersClass = getArguments().getString(ORDERS_CLASS);
        ordersName = getArguments().getString(ORDERS_NAME);
        initTitle();
        initContent();
        return (ViewGroup) root;
    }

    private void initContent() {
        try {
            Class<?> className = Class.forName(ordersClass);
            fragment = (OrdersBaseFragment)className.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_orders_content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTitle() {
        View head = root.findViewById(R.id.fragment_orders_head);
        TextView title = (TextView) head.findViewById(R.id.head_title);
        title.setText(ordersName);
        head.findViewById(R.id.head_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
