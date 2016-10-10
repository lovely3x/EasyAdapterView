package com.lovely3x.eavlibrary.ext.banner;

import android.view.View;
import android.view.ViewGroup;

/**
 * 指示器 Provider
 * Created by lovely3x on 16/9/16.
 */
public interface IIndicatorProvider extends IView{

    /**
     * 状态为 选中
     */
    public static final int STATE_SELECTED = android.R.attr.state_selected;

    /**
     * 状态为 未选中
     */
    public static final int STATE_UNSELECTED = 0;

    /**
     * 创建指示器
     *
     * @param position       需要创建指示器的位置
     * @param indicatorGroup 包装指示器的的容器
     * @return 创建的指示器
     */
    View createIndicator(int position, ViewGroup indicatorGroup);

    /**
     * 获取指示器
     *
     * @param position       需要获取的指示器的位置
     * @param indicatorGroup 指示器组
     * @return 获取到的指示器或null, 如果没有获取到
     */
    View getIndicator(int position, ViewGroup indicatorGroup);

    /**
     * 为指示器布局
     *
     * @param position  需要布局的指示器位置
     * @param indicator 需要布局的指示器视图对象
     * @param container 放置指示器的容器
     */
    void layoutIndicator(int position, View indicator, ViewGroup container);

    /**
     * 刷新指示器的状态
     *
     * @param state         当前的指示器的状态
     * @param position      需要刷新的指示器的位置
     * @param indicatorView 需要刷新的指示器视图
     */
    void refreshIndicatorState(int state, int position, View indicatorView);

}
