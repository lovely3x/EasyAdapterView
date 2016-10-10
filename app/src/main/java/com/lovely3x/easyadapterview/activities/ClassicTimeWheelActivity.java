package com.lovely3x.easyadapterview.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.easyadapterview.widgets.ClassicTimeWheelView;

import java.util.Arrays;

/**
 * 时间滚动选择界面
 * Created by lovely3x on 16/9/18.
 */
public class ClassicTimeWheelActivity extends AppCompatActivity {
    private ClassicTimeWheelView mTimeWheelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_wheel);
        this.mTimeWheelView = (ClassicTimeWheelView) findViewById(R.id.ctwv_activity_time_wheel_time_wheel);
        mTimeWheelView.setMinuteAdapter(
                new ClassicTimeWheelView.TimeAdapter(Arrays.asList(
                        "1", "2", "3", "4", "5", "6", "7", "8",
                        "9", "10", "11", "12", "13", "14", "15", "16"), "%s分钟") {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ((TextView)view).setTextColor(Color.RED);
                        return view;
                    }
                }
        );
    }
}
