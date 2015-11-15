package com.feibo.snacks.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.feibo.snacks.R;

@SuppressLint("InflateParams")
public class RemindDialog extends AlertDialog implements android.view.View.OnClickListener {

    private OnDialogClickListener listener;
    private ViewGroup contentViewParent;
    private RemindSource source;
    private View view;

    public RemindDialog(Context context, RemindSource source) {
        super(context);
        this.source = source;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.alpha = 1.0f;
        lp.dimAmount = 1.0f;
        getWindow().setAttributes(lp);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        view = inflater.inflate(R.layout.dialog_remind, null);

        contentViewParent = (ViewGroup) view.findViewById(R.id.dialog_remind_content_view);
        TextView contentStr = (TextView) view.findViewById(R.id.dialog_remind_content_string);
        View btnCutLine =  view.findViewById(R.id.dialog_remind_btn_cut_line);
        View verCutLine =  view.findViewById(R.id.dialog_remind_cut_line_ver);
        TextView title= (TextView) view.findViewById(R.id.dialog_remind_title);
        TextView confirm = (TextView) view.findViewById(R.id.dialog_remind_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_remind_cancle);

        if(source.contentView != null){
            contentViewParent.setVisibility(View.VISIBLE);
            contentViewParent.addView(source.contentView,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
        }

        if(source.title != null){
            title.setVisibility(View.VISIBLE);
            title.setText(source.title);
        }

        if(source.contentStr != null){
            contentStr.setVisibility(View.VISIBLE);
            contentStr.setText(source.contentStr);
        }

        if(source.confirm != null){
            confirm.setVisibility(View.VISIBLE);
            confirm.setText(source.confirm);
        }

        if(source.cancel != null){
            cancel.setVisibility(View.VISIBLE);
            cancel.setText(source.cancel);
        }

        if(source.confirm!=null && source.cancel!=null){
            btnCutLine.setVisibility(View.VISIBLE);
        }else if(source.confirm == null && source.cancel == null){
            verCutLine.setVisibility(View.GONE);
        }

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        ViewGroup.LayoutParams clp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view, clp);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public static RemindDialog show(Context context, RemindSource remindSource) {
        RemindDialog dialog = new RemindDialog(context, remindSource);
        dialog.show();
        return dialog;
    }

    public static class RemindSource {
        public String title;
        public String contentStr;
        public View contentView;
        public String confirm;
        public String cancel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.dialog_remind_confirm:
            listener.onConfirm();
            break;
        case R.id.dialog_remind_cancle:
            listener.onCancel();
            break;
        default:
            break;
        }
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public interface OnDialogClickListener {
        void onConfirm();
        void onCancel();
    }
}
