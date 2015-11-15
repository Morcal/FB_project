package com.feibo.joke.view.group;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import fbcore.log.LogUtil;
import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;

public class BasePullWaterGroup extends AbsPullWaterGroup<Video> implements OnItemClickListener{

    private String clickName;

    public BasePullWaterGroup(Context context) {
        super(context);
        init();
    }

    public BasePullWaterGroup(Context context, boolean isDetail) {
        super(context, isDetail);
        init();
    }

    public BasePullWaterGroup(Context context, boolean isDetail, boolean refreshEnable, boolean loadmoreEnable) {
        super(context, isDetail, refreshEnable, loadmoreEnable);
        init();
    }

    public void setUmengVideoClickName(String clickName) {
        this.clickName = clickName;
    }

    private void init() {
        setOnItemClickListener(this);
    }

    @Override
    public void setListAdapter(BaseSingleTypeAdapter<Video> adapter) {
        super.setListAdapter(adapter);
    }

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle= VideoDetailFragment.buildBundle(getListAdapter().getItem(position).id, position);
        LogUtil.i("onItemClick=======BasePullWater", "position=" + (position) + ";videoPosition=" + 0);
        LaunchUtil.launchSubActivity(getContext(), VideoDetailFragment.class, bundle);

        if(!StringUtil.isEmpty(clickName)) {
            MobclickAgent.onEvent(getContext(), clickName);
        }
    }

}
