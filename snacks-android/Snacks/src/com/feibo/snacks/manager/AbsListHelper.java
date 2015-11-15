package com.feibo.snacks.manager;

import android.content.Context;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.EntityArray;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.view.module.person.orders.pay.PayHelper;
import com.feibo.snacks.view.util.RemindControl;

import java.util.List;

import fbcore.log.LogUtil;
import fbcore.utils.Utils;

/**
 * 加载列表数据的帮助类
 * Created by lidiqing on 15-8-28.
 */
public abstract class AbsListHelper extends AbsLoadHelper{
    private static final String TAG = AbsListHelper.class.getSimpleName();
    private Context context;
    private int curPage;
    private boolean isLoading;
    private boolean isMoreData;

    public AbsListHelper(BaseDataType type){
        super(type);
        this.context = AppContext.getContext();
        this.curPage = 1;
        this.isLoading = false;
        this.isMoreData = true;
    }

    /**
     * 加载更多
     * @param needCache
     * @param listener
     */
    public synchronized void loadMore(final boolean needCache, final HelperListener listener){
        loadListData(needCache, listener);
    }

    /**
     * 刷新列表
     * @param needCache
     * @param listener
     */
    public synchronized void refresh(final boolean needCache, final HelperListener listener){
        resetCurPage();
        loadListData(needCache, listener);
    }

    /**
     * 加载列表数据，线程安全
     *
     * @param needCache
     * @param listener
     */
    private synchronized void loadListData(final boolean needCache, final HelperListener listener) {
        // 检查网络
        if (context != null && !Utils.isNetworkAvailable(context)) {
            if (listener != null) {
                listener.onFail(NetResult.NOT_NETWORK_STRING);
                return;
            }
        }

        // 正在加载，不是第一次加载并且没有更多数据，退出
        final boolean isFirst = curPage == 1;
        if ((!isFirst && !isMoreData) || isLoading) {
            return;
        }
        isLoading = true;

        // 加载数据
        loadData(needCache, getParams(), new DaoListener() {
            @Override
            public void result(Response response) {
                isLoading = false;
                LogUtil.i(TAG, "loadListData code:" + response.code);
                if (response == null) {
                    // TODO 修改错误类型，为主机异常或者服务器错误
                    listener.onFail(NetResult.NOT_DATA.responseMsg);
                    return;
                }

                // 成功获取数据
                if (response.code.equals(NetResult.SUCCESS_CODE)) {
                    Object result = response.data;

                    // 普通列表数据
                    if (result instanceof EntityArray) {
                        List resultList = ((EntityArray) result).items;
                        if (isFirst) {
                            clearData();
                        } else {
                            List<Object> list = resultList;
                            List<Object> oldList = (List<Object>) getData();
                            if (oldList == null || list == null) {
                                return;
                            }
                            oldList.addAll(list);
                            LogUtil.i(TAG, "list size:" + oldList.size());
                            resultList = oldList;
                        }
                        curPage++;
                        putData(resultList);
                        listener.onSuccess();
                        return;
                    }

                    // 品牌团列表数据
                    if (result instanceof BrandDetail) {
                        if (isFirst) {
                            LogUtil.i(TAG, "new data");
                            clearData();
                        } else {
                            LogUtil.i(TAG, "add brand");
                            BrandDetail detailSubject = (BrandDetail) getData();
                            List<Goods> oldList = detailSubject.goodses;
                            List<Goods> newList = ((BrandDetail) result).goodses;
                            if (newList == null || newList.size() == 0) {
                                isMoreData = false;
                                listener.onFail(NetResult.NOT_DATA_STRING);
                                return;
                            }
                            oldList.addAll(newList);
                            result = detailSubject;
                        }

                        curPage++;
                        putData(result);
                        listener.onSuccess();
                        return;
                    }
                }

                // 返回的数据为空
                if (response.code.equals(NetResult.NOT_DATA.responseCode)) {
                    isMoreData = false;
                    listener.onFail(NetResult.NOT_DATA_STRING);
                    return;
                }

                if (response.code.equals("1002")) {
                    isMoreData = false;
                    listener.onFail(NetResult.NOT_DATA_STRING);
                    return;
                }

                // 返回的数据有误
                listener.onFail(response.msg);
            }
        });
    }

    public void resetCurPage() {
        clearData();
        curPage = 1;
        isMoreData = true;
    }

    public int getCurPage(){
        return curPage;
    }

    public boolean hasMoreData() {
        return isMoreData;
    }

    public boolean isLoading(){
        return isLoading;
    }

}
