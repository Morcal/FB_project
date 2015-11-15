package com.feibo.joke.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.widget.LToggleButton.OnStateChangeListener;

public class BaseItemLayout extends LinearLayout implements OnClickListener {

    private final static int NULL = 0;
    private final static int HINT_UNMBLE = 1;
    private final static int HINT_ROUND = 2;
    private final static int HINT_ROUND_TITLE = 3;

    private final static int SETTING_SWITCH = 4;

    private String messageHint;

    private boolean showLine;
    private TextView hintNum;
//    private View messageHintLayout;
    private TextView messageTextView;

    private View titleHintView;

    private int type;// 类型
    private boolean switchStatu;

    private LToggleButton toggleButton;
    private View rightView;
    
    private OnToggleListener onToggleListene;
    private int toggleButtonId;
    
    private OnClickListener listener;

    @SuppressLint("NewApi")
    public BaseItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public BaseItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public void init(Context context, AttributeSet attrs, int defStyle) {
        float defaultHeight = getContext().getResources().getDimension(R.dimen.h45);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BaseItemLayout, defStyle, 0);
        type = a.getInt(R.styleable.BaseItemLayout_hintType, NULL);
        showLine = a.getBoolean(R.styleable.BaseItemLayout_showBottomLine, false);
        messageHint = a.getString(R.styleable.BaseItemLayout_messageHintTitle);
        switchStatu = a.getBoolean(R.styleable.BaseItemLayout_switchStatu, true);
        Drawable drawable = a.getDrawable(R.styleable.BaseItemLayout_itemDrawable);
        String title = a.getString(R.styleable.BaseItemLayout_itemTitle);
        boolean showArrow = a.getBoolean(R.styleable.BaseItemLayout_showArrow, true);
        float height = a.getDimension(R.styleable.BaseItemLayout_vheight, defaultHeight);
        a.recycle();

        View root = LayoutInflater.from(context).inflate(R.layout.base_item_layout, null);
        rightView = root.findViewById(R.id.layout_right);
        toggleButton = (LToggleButton) root.findViewById(R.id.toggleButton);
        View arrow = root.findViewById(R.id.img_arrow);
        View layoutTxHint = root.findViewById(R.id.layout_tx_hint);

        if (defaultHeight != height) {
            View cv = root.findViewById(R.id.base_item_layout);
            LinearLayout.LayoutParams lp = (LayoutParams) cv.getLayoutParams();
            lp.height = (int) height;
            cv.setLayoutParams(lp);
        }

        TextView tx = (TextView) root.findViewById(R.id.title);
        tx.setText(title);
        if(drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tx.setCompoundDrawables(drawable, null, null, null);
        } else {
            tx.setCompoundDrawablePadding(0);
            tx.setCompoundDrawables(null, null, null, null);
        }

        if (type == SETTING_SWITCH) {
            rightView.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.initState(switchStatu);
            toggleButton.setOnStateChangeListener(new OnStateChangeListener() {

                @Override
                public void onStateChange(boolean open) {
                    if(onToggleListene != null) {
                        onToggleListene.onStateChange(toggleButtonId, open);
                    }
                }
            });
        } else {
            rightView.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.GONE);
            hintNum = (TextView) root.findViewById(R.id.hint_num);
            messageTextView = (TextView) root.findViewById(R.id.message_tx);
            titleHintView = root.findViewById(R.id.title_hint);
            if(!showArrow) {
                arrow.setVisibility(View.GONE);
                setViewToRight(layoutTxHint);
            }

            if (type == HINT_UNMBLE) {
                hintNum.setVisibility(View.VISIBLE);
                titleHintView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
            } else if (type == HINT_ROUND) {
                hintNum.setVisibility(View.GONE);
                titleHintView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
            } else if (type == HINT_ROUND_TITLE) {
                hintNum.setVisibility(View.GONE);
                messageTextView.setText(messageHint == null ? "默认" : messageHint);
                titleHintView.setVisibility(View.VISIBLE);
                messageTextView.setVisibility(View.VISIBLE);
            } else {
                hintNum.setVisibility(View.GONE);
                titleHintView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
            }
        }
        root.findViewById(R.id.line).setVisibility(showLine ? View.VISIBLE : View.GONE);

        attachViewToParent(root, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        
        this.setOnClickListener(this);
    }

    /** 设置提示语和红点显示 msg为空时不显示文字*/
    public void setMessageHintTitle(boolean hasNew, String msg) {
        if (type != HINT_ROUND_TITLE) {
            return;
        }
        if(hasNew) {
            titleHintView.setVisibility(View.VISIBLE);
        }  else {
            titleHintView.setVisibility(View.GONE);
        }

        if(!StringUtil.isEmpty(msg)) {
            messageTextView.setText(msg);
            messageTextView.setVisibility(View.VISIBLE);
        }else{
            messageTextView.setVisibility(View.GONE);
        }
        invalidate();
    }
    
    public void initToggleButton(boolean open) {
        if(toggleButton != null) {
            toggleButton.initState(open);
        }
    }
    
    public void setToggleButton(boolean open) {
        if(toggleButton != null) {
            toggleButton.setToggleState(open);
        }
    }
    
    public void setToggleButtonEnable(boolean enable) {
        if(toggleButton != null) {
            toggleButton.setEnabled(enable);
        }
    }
    
    public void getToggleButtonEnable(boolean enable) {
        if(toggleButton != null) {
            toggleButton.isEnabled();
        }
    }
    
    public void setOnToggleStateChangeListener(OnToggleListener onToggleListene, int toggleButtonId) {
        this.onToggleListene = onToggleListene;
        this.toggleButtonId = toggleButtonId;
    }
    
    public interface OnToggleListener{
        public void onStateChange(int viewId, boolean open);
    }
    
    private void setViewToRight(View view) {
        RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        view.setLayoutParams(lp);
    }
    
    /** 设置消息数量 */
    public void setHintNum(int num) {
        if (type != HINT_UNMBLE) {
            return;
        }
        if (num <= 0) {
            hintNum.setVisibility(View.GONE);
        } else {
            hintNum.setVisibility(View.VISIBLE);
            if(num > 99) {
                hintNum.setText("99+");
            } else {
                hintNum.setText(String.valueOf(num));
            }
        }
    }
    public void setHintNum(String num) {
        if (type != HINT_UNMBLE) {
            return;
        }
        if (num == null) {
            hintNum.setVisibility(View.GONE);
        } else {
            hintNum.setVisibility(View.VISIBLE);
            hintNum.setText(num);
        }
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
            this.setEnabled(false);
            postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    setEnabled(true);
                }
            }, 1000);
        }
    }
    
    public void setOnBaseItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

}
