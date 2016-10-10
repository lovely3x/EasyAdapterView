package com.lovely3x.eavlibrary.ext.wheel.abs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.lovely3x.eavlibrary.R;

import java.lang.reflect.Constructor;

/**
 * 滚动布局
 * 思路:
 * 这个只是作为一个布局的容器,方便外面使用
 * 所有的都由 "控制器" 来控制
 * "控制器" 中包含布局管理器 通过布局管理器来控制布局不同的控制器提供的方法不相同
 * "控制器" 中包含装饰器 通过装饰器来修饰不同的装饰物
 *
 * @attr ref R.styleable.WheelLayout_controller 指定一个控制器,但是这个控制器必须存在一个 Context 和WheelLayout 的构造器
 * 否则无法创建
 * Created by lovely3x on 16/9/25.
 */
public class WheelLayout extends FrameLayout {

    private static final String TAG = "WheelLayout";

    private WheelController mWheelController;

    private int[] mWheelSize = {0, 0};

    public WheelLayout(Context context) {
        this(context, null);
    }

    public WheelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray styles = context.obtainStyledAttributes(attrs, R.styleable.WheelLayout);
            String controller = styles.getString(R.styleable.WheelLayout_controller);
            if (controller != null) {
                try {
                    Constructor<?> clazz = Class.forName(controller).getConstructor(WheelLayout.class, Context.class);
                    Object obj = clazz.newInstance(context);
                    if (obj instanceof WheelController) {
                        setWheelController((WheelController) obj);
                    }
                } catch (Exception e) {
                    //Ignored
                }
            }
            styles.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWheelController == null || mWheelController.getLayoutManager() == null) {

            setMeasuredDimension(0, 0);

        } else {

            final WheelLayoutManager layoutManager = mWheelController.getLayoutManager();

            this.mWheelSize = layoutManager.getLayoutSize(this, widthMeasureSpec, heightMeasureSpec);

            if (mWheelSize == null || mWheelSize.length < 2) {
                setMeasuredDimension(0, 0);
            } else {
                setMeasuredDimension(mWheelSize[0], mWheelSize[1]);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mWheelController != null && mWheelController.getLayoutManager() != null) {
            mWheelController.getLayoutManager().layout(this, left, top, right, bottom);
        }
    }

    /**
     * 设置滚轮控制器
     *
     * @param controller 控制器
     */
    public void setWheelController(WheelController controller) {
        this.mWheelController = controller;
        removeAllViews();
        requestLayout();
    }

    /**
     * 获取滚轮控制器
     *
     * @return
     */
    public WheelController getWheelController() {
        return mWheelController;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.i(TAG, "Draw");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        WheelDecorDrawer decorDrawer;
        if (mWheelController != null && (decorDrawer = mWheelController.getDecorDrawer()) != null) {
            decorDrawer.drawDecor(this, canvas);
        }
    }
}
