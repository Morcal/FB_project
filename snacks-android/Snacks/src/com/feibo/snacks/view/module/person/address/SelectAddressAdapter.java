package com.feibo.snacks.view.module.person.address;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Address;


public class SelectAddressAdapter extends BaseSingleTypeAdapter<Address> {

    public static final int DEFAULT_SELECTED_ID = -1;
    private long selectStateItem = DEFAULT_SELECTED_ID;

    private SelectAddressOpterationListener listener;

    public SelectAddressAdapter(Context context) {
        super(context);
    }

    @Override
    public void setItems(List<Address> items) {
        super.setItems(items);
        if (items == null || items.size() == 0) {
            return;
        }
        if(selectStateItem != DEFAULT_SELECTED_ID){
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            Address address = items.get(i);
            if (address.type == Address.TYPE_DEFAULT) {
                selectStateItem = address.id;
            }
        }
    }

    public long getSelectedAddressID() {
        return selectStateItem;
    }

    public void setSelectedAddressID(long id) {
        selectStateItem = id;
    }

    public void setSelectAddressOpterationListener(SelectAddressOpterationListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        AddressHolder holder;
        if (view == null) {
            view =  LayoutInflater.from(mContext).inflate(R.layout.item_select_address, null);
            holder = new AddressHolder();
            holder.name = (TextView) view.findViewById(R.id.address_name);
            holder.phone = (TextView) view.findViewById(R.id.address_phone);
            holder.address = (TextView) view.findViewById(R.id.address);
            holder.edit = view.findViewById(R.id.address_edit);
            holder.defaultImg = view.findViewById(R.id.address_default_img);
            holder.selectState = (ImageView) view.findViewById(R.id.address_select_state);
            holder.bottomShadow = view.findViewById(R.id.bottom_shadow);
            view.setTag(holder);
        } else {
            holder = (AddressHolder) view.getTag();
        }
        final Address address = getItem(i);
        holder.name.setText(address.name);
        holder.phone.setText(address.phone);
        holder.address.setText(address.province + address.city + address.proper + address.street);
        holder.defaultImg.setVisibility(address.type == Address.TYPE_DEFAULT ? View.VISIBLE : View.GONE);

        if (i == mItems.size()-1) {
            holder.bottomShadow.setVisibility(View.VISIBLE);
        } else {
            holder.bottomShadow.setVisibility(View.GONE);
        }

        if (address.id == selectStateItem) {
            holder.selectState.setSelected(true);
        } else {
            holder.selectState.setSelected(false);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.editAddress(i);
                }
            }
        });
        return view;
    }

    private static class AddressHolder {
        public TextView name;
        public TextView phone;
        public TextView address;
        public ImageView selectState;
        public View edit;
        public View defaultImg;
        public View bottomShadow;
    }

    public static interface SelectAddressOpterationListener {
        void editAddress(int i);
    }
}
