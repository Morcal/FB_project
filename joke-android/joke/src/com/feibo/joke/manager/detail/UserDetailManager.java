package com.feibo.joke.manager.detail;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;

public class UserDetailManager extends BaseManager{
    
    private long userId;
    private User user;
    
    public UserDetailManager(long userId){
        this.userId = userId;
    }
    
    public void getUserDetail(final LoadListener listener){
        JokeDao.getUserInfo(userId, new IEntityListener<User>() {
            
            @Override
            public void result(Response<User> response) {
                if(response.rsCode != ReturnCode.RS_SUCCESS){
                    handleFail(listener, response.rsCode);
                    return;
                }
                
                user = response.data;
                handleSuccess(listener);
            }
        }, true);
    }
    
    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
