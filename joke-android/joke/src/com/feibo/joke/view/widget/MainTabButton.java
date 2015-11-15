package com.feibo.joke.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;

public class MainTabButton extends LinearLayout{

    private TextView textview;
    private View redHintView;
    
    private int topPadding;
    private String text;
    private Drawable topDrawableNormal;
    private Drawable topDrawablePress;
    
    @SuppressLint("NewApi")
    public MainTabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }
    
    public MainTabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
    
    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MainTabButton, defStyle, 0);
        text = a.getString(R.styleable.MainTabButton_text);
        topDrawableNormal = a.getDrawable(R.styleable.MainTabButton_drawableTopNormal);
        topDrawablePress = a.getDrawable(R.styleable.MainTabButton_drawableTopPress);
        topPadding = a.getDimensionPixelSize(R.styleable.MainTabButton_drawablePadding, 14);
        boolean redHintVisible = a.getBoolean(R.styleable.MainTabButton_redHintVisible, true);
        a.recycle();
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.main_bottom_tab, null);
        redHintView = root.findViewById(R.id.tab_round);
        textview = (TextView) root.findViewById(R.id.tab);
        textview.setText(text);
        textview.setCompoundDrawablePadding(topPadding);
        setRedHintVisible(redHintVisible);
        
        setSelect(false);
        attachViewToParent(root, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    /** 设置红点显示 */
    public void setRedHintVisible(boolean visible) {
        redHintView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
    
    public void setSelect(boolean select) {
        if(select) {
            topDrawablePress.setBounds(0, 0, topDrawablePress.getMinimumWidth(), topDrawablePress.getMinimumHeight());
            textview.setCompoundDrawables(null, topDrawablePress, null, null);
            textview.setTextColor(getResources().getColor(R.color.main_tab_color_press));
        } else {
            topDrawableNormal.setBounds(0, 0, topDrawableNormal.getMinimumWidth(), topDrawableNormal.getMinimumHeight());
            textview.setCompoundDrawables(null, topDrawableNormal, null, null);
            textview.setTextColor(getResources().getColor(R.color.main_tab_color_normal));
        }
    }
    
}
