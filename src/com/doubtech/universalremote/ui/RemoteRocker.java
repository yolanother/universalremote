package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteRocker extends View implements IRemoteView {
	public static final String XMLTAG = "rocker";
	private ButtonFunctionSet mTopButton;
	private ButtonFunctionSet mBottomButton;

	public RemoteRocker(Context context) {
		super(context);
		setBackgroundResource(R.drawable.rounded_button);
	}

	public RemoteRocker(Context context, ButtonFunction topButton,
			ButtonFunction bottomButton) {
		super(context);
		mTopButton = topButton;
		mBottomButton = bottomButton;
		setBackgroundResource(R.drawable.rounded_button);
	}

	public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
		xml.startTag("", XMLTAG);
		spec.writeXml(xml);

		if(null != mTopButton) {
			xml.startTag("", "up");
			mTopButton.writeXml(xml);
			xml.endTag("", "up");
		}
		if(null != mBottomButton) {
			xml.startTag("", "down");
			mBottomButton.writeXml(xml);
			xml.endTag("", "down");
		}
		xml.endTag("", XMLTAG);
	}
	
	public static RemoteRocker fromXml(Context context, Element item) {
		RemoteRocker rocker = new RemoteRocker(context);
		rocker.mTopButton = ButtonFunctionSet.fromXml(context, "up", item);
		rocker.mBottomButton = ButtonFunctionSet.fromXml(context, "down", item);
		return rocker;
	}
}
