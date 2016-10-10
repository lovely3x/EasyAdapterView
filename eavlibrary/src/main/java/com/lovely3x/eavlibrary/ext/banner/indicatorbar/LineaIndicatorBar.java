package com.lovely3x.eavlibrary.ext.banner.indicatorbar;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.ext.banner.IBanner;
import com.lovely3x.eavlibrary.ext.banner.IIndicatorBarProvider;
import com.lovely3x.eavlibrary.ext.banner.IIndicatorProvider;

/**
 * 线性布局指示器条提供者
 * 这个类提供一个线性布局作为指示器条
 * Created by lovely3x on 16/9/17.
 */
public class LineaIndicatorBar implements IIndicatorBarProvider {

    /**
     * 默认的距离底部的距离: 8 dp
     */
    public static final int DEFAULT_BOTTOM_SPACE = 8;

    private LinearLayout mBar;

    private boolean mIsAttached;

    private ViewGroup.LayoutParams specLayoutParam;

    private int mBottomSpace = -1;

    public LineaIndicatorBar() {
    }

    /**
     * 通过指定一个自定义的 布局参数 来构造一个 LinearIndicatorBar
     *
     * @param specLayoutParam 布局参数
     */
    public LineaIndicatorBar(ViewGroup.LayoutParams specLayoutParam) {
        this.specLayoutParam = specLayoutParam;
    }

    /**
     * 通过制定 底部的边距 来构造一个 LinearIndicatorBar
     *
     * @param bottomSpace 底部的边距
     */
    public LineaIndicatorBar(int bottomSpace) {
        this.mBottomSpace = bottomSpace;
    }

    @Override
    public ViewGroup createIndicatorBar(IBanner banner, ViewGroup parent) {
        this.mBar = new LinearLayout(parent.getContext());
        return mBar;
    }

    @Override
    public ViewGroup getIndicatorBar() {
        return mBar;
    }

    @Override
    public void attachIndicatorBar(IBanner banner, ViewGroup indicatorBar, ViewGroup parent) {
        ViewGroup.LayoutParams lp = null;
        if (specLayoutParam != null) {
            lp = specLayoutParam;
        } else {
            if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) (lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                flp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//底部居中
                if (mBottomSpace == -1) {//设置下边距
                    flp.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BOTTOM_SPACE, parent.getResources().getDisplayMetrics());
                } else {
                    flp.bottomMargin = mBottomSpace;
                }
            } else {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }

        parent.addView(indicatorBar, lp);
        this.mIsAttached = true;
    }

    @Override
    public void detachIndicatorBar(IBanner banner, ViewGroup indicatorBar, ViewGroup parent) {
        parent.removeView(indicatorBar);
        mIsAttached = false;
    }

    @Override
    public boolean isAttached() {
        return mIsAttached;
    }

    @Override
    public void refreshIndicators(IBanner banner) {

        //移除掉原有的所有指示器
        mBar.removeAllViews();

        //获取指示器提供者
        IIndicatorProvider mIndicatorProvider = banner.getIndicatorProvider();

        //获取适配器
        ListAdapter adapter = banner.getAdapter();

        //创建并刷新指示器
        if (mIndicatorProvider != null && mBar != null && mIsAttached) {
            for (int i = 0; i < adapter.getCount(); i++) {
                View indicator = mIndicatorProvider.createIndicator(i, mBar);
                mIndicatorProvider.layoutIndicator(i, indicator, mBar);
                final int state = i == 0 ? IIndicatorProvider.STATE_SELECTED : IIndicatorProvider.STATE_UNSELECTED;
                mIndicatorProvider.refreshIndicatorState(state, i, indicator);
            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }
}
