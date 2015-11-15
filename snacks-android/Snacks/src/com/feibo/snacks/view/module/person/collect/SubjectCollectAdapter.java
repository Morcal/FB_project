package com.feibo.snacks.view.module.person.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

@SuppressLint("InflateParams")
public class SubjectCollectAdapter extends BaseSingleTypeAdapter<Subject> {

    public boolean isEditMode = false;
    private IntArray intArray = new IntArray();
    private onItemSelectListener listener;
    private onItemClickListener itemClickListener;

    public SubjectCollectAdapter(Context context) {
        super(context);
    }

    public void changeMode() {
        isEditMode = !isEditMode;
        if (!isEditMode) {
            intArray.clear();
        }
        notifyDataSetChanged();
    }

    public int[] getClearRecord() {
        return intArray.toIntArray();
    }

    public void clearRecord() {
        intArray = new IntArray();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_subject, null);
            viewHolder = new ViewHolder();
            viewHolder.viewGroup = convertView;
            viewHolder.subjectImg = (ImageView) convertView.findViewById(R.id.item_subject_img);
            viewHolder.subjectDesc = (TextView) convertView.findViewById(R.id.item_subject_title);
            viewHolder.likeNum = (TextView) convertView.findViewById(R.id.item_subject_collect);
            viewHolder.collectSelectImageView = (ImageView) convertView.findViewById(R.id.item_collect_goods_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Subject subject = getItem(position);
        if (subject != null) {
            UIUtil.setDefaultImage(subject.img.imgUrl, viewHolder.subjectImg);
            viewHolder.subjectDesc.setText(subject.title);
            viewHolder.likeNum.setText(String.valueOf(subject.hotindex));
        }

        int distance = UIUtil.dp2Px(mContext, 5);
        if (position == 0) {
            convertView.setPadding(distance * 2, distance * 2, distance * 2, distance);
        } else {
            convertView.setPadding(distance * 2, distance, distance * 2, distance);
        }


        if (isEditMode) {
            viewHolder.collectSelectImageView.setVisibility(View.VISIBLE);
            boolean checked = intArray.get(position);
            viewHolder.collectSelectImageView.setImageResource(checked ? R.drawable.btn_delete_selected : R.drawable.btn_delete_normal);
            viewHolder.subjectImg.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelect(position);
                }
            });
        } else {
            viewHolder.subjectImg.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onClick(position);
                }
            });
            viewHolder.collectSelectImageView.setVisibility(View.GONE);
        }

        viewHolder.collectSelectImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelect(position);
                }
            }
        });
        return convertView;
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

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IntArray getIntArray() {
        return intArray;
    }
    public interface onItemSelectListener {
        void onSelect(int position);
    }

    public interface onItemClickListener {
        void onClick(int position);
    }

    private static class ViewHolder {
        public View viewGroup;
        public ImageView subjectImg;
        public TextView subjectDesc;
        public TextView likeNum;
        public ImageView collectSelectImageView;

    }
}
