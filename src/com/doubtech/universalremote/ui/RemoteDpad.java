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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteDpad extends View implements IRemoteView {
    public static final String XMLTAG = "dpad";
    private ButtonFunctionSet mUpButton;
    private ButtonFunctionSet mDownButton;
    private ButtonFunctionSet mRightButton;
    private ButtonFunctionSet mLeftButton;
    private ButtonFunctionSet mButtonOk;

    private Drawable mUpDrawable;
    private Drawable mDownDrawable;
    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private Drawable mOkDrawable;

    private Paint mPathPaint;

    public RemoteDpad(Context context) {
        super(context);
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setStrokeCap(Cap.ROUND);
        mPathPaint.setStrokeJoin(Join.ROUND);
        mPathPaint.setColor(getResources().getColor(color.holo_blue_dark));
        mPathPaint.setStyle(Style.STROKE);
        mPathPaint.setAntiAlias(true);
    }

    public void setUpButton(ButtonFunctionSet button) {
        mUpButton = button;
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
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        float right = getWidth() - getPaddingRight();
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float bottom = getHeight() - getPaddingBottom();

        float divWidth = (right - left) / 3.0f;
        float spacingDivWidth = divWidth / 4.0f;
        float subDivWidth = divWidth - spacingDivWidth * 2;

        float divHeight = (bottom - top) / 3.0f;
        float spacingDivHeight = divHeight / 4.0f;
        float subDivHeight = divHeight - spacingDivHeight * 2;

        float x = left + divWidth + spacingDivWidth;
        float y = top;
        path.moveTo(x, y);
        x += subDivWidth;
        path.lineTo(x, y);
        x = right - divWidth;
        y = top + divWidth;
        path.lineTo(x, y);
        x = right;
        y += spacingDivHeight;
        path.lineTo(x, y);
        y += subDivHeight;
        path.lineTo(x, y);
        x = right - divWidth;
        y = bottom - divHeight;
        path.lineTo(x, y);
        x -= spacingDivWidth;
        y = bottom;
        path.lineTo(x, y);
        x -= subDivWidth;
        path.lineTo(x, y);
        x -= spacingDivWidth;
        y = bottom - divHeight;
        path.lineTo(x, y);
        x = left;
        y -= spacingDivHeight;
        path.lineTo(x, y);
        y -= subDivHeight;
        path.lineTo(x, y);
        y = top + divHeight;
        x = left + divWidth;
        path.lineTo(x, y);
        x += spacingDivWidth;
        y = top;
        path.lineTo(x, y);

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
        return dpad;
    }
}
