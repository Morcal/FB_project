package com.feibo.snacks.view.module.person.orders.shoppingcart;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartItem4Type;
import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.log.LogUtil;
import fbcore.utils.Strings;
import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by hcy on 2015/7/8.
 */
public class CartAdapter extends BaseSingleTypeAdapter<CartItem4Type> {

    private CartItemOptListener listener;
    public long touchLongAddPreT = -1;
    public long touchLongReducePreT = -1;

    public CartAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CartHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_cart, null);
            holder = new CartHolder();
            holder.headView = view.findViewById(R.id.item_cart_head);

            holder.footerView = view.findViewById(R.id.item_cart_footer);
            holder.subTotalView = holder.footerView.findViewById(R.id.subtotal);
            holder.fullDiscountView = holder.footerView.findViewById(R.id.full_discount);
            holder.distanceView = holder.footerView.findViewById(R.id.distance);

            holder.goodsNumberTv = (TextView) holder.footerView.findViewById(R.id.item_cart_goods_number);
            holder.itemAccountTv = (TextView) holder.footerView.findViewById(R.id.item_cart_account);

            holder.tag = (TextView) holder.footerView.findViewById(R.id.item_cart_tag);
            holder.tagDesc = (TextView) holder.footerView.findViewById(R.id.item_cart_tag_desc);

            holder.selectState = view.findViewById(R.id.item_cart_select);
            holder.goodsIcon = (ImageView) view.findViewById(R.id.item_cart_goods_icon);
            holder.goodsTitle = (TextView) view.findViewById(R.id.item_cart_goods_title);
            holder.goodsKinds = (TextView) view.findViewById(R.id.item_cart_kinds);
            holder.addOptView = view.findViewById(R.id.item_cart_operation_add);
            holder.reduceOptView = view.findViewById(R.id.item_cart_operation_reduce);
            holder.optNumber = (TextView) view.findViewById(R.id.item_cart_operatoin_number);
            holder.deleteView = view.findViewById(R.id.item_cart_delete);

            holder.storeNameTv = (TextView) view.findViewById(R.id.item_cart_store_name);
            holder.goodsPriceNowTv = (TextView) view.findViewById(R.id.item_cart_price_now);
            holder.goodsPriceOrginTv = (TextView) view.findViewById(R.id.item_cart_price_orign);
            holder.goodsFailure = (TextView) view.findViewById(R.id.item_cart_goods_failure);
            holder.optParent = view.findViewById(R.id.relativeLayout);
            view.setTag(holder);
        } else {
            holder = (CartHolder) view.getTag();
        }
        CartItem4Type cartItem4Type = getItem(i);
        CartItem4Type.getCartType(cartItem4Type);
        if (cartItem4Type.type == CartItem4Type.CART_ITEM_HEAD) {
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.GONE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_FOOTER) {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.VISIBLE);
        } else if (cartItem4Type.type == CartItem4Type.CART_ITEM_ONLY_ONE){
            holder.headView.setVisibility(View.VISIBLE);
            holder.footerView.setVisibility(View.VISIBLE);
        } else {
            holder.headView.setVisibility(View.GONE);
            holder.footerView.setVisibility(View.GONE);
        }
        holder.storeNameTv.setText(cartItem4Type.suppliers.name);
        holder.selectState.setSelected(cartItem4Type.item.isSelect);
        holder.goodsTitle.setText(cartItem4Type.item.goodsTitle);
        holder.goodsKinds.setText(cartItem4Type.item.kinds);
        if (cartItem4Type.item.num > cartItem4Type.item.surplusNum) {
            cartItem4Type.item.num = cartItem4Type.item.surplusNum;
        }
        holder.optNumber.setText(cartItem4Type.item.num + "");
        holder.goodsPriceNowTv.setText("￥" + cartItem4Type.item.price.current);
        holder.goodsPriceOrginTv.setText("￥" + cartItem4Type.item.price.prime);
        holder.goodsPriceOrginTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        UIUtil.setDefaultImage(cartItem4Type.item.img.imgUrl, holder.goodsIcon);
        if (cartItem4Type.item.state == CartItem.FAILURE) {
            holder.goodsFailure.setVisibility(View.VISIBLE);
            holder.goodsFailure.setText(R.string.orders_goods_no_use);
            holder.selectState.setVisibility(View.INVISIBLE);
            holder.optParent.setVisibility(View.GONE);
            holder.goodsTitle.setEnabled(false);
            holder.goodsPriceNowTv.setEnabled(false);
        } else if (cartItem4Type.item.state == CartItem.NORMAL){
            holder.goodsFailure.setVisibility(View.GONE);
            holder.selectState.setVisibility(View.VISIBLE);
            holder.optParent.setVisibility(View.VISIBLE);
            holder.goodsTitle.setEnabled(true);
            holder.goodsPriceNowTv.setEnabled(true);
        } else if (cartItem4Type.item.state == CartItem.EMPTY) {
            holder.goodsFailure.setVisibility(View.VISIBLE);
            holder.goodsFailure.setText(R.string.orders_goods_empty);
            holder.selectState.setVisibility(View.INVISIBLE);
            holder.optParent.setVisibility(View.GONE);
            holder.goodsTitle.setEnabled(false);
            holder.goodsPriceNowTv.setEnabled(false);
        }

        handleDiscount(cartItem4Type, holder);
        holder.goodsNumberTv.setText("共" + getCartItemCount(cartItem4Type.suppliers) + "件商品");
        CharSequence cost = mContext.getResources().getString(R.string.show_cur_price, getCartItemCost(cartItem4Type.suppliers));
        holder.itemAccountTv.setText(cost);
        initOptListener(cartItem4Type, holder, i);
        return view;
    }

    /**
     * 处理满包邮活动
     * @param cartItem4Type
     * @param holder
     */
    private void handleDiscount(final CartItem4Type cartItem4Type, CartHolder holder) {
        if (!isValidActivity(cartItem4Type)) {
            UIUtil.setViewGone(holder.fullDiscountView);
            UIUtil.setViewGone(holder.distanceView);
        } else {
            UIUtil.setViewVisible(holder.fullDiscountView);
            UIUtil.setViewVisible(holder.distanceView);

            holder.tag.setText(cartItem4Type.suppliers.activity.iconTitle);
            holder.tagDesc.setText(cartItem4Type.suppliers.activity.title);
            holder.fullDiscountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.handlerDiscount(cartItem4Type.suppliers.activity.action.type,cartItem4Type.suppliers.activity.action.info,cartItem4Type.suppliers.id);
                }
            });
        }
    }

    private boolean isValidActivity(CartItem4Type cartItem4Type) {
        return cartItem4Type.suppliers.activity != null && Strings.isMeaningful(cartItem4Type.suppliers.activity.title) && Strings.isMeaningful(cartItem4Type.suppliers.activity.iconTitle);
    }

    public int getCartItemCount(CartSuppliers suppliers) {
        int count = 0;
        for (CartItem item : suppliers.items) {
            if (item.isSelect && item.state == CartItem.NORMAL) {
                count += item.num;
            }
        }
        return count;
    }

    public double getCartItemCost(CartSuppliers suppliers) {
        double count = 0;
        for (CartItem item : suppliers.items) {
            if (item.isSelect) {
                count += item.price.current * item.num;
            }
        }
        return count;
    }

    private void initOptListener(final CartItem4Type cartItem4Type, final CartHolder holder, final int i) {
        holder.selectState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChangeSelectState(i);
                }
            }
        });
        holder.addOptView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (listener == null) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchLongAddPreT = -1;
                    LogUtil.i("TA", "event : ");
                } else {
                    LogUtil.i("TA", "event : " + event.getAction());
                    listener.longTouchAddCart(i, holder);
                }
                return false;
            }
        });
        holder.addOptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItem4Type.item.state == CartItem.FAILURE) {
                    return;
                }
                if (listener != null) {
                    listener.addCart(i);
                }
            }
        });
        holder.reduceOptView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (listener == null) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchLongReducePreT = -1;
                } else {
                    LogUtil.i("TA", "event : " + event.getAction());
                    listener.longTouchReduceCart(i, holder);
                }
                return false;
            }
        });
        holder.reduceOptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItem4Type.item.state == CartItem.FAILURE) {
                    return;
                }
                if (listener != null) {
                    listener.reduceCart(i);
                }
            }
        });
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.deleteCart(i);
                }
            }
        });
    }

    public void setCartItemOptListener(CartItemOptListener listener) {
        this.listener = listener;
    }

    public interface CartItemOptListener {
        void onChangeSelectState(int i);
        void addCart(int i);
        void reduceCart(int i);
        void deleteCart(int i);
        void submitUpdateInfo(int i);
        void longTouchAddCart(int i, CartHolder holder);
        void longTouchReduceCart(int i, CartHolder holder);
        void handlerDiscount(int type,String info,long supplierId);
    }

    public static final class CartHolder {
        public View headView;
        public View footerView;
        public View selectState;
        public ImageView goodsIcon;
        public TextView goodsTitle;
        public TextView goodsKinds;
        public View deleteView;
        public View addOptView;
        public View reduceOptView;
        public TextView optNumber;
        public TextView goodsNumberTv;
        public TextView itemAccountTv;
        public TextView storeNameTv;
        public TextView goodsPriceNowTv;
        public TextView goodsPriceOrginTv;
        public TextView goodsFailure;
        public View optParent;

        public View subTotalView;
        public View fullDiscountView;
        public View distanceView;
        public TextView tag;
        public TextView tagDesc;
    }
}