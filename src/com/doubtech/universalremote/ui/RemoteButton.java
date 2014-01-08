package com.doubtech.universalremote.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.doubtech.universalremote.ButtonDetails;
import com.doubtech.universalremote.drawables.TextDrawable;
import com.doubtech.universalremote.listeners.IconLoaderListener;

public class RemoteButton extends ImageView {
	private ButtonDetails mButtonDetails;

	public RemoteButton(Context context) {
		super(context);
		setBackgroundColor(Color.DKGRAY);
	}
	
	public RemoteButton(Context context, ButtonDetails button) {
		super(context);
		setBackgroundColor(Color.DKGRAY);
		setButtonDetails(button);
	}

	public synchronized void setButtonDetails(ButtonDetails button) {
		mButtonDetails = button;
		TextDrawable drawable = new TextDrawable(getContext());
		drawable.setText(button.getLabel());
		drawable.setTextColor(Color.WHITE);
		setImageDrawable(drawable);
		mButtonDetails.getIcon(getContext(), new IconLoaderListener() {			
			@Override
			public void onIconLoaded(final Bitmap bitmap) {
				post(new Runnable() {
					@Override
					public void run() {
						if(null != bitmap) {
							setImageBitmap(bitmap);
						}
					}
				});
			}
		});
	}
}
