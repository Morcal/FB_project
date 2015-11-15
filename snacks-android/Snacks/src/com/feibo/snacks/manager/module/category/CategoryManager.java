package com.feibo.snacks.manager.module.category;

import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadingPresenter;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.ILoadingView;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.SubClassify;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.BaseDataType.CategoryDataType;

import java.util.List;

public class CategoryManager extends AbsLoadingPresenter {

    private int classifyId;
    private int subClassifyId;

    private AbsListHelper categorySelectHelper;
    private AbsListHelper categoryGoodsHelper;

    public CategoryManager(ILoadingView loadingView) {
        super(loadingView);

        categorySelectHelper = new AbsListHelper(CategoryDataType.CATEGORY_SELECT) {
            @Override
            public void loadData(boolean needCache, Object params,DaoListener listener) {
                SnacksDao.getClassifyList(classifyId, listener);
            }
        };

        categoryGoodsHelper = new AbsListHelper(CategoryDataType.CATEGORY_LIST) {
            @Override
            public void loadData(boolean needCache, Object params,DaoListener listener) {
                int curPage = getCurPage();
                List<Goods> goodsList = (List<Goods>)getData();
                long sinceId = curPage == 1 ? 0 : (goodsList == null ? 0: goodsList.get(goodsList.size()-1).id);
                SnacksDao.getSubClassifyGoodsList(subClassifyId, classifyId, sinceId, curPage, listener);
            }
        };
    }

    @Override
    public void generateLoad(LoadType type, ILoadingListener listener) {
        switch (type) {
            case LOAD_FIRST:
            case REFRESH:{
                categoryGoodsHelper.refresh(true, categoryGoodsHelper.generateLoadingListener(listener));
                break;
            }
            case LOAD_MORE: {
                categoryGoodsHelper.loadMore(true, categoryGoodsHelper.generateLoadingListener(listener));
                break;
            }
            default:
                break;
        }

    }

    @Override
    public boolean hasMore() {
        return categoryGoodsHelper.hasMoreData();
    }

    @Override
    public BaseDataType getDataType() {
        return CategoryDataType.CATEGORY_LIST;
    }

    @Override
    public BaseDataType getMoreDataType() {
        return CategoryDataType.CATEGORY_LIST;
    }

    public void loadCategorySelect(ILoadingListener listener) {
        categorySelectHelper.loadMore(true, categorySelectHelper.generateLoadingListener(listener));
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public void setSubClassifyId(int subClassifyId) {
        this.subClassifyId = subClassifyId;
    }


    public List<SubClassify> getCategories() {
        return (List<SubClassify>) categorySelectHelper.getData();
    }

    public List<Goods> getGoodsList() {
        return (List<Goods>) categoryGoodsHelper.getData();
    }

    public void removeData() {
        categorySelectHelper.clearData();
    }

    public void removeCategoryofGoods() {
        categoryGoodsHelper.clearData();
    }
}