/*
MIT License

Copyright (c) 2016

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.lovely3x.eavlibrary.ext.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.lovely3x.eavlibrary.ext.banner.content.EAVBannerContentProvider;
import com.lovely3x.eavlibrary.ext.banner.controller.EAVBannerController;
import com.lovely3x.eavlibrary.ext.banner.indicatorbar.LineaIndicatorBar;

/**
 * Banner
 * ① 支持无限滚动
 * ② 自动滚动
 * ③ 手动滚动检测
 * Created by lovely3x on 16/8/21.
 */
public class EasyBanner extends FrameLayout implements IBanner {

    private View mBannerContent;
    private ViewGroup mIndicatorBar;
    private ViewGroup mContainer;
    private ListAdapter mAdapter;

    private IIndicatorProvider mIndicatorProvider;
    private IIndicatorBarProvider mIndicatorBarProvider;
    private IBannerContentProvider mBannerContentProvider;

    private IBannerController mIBannerController;

    public EasyBanner(Context context) {
        this(context, null);
    }

    public EasyBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {

        this.mIBannerController = new EAVBannerController();
        this.mIndicatorBarProvider = new LineaIndicatorBar();
        this.mBannerContentProvider = new EAVBannerContentProvider();

        this.mContainer = this;

        initBannerContentIfNeed();
        initBannerIndicatorBarIfNeed();
        initBannerIndicatorIfNeed();
        initControllerIfNeed();
    }

    /**
     * 刷新Banner内容视图
     */
    private void initBannerContentIfNeed() {
        if (mBannerContentProvider != null && !mBannerContentProvider.isAttached()) {
            mBannerContent = mBannerContentProvider.createBannerContent(this, mContainer);
            mBannerContentProvider.attachBannerContent(this, mBannerContent, mContainer);
        }
    }

    /**
     * 刷新banner的指示器
     */
    protected void initBannerIndicatorIfNeed() {
        if (mAdapter != null && mIndicatorProvider != null &&
                mIndicatorBarProvider != null && mIndicatorBarProvider.isAttached()) {
            mIndicatorBarProvider.refreshIndicators(this);
        }
    }

    /**
     * 刷新指示器条
     */
    protected void initBannerIndicatorBarIfNeed() {
        if (mIndicatorBarProvider != null && !mIndicatorBarProvider.isAttached()) {
            mIndicatorBar = mIndicatorBarProvider.createIndicatorBar(this, mContainer);
            mIndicatorBarProvider.attachIndicatorBar(this, mIndicatorBar, mContainer);
        }
    }

    /**
     * 初始化控制器
     */
    protected void initControllerIfNeed() {
        if (mIBannerController != null && mBannerContentProvider != null &&
                mIndicatorBarProvider != null && mIndicatorProvider != null) {
            mIBannerController.init(this);
            mIBannerController.attach();
        }
    }

    @Override
    public void setIndicatorProvider(IIndicatorProvider indicatorProvider) {
        this.mIndicatorProvider = indicatorProvider;
        initBannerIndicatorIfNeed();
        initControllerIfNeed();
    }

    @Override
    public IIndicatorProvider getIndicatorProvider() {
        return mIndicatorProvider;
    }

    @Override
    public void setIndicatorBarProvider(IIndicatorBarProvider indicatorBarProvider) {
        if (mIndicatorBarProvider != null)
            mIndicatorBarProvider.detachIndicatorBar(this, mIndicatorBar, mContainer);

        this.mIndicatorBarProvider = indicatorBarProvider;

        initBannerIndicatorBarIfNeed();
        initBannerIndicatorIfNeed();
        initControllerIfNeed();
    }

    @Override
    public IIndicatorBarProvider getIndicatorBarProvider() {
        return mIndicatorBarProvider;
    }

    @Override
    public IBannerContentProvider getBannerContentProvider() {
        return mBannerContentProvider;
    }

    @Override
    public void setBannerContentProvider(IBannerContentProvider bannerContentProvider) {
        if (mBannerContentProvider != null) {
            mBannerContentProvider.detachBannerContent(this, mBannerContent, mContainer);
        }
        mBannerContentProvider = bannerContentProvider;

        initBannerContentIfNeed();
        initControllerIfNeed();
    }

    /**
     * 设置banner控制器
     *
     * @param bannerController 需要设置的banner控制器
     */
    public void setBannerController(IBannerController bannerController) {
        if (mIBannerController != null) mIBannerController.detach();
        this.mIBannerController = bannerController;
        initControllerIfNeed();
    }

    /**
     * 获取当前的Banner控制器
     *
     * @return
     */
    public IBannerController getBannerController() {
        return mIBannerController;
    }

    @Override
    public void setAdapter(ListAdapter listAdapter) {
        this.mAdapter = listAdapter;
        if (mBannerContentProvider != null) mBannerContentProvider.setAdapter(listAdapter);
        initBannerIndicatorIfNeed();
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mIndicatorBarProvider != null) mIndicatorBarProvider.onWindowFocusChanged(hasWindowFocus);
        if (mIndicatorProvider != null) mIndicatorProvider.onWindowFocusChanged(hasWindowFocus);
        if(mBannerContentProvider != null)mBannerContentProvider.onWindowFocusChanged(hasWindowFocus);
        if(mIBannerController != null)mIBannerController.onWindowFocusChanged(hasWindowFocus);
    }
}
