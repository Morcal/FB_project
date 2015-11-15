package com.feibo.joke.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feibo.joke.R;

public class TabView extends LinearLayout{

    private static final int DEFAULT = 0;
    private static final int USERDTAIL = 1;

    private int type;
    private String numText;
    private String tabText;

    private int normalColor;
    private int pressColor;

    private float hindpickTabSize = 17;
    private float detailNumSize = 14;
    private float detailTabSize = 13;

    private boolean isSelect;
    private boolean isMessageHint;

    private TextView tabTextView;
    private TextView numTextView;
    private View line;
    private View roundRed;
    private View divisionLine;
    private View content;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.TabView, defStyle, 0);
        type = t.getInt(R.styleable.TabView_type, DEFAULT);


        isSelect = t.getBoolean(R.styleable.TabView_select, false);
        isMessageHint = t.getBoolean(R.styleable.TabView_messageHint, false);
        tabText = t.getString(R.styleable.TabView_tabText);
        numText = t.getString(R.styleable.TabView_numText);
        t.recycle();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.tab_view, null);

        content = root.findViewById(R.id.tab_content);
        numTextView  = (TextView) root.findViewById(R.id.tab_num);
        tabTextView = (TextView) root.findViewById(R.id.tab_title);

        line = root.findViewById(R.id.tab_line);
        divisionLine = root.findViewById(R.id.division_line);
        roundRed = root.findViewById(R.id.tab_round);

        initView();

        attachViewToParent(root, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int width = content.getMeasuredWidth();
        android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) line.getLayoutParams();
        lp.width = width;
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        line.setLayoutParams(lp);
    }
    
    public void setLinePadding(float linePadding) {
        content.setPadding((int)linePadding, 0, (int)linePadding, 0);
    }

    public void setColor(int normalColor, int pressColor, int lineColor) {
        this.normalColor = normalColor;
        this.pressColor = pressColor;

        line.setBackgroundColor(lineColor);
        tabTextView.setTextColor(normalColor);
        numTextView.setTextColor(normalColor);

        setSelect(isSelect);
    }
    
    public void setRedHintVisible(boolean visible) {
        roundRed.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void initView() {
        line.setVisibility(isSelect ? View.VISIBLE : View.INVISIBLE);
        setRedHintVisible(isMessageHint);

        if(type == DEFAULT) {
            tabTextView.setText(tabText);
            tabTextView.setTextSize(hindpickTabSize);
            divisionLine.setVisibility(View.GONE);
        } else if(type == USERDTAIL) {
            divisionLine.setVisibility(View.VISIBLE);
            tabTextView.setText(tabText == null ? "默认" : tabText);
            tabTextView.setTextSize(detailTabSize);
            numTextView.setText(numText == null ? "0" : numText);
            numTextView.setTextSize(detailNumSize);
        }
        numTextView.setVisibility(type == DEFAULT ? View.GONE : View.VISIBLE);
        setSelect(isSelect);
    }

    public void setSelect(boolean select) {
        line.setVisibility(select ? View.VISIBLE : View.INVISIBLE);
        if(select) {
            tabTextView.setTextColor(pressColor);
            numTextView.setTextColor(pressColor);
        } else {
            tabTextView.setTextColor(normalColor);
            numTextView.setTextColor(normalColor);
        }
    }
    
    public void setNum(int n){
        if(n >= 0){
            numTextView.setText(n+"");
        }
    }

}
