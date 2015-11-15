package com.feibo.snacks.manager.module.person;

import com.feibo.snacks.manager.global.AddressManager;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingView;

import java.util.List;

/**
 * Created by hcy on 2015/7/2.
 */
public class AddressListPresenter extends AbsLoadingPresenter {
    private static final String TAG = AddressListPresenter.class.getSimpleName();

    private AddressManager addressManager;

    public AddressListPresenter(ILoadingView loadingView) {
        super(loadingView);
        addressManager = AddressManager.getInstance();
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        if(type == LoadType.LOAD_FIRST){
            addressManager.refresh(listener);
        }
    }

    @Override
    public boolean hasMore() {
        return addressManager.hasMoreAddress();
    }

    @Override
    public BaseDataType getDataType() {
        return addressManager.getListDataType();
    }

    @Override
    public BaseDataType getMoreDataType() {
        return null;
    }

    public List<Address> getAddressList(){
        return addressManager.getAddressList();
    }

    public Address getAddress(int i){
        return addressManager.getAddress(i);
    }

    public void setAddressDefault(long addressDefault, ILoadingListener listener){
        addressManager.setAddressDefault(addressDefault, listener);
    }

    public boolean isDefaultAddress(Address address, int index){
        return addressManager.isDefaultAddress(address, index);
    }

    public void deleteAddress(int index, ILoadingListener listener){
        addressManager.deleteAddress(index, listener);
    }
}
