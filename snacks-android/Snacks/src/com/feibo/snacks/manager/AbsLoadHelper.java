package com.feibo.snacks.manager;

import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;

/**
 * 加载数据的帮助类
 * Created by lidiqing on 15-8-31.
 */
public abstract class AbsLoadHelper implements ILoadHelper{
    private BaseDataType type;
    private Object params;

    public AbsLoadHelper(BaseDataType type) {
        this.type = type;
    }

    public AbsLoadHelper(){

    }

    /**
     * 获取数据
     * @return
     */
    public Object getData(){
        return DataPool.getInstance().getData(type);
    }

    /**
     * 设置新数据
     * @param data
     */
    public void setData(Object data){
        clearData();
        putData(data);
    }

    /**
     * 追加数据
     * @param data
     */
    public void putData(Object data){
        DataPool.DataBox dataBox = new DataPool.DataBox(type, data);
        DataPool.getInstance().putData(dataBox);
    }

    /**
     * 清除数据
     */
    public void clearData(){
        DataPool.getInstance().removeData(type);
    }

    // 设置参数
    public void setParams(Object params){
        this.params = params;
    }

    // 获取参数
    public Object getParams(){
        return params;
    }

    /**
     * 加载数据
     * @param needCache
     * @param listener
     */
    public abstract void loadData(boolean needCache, Object paramsDao, DaoListener listener);


    public HelperListener generateLoadingListener(final ILoadingListener listener) {
        return new HelperListener() {
            @Override
            public void onSuccess() {
                if (listener == null) {
                    return;
                }
                listener.onSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                if (listener == null) {
                    return;
                }
                listener.onFail(failMsg);
            }
        };
    }

    public HelperListener generateExtraHelperListener(final IRequestListener iExtarHelperListener, final ILoadingListener listener) {
        return new HelperListener() {
            @Override
            public void onSuccess() {
                if (iExtarHelperListener != null) {
                    iExtarHelperListener.onSuccess();
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (iExtarHelperListener != null) {
                    iExtarHelperListener.onFail(failMsg);
                }
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        };
    }

    public static interface HelperListener {
        void onSuccess();

        void onFail(String failMsg);
    }
}
