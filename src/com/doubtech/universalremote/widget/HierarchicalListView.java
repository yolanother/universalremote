package com.doubtech.universalremote.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.doubtech.universalremote.R;

public class HierarchicalListView extends FrameLayout {
    public static interface OnItemClickListener {
        boolean onItemClick(AdapterView<?> adapterView, View view, int position,
                long id);
    }

    public static interface OnHierarchyChangedListener {
        void onHierarchyChangedListener(int level);
    }

    private List<Adapter> mAdapters;
    private OnItemClickListener mItemClickListener;
    private View mInterceptView;
    private int mOffsetWidth;
    private TimeInterpolator mInterpolator;
    private OnItemLongClickListener mItemLongClickListener;
    private OnHierarchyChangedListener mHierarchyChangedListener;
    private boolean mClosing;
    private boolean mChangingHierarchy;

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
                if (mChangingHierarchy) return;
                closeTopView();
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
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    notifyDataSetChanged();
                }
            });
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

    private void drawHighlights(Canvas canvas, View view) {
        Paint p = new Paint();
        p.setColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
        p.setStrokeWidth(4);

        if (view instanceof ListView) {
            SelectionAdapter adapter = ((InternalListView) view).getSelectionAdapter();
            View selected = null != adapter ? adapter.getSelection() : null;
            if (null != selected) {
                canvas.drawLine(0, selected.getTop(), canvas.getWidth(), selected.getTop(), p);
                canvas.drawLine(0, selected.getBottom(), canvas.getWidth(), selected.getBottom(), p);

                canvas.drawLine(0, selected.getTop(), 0, selected.getBottom(), p);
            } else {
                canvas.drawLine(0, 0, 0, canvas.getHeight(), p);
            }
        } else {
            canvas.drawLine(0, 0, 0, canvas.getHeight(), p);
        }
    }

    public void addHierarchyView(View view) {
        final FrameLayout layout = new FrameLayout(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                drawHighlights(canvas, this);
                super.onDraw(canvas);
            }
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                drawHighlights(canvas, this);
            }
            @Override
            public boolean onTouchEvent(MotionEvent event) {

                return true;
            }
        };
        layout.setTag(view);
        layout.addView(view);

        completeAddHierarchyView(layout);
    }

    private class InternalListView extends ListView {
         public InternalListView(Context context) {
            super(context);
        }
        @Override
         protected void onDraw(Canvas canvas) {
             super.onDraw(canvas);
         }
         @Override
         protected void dispatchDraw(Canvas canvas) {
             super.dispatchDraw(canvas);
             drawHighlights(canvas, this);
         }
         @Override
         public ListAdapter getAdapter() {
             ListAdapter adapter = super.getAdapter();
             if (null != adapter) {
                 adapter = ((SelectionAdapter)adapter).mAdapter;
             }
             return adapter;
         }

         public SelectionAdapter getSelectionAdapter() {
             return (SelectionAdapter) super.getAdapter();
         }
    }

    public void addAdapter(final BaseAdapter adapter) {
        mChangingHierarchy = true;
        final InternalListView listView = new InternalListView(getContext());
        listView.setTag(adapter);
        mAdapters.add(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                    long id) {
                if (mChangingHierarchy) return;
                Object item = adapterView.getItemAtPosition(position);
                if (null != mItemClickListener && mItemClickListener.onItemClick(
                        listView, view, position, id)) {
                    return;
                }
                if (item instanceof BaseAdapter) {
                    ((InternalListView) adapterView).getSelectionAdapter().setSelectedPosition(position);
                    addAdapter((BaseAdapter) item);
                } else if (item instanceof View) {
                    addHierarchyView((View) item);
                }
            }
        });
        listView.setAdapter(new SelectionAdapter(new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (null == convertView) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.loading, null);
                }
                return convertView;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 1;
            }

            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return 1;
            }

            @Override
            public int getCount() {
                return 1;
            }
        }));
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if (null != mItemLongClickListener) {
                    return mItemLongClickListener.onItemLongClick(arg0, arg1, arg2, arg3);
                }
                return false;
            }
        });

        if (!(adapter instanceof CursorAdapter) || null != ((CursorAdapter)adapter).getCursor()) {
            listView.setAdapter(new SelectionAdapter(adapter));
        }
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setAdapter(new SelectionAdapter(adapter));
            }
        });
        completeAddHierarchyView(listView);
    }

    private void completeAddHierarchyView(View view) {
        mChangingHierarchy = true;
        int width = getMeasuredWidth();
        if (getChildCount() >= 2) {
            int offset = (getChildCount() - 1) * mOffsetWidth;
            width = getMeasuredWidth() - mOffsetWidth;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, getMeasuredHeight());
            view.setLayoutParams(params);
            view.setTranslationX(getWidth());
            view.animate()
                .setInterpolator(mInterpolator)
                .translationX(mOffsetWidth)
                .setDuration(500)
                .start();
        }

        mInterceptView.bringToFront();
        addView(view);

        for (int i = 0; i < getChildCount() - 2; i++) {
            View childView = getChildAt(i);
            if (i == 0) {
                childView.animate()
                .setInterpolator(mInterpolator)
                .translationXBy(mOffsetWidth-childView.getMeasuredWidth())
                .setDuration(400)
                .start();
            } else {
                childView.animate()
                    .setInterpolator(mInterpolator)
                    .translationXBy(-childView.getMeasuredWidth())
                    .setDuration(400)
                    .start();
            }
        }

        // Allow all animations to complete ten reenable item selection on
        // hierarchy.
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mChangingHierarchy = false;
            }
        }, 500);

        if (null != mHierarchyChangedListener) {
            mHierarchyChangedListener.onHierarchyChangedListener(getChildCount() - 1);
        }
    }

    public boolean closeTopView() {
        if (mClosing) return false;
        boolean closed = false;
        if (getChildCount() > 2) {
            mClosing = true;
            final View v = getChildAt(getChildCount() - 1);
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
                        if (null != mHierarchyChangedListener) {
                            mHierarchyChangedListener.onHierarchyChangedListener(getChildCount() - 2);
                        }
                        removeView(v);
                        if (v instanceof ListView) {
                            mAdapters.remove(((ListView)v).getAdapter());
                        }
                        InternalListView listview = (InternalListView) getChildAt(getChildCount() - 2);
                        listview.bringToFront();
                        listview.getSelectionAdapter().clearSelectedPosition();
                        mClosing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mClosing = false;
                    }
                })
                .start();
            closed = true;
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
        return closed;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public void setSelectedPosition(int position) {
        View v = getChildAt(getChildCount() - 1);
        if (v instanceof InternalListView) {
            ((InternalListView) v).getSelectionAdapter().setSelectedPosition(position);
        }
    }

    public void setHierarchyLevelChangedListener(OnHierarchyChangedListener listener) {
        mHierarchyChangedListener =  listener;
    }

    public Adapter getCurrentAdapter() {
        View v = getChildAt(getChildCount() - 1);
        if (v instanceof InternalListView) {
            return (Adapter) v.getTag();
        }
        return null;
    }

    public View getCurrentView() {
        View v = getChildAt(getChildCount() - 1);
        if (v instanceof FrameLayout) {
            return (View) v.getTag();
        }
        return null;
    }
}
