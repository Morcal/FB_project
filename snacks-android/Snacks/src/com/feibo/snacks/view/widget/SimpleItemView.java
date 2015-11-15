package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.feibo.snacks.R;

/**
 * 条目
 * Created by lidiqing on 15-9-11.
 */
public class SimpleItemView extends FrameLayout {

    private TextView nameView;
    private TextView valueView;
    private View arrowView;
    private View dividerView;

    private boolean hasArrow;
    private boolean hasDivider;
    private boolean canPressed;

    private String nameText;
    private String valueText;
    private int valueColor;
    private float valueSize;
    private float itemPadding;

    public SimpleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimpleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleItemView);
        hasArrow = a.getBoolean(R.styleable.SimpleItemView_has_arrow, true);
        hasDivider = a.getBoolean(R.styleable.SimpleItemView_has_divider, true);
        nameText = a.getString(R.styleable.SimpleItemView_name_text);
        valueText = a.getString(R.styleable.SimpleItemView_value_text);
        valueColor = a.getColor(R.styleable.SimpleItemView_value_color, context.getResources().getColor(R.color.c8));
        valueSize = a.getDimension(R.styleable.SimpleItemView_value_size, context.getResources().getDimension(R.dimen.s4));
        itemPadding = a.getDimension(R.styleable.SimpleItemView_item_padding, 0);
        canPressed = a.getBoolean(R.styleable.SimpleItemView_can_pressed, false);
        a.recycle();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, null);
        view.setPadding((int) itemPadding, 0, (int) itemPadding, 0);
        view.setBackgroundResource(canPressed ? R.drawable.bg_item_simple_clickable : R.drawable.bg_item_simple_normal);
        addViewInLayout(view, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    protected void onFinishInflate() {
        nameView = (TextView) findViewById(R.id.text_name);
        valueView = (TextView) findViewById(R.id.text_value);
        arrowView = findViewById(R.id.image_arrow);
        dividerView = findViewById(R.id.divider);

        arrowView.setVisibility(hasArrow ? VISIBLE : GONE);
        dividerView.setVisibility(hasDivider ? VISIBLE : GONE);

        nameView.setText(nameText);
        valueView.setText(valueText);
        valueView.setTextColor(valueColor);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
    }

    public void setNameText(String name) {
        nameView.setText(name);
    }

    public void setValueText(String value) {
        valueView.setText(value);
    }

    public void setValueColor(int valueColor) {
        valueView.setTextColor(valueColor);
    }
}
