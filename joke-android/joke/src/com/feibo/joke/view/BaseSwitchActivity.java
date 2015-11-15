package com.feibo.joke.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;

import com.feibo.joke.R;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.SlidingFinishView;

public class BaseSwitchActivity extends BaseActivity {

    private BaseFragment fragment;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        SlidingFinishView layout = (SlidingFinishView) LayoutInflater.from(this).inflate(
                R.layout.layout_sliding_finish, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_base_switch);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Class<? extends BaseFragment> cls = (Class<? extends BaseFragment>) intent
                .getSerializableExtra(LaunchUtil.FRAGMENT);
        try {
            fragment = cls.newInstance();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_in_right, R.anim.activity_out_right);
            transaction.add(R.id.fragment, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChange(int code) {
        if(fragment != null) {
            fragment.onDataChange(code);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
