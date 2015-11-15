package com.feibo.joke.view.module.home;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.TopicsDiscoveryManager;
import com.feibo.joke.manager.list.VideosDynamicManager;
import com.feibo.joke.manager.list.VideosEssenseManager;
import com.feibo.joke.manager.work.GlobalConfigurationManager;
import com.feibo.joke.model.Popup;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.view.adapter.BasePageAdapter;
import com.feibo.joke.view.adapter.DiscoveryListAdapter;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.dialog.PopupDailog;
import com.feibo.joke.view.group.AbsLoadingGroup;
import com.feibo.joke.view.group.BasePullWaterGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.DiscoveryListGroup;
import com.feibo.joke.view.group.impl.DynamicListGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.widget.TabViewGroup;
import com.feibo.joke.view.widget.TabViewGroup.ITabSceneChangeListener;

@Deprecated
public class HomeFragment extends BaseLoginFragment {
    
    //贵圈的位置
    private static final int DYNAMIC_POSITION = 2;
    //弹窗延迟启动时间
    private static final int POPUP_DELAY_TIME = 15000;
    
	private View root;
	private TabViewGroup tabGroup;
	private ViewPager titlePager;
	private BasePageAdapter contentPager;

	private VideosEssenseManager essenseManager;
	private TopicsDiscoveryManager discoveryManager;
	private VideosDynamicManager dynamicManager;

	private VideoListAdapter essenseAdapter;
	private DiscoveryListAdapter discoveryAdapter;
	private VideoListAdapter dynamicAdapter;

	private AbsLoadingGroup[] videoGroups;
	
	private Runnable popupRunnable; 
	private PopupDailog popupDailog;

	@Override
	public View containChildView() {
		root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
		initWidget();
		initListener();
		initAdapter();
		
        initRedhint();
        
		return root;
	}

	@Override
	public int setTitleLayoutId() {
		return 0;
	}

	@Override
	public void onReleaseView() {
		essenseManager.onDestroy();
		discoveryManager.onDestroy();
		dynamicManager.onDestroy();

		for (int i = 0; i < videoGroups.length; i++) {
			videoGroups[i].onDestroyView();
		}
	}

	private void initWidget() {
		tabGroup = (TabViewGroup) root.findViewById(R.id.tabGroup);
		titlePager = (ViewPager) root.findViewById(R.id.pager);

		tabGroup.selectOnScene(TabViewGroup.STATU_HANDPICK);
	}

	private void initListener() {
		tabGroup.setTabSceneChangeLisener(new ITabSceneChangeListener() {
			@Override
			public void tabSceneChanged(int tabScene) {
				titlePager.setCurrentItem(tabScene);
				// 贵圈
				if (tabScene == TabViewGroup.STATU_DYNAMIC) {
					MessageHintManager.setDynamicHint(getActivity(), false);
				}
			}

			@Override
			public void onFreshView() {
				int currentItem = titlePager.getCurrentItem();
				videoGroups[currentItem].onScollToTop(false);
			}
		});

		titlePager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tabGroup.selectOnScene(position);

				// 贵圈
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
	}

	private void initAdapter() {
	    String handpickLoadMoreOverText = getString(R.string.load_more_over_text_handpick);
	    String dynamicLoadMoreOverText = getString(R.string.load_more_over_text_dynamic);
	    
		essenseManager = new VideosEssenseManager();
		discoveryManager = new TopicsDiscoveryManager();
		dynamicManager = new VideosDynamicManager();

		essenseAdapter = new VideoListAdapter(getActivity());
		discoveryAdapter = new DiscoveryListAdapter(getActivity());
		dynamicAdapter = new VideoListAdapter(getActivity());

		videoGroups = new AbsLoadingGroup[3];
		videoGroups[0] = new BasePullWaterGroup(getActivity());
        ((BasePullWaterGroup)videoGroups[0]).setFooterLoadMoreOverText(handpickLoadMoreOverText);
		videoGroups[0].setGroupConfig(GroupConfig
				.create(GroupConfig.GROUP_VIDEO_ESSENCE));

		videoGroups[1] = new DiscoveryListGroup(getActivity());
		videoGroups[1].setGroupConfig(GroupConfig
				.create(GroupConfig.GROUP_VIDEO_DISCOVERY));

		videoGroups[2] = new DynamicListGroup(getActivity()){

			@Override
			public void onLoginClick() {

			}
		};
		videoGroups[2].setGroupConfig(GroupConfig
				.create(GroupConfig.GROUP_VIDEO_DYNAMIC));
        ((DynamicListGroup)videoGroups[2]).setFooterLoadMoreOverText(dynamicLoadMoreOverText);
//		((DynamicListGroup) videoGroups[2])
//				.onLoginClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						loginClick();
//					}
//				});

//		((BasePullWaterGroup) videoGroups[0]).setListAdapter(essenseAdapter);
//		((BasePullWaterGroup) videoGroups[0]).setListManager(essenseManager);
		((DiscoveryListGroup) videoGroups[1]).setListAdapter(discoveryAdapter);
		((DiscoveryListGroup) videoGroups[1]).setListManager(discoveryManager);
		((BasePullWaterGroup) videoGroups[2]).setListAdapter(dynamicAdapter);
		((BasePullWaterGroup) videoGroups[2]).setListManager(dynamicManager);

		contentPager = new BasePageAdapter();
		contentPager.setGroups(videoGroups);
		titlePager.setAdapter(contentPager);
	}

	@Override
	public void setTitlebar() {

	}

	@Override
	public void loginResult(boolean result, int operationCode) {
		if (result) {
			// 登陆成功,重新加载布局
			if (videoGroups != null) {
				contentPager.refreshItem(2);
				contentPager.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
        initRedhint();
		shouldUpload();
		handlePopup();
	}
	
	private void initRedhint() {
		MessageHintManager.initHome(getActivity(), tabGroup);
	}

	@Override
	public void onDataChange(int code) {
		switch (code) {
		case DataChangeEventCode.CODE_CURRENT_ITEM_FRESH:
			if (videoGroups != null) {
				int currentItem = titlePager.getCurrentItem();
				videoGroups[currentItem].onScollToTop(true);
			}
			break;
		case DataChangeEventCode.CODE_EVENT_BUS_REDHINT:
			initRedhint();
			break;
		case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
		    int currentItem = titlePager.getCurrentItem();
            videoGroups[currentItem].onDataChange(code);
		    break;
		default:
			break;
		}
	}
	
	public void shouldUpload() {
	    //是否启动上传视频
        if (VideoManager.getInstance(getActivity()).shouldUpload()) {
            // 切换到贵圈，启动上传
            int currentItem = titlePager.getCurrentItem();
            boolean hasLoading = contentPager.hasLoadingPosition(DYNAMIC_POSITION);
            if(currentItem != DYNAMIC_POSITION) {
                tabGroup.selectOnScene(TabViewGroup.STATU_DYNAMIC);
                titlePager.setCurrentItem(DYNAMIC_POSITION);
            }
            if(currentItem == DYNAMIC_POSITION || hasLoading) {
                //当前在贵圈或者已经预加载完成，既贵圈已经执行完OnCreateView的方法,这只要直接通知更新就好
                videoGroups[DYNAMIC_POSITION].onDataChange(DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS);
            }
        }
	}
	
	private void handlePopup() {
        if(GlobalConfigurationManager.getInstance().canShowPopup(getActivity(), Popup.SCENE_TYPE)) {
            if(popupDailog != null && popupDailog.isShowing()) {
                return;
            }
            popupRunnable = new Runnable() {
                
                @Override
                public void run() {
                    Popup pop = GlobalConfigurationManager.getInstance().getPopup();
                    if(pop == null || getActivity() == null) {
                        return;
                    }
                    popupDailog = PopupDailog.show(getActivity(), pop, true);
                    SPHelper.setPopupId(getActivity(), pop.id);
                }
            };
            postDelayed(popupRunnable, POPUP_DELAY_TIME);
        }
    }

	@Override
	public void onPause() {
	    removeHandle(popupRunnable);
	    super.onPause();
	}
	
}
