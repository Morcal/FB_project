package com.feibo.snacks.view.module.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.feibo.snacks.R;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.module.person.orders.shoppingcart.CartFragment;
import com.feibo.snacks.view.widget.SlidingFinishView;
import com.feibo.social.manager.SocialComponent;

public class MyCouponActivity extends BaseActivity {

    private BaseFragment fragment;
    private SlidingFinishView layout;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        layout = (SlidingFinishView) LayoutInflater.from(this).inflate(
                R.layout.layout_sliding_finish, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_base_switch);

        MyCouponFragment curFragment = new MyCouponFragment();
        fragment = curFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_in_right, R.anim.activity_out_right);
        transaction.add(R.id.fragment, curFragment).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SocialComponent.onNewIntent(this, intent);
        changeScene();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SocialComponent.onActivityResult(this, requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null) {
            fragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeScene() {
        MyCouponFragment curFragment = new MyCouponFragment();
        fragment = curFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, curFragment).commit();
    }

}
