package com.feibo.snacks.view.module.person.setting;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.AboutInfo;
import com.feibo.snacks.manager.module.person.AboutManager;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.PullVirtualListView;

public class AboutFragment extends BaseTitleFragment {

    private TextView aboutDesc;
    private TextView aboutService;
    private AboutManager manager;
    private View root;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateContentView() {
        RemindControl.cancelToast();
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_about, null);
        TextView title = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        title.setText("关于零食小喵");

        aboutDesc = (TextView) root.findViewById(R.id.about_desc);
        aboutService = (TextView) root.findViewById(R.id.about_service);

        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

//        mPull = new PullVirtualListView(getActivity());
//        mPull.setBackgroundColor(getResources().getColor(R.color.c6));
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//        mPull.setLayoutParams(layoutParams);
//        mPull.setView(root);
        initLoading();
//        return mPull;
        return root;
    }

    private void initLoading() {
        AbsLoadingView absLoadingView = new AbsLoadingView() {
            @Override
            public View getLoadingParentView() {
                return root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                manager.loadData();
            }

            @Override
            public void fillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                AboutInfo aboutInfo = manager.getData(BaseDataType.PersonDataType.ABOUT);
                if(aboutInfo != null){
                    if(!TextUtils.isEmpty(aboutInfo.desc)){
                        aboutDesc.setText(aboutInfo.desc);
                    }
                    if(!TextUtils.isEmpty(aboutInfo.service)){
                        aboutService.setText(aboutInfo.service);
                    }
                }
            }

//            @Override
//            public void hideLoadingView() {
//                super.hideLoadingView();
//                RemindControl.hidProgressDialog();
//            }

            @Override
            public void showFailView(String failMsg) {
                if(failMsg.equals(NetResult.NOT_DATA_STRING)) {
                    hideLoadingView();
                    return;
                }
                super.showFailView(failMsg);
            }
        };
        manager = new AboutManager(absLoadingView);
        manager.loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
}
