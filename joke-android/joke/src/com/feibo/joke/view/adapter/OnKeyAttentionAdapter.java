package com.feibo.joke.view.adapter;

import android.content.Context;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.model.User;

public abstract class OnKeyAttentionAdapter<T> extends BaseSingleTypeAdapter<T>{

    public OnKeyAttentionAdapter(Context context) {
        super(context);
    }
    
    public abstract boolean getHasAttention();
    public abstract void setOneKeySuccess();

    public void setOneKeySuccess(User user) {
        if(user.relationship==User.RELATIONSHIP_NULL){
            user.relationship=User.RELATIONSHIP_ATTENTION;
        }else if(user.relationship==User.RELATIONSHIP_USER_BE_ATTENTION){
            user.relationship=User.RELATIONSHIP_BOTH_ATTENTION;
        }
    }
    
}
