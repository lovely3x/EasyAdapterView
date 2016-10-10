package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lovely3x.easyadapterview.R;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by lovely3x on 16/9/18.
 */
public class PtrBannerActivity extends BannerActivity {

    private PtrFrameLayout mPtrFrameLayout;


    @Override
    public int getContentView() {
        return R.layout.activity_ptr_banner;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //和下拉刷新一起使用
        //下拉刷新的使用请参考 https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
        this.mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.pcfl_activity_banner_pull_refresh);
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 3000);
            }
        });

    }
}
