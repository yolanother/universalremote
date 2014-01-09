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
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public class RemoteNumberpad extends View implements IRemoteView {
	public static final String XMLTAG = "number-pad";
	private ButtonFunctionSet[] mButtons = new ButtonFunctionSet[10];

	public RemoteNumberpad(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setBackgroundColor(Color.CYAN);
	}

	public RemoteNumberpad(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RemoteNumberpad(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public void set0(ButtonFunction button) {
		mButtons[0] = button;
	}

	public void set1(ButtonFunction button) {
		mButtons[1] = button;
	}

	public void set2(ButtonFunction button) {
		mButtons[2] = button;
	}

	public void set3(ButtonFunction button) {
		mButtons[3] = button;
	}

	public void set4(ButtonFunction button) {
		mButtons[4] = button;
	}

	public void set5(ButtonFunction button) {
		mButtons[5] = button;
	}

	public void set6(ButtonFunction button) {
		mButtons[6] = button;
	}

	public void set7(ButtonFunction button) {
		mButtons[7] = button;
	}

	public void set8(ButtonFunction button) {
		mButtons[8] = button;
	}

	public void set9(ButtonFunction button) {
		mButtons[9] = button;
	}

	@Override
	public void writeXml(XmlSerializer xml, ChildSpec spec) throws IllegalArgumentException, IllegalStateException, IOException {
		xml.startTag("", XMLTAG);
		spec.writeXml(xml);
		
		for(int i = 0; i < mButtons.length; i++) {
			if(null != mButtons[i]) {
				xml.startTag("", "num" + Integer.toString(i));
				mButtons[i].writeXml(xml);
				xml.endTag("", "num" + Integer.toString(i));
			}
		}
		xml.endTag("", XMLTAG);
	}

	public static RemoteNumberpad fromXml(Context context, Element item) {
		RemoteNumberpad numberPad = new RemoteNumberpad(context);
		for(int i = 0; i < numberPad.mButtons.length; i++) {
			numberPad.mButtons[i] = ButtonFunctionSet.fromXml(context, "num" + Integer.toString(i), item);
		}
		return numberPad;
	}
}
