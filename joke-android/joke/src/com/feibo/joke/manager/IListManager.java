package com.feibo.joke.manager;

import java.util.List;

public interface IListManager<E>{
    void refresh(LoadListener listener);
    void loadMore(LoadListener listener);
    void refreshLocal(int position, E e); 
    boolean hasData();
    List<E> getDatas();
    List<E> getLoadMoreDatas();
}
