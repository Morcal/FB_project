package com.feibo.snacks.view.module.person.orders.logistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Logistics;
import com.feibo.snacks.model.bean.group.ExpressDetail;

/**
 * Created by hcy on 2015/7/28.
 */
public class LogisticsFooter {

    private Context context;
    private View root;
    private TextView firstInfoTv;
    private TextView firstInfoTime;

    private LinearLayout logisticsInfoGroup;

    public LogisticsFooter(Context context) {
        this.context = context;
        initWidget();
    }

    private void initWidget() {
        root = LayoutInflater.from(context).inflate(R.layout.layout_logistics_footer, null);
        firstInfoTv = (TextView) root.findViewById(R.id.logistics_first_info);
        firstInfoTime = (TextView) root.findViewById(R.id.logistics_first_time);
        logisticsInfoGroup = (LinearLayout) root.findViewById(R.id.logistics_view_group);
    }

    public View getRoot() {
        return root;
    }

    public void refreshView(ExpressDetail detail) {
        if (detail == null || detail.express == null || detail.express.detail == null || detail.express.detail.size() == 0) {
            root.setVisibility(View.GONE);
            return;
        }
        firstInfoTv.setText(detail.express.detail.get(0).desc);
        firstInfoTime.setText(detail.express.detail.get(0).time);
        for (int x = 1 ; x < detail.express.detail.size() ; x++) {
            Logistics logistics = detail.express.detail.get(x);
            View item = LayoutInflater.from(context).inflate(R.layout.item_logistics, null);
            TextView info = (TextView) item.findViewById(R.id.logistics_info);
            TextView time = (TextView) item.findViewById(R.id.logistics_time);
            info.setText(logistics.desc);
            time.setText(logistics.time);
            logisticsInfoGroup.addView(item);
        }
    }
}
