package com.feibo.joke.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;

import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.ResultListener;

public class WXEntryActivity extends com.feibo.social.manager.WXEntryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onReq(BaseReq resq) {
        super.onReq(resq);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.setLisener(new ResultListener() {

            @Override
            public void onResult(boolean isSuccess, String result) {
                ToastUtil.showSimpleToast(WXEntryActivity.this, result);
            }
        });
        super.onResp(resp);
    }

}
