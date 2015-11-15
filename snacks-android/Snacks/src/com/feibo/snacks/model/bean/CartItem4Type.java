package com.feibo.snacks.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcy on 2015/7/9.
 */
public class CartItem4Type {
    public static final int CART_ITEM_HEAD = 1;
    public static final int CART_ITEM_CONTENT = 2;
    public static final int CART_ITEM_FOOTER = 3;
    public static final int CART_ITEM_ONLY_ONE = 4;

    public CartSuppliers suppliers;

    public CartItem item;

    public int type;

    public String note;

    public int itemPosition = 0;

    public int itemSize = 0;

    public static ArrayList<CartItem4Type> getCart(List<CartSuppliers> supplieres) {
        if (supplieres == null || supplieres.size() == 0) {
            return null;
        }
        ArrayList<CartItem4Type> list = new ArrayList<CartItem4Type>();
        for (CartSuppliers suppliers : supplieres) {
            getCartItem(list, suppliers);
        }
        return list;
    }

    public static void getCartItem(ArrayList<CartItem4Type> list, CartSuppliers suppliers) {
        for (int i = 0; i < suppliers.items.size(); i++) {
            CartItem4Type cartItem4Type = new CartItem4Type();
            cartItem4Type.item = suppliers.items.get(i);
            cartItem4Type.suppliers = suppliers;
            cartItem4Type.itemPosition = i;
            cartItem4Type.itemSize = suppliers.items.size();
            getCartType(cartItem4Type);
            list.add(cartItem4Type);
        }
    }

    public static void getCartType(CartItem4Type cartItem4Type) {
        int i = cartItem4Type.itemPosition;
        int size = cartItem4Type.itemSize;
        if (i == 0 && i == (size - 1)) {
            cartItem4Type.type = CartItem4Type.CART_ITEM_ONLY_ONE;
        } else {
            if (i == 0) {
                cartItem4Type.type = CartItem4Type.CART_ITEM_HEAD;
            } else if (i == (size - 1)) {
                cartItem4Type.type = CartItem4Type.CART_ITEM_FOOTER;
            } else {
                cartItem4Type.type = CartItem4Type.CART_ITEM_CONTENT;
            }
        }
    }

    public static void removeItem(int position, List<CartItem4Type> list) {
        if (position == (list.size() - 1) && position == 0) {
            list.remove(position);
            return;
        }
        CartItem4Type item = list.get(position);
        int itemPosition = item.itemPosition;
        int itemSize = item.itemSize;
        if (itemSize == 1) {
            list.remove(position);
            return;
        }
        int itemStart = position - itemPosition;
        int itemEnd = itemStart + itemSize;
        for (int i = itemStart; i < itemEnd; i++) {
            CartItem4Type next = list.get(i);
            next.itemSize -= 1;
        }
        int range = itemSize - itemPosition;
        for (int i = position + 1; i < position + range; i++) {
            CartItem4Type next = list.get(i);
            next.itemPosition -= 1;
        }
        item.suppliers.items.remove(itemPosition);
        list.remove(position);
    }
}
