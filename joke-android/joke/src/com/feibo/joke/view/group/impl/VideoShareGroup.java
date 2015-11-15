package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.video.DialogUtils;
import com.feibo.joke.video.VideoCoverActivity;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.video.manager.VideoManager.OnParseDraftListener;
import com.feibo.joke.video.manager.VideoManager.OnSaveDraftListener;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.group.AbsLoadingGroup;
import com.feibo.joke.view.util.KeyboardUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.LToggleButton;

public class VideoShareGroup extends AbsLoadingGroup {

    private Context mContext;

    private ViewGroup parent;

    private ImageView mCoverImageView;
    private EditText mIntroduceEditText;
    private TextView mIntroduceNumView;
    private LToggleButton toggleButton;
    private View mSubmitBtn;

    private VideoManager mVideoManager;
    private int mIntroduceNum;

    private IVideoShareListener iVideoShareListener;

    private Draft mDraft;
    
    private boolean isFromDraft() {
        return this.mDraft != null;
    }

    public VideoShareGroup(Context context, Draft draft) {
        this.mContext = context;
        this.mDraft = draft;
    }

    @Override
    public void onCreateView() {
        parent = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.fragment_video_share, null);
        setRoot(parent);

        initView(parent);
        initData();
        initListener();
    }

    private void initData() {
        if(isFromDraft()) {
            setTitlebarRightVisible(View.INVISIBLE);
            launchLoadHelper(0x262626, "正在打开草稿箱...");
            loadFromDraft();
        } else {
            // 如果不是从草稿箱进来，那么无需解帧，直接显示即可
            setView();
        }
    }

    private void setView() {
        mVideoManager = VideoManager.getInstance(mContext);
        mCoverImageView.setImageBitmap(mVideoManager.getCoverBitmap());
        mIntroduceNum = mVideoManager.getIntroduceMaxWordNum();

        int textLength = mVideoManager.getIntroduce().length();
        mIntroduceNum = mIntroduceNum - textLength;
        mIntroduceNumView.setText(String.valueOf(mIntroduceNum));

        if (textLength != 0) {
            mIntroduceEditText.setText(mVideoManager.getIntroduce());
            mIntroduceEditText.setSelection(textLength);
        }
        
        setTitlebarRightVisible(isFromDraft() ? View.GONE : View.VISIBLE);
    }

    //解帧
    private void loadFromDraft() {
        VideoManager videoManager = VideoManager.getInstance(mContext);
        videoManager.parseDraft(mDraft, new OnParseDraftListener() {

            @Override
            public void onSuccess() {
                VideoShareGroup.this.hideLoadHelper();

                setView();

                setTitlebarRightVisible(View.INVISIBLE);
            }

            @Override
            public void onFail() {
                showFailMessage(0);
            }
        });
    }

    private void setTitlebarRightVisible(int visible) {
        if(iVideoShareListener != null) {
            iVideoShareListener.setTitlebarVisible(visible);
        }
    }

    private void initView(View parent) {
        mIntroduceEditText = (EditText) parent.findViewById(R.id.text_introduce);
        mIntroduceNumView = (TextView) parent.findViewById(R.id.text_num);
        mCoverImageView = (ImageView) parent.findViewById(R.id.image_cover);
        mSubmitBtn = parent.findViewById(R.id.btn_submit);
        toggleButton = (LToggleButton) parent.findViewById(R.id.toggleButton);
    }

    private void initListener() {
        mCoverImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((BaseActivity) mContext).startActivity(new Intent(mContext, VideoCoverActivity.class));
            }
        });

        mIntroduceEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = mIntroduceEditText.getText().toString();
                mIntroduceNum = mVideoManager.getIntroduceMaxWordNum() - content.length();

                if (mIntroduceNum < 0) {
                    mIntroduceNum = 0;
                }

                int color = mIntroduceNum <= 10 ? R.color.c3_red : R.color.c7_light_grey;
                mIntroduceNumView.setTextColor(mContext.getResources().getColor(color));

                mIntroduceNumView.setText(mIntroduceNum + "");
                mVideoManager.setIntroduce(content);
                setTitlebarRightVisible(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSubmitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    /**  处理发布  */
    public void handleSubmit() {
        KeyboardUtil.hideKeyboard(mIntroduceEditText);
        if(!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast("你断网了，我给你保存到草稿箱");
            handleSave(false);
            return;
        }

        final String desc = mIntroduceEditText.getText().toString();
        mVideoManager.setIntroduce(desc);

        // 验证是否登录
        if (!UserManager.getInstance().isLogin()) {
            if(iVideoShareListener != null) {
                iVideoShareListener.onLogin();
            }
            // 未登录，保存到草稿箱，并提示用户
            handleSave();
            return;
        }

        // 标记可以上传
        mVideoManager.setShouldUpload(true);

        LogUtil.d("ShareSina", "toggle:" + toggleButton.getToggleState());
        // 同步到微博
        mVideoManager.setNeedShareToSina(toggleButton.getToggleState());
        // 先存到草稿箱

        // 回到主界面后开始上传
        if(iVideoShareListener != null) {
            iVideoShareListener.onPublish();
        }
    }

    public void handleSave(){
        KeyboardUtil.hideKeyboard(mIntroduceEditText);
        handleSave(true);
    }

    private void handleSave(final boolean showToast){
        mVideoManager.saveDraft(new OnSaveDraftListener() {

            @Override
            public void onSuccess() {
                DialogUtils.hideProgressDialog();
                if(showToast) {
                    ToastUtil.showSimpleToast(mContext, "保存成功");
                }
                // 隐藏草稿箱
                setTitlebarRightVisible(View.INVISIBLE);
            }

            @Override
            public void onStart() {
                DialogUtils.showProgressDialog(mContext);
            }

            @Override
            public void onFail() {
                DialogUtils.hideProgressDialog();
                ToastUtil.showSimpleToast(mContext, "保存失败");
            }
        });

        notifyUpdateIfFromDraft();
    }

    private void notifyUpdateIfFromDraft() {
        if(isFromDraft()) {
            // 如果是从草稿箱页面进到这里的话，要返回刷新界面
            ((BaseActivity) mContext).setChangeType(DataChangeEventCode.CHANGE_TYPE_SAVE_VIDEO_DRAFT);
        }
    }

    public void setCoverImage() {
        setTitlebarRightVisible(View.VISIBLE);
        mCoverImageView.setImageBitmap(mVideoManager.getCoverBitmap());
    }

    @Override
    public void onDestroyView() {
        KeyboardUtil.hideKeyboard(mIntroduceEditText);
    }

    @Override
    public void onLoadingHelpStateChange(boolean loadingHelpVisible) {
        int visibility = loadingHelpVisible ? View.INVISIBLE : View.VISIBLE;
        for(int i=0; i<parent.getChildCount(); i++) {
            parent.getChildAt(i).setVisibility(visibility);
        }
    }

    @Override
    public void onLoadingHelperFailClick() {
        initData();
    }

    public void setVideoShareListener(IVideoShareListener iVideoShareListener) {
        this.iVideoShareListener = iVideoShareListener;
    }

    public interface IVideoShareListener {
        public void onLogin();
        public void setTitlebarVisible(int visible);
        public void onPublish();
    }

}
