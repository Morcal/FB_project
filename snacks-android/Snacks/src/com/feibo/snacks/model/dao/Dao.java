package com.feibo.snacks.model.dao;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.List;

import fbcore.conn.http.HttpParams;
import fbcore.conn.http.Method;
import fbcore.log.LogUtil;
import fbcore.task.AsyncTaskManager;
import fbcore.task.SyncTask;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;
import fbcore.task.toolbox.GetStringTask;
import fbcore.task.toolbox.GetTask;
import fbcore.task.toolbox.PostTask;

/**
 * 为Dao层的数据读取, 加载图片等方法加入异步框架, 结果会抛出到主线程
 * 目前加入fbcore库的AsyncTaskManager类实现
 * 后面可考虑替换成其他的异步框架或线程池来管理
 * @param <T>
 */
public class Dao<T> {

    private static final String TAG = "DAO";

    /**
     * 向指定地址传输数据
     */
    public static <T> void putDatas(final String url, List<HttpParams.NameValue> params, final TypeToken<Response<T>> token,
                                     final DaoListener<T> listener) {
        // 检查网络状况，无网络返回缓存，无缓存返回空
        if (!AppContext.isNetworkAvailable()) {
            Response response = new Gson().fromJson(createNotNetJson(), token.getType());
            listener.result(response);
            return;
        }
        Dao.putAsyncDatas(url, params, new TaskHandler() {
            @Override
            public void onSuccess(Object result) {
                if (listener == null) {
                    return;
                }
                if (result != null) {
                    try {
                        Response<T> o;
                        try {
                            o = new Gson().fromJson((String) result, token.getType());
                        } catch (Exception e) {
                            try {
                                result = new String((byte[]) result, "UTF-8");
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            }
                            o = new Gson().fromJson((String) result, token.getType());
                        }
                        listener.result(o);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Response<T> response = new Gson().fromJson(createNotNetJson(), token.getType());
                listener.result(response);
            }
            @Override
            public void onProgressUpdated(Object... params) {

            }

            @Override
            public void onFail(TaskFailure failure) {
                Response<T> response = new Gson().fromJson(createNotNetJson(), token.getType());
                listener.result(response);
            }
        });
    }

    private static void putAsyncDatas(final String url,  final List<HttpParams.NameValue> params, TaskHandler handler) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
                if (params == null || params.size() == 0) {
                    return new GetTask(url);
                } else {
                    HttpParams.Builder buidler = new HttpParams.Builder(Method.POST, url);
                    for (HttpParams.NameValue nv : params) {
                        buidler.addNameValue(nv.getName(), nv.getValue());
                    }
                    return new PostTask(buidler.create()).execute();
                }
            }
        }, handler);
    }

    /**
     * 获取实体类，异步
     *
     * @param paramUrl
     * @param token
     * @param listener
     * @param cache
     * @param <T>
     */
    public static <T> void getEntity(final String paramUrl, final TypeToken<Response<T>> token,
                                     final DaoListener<T> listener, final boolean cache) {
        // 检查网络状况，无网络返回缓存，无缓存返回空
        if (!AppContext.isNetworkAvailable()) {
            if (cache) {
                getDataFromDisk(paramUrl, token, listener);
            } else {
                Response response = new Gson().fromJson(createNotNetJson(), token.getType());
                listener.result(response);
            }
            return;
        }

        // 网络读数据，加入超时判断
        Dao.getAsyncString(paramUrl, new TaskTimerHandler() {

            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);

                // 返回null，主机不稳定，服务器错误等原因会导致该问题
                if (result == null) {
                    if (cache) {
                        getDataFromDisk(paramUrl, token, listener);
                    } else {
                        Response response = new Gson().fromJson(createNotNetJson(), token.getType());
                        listener.result(response);
                    }
                    return;
                }

                // 有数据
                LogUtil.i(TAG, "result:" + result + "");

                Response<T> response = null;
                try {
                    response = new Gson().fromJson((String) result, token.getType());
                    listener.result(response);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "接口解析数据失败");
                    response = new Gson().fromJson(createNotParseJson(), token.getType());
                    listener.result(response);
                    return;
                }


                if (response != null) {
                    if (response.code.equals(NetResult.SUCCESS_CODE) && cache) {
                        // 保存到本地缓存中
                        DataDiskProvider.getInstance().putStringToDisker((String) result,
                                UrlFilter.getFileNameFromUrl(paramUrl));
                    }
                }
            }

            @Override
            public void onProgressUpdated(Object... params) {

            }

            @Override
            public void onFail(TaskFailure failure) {
                super.onFail(failure);
                // 网络和缓存均没有数据
                Response<T> response = new Gson().fromJson(createNotNetJson(), token.getType());
                listener.result(response);
            }
        });
    }

    private static void getAsyncString(final String paramUrl, TaskHandler handler) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
                String url = UrlBuilder.getPublicParamUrl().append(paramUrl).toString();
                LogUtil.i(TAG, "URL=" + url);
                return new GetStringTask(url).execute();
            }
        }, handler);
    }

    private static <T> void getDataFromDisk(final String paramUrl, final TypeToken<Response<T>> token, final DaoListener<T> listener) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
                // 网络无数据，如果需要读缓存，返回缓存
                String cacheJsonStr = DataDiskProvider.getInstance().getCacheFromDisker(UrlFilter.getFileNameFromUrl(paramUrl));

                // 缓存无数据
                if (cacheJsonStr == null) {
                    Response<T> response = new Gson().fromJson(createNotDataJson(), token.getType());
                    return response;
                }

                // 缓存有数据
                Response<T> response = new Gson().fromJson(cacheJsonStr, token.getType());
                return response;
            }

        }, new TaskHandler() {

            @Override
            public void onSuccess(Object result) {
                listener.result((Response<T>) result);
            }

            @Override
            public void onProgressUpdated(Object... params) {
            }

            @Override
            public void onFail(TaskFailure failure) {
            }
        });
    }

    private static String createNotDataJson() {
        return createEmptyJson(NetResult.NOT_DATA.responseCode, NetResult.NOT_DATA.responseMsg);
    }

    private static String createNotNetJson() {
        return createEmptyJson(NetResult.NOT_NET.responseCode, NetResult.NOT_NET.responseMsg);
    }

    private static String createNotParseJson() {
        return createEmptyJson(NetResult.NOT_PARSE.responseCode, NetResult.NOT_PARSE.responseMsg);
    }

    private static String createEmptyJson(String rsCode, String rsMsg) {
        return new Gson().toJson(new Response<Object>(rsCode, rsMsg));
    }
}
