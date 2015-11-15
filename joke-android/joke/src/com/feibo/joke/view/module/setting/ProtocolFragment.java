package com.feibo.joke.view.module.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.ProtocolGroup;

public class ProtocolFragment extends BaseTitleFragment{

    private static final String BUNDLE_AD_URL = "adUrl";
    private static final String BUNDLE_NEEDCHACHE = "needCache";
    
    private ProtocolGroup pg;
    
    public static Bundle buildBundle(String url, boolean needCache) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_AD_URL, url);
        bundle.putBoolean(BUNDLE_NEEDCHACHE, needCache);
        return bundle;
    }
    
    @Override
    public View containChildView() {
        Bundle bundle = getArguments();
        String adUrl = bundle.getString(BUNDLE_AD_URL);
//        boolean needCache = bundle.getBoolean(BUNDLE_NEEDCHACHE);
        
        pg = new ProtocolGroup(getActivity(), adUrl);
        pg.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_PROTOCOL));
        pg.onCreateView();
        return pg.getRoot();
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText("用户协议");
        getTitleBar().rightPart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onReleaseView() {
        pg.onDestroyView();
    }

}
