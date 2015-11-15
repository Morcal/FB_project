package com.feibo.joke.dao;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import fbcore.conn.http.HttpParams;
import fbcore.conn.http.HttpParams.NameValue;
import fbcore.conn.http.HttpResult;
import fbcore.conn.http.Method;
import fbcore.conn.http.client.ApacheHttpClient;
import fbcore.log.LogUtil;
import fbcore.task.AsyncTaskManager;
import fbcore.task.SyncTask;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;
import fbcore.task.toolbox.GetStringTask;
import fbcore.utils.Files;
import fbcore.utils.IOUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.model.Response;
import com.feibo.joke.utils.PostUtil;
import com.feibo.joke.view.util.ToastUtil;

public class Dao<T> {
    /**
     * 异步获取Json字符串
     * 
     * @param url
     * @param handler
     */
    private static void getAsyncString(final String paramUrl, TaskHandler handler) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {
            	String url = UrlBuilder.getPublicParamUrl().append(paramUrl).toString();
                LogUtil.i("URL================", "URL=" + url);
                return new GetStringTask(url).execute();
            }
        }, handler);
    }

    /**
     * 异步传输数据
     * 
     * @param url
     * @param str
     */
    private static void putAsyncDatas(final String paramUrl, final List<NameValue> params, TaskHandler handler) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {
            @Override
            protected Object execute() {

                String url = UrlBuilder.getPublicParamUrl().append(paramUrl).toString();
                LogUtil.i("URL================", url);

                Map<String, String> mapParams = new HashMap<String, String>();
                for (NameValue param : params) {
                    mapParams.put(param.getName(), param.getValue().toString());
                }

                try {
                    String result = PostUtil.post(url, mapParams);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, handler);
    }
    
    public static <T> void getEntity(final String paramUrl, final TypeToken<Response<T>> token,
            final IEntityListener<T> listener, final boolean cache) {
        getEntity(paramUrl, token, listener, cache, false);
    }

    public static <T> void getEntity(final String paramUrl, final TypeToken<Response<T>> token,
            final IEntityListener<T> listener, final boolean cache, boolean showToast) {
        if (!checkNetwork(paramUrl, token, cache, listener, showToast)) {
            return;
        }
        Dao.getAsyncString(paramUrl, new TaskHandler() {

            @Override
            public void onSuccess(Object result) {
                // 网络有数据，返回网络数据，后更新缓存
                if (result != null) {
                    LogUtil.i("result================", result + "");
                    Response<T> o = null;
                    try {
                        o = new Gson().fromJson((String) result, token.getType());
                        listener.result(o);
                        rscode2Toast(o);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        LogUtil.e("", "接口解析数据失败");
                        return;
                    }
                    if (o.rsCode != ReturnCode.RS_EMPTY_ERROR) {
                        DataProvider.getInstance().putStringToDisker((String) result,
                                UrlFilter.getFileNameFromUrl(paramUrl));
                    }
                    return;
                }

                getDataFromCache(false, paramUrl, token, listener, cache);
            }

            @Override
            public void onProgressUpdated(Object... params) {

            }

            @Override
            public void onFail(TaskFailure failure) {
                // 网络和缓存均没有数据
                Response<T> o = new Gson().fromJson(createEmptyJson(), token.getType());
                listener.result(o);
                rscode2Toast(o);
            }
        });
    }

    private static <T> boolean checkNetwork(final String paramUrl, final TypeToken<Response<T>> token,
            boolean needCache, IEntityListener<T> listener, boolean showToast) {
        if (!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.not_network));

            getDataFromCache(true, paramUrl, token, listener, needCache);
            return false;
        }
        return true;
    }

    public static <T> void putDatas(final String paramUrl, List<NameValue> params, final TypeToken<Response<T>> token,
            final IEntityListener<T> listener) {
        putDatas(paramUrl, params, token, listener, false);
    }

    /**
     * 向指定地址传输数据
     * 
     * @param url
     * @param params
     * @param cls
     * @param listener
     */
    public static <T> void putDatas(final String paramUrl, List<NameValue> params, final TypeToken<Response<T>> token,
            final IEntityListener<T> listener, boolean showToast) {
        if (!checkNetwork(paramUrl, token, false, listener, showToast)) {
            return;
        }
        Dao.putAsyncDatas(paramUrl, params, new TaskHandler() {

            @Override
            public void onSuccess(Object result) {
                if (result != null) {
                    try {
                        Response<T> o = new Gson().fromJson((String) result, token.getType());
                        listener.result(o);
                        rscode2Toast(o);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                String error = createEmptyJson(ReturnCode.RS_POST_ERROR);

                Response<T> o = new Gson().fromJson(error, token.getType());
                listener.result(o);
            }

            @Override
            public void onProgressUpdated(Object... params) {

            }

            @Override
            public void onFail(TaskFailure failure) {
                String result = createEmptyJson(ReturnCode.RS_POST_ERROR);
                Response<T> o = new Gson().fromJson(result, token.getType());
                listener.result(o);
                rscode2Toast(o);
            }
        });
    }

    private static String createEmptyJson() {
        return createEmptyJson(ReturnCode.RS_EMPTY_ERROR);
    }

    private static String createEmptyJson(int rsCode) {
        return new Gson().toJson(new Response<Object>(rsCode));
    }

    private static <T> void getDataFromCache(final boolean noNet, final String paramUrl, final TypeToken<Response<T>> token,
            final IEntityListener<T> listener, final boolean cache) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {

            @Override
            protected Object execute() {
             // 网络无数据，如果需要读缓存，返回缓存
                if (cache) {
                    String cacheJsonStr = DataProvider.getInstance().getCacheFromDisker(UrlFilter.getFileNameFromUrl(paramUrl));

                    // 缓存无数据
                    if (cacheJsonStr == null) {
                        Response<T> o = new Gson().fromJson(createEmptyJson(noNet ? ReturnCode.NO_NET
                                : ReturnCode.RS_EMPTY_ERROR), token.getType());
                        return o;
                    }

                    // 缓存有数据
                    Response<T> o = new Gson().fromJson(cacheJsonStr, token.getType());
                    return o;
                }

                // 网络和缓存均没有数据
                Response<T> o = new Gson().fromJson(createEmptyJson(noNet ? ReturnCode.NO_NET : ReturnCode.RS_EMPTY_ERROR),
                        token.getType());
                return o;
            }
            
        }, new TaskHandler() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(Object result) {
                listener.result((Response<T>)result);
            }
            
            @Override
            public void onProgressUpdated(Object... params) {
            }
            
            @Override
            public void onFail(TaskFailure failure) {
            }
        });
    }

    public static void download(final String urlPath, final String saveDir, final String saveFileName,
            TaskHandler handler) {
        AsyncTaskManager.INSTANCE.execute(new SyncTask() {

            @Override
            protected Object execute() {
                try {
                    HttpParams params = new HttpParams.Builder(Method.GET, urlPath).create();
                    ApacheHttpClient client = new ApacheHttpClient();
                    client.request(params);
                    HttpResult result = client.getHttpResult();
                    byte[] content = result.getContent();
                    File file = new File(saveDir, saveFileName);
                    if (Files.create(file)) {
                        IOUtil.writeBytesTo(file, content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, handler);
    }

    private static <T> void rscode2Toast(Response<T> o) {
//        if (o.rsCode == ReturnCode.RS_INTERNAL_ERROR) {
//            ToastUtil.showSimpleToast("网络错误");
//        } else if (o.rsCode == ReturnCode.RS_PARAMETER_ERROR) {
//            ToastUtil.showSimpleToast("网络错误");
//        } else if (o.rsCode == ReturnCode.RS_REPECT_CLICK) {
//            ToastUtil.showSimpleToast("重复点击");
//        } else if (o.rsCode == ReturnCode.RS_NONE_OBJECT) {
//        ToastUtil.showSimpleToast("错误操作");
//    }
        if (o.rsCode == ReturnCode.RS_VALIDATE_ERROR) {
            ToastUtil.showSimpleToast("验证错误");
        } else if (o.rsCode == ReturnCode.RS_NO_PRIVILEGE) {
            ToastUtil.showSimpleToast("无权限");
        } else if (o.rsCode == ReturnCode.RS_AD_WORD_EXISTS) {
            ToastUtil.showSimpleToast("含太多广告");
        } else if (o.rsCode == ReturnCode.RS_SENSITIVE_WORD_EXISTS) {
            ToastUtil.showSimpleToast("敏感词");
        }  else if (o.rsCode == ReturnCode.RS_UP_REPECTED_TEXT) {
            ToastUtil.showSimpleToast("重复评论");
        }
    }

}
