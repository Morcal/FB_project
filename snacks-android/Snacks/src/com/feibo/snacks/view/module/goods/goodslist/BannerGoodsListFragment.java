package com.feibo.snacks.view.module.goods.goodslist;

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
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.manager.module.goods.BannerGoodsListManager;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jayden on 2015/7/21.
 */
public class BannerGoodsListFragment extends BaseTitleFragment{

    public static final String BANNER_ID = "bannerId";
    public static final String BANNER_TITLE = "bannerTilte";
    private static final String TITLE = "商品列表";

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private View scrollTopView;
    private View root;
    private TitleViewHolder titleHolder;

    private RedPointManager redPointManager;
    private BannerGoodsListManager manager;
    private BaseDoubleGoodsAdapter adapter;
    private List<Goods> todayGoods;
    private int supplierId = 0;// 默认值是0

    private RedPointManager.RedPointObserver redPointObserver;

    private String bannerTitle;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pulltorefresh_list, null);
        initWidget(root);
        return root;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(bannerTitle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle ags = getArguments();
        supplierId = ags.getInt(BANNER_ID,0);
        bannerTitle = TITLE ;
        redPointManager = RedPointManager.getInstance();
        setFragmentName(getResources().getString(R.string.bannerGoodsListFragment));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showData();
        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                redPointManager.setRedNumberView(titleHolder.carNumText);
            }
        };
        redPointManager.addObserver(redPointObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        RemindControl.cancelToast();
        // 购物车红点
        redPointManager.setRedNumberView(titleHolder.carNumText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        titleHolder.onDestroy();
        redPointManager.deleteObserver(redPointObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clearData();
        manager = null;
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
        manager = new BannerGoodsListManager(refreshLoadingView4List);
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

    // 进入购物车
    public void handleCart(){
        LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
    }

    // 退出程序
    public void handleQuit(){
        getActivity().finish();
    }

    class TitleViewHolder {

        @Bind(R.id.head_title_name)
        TextView titleText;

        @Bind(R.id.home_car_number)
        TextView carNumText;

        public TitleViewHolder(View view) {
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        // 退出
        @OnClick(R.id.head_left)
        public void clickHeadLeft() {
            handleQuit();
        }

        // 进入购物车
        @OnClick(R.id.head_right)
        public void clickHeadRight() {
            handleCart();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }
}
