package com.doubtech.universalremote.ui;

import android.content.Context;
import android.graphics.Color;

import com.doubtech.universalremote.ButtonDetails;

public class RemoteToggleButton extends RemoteButton {
	public RemoteToggleButton(Context context) {
		super(context);
		setBackgroundColor(Color.MAGENTA);
	}

	public void addState(ButtonDetails state) {
		// TODO Auto-generated method stub
		
	}

	public int getStateCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
