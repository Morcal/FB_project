package com.feibo.snacks.manager;

import android.content.Context;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.view.util.RemindControl;

import fbcore.log.LogUtil;
import fbcore.utils.Utils;

/**
 * 提交数据帮助类
 * Created by lidiqing on 15-8-28.
 */
public abstract class AbsSubmitHelper extends AbsLoadHelper{
    private static final String TAG = AbsSubmitHelper.class.getSimpleName();
    private boolean isLoading;
    private boolean isSaveData;
    private Context context;

    /**
     * 无参构造函数，提交数据后，只需关注response的code
     */
    public AbsSubmitHelper() {
        super();
        this.context = AppContext.getContext();
        this.isLoading = false;
        this.isSaveData = false;
    }

    /**
     * 包含数据类型，提交数据后，response的data将会被保存使用
     * @param type
     */
    public AbsSubmitHelper(BaseDataType type){
        super(type);
        this.context = AppContext.getContext();
        this.isLoading = false;
        this.isSaveData = true;
    }

    /**
     * 提交数据，线程安全
     *
     * @param listener
     */
    public synchronized void submitData(final HelperListener listener) {
        // 检查网络
        if (context != null && !Utils.isNetworkAvailable(context)) {
            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.not_network));
            if (listener != null) {
                listener.onFail(NetResult.NOT_NETWORK_STRING);
                return;
            }
        }

        // 防止反复提交
        if (isLoading) {
            return;
        }
        isLoading = true;

        // 提交的实现
        loadData(false, getParams(), new DaoListener() {
            @Override
            public void result(Response response) {
                isLoading = false;
                if (response == null) {
                    listener.onFail(NetResult.NOT_DATA.responseMsg);
                    return;
                }
                LogUtil.i(TAG, "response code:" + response.code);

                if (response.code.equals(NetResult.SUCCESS_CODE)) {
                    // 如果返回的data数据有必要保存
                    if(isSaveData){
                        setData(response.data);
                    }
                    listener.onSuccess();
                    return;
                }

                listener.onFail(response.msg);
            }
        });
    }
}
