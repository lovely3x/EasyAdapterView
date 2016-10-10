package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;

/**
 * Resource indicator provider
 * Created by lovely3x on 16/9/17.
 */
public class ResourceIndicatorProvider extends DrawableIndicatorProvider {

    public ResourceIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity, int drawableRes) {
        super(context, indicatorWidth, indicatorHeight, gravity, null);
        setDrawable(context.getResources().getDrawable(drawableRes));
    }

    public ResourceIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int drawableRes) {
        super(context, indicatorWidth, indicatorHeight, null);
        setDrawable(context.getResources().getDrawable(drawableRes));
    }

    public ResourceIndicatorProvider(Context context, int drawableRes) {
        super(context, null);
        setDrawable(context.getResources().getDrawable(drawableRes));
    }
}
