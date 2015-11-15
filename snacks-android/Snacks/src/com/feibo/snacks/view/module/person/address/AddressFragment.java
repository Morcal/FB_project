package com.feibo.snacks.view.module.person.address;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.manager.module.person.AddressListPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.dialog.RemindDialog;

import java.util.List;

/**
 * Created by hcy on 2015/7/2.
 */
public class AddressFragment extends BaseTitleFragment{

    public static final int REQUEST_CODE_FOR_ADDRESSFRAGMENT = 0x125;
    public static final int RESULT_CODE_UPDATE_ADDRESS = 0x126;
    public static final int RESULT_CODE_ADD_ADDRESS = 0x127;
    public static final String RESULT_KEY_FOR_ADDRESS = "result_key_for_address";

    private View root;
    private ListView listView;
    private AddressListPresenter addressListPresenter;
    private AddressAdapter adapter;
    private AbsLoadingView absLoadingView;

    private View emptyView;
    private View entryAddBtn;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
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
                LaunchUtil.launchActivityForResult(REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, AddAddressFragment.class, null);
            }
        });
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        entryAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchActivityForResult(REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, AddAddressFragment.class, null);
            }
        });
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
//                if (data == null) {
//                    return;
//                }
                UIUtil.setViewVisible(getTitleBar().rightPart);
                if (getActivity() == null) {
                    return;
                }
                updateView();
            }

            @Override
            public void showFailView(String failMsg) {
                if(failMsg.equals(NetResult.NOT_DATA_STRING)){
                    UIUtil.setViewVisible(getTitleBar().rightPart);
                    hideLoadingView();
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                if(failMsg.equals(NetResult.NOT_NETWORK_STRING)){
                    UIUtil.setViewGone(getTitleBar().rightPart);
                }
                super.showFailView(failMsg);
            }
        };
        absLoadingView.setLauncherPositon(2);
        addressListPresenter = new AddressListPresenter(absLoadingView);
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
    }

    private void createAdapter() {
        adapter = new AddressAdapter(getActivity());
        adapter.setAddressOpterationListener(new AddressAdapter.AddressOpterationListener() {
            @Override
            public void setDefaultAddress(final int i) {
                final Address tempAddress = addressListPresenter.getAddress(i);
                showProgressDialog();
                addressListPresenter.setAddressDefault(tempAddress.id, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        tempAddress.type = Address.TYPE_DEFAULT;
                        addressListPresenter.isDefaultAddress(tempAddress, i);
                        adapter.setDefaultAddress(tempAddress.id);
                        adapter.notifyDataSetChanged();
                        hidProgressDialog();
                    }

                    @Override
                    public void onFail(String failMsg) {
                        RemindControl.showSimpleToast(getActivity(), failMsg);
                        hidProgressDialog();
                    }
                });
            }

            @Override
            public void editAddress(int i) {
                Bundle arg = new Bundle();
                arg.putInt(UpdateAddressFragment.ADDRESS_POSITION, i);
                LaunchUtil.launchActivityForResult(REQUEST_CODE_FOR_ADDRESSFRAGMENT, getActivity(), BaseSwitchActivity.class, UpdateAddressFragment.class, arg);
            }

            @Override
            public void deleteAddress(final int i) {
                Resources res = getActivity().getResources();
                RemindDialog.RemindSource remindSource = new RemindDialog.RemindSource();
                remindSource.contentStr = res.getString(R.string.dailog_delete_address_content);
                remindSource.confirm = res.getString(R.string.str_confirm);
                remindSource.cancel = res.getString(R.string.str_cancel);
                RemindControl.showRemindDialog(getActivity(), remindSource, new RemindControl.OnRemindListener() {
                    @Override
                    public void onConfirm() {
                        final Address address = addressListPresenter.getAddress(i);
                        showProgressDialog();
                        addressListPresenter.deleteAddress(i, new ILoadingListener() {
                            @Override
                            public void onSuccess() {
                                if (address.type == Address.TYPE_DEFAULT && addressListPresenter.getAddressList().size() >= 1) {
                                    setDefaultAddress(0);
                                }
                                adapter.notifyDataSetChanged();
                                hidProgressDialog();
                            }

                            @Override
                            public void onFail(String failMsg) {
                                RemindControl.showSimpleToast(getActivity(), failMsg);
                                hidProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }
        });
    }

    private void hidProgressDialog() {
        RemindControl.hideProgressDialog();
    }

    private void showProgressDialog() {
        RemindControl.showProgressDialog(getActivity(), R.string.dailog_address_updata, null);
    }

    private void setTitle() {
        TextView addAddress = (TextView) getTitleBar().rightPart;
//        getTitleBar().rightPart.setVisibility(View.VISIBLE);
        addAddress.setText("新增");
        TextView title = (TextView) getTitleBar().title;
        title.setText("收货地址管理");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            // TODO
            case RESULT_CODE_ADD_ADDRESS: {
                updateView();
                break;
            }
            case RESULT_CODE_UPDATE_ADDRESS: {
                updateView();
                break;
            }
        }
    }
}
