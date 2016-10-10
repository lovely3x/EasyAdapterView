package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lovely3x on 16/8/18.
 */
public class GalleryActivity extends BaseActivity<String> implements View.OnClickListener {

    EasyAdapterView eavGallery;
    private EditText mEtPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);


        eavGallery = (EasyAdapterView) findViewById(R.id.eav_activity_gallery_gallery);
        this.mEtPosition = (EditText) findViewById(R.id.et_position);

        findViewById(R.id.first).setOnClickListener(this);
        findViewById(R.id.last).setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);

        eavGallery.setAdapter(new BaseEasyAdapter(this, makeData(0)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first: {
                eavGallery.smoothToPosition(0, 200);
            }
            break;
            case R.id.last: {
                eavGallery.smoothToPosition(eavGallery.getAdapter().getCount() - 1,  200);
            }
            break;
            case R.id.select: {
                Editable text = mEtPosition.getText();
                int position = 0;
                if (!TextUtils.isEmpty(text.toString().trim())) {
                    position = Integer.parseInt(text.toString().trim());
                }
                eavGallery.setSelection(position);
            }
            break;
        }
    }

    @Override
    protected List<String> makeData(int count) {
        return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return  getLayoutInflater().inflate(R.layout.view_gallery_item, parent, false);
    }

    @Override
    protected void handleData(View view, int position, String s) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_view_gallery_item_img);
        TextView tv = (TextView) view.findViewById(R.id.tv_view_gallery_item_index);
        tv.setText(String.valueOf(position));
    }
}
