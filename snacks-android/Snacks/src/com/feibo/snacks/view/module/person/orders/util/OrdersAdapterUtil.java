package com.feibo.snacks.view.module.person.orders.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Image;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.view.module.person.orders.item.OrdersAdapter;
import com.feibo.snacks.view.util.UIUtil;

import java.util.List;

/**
 * Created by hcy on 2015/7/14.
 */
public class OrdersAdapterUtil {

    public static View getOrders4Simple(Context context, int position, View convertView, ItemOrder itemOrder, OrdersAdapter.OrdersOptListener listener) {
        OrdersSimpleHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_orders_simple, null);
            holder = new OrdersSimpleHolder();
            holder.storeNameTv = (TextView) convertView.findViewById(R.id.item_orders_store_name);
            holder.ordersStateTv = (TextView) convertView.findViewById(R.id.item_orders_state);
            holder.goodsIv = (ImageView) convertView.findViewById(R.id.item_orders_goods_icon);
            holder.goodsTitleTv = (TextView) convertView.findViewById(R.id.item_orders_goods_title);
            holder.goodsKinds = (TextView) convertView.findViewById(R.id.item_orders_kinds);
            holder.goodsSimplePrice = (TextView) convertView.findViewById(R.id.item_orders_price);
            holder.simpleGoodsCount = (TextView) convertView.findViewById(R.id.item_orders_goods_count);
            holder.ordersCountTv = (TextView) convertView.findViewById(R.id.item_orders_goods_style_count);
            holder.ordersLeftBtn = (Button) convertView.findViewById(R.id.item_orders_opt_left);
            holder.ordersRightBtn = (Button) convertView.findViewById(R.id.item_orders_opt_right);
            holder.ordersExtraBtn = (Button) convertView.findViewById(R.id.item_orders_opt_extra);
            holder.ordersFreightTv = (TextView) convertView.findViewById(R.id.item_orders_freight);
            holder.ordersPayTv = (TextView) convertView.findViewById(R.id.item_orders_pay);
            holder.root = convertView.findViewById(R.id.item_orders_simple_parent);
            convertView.setTag(holder);
        }
        holder = (OrdersSimpleHolder) convertView.getTag();
        changeState(context, holder, itemOrder.type);
        holder.storeNameTv.setText(itemOrder.name);
        holder.ordersCountTv.setText("共" +  itemOrder.num + "件商品");
        holder.ordersFreightTv.setText("运费 : ￥" + itemOrder.freight);
        holder.ordersPayTv.setText("￥" + itemOrder.finalSum);

        UIUtil.setDefaultImage(itemOrder.single.img.imgUrl, holder.goodsIv);
        holder.goodsTitleTv.setText(itemOrder.single.goodsTitle);
        holder.goodsKinds.setText(itemOrder.single.kinds);
        holder.goodsSimplePrice.setText("￥" + itemOrder.single.price.current);
        holder.simpleGoodsCount.setText("x" + itemOrder.num);
        initListener(listener, position, holder);
        return convertView;
    }

    private static void initListener(final OrdersAdapter.OrdersOptListener listener, final int position, OrdersBase holder) {
        holder.ordersLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onLeftBtnClick(position);
            }
        });
        holder.ordersRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onRightBtnClick(position);
            }
        });
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onItemClick(position);
            }
        });
        holder.ordersExtraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onExtraBtnClick(position);
            }
        });
    }

    private static void changeState(Context context, OrdersBase ordersBase, int type) {
        int state = R.string.orders_state_wait_pay;
        int leftBtnState = View.VISIBLE;
        int leftBtnContent = R.string.cancel_orders;
        int rightBtnState = View.VISIBLE;
        int rightBtnContent = R.string.account;
        int rightBtnBg = R.drawable.btn_orders_cancle;
        int rightBtnTextColor = R.color.c8;
        int extraBtnState = View.GONE;
        int extraBtnContent = R.string.exmind_logistics;
        switch (type) {
            case ItemOrder.WAIT_PAY: {
                state = R.string.orders_state_wait_pay;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.cancel_orders;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.account;
                rightBtnBg = R.drawable.btn_account_select;
                rightBtnTextColor = R.color.c6;
                break;
            }
            case ItemOrder.WAIT_SEND: {
                state = R.string.orders_state_wait_send;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.refund_orders;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.remind_orders;
                rightBtnBg = R.drawable.btn_orders_cancle;
                rightBtnTextColor = R.color.c8;
                break;
            }
            case ItemOrder.WAIT_GET: {
                state = R.string.orders_state_wait_get;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.orders_refund_goods;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.affirm_orders;
                rightBtnBg = R.drawable.btn_delivery;
                rightBtnTextColor = R.color.c1;
                extraBtnContent = R.string.exmind_logistics;
                extraBtnState = View.VISIBLE;
                break;
            }
            case ItemOrder.TRADE_SUCCESS: {
                state = R.string.orders_state_trade_success;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.exmind_logistics;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.delete_orders;
                break;
            }
            case ItemOrder.TRADE_FAIL: {
                state = R.string.orders_state_trade_fail;
                leftBtnState = View.GONE;
                leftBtnContent = R.string.exmind_logistics;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.delete_orders;
                break;
            }
            case ItemOrder.WAIT_COMMENT: {
                state = R.string.orders_state_trade_success;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.exmind_logistics;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.orders_comment;
                break;
            }
            case  ItemOrder.RETURN_GOODS: {
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.delete_orders;
                rightBtnState = View.GONE;
                rightBtnContent = R.string.delete_orders;
                break;
            }
            default: {
                state = R.string.orders_state_wait_pay;
                leftBtnState = View.VISIBLE;
                leftBtnContent = R.string.cancel_orders;
                rightBtnState = View.VISIBLE;
                rightBtnContent = R.string.account;
                break;
            }
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ordersBase.ordersLeftBtn.getLayoutParams();
        if (rightBtnState == View.GONE) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.item_orders_opt);
        } else {
            params.addRule(RelativeLayout.LEFT_OF, R.id.item_orders_opt_right);
        }
        ordersBase.ordersExtraBtn.setText(extraBtnContent);
        ordersBase.ordersExtraBtn.setVisibility(extraBtnState);
        ordersBase.ordersLeftBtn.setLayoutParams(params);
        ordersBase.ordersStateTv.setText(state);
        ordersBase.ordersLeftBtn.setVisibility(leftBtnState);
        ordersBase.ordersLeftBtn.setText(leftBtnContent);
        ordersBase.ordersRightBtn.setVisibility(rightBtnState);
        ordersBase.ordersRightBtn.setText(rightBtnContent);
        ordersBase.ordersRightBtn.setTextColor(context.getResources().getColor(rightBtnTextColor));
        ordersBase.ordersRightBtn.setBackgroundResource(rightBtnBg);
    }

    public static View getOrders4Complex(Context context, final int position, View convertView, ItemOrder itemOrder, final OrdersAdapter.OrdersOptListener listener) {
        OrdersComplexHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_orders_complex, null);
            holder = new OrdersComplexHolder();
            holder.storeNameTv = (TextView) convertView.findViewById(R.id.item_orders_store_name);
            holder.ordersStateTv = (TextView) convertView.findViewById(R.id.item_orders_state);
            holder.orderImageBoard = convertView.findViewById(R.id.board_order_image);
            holder.goodsesRecyclerView = (RecyclerView) convertView.findViewById(R.id.item_orders_recycle_view);
            holder.nextGoods = convertView.findViewById(R.id.item_orders_cehua);
            holder.ordersFreightTv = (TextView) convertView.findViewById(R.id.item_orders_freight);
            holder.ordersPayTv = (TextView) convertView.findViewById(R.id.item_orders_pay);

            holder.ordersCountTv = (TextView) convertView.findViewById(R.id.item_orders_complex_count);
            holder.ordersLeftBtn = (Button) convertView.findViewById(R.id.item_orders_opt_left);
            holder.ordersRightBtn = (Button) convertView.findViewById(R.id.item_orders_opt_right);
            holder.ordersExtraBtn = (Button) convertView.findViewById(R.id.item_orders_opt_extra);
            holder.root = convertView.findViewById(R.id.item_orders_complex_parent);

            convertView.setTag(holder);
        }
        holder = (OrdersComplexHolder) convertView.getTag();
        changeState(context, holder, itemOrder.type);
        holder.storeNameTv.setText(itemOrder.name);

        holder.ordersCountTv.setText("共" + itemOrder.num + "件商品");
        holder.ordersFreightTv.setText("运费 : ￥" + itemOrder.freight);
        holder.ordersPayTv.setText("￥" + itemOrder.finalSum);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (itemOrder.multi.size() > 4) {
            holder.nextGoods.setVisibility(View.VISIBLE);
        } else {
            holder.nextGoods.setVisibility(View.GONE);
        }
        holder.goodsesRecyclerView.setLayoutManager(linearLayoutManager);
        GoodsRecycleAdapter adapter = new GoodsRecycleAdapter(context, itemOrder.multi);
        adapter.setOnOrdersItemClickListener(new GoodsRecycleAdapter.OnOrdersItemClickListener() {
            @Override
            public void onItemClick() {
                if (listener == null) {
                    return;
                }
                listener.onItemClick(position);
            }
        });
        holder.goodsesRecyclerView.setAdapter(adapter);
        initListener(listener, position, holder);

        return convertView;
    }

    public static class GoodsRecycleAdapter extends RecyclerView.Adapter<GoodsRecycleAdapter.GoodsRecycleHolder> {

        private List<Image> list;
        private Context context;
        private OnOrdersItemClickListener listener;

        public GoodsRecycleAdapter(Context context, List<Image> list) {
            this.context = context;
            this.list = list;
        }

        public void setOnOrdersItemClickListener(OnOrdersItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public GoodsRecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(context).inflate(R.layout.item_orders_recycle, null);
            GoodsRecycleHolder holder = new GoodsRecycleHolder(root);
            holder.goodsIcon = (ImageView) root.findViewById(R.id.item_orders_recycle_goods);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        return;
                    }
                    listener.onItemClick();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(GoodsRecycleHolder holder, int position) {
            UIUtil.setDefaultImage(list.get(position).imgUrl, holder.goodsIcon);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class GoodsRecycleHolder extends RecyclerView.ViewHolder {
            public ImageView goodsIcon;

            public GoodsRecycleHolder(View itemView) {
                super(itemView);
            }
        }

        public static interface OnOrdersItemClickListener {
            void onItemClick();
        }
    }

    private static class OrdersBase {
        public View root;
        public TextView storeNameTv;

        public TextView ordersStateTv;

        public TextView ordersCountTv;

        public TextView ordersPayTv;

        public TextView ordersFreightTv;

        public Button ordersLeftBtn;

        public Button ordersRightBtn;

        public Button ordersExtraBtn;

    }

    private static class OrdersSimpleHolder extends OrdersBase{
        public ImageView goodsIv;

        public TextView goodsTitleTv;

        public TextView goodsKinds;

        public TextView goodsSimplePrice;

        public TextView simpleGoodsCount;
    }

    private static class OrdersComplexHolder extends OrdersBase {
        public RecyclerView goodsesRecyclerView;

        public View orderImageBoard;

        public View nextGoods;

    }
}
