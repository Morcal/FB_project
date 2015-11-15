package com.feibo.snacks.manager.global.orders.opteration;

import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;

import java.util.List;

/**
 * Created by hcy on 2015/7/23.
 */
public class CartOperationManager {

    private static CartOptHelper cartOptHelper = new CartOptHelper();
    public static void updateCartNumber(int cartId, int number, ILoadingListener listener) {
        cartOptHelper.setParams(cartId, number);
        cartOptHelper.submitData(cartOptHelper.generateLoadingListener(listener));
    }

    private static CartDeleteHelper cartDeleteHelper = new CartDeleteHelper();
    public static void deleteCart(List<Long> id, ILoadingListener listener) {
        cartDeleteHelper.setParams(id);
        cartDeleteHelper.submitData(cartDeleteHelper.generateLoadingListener(listener));
    }

    private static  CartBatchOptHelper cartBatchOptHelper = new CartBatchOptHelper();
    public static void addGoods2Cart(String addition, ILoadingListener listener) {
        cartBatchOptHelper.setParams(addition);
        cartBatchOptHelper.submitData(cartBatchOptHelper.generateLoadingListener(listener));
    }

    private static class CartDeleteHelper extends AbsSubmitHelper {
        private List<Long> id;

        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            SnacksDao.delete2Cart(id, listener);
        }

        public void setParams(List<Long> id) {
            this.id = id;
        }
    }

    private static class CartBatchOptHelper extends AbsSubmitHelper {

        private String addition;

        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            SnacksDao.addGoods2Cart(addition, listener);
        }

        public void setParams(String addition) {
            this.addition = addition;
        }
    }

    private static class CartOptHelper extends AbsSubmitHelper {
        private int cartId;
        private int number;

        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            SnacksDao.updateNum2Cart(cartId, number, listener);
        }

        public void setParams(int cartId, int number) {
            this.cartId = cartId;
            this.number = number;
        }
    }
}
