package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.manager.work.GlobalConfigurationManager;
import com.feibo.joke.model.Popup;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.view.BaseFragment;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.dialog.PopupDailog;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.widget.TabViewGroup;
import com.feibo.joke.view.widget.TabViewGroup.ITabSceneChangeListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import fbcore.log.LogUtil;

public class HomeFragment2 extends BaseTitleFragment {
    private static final String TAG = HomeFragment2.class.getSimpleName();

    //贵圈的位置
    private static final int DYNAMIC_POSITION = 2;
    private static final int POPUP_DELAY_TIME = 15000;

    private TabViewGroup tabGroup;
    private ViewPager viewPager;
    private HomePagerAdapter viewPagerAdapter;
    private List<BaseFragment> fragments;

    private PopupDailog popupDailog;
    private Runnable popupRunnable;

    public HomeFragment2() {
        LogUtil.i(TAG, "new");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        fragments = new ArrayList<>();
        fragments.add(EssenceFragment.newInstance());
        fragments.add(DiscoveryFragment.newInstance());
        fragments.add(new DynamicFragment2());
        viewPagerAdapter = new HomePagerAdapter(getChildFragmentManager(), fragments);
    }

    @Override
    public View containChildView() {
        return View.inflate(getActivity(), R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG, "onViewCreated");
        tabGroup = (TabViewGroup) view.findViewById(R.id.tabGroup);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);

        tabGroup.setTabSceneChangeLisener(new ITabSceneChangeListener() {
            @Override
            public void tabSceneChanged(int tabScene) {
                LogUtil.i(TAG, "tabSceneChanged:" + tabScene);
                viewPager.setCurrentItem(tabScene);
                // 贵圈
                if (tabScene == TabViewGroup.STATU_DYNAMIC) {
                    MessageHintManager.setDynamicHint(getActivity(), false);

                    MobclickAgent.onEvent(HomeFragment2.this.getActivity(), UmengConstant.HOME_DYNAMIC);
                } else if (tabScene == TabViewGroup.STATU_FOUND) {
                    MobclickAgent.onEvent(HomeFragment2.this.getActivity(), UmengConstant.HOME_DISCOVERY);
                } else {
                    MobclickAgent.onEvent(HomeFragment2.this.getActivity(), UmengConstant.HOME_HANDPICK);
                }
            }

            @Override
            public void onFreshView() {
                int currentItem = viewPager.getCurrentItem();
                ((GroupOperator) fragments.get(currentItem)).onScrollToTop(false);
            }
        });

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabGroup.selectOnScene(position);

                // 贵圈红点
                if (position == TabViewGroup.STATU_DYNAMIC) {
                    MessageHintManager.setDynamicHint(getActivity(), false);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int statu) {
            }
        });

        tabGroup.selectOnScene(TabViewGroup.STATU_HANDPICK);
    }

    @Override
    public void onResume() {
        super.onResume();
        initRedhint();
        shouldUpload();
        handlePopup();
    }

    @Override
    public void onPause() {
        removeHandle(popupRunnable);
        super.onPause();
    }

    @Override
    public void onDataChange(int code) {
        switch (code) {
            case DataChangeEventCode.CODE_CURRENT_ITEM_FRESH:
                if(fragments != null && viewPager != null) {
                    ((GroupOperator) fragments.get(viewPager.getCurrentItem())).onScrollToTop(true);
                }
                break;
            case DataChangeEventCode.CODE_EVENT_BUS_REDHINT:
                initRedhint();
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
                fragments.get(viewPager.getCurrentItem()).onDataChange(code);
                break;
            default:
                break;
        }
    }

    private void handlePopup() {
        if (GlobalConfigurationManager.getInstance().canShowPopup(getActivity(), Popup.SCENE_TYPE)) {
            if (popupDailog != null && popupDailog.isShowing()) {
                return;
            }
            popupRunnable = new Runnable() {

                @Override
                public void run() {
                    Popup pop = GlobalConfigurationManager.getInstance().getPopup();
                    if (pop == null || getActivity() == null) {
                        return;
                    }
                    popupDailog = PopupDailog.show(getActivity(), pop, true);
                    SPHelper.setPopupId(getActivity(), pop.id);
                }
            };
            postDelayed(popupRunnable, POPUP_DELAY_TIME);
        }
    }

    private void initRedhint() {
        MessageHintManager.initHome(getActivity(), tabGroup);
    }

    private void shouldUpload() {
        //是否启动上传视频
        if (VideoManager.getInstance(getActivity()).shouldUpload()) {
            // 切换到贵圈，启动上传
            int currentItem = viewPager.getCurrentItem();
            if (currentItem != DYNAMIC_POSITION) {
                tabGroup.selectOnScene(TabViewGroup.STATU_DYNAMIC);
                viewPager.setCurrentItem(DYNAMIC_POSITION);
            }
            fragments.get(DYNAMIC_POSITION).onDataChange(DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS);
        }
    }

    static class HomePagerAdapter extends FragmentPagerAdapter {

        private List<BaseFragment> fragments;

        public HomePagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            LogUtil.i(TAG, "getItem:" + arg0);
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    // -------------------废弃或无用的方法----------------------------
    @Override
    public int setTitleLayoutId() {
        return 0;
    }

    @Override
    public void setTitlebar() {
    }

    @Override
    public void onReleaseView() {
    }
}
