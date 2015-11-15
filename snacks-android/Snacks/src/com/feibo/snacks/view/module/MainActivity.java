package com.feibo.snacks.view.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.module.home.HomeFragment;
import com.feibo.snacks.view.module.person.PersonFragment;
import com.feibo.snacks.view.module.person.setting.UpdateController;
import com.feibo.snacks.view.module.specialselling.SpecialSellFragment;
import com.feibo.snacks.view.module.subject.SubjectPageFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.social.manager.SocialComponent;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements OnClickListener {

    public final static int ENTRY_HOME_SCENCE = 3;
    private Button curButton;

    private Button homeBtn;
    private Button categoryBtn;
    private Button topicBtn;
    private Button personBtn;
    private FragmentTransaction ts;
    private BaseFragment curFragment;
    private static WeakReference<MainActivity> wrActivity = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        wrActivity = new WeakReference<MainActivity>(this);

        setContentView(R.layout.activity_main);
        homeBtn = (Button) findViewById(R.id.main_home);
        categoryBtn = (Button) findViewById(R.id.main_category);
        topicBtn = (Button) findViewById(R.id.main_topic);
        personBtn = (Button) findViewById(R.id.main_person);

        homeBtn.setOnClickListener(this);
        categoryBtn.setOnClickListener(this);
        topicBtn.setOnClickListener(this);
        personBtn.setOnClickListener(this);

        curButton = homeBtn;
        curButton.setSelected(true);

        HomeFragment homeFragment = new HomeFragment();
        curFragment = homeFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.main_content, curFragment).commit();

        checkUpdate();
        getServiceContact();
        initRedPoint();
    }

    public static final String SCENCE_LOCATION = "scence_location";
    public static final int HOME_SCENCE = 0;
    public static final int SPECIAL_SELLING_SCENCE = 1;
    public static final int SUBJECT_SCENCE = 2;
    public static final int PERSON_SCENCE = 3;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SocialComponent.onNewIntent(this, intent);
        if (intent == null) {
            return;
        }
        int scenceJump = intent.getIntExtra(MainActivity.SCENCE_LOCATION, -1);
        switch (scenceJump) {
            case HOME_SCENCE:
                changeScene(homeBtn, new HomeFragment());
                break;
            case SPECIAL_SELLING_SCENCE:
                changeScene(categoryBtn, new SpecialSellFragment());
                break;
            case SUBJECT_SCENCE:
                changeScene(topicBtn, new SubjectPageFragment());
                break;
            case PERSON_SCENCE:
                changeScene(personBtn, new PersonFragment());
                break;
            default: break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onKillProcess(this);
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this == null) return;
        SocialComponent.onActivityResult(this, requestCode, resultCode, data);
        if (resultCode == ENTRY_HOME_SCENCE) {
            changeScene(homeBtn, new HomeFragment());
        }

        if (curFragment != null) {
            curFragment.onActivityResult(requestCode, resultCode, data);
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


    private void getServiceContact() {
        ContactManager.getInstance().loadServiceContact(new ContactManager.IResultListener() {
            @Override
            public void onResult(boolean ifSuccess, String failMsg) {
            }
        });
    }

    private void checkUpdate() {
        UpdateController controller = new UpdateController();
        controller.checkUpdate(this, true, null);
    }

    private void initRedPoint() {
        RedPointManager.getInstance().loadRedPoint();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.main_home: {
            MobclickAgent.onEvent(this, getResources().getString(R.string.click_home_count));
            changeScene(homeBtn, new HomeFragment());
            break;
        }
        case R.id.main_category: {
            MobclickAgent.onEvent(this, getResources().getString(R.string.click_specialsell_count));
            changeScene(categoryBtn, new SpecialSellFragment());
            break;
        }
        case R.id.main_topic: {
            MobclickAgent.onEvent(this, getResources().getString(R.string.click_subject_count));
            changeScene(topicBtn, new SubjectPageFragment());
            break;
        }
        case R.id.main_person: {
            MobclickAgent.onEvent(this, getResources().getString(R.string.click_person_count));
            changeScene(personBtn, new PersonFragment());
            break;
        }
        default:
            break;
        }
    }

    private void changeScene(Button selectBtn, BaseFragment fragment) {
        if (selectBtn == curButton) {
            return;
        }
        if (wrActivity.get() != null && wrActivity.get().isFinishing() != true) {
        	ts = getSupportFragmentManager().beginTransaction();
        	homeBtn.setSelected(false);
        	categoryBtn.setSelected(false);
        	topicBtn.setSelected(false);
        	personBtn.setSelected(false);
        	selectBtn.setSelected(true);
        	ts.replace(R.id.main_content, fragment).commit();
        	curFragment = fragment;
        	curButton = selectBtn;
        }
    }

    private long lastClickTime = -1;
    private static final int DOUBLE_CLICK_INTEVAL_TO_EXIT = 2000;

    private void doubleClick2Exit() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickTime < DOUBLE_CLICK_INTEVAL_TO_EXIT) {
            finish();
        } else {
            lastClickTime = now;
            RemindControl.showSimpleToast(this, getResources().getString(R.string.exit_snacks));
        }
    }

}