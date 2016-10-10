package com.lovely3x.eavlibrary.ext.banner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * 为Banner提供内容视图的提供者
 * Created by lovely3x on 16/9/17.
 */
public interface IBannerContentProvider extends IView {

    /**
     * 创建BannerContent视图
     *
     * @param banner banner视图
     * @param parent 用来放置内容视图的视图容器
     * @return 创建的内容视图
     */
    public View createBannerContent(IBanner banner, ViewGroup parent);

    /**
     * 获取BannerContent视图
     *
     * @return BannerContent
     */
    public View getBannerContent();

    /**
     * 附加Banner视图
     *
     * @param banner        banner
     * @param bannerContent 需要附加的Banner视图对象
     * @param parent        Banner视图容器,并不一定需要附加到这个容器中
     */
    public void attachBannerContent(IBanner banner, View bannerContent, ViewGroup parent);

    /**
     * 分离 Banner内容视图
     *
     * @param banner        banner
     * @param bannerContent 需要分离的内容视图
     * @param parent        内容视图容器
     */
    public void detachBannerContent(IBanner banner, View bannerContent, ViewGroup parent);

    /**
     * 内容视图是否已经附加
     *
     * @return
     */
    public boolean isAttached();

    /**
     * 为内容视图设置适配器
     *
     * @param listAdapter 列表适配器
     */
    void setAdapter(ListAdapter listAdapter);

    /**
     * 设置触摸监听器
     *
     * @param listener 需要设置的触摸监听器
     */
    void setBannerContentTouchingListener(BannerContentTouchingListener listener);

    /**
     * BannerContent 触摸监听器
     */
    public interface BannerContentTouchingListener {

        /**
         * bannerContent被触摸后调用
         */
        void onTouching();

        /**
         * 用户触摸离开BannerContent
         */
        void onRelease();
    }
}
