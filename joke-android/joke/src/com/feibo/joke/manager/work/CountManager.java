package com.feibo.joke.manager.work;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.model.Response;

/**
 * Create by：ml_bright on 2015/10/22 09:30
 * Email: 2504509903@qq.com
 */
public class CountManager {

    //显示弹窗事件
    public static final int ACTION_SHOW = 0;

    public enum Action {
        SHOW,
        LEFT,
        RIGHT
    }


    //统计弹窗
    public static void countPopDialog(long id, Action action) {
        JokeDao.countPopDialog(id, action, new IEntityListener<Object>() {
            @Override
            public void result(Response<Object> response) {

            }
        });
    }

}
