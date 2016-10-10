package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Drawable指示器提供者
 * 可以通过一组Drawable来为指示器提供效果
 */
public class DoubleIndicatorProvider extends AbsDoubleIndicatorProvider {

    private final Drawable mSelectedDrawable;
    private final Drawable mUnSelectedDrawable;

    public DoubleIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity, Drawable selectedDrawable, Drawable unselectedDrawable) {
        super(context, indicatorWidth, indicatorHeight, gravity);
        this.mSelectedDrawable = selectedDrawable;
        this.mUnSelectedDrawable = unselectedDrawable;
    }

    public DoubleIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, Drawable selectedDrawable, Drawable unselectedDrawable) {
        super(context, indicatorWidth, indicatorHeight);
        this.mSelectedDrawable = selectedDrawable;
        this.mUnSelectedDrawable = unselectedDrawable;

    }

    public DoubleIndicatorProvider(Context context, Drawable selectedDrawable, Drawable unselectedDrawable) {
        super(context);
        this.mSelectedDrawable = selectedDrawable;
        this.mUnSelectedDrawable = unselectedDrawable;
    }

    @Override
    public Drawable getSelectedDrawable(int position) {
        return mSelectedDrawable;
    }

    @Override
    public Drawable getUnselectedDrawable(int position) {
        return mUnSelectedDrawable;
    }

}