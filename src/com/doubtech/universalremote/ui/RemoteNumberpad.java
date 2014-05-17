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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.json.JsonUtil;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteNumberpad extends View implements IRemoteView {
    public static final String XMLTAG = "number-pad";
    private ButtonFunctionSet[] mButtons = new ButtonFunctionSet[10];
    private Drawable[] mButtonDrawables = new Drawable[10];
    private Rect[] mButtonRects = new Rect[10];
    private int right;
    private int left;
    private int top;
    private int bottom;
    private float divWidth;
    private float divHeight;
    private Rect mTouchedRect;
    private int mTouchedIndex;
    private GradientDrawable mGradientDrawable;
    private Paint mTouchedPaint;
    private boolean mEditMode;

    public RemoteNumberpad(Context context) {
        super(context);
        setBackgroundResource(R.drawable.rounded_button);

        mTouchedPaint = new Paint();
        mTouchedPaint.setColor(getResources().getColor(color.holo_blue_dark));
        mTouchedPaint.setAlpha(190);
        mTouchedPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width));
    }

    public void setButtonFunction(final int idx, ButtonFunctionSet button) {
        if (idx < mButtons.length) {
            mButtons[idx] = button;
            mButtonDrawables[idx] = new TextDrawable(getContext());
            ((TextDrawable)mButtonDrawables[idx]).setText(Integer.toString(idx));
            button.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    if (null != bitmap) {
                        mButtonDrawables[idx] = new BitmapDrawable(getResources(), bitmap);
                    }
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        left = getPaddingLeft();
        right = w - getPaddingRight();
        top = getPaddingTop();
        bottom = h - getPaddingBottom();


        divWidth = (right - left) / 3.0f;
        divHeight = (bottom - top) / 4.0f;

        int row = 3;
        int col = 1;

        for (int i = 0; i < mButtonDrawables.length; i++) {
            Drawable d = mButtonDrawables[i];
            if (null != d) {
                mButtonRects[i] = new Rect(
                        (int) (left + divWidth * col),
                        (int) (top + divHeight * row),
                        (int) (left + divWidth * col + divWidth),
                        (int) (top + divHeight * row + divHeight));
                d.setBounds(mButtonRects[i]);
            }
            col++;
            if (col > 2 || row == 3) {
                col = 0;
                row--;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != mTouchedRect) {
            canvas.drawRect(mTouchedRect, mTouchedPaint);
        }

        for (Drawable d : mButtonDrawables) {
            if (null != d) {
                d.draw(canvas);
            }
        }
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditMode) return super.onTouchEvent(event);
        for (int i = 0; i < mButtonDrawables.length; i++) {
            if (null != mButtonDrawables[i] &&
                    mButtonRects[i].contains((int) event.getX(), (int) event.getY())) {
                mTouchedRect = mButtonRects[i];
                mTouchedIndex = i;
                break;
            }
        }
        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            break;
        case MotionEvent.ACTION_UP:
            mButtons[mTouchedIndex].send(getContext());
        case MotionEvent.ACTION_CANCEL:
            mTouchedRect = null;
        }
        invalidate();
        return true;
    }

    public void setTextColor(int color) {
        for (Drawable d : mButtonDrawables) {
            if (d instanceof TextDrawable) {
                ((TextDrawable)d).setTextColor(color);
            }
        }
    }

    @Override
    public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        spec.writeXml(xml);

        for (int i = 0; i < mButtons.length; i++) {
            if (null != mButtons[i]) {
                xml.startTag("", "num" + Integer.toString(i));
                mButtons[i].writeXml(xml);
                xml.endTag("", "num" + Integer.toString(i));
            }
        }
        xml.endTag("", XMLTAG);
    }

    public static RemoteNumberpad fromXml(Context context, Element item) {
        boolean warn = false;
        RemoteNumberpad numberPad = new RemoteNumberpad(context);
        for (int i = 0; i < numberPad.mButtons.length; i++) {
            ButtonFunctionSet button = ButtonFunctionSet.fromXml(context, "num" + Integer.toString(i), item);
            numberPad.setButtonFunction(i, button);
            if (null == button) warn = true;
        }
        if (warn) {
            Log.w("UniversalRemote:XML", "A button in the numberpad was not loaded.\n" + item.toString());
        }
        // TODO add xml attribute for setting color
        numberPad.setTextColor(Color.WHITE);
        return numberPad;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        for (int i = 0; i < mButtons.length; i++) {
            JsonUtil.put(object, "num" + i, mButtons[i]);
        }
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        boolean warn = false;
        for (int i = 0; i < mButtons.length; i++) {
            setButtonFunction(i, JsonUtil.getButton(getContext(), object, "num" + i));
            if (null == mButtons[i]) warn = true;
        }
        setTextColor(JsonUtil.getInt(object, "textcolor", Color.WHITE));

        if (warn) {
            Log.w("UniversalRemote:JSON", "A button in the numberpad was not loaded.\n" + JsonUtil.toDebugString(object));
        }
    }
}
