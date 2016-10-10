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
package com.lovely3x.eavlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.right;
import static android.R.attr.scrollX;
import static android.R.attr.tag;
import static android.R.attr.translateX;
import static android.R.attr.translateY;
import static com.lovely3x.eavlibrary.EasyAdapterView.ViewMode.LIST_VIEW;

/**
 * Easy adapter View
 * Easy use !
 * Like List view or recycler view And wheel view.
 * Created by lovely3x on 16/8/3.
 * 还未完善的:
 * <p/>
 * ① 边缘效果 因为手指不滑动的话,会导致边缘效果动画,自动淡出,但是如果此时手指再次移动会导致动画严重的闪动
 * ④ 多点触摸支持
 * ⑦ 长按Item的支持
 * <p/>
 * <p/>
 */
public class EasyAdapterView extends AdapterView<ListAdapter> {

    private static final String TAG = "EasyAdapterView";

    protected static final boolean DEBUG = false;

    /**
     * 垂直布局
     */
    public static final int LAYOUT_DIRECTLY_VERTICAL = 1;

    /**
     * 水平布局
     */
    public static final int LAYOUT_DIRECTLY_HORIZONTAL = 2;

    /**
     * 滚动触发者:用户
     */
    public static final int SCROLL_TRIGGER_USER = 1;
    /**
     * 滚动触发者:系统
     */
    public static final int SCROLL_TRIGGER_SYSTEM = 2;

    /**
     * 默认平滑滚动velocity值
     */
    public static final float DEFAULT_SMOOTH_ADJUST_ON_FLING_VELOCITY = 70.0F;

    private static final int DEFAULT_SMOOTH_DURATION = 500;

    /**
     * 平滑滚动Velocity值
     * 为什么会有它?
     * 我们需要注意的是在滚轮模式下,我们是可以选择一个视图的
     * 但是这个视图很可能并没有"刚好"在它所属的位置
     * 那么就需要调整,但是调整也同样是需要滚动的
     * 那么如果等到快速滚动模式已经完成,这时再调整就会显得比较突兀
     * 所以我们需要在他
     */
    private float mSmoothFlingVelocity = DEFAULT_SMOOTH_ADJUST_ON_FLING_VELOCITY;

    /**
     * 静止状态
     */
    public static final int REST = 0;

    /**
     * 快速滚动
     */
    public static final int FLING = 1;

    /**
     * 滚动
     */
    public static final int SCROLL = 2;

    /**
     * 回弹
     */
    public static final int SPRING_BACK = 3;

    /**
     * overscroll
     */
    public static final int OVER_SCROLL = 4;

    /**
     * 点击
     */
    public static final int TAP = 4;

    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private final Paint mDebugPaint;

    private final EdgeEffect mBottomEdgeEffect;
    private final EdgeEffect mTopEdgeEffect;

    private final EdgeEffect mLeftEdgeEffect;
    private final EdgeEffect mRightEdgeEffect;

    /**
     * 布局的方向
     */
    private int mLayoutDirectly = LAYOUT_DIRECTLY_VERTICAL;

    /**
     * 视图的类型,默认为普通的list view模式
     */
    private ViewMode mViewMode = ViewMode.ENDLESS_WHEEL_VIEW;

    /**
     * 当前所处的触摸状态
     */
    private int mTouchState = REST;

    /**
     * 正在布局中
     */
    private boolean mInLayout;

    /**
     * 当前的视图的第一个位置
     */
    protected int mFirstPosition;

    /**
     * 距离起点的偏移量
     * 主要用于恢复现场
     * 比如用户在使用notifyDataSetChanged之后,我们需要用这个偏移量来定位新的位置
     */
    protected int mStartOffset;

    /**
     * 当前选中的位置
     */
    protected int mSelectedPosition = INVALID_POSITION;

    private View mSelectedView;

    /**
     * 适配器的数据的数量
     */
    private int mItemCount;

    protected float mLastMotionX = Integer.MIN_VALUE;
    protected float mLastMotionY = Integer.MIN_VALUE;

    protected Rect mItemClickRect = new Rect();
    protected Rect mTempRect = new Rect();

    protected boolean isBeginDrag;

    protected ListAdapter mAdapter;
    protected VelocityTracker mVelocity;
    protected boolean mDataSetInvalidated;

    /**
     * 分割线
     */
    protected Drawable mDivider;

    /**
     * 分割线的高度
     */
    protected int mDividerHeight;

    protected OnItemClickListener mItemClickListener;
    protected OnScrollListener mOnScrollListener;
    protected OnSelectedItemChangedListener mOnSelectedItemChangedListener;

    protected boolean mSmoothScrollbarEnabled = true;
    protected boolean mDataChanged;

    protected int mForceOffsetFromStart;

    /**
     * 当前状态是否是不稳定状态
     */
    protected boolean mIsMutable;

    /**
     * "手动滚动"模式下的一次最大的页码滚动数量
     * 什么叫做"手动滚动"模式? 由用户手动触发的滚动,如 "Fling" "Adjusting" "Scrolling" 等都被当做 "手动滚动"模式!
     * <p>
     * 默认情况下,这个值为-1,表示不限制
     */
    protected int mMaxAmountScrollPage = -1;

    /**
     * 用来记录本次触摸事件产生后，能够达到的最小位置
     * -1 表示不限制
     */
    protected int mMinPosition = -1;
    /**
     * 用来记录本次触摸事件产生后，能够达到的最大位置
     * -1 表示不限制
     */
    protected int mMaxPosition = -1;

    /**
     * 滚动事件触发者
     * 下列枚举值之一 {@link #SCROLL_TRIGGER_SYSTEM} 或 {@link #SCROLL_TRIGGER_USER}
     */
    protected int mScrollTrigger;

    protected int mDownY;
    protected int mDownX;

    protected int mYOverScrollDistance;
    protected int mXOverScrollDistance;

    /**
     * 是否开启overscroll
     */
    protected boolean mOverScroll = true;
    protected Runnable mKeepOverScrollRunnable;
    /**
     * 当前处于活跃状态的OverScroll效果
     */
    private EdgeEffect mActiveEffect;

    public EasyAdapterView(Context context) {
        this(context, null);
    }

    public EasyAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mDebugPaint.setColor(Color.RED);
        this.mDebugPaint.setStrokeWidth(5);
        this.mDebugPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop() / 2;
        this.mVelocity = VelocityTracker.obtain();

        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();

        this.mTopEdgeEffect = new EdgeEffect(getContext());
        this.mBottomEdgeEffect = new EdgeEffect(getContext());

        this.mLeftEdgeEffect = new EdgeEffect(getContext());
        this.mRightEdgeEffect = new EdgeEffect(getContext());

        mDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());
        mDivider = new ColorDrawable(Color.LTGRAY);

        if (attrs != null) initAttrs(attrs);

        setWillNotDraw(false);

        previewInEditMode();
    }

    /**
     * 初始化参数
     *
     * @param attrs 参数
     */
    protected void initAttrs(AttributeSet attrs) {
        if (attrs != null) {

            TypedArray typeArray = null;
            try {
                typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.EasyAdapterView);

                mLayoutDirectly = typeArray.getInteger(R.styleable.EasyAdapterView_orientation, LAYOUT_DIRECTLY_VERTICAL);
                mViewMode = ViewMode.wrap(typeArray.getInteger(R.styleable.EasyAdapterView_mode, LIST_VIEW.mValue));

                mDividerHeight = typeArray.getDimensionPixelOffset(R.styleable.EasyAdapterView_dividerHeight, mDividerHeight);
                Drawable divider = typeArray.getDrawable(R.styleable.EasyAdapterView_divider);
                if (divider != null) mDivider = divider;

                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL: {
                        setHorizontalScrollBarEnabled(true);
                        setVerticalScrollBarEnabled(false);


                    }
                    break;
                    case LAYOUT_DIRECTLY_VERTICAL: {
                        setVerticalScrollBarEnabled(true);
                        setHorizontalScrollBarEnabled(false);
                    }
                    break;
                }

            } finally {
                if (typeArray != null) typeArray.recycle();
            }


        }
    }

    /**
     * 编辑器模式下的预览
     */
    protected void previewInEditMode() {
        if (isInEditMode()) {
            setAdapter(new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    new String[]{
                            "C", "C++", "Java", "Python", "JavaScript", "VB",
                            "Objective-c", "Swift", "Shell",
                            "Perl", "Ruby", "Groovy", "Kotlin",
                            "C#", "CSS", "ANTLR", "Pascal"}) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        LayoutParams lp = view.getLayoutParams();
                        if (lp != null) {
                            lp.height = LayoutParams.WRAP_CONTENT;
                            lp.width = LayoutParams.WRAP_CONTENT;
                        }
                    }
                    return view;
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDivider != null && mDividerHeight > 0) {
            mDivider.setBounds(0, 0, w, mDividerHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int height = 0;

        View firstView = null;

        if (mAdapter != null && mAdapter.getCount() > 0) {
            View childView = firstView = obtainView(0);
            if (childView != null) {
                setupChildView(childView);
                mRecyclerBin.addRecyclerBin(0, childView);
            }
        }

        //测量宽
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (firstView != null) {
                    width = firstView.getMeasuredWidth();
                } else {
                    width = MeasureSpec.getSize(widthMeasureSpec);
                }
                break;
        }

        //测量高
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (firstView != null) {
                    height = firstView.getMeasuredHeight();
                } else {
                    height = MeasureSpec.getSize(heightMeasureSpec);
                }
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            mInLayout = true;
            super.onLayout(changed, left, top, right, bottom);
            invalidLayout();
        } finally {
            mInLayout = false;
        }
    }


    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
        this.mItemClickListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean beginScroll = false;

        float x = ev.getX();
        float y = ev.getY();

        mVelocity.addMovement(ev);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {

                mLastMotionX = x;
                mLastMotionY = y;

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                switch (mTouchState) {
                    case FLING:
                    case SCROLL:
                    case SPRING_BACK:
//                        beginScroll = true;
                        mTouchState = REST;
                        break;
                    default:
                        mFlingRunnable.endScrollerAnimation(false, true);
                        mTouchState = TAP;
                        break;
                }


                if (mMaxAmountScrollPage != -1) {//用户有设定,最大的滚动页码
                    final int first = getSelectedItemPosition();
                    mMaxPosition = first + mMaxAmountScrollPage;
                    mMinPosition = first - mMaxAmountScrollPage;
                }
                mScrollTrigger = SCROLL_TRIGGER_USER;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL:
                        if (mLastMotionX != Integer.MIN_VALUE) {
                            float xDelta = x - mLastMotionX;
                            float absDelta = Math.abs(xDelta);
                            if (absDelta >= mTouchSlop) {
                                beginScroll = true;
                                mTouchState = SCROLL;
                                if (getParent() != null) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                startScrollIfNeed(xDelta, 0);
                            } else {
                                mTouchState = TAP;
                            }
                            mLastMotionX = x;
                            mLastMotionY = y;
                        }
                        break;
                    case LAYOUT_DIRECTLY_VERTICAL: {
                        if (mLastMotionY != Integer.MIN_VALUE) {
                            float yDelta = y - mLastMotionY;
                            float absDelta = Math.abs(yDelta);
                            if (absDelta >= mTouchSlop) {
                                if (getParent() != null) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                mTouchState = SCROLL;
                                beginScroll = true;
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                startScrollIfNeed(0, yDelta);
                            } else {
                                mTouchState = TAP;
                            }
                            mLastMotionX = x/*Integer.MIN_VALUE*/;
                            mLastMotionY = y;
                        }
                    }
                    break;
                }
                break;
            }

        }

        return beginScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        mVelocity.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
//                switch (mTouchState) {
//                    case FLING:
//                    case SCROLL:
//                    case SPRING_BACK:
//                        mTouchState = REST;
//                        break;
//                    default:
//                        mTouchState = TAP;
//                        mFlingRunnable.endScrollerAnimation(false);
//                        break;
//                }
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();

                mDownX = (int) event.getX();
                mDownY = (int) event.getY();

                switch (mTouchState) {
                    case FLING:
                    case SCROLL:
                    case SPRING_BACK:
//                        beginScroll = true;
                        mTouchState = REST;
                        break;
                    default:
                        mFlingRunnable.endScrollerAnimation(false, true);
                        mTouchState = TAP;
                        break;
                }

                if (mMaxAmountScrollPage != -1) {//用户有设定,最大的滚动页码
                    final int first = getSelectedItemPosition();
//                    mMaxPosition = (first + mMaxAmountScrollPage) % (mItemCount - 1);
//                    mMinPosition = (first - mMaxAmountScrollPage) % (mItemCount - 1);
//
                    mMaxPosition = (first + mMaxAmountScrollPage);
                    mMinPosition = (first - mMaxAmountScrollPage);
                }

                mScrollTrigger = SCROLL_TRIGGER_USER;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL:
                        if (mLastMotionX != Integer.MIN_VALUE) {
                            float xDelta = x - mLastMotionX;
                            float absDelta = Math.abs(xDelta);
                            if (isBeginDrag) {
                                mTouchState = SCROLL;
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                startScrollIfNeed(xDelta, 0);
                            } else {
                                if (absDelta >= mTouchSlop) {
                                    isBeginDrag = true;
                                    mTouchState = SCROLL;

                                    if (getParent() != null) {
                                        getParent().requestDisallowInterceptTouchEvent(true);
                                    }
                                    reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                    startScrollIfNeed(xDelta, 0);
                                } else {
                                    mTouchState = TAP;
                                }
                            }
                            mLastMotionX = x;
                            mLastMotionY = y/*Integer.MIN_VALUE*/;
                        }
                        break;
                    case LAYOUT_DIRECTLY_VERTICAL: {
                        if (mLastMotionY != Integer.MIN_VALUE) {
                            float yDelta = y - mLastMotionY;
                            float absDelta = Math.abs(yDelta);
                            if (isBeginDrag) {
                                mTouchState = SCROLL;
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                startScrollIfNeed(0, yDelta);
                            } else {
                                if (absDelta >= mTouchSlop) {
                                    isBeginDrag = true;
                                    mTouchState = SCROLL;
                                    if (getParent() != null) {
                                        getParent().requestDisallowInterceptTouchEvent(true);
                                    }
                                    reportScrollStateChanged(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                    startScrollIfNeed(0, yDelta);
                                } else {
                                    mTouchState = TAP;
                                }
                            }
                            mLastMotionX = x/*Integer.MIN_VALUE*/;
                            mLastMotionY = y;
                        }
                    }
                    break;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isBeginDrag = false;
                mVelocity.computeCurrentVelocity(1000, mMaximumVelocity);

                float yVelocity = mVelocity.getYVelocity();
                float xVelocity = mVelocity.getXVelocity();

                int xDelta = 0;
                if (mDownX != Integer.MIN_VALUE) xDelta = (int) (event.getX() - mDownX);
                int yDelta = 0;
                if (mDownY != Integer.MIN_VALUE) yDelta = (int) (event.getY() - mDownY);

                final int maxDelta = Math.max(Math.abs(xDelta), Math.abs(yDelta));

                boolean needAdjust = true;
                boolean isUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;

                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL: {
                        if (Math.abs(xVelocity) > mMinimumVelocity && isUpEvent && maxDelta >= mTouchSlop) {
                            needAdjust = false;
                            mTouchState = FLING;
                            flingInner(xVelocity, 0);
                        } else {
                            mFlingRunnable.endScrollerAnimation(false);
                            mScrollTrigger = SCROLL_TRIGGER_SYSTEM;

                            if (mTouchState == TAP && isUpEvent) {
                                tryToClickItem((int) x, (int) y);
                                adjustSelView();
                            } else {
                                adjustSelView();
                            }
                        }
                        break;
                    }
                    case LAYOUT_DIRECTLY_VERTICAL: {

                        if (Math.abs(yVelocity) > mMinimumVelocity && isUpEvent && maxDelta >= mTouchSlop) {
                            needAdjust = false;
                            mTouchState = FLING;
                            flingInner(0, yVelocity);
                        } else {
                            mFlingRunnable.endScrollerAnimation(false);
                            mScrollTrigger = SCROLL_TRIGGER_SYSTEM;

                            if (mTouchState == TAP && isUpEvent) {
                                tryToClickItem((int) x, (int) y);
                                adjustSelView();
                            } else {
                                adjustSelView();
                            }
                        }
                        break;
                    }
                }

                if (needAdjust) mVelocity.clear();

                mLastMotionY = Integer.MIN_VALUE;
                mLastMotionX = Integer.MIN_VALUE;

                mDownX = Integer.MIN_VALUE;
                mDownY = Integer.MIN_VALUE;

                mYOverScrollDistance = -1;
                mXOverScrollDistance = -1;
                mActiveEffect = null;

                break;
            }
        }
        return true;
    }

    /**
     * 尝试执行条目点击事件
     *
     * @param x
     * @param y
     */
    protected boolean tryToClickItem(int x, int y) {
        int viewPosition = computeClickViewPosition(x, y);
        if (viewPosition != INVALID_POSITION) {
            View view = getChildAt(viewPosition);
            invokeOnItemClickListener(view, mFirstPosition + viewPosition);
            return true;
        }
        return false;
    }

    /**
     * 调整当前选中的视图
     */
    protected boolean adjustSelView() {
        return adjustSelView(-1);
    }

    /**
     * 使用指定的时长调整当前选中的视图
     *
     * @param duration 调整所需时长
     */
    protected boolean adjustSelView(int duration) {
        if (DEBUG) Log.d(TAG, "Adjusting View duration => " + duration);
        View selectedView = getSelectedView();
        boolean adjustment = false;
        if (selectedView != null) {
            adjustment = true;
            reportScrollStateChanged(OnScrollListener.SCROLL_STATE_ADJUSTMENT);

            switch (mViewMode) {
                case END_LESS_LIST_VIEW:
                case LIST_VIEW: {//ListView 模式
                    //// TODO: 16/8/9
                }
                break;
                case ENDLESS_WHEEL_VIEW:
                case WHEEL_VIEW: {//滚轮模式
                    switch (mLayoutDirectly) {
                        case LAYOUT_DIRECTLY_HORIZONTAL: {//水平模式
                            //首先获取当前选中视图的中点
                            //然后判断XSel点在中心点之前还是之后
                            //根据XSel在中心点之前还是之后,来进行向后滚动还是向前滚动

                            final int selectedViewCenter = selectedView.getLeft() + (selectedView.getRight() - selectedView.getLeft()) / 2;
                            final int xSel = getXSel();

                            int offset = 0;

                            if (selectedViewCenter > xSel) {//当前选中的视图的中心点大于选中线
                                offset = -((selectedViewCenter - xSel));
                            } else if (selectedViewCenter < xSel) {
                                offset = (xSel - selectedViewCenter);
                            }

                            //如果有调整
                            if (offset != 0) {
                                if (duration >= 0) {
                                    mFlingRunnable.startSpringBack(offset, 0, duration);
                                } else {
                                    mFlingRunnable.startSpringBack(offset, 0);
                                }
                            } else {
                                resetTriggerToSystem();
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_IDLE);
                            }
                        }
                        break;
                        case LAYOUT_DIRECTLY_VERTICAL: {//垂直模式

                            final int selectedViewCenter = selectedView.getTop() + (selectedView.getBottom() - selectedView.getTop()) / 2;
                            final int ySel = getYSel();
                            int offset = 0;

                            if (selectedViewCenter < ySel) {//当前选中的视图的中心点小于选中线
                                offset = ySel - selectedViewCenter;
                            } else if (selectedViewCenter > ySel) {//当前选中的视图的中心大于选中线
                                offset = -(selectedViewCenter - ySel);
                            }

                            if (offset != 0) {
                                if (duration >= 0) {
                                    mFlingRunnable.startSpringBack(0, offset, duration);
                                } else {
                                    mFlingRunnable.startSpringBack(0, offset);
                                }
                            } else {
                                resetTriggerToSystem();
                                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_IDLE);
                            }
                        }
                        break;
                    }
                }
                break;
            }
        } else {
            reportScrollStateChanged(OnScrollListener.SCROLL_STATE_IDLE);
            mTouchState = REST;
        }

        return adjustment;
    }

    /**
     * 飞起来
     *
     * @param xVelocity
     * @param yVelocity
     */
    protected void flingInner(float xVelocity, float yVelocity) {
        if (DEBUG)
            Log.d(TAG, "Fling xVelocity " + " , " + xVelocity + " yVelocity " + yVelocity);
        mFlingRunnable.startFling(-(int) (xVelocity * 1.5f), -(int) (yVelocity * 1.5f));
    }


    /**
     * 重置触发器为系统
     */
    protected void resetTriggerToSystem() {
        mScrollTrigger = SCROLL_TRIGGER_SYSTEM;
    }

    /**
     * 偏移所有子视图的top和bottom属性
     *
     * @param offset 需要偏移的偏移量
     */
    public void offsetChildrenTopAndBottom(int offset) {
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            v.setTop(v.getTop() + offset);
            v.setBottom(v.getBottom() + offset);
        }

        invalidate();
    }

    /**
     * 偏移所有的子视图的left和right属性
     *
     * @param offset 需要偏移的偏移量
     */
    public void offsetChildrenLeftAndRight(int offset) {
        if (DEBUG) Log.i(TAG, "OffsetChildrenLeftAndRight => " + offset);
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            v.setLeft(v.getLeft() + offset);
            v.setRight(v.getRight() + offset);
        }
        invalidate();
    }

    @Override
    public boolean canScrollVertically(int direction) {
        switch (mViewMode) {
            case LIST_VIEW: {//ListView 模式
                if (direction > 0) {//往下滑动
                    return !(mFirstPosition == 0 && getChildAt(0).getTop() >= 0/*getTop()*/);
                } else if (direction < 0) {//往上滑动
                    return !(mFirstPosition + getChildCount() >= mItemCount && getChildAt(getChildCount() - 1).getBottom() <= (getBottom() - getTop()));
                }
            }
            break;
            case WHEEL_VIEW: {//滚轮模式
                if (direction > 0) {//向下滑动
                    if (mFirstPosition == 0) {//已经滑动第一个位置
                        View firstChild = getChildAt(0);
                        int ySel = getYSel() - (firstChild.getBottom() - firstChild.getTop()) / 2;
                        if (firstChild.getTop() >= ySel) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if (direction < 0) {//向上滑动
                    if (mFirstPosition + getChildCount() >= mItemCount) {//已经滚动到最后一个视图
                        View lastChild = getChildAt(getChildCount() - 1);
                        int ySel = getBottom() - getYSel() + ((lastChild.getBottom() - lastChild.getTop()) / 2);
                        if (lastChild.getBottom() < ySel) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
            break;
            case END_LESS_LIST_VIEW: //无尽ListView模式
            case ENDLESS_WHEEL_VIEW: {//无尽滚轮模式
                return true;
            }
        }
        return super.canScrollVertically(direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        switch (mViewMode) {
            case LIST_VIEW: {///ListView 模式
                if (direction > 0) {//往右滑动
                    return !(mFirstPosition == 0 && getChildAt(0).getLeft() >= getLeft());
                } else if (direction < 0) {//往左滑动
                    return !(mFirstPosition + getChildCount() >= mItemCount && getChildAt(getChildCount() - 1).getRight() <= getRight());
                }
            }
            break;
            case WHEEL_VIEW: {//滚轮模式
                if (direction > 0) {//往右滑动
                    if (mFirstPosition == 0) {
                        final View firstChild = getChildAt(0);
                        final int xSel = getXSel();
                        // 你可以通过这个 中心点 来控制可以拖动的范围
                        // 0 就是可以拖动到视图的最左边
                        // ((lastChild.getRight() - lastChild.getLeft()) /2) 就是拖动大视图的中点
                        final int firstChildCenter = ((firstChild.getRight() - firstChild.getLeft()) / 2);
                        if (firstChild.getLeft() + firstChildCenter >= xSel) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else if (direction < 0) {//往左滑动
                    if (mFirstPosition + getChildCount() >= mItemCount) {//滑动到最后一个了
                        View lastChild = getChildAt(getChildCount() - 1);
                        int xSel = getXSel();
                        //你可以通过这个 中心点 来控制可以拖动的范围
                        // 0 就是可以拖动到视图的最右边
                        //((lastChild.getRight() - lastChild.getLeft()) /2) 就是拖动大视图的中点
                        int lastChildCenter = ((lastChild.getRight() - lastChild.getLeft()) / 2);
                        if (lastChild.getRight() - lastChildCenter <= xSel) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
            break;
            case END_LESS_LIST_VIEW://无尽ListView模式
            case ENDLESS_WHEEL_VIEW://无尽滚轮模式
                return true;
        }
        return super.canScrollHorizontally(direction);
    }

    /**
     * 根据最大滚动页码限制来调整垂直方向上滚动的距离
     *
     * @param yDelta 尝试滚动的垂直方向距离
     * @return 还可以继续滚动的距离
     */
    protected int verticalAdjustWithMaxScrollRestrict(int yDelta) {
        if (DEBUG)
            Log.i(TAG, "VerticalAdjustWithMaxScrollRestrict Scroll trigger " + mScrollTrigger);

        if (mScrollTrigger == SCROLL_TRIGGER_USER && mMaxAmountScrollPage != -1) {//只有触发者是 User 时,才需要去拦截

            final int selViewIndex = getSelectedViewPosition();
            if (selViewIndex == INVALID_POSITION) return 0;

            mMinPosition %= mItemCount;
            if (mMinPosition < 0) mMinPosition = mItemCount + mMinPosition;
            mMaxPosition %= mItemCount;

            boolean isLooping = mMaxPosition < mMinPosition;//是否正在进行循环过渡

            View view = getChildAt(selViewIndex);
            final int firstPosition = (selViewIndex + mFirstPosition) % mItemCount;
            if (yDelta > 0) {//往下拉
                if (firstPosition < mMinPosition && !isLooping) {
                    return 0;
                } else if (firstPosition == mMinPosition) {
                    switch (mViewMode) {
                        case LIST_VIEW:
                        case END_LESS_LIST_VIEW: {
                            View firstView = getChildAt(0);
                            if (firstView != null) {
                                return Math.min(yDelta, Math.max(0, getTop() - firstView.getTop()));
                            }
                        }
                        break;
                        case WHEEL_VIEW:
                        case ENDLESS_WHEEL_VIEW: {
                            if (mSelectedView != null) {
                                return Math.max(0, Math.min(getYSel() - mSelectedView.getBottom() + mSelectedView.getHeight() / 2, yDelta));
                            } else {
                                return 0;
                            }
                        }
                    }
                } else {
                    return Math.min(yDelta, view.getHeight() / 2);
                }

            } else if (yDelta < 0) {//往上提
                if (firstPosition > mMaxPosition && !isLooping) {
                    return 0;
                } else if (firstPosition == mMaxPosition) {
                    switch (mViewMode) {
                        case LIST_VIEW:
                        case END_LESS_LIST_VIEW: {
                            View firstView = getChildAt(0);
                            if (firstView != null) {
                                return Math.max(yDelta, Math.min(0, getTop() - firstView.getBottom()));
                            }
                        }
                        break;
                        case WHEEL_VIEW:
                        case ENDLESS_WHEEL_VIEW: {
                            if (mSelectedView != null) {
                                return Math.max(yDelta, -Math.max(0, (mSelectedView.getTop() + mSelectedView.getHeight() / 2) - getYSel()));
                            } else {
                                return 0;
                            }
                        }
                    }
                } else {
                    return Math.max(yDelta, -view.getHeight() / 2);
                }
            } else {
                return 0;
            }
        }
        return yDelta;
    }

    /**
     * 根据最大滚动页码限制来调整水平方向上滚动的距离
     *
     * @param xDelta 尝试在水平方向上的滚动距离
     * @return 还可以继续滚动的距离
     */
    protected int horizontalAdjustWithMaxScrollRestrict(int xDelta) {
        if (DEBUG)
            Log.i(TAG, "horizontalAdjustWithMaxScrollRestrict Scroll trigger " + mScrollTrigger);

        if (mScrollTrigger == SCROLL_TRIGGER_USER && mMaxAmountScrollPage != -1) {//只有触发者是 User 时,才需要去拦截

            final int selViewIndex = getSelectedViewPosition();
            if (selViewIndex == INVALID_POSITION) return 0;

            mMinPosition %= mItemCount;
            if (mMinPosition < 0) {
                mMinPosition = mItemCount + mMinPosition;
            }
            mMaxPosition %= mItemCount;
            boolean isLooping = mMaxPosition < mMinPosition;//是否正在进行循环过渡

            View view = getChildAt(selViewIndex);
            final int firstPosition = (selViewIndex + mFirstPosition) % mItemCount;

            if (xDelta > 0) {//往右拉
                if (firstPosition < mMinPosition && !isLooping) {
                    return 0;
                } else if (firstPosition == mMinPosition) {
                    switch (mViewMode) {
                        case LIST_VIEW:
                        case END_LESS_LIST_VIEW: {
                            View firstView = getChildAt(0);
                            if (firstView != null) {
                                return Math.min(xDelta, Math.max(0, getLeft() - firstView.getLeft()));
                            }
                        }
                        break;
                        case WHEEL_VIEW:
                        case ENDLESS_WHEEL_VIEW: {
                            if (mSelectedView != null) {
                                return Math.max(0, Math.min(getXSel() - mSelectedView.getRight() + mSelectedView.getWidth() / 2, xDelta));
                            } else {
                                return 0;
                            }
                        }
                    }
                } else {
                    return Math.min(xDelta, view.getWidth() / 2);
                }

            } else if (xDelta < 0) {//往左拉
                if (firstPosition > mMaxPosition && !isLooping) {
                    return 0;
                } else if (firstPosition == mMaxPosition) {
                    switch (mViewMode) {
                        case LIST_VIEW:
                        case END_LESS_LIST_VIEW: {
                            View firstView = getChildAt(0);
                            if (firstView != null) {
                                return Math.max(xDelta, Math.min(0, getLeft() - firstView.getRight()));
                            }
                        }
                        break;
                        case WHEEL_VIEW:
                        case ENDLESS_WHEEL_VIEW: {
                            if (mSelectedView != null) {
                                return Math.max(xDelta, -Math.max(0, (mSelectedView.getLeft() + mSelectedView.getWidth() / 2) - getXSel()));
                            } else {
                                return 0;
                            }
                        }
                    }
                } else {
                    return Math.max(xDelta, -view.getWidth() / 2);
                }
            } else {
                return 0;
            }
        }
        return xDelta;
    }

    /**
     * 在水平方向滚动
     *
     * @param xDelta
     * @return 是否发生了滚动
     */
    protected boolean horizontalScroll(int xDelta) {
        if (DEBUG) Log.i(TAG, "HorizontalScroll " + xDelta);
        final int childCount = getChildCount();

        View firstChild = getChildAt(0);
        View lastChild = getChildAt(childCount - 1);

        int offScreenChildStart = 0;
        int offScreenChildCount = 0;


        if (xDelta > 0) {//往右滑动
            for (int i = getChildCount() - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                if (child.getLeft() + xDelta - mDividerHeight < (getRight() - getLeft())/*getRight()*/) {//没有越出边界
                    break;
                } else {
                    offScreenChildStart = i;//很巧妙,倒序遍历
                    offScreenChildCount++;
                    int position = mFirstPosition + i;
                    mRecyclerBin.addRecyclerBin(position, child);
                }
            }
        } else if (xDelta < 0) {//向左滑动
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getRight() + xDelta + mDividerHeight > 0/*getLeft()*/) {
                    break;
                } else {
                    offScreenChildCount++;
                    int position = mFirstPosition + i;
                    mRecyclerBin.addRecyclerBin(position, child);
                }
            }
        } else {
            //Ignored
        }

        if (offScreenChildCount > 0) {
            //将超出边界的子视图移除掉
            detachViewsFromParent(offScreenChildStart, offScreenChildCount);
        }

        offsetChildrenLeftAndRight(xDelta);

        if (!awakenScrollBars()) {
            invalidate();
        }

        if (xDelta < 0) {
            fillRightView(mFirstPosition + childCount, lastChild.getRight() + mDividerHeight, offScreenChildCount);
            correctTooRight(getChildCount());
        } else {
            fillLeftView(mFirstPosition - 1, firstChild.getLeft() - mDividerHeight);
            correctTooLeft(getChildCount());
        }

        return xDelta != 0;
    }

    /**
     * 在垂直方向上移动
     *
     * @param yDelta
     */
    protected boolean verticalScroll(int yDelta) {
        if (DEBUG) Log.i(TAG, "VerticalScroll " + yDelta);

        final int childCount = getChildCount();

        View firstChild = getChildAt(0);
        View lastChild = getChildAt(childCount - 1);

        int offScreenChildStart = 0;
        int offScreenChildCount = 0;

        if (yDelta > 0) {//往下滑动
            for (int i = getChildCount() - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                if (child.getTop() + yDelta - mDividerHeight < (getBottom() - getTop())) {//没有越出边界
                    break;
                } else {
                    Log.i(TAG, "View OffScreen => " + i);
                    offScreenChildStart = i;
                    offScreenChildCount++;
                    int position = mFirstPosition + i;
                    mRecyclerBin.addRecyclerBin(position, child);
                }
            }
        } else if (yDelta < 0) {//向上滑动
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getBottom() + yDelta + mDividerHeight > 0/*getTop()*/) {
                    break;
                } else {
                    Log.i(TAG, "View OffScreen => " + i);
                    offScreenChildCount++;
                    int position = mFirstPosition + i;
                    mRecyclerBin.addRecyclerBin(position, child);
                }
            }
        } else {
            //Ignored
        }

        if (offScreenChildCount > 0) {
            //将超出边界的子视图移除掉
            detachViewsFromParent(offScreenChildStart, offScreenChildCount);
        }

        //在滑动之后有调整阶段,所以去掉了这里
//        if (yDelta < 0) {//往上滑动
//            //如果滑动到最后一个视图,还在上滑动,那么久需要注意,防止滑动超出
//            if (mFirstPosition + getChildCount() == mItemCount) {
//                int scrollMaxDistance = getChildAt(getChildCount() - 1).getBottom() - getBottom();
//                yDelta = Math.max(-scrollMaxDistance, yDelta);
//            }
//        } else if (yDelta > 0) {//往下滑动
//            if (mFirstPosition == 0) {
//                //如果滑动到第一个视图,还在往下滑动,那么就需要注意防止滑动超出
//                int scrollMaxDistance = getTop() - getChildAt(0).getTop();
//                yDelta = Math.min(scrollMaxDistance, yDelta);
//            }
//        }


        if (!awakenScrollBars()) {
            invalidate();
        }

        offsetChildrenTopAndBottom(yDelta);

        if (yDelta < 0) {
            fillDownView(mFirstPosition + childCount, lastChild.getBottom() + mDividerHeight, offScreenChildCount);
            correctTooHigh(getChildCount());
        } else {
            fillUpView(mFirstPosition - 1, firstChild.getTop() - mDividerHeight);
            correctTooLow(getChildCount());
        }
        return yDelta != 0;
    }

    protected void invalidateTopGlow() {
        if (mTopEdgeEffect == null) {
            return;
        }
        final int top = 0;
        final int left = 0;
        final int right = getWidth();
        invalidate(left, top, right, top + mTopEdgeEffect.getMaxHeight());
    }

    protected void invalidateBottomGlow() {
        if (mBottomEdgeEffect == null) {
            return;
        }
        final int bottom = getHeight();
        final int left = 0;
        final int right = getWidth();
        invalidate(left, bottom - mBottomEdgeEffect.getMaxHeight(), right, bottom);
    }

    protected void invalidateRightGlow() {
        if (mRightEdgeEffect == null) {
            return;
        }

        final int bottom = getHeight();
        final int top = 0;
        final int right = getWidth();
        invalidate(/*right - mRightEdgeEffect.getMaxHeight(), top, right, bottom*/);
    }

    protected void invalidateLeftGlow() {
        if (mLeftEdgeEffect == null) {
            return;
        }

        final int top = 0;
        final int left = 0;
        final int bottom = getHeight();

        invalidate(left, top, left + mLeftEdgeEffect.getMaxHeight(), bottom);
    }

    /**
     * 设置是否开启overscroll
     *
     * @param overScroll
     */
    public void setOverScroll(boolean overScroll) {
        this.mOverScroll = overScroll;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //在开启overscroll的情况下绘制overscroll效果
        if (mOverScroll) drawOverScrollEffect(canvas);
    }

    /**
     * 绘制overscroll效果
     *
     * @param canvas
     */
    protected void drawOverScrollEffect(Canvas canvas) {
        final int scrollY = getScrollY();
        final int scrollX = getScrollX();

        final int width;
        final int height;
        final int translateX;
        final int translateY;

        width = getWidth();
        height = getHeight();

        translateX = 0;
        translateY = 0;

        if (mTopEdgeEffect != null) {//绘制顶部的边缘效果
            if (!mTopEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                canvas.clipRect(translateX, translateY, translateX + width, translateY + mTopEdgeEffect.getMaxHeight());
                final int edgeY = Math.min(0, scrollY) + translateY;
                canvas.translate(translateX, edgeY);
                mTopEdgeEffect.setSize(width, height);
                if (mTopEdgeEffect.draw(canvas)) {
                    invalidateTopGlow();
                }
                canvas.restoreToCount(restoreCount);
            }
        }

        if (mBottomEdgeEffect != null) {//绘制底部的边缘效果
            if (!mBottomEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                canvas.clipRect(translateX, translateY + height - mBottomEdgeEffect.getMaxHeight(), translateX + width, translateY + height);
                final int edgeX = -width + translateX;
                final int edgeY = Math.max(getHeight(), scrollY);
                canvas.translate(edgeX, edgeY);
                canvas.rotate(180, width, 0);
                mBottomEdgeEffect.setSize(width, height);
                if (mBottomEdgeEffect.draw(canvas)) {
                    invalidateBottomGlow();
                }
                canvas.restoreToCount(restoreCount);
            }
        }


        if (mLeftEdgeEffect != null) {//绘制左边的边缘效果
            if (!mLeftEdgeEffect.isFinished()) {
                canvas.clipRect(translateX, translateY, translateX + mLeftEdgeEffect.getMaxHeight(), translateY + height);
                final int restoreCount = canvas.save();
                canvas.translate(-width, getHeight() - getWidth());//位移画布
                canvas.rotate(270, width, 0);//旋转画布
                mLeftEdgeEffect.setSize(height, width);//交换宽高
                if (mLeftEdgeEffect.draw(canvas)) {
                    invalidateLeftGlow();
                }
                canvas.restoreToCount(restoreCount);
            }
        }

        if (mRightEdgeEffect != null) {//绘制右边的边缘效果
            if (!mRightEdgeEffect.isFinished()) {
                //     canvas.clipRect(translateX, width - mRightEdgeEffect.getMaxHeight(), width, translateY + height);
                final int restoreCount = canvas.save();
                canvas.translate(0, getWidth());//位移画布
                canvas.rotate(-270, width, 0);//旋转画布
                mRightEdgeEffect.setSize(height, width);//交换宽高
                if (mRightEdgeEffect.draw(canvas)) {
                    invalidateRightGlow();
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    /**
     * 如果我们将这个视图的底部拖动的过高
     * 我们尝试通过滑动所有的视图
     * 使其返回到正确的位置
     *
     * @param childCount 子视图的数量
     */
    protected void correctTooHigh(int childCount) {
        switch (mViewMode) {
            case LIST_VIEW: {//ListView 模式
                correctTooHighForListView(childCount);
            }
            break;
            case WHEEL_VIEW: {//滚轮模式
                correctTooHighForWheelView(childCount);
            }
            break;
        }

    }

    /**
     * 为WheelView模式下的视图调整过高问题
     *
     * @param childCount
     */
    protected void correctTooHighForWheelView(int childCount) {
        //首先判断最后一个视图是否可见
        //如果不可见,那么不需要去调整
        int lastPosition = mFirstPosition + childCount - 1;

        //如果已经滚动到最后一个视图,且有子视图
        if (lastPosition == mItemCount - 1 && childCount > 0) {

            //获取最后一个子视图
            final View lastChild = getChildAt(childCount - 1);

            //获取最后一个子视图的底部边缘点
            final int lastBottom = lastChild.getBottom();

            //获取我们绘制区域
            final int end = getBottom() - getYSel() + ((lastChild.getBottom() - lastChild.getTop()) / 2) - getTop();

            //最后一个视图的边缘距离我们的绘制区域底部有多远
            int bottomOffset = end - lastBottom;

            //获取第一个子视图
            View firstChild = getChildAt(0);
            //获取第一个子视图的顶部边缘点
            final int firstTop = firstChild.getTop();

            //1) 确保我们是 "过高的"
            //2) 无论我们的顶部是否有更多行或只有一行
            //判断第一行的顶部是否滚动出了绘制区域
            if (bottomOffset > 0 && (mFirstPosition > 0 || firstTop < getTop())) {

                if (mFirstPosition == 0) {
                    //不要将顶部拉的过远
                    bottomOffset = Math.min(bottomOffset, getTop() - firstTop);
                }

                // 将所有的子视图向下移动
                offsetChildrenTopAndBottom(bottomOffset);
//                startScrollIfNeed(0, bottomOffset);
                if (mFirstPosition > 0) {
                    //尝试填充所有的空白区域
                    fillUpView(mFirstPosition - 1, firstChild.getTop() - mDividerHeight);
                    //调整剩下的空白区域
                    adjustViewsUpOrDown();
                }

            }
        }
    }

    /**
     * 为ListView模式下的
     *
     * @param childCount
     */
    protected void correctTooHighForListView(int childCount) {
        //首先判断最后一个视图是否可见
        //如果不可见,那么不需要去调整
        int lastPosition = mFirstPosition + childCount - 1;

        //如果已经滚动到最后一个视图,且有子视图
        if (lastPosition == mItemCount - 1 && childCount > 0) {

            //获取最后一个子视图
            final View lastChild = getChildAt(childCount - 1);

            //获取最后一个子视图的底部边缘点
            final int lastBottom = lastChild.getBottom();

            //获取我们绘制区域
            final int end = getBottom() - getTop();

            //最后一个视图的边缘距离我们的绘制区域底部有多远
            int bottomOffset = end - lastBottom;

            //获取第一个子视图
            View firstChild = getChildAt(0);
            //获取第一个子视图的顶部边缘点
            final int firstTop = firstChild.getTop();

            //1) 确保我们是 "过高的"
            //2) 无论我们的顶部是否有更多行或只有一行
            //判断第一行的顶部是否滚动出了绘制区域
            if (bottomOffset > 0 && (mFirstPosition > 0 || firstTop < getTop())) {

                if (mFirstPosition == 0) {
                    //不要将顶部拉的过远
                    bottomOffset = Math.min(bottomOffset, getTop() - firstTop);
                }

                // 将所有的子视图向下移动
                offsetChildrenTopAndBottom(bottomOffset);
//                startScrollIfNeed(0, bottomOffset);
                if (mFirstPosition > 0) {
                    //尝试填充所有的空白区域
                    fillUpView(mFirstPosition - 1, firstChild.getTop() - mDividerHeight);
                    adjustViewsUpOrDown();
                }

            }
        }
    }

    /**
     * 如果我们将这个视图的底部拖动的过低
     * 我们尝试将 所有的子视图进行滑动到正确的位置
     *
     * @param childCount 子视图的数量
     */
    private void correctTooLow(int childCount) {
        switch (mViewMode) {
            case LIST_VIEW: {//ListView 模式
                correctTooLowForListView(childCount);
            }
            break;
            case WHEEL_VIEW: {//滚轮模式
                correctTooLowForWheelView(childCount);
            }
            break;
        }

    }

    /**
     * 为WheelView模式下的视图调整过低问题
     *
     * @param childCount
     */
    protected void correctTooLowForWheelView(int childCount) {
        //首先,判断第一个条目视图是否可见
        //如果不是第一条条目视图
        //那么不需要调整
        //否则尝试将视图推到正确的位置
        if (mFirstPosition == 0 && childCount > 0) {

            // 获取第一个视图
            final View firstChild = getChildAt(0);

            //获取第一个视图的顶部边缘
            final int firstTop = firstChild.getTop();

            //获取我们的绘制区域的顶部边缘
            final int start = getTop();

            //获取我们绘制区域的底部边缘
            final int end = (getBottom() - getTop());

            //第一个视图顶部边缘和绘制区域的顶部之间的距离
            int topOffset = firstTop - getYSel() + ((firstChild.getBottom() - firstChild.getTop()) / 2) - start;

            View lastChild = getChildAt(childCount - 1);

            final int lastBottom = lastChild.getBottom();

            int lastPosition = mFirstPosition + childCount - 1;

            if (topOffset > 0) {
                if (lastPosition < mItemCount - 1 || lastBottom > end) {
                    if (lastPosition == mItemCount - 1) {
                        // Don't pull the bottom too far up
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }

                    //将所有的子视图向上移动
                    offsetChildrenTopAndBottom(-topOffset);
//                    startScrollIfNeed(0, -topOffset);

                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillDownView(lastPosition + 1, lastChild.getBottom() + mDividerHeight, 0);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /**
     * 为ListView模式下的视图调整过低问题
     *
     * @param childCount
     */
    protected void correctTooLowForListView(int childCount) {
        //首先,判断第一个条目视图是否可见
        //如果不是第一条条目视图
        //那么不需要调整
        //否则尝试将视图推到正确的位置
        if (mFirstPosition == 0 && childCount > 0) {

            // 获取第一个视图
            final View firstChild = getChildAt(0);

            //获取第一个视图的顶部边缘
            final int firstTop = firstChild.getTop();

            //获取我们的绘制区域的顶部边缘
            final int start = /*getTop();*/0;

            //获取我们绘制区域的底部边缘
            final int end = (getBottom() - getTop());

            //第一个视图顶部边缘和绘制区域的顶部之间的距离
            int topOffset = firstTop - start;

            View lastChild = getChildAt(childCount - 1);

            final int lastBottom = lastChild.getBottom();

            int lastPosition = mFirstPosition + childCount - 1;

            // Make sure we are 1) Too low, and 2) Either there are more rows below the
            // last row or the last row is scrolled off the bottom of the drawable area
            if (topOffset > 0) {
                if (lastPosition < mItemCount - 1 || lastBottom > end) {
                    if (lastPosition == mItemCount - 1) {
                        // Don't pull the bottom too far up
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }

                    //将所有的子视图向上移动
                    offsetChildrenTopAndBottom(-topOffset);
//                    startScrollIfNeed(0, -topOffset);
                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillDownView(lastPosition + 1, lastChild.getBottom() + mDividerHeight, 0);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /**
     * 调整当前的视图的上下偏移
     * 主要作用就是不能让用户选中分割线
     * 选中分割线就尴尬了
     */
    protected void adjustViewsUpOrDown() {
        //// TODO: 16/8/9
        Log.i(TAG, "AdjustViewsUpOrDown");
    }

    /**
     * 调整当前的视图的左右偏移
     * 主要作用就是不能让用户选中分割线
     */
    protected void adjustViewLeftOrRight() {
        //// TODO: 16/8/9
    }

    /**
     * 调整过左问题
     *
     * @param childCount 当前子视图数量
     */
    private void correctTooLeft(int childCount) {
        switch (mViewMode) {
            case LIST_VIEW: {//普通的list view 模式
                correctTooLeftForListView(childCount);
            }
            break;
            case WHEEL_VIEW: {
                correctTooLeftForWheelView(childCount);
            }
            break;
        }
    }

    /**
     * 调整WheelView模式下的过左问题
     *
     * @param childCount
     */
    protected void correctTooLeftForWheelView(int childCount) {
        //首先,判断第一个条目视图是否可见
        //如果不是第一条条目视图
        //那么不需要调整
        //否则尝试将视图推到正确的位置
        if (mFirstPosition == 0 && childCount > 0) {

            // 获取第一个视图
            final View firstChild = getChildAt(0);

            //获取第一个视图的顶部边缘
            final int firstLeft = firstChild.getLeft();

            //获取我们的绘制区域的顶部边缘
            final int start = getLeft() + getXSel();

            //获取我们绘制区域的底部边缘
            final int end = (getRight() - getLeft());

            //第一个视图顶部边缘和绘制区域的顶部之间的距离
            int startOffset = firstLeft - start;

            View lastChild = getChildAt(childCount - 1);

            final int lastRight = lastChild.getRight();

            int lastPosition = mFirstPosition + childCount - 1;

            // Make sure we are 1) Too low, and 2) Either there are more rows below the
            // last row or the last row is scrolled off the bottom of the drawable area
            if (startOffset > 0) {
                if (lastPosition < mItemCount - 1 || lastRight > end) {
                    if (lastPosition == mItemCount - 1) {
                        // Don't pull the bottom too far up
                        startOffset = Math.min(startOffset, lastRight - end);
                    }

                    offsetChildrenLeftAndRight(-startOffset);
                    //将所有的子视图向左移动
//                    startScrollIfNeed(-startOffset, 0);

                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillRightView(lastPosition + 1, lastChild.getRight() + mDividerHeight, 0);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }

    }

    /**
     * 调整ListView模式下的过左问题
     *
     * @param childCount
     */
    protected void correctTooLeftForListView(int childCount) {
        //首先,判断第一个条目视图是否可见
        //如果不是第一条条目视图
        //那么不需要调整
        //否则尝试将视图推到正确的位置
        if (mFirstPosition == 0 && childCount > 0) {

            // 获取第一个视图
            final View firstChild = getChildAt(0);

            //获取第一个视图的顶部边缘
            final int firstLeft = firstChild.getLeft();

            //获取我们的绘制区域的顶部边缘
            final int start = getLeft();

            //获取我们绘制区域的底部边缘
            final int end = (getRight() - getLeft());

            //第一个视图顶部边缘和绘制区域的顶部之间的距离
            int startOffset = firstLeft - start;

            View lastChild = getChildAt(childCount - 1);

            final int lastRight = lastChild.getRight();

            int lastPosition = mFirstPosition + childCount - 1;

            // Make sure we are 1) Too low, and 2) Either there are more rows below the
            // last row or the last row is scrolled off the bottom of the drawable area
            if (startOffset > 0) {
                if (lastPosition < mItemCount - 1 || lastRight > end) {
                    if (lastPosition == mItemCount - 1) {
                        // Don't pull the bottom too far up
                        startOffset = Math.min(startOffset, lastRight - end);
                    }

                    //将所有的子视图向右移动
                    offsetChildrenLeftAndRight(-startOffset);
//                    startScrollIfNeed(-startOffset, 0);

                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillRightView(lastPosition + 1, lastChild.getRight() + mDividerHeight, 0);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /**
     * 调整过右问题
     *
     * @param childCount 当前子视图的数量
     */
    private void correctTooRight(int childCount) {
        switch (mViewMode) {
            case LIST_VIEW: {//普通的list view 模式
                correctTooRightForListView(childCount);
            }
            break;
            case WHEEL_VIEW: {//滚轮模式
                correctTooRightForWheelView(childCount);
            }
            break;
        }
    }

    /**
     * 调整 WheelView模式下的过右问题
     *
     * @param childCount
     */
    protected void correctTooRightForWheelView(int childCount) {
        //首先判断最后一个视图是否可见
        //如果不可见,那么不需要去调整
        int lastPosition = mFirstPosition + childCount - 1;

        //如果已经滚动到最后一个视图,且有子视图
        if (lastPosition == mItemCount - 1 && childCount > 0) {

            //获取最后一个子视图
            final View lastChild = getChildAt(childCount - 1);

            //获取最后一个子视图的右边边缘点
            final int lastRight = lastChild.getRight();

            //获取我们绘制区域
            int xSel = getRight() - getXSel() + (lastChild.getRight() - lastChild.getLeft()) / 2;

            //假设 EAV 的 right 为 1080
            //假设 XSel 的 为 500
//            假设 LastChild width = 800
            //什么情况为 过右?
            //过右:当 LastChild的Right 小于 XSel的值时
            //Xsel值不能直接使用,应该先转换
            //Xsel = EAV.right - Xsel + lastchildwidth /2 = 980 = (1080 - 500) + 800 /2
            //Offset = Xsel - lastChild.right

            //最后一个视图的边缘距离我们的绘制区域右边有多远
            int rightOffset = xSel - lastRight;

            //获取第一个子视图
            View firstChild = getChildAt(0);

            //获取第一个子视图的左边缘点
            final int firstLeft = firstChild.getLeft();

            //1) 确保我们是 "过右的"
            if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < getLeft())) {

                if (mFirstPosition == 0) {
                    //不要将右边部拉的过远
                    rightOffset = Math.min(rightOffset, getLeft() - firstLeft);
                }

                // 将所有的子视图向左右移动
                offsetChildrenLeftAndRight(rightOffset);
//                startScrollIfNeed(rightOffset, 0);

                if (mFirstPosition > 0) {
                    //尝试填充所有的空白区域
                    fillRightView(lastPosition + 1, lastChild.getRight() + mDividerHeight, 0);
                    //调整剩下的空白区域
                    adjustViewsUpOrDown();
                }

            }
        }
    }

    /**
     * 调整ListView模式下的过右问题
     *
     * @param childCount
     */
    protected void correctTooRightForListView(int childCount) {
        //首先判断最后一个视图是否可见
        //如果不可见,那么不需要去调整
        int lastPosition = mFirstPosition + childCount - 1;

        //如果已经滚动到最后一个视图,且有子视图
        if (lastPosition == mItemCount - 1 && childCount > 0) {

            //获取最后一个子视图
            final View lastChild = getChildAt(childCount - 1);

            //获取最后一个子视图的右边边缘点
            final int lastRight = lastChild.getRight();

            //获取我们绘制区域
            final int end = getRight() - getLeft();

            //最后一个视图的边缘距离我们的绘制区域右边有多远
            int rightOffset = end - lastRight;

            //获取第一个子视图
            View firstChild = getChildAt(0);
            //获取第一个子视图的左部边缘点
            final int firstLeft = firstChild.getLeft();

            //1) 确保我们是 "过高的"
            //2) 无论我们的顶部是否有更多行或只有一行
            //判断第一行的顶部是否滚动出了绘制区域
            if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < getLeft())) {

                if (mFirstPosition == 0) {
                    //不要将左边拉的过远
                    rightOffset = Math.min(rightOffset, getLeft() - firstLeft);
                }

                // 将所有的子视图向左右移动
                offsetChildrenLeftAndRight(rightOffset);
//                startScrollIfNeed(rightOffset, 0);

                if (mFirstPosition > 0) {
                    //尝试填充所有的空白区域
                    fillRightView(lastPosition + 1, lastChild.getRight() + mDividerHeight, 0);
                    if (firstChild.getLeft() > getLeft()) {//Need fill left views.
                        fillLeftView(mFirstPosition - 1, firstChild.getLeft() + mDividerHeight);
                    }
                    //mFirstPosition - 1, firstChild.getLeft() - mDividerHeight
                    //调整剩下的空白区域
                    adjustViewsUpOrDown();
                }

            }
        }
    }

    @Override
    protected void detachViewsFromParent(int start, int count) {
        if (DEBUG) {
            Log.i(TAG, String.format(Locale.US, "Detach views from parent => " + "Start %d <==> %d ", start, count));
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                Log.i(TAG, String.format(Locale.US, "Detach views bounds top = [%d] left = [%d] bottom = [%d] right=[%d]", child.getTop(), child.getLeft(), child.getBottom(), child.getRight()));
            }
        }
        super.detachViewsFromParent(start, count);
    }

    /**
     * 滚动
     *
     * @param xDelta
     * @param yDelta
     */
    protected boolean startScrollIfNeed(float xDelta, float yDelta) {
        if (DEBUG)
            Log.d(TAG, String.format(Locale.US, "startScrollIfNeed xDelta %.2f yDelta %.2f ", xDelta, yDelta));

        if (getChildCount() == 0) return false;

        boolean moved = false;

        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL: {
                boolean canScroll = canScrollHorizontally((int) xDelta);
                if (DEBUG) Log.i(TAG, "Can scroll => " + canScroll);
                if (canScroll) {
                    moved = horizontalScroll(horizontalAdjustWithMaxScrollRestrict((int) xDelta));
                } else {//At end
                    if (mLastMotionY != Integer.MIN_VALUE) {
                        pullHorizontalDirectionGlowEffect(xDelta);
                    }
                }
                break;
            }
            case LAYOUT_DIRECTLY_VERTICAL: {
                boolean canVerticalScroll = canScrollVertically((int) yDelta);
                if (DEBUG) Log.d(TAG, "Can vertical scroll => " + canVerticalScroll);
                if (canVerticalScroll)
                    moved = verticalScroll(verticalAdjustWithMaxScrollRestrict((int) yDelta));
                else {//At end
                    if (mLastMotionX != Integer.MIN_VALUE) {
                        pullVerticalDirectionGlowEffect(yDelta);
                    }
                }
                break;
            }
        }

        //如果有发生移动,我们通知我们的监听器
        if (moved) {
            invokeOnItemScrollListener();
            tryInvokeSelectItemChangeListener();
        }
        return moved;
    }

    /**
     * 触发垂直方向上的边缘效果
     *
     * @param yDelta 本次尝试滑动的y轴增量,我们用它作为overscroll的增量
     */
    public void pullVerticalDirectionGlowEffect(float yDelta) {
        if (DEBUG) Log.i(TAG, "Pull vertical direction glow effect " + yDelta);
        if (yDelta > 0) {//向下拉动

            //如果用户之前向上拉动,但是现在向下拉动,那么重置之前overscroll的距离

            mTouchState = OVER_SCROLL;
            mActiveEffect = mTopEdgeEffect;

            if (mYOverScrollDistance < 0) mYOverScrollDistance = -1;

            //如果这是 "开始" overscroll 那么就是将本次的滚动距离直接赋值
            if (mYOverScrollDistance == -1) mYOverScrollDistance = (int) yDelta;
            else mYOverScrollDistance += yDelta;//否则的话就是自增当前的滚动的距离

            //计算边缘效果的 "波峰点"
            float displacement = (mLastMotionX / getWidth());

            //触发边缘效果
            mTopEdgeEffect.onPull(mYOverScrollDistance * 1.0f / getHeight(), displacement);

            //如果上一次的效果还未完成
            //那么我们中断上次的效果,开始本次的效果
            if (!mTopEdgeEffect.isFinished()) {
                mTopEdgeEffect.onRelease();
            }

            //刷新顶部边缘效果
            invalidateTopGlow();

        } else if (yDelta < 0) {//向上拉动

            if (mYOverScrollDistance > 0)
                mYOverScrollDistance = -1;//如果用户之前向下拉动,但是现在向上拉动,那么重置之前overscroll的距离

            float displacement = 1.0f - (mLastMotionX / getWidth());//计算边缘效果的 "波峰点"

            //如果这是 "开始" overscroll 那么就是将本次的滚动距离直接赋值
            if (mYOverScrollDistance == -1) mYOverScrollDistance = (int) yDelta;
            else mYOverScrollDistance += yDelta;//否则的话就是自增当前的滚动的距离

            //触发边缘效果
            mBottomEdgeEffect.onPull(mYOverScrollDistance * 1.0f / getHeight(), displacement);

            //如果上一次的效果还未完成
            //那么我们中断上次的效果,开始本次的效果
            if (!mBottomEdgeEffect.isFinished()) mBottomEdgeEffect.onRelease();

            //刷新底部的边缘效果
            invalidateBottomGlow();
        } else {

        }
    }

    /**
     * 触发水平方向上的边缘效果
     *
     * @param xDelta 本次尝试滑动的x轴增量,我们用它作为overscroll的增量
     */
    public void pullHorizontalDirectionGlowEffect(float xDelta) {
        if (xDelta > 0) {//向右拉动

            //如果用户之前向上拉动,但是现在向下拉动,那么重置之前overscroll的距离
            if (mXOverScrollDistance < 0) mXOverScrollDistance = -1;

            //如果这是 "开始" overscroll 那么就是将本次的滚动距离直接赋值
            if (mXOverScrollDistance == -1) mXOverScrollDistance = (int) xDelta;
            else mXOverScrollDistance += xDelta;//否则的话就是自增当前的滚动的距离

            //计算边缘效果的 "波峰点"
            float displacement = 1.0f - (mLastMotionY / getHeight());

            //触发边缘效果
            mLeftEdgeEffect.onPull(mXOverScrollDistance * 1.0f / getWidth(), displacement);

            //如果上一次的效果还未完成
            //那么我们中断上次的效果,开始本次的效果
            if (!mLeftEdgeEffect.isFinished()) mLeftEdgeEffect.onRelease();

            //刷新左边边缘效果
            invalidateLeftGlow();
        } else if (xDelta < 0) {//向左拉动

            if (mYOverScrollDistance > 0)
                mYOverScrollDistance = -1;//如果用户之前向下拉动,但是现在向上拉动,那么重置之前overscroll的距离

            float displacement = 1.0f - (mLastMotionY / getHeight());//计算边缘效果的 "波峰点"

            //如果这是 "开始" overscroll 那么就是将本次的滚动距离直接赋值
            if (mXOverScrollDistance == -1) mXOverScrollDistance = (int) xDelta;
            else mXOverScrollDistance += xDelta;//否则的话就是自增当前的滚动的距离

            //触发边缘效果
            mRightEdgeEffect.onPull(mXOverScrollDistance * 1.0f / getWidth(), displacement);

            //如果上一次的效果还未完成
            //那么我们中断上次的效果,开始本次的效果
            if (!mRightEdgeEffect.isFinished()) mRightEdgeEffect.onRelease();

            //刷新右边的边缘效果
            invalidateRightGlow();
        }
    }

    protected void tryInvokeSelectItemChangeListener() {
        if (!mIsMutable) {
            int vp = getSelectedViewPosition();
            if (vp != INVALID_POSITION) {
                final View selectedView = getChildAt(vp);
                int sel = vp + mFirstPosition;
                if (selectedView != null) {
                    //为啥要加一个 selectedView != mSelectedView 判断?
                    //因为 在开始调整时 因为滚轮模式存在调整的,所以会导致相同的位置出现视图的位置不相同
                    if (selectedView != mSelectedView || mSelectedPosition != sel) {
                        final int oldSel = mSelectedPosition;
                        final View oldSelView = mSelectedView;

                        mSelectedPosition = sel;
                        mSelectedView = selectedView;

                        invokeOnSelectedItemListener(oldSel % mItemCount, oldSelView, mSelectedPosition % mItemCount, selectedView);
                    }

                }
            }
        }
    }

    /**
     * 填充视图
     */
    protected void fillView() {
        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL:
                fillRightView(mFirstPosition + getChildCount(), 0, 0);
                break;
            case LAYOUT_DIRECTLY_VERTICAL:
                fillDownView(mFirstPosition + getChildCount(), 0, 0);
                break;
        }
    }

    /***
     * 创建并添加视图
     *
     * @param position  需要创建的视图的所在position
     * @param y         视图摆放的y轴偏移
     * @param x         视图板房的x轴偏移
     * @param stuffMode 填充模式
     * @return
     */
    protected View makeAndAddView(int position, int y, int x, StuffMode stuffMode) {

        if (DEBUG)
            Log.i(TAG, String.format("Make and add view position == %d x == %d y == %d ", position, x, y));

        View view = obtainView(position);

        LayoutParams lp = setupChildView(view);

        int childWidth = view.getMeasuredWidth();
        int childHeight = view.getMeasuredHeight();

        switch (stuffMode) {
            case flowDown: {//向下填充
                addViewInLayout(view, -1, lp, true);
                view.layout(0, y, childWidth, y + childHeight);
            }
            break;
            case flowUp: {//向上填充
                addViewInLayout(view, 0, lp, true);
                view.layout(0, y - childHeight, childWidth, y);
            }
            break;
            case flowRight: {//向右填充
                addViewInLayout(view, -1, lp, true);
                view.layout(x, y, x + childWidth, y + childHeight);
            }
            break;
            case flowLeft: {//向左填充
                addViewInLayout(view, 0, lp, true);
                view.layout(x - childWidth, y, x, y + childHeight);
            }
            break;

        }
        return view;
    }

    /**
     * 测量初始化子视图
     * 子类完全可以重写来达到 不同的测量效果
     *
     * @param view
     * @return
     */
    protected LayoutParams setupChildView(View view) {
        LayoutParams lp = view.getLayoutParams();
        if (lp == null) lp = generateDefaultLayoutParams();

        int widthMeasureSpec = 0;
        int heightMeasureSpec = 0;

        final int width = lp.width < 0 ? getWidth() : lp.width;
        final int height = lp.height < 0 ? getHeight() : lp.height;

        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL: {
                //未指定模式适用于紧致缩放,子视图都将会为自已做最小化的定位操作
                //我们尽量的使用精确指定模式
                //因为这样就可以使用 Match_parent属性啦
                //哈哈哈哈哈........
                //widthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.UNSPECIFIED);
//                heightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.UNSPECIFIED);
                int widthMode = (lp.width == LayoutParams.MATCH_PARENT && width > 0 || lp.width > 0) ? MeasureSpec.EXACTLY : MeasureSpec.UNSPECIFIED;
                int heightMode = (lp.height == LayoutParams.MATCH_PARENT && height > 0 || lp.width > 0) ? MeasureSpec.EXACTLY : MeasureSpec.UNSPECIFIED;

                widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(width, Integer.MAX_VALUE / 2), widthMode);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(height, Integer.MAX_VALUE / 2), heightMode);
            }
            break;
            case LAYOUT_DIRECTLY_VERTICAL: {
                //widthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.UNSPECIFIED);
//                heightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, lp.height > 0 ? MeasureSpec.EXACTLY : MeasureSpec.UNSPECIFIED);

                int widthMode = (lp.width == LayoutParams.MATCH_PARENT && width > 0 || lp.width > 0) ? MeasureSpec.EXACTLY : MeasureSpec.UNSPECIFIED;
                int heightMode = (lp.height == LayoutParams.MATCH_PARENT && height > 0 || lp.width > 0) ? MeasureSpec.EXACTLY : MeasureSpec.UNSPECIFIED;

                widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(width, Integer.MAX_VALUE / 2), widthMode);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(height, Integer.MAX_VALUE / 2), heightMode);
            }
            break;
        }

        view.measure(widthMeasureSpec, heightMeasureSpec);
        return lp;
    }

    /**
     * 从适配器中获取一个视图
     *
     * @param position 需要获取视图的位置
     * @return
     */
    protected View obtainView(int position) {
        return mAdapter.getView(position, mRecyclerBin.findRecyclerBin(position), this);
    }

    /**
     * 从指定的位置指定的偏移量开始填充视图
     *
     * @param pos         需要填充的开始位置
     * @param startOffset 填充开始位置的偏移量
     */
    protected void fillSpecView(int pos, int startOffset) {
        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL:
                fillRightSpecView(pos, startOffset);
                break;
            case LAYOUT_DIRECTLY_VERTICAL:
                fillDownSpecView(pos, startOffset);
                break;
        }
    }

    /**
     * 从指定的位置指定的偏移量开始向右填充视图
     *
     * @param pos
     * @param xOffset
     */
    protected void fillRightSpecView(int pos, int xOffset) {
        View temp = makeAndAddView(pos, 0, xOffset, StuffMode.flowRight);
        mFirstPosition = pos;
        fillRightView(pos + 1, temp.getRight() + mDividerHeight, 0);
    }

    /**
     * 从指定的位置指定的偏移量开始向下填充视图
     *
     * @param pos
     * @param yOffset
     */
    protected void fillDownSpecView(int pos, int yOffset) {
        View temp = makeAndAddView(pos, yOffset, 0, StuffMode.flowDown);
        mFirstPosition = pos;
        fillDownView(pos + 1, temp.getBottom() + mDividerHeight, 0);
    }

    /**
     * 向下填充视图
     *
     * @param pos     开始填充的位置
     * @param nextTop 填充的视图的y轴
     */
    protected void fillDownView(int pos, int nextTop, int offScreenChildCount) {
        switch (mViewMode) {
            case LIST_VIEW:
            case WHEEL_VIEW: {
                while (nextTop < (getBottom() - getTop() + mDividerHeight/**Fix like list view divider bugs.*/) && pos < mAdapter.getCount()) {
                    View topView = makeAndAddView(pos, nextTop, 0, StuffMode.flowDown);
                    nextTop = topView.getBottom() + mDividerHeight;
                    pos++;
                }
                mFirstPosition += offScreenChildCount;
            }

            break;
            case END_LESS_LIST_VIEW:
            case ENDLESS_WHEEL_VIEW: {
                //先校验下数据,不然在数据首尾数据交界处会有问题
                if (pos >= mItemCount) {
                    pos = pos % mItemCount;
                }

                while (nextTop < (getBottom() - getTop() + mDividerHeight /**Fix like list view divider bugs.*/) && mItemCount > 0 /*&& pos < mItemCount*/) {
                    if (DEBUG) Log.d(TAG, "Fill down => " + pos);
                    View topView = makeAndAddView(pos, nextTop, 0, StuffMode.flowDown);
                    nextTop = topView.getBottom() + mDividerHeight;
                    pos++;

                    if (pos >= mItemCount) {
                        if (DEBUG)
                            Log.d(TAG, String.format(Locale.US, "Fill down pos[%d] >= ItemCount[%d] re-pos = [%d]", pos, mItemCount, pos - mItemCount));
                        pos = pos - mItemCount;
                    }
                }
                mFirstPosition += offScreenChildCount;
                mFirstPosition %= mItemCount;
            }
            break;

        }
    }

    /**
     * 向下填充视图
     *
     * @param pos            开始填充的位置
     * @param previousBottom 填充的视图的y轴
     */
    protected void fillUpView(int pos, int previousBottom) {
        if (DEBUG)
            Log.d(TAG, String.format("Fill up view position == %d Previous bottom ==  %d", pos, previousBottom));

        switch (mViewMode) {
            case LIST_VIEW:
            case WHEEL_VIEW: {//普通视图模式
                while (previousBottom + mDividerHeight/**Fix like list view divider bugs.*/ > 0 && pos >= 0 && pos < mItemCount) {
                    View leftView = makeAndAddView(pos, previousBottom, 0, StuffMode.flowUp);
                    previousBottom = leftView.getTop() - mDividerHeight;
                    pos--;
                }
                mFirstPosition = pos + 1;
            }
            break;
            case END_LESS_LIST_VIEW:
            case ENDLESS_WHEEL_VIEW: {//无尽模式
                if (pos < 0) pos = mItemCount - 1;
                while (previousBottom + mDividerHeight/**Fix like list view divider bugs.*/ > 0 && pos < mItemCount && mItemCount > 0) {
                    View leftView = makeAndAddView(pos, previousBottom, 0, StuffMode.flowUp);
                    previousBottom = leftView.getTop() - mDividerHeight;
                    pos--;
                    if (pos < 0) pos = mItemCount - 1;
                }
                mFirstPosition = pos + 1;
                mFirstPosition %= mItemCount;
            }

        }

    }

    /**
     * 向右填充视图
     *
     * @param offsetScreenNum
     * @param pos             填充视图的起点
     * @param nextLeft        视图开始的位置
     */
    protected void fillRightView(int pos, int nextLeft, int offsetScreenNum) {
        switch (mViewMode) {
            case LIST_VIEW:
            case WHEEL_VIEW: {//普通模式
                while (nextLeft < (getRight() - getLeft() + mDividerHeight) && pos < mItemCount) {
                    View topView = makeAndAddView(pos, 0, nextLeft, StuffMode.flowRight);
                    nextLeft = topView.getRight() + mDividerHeight;
                    pos++;
                }
                mFirstPosition += offsetScreenNum;
            }
            break;
            case END_LESS_LIST_VIEW:
            case ENDLESS_WHEEL_VIEW: {//无穷模式

                //先校验下数据,不然在数据首尾数据交界处会有问题
                if (pos >= mItemCount) {
                    pos = pos % mItemCount;
                }

                while (nextLeft < (getRight() - getLeft() + mDividerHeight) && mItemCount > 0 /*&& pos < mItemCount*/) {
                    if (DEBUG) Log.d(TAG, "Fill right => " + pos);
                    View topView = makeAndAddView(pos, 0, nextLeft, StuffMode.flowRight);
                    nextLeft = topView.getRight() + mDividerHeight;
                    pos++;

                    if (pos >= mItemCount) {
                        if (DEBUG)
                            Log.d(TAG, String.format(Locale.US, "Fill right pos[%d] >= ItemCount[%d] re-pos = [%d]", pos, mItemCount, pos - mItemCount));
                        pos = pos - mItemCount;
                    }
                }
                mFirstPosition += offsetScreenNum;
                mFirstPosition %= mItemCount;
            }
            break;
        }
    }

    /**
     * 向左填充视图
     *
     * @param pos           填充视图的起点
     * @param previousRight 视图开始填充的位置
     */
    protected void fillLeftView(int pos, int previousRight) {
        if (DEBUG)
            Log.d(TAG, String.format("Fill left view position == %d Previous right ==  %d", pos, previousRight));

        switch (mViewMode) {
            case LIST_VIEW://普通模式
            case WHEEL_VIEW: {

                while (previousRight + mDividerHeight/**Fix like list view divider bugs.*/ > 0 && pos >= 0 && pos < mItemCount) {
                    View leftView = makeAndAddView(pos, 0, previousRight, StuffMode.flowLeft);
                    previousRight = leftView.getLeft() - mDividerHeight;
                    pos--;
                }
                mFirstPosition = pos + 1;
            }
            break;
            case END_LESS_LIST_VIEW:
            case ENDLESS_WHEEL_VIEW: {//无尽模式
                if (DEBUG)
                    Log.d(TAG, "Previous right => " + previousRight + " , Pos => " + pos + " mFirstPosition => " + mFirstPosition);
                if (pos < 0) pos = mItemCount - 1;
                while (previousRight + mDividerHeight/**Fix like list view divider bugs.*/ > 0 && pos < mItemCount && mItemCount > 0) {
                    View leftView = makeAndAddView(pos, 0, previousRight, StuffMode.flowLeft);
                    previousRight = leftView.getLeft() - mDividerHeight;
                    pos--;
                    if (pos < 0) pos = mItemCount - 1;
                }
                mFirstPosition = pos + 1;
                mFirstPosition %= mItemCount;
            }
            break;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL:
                return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            case LAYOUT_DIRECTLY_VERTICAL:
                return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) mAdapter.unregisterDataSetObserver(mDataSetObserver);
        this.mAdapter = adapter;
        if (mAdapter != null) adapter.registerDataSetObserver(mDataSetObserver);
        requestLayout();
    }

    /**
     * 刷新布局
     */
    protected void invalidLayout() {
        if (getHeight() == 0 && getWidth() == 0) return;
        mIsMutable = true;

        try {
            if (mAdapter == null) {
                reset();
                invokeOnItemScrollListener();
            }

            mItemCount = mAdapter == null ? 0 : mAdapter.getCount();

            if (mDataSetInvalidated || mDataChanged) {
                removeAllViewsInLayout();
            }

            if (mDataSetInvalidated) {
                mRecyclerBin.clearRecyclerBins();
            }

            final int N = getChildCount();

            if (N > 0) {
                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL: {//水平模式
                        mStartOffset = getChildAt(0).getLeft();
                    }
                    break;
                    case LAYOUT_DIRECTLY_VERTICAL: {//垂直模式
                        mStartOffset = getChildAt(0).getTop();
                    }
                    break;
                }
            }

            for (int i = 0; i < N; i++)
                mRecyclerBin.addRecyclerBin(mFirstPosition + i, getChildAt(i));

            detachAllViewsFromParent();
            if (mItemCount > 0) {
                mStartOffset += mForceOffsetFromStart;

                if (mStartOffset == 0) {//No offset
                    fillView();
                } else {
                    fillSpecView(mFirstPosition, mStartOffset);
                }

                if (mTouchState == REST) adjustSelView(0);
            }
        } finally {
            mIsMutable = false;
            invokeOnItemScrollListener();
            tryInvokeSelectItemChangeListener();
        }
    }

    /**
     * 重置
     */
    protected void reset() {
        mItemCount = 0;
        mFirstPosition = 0;
        mScrollTrigger = SCROLL_TRIGGER_SYSTEM;
        mSelectedPosition = INVALID_POSITION;
        if (mFlingRunnable != null) mFlingRunnable.endScrollerAnimation(true);
    }

    public int getSelectedViewPosition() {
        switch (mViewMode) {
            case END_LESS_LIST_VIEW:
            case LIST_VIEW: {//ListView 模式
                return 0;
            }
            case ENDLESS_WHEEL_VIEW:
            case WHEEL_VIEW: {//滚轮模式
                switch (mLayoutDirectly) {
                    case LAYOUT_DIRECTLY_HORIZONTAL: {//水平模式
                        final int N = getChildCount();
                        final int xSel = getXSel();
                        for (int i = 0; i < N; i++) {
                            View child = getChildAt(i);
                            //// TODO: 16/8/6 May have bugs.
                            if (child.getLeft() <= xSel && child.getRight() >= xSel) {
                                if (DEBUG)
                                    Log.i(TAG, "Current Selected position, on divider => " + i);
                                return i;
                            } else if (child.getTop() <= xSel && child.getRight() + mDividerHeight >= xSel) {
                                if (DEBUG)
                                    Log.i(TAG, "Current Selected position, on divider => " + i);
                                return i;
                            }
                            // TODO: 16/8/18
                        }
                    }
                    break;
                    case LAYOUT_DIRECTLY_VERTICAL: {//垂直模式
                        final int N = getChildCount();
                        final int ySel = getYSel();
                        for (int i = 0; i < N; i++) {
                            View child = getChildAt(i);
                            //// TODO: 16/8/6 May have bugs.
                            if (child.getTop() <= ySel && child.getBottom() >= ySel) {
                                if (DEBUG) Log.i(TAG, "Current Selected position => " + i);
                                return i;
                            } else if (child.getTop() <= ySel && child.getBottom() + mDividerHeight >= ySel) {
                                if (DEBUG)
                                    Log.i(TAG, "Current Selected position, on divider => " + i);
                                return i;
                            }
                        }
                    }
                    break;
                }
            }
            break;
        }
        return INVALID_POSITION;
    }

    @Override
    public int getSelectedItemPosition() {
        int viewPosition = getSelectedViewPosition();
        if (viewPosition != INVALID_POSITION) {
            return (viewPosition + mFirstPosition) % mItemCount;
        }
        return INVALID_POSITION;
    }

    @Override
    public View getSelectedView() {
        int viewPosition = getSelectedViewPosition();
        if (viewPosition != INVALID_POSITION) {
            return getChildAt(viewPosition);
        }
        return null;
    }

    @Override
    public void setSelection(int position) {
        setSelectionInner(position, 0);
    }

    private void setSelectionInner(int position, int startOffset) {

        if (mAdapter == null || mItemCount == 0) return;

        position = Math.min(Math.max(0, position), mItemCount - 1);
        if (DEBUG) Log.i(TAG, "SetSelection " + position + " Start Offset " + startOffset);

        if (getSelectedItemPosition() == position) return;

        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL: {
                switch (mViewMode) {
                    case LIST_VIEW:
                    case END_LESS_LIST_VIEW: {
                        reset();
                        mFirstPosition = position;

                        mForceOffsetFromStart = 0;
                        //矫正偏移
                        if (getChildCount() > 0) {
                            View firstChild = getChildAt(0);
                            mForceOffsetFromStart = -firstChild.getLeft();
                        }

                        mForceOffsetFromStart -= startOffset;//添加指定的偏移

                        invalidLayout();

                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        correctTooRight(getChildCount());
                        correctTooLeft(getChildCount());


                    }
                    break;
                    case WHEEL_VIEW: {
                        reset();
                        mFirstPosition = position;
                        mForceOffsetFromStart = getXSel();

                        if (getChildCount() > 0) mForceOffsetFromStart -= getChildAt(0).getLeft();

                        invalidLayout();
                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        if (getChildCount() > 0) {
                            fillLeftView(mFirstPosition - 1, getChildAt(0).getLeft() - mDividerHeight);
                            correctTooLeft(getChildCount());
                            correctTooRight(getChildCount());

                        }
                        adjustSelView(0);
                    }
                    break;
                    case ENDLESS_WHEEL_VIEW: {
                        reset();
                        mFirstPosition = position;
                        mForceOffsetFromStart = getXSel();
                        invalidLayout();
                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        if (getChildCount() > 0) {
                            fillLeftView(mFirstPosition - 1, getChildAt(0).getLeft() - mDividerHeight);
                            correctTooLeft(getChildCount());
                            correctTooRight(getChildCount());
                        }
                        adjustSelView(0);
                    }
                    break;
                }
            }
            break;
            case LAYOUT_DIRECTLY_VERTICAL: {
                switch (mViewMode) {
                    case LIST_VIEW:
                    case END_LESS_LIST_VIEW: {
                        reset();
                        mFirstPosition = position;
                        mForceOffsetFromStart = 0;

                        //矫正偏移
                        if (getChildCount() > 0) {
                            View firstChild = getChildAt(0);
                            mForceOffsetFromStart = -firstChild.getTop();
                        }

                        mForceOffsetFromStart -= startOffset;//添加指定的偏移

                        invalidLayout();

                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        correctTooHigh(getChildCount());
                        correctTooLow(getChildCount());

                    }
                    break;
                    case WHEEL_VIEW: {
                        reset();
                        mFirstPosition = position;

                        mForceOffsetFromStart = getYSel();

                        View view = mAdapter.getView(position, mRecyclerBin.findRecyclerBin(position), this);
                        setupChildView(view);
                        int itemHeight = view.getMeasuredHeight();
                        mForceOffsetFromStart -= (itemHeight / 2.0f);

                        invalidLayout();

                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        if (getChildCount() > 0) {
                            int selViewIndex = getSelectedViewPosition();
                            if (selViewIndex != -1) {
                                View selView = getChildAt(selViewIndex);
                                int selPos = (selViewIndex + mFirstPosition) % mItemCount;
                                fillUpView(selPos - 1, selView.getTop() - mDividerHeight);
                            } else {
                                fillUpView(mFirstPosition - 1, getChildAt(0).getTop() - mDividerHeight);
                            }
                            correctTooLow(getChildCount());
                            correctTooHigh(getChildCount());
                        }

                        adjustSelView(0);
                    }
                    break;
                    case ENDLESS_WHEEL_VIEW: {
                        reset();
                        mFirstPosition = position;
                        mForceOffsetFromStart = getYSel();

                        View view = mAdapter.getView(position, mRecyclerBin.findRecyclerBin(position), this);
                        setupChildView(view);
                        int itemHeight = view.getMeasuredHeight();
                        mForceOffsetFromStart -= (itemHeight / 2.0f);
                        invalidLayout();
                        mForceOffsetFromStart = 0;
                        mStartOffset = 0;
                        if (getChildCount() > 0) {
                            fillUpView(mFirstPosition - 1, getChildAt(0).getTop() - mDividerHeight);
                            correctTooLow(getChildCount());
                            correctTooHigh(getChildCount());
                        }
                        adjustSelView(0);
                    }
                    break;
                }
            }
            break;
        }
        invalidate();
    }

    /**
     * 选中条目并滚动到指定的偏移
     * 注意:这个只支持{@link ViewMode#LIST_VIEW 或 ViewMode#END_LESS_LIST_VIEW}模式
     *
     * @param position    位置
     * @param startOffset 开始位置的偏移
     */
    public void setSelectionFromStart(int position, int startOffset) {
        switch (mViewMode) {
            case LIST_VIEW:
            case END_LESS_LIST_VIEW: {
                //todo May have bugs.
                setSelectionInner(position, startOffset);
            }
            break;
            case WHEEL_VIEW:
            case ENDLESS_WHEEL_VIEW: {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * 获取给定的位置上的视图
     * 注意:这个视图必须是可见的
     *
     * @param position 给定的位置
     * @return null 或获取到的视图
     * @throws IndexOutOfBoundsException 如果给定的下标不对
     */
    public View getVisibleViewAtPosition(int position) {

        if (position < 0 || position >= mItemCount)
            throw new IndexOutOfBoundsException(String.format(Locale.US, "0 >= [%s] < %s ", position, mItemCount));

        final int N = getChildCount();

        final int minPosition = mFirstPosition;
        final int maxPosition = mFirstPosition + N - 1;

        if (position < minPosition || position > maxPosition)
            throw new IndexOutOfBoundsException(String.format(Locale.US, "%s >= [%s] < %s ", minPosition, position, maxPosition));

        return getChildAt(position - minPosition);
    }

    /**
     * 使用指定的时长滚动到指定的位置
     *
     * @param position 目标位置
     * @param duration 滚动时长
     */
    public void smoothToPosition(int position, int duration) {
        if (mItemCount == 0) return;
        //防止边界超出
        position = Math.max(0, Math.min(position, mItemCount - 1));
        mSmoothScrollRunnable.scrollToPosition(position, 0, duration);
    }

    /**
     * 平滑滚动到指定的位置,并且在头部偏移指定的偏移量
     *
     * @param position 目标位置
     * @param offset   顶部的偏移
     * @param duration 时长
     */
    public void smoothToPositionFromStart(int position, int offset, int duration) {
        if (mItemCount == 0) return;
        switch (mViewMode) {
            case LIST_VIEW:
            case END_LESS_LIST_VIEW: {
                mSmoothScrollRunnable.scrollToPosition(position, offset, duration);
            }
            break;
            case WHEEL_VIEW:
            case ENDLESS_WHEEL_VIEW: {
                throw new UnsupportedOperationException("#smoothToPositionFromStart can use on wheel mode.");
            }
        }
    }


    /**
     * 平滑滚动到指定的位置,并且在头部偏移指定的偏移量
     *
     * @param position 目标位置
     * @param offset   顶部的偏移
     */
    public void smoothToPositionFromStart(int position, int offset) {
        smoothToPositionFromStart(position, offset, DEFAULT_SMOOTH_DURATION);
    }


    /**
     * 使用默认的时长{@link #DEFAULT_SMOOTH_DURATION},平滑滚动到指定位置
     *
     * @param position 需要滚动的位置
     */
    public void smoothToPosition(int position) {
        smoothToPosition(position, DEFAULT_SMOOTH_DURATION);
    }

    /**
     * 平滑滚动
     *
     * @param distance 滚动的距离
     * @param duration 滚动的时长
     * @param linear   是否线性滚动
     */
    public void smoothScroll(int distance, int duration, boolean linear) {
        mFlingRunnable.startScroll(distance, duration, linear);
    }

    /**
     * 获取水平模式下的选中线
     *
     * @return
     */
    protected int getXSel() {
        return getMeasuredWidth() / 2;
    }

    /**
     * 获取垂直模式下的选中线
     *
     * @return
     */
    protected int getYSel() {
        return getMeasuredHeight() / 2;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawDivider(canvas);
        if (DEBUG) {
            switch (mLayoutDirectly) {
                case LAYOUT_DIRECTLY_HORIZONTAL:
                    canvas.drawLine(getXSel(), 0, getXSel(), getHeight(), mDebugPaint);
                    break;
                case LAYOUT_DIRECTLY_VERTICAL:
                    canvas.drawLine(0, getYSel(), getWidth(), getYSel(), mDebugPaint);
                    break;
            }
            canvas.drawText(String.valueOf(mFirstPosition), 100, 100, mDebugPaint);
        }

    }

    protected void drawDivider(Canvas canvas) {
        if (mDivider == null || mDividerHeight == 0) return;

        final int N = getChildCount();
        final Rect bounds = mTempRect;

        switch (mLayoutDirectly) {
            case LAYOUT_DIRECTLY_HORIZONTAL: {
                if (N > 1) {
                    bounds.top = 0;
                    bounds.bottom = getBottom() - getTop();

                    for (int i = 0; i < N; i++) {
                        final View child = getChildAt(i);
                        int right = child.getRight();
                        final boolean isLastItem = (i == (N - 1));
                        final int listRight = getRight();

                        if ((right < listRight) && !isLastItem) {
                            bounds.left = right;
                            bounds.right = right + mDividerHeight;
                            drawDivider(canvas, bounds);
                        }
                    }
                }
            }
            break;
            case LAYOUT_DIRECTLY_VERTICAL: {
                if (N > 1) {
                    bounds.left = 0;
                    bounds.right = getRight() - getLeft();

                    for (int i = 0; i < N; i++) {
                        final View child = getChildAt(i);
                        int bottom = child.getBottom();
                        final boolean isLastItem = (i == (N - 1));
                        final int listBottom = getBottom();

                        if ((bottom < listBottom) && !isLastItem) {
                            bounds.top = bottom;
                            bounds.bottom = bottom + mDividerHeight;
                            drawDivider(canvas, bounds);
                        }
                    }
                }
            }
            break;
        }
    }

    void drawDivider(Canvas canvas, Rect bounds) {
        // This widget draws the same divider for all children
        final Drawable divider = mDivider;
        divider.setBounds(bounds);
        divider.draw(canvas);
    }

    /**
     * 数据变化观察者
     */
    protected DataSetObserver mDataSetObserver = new AdapterDataSetObserver();

    /**
     * 回收器
     */
    protected RecyclerBin mRecyclerBin = new RecyclerBin();

    /**
     * 快速滚动
     */
    protected FlingRunnable mFlingRunnable = new FlingRunnable();

    /**
     * 平滑滚动Runnable
     */
    protected SmoothScrollRunnable mSmoothScrollRunnable = new SmoothScrollRunnable();

    /**
     * 切换视图模式
     *
     * @param mode 目标模式
     */
    public void setMode(ViewMode mode) {
        if (mViewMode != mode) {
            this.mViewMode = mode;
            mDataSetInvalidated = true;
            requestLayout();
        }
    }

    public Drawable getDivider() {
        return mDivider;
    }

    public void setDivider(Drawable mDivider) {
        this.mDivider = mDivider;
        invalidLayout();
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
    }

    /**
     * Recycler
     */
    protected class RecyclerBin {

        private final SparseArray<List<View>> mCachedViews = new SparseArray<List<View>>();

        public void clearRecyclerBins() {
            mCachedViews.clear();
        }

        /**
         * 根据指定的Position对象查询可以使用的回收的视图
         *
         * @param position
         * @return
         */
        public View findRecyclerBin(int position) {
            int type = mAdapter.getItemViewType(position);
            List<View> cacheViews = mCachedViews.get(type);
            View cacheView = null;
            if (cacheViews != null && !cacheViews.isEmpty()) cacheView = cacheViews.remove(0);
            if (DEBUG)
                Log.i(TAG, String.format(Locale.US, "Pull recycler bin %s of type [%d] from pool ", cacheView, type));
            return cacheView;
        }

        /**
         * 添加一个视图到缓存区
         *
         * @param position
         * @param scrapView
         */
        public void addRecyclerBin(int position, View scrapView) {
            int type = mAdapter.getItemViewType(position);
            List<View> cacheViews = mCachedViews.get(type);
            if (cacheViews == null) mCachedViews.put(type, cacheViews = new ArrayList<>());
            if (DEBUG)
                Log.i(TAG, String.format(Locale.US, "Push recycler bin %s of type [%d] to pool ", scrapView, type));
            cacheViews.add(scrapView);
        }
    }

    /**
     * 数据变化观察者
     */
    protected class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            mDataChanged = true;
            invalidLayout();
        }

        @Override
        public void onInvalidated() {
            mDataSetInvalidated = true;
            reset();
            invalidLayout();
        }
    }

    /**
     * 反馈当前的状态到监听器中
     *
     * @param newState 需要反馈的状态
     */
    protected void reportScrollStateChanged(int newState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(this, newState);
        }
    }

    /**
     * 计算点击的位置
     *
     * @param x x
     * @param y y
     * @return 点击的视图的位置, 注意不是position
     */
    protected int computeClickViewPosition(int x, int y) {
        int position = INVALID_POSITION;
        final int N = getChildCount();
        if (N > 0) {
            for (int i = 0; i < N; i++) {
                View child = getChildAt(i);
                child.getHitRect(mItemClickRect);
                if (mItemClickRect.contains(x, y)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 执行点击事件回调
     *
     * @param view     点击的视图
     * @param position 点击的位置
     */
    protected void invokeOnItemClickListener(View view, int position) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(this, view, position, getItemIdAtPosition(position));
        }
    }

    /**
     * 执行滚动回调
     * 调用这个方法会回调当前的状态到监听器
     */
    protected void invokeOnItemScrollListener() {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, mFirstPosition, getChildCount(), mItemCount);
        }
    }

    /**
     * 执行选中的条目变化回调
     *
     * @param oldSel
     * @param oldSelView
     * @param newSel
     * @param selectedView
     */
    protected void invokeOnSelectedItemListener(int oldSel, View oldSelView, int newSel, View selectedView) {
        if (mOnSelectedItemChangedListener != null) {
            mOnSelectedItemChangedListener.onSelectedItemChanged(this, oldSel, oldSelView, newSel, selectedView);
        }
    }

    /**
     * 设置滚动监听器
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    /**
     * 设置选中条目变化监听器
     *
     * @param onSelectedItemChangedListener
     */
    public void setOnSelectedItemChangedListener(OnSelectedItemChangedListener onSelectedItemChangedListener) {
        this.mOnSelectedItemChangedListener = onSelectedItemChangedListener;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            if (mSmoothScrollbarEnabled) {
                int extent = count * 100;

                View view = getChildAt(0);
                final int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    extent += (top * 100) / height;
                }

                view = getChildAt(count - 1);
                final int bottom = view.getBottom();
                height = view.getHeight();
                if (height > 0) {
                    extent -= ((bottom - getHeight()) * 100) / height;
                }
                return extent;
            } else {
                return 1;
            }
        }
        return 0;
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            if (mSmoothScrollbarEnabled) {
                int extent = count * 100;

                View view = getChildAt(0);
                final int left = view.getLeft();
                int width = view.getWidth();
                if (width > 0) {
                    extent += (left * 100) / width;
                }
                view = getChildAt(count - 1);
                final int right = view.getRight();
                width = view.getWidth();
                if (width > 0) {
                    extent -= ((right - getWidth()) * 100) / width;
                }
                return extent;
            } else {
                return 1;
            }
        }
        return 0;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        final int firstPosition = mFirstPosition;
        final int childCount = getChildCount();

        if (firstPosition >= 0 && childCount > 0) {
            if (mSmoothScrollbarEnabled) {
                final View view = getChildAt(0);
                final int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    return Math.max(firstPosition * 100 - (top * 100) / height +
                            (int) ((float) getScrollY() / getHeight() * mItemCount * 100), 0);
                }
            } else {
                int index;
                final int count = mItemCount;
                if (firstPosition == 0) {
                    index = 0;
                } else if (firstPosition + childCount == count) {
                    index = count;
                } else {
                    index = firstPosition + childCount / 2;
                }
                return (int) (firstPosition + childCount * (index / (float) count));
            }
        }

        return 0;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        final int firstPosition = mFirstPosition;
        final int childCount = getChildCount();

        if (firstPosition >= 0 && childCount > 0) {
            if (mSmoothScrollbarEnabled) {
                final View view = getChildAt(0);
                final int left = view.getLeft();
                int width = view.getWidth();
                if (width > 0) {
                    return Math.max(firstPosition * 100 - (left * 100) / width +
                            (int) ((float) getScrollX() / getWidth() * mItemCount * 100), 0);
                }
            } else {
                int index;
                final int count = mItemCount;
                if (firstPosition == 0) {
                    index = 0;
                } else if (firstPosition + childCount == count) {
                    index = count;
                } else {
                    index = firstPosition + childCount / 2;
                }
                return (int) (firstPosition + childCount * (index / (float) count));
            }
        }

        return 0;
    }

    @Override
    protected int computeVerticalScrollRange() {
        int result;
        if (mSmoothScrollbarEnabled) {
            result = Math.max(mItemCount * 100, 0);
            if (getScrollY() != 0) {
                result += Math.abs((int) ((float) getScrollY() / getHeight() * mItemCount * 100));
            }
        } else {
            result = mItemCount;
        }
        return result;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        int result;
        if (mSmoothScrollbarEnabled) {
            result = Math.max(mItemCount * 100, 0);
            if (getScrollX() != 0) {
                result += Math.abs((int) ((float) getScrollX() / getWidth() * mItemCount * 100));
            }
        } else {
            result = mItemCount;
        }
        return result;
    }

    /**
     * 设置滚动的最大页码限制
     *
     * @param maxAmountScrollPage 需要设置的最大页码限制数量
     */
    public void setScrollPageLimit(int maxAmountScrollPage) {
        if (maxAmountScrollPage != -1 && maxAmountScrollPage < 0)
            throw new IllegalArgumentException("Number of pages should be a positive number or -1.");
        this.mMaxAmountScrollPage = maxAmountScrollPage;
    }

    /**
     * 获取一次滚动的最大页码限制
     *
     * @return -1 或设置的页码限制
     */
    public int getScrollPageLimit() {
        return mMaxAmountScrollPage;
    }

    public ViewMode getViewMode() {
        return mViewMode;
    }


    @Override
    public int getCount() {
        return mItemCount;
    }

    /**
     * 设置布局方向
     *
     * @param layoutDirectly
     */
    public void setLayoutDirectly(int layoutDirectly) {
        this.mLayoutDirectly = layoutDirectly;
        reset();
        invalidLayout();
    }

    public int getLayoutDirectly() {
        return mLayoutDirectly;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superInstanceState = super.onSaveInstanceState();
        return new InstanceStateHolder(this, superInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof InstanceStateHolder) {
            InstanceStateHolder instanceStateHolder = ((InstanceStateHolder) state);
            super.onRestoreInstanceState(((InstanceStateHolder) state).superParcelable);
            mFirstPosition = instanceStateHolder.firstPos;
            mStartOffset = instanceStateHolder.startOffset;
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * 快速滚动 Runnable
     * 参考{@link android.widget.AbsListView#FlingRunnable}
     */
    protected class FlingRunnable implements Runnable {

        private final OverScroller mSpringBackScroller;

        private int mLastFlingY;
        private int mLastFlingX;

        private int mLastSpringBackX;
        private int mLastSpringBackY;

        private OverScroller mScroller;
        private Interpolator sLinearInterpolator = new LinearInterpolator();

        private int mSpringDuration;

        private boolean mWaitingToScrollerFinish;

        public FlingRunnable() {
            mScroller = new OverScroller(getContext(), null);
            mSpringBackScroller = new OverScroller(getContext(), new LinearInterpolator());
        }

        public void startFling(int xVelocity, int yVelocity) {
            endScrollerAnimation(false);//开始快速滚动之前,先停止之前的快速滚动

            switch (mLayoutDirectly) {
                case LAYOUT_DIRECTLY_HORIZONTAL: {
                    int initialX = xVelocity < 0 ? Integer.MAX_VALUE : 0;
                    mLastFlingX = initialX;
                    mScroller.setInterpolator(null);
                    mScroller.fling(initialX, 0, xVelocity, 0, 0, Integer.MAX_VALUE, 0, 0);
                    mTouchState = FLING;
                }
                break;
                case LAYOUT_DIRECTLY_VERTICAL: {
                    int initialY = yVelocity < 0 ? Integer.MAX_VALUE : 0;
                    mLastFlingY = initialY;
                    mScroller.setInterpolator(null);
                    mScroller.fling(0, initialY, 0, yVelocity, 0, 0, 0, Integer.MAX_VALUE);
                    mTouchState = FLING;
                }
                break;
            }

            if (mTouchState == FLING) reportScrollStateChanged(OnScrollListener.SCROLL_STATE_FLING);

            postOnAnimation(this);
        }

        public void startScroll(int distance, int duration, boolean linear) {

            endScrollerAnimation(false);

            switch (mLayoutDirectly) {
                case LAYOUT_DIRECTLY_VERTICAL: {
                    int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
                    mLastFlingY = initialY;
                    mScroller.setInterpolator(linear ? sLinearInterpolator : null);
                    mTouchState = FLING;
                    mWaitingToScrollerFinish = true;

                    mScroller.startScroll(0, initialY, 0, distance, duration);

                    postOnAnimation(this);
                }
                break;
                case LAYOUT_DIRECTLY_HORIZONTAL: {
                    int initialX = distance < 0 ? Integer.MAX_VALUE : 0;
                    mLastFlingX = initialX;
                    mScroller.setInterpolator(linear ? sLinearInterpolator : null);
                    mTouchState = FLING;
                    mWaitingToScrollerFinish = true;

                    mScroller.startScroll(initialX, 0, distance, 0, duration);

                    postOnAnimation(this);
                }
                break;
            }

            if (mTouchState == FLING) reportScrollStateChanged(OnScrollListener.SCROLL_STATE_FLING);
        }

        /**
         * 回弹操作
         */
        public void startSpringBack(int x, int y) {
            startSpringBack(x, y, (int) (Math.abs(x) > Math.abs(y) ? Math.abs(x) * 1.5f : Math.abs(y) * 1.5f));
        }

        /**
         * 回弹操作
         */
        public void startSpringBack(int x, int y, int duration) {
            endScrollerAnimation(false);

            switch (mLayoutDirectly) {
                case LAYOUT_DIRECTLY_HORIZONTAL: {///水平方向回弹
                    mSpringBackScroller.startScroll(0, 0, x, 0, duration);
                }
                break;
                case LAYOUT_DIRECTLY_VERTICAL: {//垂直方向回弹
                    mSpringBackScroller.startScroll(0, 0, 0, y, duration);
                }
                break;
            }

            mTouchState = SPRING_BACK;
            mSpringDuration = duration;

            if (duration > 0) {
                postOnAnimation(this);
            } else {
                run();
            }
        }

        public void endScrollerAnimation(boolean idle) {
            endScrollerAnimation(idle, idle);
        }

        public void endScrollerAnimation(boolean reportIdleEvent, boolean resetState) {
            removeCallbacks(this);
            mScroller.abortAnimation();
            mSpringBackScroller.abortAnimation();

            mLastFlingX = 0;
            mLastFlingY = 0;
            mLastSpringBackX = 0;
            mLastSpringBackY = 0;
            mWaitingToScrollerFinish = false;

            if (resetState) {
                mScrollTrigger = SCROLL_TRIGGER_SYSTEM;
                mTouchState = REST;
            }

            if (reportIdleEvent) {
                reportScrollStateChanged(OnScrollListener.SCROLL_STATE_IDLE);
            }


            if (DEBUG) Log.i(TAG, "End Scroller anim => " + reportIdleEvent);
        }

        @Override
        public void run() {
            switch (mTouchState) {
                case FLING: {//快速滚动
                    switch (mLayoutDirectly) {
                        case LAYOUT_DIRECTLY_VERTICAL: {
                            if (mItemCount == 0 || getChildCount() == 0) {
                                endScrollerAnimation(true);
                                return;
                            } else {
                                flingWithVerticalDirection();
                            }
                        }
                        break;
                        case LAYOUT_DIRECTLY_HORIZONTAL: {
                            if (mItemCount == 0 || getChildCount() == 0) {
                                endScrollerAnimation(true);
                                return;
                            } else {
                                flingWithHorizontalDirection();
                            }
                        }
                        break;
                    }
                }
                break;
                case SPRING_BACK: {//回弹
                    switch (mLayoutDirectly) {
                        case LAYOUT_DIRECTLY_VERTICAL: {
                            if (mItemCount == 0 || getChildCount() == 0) {
                                endScrollerAnimation(true);
                                return;
                            }
                            springBackWithVerticalDirection();
                        }
                        break;
                        case LAYOUT_DIRECTLY_HORIZONTAL: {
                            if (mItemCount == 0 || getChildCount() == 0) {
                                endScrollerAnimation(true);
                                return;
                            }
                            springBackWithHorizontalDirection();
                        }
                        break;
                    }
                }
                break;
            }

        }

        /**
         * 水平方向SpringBack操作
         */
        private void springBackWithHorizontalDirection() {

            final OverScroller scroller = mSpringBackScroller;

            if (scroller.computeScrollOffset()) {
                int x = scroller.getCurrX();
                int delta = x - mLastSpringBackX;
                mLastSpringBackX = x;
                startScrollIfNeed(delta, 0);
                if (mSpringDuration > 0) {
                    postOnAnimation(this);
                } else {
                    run();
                }
            } else {
                int x = scroller.getCurrX();
                int delta = x - mLastSpringBackX;
//                offsetChildrenLeftAndRight(delta);
                startScrollIfNeed(delta, 0);
                endScrollerAnimation(true);
//                if (DEBUG) {
//                    ALog.i(TAG, "Child count == " + getChildCount() + " Left == " + getLeft() + " Right == " + getRight());
//                    for (int i = 0; i < getChildCount(); i++) {
//                        View child = getChildAt(i);
//                        ALog.i(TAG, String.format(Locale.US, "Child [%d] left => %d right => %d ", i, child.getLeft(), child.getRight()));
//                    }
//                }
            }
        }

        /**
         * 垂直方向上SpringBack操作
         */
        private void springBackWithVerticalDirection() {
            final OverScroller scroller = mSpringBackScroller;

            if (scroller.computeScrollOffset()) {
                int y = scroller.getCurrY();
                int delta = y - mLastSpringBackY;
                mLastSpringBackY = y;
                startScrollIfNeed(0, delta);
                if (mSpringDuration > 0) {
                    postOnAnimation(this);
                } else {
                    run();
                }
            } else {
                int y = scroller.getCurrY();
                int delta = y - mLastSpringBackY;
                startScrollIfNeed(0, delta);
//                offsetChildrenLeftAndRight(delta);
                endScrollerAnimation(true);
//                if (DEBUG) {
//                    ALog.i(TAG, "Child count == " + getChildCount() + " Left == " + getLeft() + " Right == " + getRight());
//                    for (int i = 0; i < getChildCount(); i++) {
//                        View child = getChildAt(i);
//                        ALog.i(TAG, String.format(Locale.US, "Child [%d] left => %d right => %d ", i, child.getLeft(), child.getRight()));
//                    }
//                }
            }
        }

        /**
         * 在水平方快速滚动
         */
        private void flingWithHorizontalDirection() {
            final OverScroller scroller = mScroller;

            boolean more = scroller.computeScrollOffset();

            final int x = scroller.getCurrX();


            //修护快速滚动时,无法滚动的bug
            if (x == 0 && more) {
                postOnAnimation(this);
                return;
            }

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int xDelta = mLastFlingX - x;

            if (xDelta > 0) {
                // Don't flingInner more than 1 screen
                xDelta = Math.min(getRight() - getLeft() - 1, xDelta);
            } else {
                // Don't flingInner more than 1 screen
                xDelta = Math.max(-(getRight() - getLeft() - 1), xDelta);
            }

            boolean hasMoved = startScrollIfNeed(xDelta, 0);

            mLastFlingX = x;

            if (x != scroller.getFinalX() && more && ((hasMoved && Math.abs(xDelta) > 0) || mWaitingToScrollerFinish)) {
                postOnAnimation(this);
            } else {
                if (DEBUG)
                    Log.i(TAG, "Prepare adjust view Velocity => " + scroller.getCurrVelocity());
                endScrollerAnimation(false);
                adjustSelView();
            }
        }

        /**
         * 在垂直方向快速滚动
         */
        private void flingWithVerticalDirection() {
            final OverScroller scroller = mScroller;

            boolean more = scroller.computeScrollOffset();

            final int y = scroller.getCurrY();

            //修护快速滚动的bug
            if (y == 0 && more) {
                postOnAnimation(this);
                return;
            }

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int yDelta = mLastFlingY - y;

            if (DEBUG) {
                Log.i(TAG, String.format(Locale.US, "flingWithVerticalDirection Last flingInner Y => %s Current Y %s ", mLastFlingY, y));
                Log.i(TAG, String.format("flingWithVerticalDirection yDelta => %s  Has More %s", yDelta, more));
            }

            if (yDelta > 0) {
                // Don't flingInner more than 1 screen
                yDelta = Math.min(getBottom() - getTop() - 1, yDelta);
            } else {
                // Don't flingInner more than 1 screen
                yDelta = Math.max(-(getBottom() - getTop() - 1), yDelta);
            }

            boolean hasMoved = startScrollIfNeed(0, yDelta);

            mLastFlingY = y;

            if (more && ((hasMoved && Math.abs(yDelta) > 0) || mWaitingToScrollerFinish)) {
                postOnAnimation(this);
            } else {
                endScrollerAnimation(false);
                mScrollTrigger = SCROLL_TRIGGER_SYSTEM;
                adjustSelView();
            }
        }
    }

    /**
     * 平滑滚动Runnable
     */
    protected class SmoothScrollRunnable implements Runnable {

        private int mTargetPosition;
        private int mDuration;
        private int mLastSeenPos;
        private int mStartOffset;

        public void scrollToPosition(int position, int offset, int duration) {
            stop();

            this.mTargetPosition = position;
            mStartOffset = offset;

            switch (mViewMode) {
                case LIST_VIEW:
                case END_LESS_LIST_VIEW: {
                    int firstPos = mFirstPosition;

                    final int lastPosition = firstPos + getChildCount() - 1;

                    int viewTravelCount;
                    if (mTargetPosition < firstPos) {
                        viewTravelCount = firstPos - mTargetPosition;
                    } else if (mTargetPosition > lastPosition) {
                        viewTravelCount = mTargetPosition - lastPosition;
                    } else {
                        //In screen
                        View targetChild = getChildAt(mTargetPosition - firstPos);
                        final int targetStart = mLayoutDirectly == LAYOUT_DIRECTLY_VERTICAL ? targetChild.getTop() : targetChild.getLeft();
                        smoothScroll(targetStart + offset, duration, true);
                        stop();
                        return;
                    }

                    // Estimate how many screens we should travel
                    final float screenTravelCount = (float) viewTravelCount / getChildCount();
                    mDuration = screenTravelCount < 1 ? duration : (int) (duration / screenTravelCount);
                    mStartOffset = offset;
                    mLastSeenPos = INVALID_POSITION;
                }
                break;
                case WHEEL_VIEW:
                case ENDLESS_WHEEL_VIEW: {

                    final int selViewPosition = getSelectedViewPosition();

                    int firstPos = (mFirstPosition + selViewPosition) % mItemCount;

                    final int lastPosition = firstPos + getChildCount() - selViewPosition - 1;

                    int viewTravelCount;
                    if (mTargetPosition < firstPos) {
                        viewTravelCount = firstPos - mTargetPosition;
                    } else if (mTargetPosition > lastPosition) {
                        viewTravelCount = mTargetPosition - lastPosition;
                    } else {
                        //In screen

                        boolean isVertical = mLayoutDirectly == LAYOUT_DIRECTLY_VERTICAL;

                        int selPos = getSelectedViewPosition();

                        if (selPos == INVALID_POSITION) selPos = 0;

                        View targetChild = getChildAt(selPos + mTargetPosition - firstPos);

                        if (targetChild != null) {
                            int distance = 0;

                            if (position < firstPos) {//向下滚动,展示更多的顶部内容
                                distance = isVertical ?
                                        (getYSel() + ((targetChild.getBottom() - targetChild.getTop()) / 2)) :
                                        (getXSel() + ((targetChild.getRight() - targetChild.getLeft()) / 2));

                            } else if (position > firstPos) {//向上滚动,展示更多的底部内容
                                distance = isVertical ?
                                        targetChild.getBottom() - ((targetChild.getBottom() - targetChild.getTop()) / 2) - getYSel() :
                                        targetChild.getRight() - ((targetChild.getRight() - targetChild.getLeft()) / 2) - getXSel();

                            } else {//滚动到target child 的中点
                                distance = isVertical ?
                                        targetChild.getTop() + (targetChild.getBottom() - targetChild.getTop()) / 2 - getYSel() :
                                        targetChild.getLeft() + (targetChild.getRight() - targetChild.getLeft()) / 2 - getXSel();
                            }

                            smoothScroll(distance, duration, true);

                            stop();
                        }
                        return;
                    }

                    // Estimate how many screens we should travel
                    final float screenTravelCount = (float) viewTravelCount / getChildCount();
                    mDuration = screenTravelCount < 1 ? duration : (int) (duration / screenTravelCount);
                    mLastSeenPos = INVALID_POSITION;
                }
            }

            postOnAnimation(this);
        }

        /**
         * 停止滚动
         */
        private void stop() {

            removeCallbacks(this);
            invalidate();
        }

        @Override
        public void run() {

            switch (mViewMode) {
                case LIST_VIEW:
                case END_LESS_LIST_VIEW: {
                    int firstPos = mFirstPosition;

                    mLastSeenPos = firstPos;

                    final int childCount = getChildCount();
                    final int position = mTargetPosition;
                    final int lastPos = firstPos + childCount - 1;

                    int viewTravelCount = 0;
                    if (position < firstPos) {
                        viewTravelCount = firstPos - position + 1;
                    } else if (position > lastPos) {
                        viewTravelCount = position - lastPos;
                    }

                    // Estimate how many screens we should travel
                    final float screenTravelCount = (float) viewTravelCount / childCount;

                    final boolean isVertical = mLayoutDirectly == LAYOUT_DIRECTLY_VERTICAL;

                    final float modifier = Math.min(Math.abs(screenTravelCount), 1.f);
                    if (position < firstPos) {
                        final int distance = (int) (-(isVertical ? getHeight() : getWidth()) * modifier);
                        final int duration = (int) (mDuration * modifier);
                        smoothScroll(distance, duration, true);
                        postOnAnimation(this);
                    } else if (position > lastPos) {
                        final int distance = (int) (isVertical ? getHeight() : getWidth() * modifier);
                        final int duration = (int) (mDuration * modifier);
                        smoothScroll(distance, duration, true);
                        postOnAnimation(this);
                    } else {
                        // On-screen, just scroll.

                        View targetChild = getChildAt(mTargetPosition - firstPos);
                        final int distance = isVertical ? targetChild.getTop() : targetChild.getLeft();
                        final int duration = (int) (mDuration * ((float) Math.abs(distance) / getHeight()));
                        smoothScroll(distance + mStartOffset, duration, true);
                    }
                }
                break;
                case WHEEL_VIEW:
                case ENDLESS_WHEEL_VIEW: {

                    final int selViewPosition = getSelectedViewPosition();

                    int firstPos = (mFirstPosition + selViewPosition) % mItemCount;

                    if (DEBUG)
                        Log.i(TAG, String.format(Locale.US, "Smooth scroll => First position [%s] TargetPosition  [%s]", firstPos, mTargetPosition));

                    mLastSeenPos = firstPos;

                    final int position = mTargetPosition;

                    final int childCount = getChildCount() - selViewPosition;

                    final int lastPos = firstPos + childCount - 1;

                    int viewTravelCount = 0;
                    if (position < firstPos) {
                        viewTravelCount = firstPos - position + 1;
                    } else if (position > lastPos) {
                        viewTravelCount = position - lastPos;
                    }

                    // Estimate how many screens we should travel
                    final float screenTravelCount = (float) viewTravelCount / childCount;

                    final boolean isVertical = mLayoutDirectly == LAYOUT_DIRECTLY_VERTICAL;

                    final float modifier = Math.min(Math.abs(screenTravelCount), 1.f);
                    if (position < firstPos) {
                        final int distance = (int) (-(isVertical ? /*getHeight()*//*selView.getTop()*/getYSel() : /*getWidth()*/ /*selView.getLeft()*/getXSel()) * modifier);
                        final int duration = (int) (mDuration * modifier);
                        smoothScroll(distance, duration, true);
                        postOnAnimation(this);
                    } else if (position > lastPos) {
                        final int distance = (int) ((isVertical ? (getHeight() - getYSel()) : (getWidth() - getXSel())) * modifier);
                        final int duration = (int) (mDuration * modifier);
                        smoothScroll(distance, duration, true);
                        postOnAnimation(this);
                    } else {
                        // On-screen, just scroll.

                        View targetChild = getChildAt(selViewPosition + mTargetPosition - firstPos);

                        int distance = 0;
                        int duration = 0;

                        if (position < firstPos) {//向下滚动,展示更多的顶部内容
                            distance = isVertical ?
                                    (getYSel() + ((targetChild.getBottom() - targetChild.getTop()) / 2)) :
                                    (getXSel() + ((targetChild.getRight() - targetChild.getLeft()) / 2));


                        } else if (position > firstPos) {//向上滚动,展示更多的底部内容
                            distance = isVertical ?
                                    targetChild.getBottom() - ((targetChild.getBottom() - targetChild.getTop()) / 2) - getYSel() :
                                    targetChild.getRight() - ((targetChild.getRight() - targetChild.getLeft()) / 2) - getXSel();

                        } else {//滚动到target child 的中点
                            //todo May have bugs.
                            distance = isVertical ?
                                    targetChild.getTop() + (targetChild.getBottom() - targetChild.getTop()) / 2 - getYSel() :
                                    targetChild.getLeft() + (targetChild.getRight() - targetChild.getLeft()) / 2 - getXSel();

                        }

                        duration = (int) (mDuration * ((float) Math.abs(distance) / (isVertical ? getYSel() : getXSel())));

                        smoothScroll(distance, duration, true);
                    }
                }
                break;
            }

        }
    }

    /**
     * 填充模式
     */
    protected enum StuffMode {
        /**
         * 向下填充
         */
        flowDown,
        /**
         * 向上填充
         */
        flowUp,
        /**
         * 向左填充
         */
        flowLeft,
        /**
         * 向右填充
         */
        flowRight
    }

    /**
     * 视图模式
     */
    public static enum ViewMode {
        /**
         * 普通的list view 展示模式
         */
        LIST_VIEW(1),
        /**
         * 可无限循环滚动的 list view 展示模式
         */
        END_LESS_LIST_VIEW(2),
        /**
         * 滚轮视图 展示模式
         * 这个和普通的list view 的区别就是
         * 这个视图拥有选中区域,当视图滚动到这个区域中后,视图会被选中
         * 那么同样因为可以被选中的这个原因,再假设选中区域在视图的中心点
         * 那么第一个视图就将可以被拖动到视图中心点
         * 这就是区别咯
         */
        WHEEL_VIEW(3),
        /**
         * 和滚轮模式一样
         * 但是是可以无限循环的
         */
        ENDLESS_WHEEL_VIEW(4);

        private final int mValue;

        ViewMode(int value) {
            this.mValue = value;
        }

        public static ViewMode wrap(int value) {
            switch (value) {
                case 1:
                    return LIST_VIEW;
                case 2:
                    return END_LESS_LIST_VIEW;
                case 3:
                    return WHEEL_VIEW;
                case 4:
                    return ENDLESS_WHEEL_VIEW;
            }
            return null;
        }
    }

    /**
     * 滚动监听器
     */
    public static interface OnScrollListener {

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
         * @param view        滚动的视图咯
         * @param scrollState 当前的滚动状态
         */
        public void onScrollStateChanged(EasyAdapterView view, int scrollState);

        /**
         * 当滚动后调用
         *
         * @param view             滚动的视图
         * @param firstVisibleItem 第一个可见的的条目的位置
         * @param visibleItemCount 可见的条目的数量
         * @param totalItemCount   条目的总数量
         */
        public void onScroll(EasyAdapterView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public static interface OnSelectedItemChangedListener {

        void onSelectedItemChanged(EasyAdapterView easyAdapterView, int oldSel, View oldSelView, int position, View selectedView);
    }

    /**
     * 保存实例状态的holder
     */
    private static class InstanceStateHolder implements Parcelable {
        private int firstPos;
        private int amountMax;
        private ViewMode mode;
        private int orientation;
        private int startOffset;

        private Parcelable superParcelable;

        public InstanceStateHolder(EasyAdapterView easyAdapterView, Parcelable superParcelable) {
            this.firstPos = easyAdapterView.mFirstPosition;
            this.amountMax = easyAdapterView.mMaxAmountScrollPage;
            this.mode = easyAdapterView.mViewMode;
            this.orientation = easyAdapterView.mLayoutDirectly;
            this.startOffset = easyAdapterView.mStartOffset;
            this.superParcelable = superParcelable;
        }

        public InstanceStateHolder() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.firstPos);
            dest.writeInt(this.amountMax);
            dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
            dest.writeInt(this.orientation);
            dest.writeParcelable(this.superParcelable, flags);
        }

        protected InstanceStateHolder(Parcel in) {
            this.firstPos = in.readInt();
            this.amountMax = in.readInt();
            int tmpMode = in.readInt();
            this.mode = tmpMode == -1 ? null : ViewMode.values()[tmpMode];
            this.orientation = in.readInt();
            this.superParcelable = in.readParcelable(Parcelable.class.getClassLoader());
        }

        public static final Creator<InstanceStateHolder> CREATOR = new Creator<InstanceStateHolder>() {
            @Override
            public InstanceStateHolder createFromParcel(Parcel source) {
                return new InstanceStateHolder(source);
            }

            @Override
            public InstanceStateHolder[] newArray(int size) {
                return new InstanceStateHolder[size];
            }
        };
    }
}
