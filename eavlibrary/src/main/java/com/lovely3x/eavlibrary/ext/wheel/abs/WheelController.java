package com.lovely3x.eavlibrary.ext.wheel.abs;

import com.lovely3x.eavlibrary.ext.wheel.classic.ClassicWheelDecorDrawer;

/**
 * 滚轮布局控制器
 * Created by lovely3x on 16/9/26.
 */
public interface WheelController {

    /**
     * 获取布局管理器
     *
     * @return 布局管理器
     */
    public WheelLayoutManager getLayoutManager();


    /**
     * 获取装饰物绘制器
     *
     * @return 装饰物绘制器
     */
    public ClassicWheelDecorDrawer getDecorDrawer();

}
