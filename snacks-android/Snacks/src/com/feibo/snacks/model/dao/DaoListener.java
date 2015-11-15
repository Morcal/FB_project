package com.feibo.snacks.model.dao;


import com.feibo.snacks.model.bean.Response;

public interface DaoListener<T>{
    void result(Response<T> response);
}
