package com.feibo.snacks.view.widget.loadingview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingView;

/**
 *
 * @author hcy
 */
public abstract class AbsLoadingView implements ILoadingView {

    private int loadingLayoutId;
    private ListView listView;
    private ViewGroup frameView;
    private LoadingViewHelper loadingHelper;
    private int launcherPositon = 0;

    public AbsLoadingView() {
        this.frameView = (ViewGroup) getLoadingParentView();
    }

    public AbsLoadingView(ListView listView) {
        this.listView = listView;
        this.frameView = (ViewGroup) getLoadingParentView();
    }

    public abstract View getLoadingParentView();

    @Override
    public void showLoadingView() {
        if(listView != null) {
            listView.setVisibility(View.GONE);
        }
        launchLoadHelper();
    }

    @Override
    public void hideLoadingView() {
        if(listView != null) {
            listView.setVisibility(View.VISIBLE);
        }
        hideLoadHelper();
    }

    @Override
    public void showFailView(String failMsg) {
        if (loadingHelper == null) {
            return;
        }
        loadingHelper.fail(failMsg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingHelperFailButtonClick();
            }
        });
    }

    /**
     *
     * @param position 如果需要重置启动的位置
     */
    public void setLauncherPositon(int position) {
        this.launcherPositon = position;
    }

    private void launchLoadHelper() {
        if (loadingHelper == null) {
            loadingHelper = LoadingViewHelper.generateOnParentAtPosition(loadingLayoutId,frameView, launcherPositon);
        }
        loadingHelper.start();
    }

    private void hideLoadHelper() {
        if (loadingHelper != null) {
            loadingHelper.end();
            loadingHelper = null;
        }
    }

    @Override
    public void showToast(String msg) {
        // TODO Auto-generated method stub

    }

    public void setLoadingLayoutId(int loadingLayoutId) {
        this.loadingLayoutId = loadingLayoutId;
    }

    public void showCouponFailView(String hint) {
        frameView.removeViewAt(0);
        View view = LayoutInflater.from(frameView.getContext()).inflate(R.layout.layout_loading, null);
        View loading = view.findViewById(R.id.widget_loading);
        TextView failView = (TextView) view.findViewById(R.id.widget_fail_view);
        TextView failView2 = (TextView) view.findViewById(R.id.widget_fail_view2);
        loading.setVisibility(View.GONE);
        failView.setVisibility(View.VISIBLE);
        failView2.setVisibility(View.GONE);
        failView.setCompoundDrawablesWithIntrinsicBounds(null, frameView.getResources().getDrawable(R.drawable.icon_coupon_big), null, null);
        failView.setText(hint);
        frameView.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public abstract void onLoadingHelperFailButtonClick();
}