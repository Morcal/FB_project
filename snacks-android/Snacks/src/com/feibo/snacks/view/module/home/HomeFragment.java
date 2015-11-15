package com.feibo.snacks.view.module.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.module.home.HomeManager;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.group.HomePageHead;
import com.feibo.snacks.view.base.BannerJumpHelper;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.home.NewProductAdapter.HomeGoodsClickListener;
import com.feibo.snacks.view.module.home.search.SearchFragment;
import com.feibo.snacks.view.util.GoodsHelper;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import fbcore.log.LogUtil;

@SuppressLint("InflateParams")
public class HomeFragment extends BaseTitleFragment {
    private static final String TAG = "HomeFragment";
    private HomeAboveViewGroup homeAboveViewGroup;

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private View rootView;
    private View scrollTopView;
    private TextView carNumView;

    private HomePageHead homeAbove;

    private List<Goods> goodsList;
    private NewProductAdapter goodsListAdapter;

    private HomeManager manager;
    private RedPointManager redPointManager;
    private RedPointManager.RedPointObserver redPointObserver;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_home_title;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pulltorefresh_list, null);
        pullToRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        scrollTopView = rootView.findViewById(R.id.scroll_top);
        carNumView = (TextView) getTitleBar().rightPart.findViewById(R.id.home_car_number);

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_home_above, null);
        homeAboveViewGroup = new HomeAboveViewGroup(headView, getActivity());

        listView = pullToRefreshListView.getRefreshableView();
        listView.addHeaderView(headView, null, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setFragmentName(getResources().getString(R.string.homefragment));
        redPointManager = RedPointManager.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAdapter();
        initListener();
        showData();
        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                showCarNumber();
            }
        };
        redPointManager.addObserver(redPointObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        showCarNumber();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GoodsHelper.cancelCountDownTime();
        homeAboveViewGroup.releaseView();
        homeAboveViewGroup = null;
    }

    private void initManager() {
        RefreshLoadingView4List refreshLoadingView = new RefreshLoadingView4List(pullToRefreshListView) {
            @Override
            public void reFillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                createAdapter();
                setData(data);
            }

            @Override
            public void fillData(Object data) {
                setData(data);
            }

            @Override
            public void fillMoreData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof List) {
                    goodsList = (List<Goods>) data;
                    showBelowData();
                }
            }

            @Override
            public void loadMoreData(LoadMoreScrollListener listener) {
                manager.loadMore(listener);
            }

            @Override
            public ViewGroup getLoadingParentView() {
                return (ViewGroup) rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                manager.loadData();
            }

            private void setData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof HomePageHead) {
                    homeAbove = (HomePageHead) data;
                    showAboveData();
                }
            }
        };
        manager = new HomeManager(refreshLoadingView);
        RefreshListViewOperation operation = new RefreshListViewOperation(pullToRefreshListView, manager) {
            @Override
            public void operationItemAtPosition(int position) {
                if (scrollTopView != null) {
                    if (position > 2) {
                        scrollTopView.setVisibility(View.VISIBLE);
                    } else {
                        scrollTopView.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private void initData() {
        manager.loadData();
    }

    private void initListener() {
        scrollTopView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0, 0, 0));
                listView.setSelection(0);
            }
        });

        getTitleBar().title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMethod();
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, SearchFragment.class, null);
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_home_search));
            }
        });
        getTitleBar().rightPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
            }
        });
    }

    private void showData() {
        LogUtil.i(TAG, "showData");
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }

        homeAbove = manager.getHomeAbove();
        goodsList = manager.getNewProducts();

        if (homeAbove == null || goodsList == null) {
            initData();
            return;
        }

        showAboveData();
        showBelowData();
    }

    private void showAboveData() {
        homeAboveViewGroup.updateView(homeAbove);
        goodsListAdapter.setHomeAbove(homeAbove);
        goodsListAdapter.notifyDataSetChanged();
    }

    private void showBelowData() {
        goodsListAdapter.setNewProducts(goodsList);
        goodsListAdapter.notifyDataSetChanged();
    }

    private void showCarNumber() {
        redPointManager.setRedNumberView(carNumView);
    }

    private void createAdapter() {
        goodsListAdapter = null;
        goodsListAdapter = new NewProductAdapter(getActivity());
        goodsListAdapter.setOnItemClickListener(new HomeGoodsClickListener() {
            //好吃到爆
            @Override
            public void onRecommendClick(int position) {
                if (manager == null || manager.getNewProducts() == null || manager.getNewProducts().size() < position) {
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, manager.getNewProducts().get(position).id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.TASTY);
                bundle.putString(BaseFragment.ORIGIN, getString(R.string.deliciousGoods));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_home_recommend));
            }

            @Override
            public void onSpecialClick(int position) {
                if (manager == null || manager.getHomeAbove() == null || manager.getHomeAbove().specials == null
                        || manager.getHomeAbove().specials.size() < position) {
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_home_specials));
                BannerJumpHelper.turnBannerSituation(getActivity(), homeAbove.specials.get(position),
                        Constant.HOME_SPECIAL, getString(R.string.homeAd));
            }

            // 上面
            @Override
            public void onPromotionClick(int position) {
                if (manager == null || manager.getHomeAbove() == null || manager.getHomeAbove().brands == null
                        || manager.getHomeAbove().brands.size() < position) {
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(BrandGroupFragment.BRAND_GROUP_ID, manager.getHomeAbove().brands.get(position).id);
                bundle.putString(BrandGroupFragment.BRAND_GROUP_TITLE,
                        manager.getHomeAbove().brands.get(position).title);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, BrandGroupFragment.class, bundle);
                MobclickAgent.onEvent(getActivity(), getActivity().getResources().getString(R.string.click_home_brand));
            }
        });
        listView.setAdapter(goodsListAdapter);
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getTitleBar().title.getWindowToken(), 0);
    }
}