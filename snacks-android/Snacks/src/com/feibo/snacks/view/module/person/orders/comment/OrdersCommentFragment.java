package com.feibo.snacks.view.module.person.orders.comment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.orders.paid.OrdersCommentManager;
import com.feibo.snacks.manager.global.orders.opteration.OrdersOperationManager;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

/**
 * Created by hcy on 2015/7/21.
 */
public class OrdersCommentFragment extends BaseTitleFragment {

    public static final String ORDERS_COMMENT_RESULT = "result";
    public static final String ORDERS_ID = "orders_id";

    private View root;
    private ListView listView;
    private View noUseNameView;
    private Button commentBtn;

    private OrdersCommentAdapter adapter;
    private OrdersCommentManager manager;
    private List<CartItem> items;

    private AbsLoadingView absLoadingView;
    private String ordersId;
    private EditText edit;
    private boolean noUseName = true;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_comment, null);
        ordersId = getArguments().getString(ORDERS_ID);
        initWidget();
        initLoading();
        initListener();

        return root;
    }

    private void initLoading() {
        absLoadingView = new AbsLoadingView(listView) {
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
                refreshView();
            }
        };
        absLoadingView.setLauncherPositon(2);
        OrdersCommentManager.setLoadingView(absLoadingView);
        OrdersCommentManager.setOrdersId(ordersId);
        manager = OrdersCommentManager.instance();
        manager.loadData();
    }


    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP) {
            RemindControl.showCommentExitRemind(getActivity(), new RemindControl.OnRemindListener() {
                @Override
                public void onConfirm() {
                    getActivity().finish();
                }

                @Override
                public void onCancel() {

                }
            });
            return;
        }
        super.onKeyDown(keyCode, event);
    }

    private void initListener() {
        listView.setFocusable(false);
        getTitleBar().leftPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindControl.showCommentExitRemind(getActivity(), new RemindControl.OnRemindListener() {
                    @Override
                    public void onConfirm() {
                        getActivity().finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
        noUseNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noUseName = !noUseName;
                noUseNameView.setSelected(noUseName);
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = listView.getChildCount();
                if (count < 1) {
                    return;
                }
                boolean sendComment = true;
                sendComment = isSendComment(count, sendComment);
                if (sendComment) {
                    RemindControl.showProgressDialog(getActivity(), null, null);
                    OrdersOperationManager.sendOrdersComment(noUseName, items, new ILoadingListener() {
                        @Override
                        public void onSuccess() {
                            if (getActivity() == null) {
                                return;
                            }
                            RemindControl.hideProgressDialog();
                            Toast.makeText(getActivity(), "评价成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra(OrdersCommentFragment.ORDERS_COMMENT_RESULT, true);
                            getActivity().setResult(LaunchUtil.REQUEST_ORDERS_COMMENT, intent);
                            getActivity().finish();
                        }

                        @Override
                        public void onFail(String failMsg) {
                            RemindControl.hideProgressDialog();
                            if (getActivity() == null) {
                                return;
                            }
                            RemindControl.showSimpleToast(getActivity(), failMsg);
                        }
                    });
                }
            }
        });
    }

    private boolean isSendComment(int count, boolean sendComment) {
        int unEditCount = 0;
        for (int i = 0; i < count; i++) {
            View childView = listView.getChildAt(i);
            OrdersCommentAdapter.OdersCommentHolder holder = (OrdersCommentAdapter.OdersCommentHolder) childView.getTag();
            String content = holder.commentEdit.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                unEditCount++;
                sendComment = false;
            } else {
                items.get(i).commentContent = content;
            }
        }
        if (unEditCount == count) {
            RemindControl.showSimpleToast(getActivity(), R.string.orders_comment_all_empty);
        } else if (!sendComment){
            RemindControl.showSimpleToast(getActivity(), R.string.orders_comment_exit_one_empty);
        }
        return sendComment;
    }

    private void refreshView() {
        createAdapter();
        items = manager.getData(manager.getDataType());
        for (CartItem item : items) {
            item.isFouce = false;
        }
        adapter.setItems(items);
        listView.setAdapter(adapter);
    }

    private void createAdapter() {
        adapter = new OrdersCommentAdapter(getActivity());
        adapter.setOnEditCommentListener(new OrdersCommentAdapter.OnEditCommentListener() {
            @Override
            public void editComment(final int i) {
                edit = RemindControl.showEditTextRemind(getActivity(),getString(R.string.str_comment_edit_dialog_title), items.get(i).commentContent,getString(R.string.str_comment_edit_dialog_hint), new RemindControl.OnRemindListener() {
                    @Override
                    public void onConfirm() {
                        if (edit == null) {
                        }
                        items.get(i).commentContent = edit.getText().toString().trim();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                focusInput();
            }

            private void focusInput() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        edit.setFocusableInTouchMode(true);
                        edit.requestFocus();
                        InputMethodManager m = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }, 300);
            }
        });
    }

    private void initWidget() {
        listView = (ListView) root.findViewById(R.id.fragment_orders_comment_list);
        noUseNameView = root.findViewById(R.id.fragment_orders_comment_use_name);
        commentBtn = (Button) root.findViewById(R.id.item_orders_send_comment);
        noUseNameView.setSelected(noUseName);
        TextView title = (TextView) getTitleBar().title;
        title.setText("发表评价");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        absLoadingView = null;
        manager.release();
        manager = null;
        listView = null;
        root = null;
    }
}
