package com.feibo.snacks.manager;

import android.content.Context;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.view.util.RemindControl;

import fbcore.utils.Utils;

/**
 * 加载实体的帮助类
 * Created by lidiqing on 15-8-28.
 */
public abstract class AbsBeanHelper extends AbsLoadHelper {

    private boolean isLoading;
    private Context context;

    public AbsBeanHelper(BaseDataType type) {
        super(type);
        this.context = AppContext.getContext();
        this.isLoading = false;
    }

    /**
     * 加载实体数据，线程安全
     *
     * @param needCache
     * @param listener
     */
    public synchronized void loadBeanData(final boolean needCache, final HelperListener listener) {
        // 检查网络
        if (context != null && !Utils.isNetworkAvailable(context)) {
            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.not_network));
            if (listener != null) {
                listener.onFail(NetResult.NOT_NETWORK_STRING);
                return;
            }
        }

        // 正在加载，退出
        if (isLoading) {
            return;
        }
        isLoading = true;

        // 加载数据的实现
        loadData(needCache, getParams(), new DaoListener() {
            @Override
            public void result(Response response) {
                isLoading = false;

                if (response == null) {
                    listener.onFail(NetResult.NOT_DATA.responseMsg);
                    return;
                }

                if (response.code.equals(NetResult.SUCCESS_CODE)) {
                    setData(response.data);
                    listener.onSuccess();
                    return;
                }

                listener.onFail(response.msg);
            }
        });
    }
}
