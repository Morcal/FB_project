package com.feibo.joke.view.module.mine;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.DraftListManager;
import com.feibo.joke.video.VideoShareFragment;
import com.feibo.joke.video.manager.VideoDraftManager;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.DraftListAdapter;
import com.feibo.joke.view.adapter.DraftListAdapter.OnDeleteClickListener;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;
import com.feibo.joke.view.group.AbsListGroup.OnRefreshDataListener;
import com.feibo.joke.view.group.AbsPullWaterGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView.OnItemClickListener;

public class VideoDraftFragment extends BaseTitleFragment implements OnItemClickListener {

    private DraftListManager manager;
    private DraftListAdapter adapter;
    private AbsPullWaterGroup<Draft> group;
    
    @Override
    public View containChildView() {
        manager = new DraftListManager();
        adapter = new DraftListAdapter(getActivity());
        group = new AbsPullWaterGroup<Draft>(getActivity(), false, false, false) {};
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VIDEO_DRAFT));
        group.onCreateView();
        
        initListener();
        
        return group.getRoot();
    }
    
    public void initListener() {
        group.setOnItemClickListener(this);
        group.setOnRefreshDataListener(new OnRefreshDataListener() {

            @Override
            public void onStart(boolean isRefresh) {
                if(getActivity() == null) {
                    return;
                }
                getTitleBar().rightPart.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish(boolean isRefresh, boolean succuss) {
                if(adapter != null && adapter.getCount() == 0) {
                    getTitleBar().rightPart.setVisibility(View.INVISIBLE);
                } else {
                    getTitleBar().rightPart.setVisibility(View.VISIBLE);
                    getTitleBar().ivHeadRight.setVisibility(View.INVISIBLE);
                    getTitleBar().tvHeadRight.setVisibility(View.VISIBLE);
                    setTitlebar(adapter.isEditState());
                }
            }
        });
        adapter.setOnDeleteClickListener(new OnDeleteClickListener() {
            
            @Override
            public void onDelete(final Draft draft) {
                RemindDialog deleteDialog = RemindDialog.show(getActivity(), new RemindSource("真的要删掉我吗？"), true);
                deleteDialog.setOnDialogClickListener(new OnDialogClickListener() {
                    
                    @Override
                    public void onConfirm() {
                        VideoDraftManager.getInstance().deleteDraft(draft.videoPath);
                        group.refreshData();
                        ToastUtil.showSimpleToast("删除成功!");
                    }
                    
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    @Override
    public void setTitlebar() {
        ((TextView)getTitleBar().title).setText("草稿箱");
        getTitleBar().rightPart.setVisibility(View.INVISIBLE);
        getTitleBar().rightPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setIsEditState(!adapter.isEditState());
                adapter.notifyDataSetChanged();
                setTitlebar(adapter.isEditState());
            }
        });
    }
    
    private void setTitlebar(boolean isEditState) {
        getTitleBar().tvHeadRight.setText(isEditState ? "完成" : "编辑");
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        group.onDestroyView();
    }
    
    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
        final Draft draft = adapter.getItem(position);
        LaunchUtil.launchSubActivity(getActivity(), VideoShareFragment.class, VideoShareFragment.buildBuilder(draft));
    }

    @Override
    public void onDataChange(int code) {
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_SAVE_VIDEO_DRAFT:
            group.onDataChange(DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE);
            break;
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            setChangeTypeAndFinish(code);
            break;
        }
    }

}
