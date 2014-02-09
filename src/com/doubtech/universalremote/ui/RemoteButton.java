package com.doubtech.universalremote.ui;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.io.BackgroundLoader;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteButton extends ImageView implements IRemoteView {
    public static final String XMLTAG = "button";
    private ButtonFunctionSet mButtonDetails;
    protected String mBackground;

    public RemoteButton(Context context) {
        super(context);
        init();
    }

    public RemoteButton(Context context, ButtonFunction button) {
        super(context);
        init();
        setButtonDetails(button);
    }

    private void init() {
        setPadding(10, 10, 10, 10);
        setBackgroundResource(R.drawable.rounded_button);
        mButtonDetails = new ButtonFunctionSet();
        setOnClickListener(null);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        OnClickListener onClickListener;
        if (null == listener) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mButtonDetails.send(getContext());
                }
            };
        } else {
            onClickListener = listener;
        }
        super.setOnClickListener(onClickListener);
    }

    public void setBackground(String background) {
        mBackground = BackgroundLoader.setBackground(this, background);
    }

    @Override
    public void setBackgroundResource(int resid) {
        mBackground = BackgroundLoader.getBackgroundName(getContext(), resid);
        super.setBackgroundResource(resid);
    }

    public synchronized void clearFunctions() {
        mButtonDetails.clear();
    }

    public int getFunctionCount() {
        return mButtonDetails.size();
    }

    public void setButtonDetails(ButtonFunction button) {
        clearFunctions();
        mDrawablesSet = false;
        addButtonFunction(button);
    }

    public synchronized void setButtonFunctionSet(ButtonFunctionSet functionSet) {
        clearFunctions();
        for (ButtonFunction function : functionSet) {
            addButtonFunction(function);
        }
    }

    private boolean mDrawablesSet;
    public synchronized void addButtonFunction(final ButtonFunction button) {
        if (null == button) return;
        mButtonDetails.add(button);
        if (!mDrawablesSet) {
            TextDrawable drawable = new TextDrawable(getContext());
            drawable.setText(button.getLabel());
            drawable.setTextColor(Color.WHITE);
            setImageDrawable(drawable);
            button.getIcon(getContext(), new IconLoaderListener() {
                @Override
                public void onIconLoaded(final Bitmap bitmap) {
                    mDrawablesSet = true;
                    setImageDrawable(null);
                    setImageBitmap(bitmap);
                }
            });
        }
    }

    public synchronized Collection<ButtonFunction> getButtonDetails() {
        return Collections.unmodifiableCollection(mButtonDetails);
    }

    public String getXmlTag() {
        return XMLTAG;
    }

    @Override
    public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", getXmlTag());
        xml.attribute("", "background", mBackground);
        spec.writeXml(xml);
        for (ButtonFunction button : mButtonDetails) {
            button.writeXml(xml);
        }
        xml.endTag("", getXmlTag());
    }

    public static RemoteButton fromXml(Context context, Element item) {
        RemoteButton button = new RemoteButton(context);
        return fromXml(button, context, item);
    }

    public static RemoteButton fromXml(RemoteButton button, Context context,
            Element item) {
        button.mBackground = BackgroundLoader.setBackground(button, item.getAttribute("background"));
        button.setButtonFunctionSet(ButtonFunctionSet.fromXml(context, item));
        return button;
    }

    @Override
    public String toString() {
        return mButtonDetails.toString();
    }

    @Override
    public void setEditMode(boolean editMode) {

    }
}
