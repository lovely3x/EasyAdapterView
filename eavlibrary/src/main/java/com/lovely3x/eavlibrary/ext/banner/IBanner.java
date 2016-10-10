package com.lovely3x.eavlibrary.ext.banner;

import android.widget.ListAdapter;

/**
 * Abstract Banner
 * Created by lovely3x on 16/9/16.
 */
public interface IBanner {

    /**
     * 设置Banner的指示器 提供者
     *
     * @param indicatorProvider 指示器提供器
     */
    public void setIndicatorProvider(IIndicatorProvider indicatorProvider);

    /**
     * 获取指示器提供者
     *
     * @return 指示器提供者
     */
    public IIndicatorProvider getIndicatorProvider();


    /**
     * 设置指示器条的提供者
     *
     * @param indicatorBarProvider 指示器条
     */
    public void setIndicatorBarProvider(IIndicatorBarProvider indicatorBarProvider);

    /**
     * 获取指示条 提供者
     *
     * @return
     */
    public IIndicatorBarProvider getIndicatorBarProvider();


    /**
     * 获取内容视图提供者
     *
     * @return
     */
    public IBannerContentProvider getBannerContentProvider();

    /**
     * 设置内容视图提供者
     *
     * @param bannerContentProvider
     */
    public void setBannerContentProvider(IBannerContentProvider bannerContentProvider);


    /**
     * 设置内容适配器
     *
     * @param listAdapter 适配器
     */
    public void setAdapter(ListAdapter listAdapter);


    /**
     * 获取设置的内容适配器
     *
     * @return
     */
    public ListAdapter getAdapter();


    /**
     * 设置banner控制器
     *
     * @param bannerController 需要设置的banner控制器
     */
    public void setBannerController(IBannerController bannerController);

    /**
     * 获取当前的Banner控制器
     *
     * @return
     */
    public IBannerController getBannerController();
}
