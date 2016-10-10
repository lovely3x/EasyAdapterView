package com.lovely3x.eavlibrary.ext.wheel.classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.ext.wheel.InnerWheel;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelDecorDrawer;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayout;

/**
 * Created by lovely3x on 16/9/26.
 */
public class ClassicWheelDecorDrawer implements WheelDecorDrawer {

    private final int[] SHADOW_COLORS = new int[]{0xAAFFFFFF, 0x22FFFFFF, 0x22FFFFFF, 0xAAFFFFFF};

    private final Paint mPaint;

    private final Paint mShadowPaint;

    private ClassicWheelController mClassicWheelController;

    private LinearGradient mShadowShader;

    private int mCenterRectHeight = -1;

    public ClassicWheelDecorDrawer(Context context, ClassicWheelController classicWheelController) {
        this.mClassicWheelController = classicWheelController;
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.argb(0xFF, 0xd9, 0xd9, 0xd9));//d9d9d9
        mPaint.setStrokeWidth(context.getResources().getDisplayMetrics().density * 0.5f + 0.5f);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
    }

    private void measureItemHeightIfNeed() {
        if (mClassicWheelController.getLayoutManager() != null && mCenterRectHeight <= 0) {
            InnerWheel[] views = mClassicWheelController.getLayoutManager().getWheelViews();
            if (views != null && views.length > 0) {
                ListAdapter adapter = views[0].getAdapter();
                if (adapter != null && adapter.getCount() > 0) {
                    int height = views[0].measureItemHeightAtPosition(0);
                    if (height != -1) this.mCenterRectHeight = height;
                }
            }
        }
    }

    @Override
    public void drawDecor(WheelLayout layout, Canvas canvas) {
        final ClassicWheelLayoutManager layoutManager = mClassicWheelController.getLayoutManager();
        if (layoutManager != null) {
            drawCenterRect(layout, canvas, layoutManager);
            drawShadow(layout, canvas, layoutManager);
        }
    }

    protected void drawShadow(WheelLayout layout, Canvas canvas, ClassicWheelLayoutManager layoutManager) {

        final int width = layout.getWidth();
        final int height = layout.getHeight();

        createShadowShaderIfNeed(width, height);

        canvas.drawRect(0, 0, width, height, mShadowPaint);
    }

    /**
     * 创建渐变shader
     *
     * @param width  视图的宽度  影响渐变的区域
     * @param height 视图的高度 影响渐变的区域
     */
    protected void createShadowShaderIfNeed(int width, int height) {
        if (mShadowShader == null) {
            float top = (height / 2.0f - mCenterRectHeight / 2.0f) / height;
            float bottom = (height / 2.0f + mCenterRectHeight / 2.0f) / height;

            mShadowShader = new LinearGradient(0, 0, 0, height, SHADOW_COLORS, new float[]{0, top, bottom, 1.0f}, LinearGradient.TileMode.REPEAT);
            mShadowPaint.setShader(mShadowShader);
        }
    }

    /**
     * 绘制中间的矩形框框
     *
     * @param layout
     * @param canvas
     * @param layoutManager
     */
    protected void drawCenterRect(WheelLayout layout, Canvas canvas, ClassicWheelLayoutManager layoutManager) {
        measureItemHeightIfNeed();
        final InnerWheel[] views = layoutManager.getWheelViews();

        //主要就是绘制中间的那两根线
        if (mCenterRectHeight > 0 && views != null && views.length > 0) {
            final int yCenter = views[0].getYSel();

            int left = 0/*leftCenterRectPadding*/;
            int right = layout.getWidth() /*- rightCenterRectPadding*/;

            int top = yCenter - mCenterRectHeight / 2;
            int bottom = yCenter + mCenterRectHeight / 2;

            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
