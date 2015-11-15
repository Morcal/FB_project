package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.feibo.joke.R;
import com.feibo.joke.view.group.BasePullListGroup;

/**
 * Created by Administrator on 2015/11/8.
 */
public class SearchDetailGroup extends BasePullListGroup {
    public SearchDetailGroup(Context context, boolean needPullRefresh, boolean needLoadMore) {
        super(context, false, needLoadMore);
    }

    @Override
    public View initHeaderView() {
        View headeView= LayoutInflater.from(getContext()).inflate(R.layout.search_detial_header,null);
        return headeView;
    }

}
