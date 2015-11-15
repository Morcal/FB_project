package com.feibo.snacks.view.module.person.address;

import android.app.Activity;
import android.content.Intent;
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
 * Created by hcy on 2015/7/4.
 */
public class AddAddressFragment extends BaseTitleFragment {

    public final static int NAME_LENGTH_MAX = 15;
    public final static int STREET_LENGTH_MAX = 60;
    public final static int STREET_LENGTH_MIN = 5;

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

    private boolean isSelectState = false;
    private boolean isSubmit = false;

    private String provinceContent;
    private String cityContent;
    private String properContent;

    private AddressManager manager;

    TitleViewHolder titleHolder;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_address, null);
        ButterKnife.bind(this, rootView);
//        handleCacheShow();
        return rootView;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(getString(R.string.add_address));
        titleHolder.rightBtn.setText(getString(R.string.add_address_finish));
        titleHolder.rightBtn.setVisibility(View.VISIBLE);
        titleHolder.rightBtn.setTextColor(getResources().getColor(R.color.c1));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        manager = AddressManager.getInstance();
        isSubmit = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Address> addressList = manager.getAddressList();
        if (addressList == null || addressList.size() == 0) {
            selectCheckBtn.setSelected(true);
            isSelectState = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        handleCacheSave();
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

    // 检验是否有输入的数据
    public boolean checkInput() {
        String name = nameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String city = cityEdt.getText().toString().trim();
        String street = streetEdt.getText().toString().trim();
        if (name.length() > 0 ||
                phone.length() > 0 ||
                city.length() > 0 ||
                street.length() > 0) {
            return true;
        }
        return false;
    }

    // 提交的数据校验
    private Address checkSubmit() {
        String name = nameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String city = cityEdt.getText().toString().trim();
        String street = streetEdt.getText().toString().trim();
        int streetLength = street.length();

        View focusView = null;
        if (TextUtils.isEmpty(name)) {
            // 用户名为空
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_name_null);
            focusView = nameEdt;
        } else if (name.length() > NAME_LENGTH_MAX) {
            // 用户名超长
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_name_length_error);
            focusView = nameEdt;
        } else if (TextUtils.isEmpty(phone)) {
            // 手机号为空
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_null);
            focusView = phoneEdt;
        } else if (!phone.matches(getString(R.string.login_phone_rule))) {
            // 手机号格式错误
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_rule);
            focusView = phoneEdt;
        } else if (TextUtils.isEmpty(city)) {
            // 城市为空
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_city_null);
            focusView = cityEdt;
        } else if (TextUtils.isEmpty(street)) {
            // 街道为空
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_street_null);
            focusView = streetEdt;
        } else if (streetLength < STREET_LENGTH_MIN || streetLength > STREET_LENGTH_MAX) {
            // 街道长度不合格
            RemindControl.showSimpleToast(getActivity(), R.string.add_address_street_length_error);
            focusView = streetEdt;
        }

        // 有不合法的内容
        if (focusView != null) {
            focusView.requestFocus();
            return null;
        }

        Address address = new Address();
        address.name = name;
        address.phone = phone;
        address.type = isSelectState ? Address.TYPE_DEFAULT : Address.TYPE_NORMAL;
        address.province = provinceContent;
        address.city = cityContent;
        address.proper = properContent;
        address.street = street;
        return address;
    }


    // 设为默认
    @OnClick(R.id.add_address_set_default_address)
    public void handleAddressDefault() {
        List<Address> addreses = manager.getAddressList();
        if (addreses != null && addreses.size() >= 1) {
            isSelectState = !isSelectState;
            selectCheckBtn.setSelected(isSelectState);
        }
    }

    // 选择地址
    @OnClick(R.id.edit_address)
    public void handleAddressSelect() {
        final RemindDialog dialog = AddressDialog.show(getActivity(), (province, city, prop) -> {
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
        final Address address = checkSubmit();
        if (address != null) {
            RemindControl.showProgressDialog(getActivity(), R.string.dailog_address_updata, null);
            manager.addAddress(address, new ILoadingListener() {
                @Override
                public void onSuccess() {
                    isSubmit = true;
                    manager.clearAddressAddCache();
                    Intent intent = new Intent();
                    intent.putExtra(AddressFragment.RESULT_KEY_FOR_ADDRESS, address);
                    getActivity().setResult(AddressFragment.RESULT_CODE_ADD_ADDRESS, intent);
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

//    // 缓存数据
//    public void handleCacheSave() {
//        if (!isSubmit) {
//            Address address = new Address();
//            address.name = nameEdt.getText().toString().trim();
//            address.phone = phoneEdt.getText().toString().trim();
//            address.province = provinceContent;
//            address.city = cityContent;
//            address.proper = properContent;
//            address.street = streetEdt.getText().toString().trim();
//            address.type = isSelectState ? Address.TYPE_DEFAULT : Address.TYPE_NORMAL;
//            manager.setAddressAddCache(address);
//        }
//    }

//    // 显示缓存
//    public void handleCacheShow() {
//        Address address = manager.getAddressAddCache();
//        if (address != null) {
//            provinceContent = address.province;
//            cityContent = address.city;
//            properContent = address.proper;
//            nameEdt.setText(address.name);
//            nameEdt.setSelection(nameEdt.getText().length());
//            phoneEdt.setText(address.phone);
//            if(!TextUtils.isEmpty(provinceContent) && !TextUtils.isEmpty(cityContent) && !TextUtils.isEmpty(properContent)) {
//                cityEdt.setText(provinceContent + "\t\t" + cityContent + "\t\t" + properContent);
//            }
//            streetEdt.setText(address.street);
//            isSelectState = address.type == Address.TYPE_DEFAULT;
//            selectCheckBtn.setSelected(isSelectState);
//        }
//    }

    // 退出
    public void handleQuit() {
        if (checkInput()) {
            // 有输入
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