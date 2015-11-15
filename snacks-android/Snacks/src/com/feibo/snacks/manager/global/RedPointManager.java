package com.feibo.snacks.manager.global;

import android.widget.TextView;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.view.util.UIUtil;

import java.util.Observable;
import java.util.Observer;

import fbcore.log.LogUtil;

public class RedPointManager extends Observable{
    private static final String TAG = RedPointManager.class.getSimpleName();
    private static RedPointManager redPointManager;

    public synchronized static RedPointManager getInstance() {
        if (redPointManager == null) {
            redPointManager = new RedPointManager();
        }
        return redPointManager;
    }

    private AbsBeanHelper beanHelper;

    private RedPointManager() {
        beanHelper = new AbsBeanHelper(BaseDataType.AppDataType.RED_POINT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getRedPointAll(listener);
            }
        };
    }

    public void loadRedPoint() {
        beanHelper.loadBeanData(true, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                setChanged();
                notifyObservers((RedPointInfo)beanHelper.getData());
            }

            @Override
            public void onFail(String failMsg) {

            }
        });
    }

    // 工具类，为TextView设置红点数据
    public void setRedNumberView(final TextView carNumber) {
        if(carNumber == null){
            return;
        }
        RedPointInfo info = getRedPointInfo();
        LogUtil.i(TAG, "car num:" + info.cartNum);
        if (info == null || 0 == info.cartNum) {
            UIUtil.setViewGone(carNumber);
        } else {
            UIUtil.setViewVisible(carNumber);
            carNumber.setText(String.valueOf(info.cartNum));
            carNumber.invalidate();
        }
    }

    public void resetRedPoint() {
        beanHelper.clearData();
    }

    public RedPointInfo getRedPointInfo() {
        RedPointInfo info = (RedPointInfo) beanHelper.getData();
        if (info == null) {
            info = new RedPointInfo();
        }
        return info;
    }

    public abstract static class RedPointObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            updateRedPoint((RedPointInfo) data);
        }

        public abstract void updateRedPoint(RedPointInfo info);
    }
}
