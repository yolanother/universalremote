package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteDpad extends View implements IRemoteView {
	public static final String XMLTAG = "dpad";
	private ButtonFunctionSet mUpButton;
	private ButtonFunctionSet mDownButton;
	private ButtonFunctionSet mRightButton;
	private ButtonFunctionSet mLeftButton;
	private ButtonFunctionSet mButtonOk;

	public RemoteDpad(Context context) {
		super(context);
		setBackgroundColor(Color.RED);
	}

	public void setUpButton(ButtonFunctionSet button) {
		mUpButton = button;
	}

	public void setDownButton(ButtonFunctionSet button) {
		mDownButton = button;
	}

	public void setRightButton(ButtonFunctionSet button) {
		mRightButton = button;
	}

	public void setLeftButton(ButtonFunctionSet button) {
		mLeftButton = button;
	}

	public void setOkButton(ButtonFunctionSet button) {
		mButtonOk = button;
	}

	@Override
	public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
		xml.startTag("", XMLTAG);
		spec.writeXml(xml);
		if(null != mUpButton) {
			xml.startTag("", "up");
			mUpButton.writeXml(xml);
			xml.endTag("", "up");
		}
		if(null != mDownButton) {
			xml.startTag("", "down");
			mDownButton.writeXml(xml);
			xml.endTag("", "down");
		}
		if(null != mLeftButton) {
			xml.startTag("", "left");
			mLeftButton.writeXml(xml);
			xml.endTag("", "left");
		}
		if(null != mRightButton) {
			xml.startTag("", "right");
			mRightButton.writeXml(xml);
			xml.endTag("", "right");
		}
		if(null != mButtonOk) {
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
