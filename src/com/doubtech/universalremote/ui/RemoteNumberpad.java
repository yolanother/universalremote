package com.doubtech.universalremote.ui;

import com.doubtech.universalremote.ButtonDetails;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class RemoteNumberpad extends View {

	private ButtonDetails mButton0;
	private ButtonDetails mButton1;
	private ButtonDetails mButton2;
	private ButtonDetails mButton3;
	private ButtonDetails mButton4;
	private ButtonDetails mButton5;
	private ButtonDetails mButton6;
	private ButtonDetails mButton7;
	private ButtonDetails mButton8;
	private ButtonDetails mButton9;

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

	public void set0(ButtonDetails button) {
		mButton0 = button;
	}

	public void set1(ButtonDetails button) {
		mButton1 = button;
	}

	public void set2(ButtonDetails button) {
		mButton2 = button;
	}

	public void set3(ButtonDetails button) {
		mButton3 = button;
	}

	public void set4(ButtonDetails button) {
		mButton4 = button;
	}

	public void set5(ButtonDetails button) {
		mButton5 = button;
	}

	public void set6(ButtonDetails button) {
		mButton6 = button;
	}

	public void set7(ButtonDetails button) {
		mButton7 = button;
	}

	public void set8(ButtonDetails button) {
		mButton8 = button;
	}

	public void set9(ButtonDetails button) {
		mButton9 = button;
	}
}
