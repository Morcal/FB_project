package com.feibo.snacks.view.module.person.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.manager.global.orders.AbsOrdersManager;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.person.orders.comment.OrdersCommentFragment;
import com.feibo.snacks.view.module.person.orders.item.OrdersAdapter;
import com.feibo.snacks.view.module.person.orders.ordersdetail.OrdersDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.loadingview.LoadingMoreView4List;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;

import static com.feibo.snacks.view.module.person.orders.item.OrdersAdapter.OrdersOptListener;

/**
 * Created by hcy on 2015/7/15.
 */
public abstract class OrdersBaseFragment extends BaseFragment {

    private View root;
    protected ListView listView;
    private View emptyView;
    private View entryHomeBtn;
    protected OrdersAdapter adapter;

    private AbsOrdersManager manager;
    protected  boolean isVisible;

    private int selectedPosition = -1;

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_list, null);
        initWidget();
        initLoading();
        initListener();
        return (ViewGroup) root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    private void onInvisible() {
    }

    private void onVisible() {
        if (root == null || !isVisible) {
            return;
        }
        manager.loadData();
    }

    private void initListener() {
        entryHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().setResult(MainActivity.ENTRY_HOME_SCENCE, intent);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    protected void launchOrdersDetail(int position) {
        selectedPosition = position;
        ItemOrder itemOrder = manager.getData(manager.getDataType()).get(position);
        Bundle args = new Bundle();
        args.putString(OrdersDetailFragment.ORDERS_DETAIL_ID, itemOrder.id);
        args.putInt(OrdersDetailFragment.ORDERS_DETAIL_STATE, itemOrder.type);
        LaunchUtil.launchActivityForResult(LaunchUtil.REQUEST_ORDERS_DETAIL, getActivity(), BaseSwitchActivity.class, OrdersDetailFragment.class, args);
    }

    private void initWidget() {
        listView = (ListView) root.findViewById(R.id.list);
        ImageView headView = new ImageView(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtil.dp2Px(getActivity(), 10));
        headView.setLayoutParams(params);
        headView.setBackgroundColor(getResources().getColor(R.color.c5));
        listView.addHeaderView(headView);

        emptyView = root.findViewById(R.id.fragment_list_empty);
        entryHomeBtn = emptyView.findViewById(R.id.fragment_orders_empty_entry_home);
        listView.setEmptyView(emptyView);
        emptyView.setVisibility(View.GONE);
    }

    public abstract AbsOrdersManager generateOrdersManager(ILoadingView iLoadingView);

    public abstract OrdersOptListener getOrderOptListener();

    private void initLoading() {
        LoadingMoreView4List loadingMoreView4List = new LoadingMoreView4List(listView) {
            @Override
            public void showFailView(String failMsg) {
                if (failMsg.equals(NetResult.NOT_DATA_STRING)) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    super.showFailView(failMsg);
                }
            }

            @Override
            public void fillMoreData(Object data) {
                if (adapter == null) {
                    return;
                }
                if (manager == null) {
                    return;
                }
                adapter.setItems(manager.getData(manager.getDataType()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void hideLoadMoreView(LoadMoreScrollListener listener, String failMsg) {
                super.hideLoadMoreView4Orders(listener, failMsg);
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
                manager.loadData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (manager == null) {
                    return;
                }
                adapter = new OrdersAdapter(getActivity());
                adapter.setItems(manager.getData(manager.getDataType()));
                adapter.setOrdersOptListener(getOrderOptListener());
                listView.setAdapter(adapter);
            }
        };
        manager = generateOrdersManager(loadingMoreView4List);
        ListViewOperation operation = new ListViewOperation(listView, manager) {
            @Override
            public void operationItemAtPosition(int position) {

            }
        };
        operation.initListData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LaunchUtil.REQUEST_ORDERS_DETAIL && OrdersDetailFragment.needRefreshList) {
            removeSelectItem();
        } else if (requestCode == LaunchUtil.REQUEST_ORDERS_COMMENT && data!= null) {
            boolean needRefresh = data.getBooleanExtra(OrdersCommentFragment.ORDERS_COMMENT_RESULT, false);
            if (needRefresh) {
                if (manager == null) {
                    return;
                }
                manager.loadData();
            }
        }
    }

    private void removeSelectItem() {
        if (selectedPosition == -1) {
            return;
        }
        if (manager == null || manager.getData(manager.getDataType()) == null ||  manager.getData(manager.getDataType()).size() < selectedPosition) {
            return;
        }
        if (adapter == null) {
            return;
        }
        manager.getData(manager.getDataType()).remove(selectedPosition);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listView = null;
        root = null;
        manager.release();
        manager = null;
    }
}
