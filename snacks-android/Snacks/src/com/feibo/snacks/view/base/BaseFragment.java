package com.feibo.snacks.view.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import fbcore.log.LogUtil;

public abstract class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    public static final String ORIGIN = "ORIGIN";
    private String originName;

    // 用来做友盟统计Fragment名称
    public String fragmentName = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            originName = getArguments().getString(ORIGIN);
        }
        return onCreateView(inflater, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        if (fragmentName != null) {
            MobclickAgent.onPageStart(getFragmentName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
        if (fragmentName != null) {
            MobclickAgent.onPageEnd(getFragmentName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
    }

    public void setFragmentName(String fragmentName){
        this.fragmentName = fragmentName;
    }

    private String getFragmentName() {
        if (TextUtils.isEmpty(originName)) {
            return fragmentName;
        } else {
            return originName + "_" + fragmentName;
        }
    }

    public void onKeyDown(int keyCode, KeyEvent event) {}

    public abstract View onCreateView(LayoutInflater inflater, Bundle savedInstanceState);
}
