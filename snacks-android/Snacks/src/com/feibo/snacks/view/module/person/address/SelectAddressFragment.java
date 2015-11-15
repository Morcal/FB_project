package com.feibo.snacks.view.module.person.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.manager.module.person.AddressListPresenter;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.person.orders.ordersconfirm.OrdersConfirmFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

import java.util.List;


public class SelectAddressFragment extends BaseTitleFragment{

    public static final String SELECT_ADDRESS_ID = "select_address_id";
    private View root;
    private ListView listView;
    private AddressListPresenter addressListPresenter;
    private SelectAddressAdapter adapter;
    private AbsLoadingView absLoadingView;

    private View emptyView;
    private View entryAddBtn;
    private long primeSelectID = SelectAddressAdapter.DEFAULT_SELECTED_ID;
    private long currentSelectID = SelectAddressAdapter.DEFAULT_SELECTED_ID;
    private long tempSelectID = -2;

    private boolean isEdit = false;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        Bundle args = getArguments();
        primeSelectID = args.getLong(OrdersConfirmFragment.CURRENT_ADDRESS_ID, SelectAddressAdapter.DEFAULT_SELECTED_ID);

        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_address_list, null);
        initWidget();
        setLoadingView();
        setTitle();
        initListener();
        return root;
    }

    private void initWidget() {
        listView = (ListView)root.findViewById(R.id.list);

        emptyView = root.findViewById(R.id.fragment_address_empty);
        entryAddBtn = emptyView.findViewById(R.id.fragment_address_quick_add_btn);
        listView.setEmptyView(emptyView);
        emptyView.setVisibility(View.GONE);
    }

    private void initListener() {
        getTitleBar().rightPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchActivityForResult(AddressFragment.REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, AddAddressFragment.class, null);
            }
        });
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                getActivity().finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelectID = adapter.getItem(position).id;
                adapter.setSelectedAddressID(currentSelectID);
                adapter.notifyDataSetChanged();
                setResult();
                getActivity().finish();
            }
        });
        entryAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchActivityForResult(AddressFragment.REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, AddAddressFragment.class, null);
            }
        });
    }

    private void setResult() {
        if(primeSelectID != currentSelectID || isEdit){
            if(tempSelectID == currentSelectID){
                return;
            }
            tempSelectID = currentSelectID ;
            Intent intent = new Intent();
            intent.putExtra(SELECT_ADDRESS_ID, currentSelectID);
            getActivity().setResult(Activity.RESULT_OK, intent);
        }else{
            getActivity().setResult(Activity.RESULT_CANCELED, null);
        }
    }

    private void setLoadingView() {
        absLoadingView = new AbsLoadingView(listView) {
            @Override
            public ViewGroup getLoadingParentView() {
                return (ViewGroup) root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                if (addressListPresenter != null) {
                    addressListPresenter.loadData();
                }
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                updateView();
            }

            @Override
            public void showFailView(String failMsg) {
                if(failMsg.equals(NetResult.NOT_DATA_STRING)){
                    hideLoadingView();
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                super.showFailView(failMsg);
            }
        };
        absLoadingView.setLauncherPositon(2);
        addressListPresenter =  new AddressListPresenter(absLoadingView);
        ListViewOperation operation = new ListViewOperation(listView, addressListPresenter) {
            @Override
            public void operationItemAtPosition(int position) {

            }
        };
        operation.initListData();
    }

    private void updateView() {
        List<Address> list = addressListPresenter.getAddressList();
        if (adapter == null) {
            createAdapter();
            adapter.setItems(list);
            listView.setAdapter(adapter);
        } else {
            adapter.setItems(list);
            adapter.notifyDataSetChanged();
        }
        if (adapter.getItems() != null) {
            absLoadingView.hideLoadingView();
        }
        currentSelectID = adapter.getSelectedAddressID();
        setResult();
    }

    private void createAdapter() {
        adapter = new SelectAddressAdapter(getActivity());
        adapter.setSelectAddressOpterationListener(new SelectAddressAdapter.SelectAddressOpterationListener() {

            @Override
            public void editAddress(int i) {
                Bundle arg = new Bundle();
                arg.putInt(UpdateAddressFragment.ADDRESS_POSITION, i);
                LaunchUtil.launchActivityForResult(AddressFragment.REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, UpdateAddressFragment.class, arg);
            }
        });
        adapter.setSelectedAddressID(primeSelectID);
    }

    private void hidProgressDialog() {
        RemindControl.hideProgressDialog();
    }

    private void showProgressDialog() {
        RemindControl.showProgressDialog(getActivity(), R.string.dailog_address_updata, null);
    }

    private void setTitle() {
        TextView addAddress = (TextView) getTitleBar().rightPart;
        getTitleBar().rightPart.setVisibility(View.VISIBLE);
        addAddress.setText("新增");
        addAddress.setTextColor(getResources().getColor(R.color.c1));
        TextView title = (TextView) getTitleBar().title;
        title.setText("我的收货地址");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            // TODO
            case AddressFragment.RESULT_CODE_ADD_ADDRESS: {
                isEdit = true;
                updateView();
                break;
            }
            case AddressFragment.RESULT_CODE_UPDATE_ADDRESS: {
                isEdit = true;
                updateView();
                break;
            }
        }
    }
}
