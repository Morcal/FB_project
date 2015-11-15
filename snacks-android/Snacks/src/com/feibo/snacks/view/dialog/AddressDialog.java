package com.feibo.snacks.view.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.AddressManager;
import com.feibo.snacks.view.widget.wheelcity.WheelView;
import com.feibo.snacks.view.widget.wheelcity.adapter.AbstractWheelTextAdapter;

import java.util.List;

/**
 * 地址选择
 * Created by lidiqing on 15-9-8.
 */
public class AddressDialog {

    public static RemindDialog show(Context context, OnAddressChangedListener listener){
        View view = AddressHelper.createAddressView(context, listener);
        RemindDialog.RemindSource remindSource = new RemindDialog.RemindSource();
        remindSource.cancel =  context.getString(R.string.str_cancel);
        remindSource.confirm = context.getString(R.string.str_confirm);
        remindSource.title = context.getString(R.string.dialog_title_choose_address);
        remindSource.contentView = view;
        RemindDialog remindDialog = RemindDialog.show(context, remindSource);
        return remindDialog;
    }

    public static class AddressHelper {
        private static AddressWheelAdapter provinceAdapter;
        private static AddressWheelAdapter cityAdapter;
        private static AddressWheelAdapter areaAdapter;

        public static View createAddressView(final Context context, final OnAddressChangedListener listener) {
            View contentView = LayoutInflater.from(context).inflate(
                    R.layout.layout_wheelcity_cities, null);

            AddressManager.AddressData addressData = AddressManager.getInstance().getAddressData();
            if (addressData == null) {
                return contentView;
            }

            final WheelView provinceView = (WheelView) contentView.findViewById(R.id.list_province);
            final WheelView cityView = (WheelView) contentView.findViewById(R.id.list_city);
            final WheelView areaView = (WheelView) contentView.findViewById(R.id.list_area);

            provinceAdapter = new AddressWheelAdapter(context, addressData.provinceList);
            cityAdapter = new AddressWheelAdapter(context, ((AddressManager.Province) provinceAdapter.getSelectAddress()).cityList);
            areaAdapter = new AddressWheelAdapter(context, ((AddressManager.City) cityAdapter.getSelectAddress()).areaList);

            provinceView.setViewAdapter(provinceAdapter);
            cityView.setViewAdapter(cityAdapter);
            areaView.setViewAdapter(areaAdapter);

            listener.onChange(provinceAdapter.getSelectAddress().name,
                    cityAdapter.getSelectAddress().name,
                    areaAdapter.getSelectAddress().name);

            provinceView.addChangingListener((WheelView wheel, int oldValue, int newValue) -> {
                provinceAdapter.setSelectIndex(newValue);

                AddressManager.Province province = (AddressManager.Province) provinceAdapter.getSelectAddress();
                cityAdapter = new AddressWheelAdapter(context, province.cityList);
                cityView.setViewAdapter(cityAdapter);
                cityView.setCurrentItem(0);

                areaAdapter = new AddressWheelAdapter(context, ((AddressManager.City) province.cityList.get(0)).areaList);
                areaView.setViewAdapter(areaAdapter);
                areaView.setCurrentItem(0);

                listener.onChange(provinceAdapter.getSelectAddress().name,
                        cityAdapter.getSelectAddress().name,
                        areaAdapter.getSelectAddress().name);
            });

            cityView.addChangingListener((WheelView wheel, int oldValue, int newValue) -> {
                cityAdapter.setSelectIndex(newValue);

                AddressManager.City city = (AddressManager.City) cityAdapter.getSelectAddress();
                areaAdapter = new AddressWheelAdapter(context, city.areaList);
                areaView.setViewAdapter(areaAdapter);
                areaView.setCurrentItem(0);

                listener.onChange(provinceAdapter.getSelectAddress().name,
                        cityAdapter.getSelectAddress().name,
                        areaAdapter.getSelectAddress().name);
            });

            areaView.addChangingListener((WheelView wheel, int oldValue, int newValue) -> {
                areaAdapter.setSelectIndex(newValue);

                listener.onChange(provinceAdapter.getSelectAddress().name,
                        cityAdapter.getSelectAddress().name,
                        areaAdapter.getSelectAddress().name);
            });


            provinceView.setVisibleItems(3);
            return contentView;
        }

        private static class AddressWheelAdapter extends AbstractWheelTextAdapter {

            private List<AddressManager.AddressNameBean> addressNameList;

            private int selectIndex;

            private AddressWheelAdapter(Context context, List<AddressManager.AddressNameBean> addressNameBeanList) {
                super(context);
                addressNameList = addressNameBeanList;
                selectIndex = 0;
            }

            @Override
            protected CharSequence getItemText(int index) {
                return addressNameList.get(index).name;
            }

            @Override
            public int getItemsCount() {
                return addressNameList.size();
            }

            @Override
            protected void configureTextView(TextView view) {
                super.configureTextView(view);
                view.setTextSize(12);
                view.setPadding(10, 10, 10, 10);
                view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }

            public void setSelectIndex(int index) {
                selectIndex = index;
            }

            public AddressManager.AddressNameBean getSelectAddress() {
                return addressNameList.get(selectIndex);
            }
        }
    }

    public static interface OnAddressChangedListener {
        void onChange(String province, String city, String prop);
    }

}