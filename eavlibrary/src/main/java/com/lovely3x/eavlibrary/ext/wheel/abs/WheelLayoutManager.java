package com.lovely3x.eavlibrary.ext.wheel.abs;

/**
 * 滚轮布局管理器
 * 主要负责对滚轮视图进行布局
 * Created by lovely3x on 16/9/26.
 */
public interface WheelLayoutManager {

    /**
     * 获取布局的大小 类似于 {@link android.view.View#onMeasure(int, int)}
     *
     *
     * @param layout
     * @param parentWidthMeasureSpec  容器给定的宽度与模式
     * @param parentHeightMeasureSpec 容器给定的高度与模式
     * @return arr[0] width,arr[1] height
     */
    public int[] getLayoutSize(WheelLayout layout, int parentWidthMeasureSpec, int parentHeightMeasureSpec);

    /**
     * 对滚轮布局
     * @param layout 父容器
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void layout(WheelLayout layout, int left, int top, int right, int bottom);

}
