package com.feibo.snacks.manager;

import com.feibo.snacks.model.dao.DaoListener;

/**
 * Created by lidiqing on 15-9-6.
 */
public interface ILoadHelper {
    void loadData(boolean needCache, Object params, DaoListener listener);
}
