package com.feibo.snacks.view.module.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.manager.module.home.BrandGroupManager;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.widget.loadingview.LoadingMoreView4List;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.GoodsHelper;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.NCountDownTimerUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;

@SuppressLint("InflateParams")
public class BrandGroupFragment extends BaseTitleFragment {

    public static final String BRAND_GROUP_TITLE = "brand_detail_title";
    public static final String BRAND_GROUP_ID = "brand_group_id";

    private View headerView;
    private View root;
    private View scrollTopView;
    private ListView listView;
    private TextView brandTime;
    private TextView titleView;
    private TextView provideView;
    private ImageView brandImg;

    private int keyId;
    private boolean isVisible = true;
    private BrandDetail brandDetail;
    private BrandGroupManager manager;
    private BaseDoubleGoodsAdapter adapter;
    private TextView carNumber;

    private RedPointManager redPointManager;
    private RedPointManager.RedPointObserver redPointObserver;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header_nodistance;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_subject_detail_list, null);
        setTitle();
        initWidget(root);
        initListener();
        showData();

        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                showCarNumber();
            }
        };
        redPointManager.addObserver(redPointObserver);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redPointManager = RedPointManager.getInstance();
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.brandGroupFragment);
        super.onResume();
        isVisible = true;
        RemindControl.cancelToast();
        showCarNumber();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.brandGroupFragment);
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clear();
        NCountDownTimerUtil util = (NCountDownTimerUtil) brandTime.getTag();
        if (util != null) {
            util.cancel();
            util = null;
        }
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }

        brandDetail = manager.getBrandDetail();
        if (brandDetail == null) {
            initData();
            return;
        }
        hasData();
    }

    private void hasData() {
        if (brandDetail.brand != null) {
            showHeader();
        }
        if (brandDetail.goodses != null) {
            adapter.setItems(brandDetail.goodses);
            adapter.notifyDataSetChanged();
        }
    }

    private void setData(Object data) {
        if (data != null && data instanceof BrandDetail) {
            brandDetail = (BrandDetail) data;
            hasData();
        }
    }

    private void showHeader() {
        UIUtil.setBrandGroupHeaderImage(brandDetail.brand.img.imgUrl, brandImg);
        titleView.setText(brandDetail.brand.title);
        provideView.setText(brandDetail.brand.provider);
        showTime();
    }

    private void showTime() {
        GoodsHelper.showRestTime(brandTime, brandDetail.brand.time, new NewProductAdapter.ISellingEndListener() {
            @Override
            public void onEnd() {
                handleBrandEnd();
            }
        });
    }

    private void handleBrandEnd() {
        if (isVisible) {
            RemindControl.showSimpleToast(getActivity(), R.string.end_brand);
        }
        getActivity().finish();
    }

    private void initData() {
        manager.loadData();
    }

    private void initManager() {
        LoadingMoreView4List loadingMoreView4List = new LoadingMoreView4List(listView) {
            @Override
            public void fillMoreData(Object data) {
                if (getActivity() != null) {
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
                if (getActivity() != null) {
                    setData(data);
                }
            }

            @Override
            public void showFailView(String failMsg) {
                if (NetResult.NOT_NETWORK_STRING.equals(failMsg) || NetResult.NOT_DATA_STRING.equals(failMsg)) {
                    super.showFailView(failMsg);
                    return;
                }
                ((ViewGroup)root).removeAllViews();
                View view = LayoutInflater.from(root.getContext()).inflate(R.layout.widget_common_loading, null);
                View loading = view.findViewById(R.id.widget_loading);
                TextView failView = (TextView) view.findViewById(R.id.widget_fail_view);
                TextView failView2 = (TextView) view.findViewById(R.id.widget_fail_view2);
                loading.setVisibility(View.GONE);
                failView.setVisibility(View.VISIBLE);
                failView2.setVisibility(View.GONE);
                failView.setText(failMsg);
                ((ViewGroup)root).addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        };
        manager = new BrandGroupManager(loadingMoreView4List, keyId);
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

    private void setTitle() {
        Bundle bundle = getArguments();
        carNumber = (TextView) getTitleBar().rightPart.findViewById(R.id.home_car_number);
        titleView = (TextView) getTitleBar().headView.findViewById(R.id.head_title_name);
        keyId = bundle.getInt(BRAND_GROUP_ID, 0);
        titleView.setText(bundle.getString(BRAND_GROUP_TITLE));
        getTitleBar().rightPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
            }
        });
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        scrollTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0, 0, 0));
                listView.setSelection(0);
            }
        });
    }

    private void initWidget(View view) {
        listView = (ListView) view.findViewById(R.id.subject_detail_list);
        scrollTopView = view.findViewById(R.id.scroll_top);

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_brand_group_header, null);
        brandImg = (ImageView) headerView.findViewById(R.id.subject_img);
        brandTime = (TextView) headerView.findViewById(R.id.brand_group_tag_time);
        provideView = (TextView) headerView.findViewById(R.id.brand_group_title);

        listView.addHeaderView(headerView, null, false);

        adapter = new BaseDoubleGoodsAdapter(getActivity());
        adapter.setListener(new BaseDoubleGoodsAdapter.OnGoodsClickListener() {
            @Override
            public void onGoodsClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, adapter.getItem(position).id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.BRAND_GROUP_DETAIL);
                bundle.putString(BaseFragment.ORIGIN, getResources().getString(R.string.brandGroupFragment));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
        });
        listView.setAdapter(adapter);
    }

    private void showCarNumber() {
        RedPointManager.getInstance().setRedNumberView(carNumber);
    }


}
