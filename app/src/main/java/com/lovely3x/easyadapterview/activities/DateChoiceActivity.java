package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.easyadapterview.widgets.DateWheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期选择界面
 * Created by lovely3x on 16/9/18.
 */
public class DateChoiceActivity extends AppCompatActivity {
    private DateWheelView mDateWheelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_choice);
        this.mDateWheelView = (DateWheelView) findViewById(R.id.dwv_activity_date_choice_date_wheel);
        mDateWheelView.setAdapter(
                new DateAdapter(makeYearData(1971, 2039)),
                new DateAdapter(makeMonthData()),
                new DateAdapter(makeDaysData())
        );
    }

    private List<String> makeYearData(int start, int end) {
        List<String> year = new ArrayList<>();
        for (int i = start; i < end; i++) {
            year.add(i + "年");
        }
        return year;
    }

    private List<String> makeMonthData() {
        List<String> year = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            year.add(i + "月");
        }
        return year;
    }

    private List<String> makeDaysData() {
        List<String> year = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            year.add(i + "日");
        }
        return year;
    }

    public static class DateAdapter extends BaseAdapter {
        private List<String> data;

        private LayoutInflater inflater;

        public DateAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_date, parent, false);
            }

            ((TextView) convertView).setText(getItem(position));

            return convertView;
        }
    }
}
