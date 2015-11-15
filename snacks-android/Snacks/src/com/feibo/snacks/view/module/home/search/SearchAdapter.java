package com.feibo.snacks.view.module.home.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.SearchGuide;
import com.feibo.snacks.view.module.home.search.SearchFragment.OnDeleteListener;

@SuppressLint("InflateParams")
public class SearchAdapter extends BaseSingleTypeAdapter<SearchGuide> {

    private OnDeleteListener listener;

    public SearchAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search, null);
            viewHolder.historyWord = (TextView) convertView.findViewById(R.id.search_history_word);
            viewHolder.delHistory = (ImageButton) convertView.findViewById(R.id.search_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SearchGuide guide = getItem(position);
        if (guide != null) {
            viewHolder.historyWord.setText(guide.guideWord);
        }
        initListener(viewHolder,position);
        return convertView;
    }

    private void initListener(ViewHolder viewHolder, final int position) {
        viewHolder.delHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(position);
            }
        });
    }

    private class ViewHolder{
        private TextView historyWord;
        private ImageButton delHistory;
    }

    public void setListener(OnDeleteListener listener) {
        this.listener = listener;
    }
}
