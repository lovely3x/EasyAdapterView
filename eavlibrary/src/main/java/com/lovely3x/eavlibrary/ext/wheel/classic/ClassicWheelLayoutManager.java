package com.lovely3x.eavlibrary.ext.wheel.classic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.eavlibrary.ext.wheel.InnerWheel;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayout;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayoutManager;

/**
 * Classic布局管理器
 * Created by lovely3x on 16/9/26.
 */
public class ClassicWheelLayoutManager implements WheelLayoutManager {

    /**
     * 布局的方向:垂直
     */
    public static final int ORIENTATION_VERTICAL = LinearLayout.VERTICAL;

    /**
     * 布局的方向:水平
     */
    public static final int ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;

    private final Context mContext;

    /**
     * 滚轮数量
     */
    private final int mWheelNum;

    /**
     * 布局方向
     */
    private int mOrientation = ORIENTATION_HORIZONTAL;

    /**
     * 滚轮视图
     */
    private InnerWheel[] mViews;

    private LinearLayout mLinearLayout;

    /**
     * 指定滚轮的数量来创建一个线性滚轮布局管理器
     *
     * @param wheelNum 滚轮数量
     */
    public ClassicWheelLayoutManager(Context context, int wheelNum, int orientation) {
        this.mWheelNum = wheelNum;
        this.mContext = context;
        this.mOrientation = orientation;

        //创建滚轮容器
        createDirectChild();
        //创建滚轮视图
        if (wheelNum > 0) createWheels();
            //重置滚轮视图
        else resetWheels();
    }


    protected void createDirectChild() {
        this.mLinearLayout = new LinearLayout(mContext);
        this.mLinearLayout.setOrientation(mOrientation == ORIENTATION_VERTICAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
    }

    /**
     * 重置创建的滚轮视图
     */
    protected void resetWheels() {
        if (mViews != null) mViews = new InnerWheel[0];
    }

    /**
     * 创建滚轮数量
     */
    protected void createWheels() {
        resetWheels();
        mViews = new InnerWheel[mWheelNum];
        for (int i = 0; i < mWheelNum; i++) {
            InnerWheel wheel = mViews[i] = new InnerWheel(mContext);
            wheel.setDivider(null);//关闭掉分割线
            wheel.setDividerHeight(0);//关闭掉分割线
            wheel.setLayoutDirectly(mOrientation == ORIENTATION_HORIZONTAL ? EasyAdapterView.LAYOUT_DIRECTLY_VERTICAL : EasyAdapterView.LAYOUT_DIRECTLY_HORIZONTAL);
            wheel.setMode(EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW);//设置滚轮模式默认为 无限滚动
            wheel.setOverScroll(false);//关闭overscroll
        }
    }

    /**
     * 进入或退出无线滚动模式
     * 默认为无限滚动模式
     *
     * @param isEndlessMode 是否进入无限滚动模式
     */
    public void setEndlessMode(boolean isEndlessMode) {
        if (mViews != null) {
            EasyAdapterView.ViewMode mode = isEndlessMode ? EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW : EasyAdapterView.ViewMode.WHEEL_VIEW;
            for (InnerWheel wheel : mViews) {
                wheel.setMode(mode);
            }
        }
    }


    @Override
    public int[] getLayoutSize(WheelLayout layout, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (!contain(layout, mLinearLayout)) {
            layout.addView(mLinearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layoutChildren();
        }

        mLinearLayout.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);

        return new int[]{View.MeasureSpec.getSize(parentWidthMeasureSpec), View.MeasureSpec.getSize(parentHeightMeasureSpec)};
    }

    @Override
    public void layout(WheelLayout layout, int left, int top, int right, int bottom) {
        mLinearLayout.layout(0, 0, mLinearLayout.getMeasuredWidth(), mLinearLayout.getMeasuredHeight());
    }

    private boolean contain(ViewGroup viewGroup, View child) {
        final int N = viewGroup.getChildCount();
        for (int i = 0; i < N; i++) {
            if (viewGroup.getChildAt(i) == child) return true;
        }
        return false;
    }

    /**
     * 对所有的滚轮布局
     */
    protected void layoutChildren() {
        if (mViews == null) return;

        for (View view : mViews) mLinearLayout.removeViewInLayout(view);

        LinearLayout.LayoutParams childLp = null;

        switch (mOrientation) {
            case ORIENTATION_HORIZONTAL:
                childLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                break;
            case ORIENTATION_VERTICAL:
                childLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                break;
        }

        for (View view : mViews) {
            mLinearLayout.addView(view, childLp);
        }
    }

    /**
     * 获取创建的滚轮视图
     *
     * @return null 如果num小于等于0或尚未创建, 创建的滚轮视图列表
     */
    public InnerWheel[] getWheelViews() {
        return mViews;
    }
}
