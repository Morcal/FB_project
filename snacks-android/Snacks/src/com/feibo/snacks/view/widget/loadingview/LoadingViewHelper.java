package com.feibo.snacks.view.widget.loadingview;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.NetResult;

@SuppressLint("InflateParams")
public class LoadingViewHelper {

    private View group;
    private View loading;
    private TextView failView;
    private TextView failView2;
    private ImageView pageLoaing;

    private LoadingViewHelper(View loadingGroup) {
        this.group = loadingGroup;
        loading = loadingGroup.findViewById(R.id.widget_loading);
        pageLoaing = (ImageView) loading.findViewById(R.id.widget_loading_pb);
        failView = (TextView) loadingGroup.findViewById(R.id.widget_fail_view);
        failView2 = (TextView) loadingGroup.findViewById(R.id.widget_fail_view2);
    }

    public static LoadingViewHelper generateOnParent(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_common_loading, null);
        parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new LoadingViewHelper(view);
    }

    public static LoadingViewHelper generateOnParentAtPosition(int loadingLayoutId, ViewGroup parent, int position) {
        if (parent == null) {
            return null;
        }
        if (position < 0) {
            position = 0;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(loadingLayoutId == 0 ? R.layout.widget_common_loading : loadingLayoutId, null);
        parent.addView(view, position, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new LoadingViewHelper(view);
    }

    public void start() {
        group.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        pageLoaing.setBackgroundResource(R.drawable.ic_page_loading);
        AnimationDrawable animation = (AnimationDrawable) pageLoaing.getBackground();
        animation.start();
        failView.setVisibility(View.GONE);
        failView2.setVisibility(View.GONE);
        group.setOnClickListener(null);
    }

    public void end() {
        group.setVisibility(View.GONE);
        AnimationDrawable animation = (AnimationDrawable) pageLoaing.getBackground();
        animation.stop();
    }

    public void showCollectEmptyFailView(OnClickListener clickListener) {
        showFailView(failView.getResources().getString(R.string.empty_collect_text1),
                failView.getResources().getString(R.string.empty_collect_text2), R.drawable.icon_collection, clickListener);
    }

    public void showNoNetFailView(OnClickListener clickListener) {
        showFailView(failView.getResources().getString(R.string.loading_default_fail_text), failView.getResources().getString(R.string.loading_default_fail_text2), R.drawable.icon_network, clickListener);
    }

    public void fail(String msg, OnClickListener clickListener) {
        if(NetResult.NOT_DATA_STRING.equals(msg)) {
            showFailView(failView.getResources().getString(R.string.search_no_data), failView.getResources().getString(R.string.search_no_data1), R.drawable.icon_wuhuo, clickListener);
            return;
        }
        showFailView(failView.getResources().getString(R.string.loading_default_fail_text), failView.getResources().getString(R.string.loading_default_fail_text2), R.drawable.icon_network, clickListener);
    }


    public void show() {
        group.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        failView.setVisibility(View.VISIBLE);
        failView2.setVisibility(View.VISIBLE);
    }

    public void hidden() {
        group.setVisibility(View.GONE);
        pageLoaing.setVisibility(View.GONE);
        failView.setVisibility(View.GONE);
        failView2.setVisibility(View.GONE);
    }

    private void showFailView(String msg1, String msg2, int backgroundSourceId, OnClickListener listener) {
        group.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        Drawable drawable = group.getResources().getDrawable(backgroundSourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        failView.setCompoundDrawables(null, drawable, null, null);
        failView.setVisibility(View.VISIBLE);
        failView2.setVisibility(View.VISIBLE);
        if (msg1 == null || msg1.length() == 0) {
            msg1 = failView.getResources().getString(R.string.loading_default_fail_text);
        } else {
            failView.setText(msg1);
        }
        if (msg2 == null || msg2.length() == 0) {
            failView2.setText(failView2.getResources().getString(R.string.loading_default_fail_text2));
        } else {
            failView2.setText(msg2);
        }
        if (listener != null) {
            group.setOnClickListener(listener);
        }
    }

    public ViewGroup getLoadingRootView() {
        return (ViewGroup) group;
    }
}
