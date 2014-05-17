package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.json.JsonUtil;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.utils.ProviderUtils;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteDpad extends View implements IRemoteView {
    private static final String FIELD_REPEATING = "repeating";
    private static final String FIELD_OK = "ok";
    private static final String FIELD_RIGHT = "right";
    private static final String FIELD_LEFT = "left";
    private static final String FIELD_DOWN = "down";
    private static final String FIELD_UP = "up";
    public static final String XMLTAG = "dpad";
    public static final int BUTTON_UP = 0;
    public static final int BUTTON_RIGHT = 1;
    public static final int BUTTON_DOWN = 2;
    public static final int BUTTON_LEFT = 3;
    public static final int BUTTON_OK = 4;

    private static final String[] FIELDS = new String[] {
        FIELD_UP,
        FIELD_RIGHT,
        FIELD_DOWN,
        FIELD_LEFT,
        FIELD_OK
    };

    private class DpadButton {
        ButtonFunctionSet functions;
        Drawable drawable;
    }

    private DpadButton[] mButtonFunctionSets = new DpadButton[5];

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
    private boolean mEditMode;
    private Path mPath = new Path();
    private Rect[] mButtonBounds;
    private int mTextColor;

    public RemoteDpad(Context context) {
        super(context);
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width));
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

        mTextColor = Color.WHITE;
    }

    public DpadButton setButton(int buttonId, ButtonFunctionSet button) {
        if (null == button) {
            mButtonFunctionSets[buttonId] = null;
            postInvalidate();
        } else {
            final DpadButton b = new DpadButton();
            mButtonFunctionSets[buttonId] = b;
            b.functions = button;
            TextDrawable td = new TextDrawable(getContext());
            td.setTextColor(mTextColor);
            b.drawable = td;

            if (null != button) {
                ((TextDrawable) b.drawable).setText(button.getLabel());
                b.functions.getIcon(getContext(), new IconLoaderListener() {
                    @Override
                    public void onIconLoaded(Bitmap bitmap) {
                        b.drawable = new BitmapDrawable(getResources(), bitmap);
                        postInvalidate();
                    }
                });
            }
            postInvalidate();
            return b;
        }
        return null;
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
        y = top + divHeight;
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

        mButtonBounds = new Rect[] {
                new Rect(
                    (int) (left + divWidth),
                    (int) (top),
                    (int) (right - divWidth),
                    (int) (top + divHeight)),
                new Rect(
                    (int) (right - divWidth),
                    (int) (top + divHeight),
                    (int) (right),
                    (int) (top + divHeight * 2)),
                new Rect(
                    (int) (left + divWidth),
                    (int) (bottom - divHeight),
                    (int) (right - divWidth),
                    (int) (bottom)),
                new Rect(
                    (int) (left),
                    (int) (top + divHeight),
                    (int) (left + divWidth),
                    (int) (top + divHeight * 2)),
                new Rect(
                    (int) (left + divWidth),
                    (int) (top + divHeight),
                    (int) (right - divWidth),
                    (int) (bottom - divHeight))
            };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();

        if (null != mCurrentPoly) {
            mPath.moveTo(mCurrentPoly.points[0].x, mCurrentPoly.points[0].y);
            for (PointF point : mCurrentPoly.points) {
                mPath.lineTo(point.x, point.y);
            }
            canvas.drawPath(mPath, mTouchedPaint);
        }
        mPath.reset();
        mPath.moveTo(mVisiblePolygons[0].points[0].x, mVisiblePolygons[0].points[0].y);
        for (Poly poly : mVisiblePolygons) {
            for (PointF point : poly.points) {
                mPath.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(mPath, mPathPaint);

        super.onDraw(canvas);

        for (int i = 0; i < mButtonFunctionSets.length; i++) {
            DpadButton button = mButtonFunctionSets[i];
            if (null != button) {
                button.drawable.setBounds(mButtonBounds [i]);
                button.drawable.draw(canvas);
            }
        }
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditMode) return super.onTouchEvent(event);
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
            ProviderUtils.sendButton(getContext(), mButtonFunctionSets[mCurrentTouchIndex].functions);
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

        for (int i = 0; i < mButtonFunctionSets.length; i++) {
            xml.startTag("", FIELDS[i]);
            mButtonFunctionSets[i].functions.writeXml(xml);
            xml.endTag("", FIELDS[i]);
        }
        xml.endTag("", XMLTAG);
    }

    public static RemoteDpad fromXml(Context context, Element item) {
        RemoteDpad dpad = new RemoteDpad(context);
        for (int i = 0; i < FIELDS.length; i++) {
            dpad.setButton(i, ButtonFunctionSet.fromXml(context, FIELDS[i], item));
        }
        if (!item.hasAttribute(FIELD_REPEATING) || !Boolean.parseBoolean(item.getAttribute(FIELD_REPEATING))) {
            dpad.setRepeating(false);
        }
        dpad.setTextColor(Color.WHITE);
        return dpad;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        for (int i = 0; i < FIELDS.length; i++) {
            DpadButton button = mButtonFunctionSets[i];
            if (null != button) {
                JsonUtil.put(object, FIELDS[i], button.functions);
            }
        }
        object.put(FIELD_REPEATING, mRepeating);
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        Context context = getContext();
        boolean warn = false;
        for (int i = 0; i < FIELDS.length; i++) {
            DpadButton button = setButton(i, JsonUtil.getButton(context, object, FIELDS[i]));
            if (null == button && i < BUTTON_OK) {
                warn = true;
            }
        }
        mRepeating = JsonUtil.getBoolean(object, FIELD_REPEATING, false);
        setTextColor(JsonUtil.getInt(object, "textcolor", Color.WHITE));

        if (warn) {
            Log.w("UniversalRemote:JSON", "One or more arrow button was not loaded on a rocker button.\n" + JsonUtil.toDebugString(object));
        }
    }

    public void setTextColor(int color) {
        mTextColor = color;
        for (DpadButton button : mButtonFunctionSets) {
            if (null != button && button.drawable instanceof TextDrawable) {
                ((TextDrawable) button.drawable).setTextColor(color);
            }
        }
    }
}
