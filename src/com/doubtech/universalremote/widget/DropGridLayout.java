package com.doubtech.universalremote.widget;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.xmlpull.v1.XmlSerializer;

import com.doubtech.universalremote.R;

import android.R.color;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.RelativeLayout;

public class DropGridLayout<T> extends RelativeLayout {
    private PointF mDragShadow;
    protected Object mDragView;
    private Paint mHoloPaint;
    private float mCellWidth;
    private float mCellHeight;
    private RectF mDragBounds;
    private int ROW_COUNT = 11;
    private int COL_COUNT = 9;
    private int mCellSpaceLeft;
    private int mCellSpaceTop;
    private int mCellSpaceRight;
    private int mCellSpaceBottom;
    private Class<?> mDropFilter;
    private boolean mDragEnabled;
    private RectF mLayoutRect = new RectF();
    private ChildSpec mDragSpec;

    public DropGridLayout(Context context) {
        super(context);
        init();
    }

    public DropGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHoloPaint = new Paint();
        mHoloPaint.setColor(getResources().getColor(color.holo_blue_dark));
        mHoloPaint.setAlpha(128);
        mHoloPaint.setStyle(Style.FILL_AND_STROKE);
    }

    public int getColumnCount() {
        return COL_COUNT;
    }

    public int getRowCount() {
        return ROW_COUNT;
    }

    public void setColumnCount(int colCount) {
        COL_COUNT = colCount;
        requestLayout();
    }

    public void setRowCount(int rowCount) {
        ROW_COUNT = rowCount;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        mCellWidth = (width  - getPaddingLeft() - getPaddingRight()) / getColumnCount();
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / getRowCount();

        if (height == 0 || shouldForceSquare()) {
            mCellHeight = mCellWidth;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ChildSpec spec = mChildSpecs.get(child);
            child.measure((int) (spec.colspan * mCellWidth), (int) (spec.rowspan * mCellHeight));
        }

        if (MeasureSpec.EXACTLY != MeasureSpec.getMode(heightMeasureSpec) || height == 0) {
            height = (int) (mOccupiedCells.size() * mCellHeight + getPaddingBottom() + mCellHeight);
        }
        setMeasuredDimension(width, height);
    }

    public boolean shouldForceSquare() {
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ChildSpec spec = mChildSpecs.get(child);

            layout(spec, mLayoutRect);
            child.layout((int) mLayoutRect.left, (int) mLayoutRect.top,
                    (int) mLayoutRect.right, (int) mLayoutRect.bottom);
        }
    }

    private void layout(ChildSpec spec, RectF layout) {
        layout.left = (int) (spec.col * mCellWidth) + getPaddingLeft();
        layout.top = (int) (spec.row * mCellHeight) + getPaddingTop();
        layout.right = (int) (layout.left + mCellWidth * spec.colspan);
        layout.bottom = (int) (layout.top + mCellHeight * spec.rowspan);

        layout.left += mCellSpaceLeft;
        layout.top += mCellSpaceTop;
        layout.right -= mCellSpaceRight;
        layout.bottom -= mCellSpaceBottom;
    }

    public void setCellSpacing(int spacing) {
        mCellSpaceLeft = spacing;
        mCellSpaceTop = spacing;
        mCellSpaceRight = spacing;
        mCellSpaceBottom = spacing;
    }

    /*@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCellWidth = (w - getPaddingLeft() - getPaddingRight()) / (float) getColumnCount();
        mCellHeight = (h - getPaddingTop() - getPaddingBottom()) / (float) getRowCount();

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            updateLayoutParams(view);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void updateLayoutParams(View view) {
        ChildSpec spec = mChildSpecs.get(view);
        LayoutParams params = new LayoutParams((int) (mCellWidth * spec.colspan),
                (int) (mCellHeight * spec.rowspan));
        params.leftMargin = (int) (spec.col * mCellWidth);
        params.topMargin = (int) (spec.row * mCellHeight);
        view.setLayoutParams(params);
    }*/

    public void setDropFilter(Class<?> filter) {
        mDropFilter = filter;
    }

    public void setDragEnabled(boolean dragEnabled) {
        mDragEnabled = dragEnabled;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        if (!mDragEnabled || null == event.getLocalState()) {
            return false;
        }
        switch(event.getAction()) {
        case DragEvent.ACTION_DRAG_ENTERED:
        case DragEvent.ACTION_DRAG_LOCATION:
        case DragEvent.ACTION_DRAG_STARTED:
            updateDragShadow(event.getX(), event.getY());
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            mDragShadow = null;
            mDragSpec = null;
            mDragBounds = null;
            invalidate();
            break;
        case DragEvent.ACTION_DROP:
            View view = (View) event.getLocalState();
            if (null == view.getParent() || view.getParent() == this) {
                mDragShadow = null;
                invalidate();
                addView(view, mDragSpec);
            }
            break;
        }
        if (event.getLocalState() instanceof View) {
            mDragView = event.getLocalState();
            return true;
        }
        return false;
    }

    public void startDrag(View v) {
        ClipData dragData = ClipData.newPlainText("button", toString());
        View.DragShadowBuilder shadow = new DragShadowBuilder(v);
        v.startDrag(dragData, shadow, v, 0);
        mDragSpec = mChildSpecs.get(v);
        if (null == mDragSpec) {
            mDragSpec = new ChildSpec(0, 0);
        }
        ((ViewGroup)v.getParent()).removeView(v);
    }

    private void updateDragShadow(float x, float y) {
        if (null != mDragSpec) {
            if (null == mDragBounds) {
                mDragBounds = new RectF();
            }
            mDragSpec.row = (int) (y / mCellHeight);
            mDragSpec.col = (int) (x / mCellWidth);
            layout(mDragSpec, mDragBounds);
        }
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (null != mDragBounds) {
            canvas.drawRect(mDragBounds, mHoloPaint);
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        ChildSpec spec = mChildSpecs.remove(view);
        for (int row = spec.row; row < spec.row + spec.rowspan; row++) {
            for (int col = spec.col; col <= spec.col + spec.colspan; col++) {
                setOccupied(row, col, false);
            }
        }
    }

    @Override
    public void removeViewAt(int index) {
        mChildSpecs.remove(getChildAt(index));
        super.removeViewAt(index);
    }

    @Override
    public void removeAllViews() {
        mChildSpecs.clear();
        mOccupiedCells.clear();
        super.removeAllViews();
    }

    public static class ChildSpec implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -5572258551291855416L;
        int row;
        int col;
        int rowspan;
        int colspan;

        public ChildSpec(int row, int col, int rowspan, int colspan) {
            this.row = row;
            this.col = col;
            this.rowspan = rowspan;
            this.colspan = colspan;
        }

        public ChildSpec(int row, int col) {
            this.row = row;
            this.col = col;
            this.rowspan = 1;
            this.colspan = 1;
        }

        public int getRow() {
            return row;
        }
        public int getCol() {
            return col;
        }
        public int getColSpan() {
            return colspan;
        }
        public int getRowSpan() {
            return rowspan;
        }

        public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
            xml.attribute("", "row", Integer.toString(getRow()));
            xml.attribute("", "col", Integer.toString(getCol()));
            xml.attribute("", "rowspan", Integer.toString(getRowSpan()));
            xml.attribute("", "colspan", Integer.toString(getColSpan()));
        }
    }

    private HashMap<View, ChildSpec> mChildSpecs = new HashMap<View, DropGridLayout.ChildSpec>();
    private SparseArray<SparseArray<Boolean>> mOccupiedCells = new SparseArray<SparseArray<Boolean>>();

    public void addView(View child, ChildSpec spec) {
        int padding = getResources().getDimensionPixelSize(R.dimen.stroke_width);
        child.setPadding(padding, padding, padding, padding);
        if (spec.col > getColumnCount()) {
            ChildSpec ns = new ChildSpec(spec.row, getColumnCount() - 1, spec.rowspan, spec.colspan);
            addView(child, ns);
            return;
        }
        for (int row = spec.row; row < spec.row + spec.rowspan; row++) {
            for (int col = spec.col; col < spec.col + spec.colspan; col++) {
                if (isOccupied(row, col)) {
                    ChildSpec ns = new ChildSpec(spec.row + 1, spec.col, spec.rowspan, spec.colspan);
                    addView(child, ns);
                    return;
                }
            }
        }

        for (int row = spec.row; row < spec.row + spec.rowspan; row++) {
            for (int col = spec.col; col < spec.col + spec.colspan; col++) {
                setOccupied(row, col, true);
            }
        }

        actuallyAddView(child, spec);
    }

    private void actuallyAddView(View child, ChildSpec spec) {
        super.addView(child);
        mChildSpecs.put(child, spec);
    }

    public int getMaxRow() {
        // TODO Fix for when cells are removed
        return mOccupiedCells.size();
    }

    private void setOccupied(int row, int col, boolean occupied) {
        SparseArray<Boolean> cols = mOccupiedCells.get(row);
        if (null == cols) {
            cols = new SparseArray<Boolean>();
            mOccupiedCells.put(row, cols);
        }
        cols.put(col, occupied);
    }

    public boolean isOccupied(int row, int col) {
        SparseArray<Boolean> cols = mOccupiedCells.get(row);
        if (null == cols) {
            return false;
        }
        Boolean occupied = cols.get(col);
        return null != occupied && occupied;
    }

    public ChildSpec getChildSpec(View v) {
        return mChildSpecs.get(v);
    }
}
