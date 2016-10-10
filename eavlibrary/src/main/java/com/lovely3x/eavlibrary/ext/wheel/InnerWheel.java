package com.lovely3x.eavlibrary.ext.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lovely3x.eavlibrary.EasyAdapterView;

public class InnerWheel extends EasyAdapterView {

    public InnerWheel(Context context) {
        super(context);
    }

    public InnerWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerWheel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 测量自定的条目的位置
     *
     * @param position 需要测量高度的位置
     * @return
     */
    public int measureItemHeightAtPosition(int position) {
        if (mAdapter == null || mAdapter.getCount() < position)
            throw new IllegalArgumentException();

        View view = obtainView(position);
        setupChildView(view);
        return view.getMeasuredHeight();
    }

    @Override
    public int getYSel() {
        return super.getYSel();
    }
}