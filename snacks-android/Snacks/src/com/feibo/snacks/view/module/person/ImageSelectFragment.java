package com.feibo.snacks.view.module.person;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.util.LaunchUtil;

import fbcore.log.LogUtil;

/**
 * 用来选择和裁剪图片的Activity
 * Created by lidiqing on 15-9-6.
 */
public class ImageSelectFragment extends BaseFragment {
    private static final String TAG = ImageSelectFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE_SELECT = 0x100;
    private static final int REQUEST_IMAGE_EDIT = 0x200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleSelect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "onActivityResult");
        if (requestCode == REQUEST_IMAGE_SELECT) {
            LogUtil.i(TAG, "onActivityResult select");
            if(resultCode != Activity.RESULT_OK || data == null){
                handleQuitFail();
                return;
            }
            handleEdit(data.getData());
            return;
        }

        if (requestCode == REQUEST_IMAGE_EDIT){
            LogUtil.i(TAG, "onActivityResult edit");
            if(resultCode != Activity.RESULT_OK || data == null){
                handleQuitFail();
                return;
            }
            handleQuitSuccess(data.getData());
            return;
        }
    }

    // 选择照片
    public void handleSelect() {
        LogUtil.i(TAG, "handleSelect");
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_IMAGE_SELECT);
    }

    // 裁剪图片
    public void handleEdit(Uri uri) {
        LogUtil.i(TAG, "handleEdit");
        Intent intent = new Intent();
        intent.setData(uri);
        LaunchUtil.launchActivityForResultByIntent(REQUEST_IMAGE_EDIT,
                getActivity(),
                BaseSwitchActivity.class,
                ImageEditFragment.class,
                intent);
    }

    // 成功获取图片
    public void handleQuitSuccess(Uri uri){
        LogUtil.i(TAG, "handleQuitSuccess");
        Intent intent = new Intent();
        intent.setData(uri);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    // 退出
    public void handleQuitFail(){
        LogUtil.i(TAG, "handleQuitFail");
        getActivity().setResult(Activity.RESULT_CANCELED, null);
        getActivity().finish();
    }

}
