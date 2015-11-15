package com.feibo.joke.view.dialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform.Extra;

@SuppressLint("InflateParams")
public class ShareDialog extends BaseDialog implements OnClickListener {

    private OnShareClickListener listener;
    private String copyText;
    public ShareScene scene;


    public ShareDialog(Context context) {
        super(context);
    }

    @Override
    public View initContentView(LayoutInflater inflater) {
        int layout = scene == ShareScene.SHARE_ONLY ? R.layout.dialog_share_only_layout : R.layout.dialog_share_layout;
        View view = inflater.inflate(layout, null);
        TextView deleteReport = (TextView) view.findViewById(R.id.bt_delete_report);

        view.findViewById(R.id.bt_qq).setOnClickListener(this);
        view.findViewById(R.id.bt_sina).setOnClickListener(this);
        view.findViewById(R.id.bt_wx).setOnClickListener(this);
        view.findViewById(R.id.bt_wx_friend).setOnClickListener(this);
        view.findViewById(R.id.bt_qzone).setOnClickListener(this);
        view.findViewById(R.id.bt_copy).setOnClickListener(this);

        if(scene != ShareScene.SHARE_ONLY) {
            deleteReport.setOnClickListener(this);
        }

        if (scene == ShareScene.VIDEO_DELETE) {
            deleteReport.setVisibility(View.VISIBLE);
            deleteReport.setText("删除");

            Drawable drawable = getContext().getResources().getDrawable(R.drawable.btn_dialog_delete);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            deleteReport.setCompoundDrawables(null, drawable, null, null);
        } else if (scene == ShareScene.VIDEO_REPORT) {
            deleteReport.setVisibility(View.VISIBLE);
            deleteReport.setText("举报");

            Drawable drawable = getContext().getResources().getDrawable(R.drawable.btn_dialog_report);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            deleteReport.setCompoundDrawables(null, drawable, null, null);
        }

//        view.findViewById(R.id.bt_cancel).setOnClickListener(this);
        return view;
    }

    public static ShareDialog show(Context context, ShareScene scene, OnShareClickListener listener, String copyText) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.scene = scene;
        dialog.copyText = copyText;
        dialog.show();


        dialog.listener = listener;
        return dialog;
    }

    public void setScene(ShareScene scene) {
        this.scene = scene;
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        this.dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.bt_delete_report:
                if(scene == ShareScene.VIDEO_REPORT) {
                    listener.onReport();
                } else {
                    listener.onDelete();
                }
                break;
            case R.id.bt_qq:
                listener.onShare(Extra.QQ_FRIEND);
                break;
            case R.id.bt_qzone:
                listener.onShare(Extra.QQ_QZONE);
                break;
            case R.id.bt_wx_friend:
                listener.onShare(Extra.WX_TIMELINE);
                break;
            case R.id.bt_sina:
                listener.onShare(Extra.SINA);
                break;
            case R.id.bt_wx:
                listener.onShare(Extra.WX_SESSION);
                break;
            case R.id.bt_copy:
                ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData textCd = ClipData.newPlainText("url", copyText);
                clip.setPrimaryClip(textCd);
                ToastUtil.showSimpleToast("已复制到剪切板");
                break;
            default:
                break;
        }
    }

    public enum ShareScene {
        VIDEO_DELETE,  //分享视频
        VIDEO_REPORT,  //分享视频
        SHARE_ONLY,    //单纯分享
    }

    public interface OnShareClickListener {
        void onShare(Extra extra);

        void onDelete();

        void onReport();
    }

    public void setOnClickListener(OnShareClickListener listener) {
        this.listener = listener;
    }
}
