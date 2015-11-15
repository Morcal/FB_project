package com.feibo.snacks.view.module.home.category;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.SubClassify;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.module.category.CategoryManager;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.base.BaseDoubleGoodsAdapter;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.widget.CurtainView;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint({"InflateParams", "HandlerLeak"})
public class CategoryFragment extends BaseTitleFragment {

    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_TITLE = "categoryTilte";

    private int categoryId = 0;// 默认值是0
    private int goodsType = 0;// 商品类别
    private String categoryTitle;
    private boolean isRotate = false;
    private List<Goods> list;

    private RedPointManager redPointManager;
    private CategoryManager manager;
    private CategoryAdapter selectAdapter;
    private BaseDoubleGoodsAdapter adapter;

    private View root;
    private View scrollTopView;
    private CurtainView curtainView;
    private View focusview;
    private GridView goodsSelectMenu;
    private ListView listView;
    private PullToRefreshListView pullToRefreshListView;

    private RedPointManager.RedPointObserver redPointObserver;

    private TitleViewHolder titleHolder;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_category, null);
        setTitle(categoryTitle);
        initWidget();
        return root;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        super.initTitleBar(titleBar);
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.arrowImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        redPointManager = RedPointManager.getInstance();
        Bundle ags = getArguments();
        categoryId = ags.getInt(CATEGORY_ID, 0);
        categoryTitle = ags.getString(CATEGORY_TITLE);
        setFragmentName(getResources().getString(R.string.categoryFragment));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initData();

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
        // 购物车红点
        redPointManager.setRedNumberView(titleHolder.carNumText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
        titleHolder.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRotate = false;
        manager.removeData();
    }

    private void initWidget() {
        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.list);
        listView = pullToRefreshListView.getRefreshableView();
        scrollTopView = root.findViewById(R.id.scroll_top);
        goodsSelectMenu = (GridView) root.findViewById(R.id.fragment_category_gridview);
        goodsSelectMenu.setSelector(new ColorDrawable(getResources().getColor(R.color.white)));
        focusview = root.findViewById(R.id.focusview);
        curtainView = (CurtainView) root.findViewById(R.id.fragment_category_curtain);
        curtainView.setListener(new CurtainView.ICurTainShowListener() {
            @Override
            public void isShow(boolean isShow) {
                curtainView.setFocusable(true);
                curtainView.setFocusableInTouchMode(true);
                curtainView.requestFocus();
                listView.setFastScrollEnabled(false);
            }
        });
        curtainView.onRopeClick();
        selectAdapter = new CategoryAdapter(getActivity());
        goodsSelectMenu.setAdapter(selectAdapter);
        createAdapter();
    }

    private void createAdapter() {
        adapter = new BaseDoubleGoodsAdapter(getActivity());
        adapter.setListener(new BaseDoubleGoodsAdapter.OnGoodsClickListener() {
            @Override
            public void onGoodsClick(int position) {
                if (position >= adapter.getItems().size()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, adapter.getItem(position).id);
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.SIX_CATE_OF_GOODS_LIST);
                bundle.putString(BaseFragment.ORIGIN, getResources().getString(R.string.categoryFragment));
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
        });
        listView.setAdapter(adapter);
    }

    private void setTitle(String title) {
        titleHolder.titleText.setText(title);
    }

    private void initData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        } else {
            manager.setClassifyId(categoryId);
            manager.setSubClassifyId(goodsType);
        }
        if (manager.getCategories() == null || manager.getCategories().size() == 0) {
            manager.loadCategorySelect( new ILoadingListener() {
                @Override
                public void onSuccess() {
                    SubClassify category = new SubClassify();
                    category.title = "全部";
                    manager.getCategories().add(0, category);
                    selectAdapter.setItems(manager.getCategories());
                    selectAdapter.setSelectPos(0);
                }

                @Override
                public void onFail(String failMsg) {

                }
            });
        }
        showData();
    }

    private void showData() {
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
        };

        manager = new CategoryManager(refreshLoadingView4List);
        manager.setClassifyId(categoryId);
        manager.setSubClassifyId(goodsType);
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

    private void hasData() {
        adapter.setItems(list);
        adapter.notifyDataSetChanged();
    }

    private void setData(Object data) {
        if (data != null && data instanceof List) {
            list = (List<Goods>) data;
            hasData();
        }
    }

    private void initListener() {
        focusview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCurtain();
            }
        });
        goodsSelectMenu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<SubClassify> category = manager.getCategories();
                if (position == 0) {
                    goodsType = 0;
                    setTitle(categoryTitle);
                } else {
                    SubClassify item = category.get(position);
                    goodsType = item.id;
                    setTitle(item.title);
                }
                handleCurtain();
                selectAdapter.setSelectPos(position);
                initData();
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

    protected void setAnimation(int fromDegree, int toDegree) {
        AnimationSet animationSet = new AnimationSet(true);
        // 后面的四个参数定义的是旋转的圆心位置
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);
        titleHolder.arrowImage.startAnimation(animationSet);
    }

    // 显示或隐藏类别选择
    public void handleCurtain(){
        List<SubClassify> categores = manager.getCategories();
        if (categores == null || categores.size() == 0) {
            return;
        }
        curtainView.setVisibility(View.VISIBLE);
        if (isRotate) {
            isRotate = false;
            setAnimation(-180, 0);
        } else {
            isRotate = true;
            setAnimation(0, 180);
        }
        curtainView.onRopeClick();
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

        @Bind(R.id.head_title_img)
        ImageView arrowImage;

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

        // 选择类别
        @OnClick(R.id.head_title)
        public void clickHeadTitle() {
            handleCurtain();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }

}
