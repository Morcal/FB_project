package com.feibo.snacks.manager.global;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.Status;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.UrlBuilder;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.util.TimeUtil;

import fbcore.security.Md5;

/**
 * Created by hcy on 2015/10/20.
 */
public class Share4GiftConfigManager {

    private String code;
    private AbsBeanHelper postUrlHelper;

    public Share4GiftConfigManager() {
        postUrlHelper = new AbsBeanHelper(BaseDataType.PersonDataType.POST_AUTH_URL) {
            @Override
            public void loadData(boolean needCache, Object paramsDao, DaoListener listener) {
                SnacksDao.postAuthUrl(code, listener);
            }
        };
    }

    public static AbsBeanHelper configHelper = new AbsBeanHelper(BaseDataType.PersonDataType.INVISITED_SEND_GIFT) {
        @Override
        public void loadData(boolean needCache, Object paramsDao, DaoListener listener) {
            SnacksDao.controlShareHaveGift(listener);
        }
    };

    public void postAuthUrl() {
        String tms = TimeUtil.generateTimestamp();
        code = Md5.digest32("xiaomiao" + tms);
        postUrlHelper.loadBeanData(false, postUrlHelper.generateLoadingListener(null));
    }

    public static void loadShare4GiftConfig(ILoadingListener listener) {
        configHelper.loadBeanData(false, configHelper.generateLoadingListener(listener));
    }

    public static boolean showShare4GiftView() {
        Status status = (Status) configHelper.getData();
        return status.status == 1;
    }

    public static String buildShare4GitfUrl() {
        return UrlBuilder.getPublicParamUrl().append("&srv=2921").toString();
    }
}
