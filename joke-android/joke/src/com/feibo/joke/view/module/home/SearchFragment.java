package com.feibo.joke.view.module.home;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.SearchManager;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.SearchListAdapter;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.util.KeyboardUtil;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import fbcore.log.LogUtil;
import fbcore.utils.Utils;

/**
 * Created by Administrator on 2015/11/5.
 */
public class SearchFragment extends BaseTitleFragment implements View.OnClickListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String KEYWORD = "";

    private BasePullListGroup searchListGroup;
    private SearchManager searchManager;
    private SearchListAdapter searchListAdapter;
    private EditText editText;
    private Button canclel;
    private ImageView clear;
    private String msg;
    private String keyWord = "";

    public static final Bundle bulidBudle(String keyWord) {
        Bundle bundle = new Bundle();
        bundle.putString(KEYWORD, keyWord);
        return bundle;
    }

    @Override
    public View containChildView() {
        canclel = (Button) getTitleBar().headView.findViewById(R.id.head_right);
        editText = (EditText) getTitleBar().headView.findViewById(R.id.discoivery_edit);
        clear = (ImageView) getTitleBar().headView.findViewById(R.id.btn_clear_input);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            String string = bundle.getString(KEYWORD);
            editText.setText(string);
        }

        initListener();
        searchManager = new SearchManager();
        searchListAdapter = new SearchListAdapter(getActivity());
        searchListGroup = new BasePullListGroup(getActivity(), false, false, false);
        searchListGroup.setListManager(searchManager);
        searchListGroup.setListAdapter(searchListAdapter);

        searchListGroup.onCreateView();

        searchListGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select = (String) parent.getItemAtPosition(position + 1);
                editText.setText(select);
                handleSearch();
            }
        });

        return searchListGroup.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    private void initListener() {
        canclel.setOnClickListener(this);
        clear.setOnClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msg = s.toString();
                // 判断有无网络
                if (!Utils.isNetworkAvailable(getActivity())) {
                    ToastUtil.showSimpleToast(R.string.not_network);
                    return;
                }
                if (msg.equals("") || msg == null) {

                    // searchManager.setMsg("");
                    return;
                } else {
                    searchManager.setMsg(msg);
                    LogUtil.i(TAG, msg);

                    searchListGroup.refreshData();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.i("afterTextChanged", s.toString());
                if (s != null) {
                    searchListGroup.refreshData();
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleSearch();
                    return true;
                }
                return false;
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void handleSearch() {
        // 此处的精确搜索关键字
        keyWord = editText.getText().toString().trim();
        if (keyWord.equals("") || keyWord == null) {
            ToastUtil.showSimpleToast(getActivity(), getString(R.string.search_empty_toast));
            return;
        }
        KeyboardUtil.hideKeyboard(editText);
        LogUtil.i(TAG, keyWord);
        Bundle b = SearchDetailFragment.bulidBundle(keyWord);
        LaunchUtil.launchSubActivity(getActivity(), SearchDetailFragment.class, b);
        // 添加转场动画

        getActivity().onBackPressed();
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.search_head;
    }

    @Override
    public void setTitlebar() {

        editText.setHint(R.string.search_edit_hint);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 100);

    }

    @Override
    public void onReleaseView() {
        searchManager.onDestroy();
        searchListGroup.onDestroyView();
    }

    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        switch (code) {
        case DataChangeEventCode.HANDLE_SHOW_VIEW:

            searchListGroup.onCreateView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.head_right:
            getActivity().finish();
            break;
        case R.id.btn_clear_input:
            editText.setText("");
            break;
        default:
            break;
        }
    }
}
