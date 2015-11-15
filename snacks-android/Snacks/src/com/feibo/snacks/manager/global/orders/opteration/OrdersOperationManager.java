package com.feibo.snacks.manager.global.orders.opteration;

import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.BaseComment;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/21.
 */
public class OrdersOperationManager {

    private static OrdersOptHelper affirmHelper = new OrdersOptHelper() {
        @Override
        protected void ordersOpt(String ordersId, DaoListener listener) {
            SnacksDao.confirmOrder(ordersId, listener);
        }
    };

    private static OrdersOptHelper deleteHelper = new OrdersOptHelper() {
        @Override
        protected void ordersOpt(String ordersId, DaoListener listener) {
            SnacksDao.deleteOrder(ordersId, listener);
        }
    };

    private static OrdersOptHelper cancelHelper = new OrdersOptHelper() {
        @Override
        protected void ordersOpt(String ordersId, DaoListener listener) {
            SnacksDao.cancelOrder(ordersId, listener);
        }
    };

    private static OrdersmCommentOptHelper commentOptHelper = new OrdersmCommentOptHelper();


    public static void cancelOrders(String ordersId, ILoadingListener listener) {
        cancelHelper.setParams(ordersId);
        cancelHelper.submitData(cancelHelper.generateLoadingListener(listener));
    }

    public static void deleteOrders(String ordersId, ILoadingListener listener) {
        deleteHelper.setParams(ordersId);
        deleteHelper.submitData(cancelHelper.generateLoadingListener(listener));
    }

    public static void affirmOrders(String ordersId, ILoadingListener listener) {
        affirmHelper.setParams(ordersId);
        affirmHelper.submitData(cancelHelper.generateLoadingListener(listener));
    }


    public static void sendOrdersComment(boolean noUseName, List<CartItem> list, ILoadingListener listener) {
        List<BaseComment> baseComments = new ArrayList<BaseComment>();
        for (CartItem item : list) {
            BaseComment comment = new BaseComment();
            comment.order_sn = item.orderSn;
            comment.content = item.commentContent;
            baseComments.add(comment);
        }
        commentOptHelper.setParams(noUseName, baseComments);
        commentOptHelper.submitData(commentOptHelper.generateLoadingListener(listener));
    }

    private static abstract class OrdersOptHelper extends AbsSubmitHelper {

        private String ordersId;
        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            ordersOpt(ordersId, listener);
        }

        public void setParams(String ordersId) {
            this.ordersId = ordersId;
        }

        protected abstract void ordersOpt(String ordersId, DaoListener listener);
    }

    private static class OrdersmCommentOptHelper extends AbsSubmitHelper {

        private boolean noUseName;
        private List<BaseComment> list;

        @Override
        public void loadData(boolean needCache, Object params, DaoListener listener) {
            int opt = noUseName == true ? 1 : 0;
            SnacksDao.getSendComment(opt, OrdersmCommentOptHelper.this.list, listener);
        }

        public void setParams(boolean noUseName, List<BaseComment> list) {
            this.list = list;
            this.noUseName = noUseName;
        }
    }
}
