package com.feibo.snacks.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.feibo.snacks.R;
import com.feibo.social.base.Platform.Extra;

@SuppressLint("InflateParams")
public class ShareDialog extends AlertDialog implements OnClickListener {

    private OnClickListener listener;

    public ShareDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lParams = getWindow().getAttributes();
        lParams.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        lParams.width = LayoutParams.MATCH_PARENT;
        lParams.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lParams);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_share_to_platform, null);

        View sinaBtn = view.findViewById(R.id.share_to_sina);
        View weixinBtn = view.findViewById(R.id.share_to_weixin);
        View frientBtn = view.findViewById(R.id.share_to_friend);
        View zoneBtn = view.findViewById(R.id.share_to_zone);
        View qqBtn = view.findViewById(R.id.share_to_qq);
        View copyBtn = view.findViewById(R.id.share_to_copy);
        View cancel = view.findViewById(R.id.cancel);

        sinaBtn.setOnClickListener(this);
        weixinBtn.setOnClickListener(this);
        frientBtn.setOnClickListener(this);
        zoneBtn.setOnClickListener(this);
        qqBtn.setOnClickListener(this);
        copyBtn.setOnClickListener(this);
        cancel.setOnClickListener(this);

        ViewGroup.LayoutParams clp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view, clp);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public static ShareDialog show(Context context) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        // 修改系统menu菜单不能全屏显示问题
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = (int) display.getWidth();
        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.share_to_sina: {
            listener.onShare(Extra.SINA);
            break;
        }
        case R.id.share_to_weixin: {
            listener.onShare(Extra.WX_SESSION);
            break;
        }
        case R.id.share_to_friend: {
            listener.onShare(Extra.WX_TIMELINE);
            break;
        }
        case R.id.share_to_zone: {
            listener.onShare(Extra.QQ_QZONE);
            break;
        }
        case R.id.share_to_qq: {
            listener.onShare(Extra.QQ_FRIEND);
            break;
        }
        case R.id.share_to_copy: {
            listener.onShare(null);
            break;
        }
        case R.id.cancel: {
            listener.onCancel();
            break;
        }
        default:
            break;
        }
    }

    public interface OnClickListener {
        void onShare(Extra extra);

        void onCancel();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
