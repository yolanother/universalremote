package com.doubtech.universalremote.widget;

import java.io.Serializable;
import java.util.HashMap;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DropGridLayout extends RelativeLayout {
    private PointF mDragShadow;
    protected Object mDragView;
    private Paint mHoloPaint;
    private float mCellWidth;
    private float mCellHeight;
    private RectF mDragBounds;
    private int ROW_COUNT = 11;
    private int COL_COUNT = 9;

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
        mDragBounds = new RectF();
    }

    public int getColumnCount() {
        return COL_COUNT;
    }

    public int getRowCount() {
        return ROW_COUNT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mCellWidth = (width  - getPaddingLeft() - getPaddingRight()) / getColumnCount();
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / getRowCount();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ChildSpec spec = mChildSpecs.get(child);
            child.measure((int) (spec.colspan * mCellWidth), (int) (spec.rowspan * mCellHeight));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ChildSpec spec = mChildSpecs.get(child);

            int left = (int) (spec.col * mCellWidth) + l + getPaddingLeft();
            int top = (int) (spec.row * mCellHeight) + t + getPaddingTop();
            int right = (int) (left + mCellWidth * spec.colspan);
            int bottom = (int) (top + mCellHeight * spec.rowspan);

            child.layout(left, top, right, bottom);
        }
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

    @Override
    public boolean onDragEvent(DragEvent event) {
        switch(event.getAction()) {
        case DragEvent.ACTION_DRAG_ENTERED:
        case DragEvent.ACTION_DRAG_LOCATION:
        case DragEvent.ACTION_DRAG_STARTED:
            updateDragShadow(event.getX(), event.getY());
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            mDragShadow = null;
            invalidate();
            break;
        case DragEvent.ACTION_DROP:
            View view = (View) event.getLocalState();
            Point cell = calculateCell(event.getX(), event.getY());
            mDragShadow = null;
            invalidate();
            ChildSpec spec = getChildSpec(view);
            if(null == spec) {
            	int width = view.getMeasuredWidth();
            	int height = view.getMeasuredHeight();
            	width = (int) Math.max(Math.ceil(width / (float) mCellWidth), 1);
            	height = (int) Math.max(Math.ceil(height / (float) mCellHeight), 1);
            	spec = new ChildSpec(cell.y, cell.x, width, height);
            } else {
	            spec.row = cell.y;
	            spec.col = cell.x;
            }
            addView(view, spec);
            break;
        }
        if (event.getLocalState() instanceof View) {
            mDragView = event.getLocalState();
            return true;
        }
        return false;
    }

    private Point calculateCell(float x, float y) {
        Point point = new Point();
        point.x = (int) (x / getWidth() * getColumnCount());
        point.y = (int) (y / getHeight() * getRowCount());
        return point;
    }

    private void calculateCellCoords(float x, float y, RectF cellBounds) {
        cellBounds.left = (int) (x / getWidth() * getColumnCount()) * mCellWidth + getPaddingLeft();
        cellBounds.top = (int) (y / getHeight() * getRowCount()) * mCellHeight + getPaddingTop();
        cellBounds.bottom = cellBounds.top + mCellHeight;
        cellBounds.right = cellBounds.left + mCellWidth;
    }

    private void updateDragShadow(float x, float y) {
        mDragShadow = new PointF(x, y);
        calculateCellCoords(x, y, mDragBounds);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (null != mDragShadow) {
            canvas.drawRect(mDragBounds, mHoloPaint);
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        ChildSpec spec = mChildSpecs.remove(view);
        for(int row = spec.row; row < spec.row + spec.rowspan; row++) {
        	for(int col = spec.col; col <= spec.col + spec.colspan; col++) {
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
    }

    private HashMap<View, ChildSpec> mChildSpecs = new HashMap<View, DropGridLayout.ChildSpec>();
    private SparseArray<SparseArray<Boolean>> mOccupiedCells = new SparseArray<SparseArray<Boolean>>();

    public void addView(View child, ChildSpec spec) {
        
        for(int row = spec.row; row < spec.row + spec.rowspan; row++) {
        	for(int col = spec.col; col <= spec.col + spec.colspan; col++) {
        		if(isOccupied(row, col)) {
        			ChildSpec ns = new ChildSpec(spec.row + 1, spec.col, spec.rowspan, spec.colspan);
        			addView(child, ns);
        			return;
        		}
        	}
        }

        for(int row = spec.row; row < spec.row + spec.rowspan; row++) {
        	for(int col = spec.col; col <= spec.col + spec.colspan; col++) {
        		setOccupied(row, col, true);        		
        	}
        }
        

        super.addView(child);
        mChildSpecs.put(child, spec);
        //updateLayoutParams(child);
    }

    private void setOccupied(int row, int col, boolean occupied) {
    	SparseArray<Boolean> cols = mOccupiedCells.get(row);
    	if(null == cols) {
    		cols = new SparseArray<Boolean>();
    		mOccupiedCells.put(row, cols);
    	}
    	cols.put(col, occupied);
	}

	private boolean isOccupied(int row, int col) {
    	SparseArray<Boolean> cols = mOccupiedCells.get(row);
    	if(null == cols) {
    		return false;
    	}
    	Boolean occupied = cols.get(col);
		return null != occupied && occupied;
	}

	public ChildSpec getChildSpec(View v) {
        return mChildSpecs.get(v);
    }
}
