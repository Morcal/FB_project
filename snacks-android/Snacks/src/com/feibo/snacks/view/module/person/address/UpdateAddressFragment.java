package com.feibo.snacks.view.module.person.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.AddressManager;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.dialog.AddressDialog;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.dialog.RemindDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hcy on 2015/7/5.
 */
public class UpdateAddressFragment extends BaseTitleFragment{

    public static final String ADDRESS_POSITION = "position";

    @Bind(R.id.edit_name)
    EditText nameEdt;

    @Bind(R.id.edit_phone)
    EditText phoneEdt;

    @Bind(R.id.edit_address)
    EditText cityEdt;

    @Bind(R.id.edit_street)
    EditText streetEdt;

    @Bind(R.id.add_address_select_state)
    ImageView selectCheckBtn;

    private boolean isCityChange = false;
    private boolean isSelectState = false;

    private String provinceContent;
    private String cityContent;
    private String properContent;

    private Address address;
    private int addressIndex;

    private AddressManager addressManager;

    private TitleViewHolder titleHolder;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_address, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText("修改收货地址");
        titleHolder.rightBtn.setText("保存");
        titleHolder.rightBtn.setTextColor(getResources().getColor(R.color.c1));
        titleHolder.rightBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle arg = getArguments();
        addressIndex = arg.getInt(ADDRESS_POSITION);
        addressManager = AddressManager.getInstance();
        address = addressManager.getAddress(addressIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        titleHolder.onDestroy();
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleQuit();
        } else {
            super.onKeyDown(keyCode, event);
        }
    }

    private void initDatas() {
        nameEdt.setText(address.name);
        phoneEdt.setText(address.phone);
        cityEdt.setText(address.province + "\t\t" + address.city + "\t\t" + address.proper);
        streetEdt.setText(address.street);
        List<Address> addressList = addressManager.getAddressList();
        if (address.type == Address.TYPE_DEFAULT ||addressList == null || addressList.size() <= 1) {
            selectCheckBtn.setSelected(true);
            isSelectState = true;
        } else {
            selectCheckBtn.setSelected(false);
        }
    }

    private boolean checkChange() {
        String name = nameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String city = cityEdt.getText().toString().trim();
        String street = streetEdt.getText().toString().trim();

        if (!name.equals(address.name) ||
                !phone.equals(address.phone) ||
                !city.equals(address.province + "\t\t" + address.city + "\t\t" + address.proper) ||
                !street.equals(address.street)) {
            return true;
        }
        return false;
    }

    private boolean canSave() {
        boolean canSubmit = true;
        View focusView = null;

        String name = nameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String city = cityEdt.getText().toString().trim();
        String street = streetEdt.getText().toString().trim();
        int streetLength = street.length();

        if (TextUtils.isEmpty(street)) {
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_street_null);
            focusView = streetEdt;
            canSubmit = false;
        }else if(streetLength < AddAddressFragment.STREET_LENGTH_MIN || streetLength > AddAddressFragment.STREET_LENGTH_MAX){
            RemindControl.showSimpleToast(getActivity(),R.string.add_address_street_length_error);
            focusView = streetEdt;
            canSubmit = false;
        }
        if (TextUtils.isEmpty(city)) {
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_city_null);
            focusView = cityEdt;
            canSubmit = false;
        }
        if (TextUtils.isEmpty(phone)) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_null);
            focusView = phoneEdt;
            canSubmit = false;
        } else if (!phone.matches(getString(R.string.login_phone_rule))) {
            RemindControl.showSimpleToast(getActivity(),R.string.login_edit_err_phone_rule);
            focusView = phoneEdt;
            canSubmit = false;
        }
        if (TextUtils.isEmpty(name)) {
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_name_null);
            focusView = nameEdt;
            canSubmit = false;
        }else if(name.length() > AddAddressFragment.NAME_LENGTH_MAX){
            RemindControl.showSimpleToast(getActivity(),R.string.add_address_name_length_error);
            focusView = nameEdt;
            canSubmit = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        if (canSubmit) {
            address.name = name;
            address.phone = phone;
            address.type = isSelectState ? Address.TYPE_DEFAULT : Address.TYPE_NORMAL;
            if(isCityChange){
                address.province = provinceContent;
                address.city = cityContent;
                address.proper = properContent;
            }
            address.street = street;
        }
        return canSubmit;
    }

    @OnClick(R.id.add_address_set_default_address)
    public void handleAddressDefault() {
        List<Address> addressList = addressManager.getAddressList();
        if(addressList != null && addressList.size() > 1 && address.type != Address.TYPE_DEFAULT){
            isSelectState = !isSelectState;
            selectCheckBtn.setSelected(isSelectState);
        }
    }

    // 选择地址
    @OnClick(R.id.edit_address)
    public void handleAddressSelect(){
        final RemindDialog dialog = AddressDialog.show(getActivity(), (province, city, prop) -> {
            isCityChange = true;
            provinceContent = province;
            cityContent = city;
            properContent = prop;
        });

        dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
            @Override
            public void onConfirm() {
                String unSet = getActivity().getString(R.string.add_address_un_set);
                if (provinceContent.endsWith(unSet) || cityContent.endsWith(unSet) || properContent.endsWith(unSet)) {
                    RemindControl.showSimpleToast(getActivity(), R.string.please_set_full_address);
                    return;
                }
                cityEdt.setText(provinceContent + "\t\t" + cityContent + "\t\t" + properContent);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                properContent = null;
                cityContent = null;
                properContent = null;
                dialog.dismiss();
            }
        });
    }

    // 提交
    public void handleSubmit() {
        if(canSave()) {
            RemindControl.showProgressDialog(getActivity(), R.string.dailog_address_updata, null);
            addressManager.updateAddress(address, new ILoadingListener() {
                @Override
                public void onSuccess() {
                    AddressManager manager = AddressManager.getInstance();
                    manager.isDefaultAddress(address, addressIndex);

                    Intent intent = new Intent();
                    intent.putExtra(AddressFragment.RESULT_KEY_FOR_ADDRESS, address);
                    getActivity().setResult(AddressFragment.RESULT_CODE_UPDATE_ADDRESS, intent);
                    RemindControl.hideProgressDialog();
                    getActivity().finish();
                }

                @Override
                public void onFail(String failMsg) {
                    RemindControl.showSimpleToast(getActivity(), failMsg);
                    RemindControl.hideProgressDialog();
                }
            });
        }
    }

    // 退出
    public void handleQuit() {

        if (checkChange()) {
            RemindControl.showCancelAddressRemind(getActivity(), new RemindControl.OnRemindListener() {
                @Override
                public void onConfirm() {
                    getActivity().finish();
                }

                @Override
                public void onCancel() {
                    return;
                }
            });
        } else {
            getActivity().finish();
        }

    }


    class TitleViewHolder {

        @Bind(R.id.head_title)
        TextView titleText;

        @Bind(R.id.head_right)
        TextView rightBtn;

        public TitleViewHolder(View view) {
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        // 退出
        @OnClick(R.id.head_left)
        public void clickHeadLeft() {
            handleQuit();
        }

        // 提交
        @OnClick(R.id.head_right)
        public void clickHeadRight() {
            handleSubmit();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }
}
