package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lovely3x.eavlibrary.ext.banner.IIndicatorProvider;

/**
 * Abstract Indicator
 */
public abstract class AbsIndicatorProvider implements IIndicatorProvider {

    public static final int INVALID = -1;

    private final Context mContext;

    /**
     * 默认指示器之间的距离 dp
     */
    private static final int DEFAULT_INDICATOR_HORIZONTAL_SPACE = 10;

    /**
     * 默认的指示器大小 dp
     */
    private static final int DEFAULT_INDICATOR_SIZE = 8;

    /**
     * 指示器的宽度
     */
    private int mIndicatorWidth = INVALID;

    /**
     * 指示器的高度
     */
    private int mIndicatorHeight = INVALID;

    /**
     * 指示器的重力方向
     */
    private int mGravity = Gravity.CENTER_HORIZONTAL;

    public AbsIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity) {
        this.mContext = context;
        this.mIndicatorWidth = indicatorWidth;
        this.mIndicatorHeight = indicatorHeight;
        this.mGravity = gravity;
    }

    public AbsIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.mIndicatorWidth = dp2Pix(metrics, indicatorWidth);
        this.mIndicatorHeight = dp2Pix(metrics, indicatorWidth);
        this.mContext = context;
    }

    public AbsIndicatorProvider(Context context) {
        this(context, DEFAULT_INDICATOR_SIZE, DEFAULT_INDICATOR_SIZE);
    }

    protected int dp2Pix(android.util.DisplayMetrics metrics, int dp) {
        return (int) (metrics.density * dp + 0.5f);
    }

    @Override
    public View createIndicator(int position, ViewGroup indicatorGroup) {
        View view = new View(indicatorGroup.getContext());
        return view;
    }

    @Override
    public void layoutIndicator(int position, View indicator, ViewGroup container) {
        if (container instanceof LinearLayout) {
            LinearLayout llContainer = (LinearLayout) container;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            if (position != 0) {
                lp.leftMargin = dp2Pix(mContext.getResources().getDisplayMetrics(), DEFAULT_INDICATOR_HORIZONTAL_SPACE);
            }
            llContainer.setGravity(Gravity.CENTER);
            container.addView(indicator, lp);
        }
    }

    public int getGravity() {
        return mGravity;
    }

    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public int getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public View getIndicator(int position, ViewGroup indicatorGroup) {
        return indicatorGroup.getChildAt(position);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }
}
