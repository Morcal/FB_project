package com.feibo.joke.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.model.Message;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.VImageView;

public class MessageUserAdapter extends BaseSingleTypeAdapter<Message> {

    private final String ATTENTION_STRING = "关注了你";
    private final String LIKE_STRING = "爱过了你的视频";
    
    private long lastId;
    private int selectColor;
    private int normalColor;
    
    private List<Long> readList;

    public MessageUserAdapter(Context context) {
        super(context);
        lastId = SPHelper.getReadPositionInUserMessage(mContext);
        selectColor = mContext.getResources().getColor(R.color.c9_white);
        normalColor = mContext.getResources().getColor(R.color.c8_shallow_white);
        readList = new ArrayList<Long>();
    }

    public static class Holder {
        protected View bg;
        protected VImageView avatar;
        protected TextView name;
        protected TextView msg;
        protected TextView time;
        protected ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder hodler = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_message, null);
            hodler = new Holder();
            hodler.bg = convertView;
            hodler.avatar = (VImageView) convertView.findViewById(R.id.item_avatar);
            hodler.msg = (TextView) convertView.findViewById(R.id.item_message);
            hodler.name = (TextView) convertView.findViewById(R.id.item_nickname);
            hodler.img = (ImageView) convertView.findViewById(R.id.item_img);
            hodler.time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(hodler);
        } else {
            hodler = (Holder) convertView.getTag();
        }
        Message message = getItem(position);
        if(message.id <= lastId || readList.contains((long)position)) {
            hodler.bg.setBackgroundColor(selectColor);
        } else {
            hodler.bg.setBackgroundColor(normalColor);
        }

        switchType(message, hodler);

        return convertView;
    }
    
    public void setRead(long position) {
        if(!readList.contains(position)) {
            readList.add(position);
            this.notifyDataSetChanged();
        }
    }

    //选择消息类型并初始化
    @SuppressLint("ResourceAsColor")
    private void switchType(Message message, Holder hodler) {
        SpannableString ss;
        ForegroundColorSpan fcs=new ForegroundColorSpan(mContext.getResources().getColor(R.color.c2_orange));
        switch (message.type) {
        case Message.TYPE_ATTENTION:
            //涉及复用ITEM时，消息颜色未变化的BUG，该BUG系因为使用SpannableString使得setTextColor无效
            ss=new SpannableString(ATTENTION_STRING);
            ss.setSpan(fcs,0, ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置爱心不显示
            hodler.msg.setCompoundDrawables(null, null, null, null);
            initData(hodler, message.user.isSensation(), message.user.avatar, message.user.nickname, ss, message.publishTime, null);
            break;

        case Message.TYPE_COMMENT:
            hodler.msg.setCompoundDrawables(null, null, null, null);

            User replyAuthor = message.comment.replyAuthor;

            if(replyAuthor != null && replyAuthor.nickname != null){
                ss = new SpannableString("回复 @"+replyAuthor.nickname+" :"+message.comment.content);
                ss.setSpan(fcs,3, replyAuthor.nickname.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                ss=new SpannableString(message.comment.content);
            }

            initData(hodler, message.comment.author.isSensation(), message.comment.author.avatar, message.comment.author.nickname, ss, message.comment.publishTime, message.video);
            break;

        case Message.TYPE_LIKE:
            ss=new SpannableString(LIKE_STRING);
            ss.setSpan(fcs,0, ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_link_selected);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            hodler.msg.setCompoundDrawables(drawable, null, null, null);

            initData(hodler, message.user.isSensation(), message.user.avatar, message.user.nickname, ss, message.publishTime, message.video);
            break;

        default:
            break;
        }

    }

    //填充数据
    private void initData(Holder hodler, boolean isSensation, String avatar, String name, CharSequence msg, long time, Video video) {
        UIUtil.setVAvatar(avatar, isSensation, hodler.avatar);
        
        if (video==null || video.thumbnail==null || StringUtil.isEmpty(video.thumbnail.url)) {
            if(video != null && video.oriImage !=null && !StringUtil.isEmpty(video.oriImage.url)) {
                //暂时采用原图
                hodler.img.setVisibility(View.VISIBLE);
                UIUtil.setImage(video.oriImage.url, hodler.img, R.drawable.default_avatar, R.drawable.default_avatar);
            } else {
                hodler.img.setVisibility(View.INVISIBLE);
            }
        }else{
            hodler.img.setVisibility(View.VISIBLE);
            UIUtil.setImage(video.thumbnail.url, hodler.img, R.drawable.default_avatar, R.drawable.default_avatar);
        }
        hodler.name.setText(name);
        hodler.msg.setText(msg);
        hodler.time.setText(TimeUtil.transformTime(time));
    }
}
