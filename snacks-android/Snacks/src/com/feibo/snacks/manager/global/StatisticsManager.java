package com.feibo.snacks.manager.global;

import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;

public class StatisticsManager {

    private static StatisticsManager sStatisticsManager;

    public static StatisticsManager getInstance() {
        if (sStatisticsManager == null) {
            synchronized (StatisticsManager.class) {
                if (sStatisticsManager == null) {
                    sStatisticsManager = new StatisticsManager();
                }
            }
        }
        return sStatisticsManager;
    }

    private AbsSubmitHelper behaviorHelper;  //行为统计
    private AbsSubmitHelper timeLengthHelper;  //时长统计
    private AbsSubmitHelper visitorHelper;      //访问量统计

    private int statisticsId;   //统计id
    private int behaviorType;       //1:收藏 2:分享 3:淘宝下单 4:立刻购买
    private String aboutUrl;    //立刻购买的url
    private int enterSource;  //TODO 2.1要重新定
    private long stayTime;      //停留在某个商品详情页面的时间
    private int visitorType;        //1、详情页；2、首页的banner 3、专题列表;4、专题页的banner；5、首页6个分类；

    private StatisticsManager() {
        behaviorHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.statisticBehaviour(statisticsId,behaviorType,aboutUrl,listener);
            }
        };
        timeLengthHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.statisticVisitTimeLength(statisticsId,stayTime,listener);
            }
        };
        visitorHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.statisticQuantity(statisticsId,visitorType,enterSource,listener);
            }
        };
    }

    public void statisticsBehavior(int id, int type, String aboutUrl) {
        statisticsId = id;
        behaviorType = type;
        aboutUrl = aboutUrl;
        behaviorHelper.submitData(behaviorHelper.generateLoadingListener(null));
    }

    public void statisticsTimeLength(int id, long stayTime) {
        statisticsId = id;
        stayTime = stayTime;
        timeLengthHelper.submitData(timeLengthHelper.generateLoadingListener(null));
    }

    public void statisticsVisitQuantity(int id, int type, int enterSource) {
        statisticsId = id;
        visitorType = type;
        enterSource = enterSource;
        visitorHelper.submitData(visitorHelper.generateLoadingListener(null));
    }
    
}
