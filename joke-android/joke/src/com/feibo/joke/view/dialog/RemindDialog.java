package com.feibo.joke.view.dialog;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;


public class RemindDialog extends BaseDialog implements android.view.View.OnClickListener {

    private OnDialogClickListener listener;
    private RemindSource source;

    protected boolean defaultDismiss;
    
    protected HoldeView holdeView;

    public RemindDialog(Context context, RemindSource source) {
        this(context, source, true);
    }
    
    public RemindDialog(Context context, RemindSource source, boolean canceledOnTouchOutside) {
        super(context, canceledOnTouchOutside);
        this.source = source;
    }

    public RemindDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public View initContentView(LayoutInflater inflater) {
        View view = inflater.inflate(getContentViewLayout(), null);

        initContentView(view, source);
        return view;
    }
    
    protected int getContentViewLayout() {
        return R.layout.dialog_base;
    }
    
    protected void initContentView(View view, RemindSource source) {
        holdeView = new HoldeView();
        holdeView.title = (TextView) view.findViewById(R.id.dialog_remind_title);
        holdeView.confirm = (TextView) view.findViewById(R.id.dialog_remind_confirm);
        holdeView.cancel = (TextView) view.findViewById(R.id.dialog_remind_cancle);
        holdeView.yes = (TextView) view.findViewById(R.id.dialog_yes);
        holdeView.content = (TextView) view.findViewById(R.id.dialog_remind_content);
        holdeView.btnLayout = view.findViewById(R.id.btn_layout);
        
        if(holdeView.content != null) {
            holdeView.content.setVisibility(source.content == null ? View.GONE : View.VISIBLE);
            holdeView.content.setText(source.content == null ? "" : source.content);
            holdeView.content.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        if(StringUtil.isEmpty(source.cancel)) {
            holdeView.confirm = holdeView.yes;
            holdeView.yes.setVisibility(View.VISIBLE);
            holdeView.btnLayout.setVisibility(View.GONE);
        } else {
            holdeView.cancel.setText(source.cancel);
        }
        
        holdeView.confirm.setText(source.confirm);
        holdeView.title.setText(source.title);
        
        holdeView.cancel.setOnClickListener(this);
        holdeView.confirm.setOnClickListener(this);
    }

    public static RemindDialog show(Context context, RemindSource remindSource) {
        return show(context, remindSource, false);
    }

    public static RemindDialog show(Context context, RemindSource remindSource, boolean defaultDismiss) {
        RemindDialog dialog = null;
        try {
            dialog = new RemindDialog(context, remindSource);
            dialog.defaultDismiss = defaultDismiss;
            dialog.show();
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
        return dialog;
    }
    
    public class HoldeView {
        public TextView title;
        public TextView confirm;
        public TextView cancel;
        public TextView yes;
        public TextView content;
        public View btnLayout;
    }

    public static class RemindSource {
        public String title;
        public String content;
        public String confirm;
        public String cancel;
        
        public RemindSource() {
        }
        
        public RemindSource(String title) {
            this(title, "确定", "取消");
        }
        
        public RemindSource(String title, String confirm, String cancel) {
            this(title, null, confirm, cancel);
        }
        
        public RemindSource(String title, String content, String confirm, String cancel) {
            this.title = title;
            this.content = content;
            this.confirm = confirm;
            this.cancel = cancel;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.dialog_remind_confirm:
        case R.id.dialog_yes:
            if(listener != null) {
                listener.onConfirm();
            }
            break;
        case R.id.dialog_remind_cancle:
            if(listener != null) {
                listener.onCancel();
            }
            break;
        default:
            break;
        }
        if(defaultDismiss && isShowing()) {
            dismiss();
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
