package com.feibo.snacks.view.module.subject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

@SuppressLint("InflateParams")
public class SubjectAdapter extends BaseSingleTypeAdapter<Subject> {

    public SubjectAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_subject, null);
            viewHolder = new ViewHolder();
            viewHolder.subjectImg = (ImageView) convertView.findViewById(R.id.item_subject_img);
            viewHolder.subjectDesc = (TextView) convertView.findViewById(R.id.item_subject_title);
            viewHolder.likeNum = (TextView) convertView.findViewById(R.id.item_subject_collect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Subject subject = getItem(position);
        if (subject != null) {
            UIUtil.setImage(subject.img.imgUrl, viewHolder.subjectImg, R.drawable.default_topic,
                    R.drawable.default_topic);

            viewHolder.subjectDesc.setText(subject.title);
            viewHolder.likeNum.setText(String.valueOf(subject.hotindex));
        }
        return convertView;
    }

    private static class ViewHolder {
        private ImageView subjectImg;
        private TextView subjectDesc;
        private TextView likeNum;
    }
}
