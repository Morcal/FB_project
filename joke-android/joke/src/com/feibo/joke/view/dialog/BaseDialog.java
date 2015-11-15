package com.feibo.joke.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.feibo.joke.R;

public abstract class BaseDialog extends AlertDialog implements android.view.View.OnClickListener{

    private View top;
    private View bottom;
    
    private boolean canceledOnTouchOutside;
    
    protected BaseDialog(Context context) {
        this(context, true);
    }
    
    protected BaseDialog(Context context, boolean canceledOnTouchOutside) {
        this(context, R.style.fullscreen_dialog, canceledOnTouchOutside);
    }
    
    public BaseDialog(Context context, int theme) {
        this(context, theme, true);
    }
    
    public BaseDialog(Context context, int theme, boolean canceledOnTouchOutside) {
        super(context, theme);
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ViewGroup.LayoutParams clp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View content = initContentView(layoutInflater);
        
        View layout = layoutInflater.inflate(R.layout.dialog_layout, null);
        
        if(canceledOnTouchOutside) {
            top = layout.findViewById(R.id.dialog_top);
            bottom = layout.findViewById(R.id.dialog_bottom);
            top.setOnClickListener(this);
            bottom.setOnClickListener(this);
        }
        
        LinearLayout contentLayout = (LinearLayout) layout.findViewById(R.id.dialog_content);
        contentLayout.addView(content, clp);
        
        setContentView(layout, clp);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        
        
        WindowManager.LayoutParams lParams = getWindow().getAttributes();
        lParams.gravity = Gravity.CENTER;
        lParams.width = LayoutParams.MATCH_PARENT;
        lParams.height = LayoutParams.MATCH_PARENT;
        lParams.alpha = 1.0f;
        lParams.dimAmount = 0.0f;
        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        getWindow().setAttributes(lParams);
    }
    
    @Override
    public void onClick(View v) {
        this.dismiss();
    }
    
    protected abstract View initContentView(LayoutInflater inflater); 

}
