package com.feibo.snacks.view.module.person.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.module.person.orders.item.OrdersAllFragment;
import com.feibo.snacks.view.module.person.orders.item.OrdersWaitCommentFragment;
import com.feibo.snacks.view.module.person.orders.item.OrdersWaitPayFragment;
import com.feibo.snacks.view.module.person.orders.item.OrdersWaitReiptFragment;
import com.feibo.snacks.view.module.person.orders.item.OrdersWaitSendFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hcy on 2015/7/13.
 */
public class OrdersActivity extends BaseActivity {

    public static final String PARAM_SELECT_POS = "select_pos";

    @Bind(R.id.pager_orders)
    ViewPager viewPager;

    @Bind(R.id.radio_group_orders)
    RadioGroup radioGroup;

    @Bind(R.id.head_title)
    TextView titleText;

    private ArrayList<OrdersBaseFragment> fragmentList;
    private OrdersPageAdapter viewPagerAdapter;

    private int selectPosition = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_orders_shop);
        ButterKnife.bind(this);
        titleText.setText(R.string.orders_title);
        initListener();
        initData();

        selectPosition = getIntent().getIntExtra(PARAM_SELECT_POS, 0);
        viewPager.setCurrentItem(selectPosition);
        Log.i("OrdersActivity","-------------------->onCreate()");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getString(R.string.ordersFragment));
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getString(R.string.ordersFragment));
        MobclickAgent.onPause(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        RedPointManager.getInstance().loadRedPoint();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentList.get(selectPosition).onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        Log.i("OrdersActivity","initData----------->创建Fragment");
        fragmentList = new ArrayList<OrdersBaseFragment>();
        OrdersBaseFragment allFragment = new OrdersAllFragment();
        OrdersBaseFragment waitPayFragment = new OrdersWaitPayFragment();
        OrdersBaseFragment waitSendFragment = new OrdersWaitSendFragment();
        OrdersBaseFragment waitReiptFragment = new OrdersWaitReiptFragment();
        OrdersBaseFragment waitCommentFragment = new OrdersWaitCommentFragment();
        fragmentList.add(allFragment);
        fragmentList.add(waitPayFragment);
        fragmentList.add(waitSendFragment);
        fragmentList.add(waitReiptFragment);
        fragmentList.add(waitCommentFragment);
        viewPagerAdapter = new OrdersPageAdapter(getSupportFragmentManager());
        viewPagerAdapter.setItems(fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @OnClick(R.id.head_left)
    public void handleQuit(){
        finish();
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                boolean isChecked = false;
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    if (position == i) {
                        isChecked = true;
                        selectPosition = i;
                    } else {
                        isChecked = false;
                    }
                    ((RadioButton) radioGroup.getChildAt(i)).setChecked(isChecked);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_all: {
                        selectPosition = 0;
                        break;
                    }
                    case R.id.radio_btn_wait_pay: {
                        selectPosition = 1;
                        break;
                    }
                    case R.id.ratio_btn_wait_send: {
                        selectPosition = 2;
                        break;
                    }
                    case R.id.ratio_btn_wait_receive: {
                        selectPosition = 3;
                        break;
                    }
                    case R.id.ratio_btn_wait_comment: {
                        selectPosition = 4;
                        break;
                    }
                }
                viewPager.setCurrentItem(selectPosition, true);
            }
        });
    }

    private class OrdersPageAdapter extends FragmentPagerAdapter {

        private List<OrdersBaseFragment> list;

        public void setItems(List<OrdersBaseFragment> list) {
            this.list = list;
        }

        public OrdersPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
