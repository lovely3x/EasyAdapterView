package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表格式
 * Created by lovely3x on 16/8/27.
 */
public class ListViewActivity extends BaseActivity<String> implements View.OnClickListener {

    private EasyAdapterView mListView;

    EditText etNewPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        findViewById(R.id.b_activity_list_view_select).setOnClickListener(this);
        findViewById(R.id.b_activity_list_view_last).setOnClickListener(this);
        findViewById(R.id.b_activity_list_view_first).setOnClickListener(this);
        findViewById(R.id.b_activity_list_view_offset_from_top).setOnClickListener(this);


        etNewPosition = (EditText) findViewById(R.id.et_activity_list_view_new_position);

        this.mListView = (EasyAdapterView) findViewById(R.id.eav_activity_list_view_list);
        mListView.setAdapter(new BaseEasyAdapter(this, makeData(1000)));
    }

    @Override
    protected List<String> makeData(int count) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) list.add(String.valueOf(i));
        return list;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.view_list_view_item, parent, false);
    }

    @Override
    protected void handleData(View view, int position, String s) {
        TextView tv = (TextView) view.findViewById(R.id.tv_view_list_view_item_text);
        tv.setText(s);
    }

    @Override
    public void onClick(View v) {
        int offset = 50;
        switch (v.getId()) {
            case R.id.b_activity_list_view_select: {
                String strPosition = etNewPosition.getText().toString().trim();

                int position = 0;

                if (!TextUtils.isEmpty(strPosition)) {
                    position = Integer.parseInt(strPosition);
                }
                mListView.setSelectionFromStart(position,50);
            }
            break;
            case R.id.b_activity_list_view_last://Last
                mListView.smoothToPosition(mListView.getCount(), 2 * 1000);
                break;
            case R.id.b_activity_list_view_first://First
                mListView.smoothToPosition(0, 2 * 1000);
                break;
            case R.id.b_activity_list_view_offset_from_top: {
                String strPosition = etNewPosition.getText().toString().trim();

                int position = 0;

                if (!TextUtils.isEmpty(strPosition)) {
                    position = Integer.parseInt(strPosition);
                }
                mListView.smoothToPositionFromStart(position, offset);
            }
            break;
        }
    }
}
