package com.feibo.snacks.manager.module.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.SearchGuide;
import com.feibo.snacks.util.SPHelper;

public class SearchManager {

    public static List<SearchGuide> getHistorySearchData() {
        List<SearchGuide> historyGuides = new ArrayList<SearchGuide>();
        String history = SPHelper.getSearchHistory();
        if ("No History Search Word".equals(history)) {
            return historyGuides;
        }
        // 用逗号分割内容返回数组
        String[] history_arr = history.split(",");
        for (int i = 0; i < history_arr.length; i++) {
            SearchGuide searchGuide = new SearchGuide(history_arr[i]);
            historyGuides.add(searchGuide);
        }
        return historyGuides;
    }

    public static void reSave(Context context, List<SearchGuide> historyGuides) {
        SPHelper.clear();
        if (historyGuides.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < historyGuides.size(); i++) {
                SearchGuide searchGuide = historyGuides.get(i);
                if (! builder.toString().equals(searchGuide.guideWord)) {
                    builder.append(searchGuide.guideWord + ",");
                }
            }
            SPHelper.setSearchHistory(builder.toString());
        }
    }
}
