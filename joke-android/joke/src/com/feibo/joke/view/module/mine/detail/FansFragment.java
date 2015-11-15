package com.feibo.joke.view.module.mine.detail;

import android.view.View;
import android.widget.TextView;

import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.manager.list.UsersFansManager;
import com.feibo.joke.model.User;
import com.feibo.joke.view.group.GroupConfig;

public class FansFragment extends BaseUserListFragment {

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText("粉丝");
        getTitleBar().rightPart.setVisibility(View.GONE);
    }

    @Override
    public AbsListManager<User> getGroupManager(long userId) {
        return new UsersFansManager(userId);
    }

    @Override
    public GroupConfig getGroupConfig() {
        return GroupConfig.create(GroupConfig.GROUP_USER_FANS);
    }

}
