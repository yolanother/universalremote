package com.doubtech.universalremote.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class ScalePreviewView extends FrameLayout {

    private int mRenderWidth = -1;
    private int mRenderHeight = -1;
    private float mScale;
    private View mTouchInterceptor;

    public ScalePreviewView(Context context) {
        super(context);
        init();
    }

    public ScalePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScalePreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTouchInterceptor = new View(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        };

        addView(mTouchInterceptor);
    }

    public void setRenderSize(int width, int height) {
        mRenderWidth = width;
        mRenderHeight = height;
        requestLayout();
        mScale = getWidth() / (float) width;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRenderWidth == -1) {
            mRenderWidth = w;
        }
        if (mRenderHeight == -1) {
            mRenderHeight = h;
        }

        float xScale = w / (float) mRenderWidth;
        float yScale = h / (float) mRenderHeight;
        mScale = Math.min(xScale, yScale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mRenderWidth == -1) {
            mRenderWidth = width;
        }
        if (mRenderHeight == -1) {
            mRenderHeight = height;
        }
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(mRenderWidth, mRenderHeight);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(0, 0, mRenderWidth, mRenderHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScale, mScale);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child != mTouchInterceptor) {
            removeView(mTouchInterceptor);
            super.addView(mTouchInterceptor);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
