package com.lovely3x.easyadapterview.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.eavlibrary.R;
import com.lovely3x.eavlibrary.SimpleOnScrollListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * 经典的日期选择滚动视图
 * Created by lovely3x on 16/9/18.
 */
public class ClassicDateWheelView extends DateWheelView {

    private static final int MIN_YEAR = 1971;
    private static final int MAX_YEAR = 2038;

    protected DateAdapter mMonthAdapter;
    protected DateAdapter mYearAdapter;
    protected DateAdapter mDayAdapter;

    private int mMaxYear = MAX_YEAR;
    private int mMinYear = MIN_YEAR;

    private String mYearFormat;
    private String mMonthFormat;
    private String mDayFormat;

    private final Calendar mCalendar = Calendar.getInstance();

    private int mTextSize = -1;
    private int mTextColor = -1;

    public ClassicDateWheelView(Context context) {
        this(context, null);
    }

    public ClassicDateWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicDateWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        nullFormatCheck();

        mYearAdapter = new DateAdapter(makeYearData(mMinYear, mMaxYear), mYearFormat);
        mMonthAdapter = new DateAdapter(makeMonthData(), mMonthFormat);
        mDayAdapter = new DateAdapter(null, mDayFormat);

        SimpleOnScrollListener scrollListener = new SimpleOnScrollListener() {
            @Override
            public void onScrollStateChanged(EasyAdapterView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE: {
                        reloadDays();
                    }
                    break;
                }
            }
        };

        mEavMonth.setOnScrollListener(scrollListener);
        mEavYear.setOnScrollListener(scrollListener);

        setTextColor(mTextColor);
        setTextSizeInPx(mTextSize);

        setAdapter(mYearAdapter, mMonthAdapter, mDayAdapter);
    }

    private void reloadDays() {
        if (mEavDay != null && mDayAdapter != null && mEavYear != null && mEavMonth != null) {
            int yearPos = mEavYear.getSelectedItemPosition();
            if (yearPos != -1) {
                int monthPos = mEavMonth.getSelectedItemPosition();
                if (monthPos != -1) {

                    //记录 "日" 之前的选中下标
                    int beforeDayIndex = mEavDay.getSelectedItemPosition();

                    //根据年月日,重新获取 "日" 的数量
                    int year = Integer.parseInt(mYearAdapter.getItem(yearPos));
                    int month = Integer.parseInt(mMonthAdapter.getItem(monthPos));//

                    //然后设置到 "日" 适配器上
                    mDayAdapter.setData(makeDaysData(year, month - 1));//月份需要减1

                    //还原之前显示的 "日"
                    beforeDayIndex = beforeDayIndex == -1 ? 0 : beforeDayIndex;
                    final int finalBeforeDayIndex = beforeDayIndex;
                    mEavDay.setSelection(finalBeforeDayIndex);

                    //也许你会觉得带动画的会更漂亮
//                    mEavDay.smoothToPosition(beforeDayIndex,200);
                }

            }
        }
    }

    public void setFormat(String yearFormat, String monthFormat, String dayFormat) {

        this.mYearFormat = yearFormat;
        this.mMonthFormat = monthFormat;
        this.mDayFormat = dayFormat;

        nullFormatCheck();

        if (mYearAdapter != null) mYearAdapter.setFormat(yearFormat);
        if (mMonthAdapter != null) mMonthAdapter.setFormat(monthFormat);
        if (mDayAdapter != null) mDayAdapter.setFormat(dayFormat);
    }


    /**
     * 设置文本的颜色
     *
     * @param textColor
     */
    private void setTextColor(int textColor) {
        this.mTextColor = textColor;
        if (mYearAdapter != null) mYearAdapter.setTextColor(mTextColor);
        if (mMonthAdapter != null) mMonthAdapter.setTextColor(mTextColor);
        if (mDayAdapter != null) mDayAdapter.setTextColor(mTextColor);
    }

    /**
     * 设置文本的大小
     *
     * @param textSize 需要设置的文本大小 sp
     */
    public void setTextSize(int textSize) {
        if (textSize != -1) {
            setTextSizeInPx(textSize);
        } else {
            setTextSizeInPx((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
        }
    }


    /**
     * 设置文本的大小
     *
     * @param textSize 需要设置的文本大小 px
     */
    public void setTextSizeInPx(int textSize) {
        mTextSize = textSize;
        if (mYearAdapter != null) mYearAdapter.setTextSize(mTextSize);
        if (mMonthAdapter != null) mMonthAdapter.setTextSize(mTextSize);
        if (mDayAdapter != null) mDayAdapter.setTextSize(mTextSize);
    }


    private void nullFormatCheck() {
        if (mYearFormat == null) mYearFormat = getResources().getString(R.string.format_year);
        if (mMonthFormat == null) mMonthFormat = getResources().getString(R.string.format_month);
        if (mDayFormat == null) mDayFormat = getResources().getString(R.string.format_day);
    }

    private List<String> makeYearData(int start, int end) {
        List<String> year = new ArrayList<>();
        for (int i = start; i <= end; i++) year.add(String.valueOf(i));
        return year;
    }

    private List<String> makeMonthData() {
        ArrayList<String> month = new ArrayList<>(12);
        for (int i = 1; i <= 12; i++) {
            month.add(String.valueOf(i));
        }
        return month;
    }

    private List<String> makeDaysData(int year, int month) {
        mCalendar.set(year, month, 1);
        int max = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.i(TAG, String.format(Locale.US, "%s Year %s Month Have %s days", year, month, max));
        ArrayList<String> days = new ArrayList<>(max);
        for (int i = 1; i <= max; i++) days.add(String.valueOf(i));
        return days;
    }

    public static class DateAdapter extends BaseAdapter {
        private String mFormat;
        private List<String> data;

        private LayoutInflater inflater;
        private int mTextColor = -1;
        private int mTextSize = -1;

        public DateAdapter(List<String> data, String format) {
            this.data = data;
            this.mFormat = format;
        }


        /**
         * 设置文本的颜色
         *
         * @param textColor
         */
        private void setTextColor(int textColor) {
            this.mTextColor = textColor;
            notifyDataSetChanged();
        }

        /**
         * 设置文本的大小
         *
         * @param textSize 需要设置的文本大小 px
         */
        public void setTextSize(int textSize) {
            this.mTextSize = textSize;
            notifyDataSetChanged();
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

            TextView tvText = ((TextView) convertView);

            if (mTextSize != -1 && tvText.getTextSize() != mTextSize) tvText.setTextSize(mTextSize);
            if (mTextColor != -1) tvText.setTextColor(mTextColor);

            tvText.setText(String.format(Locale.US, mFormat, getItem(position)));

            return convertView;
        }
    }

}
