package com.lovely3x.easyadapterview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private AdapterView mEasyAdapterView;
    private AdapterView mEasyAdapterView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mEasyAdapterView = (AdapterView) findViewById(R.id.eav_activity_main_sample);
        this.mEasyAdapterView2 = (ListView) findViewById(R.id.lv_activity_main_sample);

        String[] array = new String[30];
        for (int i = 0; i < array.length; i++) {
            array[i] = "Sample " + i;
        }

        this.mEasyAdapterView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp != null) lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.setBackgroundColor(Color.GREEN);
                return view;
            }
        });

        this.mEasyAdapterView2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(Color.GREEN);
                return view;
            }
        });

    }
}
