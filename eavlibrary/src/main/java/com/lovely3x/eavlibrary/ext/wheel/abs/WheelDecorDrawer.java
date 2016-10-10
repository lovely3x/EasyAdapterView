package com.lovely3x.eavlibrary.ext.wheel.abs;

import android.graphics.Canvas;

/**
 * 装饰物绘制器
 * Created by lovely3x on 16/9/26.
 */
public interface WheelDecorDrawer {

    /**
     * 绘制装饰物
     * @param layout
     * @param canvas 画布
     */
    void drawDecor(WheelLayout layout, Canvas canvas);
}
