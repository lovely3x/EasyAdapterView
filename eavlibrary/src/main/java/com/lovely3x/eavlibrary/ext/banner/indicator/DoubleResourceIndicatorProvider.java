package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/**
 * 资源指示器提供者
 * 可以通过一组资源来为指示器提供效果
 */
public class DoubleResourceIndicatorProvider extends AbsDoubleIndicatorProvider {

    private final int mSelectedResource;
    private final int mUnSelectedResource;

    public DoubleResourceIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity, @DrawableRes int selectedResource, @DrawableRes int unSelectedResource) {
        super(context, indicatorWidth, indicatorHeight, gravity);
        this.mSelectedResource = selectedResource;
        this.mUnSelectedResource = unSelectedResource;
    }

    public DoubleResourceIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, @DrawableRes int selectedResource, @DrawableRes int unSelectedResource) {
        super(context, indicatorWidth, indicatorHeight);
        this.mSelectedResource = selectedResource;
        this.mUnSelectedResource = unSelectedResource;
    }

    public DoubleResourceIndicatorProvider(Context context, @DrawableRes int selectedResource, @DrawableRes int unSelectedResource) {
        super(context);
        this.mSelectedResource = selectedResource;
        this.mUnSelectedResource = unSelectedResource;
    }

    @Override
    public Drawable getSelectedDrawable(int position) {
        return getContext().getResources().getDrawable(mSelectedResource);
    }

    @Override
    public Drawable getUnselectedDrawable(int position) {
        return getContext().getResources().getDrawable(mUnSelectedResource);
    }
}