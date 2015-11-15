package com.feibo.snacks.view.module.subject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.view.util.UIUtil;

import java.util.List;

import fbcore.utils.Strings;

/**
 * Created by Jayden on 2015/7/3.
 */
public class SubjectAboveAdapter extends RecyclerView.Adapter<SubjectAboveAdapter.ViewHolder>{

    private List<Special> banners;
    private Context context;
    private OnBannerClickListener listener;

    public SubjectAboveAdapter(Context context) {
        this.context = context;
    }

    public void setBanners(List<Special> banners) {
        this.banners = banners;
    }

    public interface OnBannerClickListener {
        void click(int id);
    }

    public void setListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject_header,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        if(i < banners.size() && banners.get(i) != null) {
            UIUtil.setSubjectHeaderImage(banners.get(i).img.imgUrl, viewHolder.subjectImg);
            if(!Strings.isEmpty(banners.get(i).title) && !"".equals(banners.get(i).title)) {
                viewHolder.title.setText(banners.get(i).title);
                viewHolder.titleView.setVisibility(View.VISIBLE);
            }

            setViewClickListener(viewHolder.subjectImg,i);
            setViewClickListener(viewHolder.title,i);
        }
    }

    private void setViewClickListener(View view, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.click(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView subjectImg;
        View titleView;

        public ViewHolder(View view) {
            super(view);
            subjectImg = (ImageView) view.findViewById(R.id.subject_image);
            title = (TextView) view.findViewById(R.id.subject_title);
            titleView = view.findViewById(R.id.title_back);
        }
    }
}
