package com.lovely3x.eavlibrary.ext.banner;

/**
 * banner控制器
 * Created by lovely3x on 16/9/17.
 */
public interface IBannerController extends IView{

    /**
     * 初始化
     *
     * @param banner banner
     */
    void init(IBanner banner);

    /**
     * 附加控制器
     */
    void attach();

    /**
     * 分离控制器
     */
    void detach();

    /**
     * 下一页
     */
    void nextPage();

    /**
     * 上一页
     */
    void prePage();

    /**
     * 设置是否自动滚动
     *
     * @param auto 是否自动滚动
     */
    void autoTruing(boolean auto);

    /**
     * 当前是否是自动滚动模式
     *
     * @return true or false
     */
    boolean isAutoTruing();

    /**
     * 使用默认的间隔时间开始滚动
     */
    void startTruing();

    /**
     * 使用指定的间隔,开始滚动
     *
     * @param interval 滚动的时间间隔
     */
    void startTruing(long interval);

    /**
     * 停止滚动
     */
    void stopTruing();

    /**
     * 进入或退出无限滚动模式
     *
     * @param isEndlessLoop 是否进入无限滚动模式
     */
    void endlessLoop(boolean isEndlessLoop);

    /**
     * 当前是否是无线滚动模式
     *
     * @return true or false
     */
    boolean isEndlessLooping();

    /**
     * 设置条目点击监听器
     *
     * @param onItemClickedListener 条目点击监听器
     */
    void setOnItemClickedListener(OnItemClickedListener onItemClickedListener);

    /**
     * Banner条目点击监听器
     */
    public interface OnItemClickedListener {

        /**
         * 当条目被点击后执行
         *
         * @param position 点击的条目
         */
        void onItemClicked(int position);

    }

}
