package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.json.JsonUtil;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteRocker extends View implements IRemoteView {
    private static final String FIELD_REPEATING = "repeating";
    private static final String FIELD_DOWN = "down";
    private static final String FIELD_UP = "up";
    public static final String XMLTAG = "rocker";
    private ButtonFunctionSet mTopButton;
    private ButtonFunctionSet mBottomButton;
    private Drawable mTopDrawable;
    private Drawable mBottomDrawable;
    private TextDrawable mLabel;
    private int mButtonSize;
    private boolean top;
    private boolean bottom;
    private GradientDrawable mUpGradient;
    private GradientDrawable mDownGradient;
    private boolean mRepeating;
    private int mAlpha;
    private boolean mEditMode;

    public RemoteRocker(Context context) {
        super(context);
        init();
    }

    public RemoteRocker(Context context, ButtonFunction topButton,
            ButtonFunction bottomButton, boolean repeating) {
        super(context);
        init();
        setTopButton(topButton);
        setBottomButton(bottomButton);
        mRepeating = repeating;
    }

    private void init() {
        setBackgroundResource(R.drawable.rounded_button);

        mRepeating = true;

        int[] gradientColors = new int[] {
                Color.TRANSPARENT,
                getResources().getColor(color.holo_blue_bright) };
        mUpGradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                gradientColors);
        mDownGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors);
    }

    private void setBottomButton(ButtonFunctionSet bottomButton) {
        if (null == bottomButton) {
            mBottomButton = null;
            mBottomDrawable = null;
            postInvalidate();
        } else {
            mBottomButton = bottomButton;
            mBottomDrawable = new TextDrawable(getContext());
            ((TextDrawable)mBottomDrawable).setText(bottomButton.getLabel());

            checkLabelOverride();

            bottomButton.getIcon(getContext(), new IconLoaderListener() {

                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mBottomDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
    }

    private void setTopButton(ButtonFunctionSet topButton) {
        if (null == topButton) {
            mTopButton = null;
            mTopDrawable = null;
            postInvalidate();
        } else {
            mTopButton = topButton;
            mTopDrawable = new TextDrawable(getContext());
            ((TextDrawable)mTopDrawable).setText(topButton.getLabel());

            checkLabelOverride();

            topButton.getIcon(getContext(), new IconLoaderListener() {

                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mTopDrawable = new BitmapDrawable(getResources(), bitmap);
                    postInvalidate();
                }
            });
        }
    }

    private void checkLabelOverride() {
        if (null == mTopButton || null == mBottomButton) {
            return;
        }

        if (mTopButton.getLabel().endsWith("+") && mBottomButton.getLabel().endsWith("-")) {
            mLabel = new TextDrawable(getContext());
            mLabel.setTextColor(Color.WHITE);
            mLabel.setText(mBottomButton.getLabel().replaceAll("-", "").trim());

            Resources res = getContext().getResources();
            mTopDrawable = res.getDrawable(R.drawable.button_plus);
            mBottomDrawable = res.getDrawable(R.drawable.button_minus);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mButtonSize = w - getPaddingLeft() - getPaddingRight();
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditMode) return super.onTouchEvent(event);

        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mAlpha = 0;
            top = event.getY() < getHeight() / 2.0f;
            bottom = !top;
            if (isRepeating()) {
                new Thread() {
                    public void run() {
                        while (top || bottom) {
                            if (top) {
                                mTopButton.send(getContext());
                            } else if (bottom) {
                                mBottomButton.send(getContext());
                            }
                            try {
                                // TODO This may not be necessary, but just to make sure the presses are discreet
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                // Do nothing.
                            }
                        }
                    }
                }.start();
                CountDownTimer timer = new CountDownTimer(500, 2) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        mAlpha = (int) (255 * (500 - millisUntilFinished) / 500.0f);
                        invalidate();
                    }

                    @Override
                    public void onFinish() {
                        if (top || bottom) {
                            mAlpha = 255;
                        } else {
                            mAlpha = 0;
                        }
                    }
                };
                timer.start();
            } else {
                mAlpha = 255;
                new Thread() {
                    public void run() {
                        if (top) {
                            mTopButton.send(getContext());
                        } else if (bottom) {
                            mBottomButton.send(getContext());
                        }
                    };
                }.start();
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (isRepeating()) {
                top = event.getY() < getHeight() / 2.0f;
                bottom = !top;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            top = false;
            bottom = false;
            break;
        }
        invalidate();
        return true;
    }

    public boolean isRepeating() {
        return false;// mRepeating; TODO: fix repeating.
    }

    public void setRepeating(boolean shouldRepeat) {
        mRepeating = shouldRepeat;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (top) {
            mUpGradient.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            mUpGradient.draw(canvas);
            mUpGradient.setAlpha(Math.min(255, mAlpha));
        } else if (bottom) {
            mDownGradient.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            mDownGradient.draw(canvas);
            mDownGradient.setAlpha(Math.min(255, mAlpha));
        }

        super.onDraw(canvas);

        if (null != mTopDrawable) {
            mTopDrawable.setBounds(getPaddingLeft(), getPaddingTop(), mButtonSize, mButtonSize);
            mTopDrawable.draw(canvas);
        }

        if (null != mBottomDrawable) {
            mBottomDrawable.setBounds(getPaddingLeft(), getHeight() - mButtonSize - getPaddingBottom(), mButtonSize, getHeight() - getPaddingBottom());
            mBottomDrawable.draw(canvas);
        }

        if (null != mLabel) {
            mLabel.setBounds(
                    getPaddingLeft(),
                    (int) (getHeight() / 2.0f) - mButtonSize,
                    getWidth() - getPaddingRight(),
                    (int) (getHeight() / 2.0f + mButtonSize));
            mLabel.draw(canvas);
        }
    }

    public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        spec.writeXml(xml);
        xml.attribute("", FIELD_REPEATING, Boolean.toString(isRepeating()));
        if (null != mTopButton) {
            xml.startTag("", FIELD_UP);
            mTopButton.writeXml(xml);
            xml.endTag("", FIELD_UP);
        }
        if (null != mBottomButton) {
            xml.startTag("", FIELD_DOWN);
            mBottomButton.writeXml(xml);
            xml.endTag("", FIELD_DOWN);
        }
        xml.endTag("", XMLTAG);
    }

    public static RemoteRocker fromXml(Context context, Element item) {
        RemoteRocker rocker = new RemoteRocker(context);
        rocker.setTopButton(ButtonFunctionSet.fromXml(context, FIELD_UP, item));
        rocker.setBottomButton(ButtonFunctionSet.fromXml(context, FIELD_DOWN, item));
        if (!item.hasAttribute(FIELD_REPEATING) || !Boolean.parseBoolean(item.getAttribute(FIELD_REPEATING))) {
            rocker.setRepeating(false);
        }
        return rocker;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        JsonUtil.put(object, FIELD_UP, mTopButton);
        JsonUtil.put(object, FIELD_DOWN, mBottomButton);
        object.put(FIELD_REPEATING, isRepeating());
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        setTopButton(JsonUtil.getButton(getContext(), object, FIELD_UP));
        setBottomButton(JsonUtil.getButton(getContext(), object, FIELD_DOWN));
        setRepeating(JsonUtil.getBoolean(object, FIELD_REPEATING, false));

        if (null == mTopButton || null == mBottomButton) {
            Log.w("UniversalRemote:JSON", "Top or bottom button was not loaded on a rocker button.\n" + JsonUtil.toDebugString(object));
        }
    }
}
