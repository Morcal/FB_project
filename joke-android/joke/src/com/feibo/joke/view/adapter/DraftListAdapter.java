package com.feibo.joke.view.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.view.widget.VImageView;

public class DraftListAdapter extends BaseSingleTypeAdapter<Draft> {

    private int width;
    
    /** 是否处于编辑状态 */
    private boolean isEditState;
    
    private OnDeleteClickListener deleteClickListener;
    
    public DraftListAdapter(Activity context) {
        super(context);
        init(context);
    }

    private void init(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 2 - 30;
    }

    public static class ViewHodler {
        public ImageView img;
        public VImageView author;
        public TextView title;
        public TextView like;
        public TextView play;
        public View parent;
        public View btnDelete;
        public View layoutDelete;
    }

    public boolean isEditState() {
        return isEditState;
    }
    
    public void setIsEditState(boolean editState) {
        this.isEditState = editState;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_draft, null);
            holder = new ViewHodler();
            holder.parent = convertView.findViewById(R.id.video_layout);
            holder.author = (VImageView) convertView.findViewById(R.id.author_img);
            holder.img = (ImageView) convertView.findViewById(R.id.video_img);
            holder.like = (TextView) convertView.findViewById(R.id.like_count);
            holder.play = (TextView) convertView.findViewById(R.id.play_count);
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);  
            holder.layoutDelete = convertView.findViewById(R.id.layout_delete);
            convertView.findViewById(R.id.layout_title_img).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.author_img).setVisibility(View.GONE);
            convertView.findViewById(R.id.layout_play_like).setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }

        final Draft draft = getItem(position);
        
        holder.layoutDelete.setVisibility(isEditState ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(isEditState ? View.VISIBLE : View.GONE);
        if(isEditState) {
            holder.btnDelete.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if(deleteClickListener != null) {
                        deleteClickListener.onDelete(draft);
                    }
                }
            });
        }

        LayoutParams lp = (LayoutParams) holder.img.getLayoutParams();
        float height = (float) (width * draft.coverHeight) / (float) draft.coverWidth;
        lp.height = (int) height;
        holder.img.setLayoutParams(lp);

        UIUtil.setImageFromFile(draft.coverPath, holder.img, R.drawable.default_video, R.drawable.default_video,
                draft.coverWidth, draft.coverHeight);

        holder.title.setText(StringUtil.isEmpty(draft.content) ? "还没添加描述" : draft.content);
        return convertView;
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }
    
    public interface OnDeleteClickListener {
        public void onDelete(Draft draft);
    }

}
