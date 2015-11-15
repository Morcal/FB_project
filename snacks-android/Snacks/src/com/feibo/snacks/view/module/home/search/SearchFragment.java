package com.feibo.snacks.view.module.home.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.SearchGuide;
import com.feibo.snacks.manager.module.home.SearchManager;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFragment extends BaseTitleFragment{

    public static final String PARAM_KEYWORD = "searchWord";

    @Bind(R.id.list_search)
    ListView historyListView;

    @Bind(R.id.edit_keyword)
    EditText inputText;

    @Bind(R.id.btn_clear_input)
    ImageView clearInputBtn;

    @Bind(R.id.btn_search)
    View searchBtn;

    @Bind(R.id.btn_cancel)
    View cancelBtn;

    private TextView clearHistoryBtn;

    private SearchAdapter adapter;
    private List<SearchGuide> historyGuides;

    @Override
    public int onCreateTitleBar() {
        return 0;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, view);
        initWidget();
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inputText.isFocusable()) {
            showInputMethod();
        }
        handleClearInput();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideInputMethod();
        SearchManager.reSave(getActivity(), historyGuides);
    }

    private void initWidget() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_history_footer, null);
        clearHistoryBtn = (TextView) footerView.findViewById(R.id.btn_clear_history);
        historyListView.addFooterView(footerView);
        inputText.setHintTextColor(getActivity().getResources().getColor(R.color.c3));

        adapter = new SearchAdapter(getActivity());
        adapter.setListener(deleteListener);
        historyListView.setAdapter(adapter);
    }

    private void initData() {
        historyGuides = SearchManager.getHistorySearchData();
        if (historyGuides.size() == 0) {
            historyListView.setVisibility(View.GONE);
            clearHistoryBtn.setVisibility(View.GONE);
            return;
        }

        historyListView.setVisibility(View.VISIBLE);
        clearHistoryBtn.setVisibility(View.VISIBLE);

        adapter.setItems(historyGuides);
        adapter.notifyDataSetChanged();
    }

    private void initListener() {
        historyListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= historyGuides.size()) {
                    return;
                }
                SearchGuide searchGuide = historyGuides.get(position);
                if (searchGuide == null) {
                    return;
                }
                addHistoryTop(searchGuide.guideWord, searchGuide);
                Bundle bundle = new Bundle();
                bundle.putString(PARAM_KEYWORD, searchGuide.guideWord);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, SearchDetailFragment.class, bundle);
            }
        });

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearInputBtn.setVisibility(View.VISIBLE);
                    showSearchBtn();
                } else {
                    clearInputBtn.setVisibility(View.GONE);
                    showCancelBtn();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleSearch();
                    return true;
                }
                return false;
            }
        });

        clearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClearHistory();
            }
        });
    }

    private void addHistoryTop(String guideWord, SearchGuide searchGuide) {
        removeSameWord(guideWord);
        historyGuides.add(0, searchGuide);
    }

    private void removeSameWord(String guideWord) {
        for (int i = 0; i < historyGuides.size(); i++) {
            if (historyGuides.get(i).guideWord.equals(guideWord)) {
                historyGuides.remove(i);
                return;
            }
        }
    }

    private void showSearchBtn(){
        searchBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.GONE);
    }

    private void showCancelBtn(){
        searchBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.VISIBLE);
    }


    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }

    private void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    // 搜索
    @OnClick(R.id.btn_search)
    public void handleSearch() {
        String guideWord = inputText.getText().toString().trim();
        if (guideWord.equals("") || guideWord == null) {
            RemindControl.showSimpleToast(getActivity(), R.string.search_key_null);
            return;
        }

        hideInputMethod();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_KEYWORD, guideWord);
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, SearchDetailFragment.class, bundle);

        SearchGuide searchGuide = new SearchGuide(inputText.getText().toString().trim());
        addHistoryTop(guideWord, searchGuide);
    }

    // 清除输入
    @OnClick(R.id.btn_clear_input)
    public void handleClearInput(){
        inputText.setText("");
        showCancelBtn();
    }

    // 清除历史记录
    public void handleClearHistory() {
        historyGuides.clear();
        adapter.setItems(historyGuides);
        adapter.notifyDataSetChanged();
        clearHistoryBtn.setVisibility(View.GONE);
    }

    // 返回
    @OnClick({R.id.btn_back, R.id.btn_cancel})
    public void handleQuit(){
        getActivity().finish();
    }

    private OnDeleteListener deleteListener = new OnDeleteListener() {
        @Override
        public void onDelete(int position) {
            historyGuides.remove(position);
            adapter.setItems(historyGuides);
            adapter.notifyDataSetChanged();
            if (historyGuides.size() == 0) {
                clearHistoryBtn.setVisibility(View.GONE);
            }
        }
    };

    public interface OnDeleteListener {
        void onDelete(int position);
    }
}
