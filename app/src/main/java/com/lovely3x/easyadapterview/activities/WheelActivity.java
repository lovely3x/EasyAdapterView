package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 滚轮视图
 * Created by lovely3x on 16/8/27.
 */
public class WheelActivity extends BaseActivity<String> {

    private static final String TAG = "WheelActivity";

    private EasyAdapterView mWheelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        this.mWheelView = (EasyAdapterView) findViewById(R.id.eav_activity_wheel_wheel);
        this.mWheelView.setAdapter(new BaseEasyAdapter(this, makeData(0)));
        this.mWheelView.setOnSelectedItemChangedListener(new EasyAdapterView.OnSelectedItemChangedListener() {

            @Override
            public void onSelectedItemChanged(EasyAdapterView easyAdapterView, int oldSel, View oldSelView, int position, View selectedView) {

                Log.i(TAG, "SelectedItemChanged => " + position);

                if (selectedView != null) {
                    selectedView.findViewById(R.id.tv_view_wheel_item_text).setSelected(true);
                }

                if (oldSelView != null)
                    oldSelView.findViewById(R.id.tv_view_wheel_item_text).setSelected(false);

            }
        });

        this.mWheelView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWheelView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mWheelView.setSelection(Calendar.getInstance().get(Calendar.MONTH));
            }
        });

    }

    @Override
    protected List<String> makeData(int count) {
        return Arrays.asList("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.view_wheel_item, parent, false);
    }

    @Override
    protected void handleData(View view, int position, String s) {
        TextView text = (TextView) view.findViewById(R.id.tv_view_wheel_item_text);
        text.setText(s);
    }
}
