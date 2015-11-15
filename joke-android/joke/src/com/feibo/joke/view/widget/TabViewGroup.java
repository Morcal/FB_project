package com.feibo.joke.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.feibo.joke.R;

public class TabViewGroup extends LinearLayout implements OnClickListener{

    public static final int STATU_HANDPICK = 0;
    public static final int STATU_FOUND = 1;
    public static final int STATU_DYNAMIC = 2;

    private int currentScene = -1;
    
    private TabView handpickTab;
    private TabView foundTab;
    private TabView dynamicTab;
    
    private ITabSceneChangeListener listener;
    
    public TabViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.tab_view_group, null);
        
        root.findViewById(R.id.tab_layout_handpick).setOnClickListener(this);
        root.findViewById(R.id.tab_layout_found).setOnClickListener(this);
        root.findViewById(R.id.tab_layout_dynamic).setOnClickListener(this);
        
        handpickTab = (TabView) root.findViewById(R.id.tabview_handpick);
        foundTab = (TabView) root.findViewById(R.id.tabview_found);
        dynamicTab = (TabView) root.findViewById(R.id.tabview_dynamic);
        
        int color = getContext().getResources().getColor(R.color.c9_white);
        handpickTab.setColor(color, color, color);
        foundTab.setColor(color, color, color);
        dynamicTab.setColor(color, color, color);
        
        attachViewToParent(root, 0, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
    public void setRedHintVisible(int scene, boolean visible) {
        switch (scene) {
        case STATU_HANDPICK:
            handpickTab.setRedHintVisible(visible);
            break;
        case STATU_FOUND:
            foundTab.setRedHintVisible(visible);
            break;
        case STATU_DYNAMIC:
            dynamicTab.setRedHintVisible(visible);
            break;
        }
    }

    public void selectOnScene(int scene) {
        selectOnScene(false, scene);
    }
    
    private void selectOnScene(boolean isListener, int scene) {
        if(currentScene == scene) {
            if(isListener) {
                listener.onFreshView();
            }
            return;
        }
        currentScene = scene;
        switch (scene) {
        case STATU_HANDPICK:
            handpickTab.setSelect(true);
            foundTab.setSelect(false);
            dynamicTab.setSelect(false);
            break;
        case STATU_FOUND:
            handpickTab.setSelect(false);
            foundTab.setSelect(true);
            dynamicTab.setSelect(false);
            break;
        case STATU_DYNAMIC:
            handpickTab.setSelect(false);
            foundTab.setSelect(false);
            dynamicTab.setSelect(true);
            break;
        }
        if(isListener) {
            listener.tabSceneChanged(scene);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tab_layout_handpick:
            selectOnScene(true, STATU_HANDPICK);
            break;
        case R.id.tab_layout_found:
            selectOnScene(true, STATU_FOUND);
            break;
        case R.id.tab_layout_dynamic:
            selectOnScene(true, STATU_DYNAMIC);
            break;
        }
    }
    
    public void setTabSceneChangeLisener(ITabSceneChangeListener listener) {
        this.listener = listener;
    }
    
    public interface ITabSceneChangeListener{
        /** tab标签选项改变 */
        public void tabSceneChanged(int tabScene);
        
        public void onFreshView();
    }
    
}
