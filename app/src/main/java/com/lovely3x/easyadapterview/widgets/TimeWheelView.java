package com.lovely3x.easyadapterview.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.R;
import com.lovely3x.eavlibrary.ext.wheel.InnerWheel;

/**
 * 时间选择滚轮
 * 只有 时和分两列 三列的话可以使用{@link DateWheelView}
 * Created by lovely3x on 16/9/18.
 */
public class TimeWheelView extends FrameLayout {

    protected final InnerWheel mHour;
    protected final InnerWheel mMinute;

    private final Paint mPaint;

    protected int mCenterRectHeight;

    protected int leftCenterRectPadding;
    protected int rightCenterRectPadding;

    private ListAdapter mHourAdapter;
    private ListAdapter mMinuteAdapter;

    public TimeWheelView(Context context) {
        this(context, null);
    }

    public TimeWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.wheel_view_time, this, true);

        this.mHour = (InnerWheel) findViewById(R.id.iw_wheel_view_time_hour);
        this.mMinute = (InnerWheel) findViewById(R.id.iw_wheel_view_time_minute);

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.argb(0xFF, 0xd9, 0xd9, 0xd9));//d9d9d9
        mPaint.setStrokeWidth(context.getResources().getDisplayMetrics().density * 0.5f + 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHour != null && mHour.getAdapter() != null && mHour.getCount() > 0) {
            int height = mHour.measureItemHeightAtPosition(0);
            if (height != -1) this.mCenterRectHeight = height;
        }
    }

    public void setAdapter(ListAdapter hourAdapter, ListAdapter minuteAdapter) {
        this.mHourAdapter = hourAdapter;
        this.mMinuteAdapter = minuteAdapter;

        mMinute.setAdapter(minuteAdapter);
        mHour.setAdapter(hourAdapter);

        requestLayout();
    }

    public void setHourAdapter(ListAdapter hourAdapter) {
        this.mHourAdapter = hourAdapter;
        mHour.setAdapter(hourAdapter);
        requestLayout();
    }


    public void setMinuteAdapter(ListAdapter minuteAdapter) {
        this.mMinuteAdapter = minuteAdapter;
        mMinute.setAdapter(minuteAdapter);
        requestLayout();
    }


    public ListAdapter getMinuteAdapter() {
        return mMinuteAdapter;
    }

    public ListAdapter getHourAdapter() {
        return mHourAdapter;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCenterRectHeight > 0) {
            final int yCenter = mHour.getYSel();

            int left = leftCenterRectPadding;
            int right = getWidth() - rightCenterRectPadding;

            int top = yCenter - mCenterRectHeight / 2;
            int bottom = yCenter + mCenterRectHeight / 2;

            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
