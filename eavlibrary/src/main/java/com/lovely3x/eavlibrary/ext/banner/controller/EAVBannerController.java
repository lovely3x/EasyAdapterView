package com.lovely3x.eavlibrary.ext.banner.controller;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;

import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.eavlibrary.ext.banner.IBanner;
import com.lovely3x.eavlibrary.ext.banner.IBannerContentProvider;
import com.lovely3x.eavlibrary.ext.banner.IBannerController;
import com.lovely3x.eavlibrary.ext.banner.IIndicatorBarProvider;
import com.lovely3x.eavlibrary.ext.banner.IIndicatorProvider;
import com.lovely3x.eavlibrary.ext.banner.content.EAVBannerContentProvider;

/**
 * Banner 控制器
 * Created by lovely3x on 16/9/17.
 */
public class EAVBannerController implements IBannerController, EasyAdapterView.OnSelectedItemChangedListener, IBannerContentProvider.BannerContentTouchingListener, AdapterView.OnItemClickListener {

    private static final String TAG = "EAVBannerController";

    private static final long DEFAULT_INTERVAL_TIME = 3000;

    private IBanner mBanner;
    private EAVBannerContentProvider.InnerBanner mEAVContent;

    private IBannerContentProvider mIBannerContentProvider;
    private IIndicatorBarProvider mIIndicatorBarProvider;
    private IIndicatorProvider mIIndicatorProvider;

    private long mInterval = DEFAULT_INTERVAL_TIME;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private OnItemClickedListener mOnItemClickedListener;

    private boolean mAutoTruing;

    private boolean initialized;

    private final Runnable NEXT_PAGE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            nextPage();
            mHandler.postDelayed(this, mInterval);
        }
    };


    @Override
    public void init(IBanner banner) {
        this.mBanner = banner;

        this.mIBannerContentProvider = banner.getBannerContentProvider();
        this.mIIndicatorBarProvider = banner.getIndicatorBarProvider();
        this.mIIndicatorProvider = banner.getIndicatorProvider();

        if (mIBannerContentProvider.getBannerContent() instanceof EAVBannerContentProvider.InnerBanner) {
            this.mEAVContent = (EAVBannerContentProvider.InnerBanner) mIBannerContentProvider.getBannerContent();
            this.mEAVContent.setOnSelectedItemChangedListener(this);
            this.mIBannerContentProvider.setBannerContentTouchingListener(this);
            this.mEAVContent.setOnItemClickListener(this);
            initialized = true;
            if (mAutoTruing) startTruing(mInterval);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void attach() {
        if (initialized) {
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
            if (mAutoTruing) {
                mHandler.postDelayed(NEXT_PAGE_RUNNABLE, mInterval);
            }
        }
    }

    @Override
    public void detach() {
        if (initialized) {
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
        }
    }

    @Override
    public void nextPage() {
        if (initialized) {
            if (!mEAVContent.nextPage(350)) {
                //如果没有滚动成功
                //那么移除掉这个下一页的滚动事件
                //重新提交下一次的滚动事件
                mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
                mHandler.postDelayed(NEXT_PAGE_RUNNABLE, mInterval);
            }
        }
    }

    @Override
    public void prePage() {

    }

    @Override
    public void autoTruing(boolean auto) {
        this.mAutoTruing = auto;
        if (auto && initialized) startTruing(mInterval);
    }

    @Override
    public boolean isAutoTruing() {
        return mAutoTruing;
    }

    @Override
    public void startTruing() {
        startTruing(DEFAULT_INTERVAL_TIME);
    }

    @Override
    public void startTruing(long interval) {
        if (initialized) {
            this.mAutoTruing = true;
            if (interval <= 0) interval = DEFAULT_INTERVAL_TIME;
            this.mInterval = interval;
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
            mHandler.postDelayed(NEXT_PAGE_RUNNABLE, mInterval);
        }
    }

    @Override
    public void stopTruing() {
        if (initialized) {
            this.mAutoTruing = false;
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
        }
    }

    @Override
    public void endlessLoop(boolean isEndlessLoop) {
        if (initialized) {
            mEAVContent.setMode(isEndlessLoop ? EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW : EasyAdapterView.ViewMode.WHEEL_VIEW);
        }
    }

    @Override
    public boolean isEndlessLooping() {
        return initialized && mEAVContent.getViewMode() == EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW;
    }

    @Override
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.mOnItemClickedListener = onItemClickedListener;
    }

    @Override
    public void onSelectedItemChanged(EasyAdapterView easyAdapterView, int oldSel, View oldSelView, int position, View selectedView) {

        View indicator = mIIndicatorProvider.getIndicator(oldSel, mIIndicatorBarProvider.getIndicatorBar());
        if (indicator != null) {
            mIIndicatorProvider.refreshIndicatorState(IIndicatorProvider.STATE_UNSELECTED, oldSel, indicator);
        }

        indicator = mIIndicatorProvider.getIndicator(position, mIIndicatorBarProvider.getIndicatorBar());
        if (indicator != null) {
            mIIndicatorProvider.refreshIndicatorState(IIndicatorProvider.STATE_SELECTED, position, indicator);
        }
    }

    @Override
    public void onTouching() {
        if (initialized) {
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
        }
    }

    @Override
    public void onRelease() {
        if (initialized) {
            mHandler.removeCallbacks(NEXT_PAGE_RUNNABLE);
            if (mAutoTruing) {
                mHandler.postDelayed(NEXT_PAGE_RUNNABLE, mInterval);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemClickedListener != null) {
            mOnItemClickedListener.onItemClicked(position);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus && initialized) {
            int pos = mEAVContent.getSelectedItemPosition();
            if (pos != -1) {
              //  mIIndicatorProvider.refreshIndicatorState(IIndicatorProvider.STATE_SELECTED,
                //        pos, mIIndicatorProvider.getIndicator(pos, mIIndicatorBarProvider.getIndicatorBar()));
            }
        }
    }
}
