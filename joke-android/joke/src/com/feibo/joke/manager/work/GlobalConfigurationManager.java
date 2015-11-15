package com.feibo.joke.manager.work;

import android.content.Context;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.model.GlobalConfiguration;
import com.feibo.joke.model.Popup;
import com.feibo.joke.model.PopupButton;
import com.feibo.joke.model.Response;
import com.feibo.joke.utils.SPHelper;

public class GlobalConfigurationManager {

    private static Popup popup;

    private static GlobalConfigurationManager manager;

    public static GlobalConfigurationManager getInstance() {
        if (manager == null) {
            manager = new GlobalConfigurationManager();
        }
        return manager;
    }

    public void getGlobalConfiguration(final Context context) {
        JokeDao.getGlobalConfiguration(new IEntityListener<GlobalConfiguration>() {

            @Override
            public void result(Response<GlobalConfiguration> response) {
                if (response.rsCode == ReturnCode.RS_SUCCESS && response != null && response.data != null) {
                    GlobalConfiguration con = response.data;
                    SPHelper.setIsShowTopic(context, con.isShowTopic == 1);

                    GlobalConfigurationManager.popup = con.popup;
                }
            }
        });
    }

    /**
     * 是否可以显示弹窗
     * 
     * @return
     */
    public boolean canShowPopup(Context context, int sceneType) {
        if (popup == null || context == null) {
            return false;
        }
        if (popup.sceneType != Popup.SCENE_TYPE || SPHelper.getPopupId(context) >= popup.id) {
            return false;
        }

        return true;
    }

    public Popup getPopup() {
        return popup;
    }

    /////////////////// 测试弹窗数据  ////////////////////
    private Popup getTestPopup() {
        Popup popup = new Popup();
        popup.content = "测试内容";
        popup.id = 800;
        popup.sceneType = 1;
        popup.title = "测试标题";
        popup.startTime = (System.currentTimeMillis() - 1000) / 1000;
        popup.endTime = (System.currentTimeMillis() + 1000 * 60 * 5) / 1000;

        PopupButton leftPb = new PopupButton();
        leftPb.action = 3;
        leftPb.lable = "视频详情";
        leftPb.isHighLight = 1;
        leftPb.info = "{\"id\":20,\"title\":\"#飞博非法北欧#\"}";
        PopupButton rightPb = new PopupButton();
        rightPb.action = 5;
        rightPb.lable = "个人主页";
        rightPb.isHighLight = 1;
        rightPb.info = "927";

        popup.leftButton = leftPb;
        popup.rightButton = rightPb;
        
        return popup;
    }

}
