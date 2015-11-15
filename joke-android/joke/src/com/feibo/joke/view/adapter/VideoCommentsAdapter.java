package com.feibo.joke.view.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.Comment;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.VImageView;
import com.umeng.analytics.MobclickAgent;

public class VideoCommentsAdapter extends BaseSingleTypeAdapter<Comment> {

    public VideoCommentsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Comment comment = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_video_comment, null);
            holder = new ViewHolder();
            holder.ivHead = (VImageView) convertView.findViewById(R.id.iv_commentator_head);
            holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tv_commentator = (TextView) convertView.findViewById(R.id.tv_commentator_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_comment_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        comment = getItem(position);

        String name = comment.author.nickname;
        holder.tv_commentator.setText(name);

        UIUtil.setVAvatar(comment.author.avatar, comment.author.isSensation(), holder.ivHead);

        // holder.ivHead.setClickable(true);
        final long id = comment.author.id;
        holder.ivHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                LaunchUtil.launchSubActivity(mContext, UserDetailFragment.class,
//                        UserDetailFragment.buildBundle(false, id));

                boolean isFromMe = UserManager.getInstance().isFromMe(id);
                LaunchUtil.launchSubActivity(mContext, UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe, id));
                MobclickAgent.onEvent(mContext, UmengConstant.VIDEO_DETAIL_COMMENT_AVATAR);
            }
        });
        holder.tv_date.setText(TimeUtil.transformTime(comment.publishTime));
        User replyAuthor = comment.replyAuthor;
        if (replyAuthor != null && replyAuthor.nickname != null) {
            SpannableString ss = new SpannableString("回复 @" + replyAuthor.nickname + " :" + comment.content);
            int color = mContext.getResources().getColor(R.color.c2_orange);
            ss.setSpan(new ForegroundColorSpan(color), 3, replyAuthor.nickname.length() + 4,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_comment.setText(ss);
        } else {
            holder.tv_comment.setText(comment.content);
        }

        return convertView;
    }

    private class ViewHolder {
        /**
         * 评论作者的头像 *
         */
        VImageView ivHead;
        /**
         * 一般的评论 *
         */
        TextView tv_comment;
        /**
         * 评论作者 *
         */
        TextView tv_commentator;
        /**
         * 评论时间 *
         */
        TextView tv_date;
    }
}
