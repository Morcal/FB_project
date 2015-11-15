package com.feibo.snacks.view.util;

import android.widget.TextView;

public class TextLoadingHelper implements Runnable {
    private static final String[] LOADING_TEXT = new String[] {"···"};
    private static final int INTERVAL = 500;
    private TextView textView;
    private String perfix;
    private int index = 0;
    private boolean isStop = false;
    private static final String FAIL_TEXT = "家底都被你搬光了，快去发现新大陆~";

    private TextLoadingHelper(TextView textView, String perfix) {
        this.textView = textView;
        this.perfix = perfix;
        run();
    }

    @Override
    public void run() {
        if (isStop) {
            textView = null;
            return;
        }
        textView.setText(perfix + LOADING_TEXT[index++]);
        if (index >= LOADING_TEXT.length) {
            index = 0;
        }
        textView.postDelayed(this, INTERVAL);
    }

    private void stop() {
        isStop = true;
    }

    public static void start(final TextView textView) {
        textView.setTag(new TextLoadingHelper(textView, ""));
    }

    public static void start(final TextView textView, String perfix) {
        textView.setTag(new TextLoadingHelper(textView, perfix));
    }

    public static void end(TextView textView) {
        TextLoadingHelper loading = (TextLoadingHelper) textView.getTag();
        if (loading != null) {
            loading.stop();
        }
        textView.setTag(null);
    }

    public static void fail(TextView textView) {
        Object object = textView.getTag();
        if (object != null && object instanceof TextLoadingHelper) {
            ((TextLoadingHelper) object).stop();
        }
        textView.setTag(null);
        textView.setText(FAIL_TEXT);
        
    }

    public static boolean isLoading(TextView textView) {
        TextLoadingHelper loading = (TextLoadingHelper) textView.getTag();
        if (loading != null) {
            return !loading.isStop;
        }
        return false;
    }
}