package com.feibo.joke.view.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.feibo.joke.view.module.home.HomeFragment2;
import com.umeng.analytics.MobclickAgent;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.Joke;
import com.feibo.joke.manager.work.PushManager;
import com.feibo.joke.video.VideoRecordActivity;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.BaseFragment;
import com.feibo.joke.view.module.mine.MineFragment;
import com.feibo.joke.view.module.setting.UpdateController;
import com.feibo.joke.view.module.setting.UpdateController.OnUpdateListener;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.MainTabButton;

public class MainActivity extends BaseActivity implements OnClickListener {

	private MainTabButton currentBtn;
	private MainTabButton homeTab;
	private MainTabButton mineTab;
	private View videoTab;

	private BaseFragment currentFragment;
	private FragmentTransaction ts;

	private long lastClickTime = -1;
	private static final int DOUBLE_CLICK_INTEVAL_TO_EXIT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		initWidget();
		initListener();

		HomeFragment2 handpickPageFragment = new HomeFragment2();
		currentFragment = handpickPageFragment;
		currentBtn = homeTab;
		changeTabScane(true);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.home_content, handpickPageFragment).commit();

        initRedhint();
		checkVersion();
	}

	private void checkVersion() {
		UpdateController controller = new UpdateController();
		controller.checkUpdate(this, true, new OnUpdateListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFail(String failMsg) {
			}
		});
	}

	private void initListener() {
		homeTab.setOnClickListener(this);
		mineTab.setOnClickListener(this);
		videoTab.setOnClickListener(this);
	}

	private void initWidget() {
		homeTab = (MainTabButton) findViewById(R.id.tab_home);
		mineTab = (MainTabButton) findViewById(R.id.tab_mine);
		videoTab = findViewById(R.id.tab_video);
	}

	private void changeScene(MainTabButton btn, BaseFragment fragment){
		changeScene(btn, fragment, false);
	}
	
	private void changeScene(MainTabButton btn, BaseFragment fragment, boolean isRecord) {
		if (currentBtn == btn) {
		    if(!isRecord) {
    			currentFragment.onDataChange(DataChangeEventCode.CODE_CURRENT_ITEM_FRESH);
		    }
			return;
		}

		ts = getSupportFragmentManager().beginTransaction();
		ts.replace(R.id.home_content, fragment).commitAllowingStateLoss();
		currentFragment = fragment;
		currentBtn = btn;
		if (currentBtn == homeTab) {
			changeTabScane(true);
		} else {
			changeTabScane(false);
		}
		PushManager.getAllPushMessage(this);
		initRedhint();
	}

	private void changeTabScane(boolean isHome) {
		if (isHome) {
			homeTab.setSelect(true);
			mineTab.setSelect(false);
		} else {
			homeTab.setSelect(false);
			mineTab.setSelect(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_home:
			changeScene(homeTab, new HomeFragment2());
			break;
		case R.id.tab_mine:
			changeScene(mineTab, new MineFragment());
			break;
		case R.id.tab_video:
			startActivity(new Intent(MainActivity.this,
					VideoRecordActivity.class));
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doubleClick2Exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doubleClick2Exit() {
		long now = SystemClock.elapsedRealtime();
		if (now - lastClickTime < DOUBLE_CLICK_INTEVAL_TO_EXIT) {
			if (Joke.app() != null) {
				Joke.app().quit();
			}
			finish();
		} else {
			lastClickTime = now;
			ToastUtil.showSimpleToast(this,
					getResources().getString(R.string.exit_joke));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Debug.stopMethodTracing();
		MobclickAgent.onKillProcess(this);
		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
        initRedhint();
        PushManager.getAllPushMessage(this);
	}

	@Override
	public void onDataChange(int code) {
	    boolean isReturn = false;
	    switch (code) {
        case DataChangeEventCode.CODE_EVENT_BUS_REDHINT:
            initRedhint();
            if(currentFragment != null) {
                currentFragment.onDataChange(code);
                isReturn = true;
            }
            break;
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            changeScene(homeTab, new HomeFragment2(), true);
            isReturn = true;
            break;
        }
	    
	    if(isReturn) {
	        return;
	    }

        if (currentFragment != null) {
            currentFragment.onDataChange(code);
        }
	}

	private void initRedhint() {
		MessageHintManager.initMain(this, homeTab, mineTab);
	}
}
