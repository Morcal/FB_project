package com.feibo.joke.manager.list;

import java.util.ArrayList;
import java.util.List;

import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.BaseManager;
import com.feibo.joke.manager.IListManager;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;
import com.feibo.joke.model.WeiboFriendsItem;
import com.feibo.joke.model.data.WeiboFriendsData;

public class WeiboFriendsManager extends BaseManager implements IListManager<WeiboFriendsItem> {
    private List<WeiboFriendsItem> weiboFriendsItems;
    private List<User> friends;
    private List<User> invitations;

    public WeiboFriendsManager() {
        friends = new ArrayList<User>();
        invitations = new ArrayList<User>();
        weiboFriendsItems = new ArrayList<WeiboFriendsItem>();
    }

    @Override
    public void refresh(LoadListener listener) {
        readWeiboFriend(listener);
    }

    @Override
    public void loadMore(LoadListener listener) {
        readWeiboFriend(listener);
    }

    @Override
    public boolean hasData() {
        return weiboFriendsItems.size() > 0;
    }

    @Override
    public List<WeiboFriendsItem> getDatas() {
        return weiboFriendsItems;
    }

    @Override
    public List<WeiboFriendsItem> getLoadMoreDatas() {
        return null;
    }

    /**
     * 读取微博朋友
     * 
     * @param listener
     */
    private void readWeiboFriend(final LoadListener listener) {
        JokeDao.getWeiboFriends(new IEntityListener<WeiboFriendsData>() {

            @Override
            public void result(Response<WeiboFriendsData> entity) {
                if (entity.rsCode != ReturnCode.RS_SUCCESS) {
                    final int returnCode = entity.rsCode;
                    handleFail(listener, returnCode);
                    return;
                }

                if(entity.data == null || (entity.data.friends == null && entity.data.invitations == null)) {
                    handleFail(listener, ReturnCode.RS_EMPTY_ERROR);
                	return;
                }
                
                if(entity.data.friends != null && entity.data.friends.items != null && entity.data.friends.items.size() != 0) {
                    friends.clear();
                    friends.addAll(entity.data.friends.items);
                }

                if (entity.data.invitations != null && entity.data.invitations.items != null && entity.data.invitations.items.size() > 0) {
                    invitations.clear();
                    invitations.addAll(entity.data.invitations.items);
                }
                if(friends.size() == 0 && invitations.size() == 0){
                    handleFail(listener, ReturnCode.RS_EMPTY_ERROR);
                    return;
                }
                
                initWeiboFriendsItems();
                handleSuccess(listener);
            }
        }, false);
    }

    private void initWeiboFriendsItems() {
        weiboFriendsItems.clear();
        for (User friend : friends) {
            WeiboFriendsItem friendItem = new WeiboFriendsItem(WeiboFriendsItem.TYPE_CAN_ADD, friend);
            weiboFriendsItems.add(friendItem);
        }
        weiboFriendsItems.add(new WeiboFriendsItem(WeiboFriendsItem.TYPE_DIVISION_CENTER, null));
        for (User invitation : invitations) {
            WeiboFriendsItem invitationItem = new WeiboFriendsItem(WeiboFriendsItem.TYPE_CAN_INVITA, invitation);
            weiboFriendsItems.add(invitationItem);
        }
    }

    @Override
    public void refreshLocal(int position, WeiboFriendsItem e) {
        
    }
}
