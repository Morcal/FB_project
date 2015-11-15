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
import com.feibo.joke.view.module.mine.edit.TimeManager;
import com.feibo.joke.view.widget.wheelcity.OnWheelChangedListener;
import com.feibo.joke.view.widget.wheelcity.WheelView;
import com.feibo.joke.view.widget.wheelcity.adpter.AbstractWheelTextAdapter;

public class TimeDialog extends BaseDialog implements
		View.OnClickListener {

	private OnTimeChangedListener listener;

	private String year, month, day;

	protected TimeDialog(Context context) {
		super(context);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		this.dismiss();
		if (v.getId() == R.id.btn_yes) {
			if (listener != null) {
				listener.onChange(year, month, day);
			}
		}
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.dialog_address, null);
		((TextView) view.findViewById(R.id.wheel_title)).setText("请选择时间");

		View addressView = AddressHelper.createAddressView(getContext(),
				new OnTimeChangedListener() {

					@Override
					public void onChange(String year, String month, String day) {
						TimeDialog.this.year = year;
						TimeDialog.this.month = month;
						TimeDialog.this.day = day;
					}
				});
		view.addView(addressView, 1, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		view.findViewById(R.id.btn_yes).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);

		return view;
	}

	public static TimeDialog show(Context context,
			OnTimeChangedListener listener) {
		TimeDialog dialogHelp = new TimeDialog(context);
		dialogHelp.listener = listener;
		dialogHelp.show();
		return dialogHelp;
	}

	public static class AddressHelper {
		private static TimeWheelAdapter yearAdapter;
		private static TimeWheelAdapter monthAdapter;
		private static TimeWheelAdapter dayAdapter;

		public static View createAddressView(final Context context,
				final OnTimeChangedListener listener) {
			View contentView = LayoutInflater.from(context).inflate(
					R.layout.layout_wheel_time, null);

			TimeManager.TimeData timeData = TimeManager
					.getInstance().getTimeData();
			if (timeData == null) {
				return contentView;
			}

			final WheelView yearView = (WheelView) contentView
					.findViewById(R.id.list_year);
			final WheelView monthView = (WheelView) contentView
					.findViewById(R.id.list_month);
			final WheelView dayView = (WheelView) contentView
					.findViewById(R.id.list_day);

			yearAdapter = new TimeWheelAdapter(context,
					timeData.yearList);
			monthAdapter = new TimeWheelAdapter(
					context,
					((TimeManager.Year) yearAdapter.getSelectAddress()).monthList);
			dayAdapter = new TimeWheelAdapter(
					context,
					((TimeManager.Month) monthAdapter.getSelectAddress()).dayList);

			yearView.setViewAdapter(yearAdapter);
			monthView.setViewAdapter(monthAdapter);
			dayView.setViewAdapter(dayAdapter);

			listener.onChange(yearAdapter.getSelectAddress().name,
					monthAdapter.getSelectAddress().name,
					dayAdapter.getSelectAddress().name);

			yearView.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					yearAdapter.setSelectIndex(newValue);

					TimeManager.Year year = (TimeManager.Year) yearAdapter.getSelectAddress();
	                monthAdapter = new TimeWheelAdapter(context, year.monthList);
	                monthView.setViewAdapter(monthAdapter);
	                monthView.setCurrentItem(0);

	                dayAdapter = new TimeWheelAdapter(context, ((TimeManager.Month) year.monthList.get(0)).dayList);
	                dayView.setViewAdapter(dayAdapter);
	                dayView.setCurrentItem(0);

	                listener.onChange(yearAdapter.getSelectAddress().name,
	                        monthAdapter.getSelectAddress().name,
	                        dayAdapter.getSelectAddress().name);
				}
			});

			monthView.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					monthAdapter.setSelectIndex(newValue);
					
					TimeManager.Month month = (TimeManager.Month) monthAdapter.getSelectAddress();
					
	                dayAdapter = new TimeWheelAdapter(context, month.dayList);
	                dayView.setViewAdapter(dayAdapter);
	                dayView.setCurrentItem(0);

	                listener.onChange(yearAdapter.getSelectAddress().name,
	                        monthAdapter.getSelectAddress().name,
	                        dayAdapter.getSelectAddress().name);
				}
			});

			dayView.addChangingListener(new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					dayAdapter.setSelectIndex(newValue);

					listener.onChange(yearAdapter.getSelectAddress().name,
							monthAdapter.getSelectAddress().name,
							dayAdapter.getSelectAddress().name);
				}
			});

			yearView.setVisibleItems(3);
			return contentView;
		}

		private static class TimeWheelAdapter extends
				AbstractWheelTextAdapter {

			private List<TimeManager.TimeNameBean> timeNameList;

			private int selectIndex;

			private TimeWheelAdapter(Context context,
					List<TimeManager.TimeNameBean> timeNameBeanList) {
				super(context);
				timeNameList = timeNameBeanList;
				selectIndex = 0;
			}

			@Override
			protected CharSequence getItemText(int index) {
				return timeNameList.get(index).name;
			}

			@Override
			public int getItemsCount() {
				return timeNameList.size();
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

			public TimeManager.TimeNameBean getSelectAddress() {
				return timeNameList.get(selectIndex);
			}
		}

	}

	public static interface OnTimeChangedListener {
		void onChange(String year, String month, String day);
	}

}
