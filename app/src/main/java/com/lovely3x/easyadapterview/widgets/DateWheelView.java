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
 * DateWheelView
 * Created by lovely3x on 16/9/18.
 */
public class DateWheelView extends FrameLayout {

    protected final InnerWheel mEavYear;
    protected final InnerWheel mEavDay;
    protected final InnerWheel mEavMonth;

    protected final Paint mPaint;

    private int leftCenterRectPadding;
    private int rightCenterRectPadding;

    private int mCenterRectHeight;

    public DateWheelView(Context context) {
        this(context, null);
    }

    public DateWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.wheel_view_date, this, true);

        this.mEavYear = (InnerWheel) findViewById(R.id.eav_wheel_view_date_year);
        this.mEavMonth = (InnerWheel) findViewById(R.id.eav_wheel_view_date_month);
        this.mEavDay = (InnerWheel) findViewById(R.id.eav_wheel_view_date_day);

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.argb(0xFF, 0xd9, 0xd9, 0xd9));//d9d9d9
        mPaint.setStrokeWidth(context.getResources().getDisplayMetrics().density * 0.5f + 0.5f);
    }


    /**
     * 设置中间的两根线的颜色
     *
     * @param color 颜色
     */
    public void setCenterRectColor(int color) {
        this.mPaint.setColor(color);
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mEavYear != null && mEavYear.getAdapter() != null && mEavYear.getCount() > 0) {
            int height = mEavYear.measureItemHeightAtPosition(0);
            if (height != -1) this.mCenterRectHeight = height;
        }
    }

    public void setAdapter(ListAdapter yearAdapter, ListAdapter monthAdapter, ListAdapter dayAdapter) {
        mEavYear.setAdapter(yearAdapter);
        mEavMonth.setAdapter(monthAdapter);
        mEavDay.setAdapter(dayAdapter);
        requestLayout();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //主要就是绘制中间的那两根线
        if (mCenterRectHeight > 0) {
            final int yCenter = mEavYear.getYSel();

            int left = leftCenterRectPadding;
            int right = getWidth() - rightCenterRectPadding;

            int top = yCenter - mCenterRectHeight / 2;
            int bottom = yCenter + mCenterRectHeight / 2;

            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
