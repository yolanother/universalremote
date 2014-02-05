package com.doubtech.universalremote.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A page adapter which works with a large data set by reusing views.
 */
public abstract class ListPageAdapter<T> extends PagerAdapter {

    // Views that can be reused.
    private final List<View> mDiscardedViews = new ArrayList<View>();
    // Views that are already in use.
    private final SparseArray<View> mBindedViews = new SparseArray<View>();

    private final ArrayList<T> mItems;
    private final LayoutInflater mInflator;
    private final int mResourceId;
    private Context mContext;

    public ListPageAdapter(Context context) {
        this(context, 0);
    }

    public ListPageAdapter(Context context, int viewRes) {
        mItems = new ArrayList<T>();
        mInflator = LayoutInflater.from(context);
        mResourceId = viewRes;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == mBindedViews.get(mItems.indexOf(obj));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = mBindedViews.get(position);
        if (view != null) {
            mDiscardedViews.add(view);
            mBindedViews.remove(position);
            container.removeView(view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View child = null;
        if (mResourceId != 0) {
            child = mDiscardedViews.isEmpty() ? mInflator.inflate(mResourceId,
                container, false) : mDiscardedViews.remove(0);
        } else {
            child = mDiscardedViews.isEmpty() ?
                    new FrameLayout(mContext) : mDiscardedViews.remove(0);
        }

        T data = mItems.get(position);
        initView(child, data, position);

        mBindedViews.append(position, child);
        container.addView(child, 0);
        return data;
    }

    public void add(T item) {
        mItems.add(item);
    }

    public T remove(int position) {
        return mItems.remove(position);
    }

    public T get(int position) {
        return mItems.get(position);
    }

    public void clear() {
        mItems.clear();
    }

    /**
     * Initiate the view here
     */
    public abstract void initView(View v, T item, int position);
}