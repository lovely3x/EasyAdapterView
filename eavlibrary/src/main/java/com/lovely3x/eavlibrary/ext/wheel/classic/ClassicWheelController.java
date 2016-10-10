package com.lovely3x.eavlibrary.ext.wheel.classic;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.eavlibrary.ext.wheel.InnerWheel;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelController;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayout;

/**
 * Classic滚轮控制器
 * Created by lovely3x on 16/9/26.
 */
public class ClassicWheelController implements WheelController {

    /**
     * 默认的滚轮数量
     */
    private static final int DEFAULT_WHEEL_NUM = 1;

    private WheelLayout mWheelLayout;

    private Context mContext;

    /**
     * 滚轮的数量
     */
    private int mWheelNum;

    /**
     * 线性滚轮布局管理器
     */
    private ClassicWheelLayoutManager mManager;

    /**
     * 重力方向
     */
    private int mOrientation = ClassicWheelLayoutManager.ORIENTATION_HORIZONTAL;

    /**
     * 滚轮适配器
     */
    private ListAdapter[] mAdapters;

    /**
     * 滚轮选择变化监听器
     */
    private WheelSelectedChangedListener[] mWheelSelectedChangedListeners;

    /**
     * 滚轮滚动变化监听器
     */
    private WheelScrollChangedListener[] mWheelScrollChangedListener;

    /**
     * 适配器数据变化的观察者
     * 用于观察适配器数据的变化,在适配器的数据变化后,我们尝试刷新布局
     */
    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mWheelLayout != null) mWheelLayout.postInvalidate();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            if (mWheelLayout != null) mWheelLayout.postInvalidate();
        }
    };

    public ClassicWheelController(WheelLayout wheelLayout, Context context) {
        this.mWheelNum = DEFAULT_WHEEL_NUM;
        this.mContext = context;
        this.mWheelLayout = wheelLayout;
    }

    public ClassicWheelController(WheelLayout wheelLayout, Context context, int wheelNum) {
        this.mWheelNum = wheelNum;
        this.mContext = context;
        this.mWheelLayout = wheelLayout;
    }

    public ClassicWheelController(WheelLayout wheelLayout, Context context, ClassicWheelLayoutManager manager) {
        this.mManager = manager;
        this.mContext = context;
        this.mWheelLayout = wheelLayout;
    }

    public ClassicWheelController(WheelLayout wheelLayout, Context context, int wheelNum, int orientation) {
        this.mWheelNum = wheelNum;
        this.mContext = context;
        this.mOrientation = orientation;
        this.mWheelLayout = wheelLayout;
    }

    public ClassicWheelController(WheelLayout wheelLayout, Context context, ClassicWheelLayoutManager manager, int orientation) {
        this.mManager = manager;
        this.mContext = context;
        this.mOrientation = orientation;
        this.mWheelLayout = wheelLayout;
    }

    @Override
    public ClassicWheelLayoutManager getLayoutManager() {
        createLayoutManagerIfNeed();
        return mManager;
    }

    @Override
    public ClassicWheelDecorDrawer getDecorDrawer() {
        return new ClassicWheelDecorDrawer(mContext, this);
    }

    /**
     * 创建布局管理器,如果目前没有任何的布局管理器存在的话
     */
    protected void createLayoutManagerIfNeed() {
        if (mManager == null) {
            mManager = new ClassicWheelLayoutManager(mContext, mWheelNum, mOrientation);
        }
    }

    /**
     * 外部调用:
     * 用来设置适配器
     *
     * @param adapters
     */
    public void setAdapter(ListAdapter... adapters) {
        //设置适配器之前,尝试反注册掉之前注册的数据变化观察者
        unregisterAdapterObservers();
        this.mAdapters = adapters;
        if (adapters != null) {
            setAdapterForEachWheelView(adapters);
        } else {
            setAdapterForEachWheelView();
        }
    }

    /**
     * 反注册已经注册的数据变化观察者
     */
    private void unregisterAdapterObservers() {
        if (mAdapters != null) {
            for (ListAdapter adapter : mAdapters) {
                if (adapter != null) adapter.unregisterDataSetObserver(observer);
            }
        }
    }

    /**
     * 内部调用:
     * 为每一个滚轮视图设置适配器
     *
     * @param adapters 适配器数组
     */
    protected void setAdapterForEachWheelView(ListAdapter... adapters) {
        final InnerWheel[] views = getLayoutManager().getWheelViews();
        final int N = views == null ? 0 : views.length;
        for (int i = 0; i < N; i++) {
            final InnerWheel wheel = views[i];
            if (wheel != null) {
                if (adapters == null || adapters.length <= i) {
                    wheel.setAdapter(null);
                } else {
                    ListAdapter adapter = adapters[i];
                    wheel.setAdapter(adapter);
                    if (adapter != null) {
                        adapter.registerDataSetObserver(observer);
                    }
                }
            }
        }
    }

    /**
     * 获取当前设置的适配器
     *
     * @return
     */
    public ListAdapter[] getAdapters() {
        return mAdapters;
    }

    /**
     * 获取当前设置的滚动变化监听器
     *
     * @return
     */
    public WheelScrollChangedListener[] getWheelScrollChangedListener() {
        return mWheelScrollChangedListener;
    }

    /**
     * 获取当前设置的选中变化监听器
     *
     * @return
     */
    public WheelSelectedChangedListener[] getWheelSelectedChangedListeners() {
        return mWheelSelectedChangedListeners;
    }

    /**
     * 外部调用:
     * 设置选择变化监听器
     *
     * @param listeners 设置的监听器列表
     */

    public void setWheelSelectedChangedListener(WheelSelectedChangedListener... listeners) {
        this.mWheelSelectedChangedListeners = listeners;
        if (listeners == null || listeners.length == 0) {
            setWSCLForEachWheelView();
        } else {
            setWSCLForEachWheelView(listeners);
        }
    }

    /**
     * 内部调用:
     * 为每一个滚轮视图设置监听器
     *
     * @param listeners 监听器列表
     */
    protected void setWSCLForEachWheelView(WheelSelectedChangedListener... listeners) {
        final InnerWheel[] views = getLayoutManager().getWheelViews();
        final int N = views == null ? 0 : views.length;
        for (int i = 0; i < N; i++) {
            InnerWheel wheel = views[i];
            if (wheel != null) {
                if (listeners == null || listeners.length <= i) {
                    wheel.setOnSelectedItemChangedListener(null);
                } else {
                    wheel.setOnSelectedItemChangedListener(new WheelSelectedChangedListenerWrapper(i, listeners[i]));
                }
            }
        }
    }

    /**
     * 设置滚轮视图滚动变化监听器
     * 为每一个滚轮视图设置滚动变化监听器
     *
     * @param listeners 监听器
     */
    public void setWheelScrollChangedListener(WheelScrollChangedListener... listeners) {
        this.mWheelScrollChangedListener = listeners;
        if (listeners == null || listeners.length == 0) {
            setWheelScrollChangedListenerForEachWheelView();
        } else {
            setWheelScrollChangedListenerForEachWheelView(listeners);
        }
    }

    /**
     * 内部调用:
     * 支持为null,支持含null,支持长度不匹配
     *
     * @param listeners 需要设置的监听器
     */
    protected void setWheelScrollChangedListenerForEachWheelView(WheelScrollChangedListener... listeners) {
        final InnerWheel[] views = getLayoutManager().getWheelViews();
        final int N = views == null ? 0 : views.length;
        for (int i = 0; i < N; i++) {
            InnerWheel wheel = views[i];
            if (wheel != null) {
                if (listeners == null || listeners.length <= i) {
                    wheel.setOnScrollListener(null);
                } else {
                    wheel.setOnScrollListener(new WheelScrollChangedListenerWrapper(i, listeners[i]));
                }
            }
        }
    }

    /**
     * 滚轮选中条目变化监听器
     */
    public interface WheelSelectedChangedListener {

        /**
         * @param no           滚动的视图的编号
         * @param view         滚动的视图
         * @param oldSel       上一次选中的位置
         * @param oldSelView   上一次选中的视图
         * @param position     当前的位置
         * @param selectedView 当前的视图
         */
        void onSelectedItemChanged(int no, EasyAdapterView view, int oldSel, View oldSelView, int position, View selectedView);

    }

    /**
     * 滚轮滚动变化监听器
     */
    public interface WheelScrollChangedListener {


        /**
         * 滚动状态:没有滚动
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * 滚动状态:用户在用手指滑动屏幕
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * 滚动状态:用户正在快速滚动
         */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * 滚动状态:正在调整滚动位置
         */
        public static int SCROLL_STATE_ADJUSTMENT = 3;


        /**
         * 当滚动状态发生变化后执行
         *
         * @param no          滚动的视图的编号
         * @param view        滚动的视图咯
         * @param scrollState 当前的滚动状态
         */
        public void onScrollStateChanged(int no, EasyAdapterView view, int scrollState);

        /**
         * 当滚动后调用
         *
         * @param no               滚动的视图的编号
         * @param view             滚动的视图
         * @param firstVisibleItem 第一个可见的的条目的位置
         * @param visibleItemCount 可见的条目的数量
         * @param totalItemCount   条目的总数量
         */
        public void onScroll(int no, EasyAdapterView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    /**
     * 用来包装和转换滚轮选中条目变化监听器的一个类
     */
    private class WheelSelectedChangedListenerWrapper implements EasyAdapterView.OnSelectedItemChangedListener {

        private final int mNo;
        private final WheelSelectedChangedListener mExternalListener;

        WheelSelectedChangedListenerWrapper(int no, WheelSelectedChangedListener externalListener) {
            this.mNo = no;
            this.mExternalListener = externalListener;
        }

        @Override
        public void onSelectedItemChanged(EasyAdapterView easyAdapterView, int oldSel, View oldSelView, int position, View selectedView) {
            if (mExternalListener != null) {
                mExternalListener.onSelectedItemChanged(mNo, easyAdapterView, oldSel, oldSelView, position, selectedView);
            }
        }
    }

    /**
     * 用来包装和转换滚轮滚动变化监听器的一个类
     */
    private class WheelScrollChangedListenerWrapper implements EasyAdapterView.OnScrollListener {

        private final WheelScrollChangedListener mExternalListener;
        private final int mNo;

        public WheelScrollChangedListenerWrapper(int no, WheelScrollChangedListener externalListener) {
            this.mNo = no;
            this.mExternalListener = externalListener;
        }

        @Override
        public void onScrollStateChanged(EasyAdapterView view, int scrollState) {
            if (mExternalListener != null) {
                mExternalListener.onScrollStateChanged(mNo, view, scrollState);
            }
        }

        @Override
        public void onScroll(EasyAdapterView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mExternalListener != null) {
                mExternalListener.onScroll(mNo, view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }
}
