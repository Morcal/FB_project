package com.feibo.joke.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fbcore.log.LogUtil;

public abstract class BaseFragment extends Fragment {
    
    private static final String TAG = BaseFragment.class.getSimpleName();
    
    private ViewGroup mRoot;
    private String fmName = null;

    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = onCreateView(inflater, savedInstanceState);
        } else if (mRoot.getParent() != null) {
            ((ViewGroup) mRoot.getParent()).removeView(mRoot);
        }
        return mRoot;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestory");
        mRoot = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(TAG, "on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fmName != null) {
//            MobclickAgent.onPageStart(fmName);
        }
    }

    public void showLoadingDialog(boolean show, String msg) {
        if(show) {
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(getActivity());
                loadingDialog.setMessage(msg);
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(true);
                loadingDialog.show();
            }
            loadingDialog.setMessage(msg);

            if (!loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        } else {
            if(loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fmName != null) {
//            MobclickAgent.onPageEnd(fmName);
        }
    }
    
    public Bundle getFinishBundle() {
        if(getActivity() == null) {
            return null;
        }
        return ((BaseActivity)getActivity()).getFinishBundle();
    }
    
    public void setFinishBundle(Bundle bundle) {
        if(getActivity() == null) {
            return;
        }
        ((BaseActivity)getActivity()).setFinishBundle(bundle);
    }

    public void setChangeTypeAndFinish(int changeType) {
        if(getActivity() == null) {
            return;
        }
        ((BaseActivity)getActivity()).setChangeTypeAndFinish(changeType);
    }
    
    public void setChangeType(int changeType) {
        if(getActivity() == null) {
            return;
        }
        ((BaseActivity)getActivity()).setChangeType(changeType);
    }
    
    public void cancleChangeType() {
        setChangeType(0);
    }

    public abstract ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState);

    public void onDataChange(int code) {}
    
}
