package com.feibo.joke.view.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.view.util.KeyboardUtil;
import com.feibo.joke.view.util.ToastUtil;

/**
 *
 * com.feibo.joke.view.widget.InputBarView
 * @author LinMW<br/>
 * Creat at2015-6-3 下午3:51:54
 */
public class InputBarView extends RelativeLayout implements OnClickListener {

    private EditText etComment;//评论编辑框
    private Button tvSend;//发送评论文字按钮
    private InputBarViewListener listener;//发送评论文字按钮
    /** 用于缓存评论，进行重复评论判断 **/
    private static LastComment commentsCache;
    private long delayTime=180;
    private long videoId=0;
    boolean isNeedJudgeRepeat=true;
    private String msgRepeat="你已经发过了哦！";
    private String msgForNull="请输入你要评论的内容吧...";
    private String msgTooLong="大侠，字数有点多，精简精简哦！";

    public InputBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
//        mRatioWidth = typedArray.getInteger(R.styleable.ScaleView_ratioWidth, 1);
//        mRatioHeight = typedArray.getInteger(R.styleable.ScaleView_ratioHeight, 1);
//        typedArray.recycle();

        findViews();
        initView();
    }

    public InputBarView(Context context) {
        this(context,null);
    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float mRatio = mRatioHeight / (float) mRatioWidth;
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = (int) Math.ceil(widthSize * mRatio);
//        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, newHeightSpec);
//    }

    private void findViews() {
        View  view=this.inflate(getContext(), R.layout.input_send_bar, null);
        etComment = (EditText) view.findViewById(R.id.et_say_something);
        tvSend = (Button) view.findViewById(R.id.tv_send);
//        this.framelayout = (FrameLayout) view.findViewById(R.id.bbvideoview);
//        this.mVideoImage = (ImageView) view.findViewById(R.id.iv_video_image);
//        this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
//        this.loadingImg = (ImageView) view.findViewById(R.id.loading_img);
//        this.progress = view.findViewById(R.id.loading_progress);
        this.addView(view);
    }

    private void initView() {
        if(commentsCache==null){
            commentsCache=new LastComment();
        }
        tvSend.setOnClickListener(this);
      //监听输入框的焦点状态，判断是否已登录
        etComment.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listener.onEditTextFocusChange(hasFocus);
            }
        });
        // 监听输入框的变化
        etComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    tvSend.setTextColor(getContext().getResources().getColor(R.color.c2_orange));
                } else {
                    tvSend.setTextColor(getContext().getResources().getColor(R.color.c5_dark_grey));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    /**设置重复判断的延迟时间，默认为3分钟*/
    public void setDelayTime(long time){
        this.delayTime=time;
    }
    public void setCurrentVideoId(long id){
        this.videoId=id;
    }
    public void setIsNeedJudgeRepeat(boolean is){
        this.isNeedJudgeRepeat=is;
    }
    public void setListener(InputBarViewListener listener){
        this.listener=listener;
    }
    /**videoId设置为0时，并且没有执行setCurrentVideoId（），则等价于不判断videoId*/
    public void setLastComment(String comment,long publishTime, long videoId){
        commentsCache.comment=comment;
        commentsCache.publishTime=publishTime;
        commentsCache.videoId=videoId;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_send: // 发送评论按钮
            if(listener.beforeClickSend()){
                break;
            }
            String content = getText();
            if (!TextUtils.isEmpty(content)) {
                //判断和前一条是不是重复内容且在3分钟内发的
                if(isNeedJudgeRepeat && content.equals(commentsCache.comment) && commentsCache.videoId==this.videoId && (TimeUtil.intervalTime(commentsCache.publishTime)<=delayTime)){
                    ToastUtil.showSimpleToast(msgRepeat);
                    break;
                }
                if(content.length()>140){
                    ToastUtil.showSimpleToast(msgTooLong);
                    break;
                }
                listener.send(content);
            } else {
                ToastUtil.showSimpleToast(msgForNull);
            }
            break;
        default:
            break;
        }

    }
    public String getText() {
        return etComment.getText().toString().trim();
    }
    public void setText(String s) {
        etComment.setText(s);
    }
    public void setMsg(String repeat,String forNull,String tooLong) {
        if(!StringUtil.isEmpty(repeat)){
            msgRepeat=repeat;
        }
        if(!StringUtil.isEmpty(forNull)){
            msgForNull=forNull;
        }
        if(!StringUtil.isEmpty(tooLong)){
            msgTooLong=tooLong;
        }
    }
    public void setHint(String s) {
        etComment.setHint(s);
    }

    public void resetInputBar(Boolean resetText) {
        listener.onResetInputBar();
        if (resetText == true) {
            etComment.setText("");
        }
        etComment.setHint("说点什么...");
    }

    public interface InputBarViewListener{
        public void onEditTextFocusChange(boolean hasFocus);
        public void send(String content);
        /**返回true将不回调send方法*/
        public boolean beforeClickSend();
        public void onResetInputBar();
    }

    private class LastComment{
        long videoId;
        String comment;
        long publishTime;
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        View parent=(View)(etComment.getParent());
        parent.requestFocus();
        KeyboardUtil.hideKeyboard(etComment);
    }

    /**
     * 显示软键盘
     *
     * @param et 将要通过软键盘进行输入的EditText
     */
    public void showKeyboard() {
        etComment.requestFocus();
        KeyboardUtil.showKeyboard(etComment);
    }

    public boolean isKeyboardShow() {
        return KeyboardUtil.isKeyboardShow(etComment);
    }
}
