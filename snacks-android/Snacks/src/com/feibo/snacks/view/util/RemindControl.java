package com.feibo.snacks.view.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.bean.UpdateInfo;
import com.feibo.snacks.view.dialog.RemindDialog;
import com.feibo.snacks.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.snacks.view.dialog.RemindDialog.RemindSource;
import com.feibo.snacks.view.dialog.SimpleDialog;

/**
 * 对话框和Toast工具类
 */
@SuppressLint("InflateParams")
public class RemindControl {
    private static final String TAG = RemindControl.class.getSimpleName();
    private static Toast toast;
    private static Dialog progressDialog;
    private static RemindDialog dialog;
    private final static int TOAST_DEFAULT_BOTTOM = -2;
    private final static int TOAST_DEFAULT_CENTER = -1;

    public static void showDeleteCartItem(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.cart_delete_remind_content);
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showDeleteAllNoAvaibleCartItem(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.cart_delete_all_no_avaible_remind_content);
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static EditText showEditTextRemind(Context context, String title, String content, String hintText,
            final OnRemindListener listener) {

        // EditText editText = new EditText(context);
        // LinearLayout.LayoutParams params = new
        // LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        // UIUtil.dp2Px(null,100));
        // editText.setLayoutParams(params);
        // //ToDo 设置其他属性
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edittext_layout, null);
        // LinearLayout.LayoutParams params = new
        // LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        // UIUtil.dp2Px(null,100));
        // editText.setLayoutParams(params);
        // ToDo 设置其他属性
        EditText editText = (EditText) view.findViewById(R.id.edit_text);
        editText.setHint(hintText);
        editText.setText(content);
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentView = view;
        remindSource.title = title;
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
        return editText;
    }

    public static void showCancelOrdersRemind(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.orders_cancel_orders_content);
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showDeleteOrdersRemind(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.orders_delete_orders_content);
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showCancelAddressRemind(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = "当前地址未保存, 是否离开本页面";
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showServiceRemind(Context context, ServiceContact contact, final OnRemindListener listener) {
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = context.getString(R.string.snacks_official_service_phone_title) + " " + contact.phone;
        remindSource.confirm = context.getString(R.string.dialog_call_btn);
        remindSource.cancel = context.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showReiptRemind(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.orders_get_goods_content);
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showCommentExitRemind(Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = "亲，评价还未完成，是否要离开本页面";
        remindSource.confirm = res.getString(R.string.str_confirm);
        remindSource.cancel = res.getString(R.string.str_cancel);
        showRemindDialog(context, remindSource, listener);
    }

    public static void showInstallRemind(String fileName, Context context, final OnRemindListener listener) {
        Resources res = context.getResources();
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = res.getString(R.string.dialog_content);
        remindSource.confirm = res.getString(R.string.dialog_confirm);
        remindSource.cancel = res.getString(R.string.dialog_cancle);

        showRemindDialog(context, remindSource, listener);
    }

    public static void showUpdateRemind(Context context, UpdateInfo updateInfo, final OnRemindListener listener) {
        RemindSource source = new RemindSource();
        source.contentStr = TextUtils.isEmpty(updateInfo.desc) ? context.getResources().getString(
                R.string.dailog_update_default_msg) : updateInfo.desc;
        source.confirm = context.getResources().getString(R.string.dailog_update_btn_now);
        source.cancel = context.getResources().getString(R.string.dailog_update_btn_wait);
        showRemindDialog(context, source, listener);
    }

    public static void showRemindDialog(Context context, RemindSource remindSource, final OnRemindListener listener) {
        if (context == null || remindSource == null) {
            return;
        }
        if (TextUtils.isEmpty(remindSource.title) && remindSource.contentView == null
                && TextUtils.isEmpty(remindSource.contentStr)) {
            return;
        }

        dialog = RemindDialog.show(context, remindSource);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
                public void onConfirm() {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
    }

    public static Dialog showSimpleDialog(Context context, String msg, String commit, String cancel,
            final OnClickListener commitClick, final OnClickListener cancelClick) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_remind, null);
        TextView content = (TextView) view.findViewById(R.id.dialog_remind_content_string);
        View cutLineBtn = view.findViewById(R.id.dialog_remind_btn_cut_line);
        TextView commitBtn = (TextView) view.findViewById(R.id.dialog_remind_confirm);
        TextView cancleBtn = (TextView) view.findViewById(R.id.dialog_remind_cancle);
        content.setVisibility(View.VISIBLE);
        content.setText(msg);
        commitBtn.setText(commit);
        commitBtn.setVisibility(View.VISIBLE);
        if (cancel != null) {
            cutLineBtn.setVisibility(View.VISIBLE);
            cancleBtn.setVisibility(View.VISIBLE);
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

    public interface OnRemindListener {
        void onConfirm();

        void onCancel();
    }

    // =================================以下为ProgressDialog相关

    public static void showProgressDialog(Context context, int msgId, DialogInterface.OnDismissListener listener) {
        showProgressDialog(context, context.getResources().getString(msgId), listener);
    }

    public static void showProgressDialog(Context context, String msg) {
        showProgressDialog(context, msg, null);
    }

    public static void showProgressDialog(Context context, String msg, DialogInterface.OnDismissListener listener) {
        if (context == null) {
            return;
        }
        hideProgressDialog();
        progressDialog = new Dialog(context, R.style.progress_dialog);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null);
        TextView tvMsg = (TextView) view.findViewById(R.id.progess_message);
        if (TextUtils.isEmpty(msg)) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setText(msg);
        }
        progressDialog.setContentView(view);
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_toast);
        progressDialog.setOnDismissListener(listener);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && progressDialog.getContext() != null) {
            progressDialog.dismiss();
        }
    }

    // =================================以下为Toast相关

    /**
     * 默认居中Toast
     * 使用全局上下文
     * @param string
     */
    public static void showSimpleToast(String string) {
        showSimpleToast(AppContext.getContext(), string);
    }

    /**
     * 默认居中Toast
     * 
     * @param context
     * @param string
     */
    public static void showSimpleToast(Context context, String string) {
        showToast(context, string);
    }

    /**
     * 默认居中Toast
     * 
     * @param context
     * @param stringId
     */
    public static void showSimpleToast(Context context, int stringId) {
        showToast(context, context.getString(stringId));
    }

    /**
     *
     * @param context
     * @param stringId
     * @param marginBottom TOAST_DEFAULT_CENTER | TOAST_DEFAULT_BOTTOM |
     *            具体的距离底部的值
     * @param showTime
     */
    public static void showSimpleToast(Context context, int stringId, int marginBottom, int showTime) {
        showToast(context, context.getString(stringId), marginBottom, showTime);
    }

    /**
     * 可设置显示位置的Toast
     * 
     * @param context
     * @param string
     * @param marginBottom TOAST_DEFAULT_CENTER | TOAST_DEFAULT_BOTTOM |
     *            具体的距离底部的值
     */
    public static void showSimpleToast(Context context, String string, int marginBottom) {
        showToast(context, string, marginBottom);
    }

    /**
     * 可设置显示位置的Toast
     * 
     * @param context
     * @param stringId
     * @param marginBottom TOAST_DEFAULT_CENTER | TOAST_DEFAULT_BOTTOM |
     *            具体的距离底部的值
     */
    public static void showSimpleToast(Context context, int stringId, int marginBottom) {
        showToast(context, context.getString(stringId), marginBottom);
    }

    private static void showToast(Context context, String content) {
        showToast(context, content, TOAST_DEFAULT_CENTER);
    }

    private static void showToast(Context context, String content, int marginBottom) {
        showToast(context, content, marginBottom, Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, String content, int marginBottom, int showTime) {
        cancelToast();

        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }

        View layout = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(content);

        toast = new Toast(context);
        if (marginBottom == TOAST_DEFAULT_CENTER) {
            toast.setGravity(Gravity.CENTER, 0, 0); // 默认居中
        } else if (marginBottom == TOAST_DEFAULT_BOTTOM) {
            toast.setGravity(Gravity.BOTTOM, 0, 100); // 显示到距离底部100
        } else {
            toast.setGravity(Gravity.BOTTOM, 0, marginBottom); // 显示到距离底部marginBottom
        }
        toast.setDuration(showTime);
        toast.setView(layout);
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
