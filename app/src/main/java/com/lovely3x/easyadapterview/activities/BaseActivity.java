package com.lovely3x.easyadapterview.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by lovely3x on 16/8/27.
 */
public abstract class BaseActivity<T> extends AppCompatActivity {

    protected abstract List<T> makeData(int count);

    protected abstract View createView(int position, ViewGroup parent);

    protected abstract void handleData(View view, int position, T t);

    /**
     * Created by lovely3x on 16/8/18.
     */
    public class BaseEasyAdapter extends BaseAdapter {

        private final LayoutInflater mInflator;
        private final List<T> mData;

        public BaseEasyAdapter(Context context, List<T> data) {
            this.mInflator = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public T getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = BaseActivity.this.createView(position, parent);
            } else {
                view = convertView;
            }

            handleData(view, position, getItem(position));

            return view;
        }
    }
}
