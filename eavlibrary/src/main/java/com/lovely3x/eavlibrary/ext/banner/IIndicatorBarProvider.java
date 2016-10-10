package com.lovely3x.eavlibrary.ext.banner;

import android.view.ViewGroup;

/**
 * 指示器条提供者
 * Created by lovely3x on 16/9/17.
 */
public interface IIndicatorBarProvider extends IView{

    /**
     * 创建指示器条
     *
     * @param banner banner视图
     * @param parent 用来放置指示条的视图
     * @return 创建的指示器视图
     */
    public ViewGroup createIndicatorBar(IBanner banner, ViewGroup parent);

    /**
     * 获取IndicatorBar
     *
     * @return IndicatorBar
     */
    public ViewGroup getIndicatorBar();

    /**
     * 附加指示器条
     *
     * @param banner       banner
     * @param indicatorBar 需要附加的指示器对象
     * @param parent       指示器容器,并不一定需要附加到这个容器中
     */
    public void attachIndicatorBar(IBanner banner, ViewGroup indicatorBar, ViewGroup parent);

    /**
     * 分离 指示器条
     *
     * @param banner       banner
     * @param indicatorBar 需要分离的指示器条
     * @param parent       指示器容器
     */
    public void detachIndicatorBar(IBanner banner, ViewGroup indicatorBar, ViewGroup parent);

    /**
     * 指示器是否已经附加
     *
     * @return
     */
    public boolean isAttached();

    /**
     * 刷新指示器
     * @param banner banner
     */
    void refreshIndicators(IBanner banner);
}
