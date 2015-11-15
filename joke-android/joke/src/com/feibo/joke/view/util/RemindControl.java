package com.feibo.joke.view.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.SimpleDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;

public class RemindControl {

    public static void showDownloadRemind(Context context, RemindSource remindSource, final OnRemindListener listener) {
        final RemindDialog dialog = RemindDialog.show(context, remindSource);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onConfirm() {
                listener.onConfirm();
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                listener.onCancel();
                dialog.dismiss();
            }
        });
    }

    public static Dialog showSimpleDialog(Context context, String msg, String commit, String cancel,
            OnClickListener commitClick) {
        return showSimpleDialog(context, msg, commit, cancel, commitClick, null);
    }

    public static Dialog showSimpleDialog(Context context, String msg, String commit, String cancel,
            final OnClickListener commitClick, final OnClickListener cancelClick) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_base, null);
        TextView content = (TextView) view.findViewById(R.id.dialog_remind_title);
        TextView commitBtn = (TextView) view.findViewById(R.id.dialog_remind_confirm);
        TextView cancleBtn = (TextView) view.findViewById(R.id.dialog_remind_cancle);
        content.setText(msg);
        commitBtn.setText(commit);
        if (cancel == null) {
            cancleBtn.setVisibility(View.GONE);
        } else {
            cancleBtn.setText(cancel);
        }
        final SimpleDialog dialog = new SimpleDialog(context, view);
        dialog.setCanceledOnTouchOutside(false);

        commitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (commitClick != null) {
                    commitClick.onClick(v);
                }
            }
        });

        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (cancelClick != null) {
                    cancelClick.onClick(v);
                }
            }
        });
        dialog.show();
        return dialog;
    }

    public static void showUpdateRemind(Context context, String title, final OnRemindListener listener) {
        RemindSource source = new RemindSource();
        source.title = title;
        source.confirm = context.getResources().getString(R.string.dialog_confirm_sure);
        source.cancel = context.getResources().getString(R.string.dialog_cancle);
        final RemindDialog dialog = RemindDialog.show(context, source);

        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onConfirm() {
                listener.onConfirm();
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                listener.onCancel();
                dialog.dismiss();
            }
        });
    }

    public static interface OnRemindListener {
        void onConfirm();

        void onCancel();
    }

}
