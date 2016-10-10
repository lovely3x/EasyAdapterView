package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * Drawable indicator provider
 * Created by lovely3x on 16/9/17.
 */
public class DrawableIndicatorProvider extends AbsIndicatorProvider {

    private static final String TAG = "DrawableIndicatorProvider";

    private Drawable mDrawable;

    public DrawableIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity, Drawable drawable) {
        super(context, indicatorWidth, indicatorHeight, gravity);
        setDrawable(drawable);
    }

    public DrawableIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, Drawable drawable) {
        super(context, indicatorWidth, indicatorHeight);
        setDrawable(drawable);
    }

    public DrawableIndicatorProvider(Context context, Drawable drawable) {
        super(context);
        setDrawable(drawable);
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
        if (mDrawable != null) {
            this.mDrawable.mutate();
        }
    }

    @Override
    public View createIndicator(int position, ViewGroup indicatorGroup) {
        View indicator = super.createIndicator(position, indicatorGroup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            indicator.setBackground(createNewDrawableFromExists());
        } else {
            indicator.setBackgroundDrawable(createNewDrawableFromExists());
        }
        return indicator;
    }

    /**
     * 从现有的drawable中创建一个新的drawable对象
     *
     * @return
     */
    protected Drawable createNewDrawableFromExists() {
        Drawable.ConstantState constantState = mDrawable.getConstantState();
        if (constantState != null) {
            return constantState.newDrawable();
        }
        return null;
    }

    @Override
    public void refreshIndicatorState(int state, int position, View indicatorView) {
        Drawable background = indicatorView.getBackground();
        if (background instanceof StateListDrawable) {
            indicatorView.setSelected(state != STATE_UNSELECTED);
        }
    }


}
