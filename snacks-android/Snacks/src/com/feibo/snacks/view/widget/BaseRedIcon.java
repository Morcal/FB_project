package com.feibo.snacks.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.snacks.R;

/**
 * User: LinMIWi(80383585@qq.com)
 * Time: 2015-07-10  07:07
 * FIXME
 */
public class BaseRedIcon extends LinearLayout {

    private final static int NULL = 0;
    private TextView tvNum;
    private ImageView ivImg;
    private TextView tvTitle;

    public BaseRedIcon(Context context) {
        super(context);
        initView(context, null, NULL);
    }

    public BaseRedIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, NULL);
    }

    @SuppressLint("NewApi")
    public BaseRedIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BaseRedIcon, defStyle, 0);
        int num = a.getInt(R.styleable.BaseRedIcon_num, NULL);
        String title = a.getString(R.styleable.BaseRedIcon_title);
        Drawable drawable = a.getDrawable(R.styleable.BaseRedIcon_image);
        a.recycle();

        View root = LayoutInflater.from(context).inflate(R.layout.base_red_icon, null);

        ivImg = ((ImageView) root.findViewById(R.id.iv_img));
        setImage(drawable);

        tvTitle = ((TextView) root.findViewById(R.id.tv_title));
        setTitle(title);

        tvNum = (TextView) root.findViewById(R.id.tv_num);
        setNum(num);

//        attachViewToParent(root,NULL,null);
        addView(root);
    }

    public void setTitle(String title) {
        if(!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }else{
            tvTitle.setVisibility(View.GONE);
        }
    }

    public void setImage(Drawable drawable) {
        if(drawable != null){
            ivImg.setBackgroundDrawable(drawable);
        }else{
            ivImg.setVisibility(View.GONE);
        }
    }

    public void setNum(int num){
        if(num > NULL){
            tvNum.setText(num+"");
            tvNum.setVisibility(View.VISIBLE);
        }else{
            tvNum.setVisibility(View.INVISIBLE);
        }
    }
}
