package com.feibo.joke.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.feibo.joke.R;

public class TabDetailGroup extends LinearLayout implements OnClickListener {

    public static final int FROM_MINE = 1;
    public static final int FROM_OTHER = 2;

    public static final int TAB_WORKS = 0;
    public static final int TAB_LOVES = 1;
    public static final int TAB_ATTENTIONS = 2;
    public static final int TAB_FANS = 3;

    private float linePadding;

    private TabView tabWorks;
    private TabView tabLoves;
    private TabView tabAttention;
    private TabView tabFans;
    private View layoutLoves;

    private int normalColor;
    private int pressColor;
    private int lineColor;

    private IOnItemSelect listener;

    private int from;

    public TabDetailGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public TabDetailGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.TabViewDetailGroup, defStyle, 0);
        linePadding = t.getDimension(R.styleable.TabViewDetailGroup_textLinePadding,
                getResources().getDimension(R.dimen.p3));
        lineColor = t.getColor(R.styleable.TabViewDetailGroup_lineColor, getResources().getColor(R.color.c9_white));
        pressColor = t.getColor(R.styleable.TabViewDetailGroup_textPressColor, getResources()
                .getColor(R.color.c9_white));
        normalColor = t.getColor(R.styleable.TabViewDetailGroup_textNormalColor,
                getResources().getColor(R.color.c9_white));
        from = t.getInt(R.styleable.TabViewDetailGroup_from, FROM_MINE);
        t.recycle();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.tab_usertetail_view, null);

        View layoutWorks = root.findViewById(R.id.tab_layout_wokes);
        layoutLoves = root.findViewById(R.id.tab_layout_loves);
        View layoutAttention = root.findViewById(R.id.tab_layout_attention);
        View layoutFans = root.findViewById(R.id.tab_layout_fans);

        if (from == FROM_OTHER) {
            layoutLoves.setVisibility(View.GONE);
        }

        layoutWorks.setOnClickListener(this);
        layoutLoves.setOnClickListener(this);
        layoutAttention.setOnClickListener(this);
        layoutFans.setOnClickListener(this);

        tabWorks = (TabView) root.findViewById(R.id.tabview_works);
        tabLoves = (TabView) root.findViewById(R.id.tabview_loves);
        tabAttention = (TabView) root.findViewById(R.id.tabview_attention);
        tabFans = (TabView) root.findViewById(R.id.tabview_fans);

        tabWorks.setColor(normalColor, pressColor, lineColor);
        tabLoves.setColor(normalColor, pressColor, lineColor);
        tabAttention.setColor(normalColor, pressColor, lineColor);
        tabFans.setColor(normalColor, pressColor, lineColor);

        tabWorks.setLinePadding(linePadding);
        tabLoves.setLinePadding(linePadding);
        tabAttention.setLinePadding(linePadding);
        tabFans.setLinePadding(linePadding);
        attachViewToParent(root, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setFrom(int from) {
        this.from = from;
        if (from == FROM_OTHER) {
            layoutLoves.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tab_layout_wokes:
            tabWorks.setSelect(true);
            tabLoves.setSelect(false);
            tabAttention.setSelect(false);
            tabFans.setSelect(false);
            if (listener != null) {
                listener.onItemClick(TAB_WORKS);
            }
            break;
        case R.id.tab_layout_loves:
//            tabWorks.setSelect(false);
//            tabLoves.setSelect(true);
//            tabAttention.setSelect(false);
//            tabFans.setSelect(false);
            if (listener != null) {
                listener.onItemClick(TAB_LOVES);
            }
            break;
        case R.id.tab_layout_attention:
//            tabWorks.setSelect(false);
//            tabLoves.setSelect(false);
//            tabAttention.setSelect(true);
//            tabFans.setSelect(false);
            if (listener != null) {
                listener.onItemClick(TAB_ATTENTIONS);
            }
            break;
        case R.id.tab_layout_fans:
//            tabWorks.setSelect(false);
//            tabLoves.setSelect(false);
//            tabAttention.setSelect(false);
//            tabFans.setSelect(true);
            if (listener != null) {
                listener.onItemClick(TAB_FANS);
            }
            break;
        }
    }

    /**
     * 有则设置，没有默认为0
     * 
     * @param works 片儿数
     * @param loves 爱过数
     * @param attentions 关注数
     * @param fans 粉丝数
     */
    public void setAllNum(int works, int loves, int attentions, int fans) {
        if (works >= 0) {
            tabWorks.setNum(works);
        }
        if (loves >= 0) {
            tabLoves.setNum(loves);
        }
        if (attentions >= 0) {
            tabAttention.setNum(attentions);
        }
        if (fans >= 0) {
            tabFans.setNum(fans);
        }
    }

    public void setOnItemClickListener(IOnItemSelect listener) {
        this.listener = listener;
    }

    public interface IOnItemSelect {
        public void onItemClick(int pos);
    }

}
