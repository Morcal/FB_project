package com.feibo.snacks.view.util;
import android.view.View;
import android.widget.TextView;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.module.home.NewProductAdapter;

public class NCountDownTimerUtil extends MyCountDownTimer {

    private TextView textView;
    private NewProductAdapter.ISellingEndListener listener;

    /**
     *
     *      表示以毫秒为单位 倒计时的总数
     *
     *      例如 millisInFuture=1000 表示1秒 
     *
     *      表示 间隔 多少微秒 调用一次 onTick 方法
     *
     *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick() 
     *
     */
    public NCountDownTimerUtil() {
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void setmMillisInFuture(long mMillisInFuture) {
        super.setmMillisInFuture(mMillisInFuture);
    }

    @Override
    public void setmCountdownInterval(long mCountdownInterval) {
        super.setmCountdownInterval(mCountdownInterval);
    }

    @Override
    public void onFinish() {
        if(listener != null) {
            listener.onEnd();
        }
        if (textView != null && textView.getVisibility() != View.INVISIBLE && textView.getVisibility() != View.GONE) {
            textView.getCompoundDrawables()[0].setVisible(false, true);
            textView.setText(GoodsHelper.END_HOT);
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String time = TimeUtil.transformTime(millisUntilFinished / 1000);
        if ("00:00:00".equals(time) || GoodsHelper.END_HOT.equals(time)) {
            onFinish();
            return;
        }
        textView.setText(time);
    }

    public void setListener(NewProductAdapter.ISellingEndListener listener) {
        this.listener = listener;
    }
}