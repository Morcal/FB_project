package com.feibo.snacks.view.module.goods.goodslist;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.module.goods.SupplierGoodsListManager;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;

import java.util.List;

/**
 * Created by Jayden on 2015/8/25.
 */
public class SupplierGoodsListFragment extends BaseTitleFragment {

    public static final String SUPPLIER_ID = "supplierId";
    public static final String SUPPLIER_TITLE = "supplierTilte";
    public static final String TITLE = "商品列表";

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private View scrollTopView;
    private View root;

    private SupplierGoodsListManager manager;
    private BaseDoubleGoodsAdapter adapter;
    private List<Goods> todayGoods;
    private int supplierId = 0;// 默认值是0
    private String bannerTitle;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pulltorefresh_list, null);
        initParams();
        setTitle();
        initWidget(root);
        showData();
        return root;
    }

    private void initParams() {
        Bundle ags = getArguments();
        supplierId = ags.getInt(SUPPLIER_ID,0);
        bannerTitle = ags.getString(SUPPLIER_TITLE);
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }

        todayGoods = manager.getBannerGoodsList();
        if (todayGoods == null) {
            initData();
            return;
        }
        hasData();
    }

    private void hasData() {
        adapter.setItems(todayGoods);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        manager.loadData();
    }

    private void initManager() {
        RefreshLoadingView4List refreshLoadingView4List = new RefreshLoadingView4List(pullToRefreshListView) {
            @Override
            public void reFillData(Object data) {
                if(getActivity() == null) {
                    return;
                }
                createAdapter();
                setData(data);
            }
            @Override
            public void fillMoreData(Object data) {
                if(getActivity() != null) {
                    setData(data);
                }
            }

            @Override
            public void loadMoreData(LoadMoreScrollListener listener) {
                manager.loadMore(listener);
            }

            @Override
            public View getLoadingParentView() {
                return root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                showData();
            }

            @Override
            public void fillData(Object data) {
                if(getActivity() != null) {
                    setData(data);
                }
            }
        };
        manager = new SupplierGoodsListManager(refreshLoadingView4List);
        manager.setKeyId(supplierId);
        RefreshListViewOperation operation = new RefreshListViewOperation(pullToRefreshListView, manager) {
            @Override
            public void operationItemAtPosition(int position) {
                if (scrollTopView != null) {
                    if (position > 5) {
                        scrollTopView.setVisibility(View.VISIBLE);
                    } else {
                        scrollTopView.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private void setData(Object data) {
        if (data != null && data instanceof List) {
            todayGoods = (List<Goods>) data;
            hasData();
        }
    }

    private void initWidget(View root) {
        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.list);
        scrollTopView = root.findViewById(R.id.scroll_top);
        listView = pullToRefreshListView.getRefreshableView();

        createAdapter();

        scrollTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0, 0, 0));
                listView.setSelection(0);
            }
        });
    }

    private void createAdapter() {
        adapter = new BaseDoubleGoodsAdapter(getActivity());
        adapter.setListener(new BaseDoubleGoodsAdapter.OnGoodsClickListener() {
            @Override
            public void onGoodsClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, adapter.getItem(position).id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.TODYAY_SPECIAL_SELLING);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
        });
        listView.setAdapter(adapter);
    }

    private void setTitle() {
        TextView title = (TextView) getTitleBar().headView.findViewById(R.id.head_title_name);
        title.setText(bannerTitle);
        getTitleBar().rightPart.setVisibility(View.GONE);
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.supplierGoodsListFragment);
        super.onResume();
        RemindControl.cancelToast();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.supplierGoodsListFragment);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clearData();
        manager = null;
    }
}
