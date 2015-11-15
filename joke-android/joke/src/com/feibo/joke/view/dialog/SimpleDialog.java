package com.feibo.joke.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.feibo.joke.R;

public class SimpleDialog extends Dialog {

    public SimpleDialog(Context context, View view) {
        super(context, R.style.SimpleDialog);
        setContentView(view);
        setCancelable(true);
    }
}