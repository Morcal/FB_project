package com.feibo.snacks.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.view.dialog.RemindDialog;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.social.ResultListener;
import com.feibo.social.base.Platform;
import com.feibo.social.manager.SocialComponent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jayden on 2015/10/24.
 */
public class TestActivity extends Activity {

    @Bind(R.id.btn1)
    Button btn1;
    @Bind(R.id.btn2)
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn1)
    public void turnToBtn1() {
        Intent intent = new Intent(this, WebAcitivity.class);
        intent.putExtra(WebAcitivity.SHOPPING_URL, "http://zengnanlin.lingshi/app_miaoturn/lucky/index.html");
        startActivity(intent);
    }

    @OnClick(R.id.btn2)
    public void turnToBtn2() {
        handleLogout();
    }

    private void handleLogout() {
        RemindDialog.RemindSource source = new RemindDialog.RemindSource();
        source.contentStr = getResources().getString(R.string.dialog_exit_content);
        source.confirm = getResources().getString(R.string.dialog_exit_cancle);
        source.cancel = getResources().getString(R.string.dialog_exit_confirm);
        RemindControl.showRemindDialog(this, source, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
            }

            @Override
            public void onCancel() {
                exitAccount();
            }
        });
    }

    private void exitAccount() {
        int type = UserManager.getInstance().getUser().getPlatform();
        Platform platform = null;
        if (type == User.PLATFORM_TYPE_QQ) {
            platform = Platform.QQ;
        } else if (type == User.PLATFORM_TYPE_WX) {
            platform = Platform.WEIXIN;
        } else if (type == User.PLATFORM_TYPE_WB) {
            platform = Platform.SINA;
        } else {
            UserManager.getInstance().logout();
            return;
        }

        SocialComponent.create(platform, this).logout(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    UserManager.getInstance().logout();
                }
            }
        });
    }

}
