package com.feibo.snacks.manager.global.orders.unpaid;

import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.Note;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.PayParams;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataPool;
import com.feibo.snacks.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/22.
 */
public class OrdersConfirmManager extends AbsLoadingPresenter {

    private List<Integer> list;
    private long addressId = -1;
    private long discouponId;
    private ArrayList<CartItem4Type> cartItem4Types;

    private AbsBeanHelper helper;

    public OrdersConfirmManager(List<Integer> list, ILoadingView loadingView) {
        super(loadingView);
        this.list = list;
        helper = new AbsBeanHelper(getDataType()) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.commitCart2OrderUrl(String.valueOf(addressId), Util.createIdString(list), String.valueOf(discouponId), listener);
            }
        };
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        if (type == LoadType.LOAD_FIRST) {
            helper.loadBeanData(false, helper.generateLoadingListener(listener));
        }
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public OrdersDetail getData(BaseDataType type) {
        return (OrdersDetail) DataPool.getInstance().getData(getDataType());
    }

    @Override
    public BaseDataType getDataType() {
        return BaseDataType.OrdersDataType.CONFIRM_ORDERS;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return null;
    }

    public void release() {
        DataPool.getInstance().removeData(getDataType());
    }

    public void setDiscouponId(long discouponId) {
        this.discouponId = discouponId;
    }

    public ArrayList<CartItem4Type> getCart() {
        OrdersDetail detail = getData(getDataType());
        if (detail == null || detail.cartSuppliers == null) {
            return null;
        }
        cartItem4Types = CartItem4Type.getCart(detail.cartSuppliers);
        return cartItem4Types;
    }

    public static PayParams createParams(OrdersDetail confirmInfo, ArrayList<CartItem4Type> list) {
        PayParams payParams = new PayParams();
        payParams.setAddId(confirmInfo.address.id);
        ArrayList<Note> notes = new ArrayList<Note>();
        List<Integer> ordersArr = new ArrayList<Integer>();
        for (CartItem4Type item4Type : list) {
            Integer id = new Integer(item4Type.item.id);
            ordersArr.add(id);
            Note note = new Note();
            note.id = item4Type.suppliers.id;
            note.note = item4Type.note;
            notes.add(note);
        }
        payParams.setNotes(notes);
        payParams.setIds(ordersArr);
        return payParams;
    }

    public void clear() {
        helper.clearData();
    }
}
