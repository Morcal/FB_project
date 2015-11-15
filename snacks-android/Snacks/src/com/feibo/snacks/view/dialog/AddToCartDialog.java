package com.feibo.snacks.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Kind;
import com.feibo.snacks.view.module.goods.goodsdetail.CommentAdapter;
import com.feibo.snacks.view.module.goods.goodsdetail.FlavorAdpter;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.FlavorRecyclerView;

import java.util.Date;
import java.util.List;

@SuppressLint("InflateParams")
public class AddToCartDialog extends AlertDialog implements OnClickListener {

    private View view;
    private View headerView;
    private View reduceBtn;
    private View addBtn;
    private TextView showPrice;
    private TextView store;
    private TextView number;
    private TextView certain;
    private TextView kindTitle;
    private ImageView goodsImg;
    private TextView selectFlavor;
    private FlavorRecyclerView kindRV;

    private OnClickListener listener;
    private FlavorAdpter adpter;
    private static List<Kind> kinds;
    private static String imageUrl;
    public long touchLongAddPreT = -1;
    public long touchLongReducePreT = -1;

    public AddToCartDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lParams = getWindow().getAttributes();
        lParams.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        lParams.width = LayoutParams.MATCH_PARENT;
        lParams.height = LayoutParams.WRAP_CONTENT;
        getWindow().setWindowAnimations(R.style.collectDeleteOperation);
        getWindow().setAttributes(lParams);

        initWidget();
        initData();
        LayoutParams clp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setContentView(view, clp);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private void initData() {
        if (kinds != null && kinds.get(0) != null && kinds.get(0).kinds != null) {
            UIUtil.setDefaultImage(imageUrl, goodsImg);
            initGoodsInfo();

            adpter = new FlavorAdpter(getContext(), kinds.get(0).kinds);
            adpter.setListener(new FlavorAdpter.OnKindItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    if (pos == FlavorAdpter.DETAULT_FLAVOR) {
                        showItemPrice(UIUtil.getPrice(getContext(), kinds.get(0).kinds.get(0).price.current));
                        showStore(kinds.get(0).kinds.get(0).surplusNum);
                        showNumber(1);
                        showSelectFlavor(view.getResources().getString(R.string.select_falvor));
                    } else {
                        showItemPrice(UIUtil.getPrice(getContext(), kinds.get(0).kinds.get(pos).price.current));
                        showNumber(Integer.parseInt(number.getText().toString().trim()));
                        showStore(kinds.get(0).kinds.get(pos).surplusNum);
                        showSelectFlavor(kinds.get(0).kinds.get(pos).title);
                    }
                }
            });
            showNumber(1);

            kindRV.setHasFixedSize(true);
            kindRV.setAdapter(adpter);
        }
    }

    private void initGoodsInfo() {
        int index = getInitGoodsIndex();
        if (index < kinds.get(0).kinds.size()) {
            showItemPrice(UIUtil.getPrice(getContext(), kinds.get(0).kinds.get(index).price.current));
            showStore(kinds.get(0).kinds.get(index).surplusNum);
        }
        kindTitle.setText(kinds.get(0).title);
    }

    private int getInitGoodsIndex() {
        for (int i=0;i<kinds.get(0).kinds.size();i++) {
            if(kinds.get(0).kinds.get(i).surplusNum > 0) {
                return i;
            }
        }
        return 0;
    }

    private void showSelectFlavor(String title) {
        selectFlavor.setText(title);
    }

    private void showItemPrice(CharSequence price) {
        showPrice.setText(price);
    }

    private void showStore(int surplusNum) {
        store.setText(this.getContext().getResources().getString(R.string.store,surplusNum));
    }

    private void initWidget() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.dialog_detail_buy, null);

        ListView listView = (ListView) view.findViewById(R.id.list);

        LayoutInflater inflaterHeader = LayoutInflater.from(getContext());

        headerView = inflaterHeader.inflate(R.layout.dialog_detail_buy_header, null);

        listView.addHeaderView(headerView);
        CommentAdapter adapter = new CommentAdapter(getContext());
        listView.setAdapter(adapter);
        kindRV = (FlavorRecyclerView) headerView.findViewById(R.id.kind_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        kindRV.setLayoutManager(layoutManager);
        if (kinds != null && kinds.get(0).kinds != null) {
            if (kinds.get(0).kinds.size() > 0) {
                kindRV.setRow((kinds.get(0).kinds.size() % 3) == 0 ? kinds.get(0).kinds.size() / 3
                        : (kinds.get(0).kinds.size() / 3) + 1);
            }
        }
        certain = (TextView) view.findViewById(R.id.goods_detail_buy);
        goodsImg = (ImageView) view.findViewById(R.id.buy_dialog_img);
        showPrice = (TextView) view.findViewById(R.id.buy_dialog_price);
        store = (TextView) view.findViewById(R.id.buy_dialog_store);
        number = (TextView) headerView.findViewById(R.id.buy_number);
        kindTitle = (TextView) headerView.findViewById(R.id.kind_title);
        selectFlavor = (TextView) view.findViewById(R.id.select_falvor);

        View close = view.findViewById(R.id.dialog_detail_buy_close);
        certain.setText(getContext().getResources().getString(R.string.confirm));
        reduceBtn = headerView.findViewById(R.id.number_reduce);
        addBtn = headerView.findViewById(R.id.number_add);

        enableAdd();
        enableReduce();

        certain.setOnClickListener(this);
        close.setOnClickListener(this);
        reduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reduceNumber();
            }
        });
        reduceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    long nowTime = new Date().getTime();
                    if (touchLongReducePreT == -1) {
                        touchLongReducePreT = nowTime;
                    } else {
                        if (nowTime - touchLongReducePreT >= 200) {
                            touchLongReducePreT = nowTime;
                            reduceNumber();
                        }
                    }
                } else {
                    touchLongReducePreT = -1;
                }
                return false;
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber();
            }
        });
        addBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    long nowTime = new Date().getTime();
                    if (touchLongAddPreT == -1) {
                        touchLongAddPreT = nowTime;
                    } else {
                        if (nowTime - touchLongAddPreT >= 200) {
                            touchLongAddPreT = nowTime;
                            addNumber();
                        }
                    }
                } else {
                    touchLongAddPreT = -1;
                }
                return false;
            }
        });
    }

    public static AddToCartDialog show(Context context, List<Kind> kinds, String imageUrl) {
        AddToCartDialog.kinds = kinds;
        AddToCartDialog.imageUrl = imageUrl;
        AddToCartDialog dialog = new AddToCartDialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        // 修改系统menu菜单不能全屏显示问题
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = (int) display.getWidth();
        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.goods_detail_buy: {
            if (adpter.getSelectId() == -1) {
                RemindControl.showSimpleToast(getContext(),
                        getContext().getResources().getString(R.string.no_select_kind));
                return;
            }
            listener.onShare(Integer.valueOf(number.getText().toString().trim()),
                    kinds.get(0).kinds.get(adpter.getSelectId()).id, kinds.get(0).id);
            break;
        }
        case R.id.dialog_detail_buy_close: {
            cancel();
            break;
        }
        default:
            break;
        }
    }

    private void addNumber() {
        int curNum = Integer.parseInt(number.getText().toString().trim());
        if (curNum >= kinds.get(0).kinds.get(adpter.getSelectId() == -1 ? 0 : adpter.getSelectId()).surplusNum) {
            disableAdd();
            RemindControl.showSimpleToast(this.getContext(), R.string.cart_number_out);
            return;
        }
        showNumber(curNum + 1);
    }

    private void reduceNumber() {
        int curNum = Integer.parseInt(number.getText().toString().trim());
        if (curNum - 1 <= 0) {
            disableReduce();
            RemindControl.showSimpleToast(this.getContext(), R.string.cart_can_not_reduce);
            return;
        }
        showNumber(curNum - 1 <= 1 ? 1 : curNum - 1);
    }

    private void enableAdd() {
        addBtn.setBackgroundResource(R.drawable.bg_number_add_enable);
    }

    private void enableReduce() {
        reduceBtn.setBackgroundResource(R.drawable.bg_number_reduce_enable);
    }

    private void disableAdd() {
        addBtn.setBackgroundResource(R.drawable.bg_number_add_disable);
    }

    private void disableReduce() {
        reduceBtn.setBackgroundResource(R.drawable.bg_number_reduce_disable);
    }

    private void showNumber(int curNum) {
        int selectId = adpter.getSelectId() == -1 ? 0 : adpter.getSelectId();
        int restNumber = curNum > kinds.get(0).kinds.get(selectId).surplusNum ? kinds.get(0).kinds
                .get(selectId).surplusNum : curNum;
        number.setText(String.valueOf(restNumber <= 0 ? 1 : restNumber));




        if (curNum <= 1) {
            disableReduce();
            if (curNum < kinds.get(0).kinds.get(adpter.getSelectId() == -1 ? 0 : adpter.getSelectId()).surplusNum) {
                enableAdd();
            } else {
                disableAdd();
            }
        }

        if (curNum > 1 && curNum < kinds.get(0).kinds.get(adpter.getSelectId() == -1 ? 0 : adpter.getSelectId()).surplusNum) {
            enableReduce();
            enableAdd();
        }

        if (curNum >= kinds.get(0).kinds.get(adpter.getSelectId() == -1 ? 0 : adpter.getSelectId()).surplusNum) {
            disableAdd();
            if (curNum == 1) {
                disableReduce();
            } else {
                enableReduce();
            }
        }

    }

    public interface OnClickListener {
        void onShare(int number, int subKindId, int kindId);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
