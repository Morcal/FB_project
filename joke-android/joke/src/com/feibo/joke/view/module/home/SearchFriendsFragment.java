package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.SearchFriendsManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.SearchFriendsAdapter;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;

import fbcore.log.LogUtil;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SearchFriendsFragment extends BaseTitleFragment implements FocusStateView.OnStatuChangeListener {

    private static final String PARAM_KEYWORD = "searchWord";

    private BasePullListGroup<User> group;
    private SearchFriendsAdapter adapter;
    private SearchFriendsManager manager;
    private int attionChangeFlag;
    private String keyWord;

    public static final Bundle bulidBundle(String keyWord) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_KEYWORD, keyWord);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        keyWord = bundle.getString(PARAM_KEYWORD);
    }

    @Override
    public View containChildView() {
        adapter = new SearchFriendsAdapter(getActivity());
        adapter.setOnStatuChangeListener(this);
        manager = new SearchFriendsManager(keyWord);

        group = new BasePullListGroup<User>(getActivity());
        group.setListAdapter(adapter);
        group.setListManager(manager);

        initListener();
        group.onCreateView();

        return group.getRoot();
    }

    private void initListener() {
        group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = adapter.getItem(position);
                boolean isFromMe = UserManager.getInstance().isFromMe((int) user.id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe, user.id));
            }
        });
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText(R.string.relate_friends);
        getTitleBar().rightPart.setVisibility(View.GONE);
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        group.onDestroyView();
    }

    @Override
    public void onStatuChange(boolean isAttention) {
        if (getActivity() == null) {
            return;
        }

        attionChangeFlag = isAttention ? (attionChangeFlag + 1) : (attionChangeFlag - 1);

        Bundle bundle = BundleUtil.buildAttentionCountChangeBundle(getFinishBundle(), attionChangeFlag);
        setFinishBundle(bundle);
        setChangeType(DataChangeEventCode.CHANGE_TYPE_ATTENTION_COUNT_CHANGE);
    }
}
