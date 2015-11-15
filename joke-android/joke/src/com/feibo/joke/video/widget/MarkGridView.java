package com.feibo.joke.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * 图标网格
 * @author Lidiqing
 *
 */
public class MarkGridView extends GridView {

	private static final int GRID_ITEM_WIDTH = 55;
	private static final int GRID_HORIZONTAL_SPACING = 24;
	private static final int GRID_VERTICAL_SPACING = 1;

	private float density;
	private int itemWidth;
	private int spacingHorizontal;
	private int spacingVertical;

	private MarkGridAdapter markGridAdapter;

	public MarkGridView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public MarkGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public MarkGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MarkGridView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		density = getResources().getDisplayMetrics().density;

		itemWidth = (int) (GRID_ITEM_WIDTH * density);
		spacingHorizontal = (int) (GRID_HORIZONTAL_SPACING * density);
		spacingVertical = (int) (GRID_VERTICAL_SPACING * density);

		setStretchMode(GridView.NO_STRETCH); // 设置为禁止拉伸模式
		setHorizontalSpacing(spacingHorizontal);
		setVerticalSpacing(spacingVertical);
		setColumnWidth(itemWidth);

		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				markGridAdapter.setPressedPosition(position);
				markGridAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		markGridAdapter = (MarkGridAdapter) adapter;
		int columnNum = markGridAdapter.getColumnNum();

		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.width = itemWidth * columnNum + (columnNum - 1)
				* spacingHorizontal;
		setNumColumns(columnNum);
		setLayoutParams(params);
	}
	
}
