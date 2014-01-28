package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.utils.ProviderUtils;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteDpad extends View implements IRemoteView {
    public static final String XMLTAG = "dpad";
    private final int BUTTON_UP = 0;
    private final int BUTTON_RIGHT = 1;
    private final int BUTTON_DOWN = 2;
    private final int BUTTON_LEFT = 3;
    private final int BUTTON_OK = 4;

    private ButtonFunctionSet mUpButton;
    private ButtonFunctionSet mDownButton;
    private ButtonFunctionSet mRightButton;
    private ButtonFunctionSet mLeftButton;
    private ButtonFunctionSet mButtonOk;

    private ButtonFunctionSet[] mButtonFunctionSets = new ButtonFunctionSet[5];

    private Drawable mUpDrawable;
    private Drawable mDownDrawable;
    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private Drawable mOkDrawable;

    private class Poly {
        PointF[] points = new PointF[4];

        Poly(int pointCount) {
            points = new PointF[pointCount];
            for (int i = 0; i < points.length; i++) {
                points[i] = new PointF(0, 0);
            }
        }

        public void setPoint(int index, float x, float y) {
            points[index].x = x;
            points[index].y = y;
        }

        /**
         * Return true if the given point is contained inside the boundary.
         * @param test The point to check
         * @return true if the point is inside the boundary, false otherwise
         *
         */
        public boolean contains(PointF test) {
          int i;
          int j;
          boolean result = false;
          for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x)) {
              result = !result;
             }
          }
          return result;
        }
    }

    private Poly mUpRect = new Poly(4);
    private Poly mDownRect = new Poly(4);
    private Poly mRightRect = new Poly(4);
    private Poly mLeftRect = new Poly(4);
    private Poly mOkRect = new Poly(4);

    private Poly[] mVisiblePolygons = new Poly[] {
            mUpRect,
            mRightRect,
            mDownRect,
            mLeftRect
    };

    private Poly[] mTouchAreas = new Poly[] {
            mUpRect,
            mRightRect,
            mDownRect,
            mLeftRect,
            mOkRect
    };

    private Paint mPathPaint;
    private boolean mRepeating;
    private int right;
    private int left;
    private int top;
    private int bottom;
    private float divWidth;
    private float spacingDivWidth;
    private float subDivWidth;
    private float divHeight;
    private float spacingDivHeight;
    private float subDivHeight;
    private Poly mCurrentPoly;
    private int mCurrentTouchIndex;
    private Paint mTouchedPaint;

    public RemoteDpad(Context context) {
        super(context);
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setStrokeCap(Cap.ROUND);
        mPathPaint.setStrokeJoin(Join.ROUND);
        mPathPaint.setColor(getResources().getColor(color.holo_blue_dark));
        mPathPaint.setStyle(Style.STROKE);
        mPathPaint.setAntiAlias(true);

        mTouchedPaint = new Paint();
        mTouchedPaint.setColor(getResources().getColor(color.holo_blue_dark));
        mTouchedPaint.setStrokeCap(Cap.ROUND);
        mTouchedPaint.setStrokeJoin(Join.ROUND);
        mTouchedPaint.setAlpha(190);
    }

    public void setUpButton(ButtonFunctionSet button) {
        mUpButton = button;
        mButtonFunctionSets[BUTTON_UP] = mUpButton;
        mUpDrawable = new TextDrawable(getContext());
        if (null != button) {
            ((TextDrawable) mUpDrawable).setText(button.getLabel());
            mUpButton.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mUpDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }

    public void setDownButton(ButtonFunctionSet button) {
        mDownButton = button;
        mButtonFunctionSets[BUTTON_DOWN] = mDownButton;
        mDownDrawable = new TextDrawable(getContext());
        if (null != button) {
            ((TextDrawable) mDownDrawable).setText(button.getLabel());
            mDownButton.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mDownDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }

    public void setRightButton(ButtonFunctionSet button) {
        mRightButton = button;
        mButtonFunctionSets[BUTTON_RIGHT] = mRightButton;
        mRightDrawable = new TextDrawable(getContext());
        if (null != button) {
            ((TextDrawable) mRightDrawable).setText(button.getLabel());
            mRightButton.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mRightDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }

    public void setLeftButton(ButtonFunctionSet button) {
        mLeftButton = button;
        mButtonFunctionSets[BUTTON_LEFT] = mLeftButton;
        mLeftDrawable = new TextDrawable(getContext());
        if (null != button) {
            ((TextDrawable) mLeftDrawable).setText(button.getLabel());
            mLeftButton.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mLeftDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }

    public void setOkButton(ButtonFunctionSet button) {
        mButtonOk = button;
        mButtonFunctionSets[BUTTON_OK] = mButtonOk;
        mOkDrawable = new TextDrawable(getContext());
        if (null != button) {
            ((TextDrawable) mOkDrawable).setText(button.getLabel());
            mButtonOk.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mOkDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        // TODO This should probably be moved to onMeasure
        right = w - getPaddingRight();
        left = getPaddingLeft();
        top = getPaddingTop();
        bottom = h - getPaddingBottom();

        divWidth = (right - left) / 3.0f;
        spacingDivWidth = divWidth / 4.0f;
        subDivWidth = divWidth - spacingDivWidth * 2;

        divHeight = (bottom - top) / 3.0f;
        spacingDivHeight = divHeight / 4.0f;
        subDivHeight = divHeight - spacingDivHeight * 2;

        // TODO This should probably be moved to onLayout
        float x = left + divWidth;
        float y = top + divHeight;
        mUpRect.setPoint(0, x, y);
        x = left + divWidth + spacingDivWidth;
        y = top;
        mUpRect.setPoint(1, x, y);
        x += subDivWidth;
        mUpRect.setPoint(2, x, y);
        x = right - divWidth;
        y = top + divWidth;
        mUpRect.setPoint(3, x, y);
        mRightRect.setPoint(0, x, y);
        x = right;
        y += spacingDivHeight;
        mRightRect.setPoint(1, x, y);
        y += subDivHeight;
        mRightRect.setPoint(2, x, y);
        x = right - divWidth;
        y = bottom - divHeight;
        mRightRect.setPoint(3, x, y);
        mDownRect.setPoint(0, x, y);
        x -= spacingDivWidth;
        y = bottom;
        mDownRect.setPoint(1, x, y);
        x -= subDivWidth;
        mDownRect.setPoint(2, x, y);
        x -= spacingDivWidth;
        y = bottom - divHeight;
        mDownRect.setPoint(3, x, y);
        mLeftRect.setPoint(0, x, y);
        x = left;
        y -= spacingDivHeight;
        mLeftRect.setPoint(1, x, y);
        y -= subDivHeight;
        mLeftRect.setPoint(2, x, y);
        y = top + divHeight;
        x = left + divWidth;
        mLeftRect.setPoint(3, x, y);

        int i = 0;
        for (Poly poly : mVisiblePolygons) {
            PointF point = poly.points[0];
            mOkRect.setPoint(i++, point.x, point.y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();

        if (null != mCurrentPoly) {
            path.moveTo(mCurrentPoly.points[0].x, mCurrentPoly.points[0].y);
            for (PointF point : mCurrentPoly.points) {
                path.lineTo(point.x, point.y);
            }
            canvas.drawPath(path, mTouchedPaint);
        }
        path.reset();
        path.moveTo(mVisiblePolygons[0].points[0].x, mVisiblePolygons[0].points[0].y);
        for (Poly poly : mVisiblePolygons) {
            for (PointF point : poly.points) {
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, mPathPaint);

        super.onDraw(canvas);

        if (null != mUpDrawable) {
            mUpDrawable.setBounds(
                    (int) (left + divWidth),
                    (int) (top),
                    (int) (right - divWidth),
                    (int) (top + divHeight));
            mUpDrawable.draw(canvas);
        }

        if (null != mDownDrawable) {
            mDownDrawable.setBounds(
                    (int) (left + divWidth),
                    (int) (bottom - divHeight),
                    (int) (right - divWidth),
                    (int) (bottom));
            mDownDrawable.draw(canvas);
        }

        if (null != mLeftDrawable) {
            mLeftDrawable.setBounds(
                    (int) (left),
                    (int) (top + divHeight),
                    (int) (left + divWidth),
                    (int) (top + divHeight * 2));
            mLeftDrawable.draw(canvas);
        }

        if (null != mRightDrawable) {
            mRightDrawable.setBounds(
                    (int) (right - divWidth),
                    (int) (top + divHeight),
                    (int) (right),
                    (int) (top + divHeight * 2));
            mRightDrawable.draw(canvas);
        }

        if (null != mOkDrawable) {
            mOkDrawable.setBounds(
                    (int) (left + divWidth),
                    (int) (top + divHeight),
                    (int) (right - divWidth),
                    (int) (bottom - divHeight));
            mOkDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        mCurrentPoly = null;
        for (int i = 0; i < mTouchAreas.length; i++) {
            Poly poly = mTouchAreas[i];
            if (poly.contains(point)) {
                mCurrentPoly = poly;
                mCurrentTouchIndex = i;
                break;
            }
        }

        switch(event.getAction()) {
        case MotionEvent.ACTION_UP:
            ProviderUtils.sendButton(getContext(), mButtonFunctionSets[mCurrentTouchIndex]);
        case MotionEvent.ACTION_CANCEL:
            mCurrentPoly = null;
            break;
        }
        invalidate();
        return true;
    }

    public boolean isRepeating() {
        return mRepeating;
    }

    public void setRepeating(boolean shouldRepeat) {
        mRepeating = shouldRepeat;
    }

    @Override
    public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        spec.writeXml(xml);
        if (null != mUpButton) {
            xml.startTag("", "up");
            mUpButton.writeXml(xml);
            xml.endTag("", "up");
        }
        if (null != mDownButton) {
            xml.startTag("", "down");
            mDownButton.writeXml(xml);
            xml.endTag("", "down");
        }
        if (null != mLeftButton) {
            xml.startTag("", "left");
            mLeftButton.writeXml(xml);
            xml.endTag("", "left");
        }
        if (null != mRightButton) {
            xml.startTag("", "right");
            mRightButton.writeXml(xml);
            xml.endTag("", "right");
        }
        if (null != mButtonOk) {
            xml.startTag("", "ok");
            mButtonOk.writeXml(xml);
            xml.endTag("", "ok");
        }
        xml.endTag("", XMLTAG);
    }

    public static RemoteDpad fromXml(Context context, Element item) {
        RemoteDpad dpad = new RemoteDpad(context);
        dpad.setUpButton(ButtonFunctionSet.fromXml(context, "up", item));
        dpad.setDownButton(ButtonFunctionSet.fromXml(context, "down", item));
        dpad.setLeftButton(ButtonFunctionSet.fromXml(context, "left", item));
        dpad.setRightButton(ButtonFunctionSet.fromXml(context, "right", item));
        dpad.setOkButton(ButtonFunctionSet.fromXml(context, "ok", item));
        if (!item.hasAttribute("repeating") || !Boolean.parseBoolean(item.getAttribute("repeating"))) {
            dpad.setRepeating(false);
        }
        return dpad;
    }
}
