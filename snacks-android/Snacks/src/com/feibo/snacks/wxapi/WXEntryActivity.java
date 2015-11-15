package com.feibo.snacks.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.feibo.snacks.util.Util;
import com.feibo.social.ResultListener;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;

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
    public void onResp(final BaseResp resp) {
        super.setLisener(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess && resp.errCode == BaseResp.ErrCode.ERR_OK) {
                    Util.notify("分享成功");
                }
            }
        });
        super.onResp(resp);
    }

}
