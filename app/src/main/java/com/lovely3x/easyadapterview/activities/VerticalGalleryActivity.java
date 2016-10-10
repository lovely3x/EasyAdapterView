package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * 垂直 Gallery
 * 演示特性: 垂直方向滚动的相册
 * <p/>
 * Created by lovely3x on 16/8/27.
 */
public class VerticalGalleryActivity extends BaseActivity<String> {

    private EasyAdapterView mVerticalGallery;
    private CheckBox mEndLessMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_gallery);
        mVerticalGallery = (EasyAdapterView) findViewById(R.id.eav_activity_vertical_gallery_gallery);
        this.mEndLessMode = (CheckBox) findViewById(R.id.accb_activity_vertical_gallery_endless);

        mEndLessMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mVerticalGallery.setMode(isChecked ?
                        EasyAdapterView.ViewMode.ENDLESS_WHEEL_VIEW :
                        EasyAdapterView.ViewMode.WHEEL_VIEW
                );
            }
        });


        mVerticalGallery.setAdapter(new BaseEasyAdapter(this, makeData(100)));
    }

    protected List<String> makeData(int count) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(String.valueOf(i));
        }
        return data;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.view_vertical_gallery_item, parent, false);
    }

    @Override
    protected void handleData(View view, int position, String s) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_view_gallery_item_img);
        TextView tv = (TextView) view.findViewById(R.id.tv_view_gallery_item_index);
        tv.setText(String.valueOf(position));
    }

}
