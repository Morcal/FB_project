package com.feibo.joke.manager.list;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;
import com.feibo.joke.model.data.BaseListData;

import java.util.List;

import fbcore.log.LogUtil;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SearchFriendsManager extends AbsListManager<User> {
    private String msg;
    private int page_id;
    private int rsCode;
    public List<User> users;

    public SearchFriendsManager(String msg) {
        this.msg = msg;
    }

    @Override
    protected void refresh(IEntityListener<BaseListData<User>> listener) {
        page_id = 1;
        JokeDao.getSearchUserMsg(page_id, msg, listener, true);
    }

    @Override
    protected void loadMore(IEntityListener<BaseListData<User>> listener) {
        page_id++;
        JokeDao.getSearchUserMsg(page_id, msg, listener, true);

    }

    /***
     * 得到相关用户集合
     * @param listener
     */
    public void getUserDetail(final LoadListener listener) {
        page_id=1;
        JokeDao.getSearchUserMsg(page_id, msg, new IEntityListener<BaseListData<User>>() {
            @Override
            public void result(Response<BaseListData<User>> response) {
                if (response.rsCode != ReturnCode.RS_SUCCESS) {
                    handleFail(listener, response.rsCode);
                    rsCode=response.rsCode;//若为1004时则为空数据
                    LogUtil.i("相关好友集合SFMansger返回状态码--------------------------------------------->", rsCode + "");
                    return;
                }
                handleSuccess(listener);
                BaseListData<User> data = response.data;


                users = data.items;
                LogUtil.i("相关好友集合SFMansger--------------------------------------------->",users.size()+"");

            }
        }, true);
        page_id++;
    }

    public List<User> getUsers() {
        return users;
    }

    public int getRsCode(){
        return rsCode;
    }
}
