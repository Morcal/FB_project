package com.feibo.snacks.view.module.home;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.model.bean.Classify;
import com.feibo.snacks.model.bean.Image;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.group.HomePageHead;
import com.feibo.snacks.manager.global.StatisticsManager;
import com.feibo.snacks.view.base.BannerJumpHelper;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.home.category.CategoryFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.widget.DifferSizeAutoScrollViewpager;
import com.feibo.snacks.view.widget.DifferSizeAutoScrollViewpager.OnItemPicClickListener;
import com.feibo.snacks.view.widget.DifferSizeIndicatorView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class HomeAboveViewGroup {

    private CategoryAdapter categoryAdapter;
    private GridView categoryGridView;
    private DifferSizeAutoScrollViewpager topicView;

    private Context context;
    private HomePageHead homeAbove;

    public HomeAboveViewGroup(View rootView, final Context context) {
        this.context = context;
        categoryGridView = (GridView) rootView.findViewById(R.id.home_category);
        categoryAdapter = new CategoryAdapter(context);
        categoryGridView.setAdapter(categoryAdapter);

        topicView = (DifferSizeAutoScrollViewpager) rootView.findViewById(R.id.home_topic);
        DifferSizeIndicatorView indicatorView = (DifferSizeIndicatorView) rootView.findViewById(R.id.home_indicator);
        topicView.addIndicatorView(indicatorView);

        initListener();
    }

    private void initListener() {
        topicView.setOnItemClickListener(new OnItemPicClickListener() {
            @Override
            public void onClick(int position) {
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.click_home_banner));
                BannerJumpHelper.turnBannerSituation(context,homeAbove.topics.get(position),Constant.HOME_BANNER, context.getString(R.string.homeBanner));
            }
        });

        categoryGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Classify classify = homeAbove.classifies.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt(CategoryFragment.CATEGORY_ID, classify.id);
                bundle.putString(CategoryFragment.CATEGORY_TITLE, classify.title);
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, CategoryFragment.class, bundle);
                StatisticsManager.getInstance().statisticsVisitQuantity(classify.id, Constant.HOME_CLASSIFY, 0);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.click_home_classify));
            }
        });
    }

    public void updateView(HomePageHead homeAbove) {
        if (homeAbove == null) {
            return;
        }
        this.homeAbove = homeAbove;
        List<Special> topics = homeAbove.topics;
        List<Image> images = new ArrayList<Image>();
        if (topics != null && topics.size() != 0) {
            for (Special topic : topics) {
                if (topic.img != null) {
                    images.add(topic.img);
                }
            }
            topicView.init(context, images);
            topicView.setVisibility(View.VISIBLE);
        } else {
            topicView.setVisibility(View.GONE);
        }
        categoryAdapter.setItems(homeAbove.classifies);
        categoryAdapter.notifyDataSetChanged();
    }

    public void releaseView() {
        topicView.pauseAutoScroll();
        topicView = null;
        categoryAdapter = null;
        categoryGridView = null;
    }
}
