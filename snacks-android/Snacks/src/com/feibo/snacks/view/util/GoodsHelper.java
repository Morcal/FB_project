package com.feibo.snacks.view.util;

import android.widget.TextView;

import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.module.home.NewProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class GoodsHelper {

    public static final String END_HOT= "已结束";
    private static List<NCountDownTimerUtil> countDownTimerUtils = new ArrayList<NCountDownTimerUtil>();

    public static void showRestTime(final TextView timeTextView, final long endTime, NewProductAdapter.ISellingEndListener listener) {
    	NCountDownTimerUtil util = null;
    	if ((NCountDownTimerUtil)timeTextView.getTag() == null) {
    		util = new NCountDownTimerUtil();
			timeTextView.setTag(util);
			countDownTimerUtils.add(util);
		}else {
			util = (NCountDownTimerUtil) timeTextView.getTag();
		}
        util.setListener(listener);
    	util.setTextView(timeTextView);
    	util.setmMillisInFuture(Long.parseLong(TimeUtil.getTime(endTime)) * 1000);
    	util.setmCountdownInterval(1050);
    	util.start();
    }
    
    public static void cancelCountDownTime() {
    	for(NCountDownTimerUtil util : countDownTimerUtils) {
    		util.cancel();
    		util = null;
    	}
    }
}
