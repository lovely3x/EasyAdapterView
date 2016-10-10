package com.lovely3x.eavlibrary.ext.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 通过两个 Drawable 实现状态切换的指示器
 * 如果想要使用一个Drawable实现效果则可使用
 * Created by lovely3x on 16/9/17.
 */
public abstract class AbsDoubleIndicatorProvider extends AbsIndicatorProvider {

    public AbsDoubleIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight, int gravity) {
        super(context, indicatorWidth, indicatorHeight, gravity);
    }

    public AbsDoubleIndicatorProvider(Context context, int indicatorWidth, int indicatorHeight) {
        super(context, indicatorWidth, indicatorHeight);
    }

    public AbsDoubleIndicatorProvider(Context context) {
        super(context);
    }

    public abstract Drawable getSelectedDrawable(int position);

    public abstract Drawable getUnselectedDrawable(int position);

    @Override
    public void refreshIndicatorState(int state, int position, View indicatorView) {
        switch (state) {
            case STATE_SELECTED: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    indicatorView.setBackground(getSelectedDrawable(position));
                } else {
                    indicatorView.setBackgroundDrawable(getSelectedDrawable(position));
                }
            }
            break;
            case STATE_UNSELECTED: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    indicatorView.setBackground(getUnselectedDrawable(position));
                } else {
                    indicatorView.setBackgroundDrawable(getUnselectedDrawable(position));
                }
            }
            break;
        }
    }
}
