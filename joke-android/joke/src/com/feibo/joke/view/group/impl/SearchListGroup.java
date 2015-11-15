package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.feibo.joke.R;
import com.feibo.joke.view.group.AbsListGroup;
import com.feibo.joke.view.group.BasePullListGroup;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by Administrator on 2015/11/6.
 */
public class SearchListGroup extends BasePullListGroup<String> {
    public ListView listView;

    public SearchListGroup(Context context) {
        super(context);
    }

    @Override
    public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.search_list, null);
        System.out.println("加载了listview布局-------------------------------------");
        return parent;
    }

    @Override
    public void initWidget() {
        listView = (ListView) getRoot().findViewById(R.id.list_search);
        listView.setAdapter(getListAdapter());
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void hideRefreshLoading() {

    }

    @Override
    public void hideLoadMoreLoading(boolean loadMoreSuccess) {

    }

    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {

    }
}
