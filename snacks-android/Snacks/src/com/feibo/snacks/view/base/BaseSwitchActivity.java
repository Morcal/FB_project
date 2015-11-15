package com.feibo.snacks.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.feibo.snacks.R;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.widget.SlidingFinishView;
import com.feibo.social.manager.SocialComponent;

public class BaseSwitchActivity extends BaseActivity {

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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SocialComponent.onActivityResult(this, requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SocialComponent.onNewIntent(this, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null) {
            fragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
