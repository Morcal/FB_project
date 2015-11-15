package com.feibo.joke.view.module.mine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.IListManager;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.OperateManager.OnAttentionOneKeyListener;
import com.feibo.joke.view.adapter.OnKeyAttentionAdapter;
import com.feibo.joke.view.group.AbsListGroup.OnRefreshDataListener;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.util.ToastUtil;

@SuppressWarnings("rawtypes")
public abstract class BaseAttentionAllFragment extends BaseLoginFragment implements OnClickListener {

    private boolean isWeibo;

    private OnKeyAttentionAdapter adapter;
    private IListManager manager;
    private BasePullListGroup group;

    ProgressDialog dialog;
    
    public BaseAttentionAllFragment(boolean isWeibo) {
        this.isWeibo = isWeibo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View containChildView() {
        manager = getGroupManager();
        adapter = getGroupAdapter();
        group = getGroup();

        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(getGroupConfig());
        group.setOnRefreshDataListener(onRefreshDataListener);

        group.onCreateView();

        initListener(adapter);
        initPrepared();

        return group.getRoot();
    }

    public BasePullListGroup getListGroup() {
        return group;
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        ((TextView) titleBar.title).setText(isWeibo ? R.string.weibo_friend : R.string.add_funny_friend_text);
        titleBar.tvHeadRight.setText(R.string.one_key);
        titleBar.rightPart.setOnClickListener(this);
    }
    
    OnRefreshDataListener onRefreshDataListener = new OnRefreshDataListener() {

        @Override
        public void onStart(boolean isRefresh) {
            if(getActivity() == null) {
                return;
            }
            getTitleBar().rightPart.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFinish(boolean isRefresh, boolean succuss) {
            if(getActivity() == null) {
                return;
            }
            if(succuss) {
                if(manager.hasData() && adapter.getHasAttention()) {
                    getTitleBar().rightPart.setVisibility(View.VISIBLE);
                }
            } else {
                getTitleBar().rightPart.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.head_right) {
            if(!adapter.getHasAttention()) {
                ToastUtil.showSimpleToast("暂时没有"+ (isWeibo ? "好友" : "达人") +"可关注哦!");
                getTitleBar().rightPart.setVisibility(View.GONE);
                return;
            }
            // 一键关注相关操作
            OperateManager.attentAllUser(isWeibo, new OnAttentionOneKeyListener() {

                @Override
                public void onStart() {
                    getTitleBar().rightPart.setEnabled(false);
                    dialog = new ProgressDialog(getActivity());
                    dialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getTitleBar().rightPart.setEnabled(true);
                        }
                    });
                    dialog.setMessage("正在关注中..");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

                @Override
                public void onFinish(boolean success) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if(success && adapter != null) {
                        adapter.setOneKeySuccess();
                    }
                    if(getTitleBar() != null && getTitleBar().rightPart != null) {
                        getTitleBar().rightPart.setVisibility(success ? View.GONE : View.VISIBLE);
                    }
                    ToastUtil.showSimpleToast("一键关注" + (success ? "成功" : "失败"));
                }
            });
        }
    }

    @Override
    public void onReleaseView() {
        if (manager instanceof BaseManager) {
            ((BaseManager) manager).onDestroy();
        }
        group.onDestroyView();
    }

    public void initPrepared() {
    }

    public void initListener(OnKeyAttentionAdapter adapter) {
    }

    public abstract OnKeyAttentionAdapter getGroupAdapter();

    public abstract IListManager getGroupManager();

    public abstract BasePullListGroup getGroup();

    public abstract GroupConfig getGroupConfig();

}
