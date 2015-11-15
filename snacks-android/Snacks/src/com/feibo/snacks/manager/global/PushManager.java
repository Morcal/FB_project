package com.feibo.snacks.manager.global;

import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;

/**
 * 推送管理
 * Created by lidiqing on 15-8-27.
 */
public class PushManager {

    private static PushManager sPushManager;

    public static PushManager getInstance() {
        if (sPushManager == null) {
            synchronized (PushManager.class) {
                if (sPushManager == null) {
                    sPushManager = new PushManager();
                }
            }
        }
        return sPushManager;
    }

    private AbsSubmitHelper pushHelper;

    private String clientId;

    private PushManager() {
        pushHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.pushCode(clientId,listener);
            }
        };
    }

    public void pushCode(String clientId) {
        this.clientId = clientId;
       pushHelper.submitData(pushHelper.generateLoadingListener(null));
    }
}
