package com.doubtech.universalremote.ui;

import com.doubtech.universalremote.ButtonDetails;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class RemoteDpad extends View {
	private ButtonDetails mUpButton;
	private ButtonDetails mDownButton;
	private ButtonDetails mRightButton;
	private ButtonDetails mLeftButton;
	private ButtonDetails mButtonOk;

	public RemoteDpad(Context context) {
		super(context);
		setBackgroundColor(Color.RED);
	}

	public void setUpButton(ButtonDetails button) {
		mUpButton = button;
	}

	public void setDownButton(ButtonDetails button) {
		mDownButton = button;
	}

	public void setRightButton(ButtonDetails button) {
		mRightButton = button;
	}

	public void setLeftButton(ButtonDetails button) {
		mLeftButton = button;
	}

	public void setOKButton(ButtonDetails button) {
		mButtonOk = button;
	}
}
