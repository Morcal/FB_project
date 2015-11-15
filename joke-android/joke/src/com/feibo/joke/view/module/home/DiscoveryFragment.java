package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.R;
import com.feibo.joke.manager.list.TopicsDiscoveryManager;
import com.feibo.joke.view.BaseFragment;
import com.feibo.joke.view.adapter.DiscoveryListAdapter;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.DiscoveryListGroup;

public class DiscoveryFragment extends BaseFragment implements GroupOperator{
    
    private TopicsDiscoveryManager discoveryManager;
    private DiscoveryListAdapter discoveryAdapter;
    private DiscoveryListGroup listGroup;
    
    public DiscoveryFragment() {
        
    }
    
    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        discoveryManager = new TopicsDiscoveryManager();
        discoveryAdapter = new DiscoveryListAdapter(getActivity());
    }
    
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        return (ViewGroup) inflater.inflate(R.layout.fragment_discovery, null);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listGroup = new DiscoveryListGroup(getActivity());
        listGroup.setGroupConfig(GroupConfig
                .create(GroupConfig.GROUP_VIDEO_DISCOVERY));
        
        listGroup.setListAdapter(discoveryAdapter);
        listGroup.setListManager(discoveryManager);
        
        listGroup.onCreateView();
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.removeAllViews();
        viewGroup.addView(listGroup.getRoot());
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onScrollToTop(boolean refresh) {
        listGroup.onScollToTop(refresh);
    }

    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        listGroup.onDataChange(code);
    }
}
