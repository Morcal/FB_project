package com.feibo.snacks.view.module.person.orders.util;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.orders.opteration.OrdersOperationManager;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.util.RemindControl;

/**
 * Created by hcy on 2015/7/16.
 */
public class OrdersOptHelper {

    public static void deleteOrders(final String ordersId, Context context, final OnRefreshOrdersLintener listener) {
        if (context == null) {
            return;
        }
        RemindControl.showDeleteOrdersRemind(context, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                OrdersOperationManager.deleteOrders(ordersId, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        listener.onRefresh();
                    }

                    @Override
                    public void onFail(String failMsg) {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void affirmOrders(final String ordersId, Context context, final OnRefreshOrdersLintener listener) {
        if (context == null) {
            return;
        }
        RemindControl.showReiptRemind(context, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                OrdersOperationManager.affirmOrders(ordersId, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        listener.onRefresh();
                    }

                    @Override
                    public void onFail(String failMsg) {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void cancelOrders(final String ordersId, Context context, final OnRefreshOrdersLintener listener) {
        if (context == null) {
            return;
        }
        RemindControl.showCancelOrdersRemind(context, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                OrdersOperationManager.cancelOrders(ordersId, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        listener.onRefresh();
                    }

                    @Override
                    public void onFail(String failMsg) {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void refoundOrder(final Context context, final ServiceContact contact) {
        RemindControl.showServiceRemind(context, contact, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                String phone;
                if(contact != null && !TextUtils.isEmpty(contact.phone)){
                    phone = contact.phone;
                }else{
                    phone = context.getString(R.string.snacks_official_service_phone);
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + phone));
                context.startActivity(intent);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void remindSendOut(Context context, String ordersId) {
        if (context == null) {
            return;
        }
        long time = SPHelper.getOrdersRemindTime(ordersId);
        long currentTime = new Date().getTime();

        if (needRemind(time, currentTime)) {
            SPHelper.setOrdersRemindTime(ordersId, currentTime);
            RemindControl.showSimpleToast(context, R.string.orders_remind_send_orders_success);
        } else {
            RemindControl.showSimpleToast(context, R.string.orders_remind_more_time);
        }
    }

    private static boolean needRemind(long time, long currentTime) {
        if (time == -1) {
            return true;
        }
        long dif = currentTime - time;
        int dur = (int) (dif / (1000 * 60 * 60 * 24));
        return dur < 0;
    }

    public static interface OnRefreshOrdersLintener {
        void onRefresh();
    }
}