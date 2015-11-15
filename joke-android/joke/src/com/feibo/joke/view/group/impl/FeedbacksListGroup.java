package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feibo.joke.R;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.view.group.BasePullListGroup;

public class FeedbacksListGroup extends BasePullListGroup<Feedback> {

    public FeedbacksListGroup(Context context) {
        super(context, false, false);
    }

    @Override
    public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_feedback, null);
        return parent;
    }

    public ListView getListView() {
        return pullToRefreshListView.getListView();
    }
}
