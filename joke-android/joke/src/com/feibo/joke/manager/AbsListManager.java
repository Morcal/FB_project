package com.feibo.joke.manager;

import java.util.ArrayList;
import java.util.List;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.data.BaseListData;

/**
 * 抽象列表管理类
 *
 * @author Lidiqing
 */
public abstract class AbsListManager<T> extends BaseManager implements IListManager<T> {

    private static final int STATE_RESET = 0;
    private static final int STATE_REFRESHING = 1;
    private static final int STATE_LOADING = 2;

    private List<T> items;
    private List<T> loadMoreItems;
    private int currentState;

    private boolean hasInit;

    private boolean loadToHead;
    private T lastItem;

    public AbsListManager() {
        this(false);
    }

    /**
     * @param loadToHead 刷新是加载数据到头部
     */
    public AbsListManager(boolean loadToHead) {
        this.loadToHead = loadToHead;
        items = new ArrayList<T>();
        loadMoreItems = new ArrayList<T>();
        switchState(STATE_RESET);
        hasInit = false;
    }

    @Override
    public void refresh(final LoadListener listener) {
        if (currentState != STATE_RESET) {
            handleFail(listener, ReturnCode.RS_GESTURE_CANCEL);
            return;
        }

        switchState(STATE_REFRESHING);
        refresh(new IEntityListener<BaseListData<T>>() {
            @Override
            public void result(Response<BaseListData<T>> entity) {

                if (entity == null) {
                    // Dao层返回数据为空，本身也没有数据，返回本地数据空
                    handleFail(listener, ReturnCode.RS_EMPTY_ERROR);
                    switchState(STATE_RESET);
                    return;
                }

                if (entity.rsCode != ReturnCode.RS_SUCCESS) {
                    if (entity.rsCode == ReturnCode.RS_EMPTY_ERROR && !loadToHead) {
                        if (items != null) {
                            items.clear();
                        }
                    }
                    if (items != null && items.size() > 0) {
                        // 本身有数据，返回成功，本身数据不变
                        handleSuccess(listener);
                    } else {
                        handleFail(listener, entity.rsCode);
                    }
                    switchState(STATE_RESET);
                    return;
                }

                BaseListData<T> data = entity.data;
                List<T> list = data.items;

                if (list.size() > 0) {
                    if (loadToHead) {
                        lastItem = list.get(list.size() - 1);
                        list.addAll(items);
                        items = list;
                    } else {
                        items.clear();
                        items.addAll(list);
                    }
                    hasInit = true;
                    handleSuccess(listener);
                    switchState(STATE_RESET);
                    return;
                }

                if (items.size() == 0) {
                    // Dao层返回数据为空，本身也没有数据，返回本地数据空
                    handleFail(listener, ReturnCode.RS_EMPTY_ERROR);
                    switchState(STATE_RESET);
                }

            }

        });
    }

    @Override
    public void loadMore(final LoadListener listener) {
        if (!hasInit || currentState != STATE_RESET) {
            handleFail(listener, ReturnCode.RS_GESTURE_CANCEL);
            return;
        }

        switchState(STATE_LOADING);
        loadMoreItems.clear();
        loadMore(new IEntityListener<BaseListData<T>>() {

            @Override
            public void result(Response<BaseListData<T>> entity) {
                if (entity == null) {
                    // Dao层返回数据为空，本身也没有数据，返回本地数据空
                    handleFail(listener, ReturnCode.RS_LOCAL_NO_MORE_DATA);
                    switchState(STATE_RESET);
                    return;
                }

                if (entity.rsCode != ReturnCode.RS_SUCCESS) {
                    // 直接把服务器的错误类型抛出
                    handleFail(listener, entity.rsCode);
                    switchState(STATE_RESET);
                    return;
                }

                BaseListData<T> data = entity.data;
                List<T> list = data.items;
                loadMoreItems = list;
                if (list.size() == 0) {
                    handleFail(listener, ReturnCode.RS_LOCAL_NO_MORE_DATA);
                } else {
                    if (loadToHead) {
                        lastItem = list.get(list.size() - 1);
                    }
                    items.addAll(list);
                    handleSuccess(listener);
                }
                switchState(STATE_RESET);
            }

        });
    }

    @Override
    public void refreshLocal(int position, T e) {
        if (items == null || items.size() == 0) {
            return;
        }
        items.remove(position);
        if (e != null) {
            items.add(position, e);
        }
    }

    public T getLastItem() {
        return lastItem;
    }

    private void switchState(int state) {
        currentState = state;
    }

    @Override
    public boolean hasData() {
        return items.size() > 0;
    }

    @Override
    public List<T> getDatas() {
        return items;
    }

    @Override
    public List<T> getLoadMoreDatas() {
        return loadMoreItems;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        items.clear();
//        items = null;
    }

    protected abstract void refresh(IEntityListener<BaseListData<T>> listener);

    protected abstract void loadMore(IEntityListener<BaseListData<T>> listener);
}
