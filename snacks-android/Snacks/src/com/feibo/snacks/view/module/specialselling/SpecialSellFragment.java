package com.feibo.snacks.view.module.specialselling;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.module.specialsell.SpecialSellManager;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

@SuppressLint({"HandlerLeak", "InflateParams", "NewApi"})
public class SpecialSellFragment extends BaseTitleFragment {

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private View scrollTopView;
    private TextView carNum;
    private View root;

    private SpecialSellManager specialSellManager;
    private RedPointManager redPointManager;
    private BaseDoubleGoodsAdapter adapter;
    private List<Goods> todayGoods;

    private RedPointManager.RedPointObserver redPointObserver;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pulltorefresh_list, null);
        setTitle();
        initWidget(root);
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setFragmentName(getResources().getString(R.string.specialsellFragment));
        redPointManager = RedPointManager.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                showCarNumber();
            }
        };
        redPointManager.addObserver(redPointObserver);
        showData();
    }

    @Override
    public void onResume() {
        super.onResume();
        RemindControl.cancelToast();
        showCarNumber();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (specialSellManager == null) {
            initManager();
        }

        todayGoods = specialSellManager.getTodaySpecial();
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
        specialSellManager.loadData();
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
                specialSellManager.loadMore(listener);
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
        specialSellManager = new SpecialSellManager(refreshLoadingView4List);
        RefreshListViewOperation operation = new RefreshListViewOperation(pullToRefreshListView, specialSellManager) {
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
                bundle.putString(H5GoodsDetailFragment.ENTER_LOCATION, getString(R.string.click_discount_taobao_order));
                bundle.putString(BaseFragment.ORIGIN, getResources().getString(R.string.specialsellFragment));
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_discount_goods));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
        });
        listView.setAdapter(adapter);
    }

    private void setTitle() {
        TextView title = (TextView) getTitleBar().headView.findViewById(R.id.head_title_name);
        carNum = (TextView) getTitleBar().rightPart.findViewById(R.id.home_car_number);
        title.setText(getResources().getString(R.string.today_special_title));
        getTitleBar().leftPart.setVisibility(View.GONE);
        getTitleBar().rightPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
            }
        });
    }

    private void showCarNumber() {
        RedPointManager.getInstance().setRedNumberView(carNum);
    }

}
