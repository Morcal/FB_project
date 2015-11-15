package com.feibo.snacks.view.module.home.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.manager.module.home.SearchDetailManager;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.widget.loadingview.LoadingMoreView4List;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import fbcore.log.LogUtil;

@SuppressLint({"HandlerLeak", "InflateParams", "NewApi"})
public class SearchDetailFragment extends BaseTitleFragment {
    private static final String TAG = SearchDetailFragment.class.getSimpleName();

    private ListView listView;
    private View root;
    private View resultView;
    private View scrollTopView;
    private View noResultView;

    private TextView searchNoResult;
    private TextView searchNoResult2;

    private SearchDetailManager manager;
    private BaseDoubleGoodsAdapter adapter;
    private String title;
    private List<Goods> searchData;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search_detail, null);
        setTitle();
        initWidget(root);
        initListener();
        showData();
        return root;
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }
        searchData = manager.getGoodsList();
        if (searchData == null) {
            initData();
            return;
        }
        setData();
    }

    private void initData() {
        ListViewOperation operation = new ListViewOperation(listView, manager) {
            @Override
            public void operationItemAtPosition(int position) {

            }
        };
        operation.initListData();
    }

    private void initManager() {
        LoadingMoreView4List loadingMoreView4List = new LoadingMoreView4List(listView) {
            @Override
            public void fillMoreData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof List) {
                    searchData = (List<Goods>) data;
                    setData();
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
                finishActivity();
            }

            @Override
            public void fillData(Object data) {
                LogUtil.i(TAG, "fillData");
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof List) {
                    searchData = (List<Goods>) data;
                    setData();
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void showFailView(String failMsg) {
                if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
                    super.showFailView(failMsg);
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                ((ViewGroup) root).removeAllViews();
                View view = LayoutInflater.from(root.getContext()).inflate(R.layout.widget_common_loading, null);
                View loading = view.findViewById(R.id.widget_loading);
                TextView failView = (TextView) view.findViewById(R.id.widget_fail_view);
                TextView failView2 = (TextView) view.findViewById(R.id.widget_fail_view2);
                loading.setVisibility(View.GONE);
                failView.setVisibility(View.VISIBLE);
                failView2.setVisibility(View.VISIBLE);
                failView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_search_no_data), null, null);
                failView.setText(getResources().getString(R.string.search_no_data2));
                failView2.setText(getResources().getString(R.string.search_no_data3));
                ((ViewGroup) root).addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishActivity();
                    }
                });
            }
        };
        manager = new SearchDetailManager(loadingMoreView4List);
        manager.setKeyWord(title);
        ListViewOperation operation = new ListViewOperation(listView, manager) {
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

    private void setData() {
        adapter.setItems(searchData);
        adapter.notifyDataSetChanged();
    }

    private void setTitle() {
        Bundle args = getArguments();
        title = args.getString(SearchFragment.PARAM_KEYWORD);
        TextView titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        titleView.setText(title);
    }

    private void initListener() {
        searchNoResult.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void finishActivity() {
        getActivity().finish();
    }

    protected void showNoDataView() {
        resultView.setVisibility(View.GONE);
        noResultView.setVisibility(View.VISIBLE);
        searchNoResult.setText(getActivity().getResources().getString(R.string.search_no_data2));
        searchNoResult2.setText(getActivity().getResources().getString(R.string.search_no_data3));
    }

    private void initWidget(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        scrollTopView = view.findViewById(R.id.scroll_top);
        resultView = view.findViewById(R.id.search_result);
        noResultView = view.findViewById(R.id.search_no_result);
        searchNoResult = (TextView) noResultView.findViewById(R.id.widget_no_data_view);
        searchNoResult2 = (TextView) noResultView.findViewById(R.id.widget_no_data_view2);

        createAdapter();
    }

    private void createAdapter() {
        adapter = new BaseDoubleGoodsAdapter(getActivity());
        adapter.setListener(new BaseDoubleGoodsAdapter.OnGoodsClickListener() {
            @Override
            public void onGoodsClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, adapter.getItem(position).id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.SEARCH_RESULT);
                bundle.putString(BaseFragment.ORIGIN,  getResources().getString(R.string.searchDetailFragment));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.search_result_enter_detail));
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clear();
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.searchDetailFragment);
        super.onResume();
        RemindControl.cancelToast();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.searchDetailFragment);
        super.onPause();
    }
}
