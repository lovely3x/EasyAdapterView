package com.lovely3x.easyadapterview.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lovely3x.eavlibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 经典的时间选择滚动
 * Created by lovely3x on 16/9/18.
 */
public class ClassicTimeWheelView extends TimeWheelView {

    private static final int MIN_MINUTE = 1;
    private static final int MAX_MINUTE = 24;

    private static final int MIN_HOUR = 1;
    private static final int MAX_HOUR = 24;

    private int mMaxMinute = MAX_MINUTE;
    private int mMinMinute = MIN_MINUTE;

    private int mMaxHour = MAX_HOUR;
    private int mMinHour = MIN_HOUR;

    protected TimeAdapter mHourAdapter;
    protected TimeAdapter mMinuteAdapter;

    private String mHourFormator;
    private String mMinuteFormator;

    public ClassicTimeWheelView(Context context) {
        this(context, null);
    }

    public ClassicTimeWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicTimeWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        nullFormatCheck();

        mHourAdapter = new TimeAdapter(makeRangeNumData(mMinHour, mMaxHour), mHourFormator);
        mMinuteAdapter = new TimeAdapter(makeRangeNumData(mMinMinute, mMaxMinute), mMinuteFormator);

        setAdapter(mHourAdapter, mMinuteAdapter);
    }

    public void setFormat(String yearFormat, String monthFormat) {

        this.mHourFormator = yearFormat;
        this.mMinuteFormator = monthFormat;

        nullFormatCheck();

        if (mHourAdapter != null) mHourAdapter.setFormat(yearFormat);
        if (mMinuteAdapter != null) mMinuteAdapter.setFormat(monthFormat);

    }

    private void nullFormatCheck() {
        if (mHourFormator == null) mHourFormator = getResources().getString(R.string.format_hour);
        if (mMinuteFormator == null)
            mMinuteFormator = getResources().getString(R.string.format_minute);
    }

    private List<String> makeRangeNumData(int start, int end) {
        List<String> nums = new ArrayList<>();
        for (int i = start; i <= end; i++) nums.add(String.valueOf(i));
        return nums;
    }

    public static class TimeAdapter extends BaseAdapter {
        private String mFormat;
        private List<String> data;

        private LayoutInflater inflater;

        public TimeAdapter(List<String> data, String format) {
            this.data = data;
            this.mFormat = format;
        }

        public void setFormat(String format) {
            this.mFormat = format;
            notifyDataSetChanged();
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

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_date_wheel_view, parent, false);
            }

            ((TextView) convertView).setText(String.format(Locale.US, mFormat, getItem(position)));

            return convertView;
        }
    }
}
