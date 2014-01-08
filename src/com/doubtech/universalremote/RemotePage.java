package com.doubtech.universalremote;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.ui.RemoteButton;
import com.doubtech.universalremote.ui.RemoteDpad;
import com.doubtech.universalremote.ui.RemoteNumberpad;
import com.doubtech.universalremote.ui.RemoteRocker;
import com.doubtech.universalremote.ui.RemoteToggleButton;
import com.doubtech.universalremote.widget.DropGridLayout;

public class RemotePage extends DropGridLayout {

	private CharSequence mTitle = "";

	public RemotePage(Context context) {
		super(context);
	}

	public RemotePage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public static class RemotePageBuilder {
		private RemotePage mPage;
		private SparseArray<ButtonDetails> identifiedButtons;
		private ArrayList<ButtonDetails> unidentifiedButtons;
		
		public RemotePageBuilder(Context context) {
			mPage = new RemotePage(context);	
			identifiedButtons = new SparseArray<ButtonDetails>();
			unidentifiedButtons = new ArrayList<ButtonDetails>();
		}

		public RemotePage build(Cursor buttonCursor) {

			int numberButtons = 0;
			int arrowButtons = 0;
			
			if(buttonCursor.moveToFirst()) {
				do {
					String authority = buttonCursor.getString(Buttons.COLIDX_AUTHORITY);
					ButtonDetails button = new ButtonDetails(authority, buttonCursor);
					int buttonIdentifier = button.getButtonIdentifier();
					if(ButtonIdentifier.BUTTON_UNKNOWN != buttonIdentifier) {
						identifiedButtons.put(buttonIdentifier, button);
						if(ButtonIdentifier.isNumber(buttonIdentifier)) {
							numberButtons++;
						} else if(ButtonIdentifier.isArrow(buttonIdentifier)) {
							arrowButtons++;
						}
					} else {
						unidentifiedButtons.add(button);
					}
				} while(buttonCursor.moveToNext());
			}
			
			int nextRow = 0;

			final int colCount = mPage.getColumnCount();

			ChildSpec spec;
			
			addToggleButton(0, ButtonIdentifier.BUTTON_POWER_TOGGLE, ButtonIdentifier.BUTTON_POWER_ON, ButtonIdentifier.BUTTON_POWER_OFF);
			
			if(arrowButtons == 4) {
				addDpad((int) (colCount / 2.0f - 1),
						ButtonIdentifier.BUTTON_UP,
						ButtonIdentifier.BUTTON_DOWN,
						ButtonIdentifier.BUTTON_LEFT,
						ButtonIdentifier.BUTTON_RIGHT,
						ButtonIdentifier.BUTTON_OK);
			}
			
			if(numberButtons == 10) {
				spec = new ChildSpec(0, (int) (colCount / 2.0f - 1), 4, 3);
				RemoteNumberpad numberPad = new RemoteNumberpad(mPage.getContext());
				numberPad.set0(identifiedButtons.get(ButtonIdentifier.BUTTON_0));
				numberPad.set1(identifiedButtons.get(ButtonIdentifier.BUTTON_1));
				numberPad.set2(identifiedButtons.get(ButtonIdentifier.BUTTON_2));
				numberPad.set3(identifiedButtons.get(ButtonIdentifier.BUTTON_3));
				numberPad.set4(identifiedButtons.get(ButtonIdentifier.BUTTON_4));
				numberPad.set5(identifiedButtons.get(ButtonIdentifier.BUTTON_5));
				numberPad.set6(identifiedButtons.get(ButtonIdentifier.BUTTON_6));
				numberPad.set7(identifiedButtons.get(ButtonIdentifier.BUTTON_7));
				numberPad.set8(identifiedButtons.get(ButtonIdentifier.BUTTON_8));
				numberPad.set9(identifiedButtons.get(ButtonIdentifier.BUTTON_9));
				mPage.addView(numberPad, spec);
			}
			
			addButton(colCount - 1, ButtonIdentifier.BUTTON_SOURCE);
	
			addRocker(colCount - 1, ButtonIdentifier.BUTTON_VOLUME_UP, ButtonIdentifier.BUTTON_VOLUME_DOWN);
			addButton(colCount - 1, ButtonIdentifier.BUTTON_MUTE);
			addRocker(0, ButtonIdentifier.BUTTON_CH_UP, ButtonIdentifier.BUTTON_CH_DOWN);
			addButton(colCount - 1, ButtonIdentifier.BUTTON_MENU);
			
			// Fill in the rest of the buttons
			int col = 0;
			for(ButtonDetails b : unidentifiedButtons) {
				spec = new ChildSpec(0, col);
				mPage.addView(new RemoteButton(mPage.getContext(), b), spec);
				col++;
				if(col >= colCount) {
					col = 0;
				}
			}
			
			return mPage;
		}

		private void addDpad(int col, int buttonUp, int buttonDown,
				int buttonLeft, int buttonRight, int buttonOk) {
			ChildSpec spec = new ChildSpec(0, col, 3, 3);
			RemoteDpad dpad = new RemoteDpad(mPage.getContext());

			dpad.setUpButton(identifiedButtons.get(ButtonIdentifier.BUTTON_UP));
			dpad.setDownButton(identifiedButtons.get(ButtonIdentifier.BUTTON_DOWN));
			dpad.setLeftButton(identifiedButtons.get(ButtonIdentifier.BUTTON_LEFT));
			dpad.setRightButton(identifiedButtons.get(ButtonIdentifier.BUTTON_RIGHT));
			dpad.setOKButton(identifiedButtons.get(ButtonIdentifier.BUTTON_OK));

			mPage.addView(dpad, spec);
		}

		private boolean addRocker(int column, int upIdentifier, int downIdentifier) {
			ButtonDetails up = identifiedButtons.get(upIdentifier);
			ButtonDetails down = identifiedButtons.get(downIdentifier);
			addRocker(column, up, down);
			return null != up && null != down;
		}

		public RemotePageBuilder addRocker(int column, ButtonDetails up, ButtonDetails down) {
			if(null != up && null != down) {
				ChildSpec spec = new ChildSpec(0, column, 2, 1);
				mPage.addView(new RemoteRocker(mPage.getContext(), up, down), spec);
			}
			return this;
		}

		private boolean addButton(int column, int buttonIdentifier) {
			ButtonDetails button;
			ChildSpec spec;
			button = identifiedButtons.get(buttonIdentifier);
			addButton(column, button);
			return null != button;
		}
		
		public RemotePageBuilder addButton(int column, ButtonDetails button) {
			if(null != button) {
				ChildSpec spec = new ChildSpec(0, column);
				mPage.addView(new RemoteButton(mPage.getContext(), button), spec);			
			}
			return this;
		}

		/**
		 * Creates a toggle button. If a toggle button has been found for the desired functionality
		 * use it, otherwise create an actual toggle button with firstState and secondState
		 * @param column
		 * @param toggleButton
		 * @param firstState
		 * @param secondState
		 * @return
		 */
		private boolean addToggleButton(int column, int toggleButton, int firstState, int secondState) {
			ButtonDetails toggle = identifiedButtons.get(toggleButton);
			ButtonDetails a = identifiedButtons.get(firstState);
			ButtonDetails b = identifiedButtons.get(secondState);
			addToggleButton(column, toggle, new ButtonDetails[] {a, b});
			return null != toggle || null != a && null != b;
		}

		/**
		 * Creates a toggle button. If a toggle button has been found for the desired functionality
		 * use it, otherwise create an actual toggle button with firstState and secondState
		 * @param column
		 * @param toggleButton
		 * @param states
		 * @return
		 */
		public RemotePageBuilder addToggleButton(int column, ButtonDetails toggleButton, ButtonDetails[] states) {
			ChildSpec spec = new ChildSpec(0, column);
			if(null != toggleButton) {
				mPage.addView(new RemoteButton(mPage.getContext(), toggleButton), spec);			
			} else {
				RemoteToggleButton button = new RemoteToggleButton(mPage.getContext());
				for(ButtonDetails state : states) {
					if(null != state) {
						button.addState(state);
					}
				}
				if(button.getStateCount() > 0) {
					mPage.addView(button, spec);
				}
			}
			return this;
		}

		public void setTitle(CharSequence text) {
			mPage.setTitle(text);
		}
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public void setTitle(CharSequence text) {
		mTitle  = text;
	}
}
