package com.feibo.snacks.view.module.person.collect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.view.base.RecGoodsContainer;
import com.feibo.snacks.view.util.UIUtil;

import java.util.List;

import fbcore.widget.BaseSingleTypeAdapter;

public class CollectAdapter extends BaseSingleTypeAdapter<Goods> {

    private static final int VALID_TITLE = 4;
    private static final int VALID_ITEM = 1;
    private static final int INVALID_TITLE = 2;
    private static final int INVALID_ITEM = 3;

    public boolean isEditMode = false;
    private IntArray intArray = new IntArray();
    private onItemSelectListener listener;
    private OnItemClickListener clickListener;
    private int validCount = 0;
    private int inValidCount = 0;

    public CollectAdapter(Context context) {
        super(context);
    }

    public void changeMode() {
        isEditMode = !isEditMode;
        if (!isEditMode) {
            intArray.clear();
        }
        notifyDataSetChanged();
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (validCount > 0) {
            count++;
            if (validCount % 2 == 1) {
                count += validCount / 2 + 1;
            } else {
                count += validCount / 2;
            }
        }
        if (getItems() != null) {
            inValidCount = getItems().size() - validCount;
            if (inValidCount > 0) {
                count++;
                if (inValidCount % 2 == 1) {
                    count += inValidCount / 2 + 1;
                } else {
                    count += inValidCount / 2;
                }
            }
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (validCount > 0) {
                return VALID_TITLE;
            } else if (mItems.size() > 0) {
                return INVALID_TITLE;
            } else
                return 0;
        } else {
            if (validCount > 0) {
                if (position <= (validCount % 2 == 0 ? validCount / 2 : validCount / 2 + 1)) {
                    return VALID_ITEM;
                } else {
                    if (position == (validCount % 2 == 0 ? validCount / 2 : validCount / 2 + 1) + 1) {
                        return INVALID_TITLE;
                    } else {
                        return INVALID_ITEM;
                    }
                }
            } else {
                return INVALID_ITEM;
            }
        }
    }

    public int[] getClearRecord() {
        return intArray.toIntArray();
    }

    public void clearRecord() {
        int tempValidCount = validCount;
        for (Integer pos : getClearRecord()) {
            if (pos < tempValidCount) {
                validCount--;
            }
        }
        intArray.clear();
        intArray = new IntArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (type == VALID_TITLE) {
            Drawable drawableId = mContext.getResources().getDrawable(R.drawable.icon_dot);
            return generateTitle(drawableId, R.string.valid_goods, position);
        } else if (type == VALID_ITEM) {
            return generateValiCollect(position - 1, convertView);
        } else if (type == INVALID_TITLE) {
            Drawable drawableId = mContext.getResources().getDrawable(R.drawable.icon_failure);
            return generateTitle(drawableId, R.string.invalid_goods, position);
        } else {
            if (validCount > 0) {
                position -= 2;
            } else {
                position -= 1;
            }
            return generateInvaliCollect(position, convertView);
        }
    }

    private View generateValiCollect(int position, View convertView) {
        if (convertView == null) {
            convertView = RecGoodsContainer.getRecView(mContext);
        }
        @SuppressWarnings("unchecked")
        List<RecGoodsContainer.ViewHolder> holders = (List<RecGoodsContainer.ViewHolder>) convertView.getTag();
        if (holders == null) {
            convertView = RecGoodsContainer.getRecView(mContext);
            holders = (List<RecGoodsContainer.ViewHolder>) convertView.getTag();
        }

        int leftIndex = position * 2;
        int rightIndex = leftIndex + 1;

        if (mItems != null && leftIndex < mItems.size()) {
            Goods leftGoods = mItems.get(leftIndex);
            RecGoodsContainer.ViewHolder holder = holders.get(0);
            RecGoodsContainer.fillView(leftIndex, leftGoods, holder, null);
            addListener(leftGoods, leftIndex, holder);
            showIsDelete(holder, leftIndex);

            if (rightIndex < mItems.size()) {
                Goods rightGoods = mItems.get(rightIndex);
                if (rightGoods.status == 0) {// 有效商品才显示
                    RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                    RecGoodsContainer.fillView(rightIndex, rightGoods, rightholder, null);
                    addListener(rightGoods, rightIndex, rightholder);
                    rightholder.viewGroup.setVisibility(View.VISIBLE);
                    showIsDelete(rightholder, rightIndex);
                } else {
                    RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                    rightholder.viewGroup.setVisibility(View.INVISIBLE);
                }
            } else {
                RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                rightholder.viewGroup.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private View generateInvaliCollect(int position, View convertView) {
        if (convertView == null) {
            convertView = RecGoodsContainer.getRecView(mContext);
        }
        @SuppressWarnings("unchecked")
        List<RecGoodsContainer.ViewHolder> holders = (List<RecGoodsContainer.ViewHolder>) convertView.getTag();
        if (holders == null) {
            convertView = RecGoodsContainer.getRecView(mContext);
            holders = (List<RecGoodsContainer.ViewHolder>) convertView.getTag();
        }

        int leftIndex = position * 2;
        if (validCount % 2 != 0) {
            leftIndex--;
        }

        int rightIndex = leftIndex + 1;

        if (mItems != null && leftIndex < mItems.size()) {
            Goods leftGoods = mItems.get(leftIndex);
            RecGoodsContainer.ViewHolder holder = holders.get(0);
            RecGoodsContainer.fillCollectGoodsView(leftIndex, leftGoods, holder, null);
            addListener(leftGoods, leftIndex, holder);
            showIsDelete(holder, leftIndex);

            if (mItems.size() > (rightIndex)) {
                Goods rightGoods = mItems.get(rightIndex);
                RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                RecGoodsContainer.fillCollectGoodsView(rightIndex, rightGoods, rightholder, null);
                rightholder.viewGroup.setVisibility(View.VISIBLE);
                addListener(leftGoods, leftIndex, holder);
                showIsDelete(rightholder, rightIndex);
            } else {
                RecGoodsContainer.ViewHolder rightholder = holders.get(1);
                rightholder.viewGroup.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private View generateTitle(Drawable drawableId, int title, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods_collect_title, null);
        View distance = view.findViewById(R.id.collect_goods_invalid_distance);
        if (position == 0) {
            UIUtil.setViewGone(distance);
        } else {
            UIUtil.setViewVisible(distance);
        }
        ImageView left = (ImageView) view.findViewById(R.id.collect_goods_distance_img);
        TextView right = (TextView) view.findViewById(R.id.collect_goods_distance_title);
        left.setBackgroundDrawable(drawableId);
        right.setText(title);
        return view;
    }

    private void showIsDelete(RecGoodsContainer.ViewHolder viewHolder, final int position) {
        if (isEditMode) {
            viewHolder.collectSelectImageView.setVisibility(View.VISIBLE);
            boolean checked = intArray.get(position);
            viewHolder.collectSelectImageView.setImageResource(checked ? R.drawable.btn_delete_selected
                    : R.drawable.btn_delete_normal);
            viewHolder.viewGroup.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelect(position);
                }
            });
        } else {
            viewHolder.collectSelectImageView.setVisibility(View.GONE);
        }

        viewHolder.collectSelectImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelect(position);
            }
        });
    }

    private void addListener(final Goods goods, final int position, RecGoodsContainer.ViewHolder holder) {
        holder.viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null && goods.status == 0) {
                    clickListener.onItemClick(position);
                }
            }
        });
    }

    public void changeItemState(int position) {
        if (intArray.get(position)) {
            intArray.del(position);
        } else {
            intArray.put(position);
        }
        notifyDataSetChanged();
    }

    public void setOnItemSelectListener(onItemSelectListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface onItemSelectListener {
        void onSelect(int position);
    }

    public IntArray getIntArray() {
        return intArray;
    }
}
