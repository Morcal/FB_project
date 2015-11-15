package com.feibo.snacks.view.module.person.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Address;

import java.util.List;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/2.
 */
public class AddressAdapter extends BaseSingleTypeAdapter<Address> {

    private long selectStateItem = -1;

    private AddressOpterationListener listener;

    public AddressAdapter(Context context) {
        super(context);
    }

    @Override
    public void setItems(List<Address> items) {
        super.setItems(items);
        if (items == null || items.size() == 0) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            Address address = items.get(i);
            if (address.type == Address.TYPE_DEFAULT) {
                selectStateItem = address.id;
            }
        }
    }

    public void setDefaultAddress(long id) {
        selectStateItem = id;
    }

    public void setAddressOpterationListener(AddressOpterationListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        AddressHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_address, null);
            holder = new AddressHolder();
            holder.name = (TextView) view.findViewById(R.id.address_name);
            holder.phone = (TextView) view.findViewById(R.id.address_phone);
            holder.address = (TextView) view.findViewById(R.id.address);
            holder.edit = (ViewGroup) view.findViewById(R.id.address_edit);
            holder.delete = (ViewGroup) view.findViewById(R.id.address_delete);
            holder.setDefaultAddress = (ViewGroup) view.findViewById(R.id.address_set_default_address);
            holder.selectState = (ImageView) view.findViewById(R.id.address_select_state);
            holder.bottomShadow = view.findViewById(R.id.bottom_shadow);
            view.setTag(holder);
        } else {
            holder = (AddressHolder) view.getTag();
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        final Address address = getItem(i);
        holder.name.setText("收件人 : " + address.name);
        holder.phone.setText(address.phone);
        holder.address.setText(address.province + address.city + address.proper + address.street);

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

        holder.setDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.id == selectStateItem) {
                    return;
                }
                if (listener != null) {
                    listener.setDefaultAddress(i);
                }
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.editAddress(i);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.deleteAddress(i);
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
        public ViewGroup edit;
        public ViewGroup delete;
        public ViewGroup setDefaultAddress;
        public View bottomShadow;
    }

    public static interface AddressOpterationListener {
        void setDefaultAddress(int i);

        void editAddress(int i);

        void deleteAddress(int i);
    }
}
