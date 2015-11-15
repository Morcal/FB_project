package com.feibo.snacks.manager.global;

import android.text.TextUtils;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;

/**
 * 获取服务联系方式
 */
public class ContactManager {

    private static ContactManager contactManager;

    public static ContactManager getInstance() {
        if (contactManager == null) {
            contactManager = new ContactManager();
        }
        return contactManager;
    }

    private AbsBeanHelper loadingHelper;
    private ServiceContact contact;

    private ContactManager() {
        loadingHelper = new AbsBeanHelper(BaseDataType.AppDataType.CONTACT) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getServiceContact(listener);
            }
        };
        initContact();
    }

    private void initContact() {
        if (contact == null) {
            contact = new ServiceContact();
        }
        if (TextUtils.isEmpty(contact.phone)) {
            contact.phone = AppContext.getContext().getString(R.string.snacks_official_service_phone);
        }
        if (TextUtils.isEmpty(contact.qq)) {
            contact.qq = AppContext.getContext().getString(R.string.snacks_official_service_qq);
        }
    }

    public void loadServiceContact(final IResultListener listener) {
        loadingHelper.loadBeanData(true, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                contact = (ServiceContact) loadingHelper.getData();
                initContact();
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    public ServiceContact getServiceContact() {
        return contact;
    }

    public static interface IResultListener  {
        void onResult(boolean ifSuccess, String failMsg);
    }
}
