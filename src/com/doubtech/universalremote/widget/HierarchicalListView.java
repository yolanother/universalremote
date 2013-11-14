package com.doubtech.universalremote.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class HierarchicalListView extends FrameLayout {
    private List<Adapter> mAdapters;
    private android.widget.AdapterView.OnItemClickListener mItemClickListener;
    private View mInterceptView;
    private int mOffsetWidth;
    private TimeInterpolator mInterpolator;

    public HierarchicalListView(Context context) {
        super(context);
        init();
    }

    public HierarchicalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HierarchicalListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAdapters = new ArrayList<Adapter>();
        mInterceptView = new View(getContext());
        mInterceptView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closeTopAdapter();
            }
        });
        addView(mInterceptView);
        mOffsetWidth = 100;
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @SuppressLint("NewApi")
    private class SelectionAdapter extends BaseAdapter {
        private BaseAdapter mAdapter;
        private int mSelectedPosition;
        private View mSelectedView;

        public SelectionAdapter(BaseAdapter adapter) {
            mAdapter = adapter;
            mSelectedPosition = -1;
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View v = mAdapter.getView(position, convertView, parent);
            boolean selected = mSelectedPosition < 0 || position == mSelectedPosition;
            if (mSelectedPosition < 0) {
                v.animate()
                    .alpha(1)
                    .setDuration(200)
                    .setListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mSelectedView == v) {
                                mSelectedView = null;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    })
                    .start();
            } else {
                v.animate()
                .alpha(selected ? 1 : .5f)
                .setDuration(200)
                .start();
                if (selected) {
                    mSelectedView = v;
                }
            }

            return v;
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
            notifyDataSetChanged();
        }

        public View getSelection() {
            return mSelectedView;
        }

        public void clearSelectedPosition() {
            mSelectedPosition = -1;
            notifyDataSetChanged();
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        ListView listView = new ListView(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
            }
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                Paint p = new Paint();
                p.setColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
                p.setStrokeWidth(4);

                View selected = ((SelectionAdapter)getAdapter()).getSelection();
                if (null != selected) {
                    canvas.drawLine(0, selected.getTop(), canvas.getWidth(), selected.getTop(), p);
                    canvas.drawLine(0, selected.getBottom(), canvas.getWidth(), selected.getBottom(), p);

                    canvas.drawLine(0, selected.getTop(), 0, selected.getBottom(), p);
                } else {
                    canvas.drawLine(0, 0, 0, canvas.getHeight(), p);
                }
            }
        };
        mAdapters.add(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                    long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item instanceof BaseAdapter) {
                    ((SelectionAdapter)adapterView.getAdapter()).setSelectedPosition(position);
                    setAdapter((BaseAdapter) item);
                } else if (null != mItemClickListener) {
                    mItemClickListener.onItemClick(adapterView, view, position, id);
                }
            }
        });

        int width = getMeasuredWidth();
        if (mAdapters.size() > 1) {
            int offset = (getChildCount() - 1) * mOffsetWidth;
            width = getMeasuredWidth() - offset;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, getMeasuredHeight());
            listView.setLayoutParams(params);
            listView.setTranslationX(getWidth());
            listView.animate()
                .setInterpolator(mInterpolator)
                .translationX(mOffsetWidth)
                .setDuration(500)
                .start();
        }

        mInterceptView.bringToFront();
        addView(listView);
        listView.setAdapter(new SelectionAdapter(adapter));

        for (int i = 0; i < getChildCount() - 2; i++) {
            View view = getChildAt(i);
            if (i == 0) {
                view.animate()
                .setInterpolator(mInterpolator)
                .translationXBy(mOffsetWidth-view.getMeasuredWidth())
                .setDuration(500)
                .start();
            } else {
                view.animate()
                    .setInterpolator(mInterpolator)
                    .translationXBy(-view.getMeasuredWidth())
                    .setDuration(500)
                    .start();
            }
        }

    }

    public void closeTopAdapter() {
        if (getChildCount() > 2) {
            final ListView v = (ListView) getChildAt(getChildCount() - 1);
            v.animate()
                .translationX(getWidth())
                .setDuration(500)
                .setListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeView(v);
                        mAdapters.remove(v.getAdapter());
                        ListView listview = (ListView) getChildAt(getChildCount() - 2);
                        listview.bringToFront();
                        ((SelectionAdapter) listview.getAdapter()).clearSelectedPosition();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub

                    }
                })
                .start();
        }
        for (int i = 0; i < getChildCount() - 2; i++) {
            View view = getChildAt(i);
            if (i == 0) {
                view.animate()
                    .setInterpolator(mInterpolator)
                    .translationXBy(view.getMeasuredWidth() - mOffsetWidth)
                    .setDuration(500)
                    .start();
            } else {
                view.animate()
                .setInterpolator(mInterpolator)
                    .translationXBy(view.getMeasuredWidth())
                    .setDuration(500)
                    .start();
            }
        }
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
