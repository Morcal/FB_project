package com.feibo.snacks.view.module.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Brand;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.group.HomePageHead;
import com.feibo.snacks.util.TimeUtil;
import com.feibo.snacks.view.base.RecGoodsContainer;
import com.feibo.snacks.view.module.home.PromotionViewGruop.PromotionViewHolder;
import com.feibo.snacks.view.util.UIUtil;

import java.util.List;

import fbcore.widget.BaseSingleTypeAdapter;

public class NewProductAdapter extends BaseSingleTypeAdapter<Goods> {

    private HomeGoodsClickListener listener;

    private List<Brand> recommGoods;
    private List<Goods> newProducts;
    private List<Special> spcialImage;
    private HomePageHead homeAbove;

    private final static int RECOMM_TITLE = 0;
    private final static int RECOMM_ITEM = 1;
    private final static int NEWPRODUCT_TITLE = 2;
    private final static int NEWPRODUCT_ITEM = 3;

    public static final int BTN_LOVER = 1;
    public static final int BTN_MOVIE = 2;
    public static final int BTN_TEA = 3;
    public static final int BTN_BEAR = 4;

    public NewProductAdapter(Context context) {
        super(context);
    }

    public void setHomeAbove(HomePageHead homeAbove) {
        this.homeAbove = homeAbove;
        setSpcialImage(homeAbove.specials);
        setRecommGoods(homeAbove.brands);
    }

    private void setSpcialImage(List<Special> spcialImage) {
        this.spcialImage = spcialImage;
    }

    private void setRecommGoods(List<Brand> recommGoods) {
        this.recommGoods = recommGoods;
    }

    public void setNewProducts(List<Goods> newProducts) {
        this.newProducts = newProducts;
    }

    public void setOnItemClickListener(HomeGoodsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (recommGoods != null && recommGoods.size() > 0) {
            count += recommGoods.size() + 1;
        }
        if (spcialImage != null && spcialImage.size() > 0) {
            count++;
        }
        if (newProducts != null && newProducts.size() > 0) {
            if (spcialImage != null && spcialImage.size() > 0) {
                count--;
            }
            count += newProducts.size();
            count++;
        }
        return count;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (recommGoods != null && recommGoods.size() > 0) {
                return RECOMM_TITLE;
            } else {
                return NEWPRODUCT_TITLE;
            }
        } else {
            if (recommGoods != null && recommGoods.size() > 0) {
                if (position <= recommGoods.size()) {
                    return RECOMM_ITEM;
                } else {
                    if (position == recommGoods.size() + 1) {
                        return NEWPRODUCT_TITLE;
                    } else {
                        return NEWPRODUCT_ITEM;
                    }
                }
            } else {
                return NEWPRODUCT_ITEM;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == RECOMM_TITLE) {
            String left = homeAbove.brandsTitleBig;
            String right = homeAbove.brandsTitleSml;
            return generateTitle(left, right);

        } else if (type == RECOMM_ITEM) {
            Brand brand = recommGoods.get(position - 1);
            return generateRecommView(convertView, brand, position - 1);

        } else if (type == NEWPRODUCT_TITLE) {
            String left = homeAbove.newTitleBig;
            String right = homeAbove.newTitleSml;
            return generateTodayTitle(left, right);

        } else {
            if (recommGoods != null && recommGoods.size() > 0) {
                position -= (recommGoods.size() + 2);
            } else {
                position--;
            }
            return generateTodayNew(position, convertView);
        }
    }

    private View generateTitle(String leftString, String rightString) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_home_item_title, null);
        TextView left = (TextView) view.findViewById(R.id.home_item_title_left);
        TextView right = (TextView) view.findViewById(R.id.home_item_title_right);
        left.setText(leftString);
        right.setText(rightString);
        return view;
    }

    private View generateTodayTitle(String leftString, String rightString) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_home_special, null);
        View interval1 = view.findViewById(R.id.interval1);
        View special = view.findViewById(R.id.home_special);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                width / 2);
        special.setLayoutParams(layoutParams);

        View todayTitle = view.findViewById(R.id.today_title);
        if (spcialImage == null || spcialImage.size() == 0) {
            UIUtil.setViewGone(interval1);
            UIUtil.setViewGone(special);
        } else {
            ImageView special1 = (ImageView) view.findViewById(R.id.btn_lover);
            ImageView special2 = (ImageView) view.findViewById(R.id.btn_movie);
            ImageView special3 = (ImageView) view.findViewById(R.id.btn_tea);
            ImageView special4 = (ImageView) view.findViewById(R.id.btn_beer);
            setOnClickAndRes(special1, BTN_LOVER);
            setOnClickAndRes(special2, BTN_MOVIE);
            setOnClickAndRes(special3, BTN_TEA);
            setOnClickAndRes(special4, BTN_BEAR);
        }

        if (newProducts == null || newProducts.size() == 0) {
            UIUtil.setViewGone(todayTitle);
        } else {
            TextView left = (TextView) view.findViewById(R.id.home_item_title_left);
            TextView right = (TextView) view.findViewById(R.id.home_item_title_right);
            left.setText(leftString);
            right.setText(rightString);
        }
        return view;
    }

    private void setOnClickAndRes(ImageView view, final int pos) {
        if(pos - 1 >= spcialImage.size()) {
            return;
        }
        if (spcialImage.get(pos - 1) != null) {
            UIUtil.setHomeSpecialImage(pos, spcialImage.get(pos - 1).img.imgUrl, view);
        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSpecialClick(pos - 1);
            }
        });
    }

    // below
    private View generateTodayNew(final int position, View convertView) {
        if (convertView == null) {
            convertView = RecGoodsContainer.getTodayNewProduct(mContext);
            int padding = UIUtil.dp2Px(mContext, 10);
            int padding_LR = UIUtil.dp2Px(mContext, 8);
            convertView.setPadding(padding_LR, padding, padding_LR, 0);
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newProducts != null) {
                    if (TimeUtil.isEnd(newProducts.get(position).time)) {
                        return;
                    }

                    if (listener != null) {
                        listener.onRecommendClick(position);
                    }
                }
            }
        });
        if (newProducts != null) {
            RecGoodsContainer.fillViewNew(newProducts.get(position), convertView,new ISellingEndListener() {
                @Override
                public void onEnd() {
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

    // above
    private View generateRecommView(View convertView, final Brand goods, final int position) {
        if (convertView == null) {
            convertView = PromotionViewGruop.generateView(mContext);
            int padding = UIUtil.dp2Px(mContext, 10);
            int padding_LR = UIUtil.dp2Px(mContext, 8);
            convertView.setPadding(padding_LR, padding, padding_LR, 0);
        }
        PromotionViewHolder holder = (PromotionViewHolder) convertView.getTag();
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimeUtil.isEnd(goods.time)) {
                    return;
                }
                if (listener != null) {
                    listener.onPromotionClick(position);
                }
            }
        });
        if (position >= recommGoods.size() - 1) {
            UIUtil.setViewGone(holder.line);
        }
        PromotionViewGruop.filterData(mContext, goods, convertView, new ISellingEndListener() {
            @Override
            public void onEnd() {
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public static interface HomeGoodsClickListener {
        void onPromotionClick(int position);

        void onRecommendClick(int position);

        void onSpecialClick(int id);
    }

    public interface ISellingEndListener {
        void onEnd();
    }
}
