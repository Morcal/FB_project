package com.feibo.snacks.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.feibo.snacks.R;
import com.feibo.snacks.view.module.coupon.MyCouponFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.util.LaunchUtil;

public abstract class BaseTitleFragment extends BaseFragment {

    private LinearLayout rootView;

    private TitleBar titleBar;

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        rootView = new LinearLayout(getActivity());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setBackgroundColor(getResources().getColor(R.color.white));

        // 标题栏
        int headLayoutId = onCreateTitleBar();
        if(headLayoutId > 0){
            View headView = inflater.inflate(headLayoutId, null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rootView.addView(headView, params);
            titleBar = new TitleBar(headView);
            initTitleBar(titleBar);
        }

        // 内容
        View contentView = onCreateContentView();
        if (contentView != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(params);
            attachToRoot(contentView);
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(rootView);
        if(rootView.getParent()!=null){
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        rootView = null;
        titleBar = null;
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    protected void attachToRoot(View view) {
        if (view == null) {
            return;
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        rootView.addView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LaunchUtil.COUPON_REQUEST_CODE && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, MyCouponFragment.class, null);
        }
    }

    protected void initTitleBar(TitleBar titleBar){};

    public TitleBar getTitleBar() {
        return titleBar;
    }

    // 标题栏布局样式id
    public abstract int onCreateTitleBar();

    // 建立内容view
    public abstract View onCreateContentView();

    public static class TitleBar {

        public View headView;
        public View title;
        public View leftPart;
        public View rightPart;

        public TitleBar(View headView){
            this.headView = headView;
            this.title = headView.findViewById(R.id.head_title);
            this.leftPart = headView.findViewById(R.id.head_left);
            this.rightPart = headView.findViewById(R.id.head_right);
        }
    }
}
