package com.lovely3x.easyadapterview.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.eavlibrary.ext.banner.EasyBanner;
import com.lovely3x.eavlibrary.ext.banner.IBannerController;
import com.lovely3x.eavlibrary.ext.banner.indicator.DrawableIndicatorProvider;

/**
 * 演示Banner
 * Created by lovely3x on 16/9/17.
 */
public class BannerActivity extends AppCompatActivity implements IBannerController.OnItemClickedListener {

    private static final String TAG = "BannerActivity";
    private EasyBanner mBanner;
    private ListView mList;

    public int getContentView() {
        return R.layout.activity_banner;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        this.mBanner = new EasyBanner(this);
        this.mList = (ListView) findViewById(R.id.eav_activity_banner_list);

        this.mList.addHeaderView(mBanner);
        this.mList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, makeData()));

        //为banner设置内容适配器
        this.mBanner.setAdapter(new LocalImgBannerAdapter());
        //设置Banner的指示器提供者
        this.mBanner.setIndicatorProvider(new DrawableIndicatorProvider(this, getResources().getDrawable(R.drawable.indicator_selector)));
        //设置banner条目点击监听器
        this.mBanner.getBannerController().setOnItemClickedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBanner != null) {
            //使用指定的间隔时长来滚动
            mBanner.getBannerController().startTruing(5 * 1000);
            //使用默认的间隔时长 3000ms 来进行滚动
//            mBanner.getBannerController().startTruing();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBanner != null) {
            //在Activity暂停时停止banner的滚动
            mBanner.getBannerController().stopTruing();
        }
    }

    private String[] makeData() {
        String[] data = new String[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = "Simple " + i;
        }
        return data;
    }

    @Override
    public void onItemClicked(int position) {
        //当Banner的条目被点击后调用
        Log.i(TAG, String.valueOf(position));
    }

    public class LocalImgBannerAdapter extends BaseAdapter {

        private int[] mBannerResource = new int[]{R.drawable.img_banner1, R.drawable.img_banner2, R.drawable.img_banner3};

        @Override
        public int getCount() {
            return mBannerResource.length;
        }

        @Override
        public Integer getItem(int position) {
            return mBannerResource[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_local_img_banner, parent, false);
            }
            ImageView ivImg = (ImageView) convertView.findViewById(R.id.iv_list_item_local_img_banner_img);
            ivImg.setBackgroundResource(getItem(position));
            return convertView;
        }
    }
}
