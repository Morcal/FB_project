package com.feibo.joke.video.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.feibo.joke.R;

/**
 * 图标网格适配器
 * @author Lidiqing 2015/4/29
 *
 */
public class MarkGridAdapter extends BaseAdapter {

	private int[] markResources;
	private int pressedPosition;
	private Context context;

	public MarkGridAdapter(Context context) {
		this.context = context;
		pressedPosition = -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_mark, null);
			ViewHolder holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.image_chartlet);
			convertView.setTag(holder);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		if (position == pressedPosition) {
			viewHolder.imageView
					.setBackgroundResource(R.drawable.bg_chartlet_pressed);
		} else {
			viewHolder.imageView
					.setBackgroundResource(R.drawable.bg_chartlet);
		}
		viewHolder.imageView.setImageResource(markResources[position]);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public int getCount() {
		return markResources.length;
	}

	public int getColumnNum() {
		int count = getCount();
		int m = count % 2;
		return m == 0 ? count / 2 : count / 2 + 1;
	}

	public int getSelectMark() {
		if (pressedPosition != -1) {
			return markResources[pressedPosition];
		}
		return -1;
	}

	public void setPressedPosition(int pos) {
		pressedPosition = pos;
	}
	
	public void clearPressedPosition(){
		pressedPosition = -1;
	}

	public class ViewHolder {
		public ImageView imageView;
	}
	
	public void setMarks(int[] chartlets){
		this.markResources = chartlets; 
	}
}

