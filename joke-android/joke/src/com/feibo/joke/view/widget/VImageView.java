package com.feibo.joke.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.feibo.joke.R;

public class VImageView extends LinearLayout{

    private ImageView image;
    private ImageView vImage;
    
    public VImageView(Context context) {
        this(context, null);
    }
    
    public VImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.VImageView);
        float vSize = t.getDimension(R.styleable.VImageView_vSize, 0);
        Drawable d = t.getDrawable(R.styleable.VImageView_vImage); 
        boolean showV = t.getBoolean(R.styleable.VImageView_showV, true);
        t.recycle();
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.view_v_image, null);
        image = (ImageView) view.findViewById(R.id.img_avatar);
        vImage = (ImageView) view.findViewById(R.id.img_v);
        
        if(vSize != 0) {
            android.view.ViewGroup.LayoutParams lp = vImage.getLayoutParams();
            lp.height = (int) vSize;
            lp.width = (int) vSize;
            vImage.setLayoutParams(lp);
        }
        if(!showV) {
            vImage.setVisibility(View.INVISIBLE);
        }
        if(d != null) {
            vImage.setImageDrawable(d);
        }
        
        attachViewToParent(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    public ImageView getImageView() {
        return image;
    }
    
    /** 是否显示红人 */
    public void showSensation(boolean showSensation) {
        if(vImage != null) {
            vImage.setVisibility(showSensation ? View.VISIBLE : View.INVISIBLE);
        }
    }
    

}
