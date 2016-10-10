package com.lovely3x.easyadapterview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.lovely3x.easyadapterview.activities.AbstractWheelViewActivity;
import com.lovely3x.easyadapterview.activities.BannerActivity;
import com.lovely3x.easyadapterview.activities.ClassicDateWheelActivity;
import com.lovely3x.easyadapterview.activities.ClassicTimeWheelActivity;
import com.lovely3x.easyadapterview.activities.DateChoiceActivity;
import com.lovely3x.easyadapterview.activities.GalleryActivity;
import com.lovely3x.easyadapterview.activities.HorizontalListViewActivity;
import com.lovely3x.easyadapterview.activities.ListViewActivity;
import com.lovely3x.easyadapterview.activities.PtrBannerActivity;
import com.lovely3x.easyadapterview.activities.VerticalGalleryActivity;
import com.lovely3x.easyadapterview.activities.VerticalWheelActivity;
import com.lovely3x.easyadapterview.activities.WheelActivity;
import com.lovely3x.eavlibrary.EasyAdapterView;

import java.util.Arrays;

/**
 * Created by lovely3x on 16/8/18.
 */
public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EasyAdapterView seavMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_point);
        seavMenu = (EasyAdapterView) findViewById(R.id.seav_activity_enter_point_menu);

        seavMenu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, Arrays.asList(
                "Gallery",
                "VerticalGallery",
                "ListView",
                "WheelView",
                "VerticalWheelView",
                "Banner",
                "PTRBanner",
                "DateSelector",
                "ClassicDateSelector",
                "TimeWheelActivity",
                "VerticalListView",
                "AbstractWheelView"
        )));
        seavMenu.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, VerticalGalleryActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, WheelActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, VerticalWheelActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case 6:
                startActivity(new Intent(this, PtrBannerActivity.class));
                break;
            case 7:
                startActivity(new Intent(this, DateChoiceActivity.class));
                break;
            case 8:
                startActivity(new Intent(this, ClassicDateWheelActivity.class));
                break;
            case 9:
                startActivity(new Intent(this, ClassicTimeWheelActivity.class));
                break;
            case 10:
                startActivity(new Intent(this, HorizontalListViewActivity.class));
                break;
            case 11:
                startActivity(new Intent(this, AbstractWheelViewActivity.class));
                break;
        }
    }
}
