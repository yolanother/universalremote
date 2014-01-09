package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteNumberpad extends View implements IRemoteView {
    public static final String XMLTAG = "number-pad";
    private ButtonFunctionSet[] mButtons = new ButtonFunctionSet[10];
    private Drawable[] mButtonDrawables = new Drawable[10];

    public RemoteNumberpad(Context context) {
        super(context);
        setBackgroundResource(R.drawable.rounded_button);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float right = getWidth() - getPaddingRight();
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float bottom = getHeight() - getPaddingBottom();


        float divWidth = (right - left) / 3.0f;
        float divHeight = (bottom - top) / 4.0f;

        int row = 3;
        int col = 1;

        for (Drawable d : mButtonDrawables) {
            if (null != d) {
                d.setBounds(
                        (int) (left + divWidth * col),
                        (int) (top + divHeight * row),
                        (int) (left + divWidth * col + divWidth),
                        (int) (top + divHeight * row + divHeight));
                d.draw(canvas);
            }
            col++;
            if (col > 2 || row == 3) {
                col = 0;
                row--;
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
        RemoteNumberpad numberPad = new RemoteNumberpad(context);
        for (int i = 0; i < numberPad.mButtons.length; i++) {
            numberPad.setButtonFunction(i,
                    ButtonFunctionSet.fromXml(context, "num" + Integer.toString(i), item));
        }
        return numberPad;
    }
}
