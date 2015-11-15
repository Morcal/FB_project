package com.feibo.joke.dao;

import com.feibo.joke.model.Response;

public interface IEntityListener<T>{
    void result(Response<T> response);
}
