package com.feibo.snacks.view.module.person.collect;



class IntItem{
    int key;
    IntItem next;

    public boolean put(int key){
        if(next==null || next.key > key){
            IntItem item = new IntItem();
            item.key = key;
            item.next = next;
            next = item;
            return true;
        } else if(next.key != key){
            return next.put(key);
        } else {
            return false;
        }
    }

    public boolean del(int key){
        System.out.println("key:"+key+",thiskey"+this.key);
        if(next==null || next.key > key){
            return false;
        } else if(next.key != key){
            return next.del(key);
        } else {
            next = next.next;
            return true;
        }
    }

    public boolean has(int key){
        if(next==null || next.key > key){
            return false;
        } else if(next.key != key){
            return next.has(key);
        } else {
            return true;
        }
    }
}

/**
 * 链表实现的SparseBooleanArray，并省略掉value，只保留key，key的存在与否决定value的值<br/>
 * <br/>
 * 注意：<br/>
 * 此链表对外透明；<br/>
 * put顺序决定key的顺序。<br/>
 * 由于链表的遍历的特性及链表对外透明的设定，不提供get(index)方法，有遍历需求的，使用toIntArray()。
 */
public class IntArray extends IntItem {

    /**
     * 添加一个key，若key存在，则不作处理
     *
     * @param key
     */
    @Override
    public boolean put(int key) {
        if(super.put(key)){
            this.key++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除一个key，若key不存在，刚不作处理
     *
     * @param key
     */
    @Override
    public boolean del(int key) {
        if(super.del(key)){
            this.key--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据value的值，选择添加key还是移除key;
     * @param key
     * @param value true:添加,false:移除
     */
    public void set(int key, boolean value){
        if(value){
            put(key);
        } else {
            del(key);
        }
    }

    /**
     * 获取key对应的value
     * @param key
     * @return true 当key存在; false 当key不存在。
     */
    public boolean get(int key){
        return has(key);
    }

    /**
     * 清空
     */
    public void clear(){
        next = null;
        key = 0;
    }

    /**
     * 返回一个包含所有key的int数组。
     *
     * @return keys[size()]
     */
    public int[] toIntArray() {
        int[] t = new int[key];
        IntItem a = next;
        for (int i = 0; i < key; i++) {
            t[i] = a.key;
            a = a.next;
        }
        return t;
    }

    public int size() {
        return key; // IntArray的头节点的key是无用的，因此，拿来缓存size
    }
}
