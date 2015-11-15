package com.feibo.joke.view.dialog;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.view.module.mine.edit.AddressManager;
import com.feibo.joke.view.widget.wheelcity.OnWheelChangedListener;
import com.feibo.joke.view.widget.wheelcity.WheelView;
import com.feibo.joke.view.widget.wheelcity.adpter.AbstractWheelTextAdapter;

public class AddressDialog extends BaseDialog implements View.OnClickListener {

	private OnAddressChangedListener listener;
	
	private String province, city;

	protected AddressDialog(Context context) {
		super(context);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		this.dismiss();
		if(v.getId() == R.id.btn_yes) {
			if(listener != null) {
				listener.onChange(province, city);
			}
		}
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.dialog_address, null);

		View addressView = AddressHelper.createAddressView(getContext(),
				new OnAddressChangedListener() {
					
					@Override
					public void onChange(String province, String city) {
						AddressDialog.this.province = province;
						AddressDialog.this.city = city;
					}
				});
		view.addView(addressView, 1, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		view.findViewById(R.id.btn_yes).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);

		return view;
	}

	public static AddressDialog show(Context context,
			OnAddressChangedListener listener) {
		AddressDialog dialogHelp = new AddressDialog(context);
		dialogHelp.listener = listener;
		dialogHelp.show();
		return dialogHelp;
	}

	public static class AddressHelper {
		private static AddressWheelAdapter provinceAdapter;
		private static AddressWheelAdapter cityAdapter;

		public static View createAddressView(final Context context,
				final OnAddressChangedListener listener) {
			View contentView = LayoutInflater.from(context).inflate(
					R.layout.layout_wheelcity_cities, null);

			AddressManager.AddressData addressData = AddressManager
					.getInstance().getAddressData();
			if (addressData == null) {
				return contentView;
			}

			final WheelView provinceView = (WheelView) contentView
					.findViewById(R.id.list_province);
			final WheelView cityView = (WheelView) contentView
					.findViewById(R.id.list_city);

			provinceAdapter = new AddressWheelAdapter(context,
					addressData.provinceList);
			cityAdapter = new AddressWheelAdapter(context,
					((AddressManager.Province) provinceAdapter
							.getSelectAddress()).cityList);

			provinceView.setViewAdapter(provinceAdapter);
			cityView.setViewAdapter(cityAdapter);

			listener.onChange(provinceAdapter.getSelectAddress().name,
					cityAdapter.getSelectAddress().name);

			provinceView.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					provinceAdapter.setSelectIndex(newValue);

					AddressManager.Province province = (AddressManager.Province) provinceAdapter
							.getSelectAddress();
					cityAdapter = new AddressWheelAdapter(context,
							province.cityList);
					cityView.setViewAdapter(cityAdapter);
					cityView.setCurrentItem(0);

					listener.onChange(provinceAdapter.getSelectAddress().name,
							cityAdapter.getSelectAddress().name);
				}
			});

			cityView.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					cityAdapter.setSelectIndex(newValue);

					listener.onChange(provinceAdapter.getSelectAddress().name,
							cityAdapter.getSelectAddress().name);
				}
			});

			provinceView.setVisibleItems(3);
			return contentView;
		}

		private static class AddressWheelAdapter extends
				AbstractWheelTextAdapter {

			private List<AddressManager.AddressNameBean> addressNameList;

			private int selectIndex;

			private AddressWheelAdapter(Context context,
					List<AddressManager.AddressNameBean> addressNameBeanList) {
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
				view.setTextSize(25);
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
		void onChange(String province, String city);
	}

}
