package com.lovely3x.eavlibrary.ext.banner.content;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.eavlibrary.OverScroller;
import com.lovely3x.eavlibrary.ext.banner.IBanner;
import com.lovely3x.eavlibrary.ext.banner.IBannerContentProvider;

/**
 * EAV banner 内容提供者
 * Created by lovely3x on 16/9/17.
 */
public class EAVBannerContentProvider implements IBannerContentProvider {

    /**
     * 默认的Banner高度 200 dp
     */
    public static final int DEFAULT_BANNER_HEIGHT = 160;

    /**
     * 每次最大的滚动数量
     */
    private static final int MAX_SCROLL_PAGE_NUM = 1;

    /**
     * Banner的高 dp
     */
    private int mBannerHeight;

    private EasyAdapterView mEav;

    private ListAdapter mAdapter;

    private boolean mIsAttached;

    private BannerContentTouchingListener mBannerContentTouchingListener;

    public EAVBannerContentProvider() {
        this(DEFAULT_BANNER_HEIGHT);
    }

    /**
     * 使用指定的banner高 来构造一个 EAVBannerContentProvider
     *
     * @param bannerHeight banner的高度 dp
     */
    public EAVBannerContentProvider(int bannerHeight) {
        this.mBannerHeight = bannerHeight;
    }

    @Override
    public View createBannerContent(IBanner banner, ViewGroup parent) {
        this.mEav = new InnerBanner(parent.getContext());
        this.mEav.setDivider(null);
        this.mEav.setDividerHeight(0);
        this.mEav.setMode(EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW);
        this.mEav.setLayoutDirectly(EasyAdapterView.LAYOUT_DIRECTLY_HORIZONTAL);
        this.mEav.setScrollPageLimit(MAX_SCROLL_PAGE_NUM);
        if (mAdapter != null) mEav.setAdapter(mAdapter);
        return mEav;
    }


    @Override
    public View getBannerContent() {
        return mEav;
    }

    @Override
    public void attachBannerContent(IBanner banner, View bannerContent, ViewGroup parent) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBannerHeight, parent.getContext().getResources().getDisplayMetrics()));
        parent.addView(bannerContent, lp);
        mIsAttached = true;
    }

    @Override
    public void detachBannerContent(IBanner banner, View bannerContent, ViewGroup parent) {
        parent.removeView(bannerContent);
        mIsAttached = false;
    }

    @Override
    public boolean isAttached() {
        return mIsAttached;
    }

    @Override
    public void setAdapter(ListAdapter listAdapter) {
        this.mAdapter = listAdapter;
        if (mEav != null) mEav.setAdapter(mAdapter);
    }

    @Override
    public void setBannerContentTouchingListener(BannerContentTouchingListener listener) {
        this.mBannerContentTouchingListener = listener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }

    /**
     * 真实的Banner
     */
    public class InnerBanner extends EasyAdapterView {

        private OverScroller mPageScroller;
        private boolean touching;

        private PageScrollRunnable mPageScrollRunnable = new PageScrollRunnable();

        public InnerBanner(Context context) {
            super(context);
            setPageScroller(new OverScroller(context, new AccelerateInterpolator()));
        }

        public void setPageScroller(OverScroller scroller) {
            if (scroller == null) throw new NullPointerException();
            this.mPageScroller = scroller;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    touching = true;
                    if (mBannerContentTouchingListener != null)
                        mBannerContentTouchingListener.onTouching();
                    abortScroll();
                }
                break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    touching = false;
                    if (mBannerContentTouchingListener != null)
                        mBannerContentTouchingListener.onRelease();
                }

            }
            return super.onTouchEvent(event);
        }

        public boolean nextPage(int duration) {
            if (!touching) {
                final int delta = -getWidth();
                mPageScrollRunnable.startScroll(delta, duration);
                return true;
            } else {
                return false;
            }
        }

        public boolean prePage(int duration) {
            if (!touching) {
                final int delta = getWidth();
                mPageScrollRunnable.startScroll(delta, duration);
                return true;
            } else {
                return false;
            }
        }

        public void abortScroll() {
            mPageScrollRunnable.abortScroll();
        }

        public class PageScrollRunnable implements Runnable {

            private static final String TAG = "PageScrollRunnable";

            private int start = 0;

            @Override
            public void run() {
                if (mPageScroller.computeScrollOffset()) {
                    int cur = mPageScroller.getCurrX();
                    if (DEBUG) Log.i(TAG, "CurrentX  " + cur);
                    final int delta = cur - start;
                    start = cur;
                    if (getLayoutDirectly() == LAYOUT_DIRECTLY_HORIZONTAL) {
                        if (DEBUG) Log.i(TAG, "Scroll distance " + delta);
                        startScrollIfNeed(delta, 0);
                    } else {
                        startScrollIfNeed(0, delta);
                    }
                    postInvalidate();
                    post(this);
                } else {
                    int cur = mPageScroller.getCurrX();
                    final int delta = cur - start;
                    start = cur;
                    if (getLayoutDirectly() == LAYOUT_DIRECTLY_HORIZONTAL) {
                        startScrollIfNeed(delta, 0);
                    } else {
                        startScrollIfNeed(0, delta);
                    }
                    invalidate();
                    adjustSelView();
                }
            }


            /**
             * 开始滚动
             *
             * @param delta    滚动的增量距离
             * @param duration 滚动的时长
             */
            void startScroll(int delta, int duration) {
                start = 0;
                removeCallbacks(mPageScrollRunnable);
                mPageScroller.abortAnimation();

                mPageScroller.startScroll(0, 0, delta, 0, duration);
                post(mPageScrollRunnable);
                invalidate();

            }

            /**
             * 终止滚动
             */
            void abortScroll() {
                start = 0;
                removeCallbacks(mPageScrollRunnable);
                mPageScroller.abortAnimation();
                invalidate();
            }
        }
    }
}
