package com.feibo.snacks.view.module.person.orders.logistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.group.ExpressDetail;
import com.feibo.snacks.view.util.UIUtil;

/**
 * Created by hcy on 2015/7/28.
 */
public class LogisticsHead {

    private Context context;
    private View root;

    private ImageView icon;
    private TextView title;
    private TextView logisticsInfo;

    public LogisticsHead(Context context) {
        this.context = context;
        root = LayoutInflater.from(context).inflate(R.layout.layout_logistics_head, null);
        initWidget();
    }

    private void initWidget() {
        icon = (ImageView) root.findViewById(R.id.logistics_icon);
        title = (TextView) root.findViewById(R.id.logistics_name);
        logisticsInfo = (TextView) root.findViewById(R.id.logistics_msg);
    }

    public View getRoot() {
        return root;
    }

    public void refreshView(ExpressDetail detail) {
        UIUtil.setDefaultImage(detail.express.logo.imgUrl, icon);
        title.setText(detail.express.name);
        logisticsInfo.setText(detail.express.number);
    }
}
