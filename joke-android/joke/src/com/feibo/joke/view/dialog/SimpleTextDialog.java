package com.feibo.joke.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;

public class SimpleTextDialog extends BaseDialog {

    private Handler handler;
    private TextView textView;
    private SimpleResoure resoure;
    
    public SimpleTextDialog(Context context, SimpleResoure resoure) {
        super(context);
        handler = new Handler();
        this.resoure = resoure;
    }

    @Override
    public View initContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_simple_text, null);
        textView = (TextView) view.findViewById(R.id.tx);
        initDialog();
        return view;
    }

    private void initDialog() {
        if (resoure == null) {
            return;
        }
        textView.setText(resoure.title);
        Drawable d = this.getContext().getResources().getDrawable(resoure.titleImg);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
    }

    public static SimpleTextDialog show(Context context, SimpleResoure resoure) {
        return show(context, resoure, false);
    }

    public static SimpleTextDialog show(Context context, SimpleResoure resoure, boolean dismissDelay) {
        final SimpleTextDialog dialog = new SimpleTextDialog(context, resoure);
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dialog != null && dialog.handler != null) {
                    dialog.handler.removeCallbacksAndMessages(null);
                }
            }
        });
        dialog.show();

        if (dismissDelay) {
            dialog.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 2000);
        }

        return dialog;
    }

}
