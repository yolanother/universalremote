package com.doubtech.universalremote.ui;

import com.doubtech.universalremote.ButtonDetails;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class RemoteRocker extends ViewGroup {
	private ButtonDetails mTopButton;
	private ButtonDetails mBottomButton;

	public RemoteRocker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RemoteRocker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RemoteRocker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RemoteRocker(Context context, ButtonDetails topButton,
			ButtonDetails bottomButton) {
		super(context);
		mTopButton = topButton;
		mBottomButton = bottomButton;
		setBackgroundColor(Color.GREEN);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

	}

}
