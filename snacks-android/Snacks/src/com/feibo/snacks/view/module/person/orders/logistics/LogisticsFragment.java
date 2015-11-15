package com.feibo.snacks.view.module.person.orders.logistics;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.group.ExpressDetail;
import com.feibo.snacks.manager.global.orders.paid.LogisticsManager;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.base.BaseTitleFragment;

/**
 * Created by hcy on 2015/7/28.
 */
public class LogisticsFragment extends BaseTitleFragment {

    public static final String ORDERS_ID = "orders_id";
    private View root;
    private ListView listView;
    private LogisticsHead head;
    private LogisticsFooter footer;

    private LogisticsManager manager;
    private String ordersId;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list, null);
        initWidget();
        initLoading();
        initTitle();
        return root;
    }

    private void initTitle() {
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });
        TextView title = (TextView) getTitleBar().title;
        title.setText("查看物流");
    }

    private void initLoading() {
        ordersId = getArguments().getString(ORDERS_ID);
        AbsLoadingView absLoadingView = new AbsLoadingView(listView) {
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
                fillView();
            }
        };
        manager = new LogisticsManager(absLoadingView, ordersId);
        manager.loadData();
    }

    private void fillView() {
        ExpressDetail detail = manager.getExpressDetail();
        head.refreshView(detail);
        footer.refreshView(detail);
        LogisticsAdapter adapter = new LogisticsAdapter(getActivity());
        adapter.setItems(manager.getCartList());
        listView.setAdapter(adapter);
    }

    private void initWidget() {
        listView = (ListView) root.findViewById(R.id.list);
        head = new LogisticsHead(getActivity());
        footer = new LogisticsFooter(getActivity());
        listView.addHeaderView(head.getRoot());
        listView.addFooterView(footer.getRoot());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listView = null;
        head = null;
        footer = null;
        root = null;
        manager.release();
        manager = null;
    }
}
