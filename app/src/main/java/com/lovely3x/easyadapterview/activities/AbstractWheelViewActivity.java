package com.lovely3x.easyadapterview.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.lovely3x.easyadapterview.R;
import com.lovely3x.easyadapterview.beans.Area;
import com.lovely3x.easyadapterview.beans.City;
import com.lovely3x.easyadapterview.beans.Displayable;
import com.lovely3x.easyadapterview.beans.Province;
import com.lovely3x.eavlibrary.EasyAdapterView;
import com.lovely3x.easyadapterview.widgets.ClassicTimeWheelView;
import com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayout;
import com.lovely3x.eavlibrary.ext.wheel.classic.ClassicWheelController;
import com.lovely3x.eavlibrary.ext.wheel.classic.ClassicWheelLayoutManager;
import com.lovely3x.jsonparser.model.JSONArray;
import com.lovely3x.jsonparser.source.JSONSourceImpl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 这里我们介绍如何利用我们提供的 {@link com.lovely3x.eavlibrary.ext.wheel.abs.WheelLayout}
 * 来做Wheel view
 * 他和前面所介绍的WheelView本质一样,
 * 但是前面介绍的都是不通用的,但是是简单的
 * 我们在这里尝试对wheelView做一个抽象
 * 所以就有了它啦
 * Created by lovely3x on 16/9/26.
 */
public class AbstractWheelViewActivity extends AppCompatActivity implements ClassicWheelController.WheelSelectedChangedListener, ClassicWheelController.WheelScrollChangedListener {

    private static final String TAG = "AWVA";

    private WheelLayout mWheelLayout;
    private WheelLayout mTwoWheelLayout;
    private WheelLayout mCityLayout;

    private ClassicWheelController mThreeColLinearWheelController;
    private ClassicWheelController mTwoColLinearWheelController;

    private ClassicWheelController mCityLinearWheelController;

    private CityAdapter<Province> mProvinceAdapter;
    private CityAdapter<City> mCityAdapter;
    private CityAdapter<Area> mAreaAdapter;

    /**
     * 级联滚动下是否是快速滚动模式
     * 快速滚动模式由 {@link com.lovely3x.eavlibrary.ext.wheel.classic.ClassicWheelController.WheelScrollChangedListener}实现
     * 迟缓模式由 {@link com.lovely3x.eavlibrary.ext.wheel.classic.ClassicWheelController.WheelScrollChangedListener}实现
     */
    private boolean fastMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_abstract_wheel_view);

        this.mWheelLayout = (WheelLayout) findViewById(R.id.wl_activity_abstract_wheel_view_wheel_layout);
        this.mTwoWheelLayout = (WheelLayout) findViewById(R.id.wl_activity_abstract_wheel_view_two_wheel_layout);
        this.mCityLayout = (WheelLayout) findViewById(R.id.wl_activity_abstract_wheel_view_city_layout);

        processThreeCol();

        processTowCol();

        processCity();
    }

    /**
     * 演示多级联动
     */
    private void processCity() {
        mCityLayout.setWheelController(this.mCityLinearWheelController =
                new ClassicWheelController(mCityLayout, this, 3, ClassicWheelLayoutManager.ORIENTATION_HORIZONTAL));

        mCityLinearWheelController = (ClassicWheelController) mCityLayout.getWheelController();

        mProvinceAdapter = new CityAdapter<Province>(null);
        mCityAdapter = new CityAdapter<City>(null);
        mAreaAdapter = new CityAdapter<Area>(null);

        //为滚轮设置数据适配器
        //和上面同理
        mCityLinearWheelController.setAdapter(mProvinceAdapter, mCityAdapter, mAreaAdapter);

        //不使用无限滚动模式
//        mCityLinearWheelController.getLayoutManager().setEndlessMode(false);

        if (fastMode) {//快速响应模式下通过条目滚动实现
            attachItemChangedListener();
        } else {//缓慢 模式通过滚动监听实现
            attachScrollChangedListener();
        }

        getProvinceData();
    }

    protected void attachItemChangedListener() {
        ClassicWheelController.WheelSelectedChangedListener listener = new ClassicWheelController.WheelSelectedChangedListener() {
            @Override
            public void onSelectedItemChanged(int no, EasyAdapterView view, int oldSel, View oldSelView, int position, View selectedView) {
                if (no == 0) {//省份
                    if (position != -1) {
                        Province province = mProvinceAdapter.getItem(position);
                        if (province != null) {
                            makeCityAdapter(province);
                        }

                    }
                } else if (no == 1) {//城市
                    if (position != -1) {
                        City city = mCityAdapter.getItem(position);
                        if (city != null) makeAreaAdapter(city);
                    }
                } else {//区

                }
            }
        };


        mCityLinearWheelController.setWheelSelectedChangedListener(listener, listener, listener);
    }

    protected void attachScrollChangedListener() {
        ClassicWheelController.WheelScrollChangedListener listener = new ClassicWheelController.WheelScrollChangedListener() {

            @Override
            public void onScrollStateChanged(int no, EasyAdapterView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    final int position = view.getSelectedItemPosition();
                    if (no == 0) {//省份
                        if (position != -1) {
                            Province province = mProvinceAdapter.getItem(position);
                            if (province != null) makeCityAdapter(province);
                        }
                    } else if (no == 1) {//城市
                        if (position != -1) {
                            City city = mCityAdapter.getItem(position);
                            if (city != null) makeAreaAdapter(city);
                        }
                    } else {//区
                    }
                }
            }

            @Override
            public void onScroll(int no, EasyAdapterView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };

        mCityLinearWheelController.setWheelScrollChangedListener(listener, listener, listener);
    }

    protected void makeProvinceAdapter(List<Province> provinces) {
        mProvinceAdapter.setDatas(provinces);
    }

    protected void makeCityAdapter(Province province) {
        mCityAdapter.setDatas(province.child);
        ClassicWheelLayoutManager layoutManager = mCityLinearWheelController.getLayoutManager();
        if (!fastMode)
            layoutManager.getWheelViews()[1].smoothToPosition(0, 200);//在重新设置数据后平滑的滚动到位置 0
    }

    protected void makeAreaAdapter(City city) {
        mAreaAdapter.setDatas(city.child);
        ClassicWheelLayoutManager layoutManager = (ClassicWheelLayoutManager) mCityLinearWheelController.getLayoutManager();
        if (!fastMode)
            layoutManager.getWheelViews()[2].smoothToPosition(0, 200);//在重新设置数据后平滑的滚动到位置 0
    }

    /**
     * 获取省份数据
     */
    protected void getProvinceData() {
        final ProgressDialog dialog = ProgressDialog.show(this, "Alert", "Loading...");
        new AsyncTask<Void, Void, List<Province>>() {

            @Override
            protected List<Province> doInBackground(Void... params) {
                try {
                    final InputStream ais = getAssets().open("city.json");
                    final JSONArray jo = new JSONArray(new JSONSourceImpl(ais));

                    return jo.createObjects(Province.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Province> province) {
                super.onPostExecute(province);
                makeProvinceAdapter(province);
                dialog.dismiss();
            }
        }.execute();
    }

    /***
     * 演示两列模式
     */
    private void processTowCol() {

        mTwoWheelLayout.setWheelController(this.mTwoColLinearWheelController = new ClassicWheelController(mTwoWheelLayout, this, 2, ClassicWheelLayoutManager.ORIENTATION_HORIZONTAL));

        mTwoColLinearWheelController = (ClassicWheelController) mTwoWheelLayout.getWheelController();

        //为滚轮设置 监听器
        //一个监听器对应一个滚轮
        //多少个滚轮就需要添加多少个监听器
        mTwoColLinearWheelController.setWheelSelectedChangedListener(this, this);

        //为滚轮设置滚动监听器
        //和上面同理
        mTwoColLinearWheelController.setWheelScrollChangedListener(this, this);

        //为滚轮设置数据适配器
        //和上面同理
        mTwoColLinearWheelController.setAdapter(makeAdapter(), makeAdapter());
    }

    /**
     * 演示三列模式
     */
    private void processThreeCol() {

        mWheelLayout.setWheelController(this.mThreeColLinearWheelController = new ClassicWheelController(mWheelLayout, this, 3, ClassicWheelLayoutManager.ORIENTATION_HORIZONTAL));

        mThreeColLinearWheelController = (ClassicWheelController) mWheelLayout.getWheelController();

        //为滚轮设置 监听器
        //一个监听器对应一个滚轮
        //多少个滚轮就需要添加多少个监听器
        mThreeColLinearWheelController.setWheelSelectedChangedListener(this, this, this);

        //为滚轮设置滚动监听器
        //和上面同理
        mThreeColLinearWheelController.setWheelScrollChangedListener(this, this, this);

        //为滚轮设置数据适配器
        //和上面同理
        mThreeColLinearWheelController.setAdapter(makeAdapter(), makeAdapter(), makeAdapter());
    }

    private ListAdapter makeAdapter() {
        return new ClassicTimeWheelView.TimeAdapter(
                Arrays.asList("1", "2", "3", "4", "6", "6",
                        "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18",
                        "19", "20", "21", "22", "23", "24"), "%s");
    }

    @Override
    public void onSelectedItemChanged(int no, EasyAdapterView view, int oldSel, View oldSelView, int position, View selectedView) {
        Log.i(TAG, String.format("No %s pos %s", no, position));
    }

    @Override
    public void onScrollStateChanged(int no, EasyAdapterView view, int scrollState) {
        Log.i(TAG, String.format("ScrollStateChanged No %s state %s", no, scrollState));
    }

    @Override
    public void onScroll(int no, EasyAdapterView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.i(TAG, String.format(" scroll No %s  firstVisibleItem %s visible item count %s total item count %s ", no, firstVisibleItem, visibleItemCount, totalItemCount));
    }

    public class CityAdapter<T extends Displayable> extends BaseAdapter {
        private List<T> datas;

        public CityAdapter(List<T> datas) {
            this.datas = datas;
        }


        public void setDatas(List<T> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public T getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                //Create
                convertView = getLayoutInflater().inflate(R.layout.list_item_city, parent, false);
                convertView.setTag(holder = new ViewHolder(convertView));
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mTvText.setText(getItem(position).display());
            return convertView;
        }

        public class ViewHolder {
            private TextView mTvText;
            private View mRootView;

            public ViewHolder(View rootView) {
                this.mRootView = rootView;
                this.mTvText = (TextView) mRootView.findViewById(R.id.tv_list_item_city_text);
            }
        }
    }

}
