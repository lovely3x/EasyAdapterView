package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * 垂直滚轮视图
 * Created by lovely3x on 16/8/27.
 */
public class VerticalWheelActivity extends BaseActivity<String> implements View.OnClickListener {

    private static final String TAG = "WheelActivity";

    private EasyAdapterView mWheelView;
    private EditText mEditPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_wheel);

        this.mWheelView = (EasyAdapterView) findViewById(R.id.eav_activity_wheel_wheel);
        this.mWheelView.setAdapter(new BaseEasyAdapter(this, makeData(0)));
        this.mWheelView.setOnSelectedItemChangedListener(new EasyAdapterView.OnSelectedItemChangedListener() {

            @Override
            public void onSelectedItemChanged(EasyAdapterView easyAdapterView, int oldSel, View oldSelView, int position, View selectedView) {

                if (selectedView != null) {
                    selectedView.setSelected(true);
                }

                if (oldSelView != null)
                    oldSelView.setSelected(false);

            }
        });


        this.mEditPosition = (EditText) findViewById(R.id.et_activity_vertical_wheel_new_position);


        findViewById(R.id.b_activity_vertical_wheel_last).setOnClickListener(this);
        findViewById(R.id.b_activity_vertical_wheel_first).setOnClickListener(this);
        findViewById(R.id.b_activity_vertical_wheel_select).setOnClickListener(this);


    }

    @Override
    protected List<String> makeData(int count) {
        int minYear = 0;
        int maxYear = 1000;
        ArrayList<String> years = new ArrayList<>();
        for (int i = minYear; i <= maxYear; i++) {
            years.add(String.valueOf(i));
        }
        return years;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.list_item_date_wheel_view, parent, false);
    }

    @Override
    protected void handleData(View view, int position, String s) {
        TextView text = (TextView) view;
        text.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_activity_vertical_wheel_last: {//Last
                mWheelView.smoothToPosition(mWheelView.getCount() - 1, 200);
            }
            break;
            case R.id.b_activity_vertical_wheel_first: {//First
                mWheelView.smoothToPosition(0, 200);
            }
            break;
            case R.id.b_activity_vertical_wheel_select: {//Exact
                int position = 0;
                final String strPosition = this.mEditPosition.getText().toString().trim();
                if (!TextUtils.isEmpty(strPosition)) {
                    position = Integer.parseInt(strPosition);
                }
                mWheelView.setSelection(position);
            }
            break;
        }
    }
}
