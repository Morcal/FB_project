package com.feibo.joke.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("InflateParams")
public class VideoDetailDialog extends BaseDialog implements OnClickListener {

    private OnBtnClickListener listener;
    private Boolean isUserOwn;

    private VideoDetailDialog(Context context) {
        super(context);
    }

    @Override
    public View initContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_btn_report, null);

        View confirm = view.findViewById(R.id.dialog_remind_confirm);
        View cancle = view.findViewById(R.id.dialog_remind_cancle);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);

        if (isUserOwn) {
            tvConfirm.setText(getContext().getResources().getString(R.string.dialog_confirm_delete));
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.icon_delete);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvConfirm.setCompoundDrawables(drawable, null, null, null);
        }

        confirm.setOnClickListener(this);
        cancle.setOnClickListener(this);
        return view;
    }

    public static VideoDetailDialog show(Context context, Boolean isUserOwn) {
        VideoDetailDialog dialog = new VideoDetailDialog(context);
        dialog.isUserOwn = isUserOwn;
        dialog.show();

        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()){
        case R.id.dialog_remind_confirm:
            listener.onConfirm();
            break;
        case R.id.dialog_remind_cancle:
            listener.onCancel();
            break;
        default:
            break;
        }
        this.dismiss();
    }

    public interface OnBtnClickListener {
        void onConfirm();
        void onCancel();
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.listener = listener;
    }

}
