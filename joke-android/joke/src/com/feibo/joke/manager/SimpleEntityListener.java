package com.feibo.joke.manager;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.model.Response;

public class SimpleEntityListener implements IEntityListener<Object>{

    private LoadListener listener;
    
    public SimpleEntityListener(LoadListener listener){
        this.listener = listener;
    }
    
    @Override
    public void result(Response<Object> entity) {
        if(entity.rsCode == ReturnCode.RS_SUCCESS){
            listener.onSuccess();
            return;
        }
        listener.onFail(entity.rsCode);
    }

}
