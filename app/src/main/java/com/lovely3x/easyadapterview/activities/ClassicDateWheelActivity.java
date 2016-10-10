package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.easyadapterview.widgets.ClassicDateWheelView;

/**
 * 通用日期滚轮视图
 * Created by lovely3x on 16/8/27.
 */
public class ClassicDateWheelActivity extends AppCompatActivity {

    private static final String TAG = "WheelActivity";

    private EasyAdapterView mWheelView;
    private ClassicDateWheelView mClassicDateWheelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_date_wheel);
        this.mClassicDateWheelView = (ClassicDateWheelView) findViewById(R.id.cdwv_wheel_view);
    }
}
