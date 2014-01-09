package com.doubtech.universalremote;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.ui.IRemoteView;
import com.doubtech.universalremote.ui.RemoteButton;
import com.doubtech.universalremote.ui.RemoteDpad;
import com.doubtech.universalremote.ui.RemoteNumberpad;
import com.doubtech.universalremote.ui.RemoteRocker;
import com.doubtech.universalremote.ui.RemoteToggleButton;
import com.doubtech.universalremote.widget.DropGridLayout;

public class RemotePage extends DropGridLayout {
	public static final String XMLTAG = "page";
	
	private static final HashMap<String, Class<? extends IRemoteView>> AVAILABLE_CONTROLS;

	private static final String TAG = "UniversalRemote::RemotePage";
	
	static {
		AVAILABLE_CONTROLS = new HashMap<String, Class<? extends IRemoteView>>();
		AVAILABLE_CONTROLS.put(RemoteButton.XMLTAG, RemoteButton.class);
		AVAILABLE_CONTROLS.put(RemoteToggleButton.XMLTAG, RemoteToggleButton.class);
		AVAILABLE_CONTROLS.put(RemoteDpad.XMLTAG, RemoteDpad.class);
		AVAILABLE_CONTROLS.put(RemoteNumberpad.XMLTAG, RemoteNumberpad.class);
		AVAILABLE_CONTROLS.put(RemoteRocker.XMLTAG, RemoteRocker.class);
	}

	private CharSequence mTitle = "";

	public RemotePage(Context context) {
		super(context);
	}

	public RemotePage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public static class RemotePageBuilder {
		private RemotePage mPage;
		private SparseArray<ButtonFunction> identifiedButtons;
		private ArrayList<ButtonFunction> unidentifiedButtons;
		HashMap<Integer, ButtonFunction> unusedIdentifiedButtons;
		
		public RemotePageBuilder(Context context) {
			mPage = new RemotePage(context);	
			identifiedButtons = new SparseArray<ButtonFunction>();
			unidentifiedButtons = new ArrayList<ButtonFunction>();
			unusedIdentifiedButtons = new HashMap<Integer, ButtonFunction>();
		}

		public RemotePage build(Cursor buttonCursor) {

			int numberButtons = 0;
			int arrowButtons = 0;
			
			if(buttonCursor.moveToFirst()) {
				do {
					String authority = buttonCursor.getString(Buttons.COLIDX_AUTHORITY);
					ButtonFunction button = new ButtonFunction(mPage.getContext(), authority, buttonCursor);
					int buttonIdentifier = button.getButtonIdentifier();
					if(ButtonIdentifier.BUTTON_UNKNOWN != buttonIdentifier) {
						identifiedButtons.put(buttonIdentifier, button);
						unusedIdentifiedButtons.put(buttonIdentifier, button);
						if(ButtonIdentifier.isNumber(buttonIdentifier)) {
							numberButtons++;
						} else if(ButtonIdentifier.isArrow(buttonIdentifier)) {
							arrowButtons++;
						}
					} else {
						unidentifiedButtons.add(button);
					}
					Log.d("AARON", button.getLabel());
				} while(buttonCursor.moveToNext());
			}
			

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
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_0);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_1);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_2);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_3);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_4);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_5);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_6);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_7);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_8);
				unusedIdentifiedButtons.remove(ButtonIdentifier.BUTTON_9);
				mPage.addView(numberPad, spec);
			}
			
			addButton(colCount - 1, ButtonIdentifier.BUTTON_SOURCE);
	
			addRocker(colCount - 1, ButtonIdentifier.BUTTON_VOLUME_UP, ButtonIdentifier.BUTTON_VOLUME_DOWN);
			addButton(colCount - 1, ButtonIdentifier.BUTTON_MUTE);
			addRocker(0, ButtonIdentifier.BUTTON_CH_UP, ButtonIdentifier.BUTTON_CH_DOWN);
			addButton(colCount - 1, ButtonIdentifier.BUTTON_MENU);
			
			int middle = (int) (colCount / 2.0f);
			int playctlheight = 2;
			if(identifiedButtons.indexOfKey(ButtonIdentifier.BUTTON_PREVIOUS) >= 0 || identifiedButtons.indexOfKey(ButtonIdentifier.BUTTON_NEXT) >= 0); {
				playctlheight = 3;
			}
			addRocker(new ChildSpec(0, middle, playctlheight, 1), ButtonIdentifier.BUTTON_PLAY, ButtonIdentifier.BUTTON_PAUSE);
			addButton(middle - 1, ButtonIdentifier.BUTTON_REW);
			addButton(middle + 1, ButtonIdentifier.BUTTON_FFWD);
			addButton(middle - 1, ButtonIdentifier.BUTTON_PREVIOUS);
			addButton(middle + 1, ButtonIdentifier.BUTTON_NEXT);
			addButton(middle - 1, ButtonIdentifier.BUTTON_RECORD);
			addButton(middle + 1, ButtonIdentifier.BUTTON_STOP);
			
			// Fill in the rest of the buttons
			int col = 0;
			int row = mPage.getMaxRow() + 1;

			unidentifiedButtons.addAll(unusedIdentifiedButtons.values());
			Collections.sort(unidentifiedButtons, new Comparator<ButtonFunction>() {

				@Override
				public int compare(ButtonFunction lhs, ButtonFunction rhs) {
					int cmp = Integer.valueOf(lhs.getButtonIdentifier()).compareTo(rhs.getButtonIdentifier());
					if(0 == cmp) {
						cmp = lhs.getLabel().compareTo(rhs.getLabel());
					}
					return cmp;
				}
			});
			for(ButtonFunction b : unidentifiedButtons) {
				for(; col < mPage.getColumnCount(); col++) {
					if(!mPage.isOccupied(row, col)) {
						break;
					}
					if(col + 1 == mPage.getColumnCount()) {
						row++;
						col = -1;
					}
				}
				spec = new ChildSpec(row, col);
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

			dpad.setUpButton(identifiedButtons.get(buttonUp));
			dpad.setDownButton(identifiedButtons.get(buttonDown));
			dpad.setLeftButton(identifiedButtons.get(buttonLeft));
			dpad.setRightButton(identifiedButtons.get(buttonRight));
			dpad.setOkButton(identifiedButtons.get(buttonOk));
			

			unusedIdentifiedButtons.remove(buttonUp);
			unusedIdentifiedButtons.remove(buttonDown);
			unusedIdentifiedButtons.remove(buttonLeft);
			unusedIdentifiedButtons.remove(buttonRight);
			unusedIdentifiedButtons.remove(buttonOk);

			mPage.addView(dpad, spec);
		}

		private boolean addRocker(int column, int upIdentifier, int downIdentifier) {
			ChildSpec spec = new ChildSpec(0, column, 2, 1);
			return addRocker(spec, upIdentifier, downIdentifier);
		}

		private boolean addRocker(ChildSpec spec, int upIdentifier, int downIdentifier) {
			ButtonFunction up = identifiedButtons.get(upIdentifier);
			ButtonFunction down = identifiedButtons.get(downIdentifier);
			addRocker(spec, up, down);
			return null != up && null != down;
		}
		
		public RemotePageBuilder addRocker(int column, ButtonFunction up, ButtonFunction down) {
			ChildSpec spec = new ChildSpec(0, column, 2, 1);
			return addRocker(spec, up, down);
		}

		public RemotePageBuilder addRocker(ChildSpec spec, ButtonFunction up, ButtonFunction down) {
			if(null != up && null != down) {
				mPage.addView(new RemoteRocker(mPage.getContext(), up, down), spec);
				unusedIdentifiedButtons.remove(up.getButtonIdentifier());
				unusedIdentifiedButtons.remove(down.getButtonIdentifier());
			}
			return this;
		}

		private boolean addButton(int column, int buttonIdentifier) {
			ButtonFunction button;
			button = identifiedButtons.get(buttonIdentifier);
			addButton(column, button);
			return null != button;
		}
		
		public RemotePageBuilder addButton(int column, ButtonFunction button) {
			if(null != button) {
				ChildSpec spec = new ChildSpec(0, column);
				mPage.addView(new RemoteButton(mPage.getContext(), button), spec);
				unusedIdentifiedButtons.remove(button.getButtonIdentifier());
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
			ButtonFunction toggle = identifiedButtons.get(toggleButton);
			ButtonFunction a = identifiedButtons.get(firstState);
			ButtonFunction b = identifiedButtons.get(secondState);
			addToggleButton(column, toggle, new ButtonFunction[] {a, b});
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
		public RemotePageBuilder addToggleButton(int column, ButtonFunction toggleButton, ButtonFunction[] states) {
			ChildSpec spec = new ChildSpec(0, column);
			if(null != toggleButton) {
				mPage.addView(new RemoteButton(mPage.getContext(), toggleButton), spec);			
			} else {
				RemoteToggleButton button = new RemoteToggleButton(mPage.getContext());
				for(ButtonFunction state : states) {
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

	public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
		xml.startTag("", XMLTAG);
		xml.attribute("", "title", "" + getTitle());
		for(int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if(v instanceof IRemoteView) {
				((IRemoteView)v).writeXml(xml, getChildSpec(v));
			}
		}
		xml.endTag("", XMLTAG);
	}

	public static RemotePage fromXml(Context context, Element item) {
		RemotePage page = new RemotePage(context);
		page.setTitle(item.getAttribute("title"));
		NodeList items = item.getChildNodes();
		for(int i = 0; i < items.getLength(); i++) {
			if(items.item(i) instanceof Element) {
				Element control = (Element) items.item(i);
				try {
					Class<? extends IRemoteView> controlClass = AVAILABLE_CONTROLS.get(control.getTagName());
					Method method = controlClass.getMethod("fromXml", Context.class, Element.class);
					int col = Integer.parseInt(control.getAttribute("col"));
					int row = Integer.parseInt(control.getAttribute("row"));
					int colspan = Integer.parseInt(control.getAttribute("colspan"));
					int rowspan = Integer.parseInt(control.getAttribute("rowspan"));
					
					page.addView((View) method.invoke(null, context, control), new ChildSpec(row, col, rowspan, colspan));
				} catch (NoSuchMethodException e) {
					Log.wtf(TAG, "Unknown control (no such method): " + control.getTagName());
				} catch (IllegalAccessException e) {
					Log.wtf(TAG, "Unknown control (illegal access): " + control.getTagName());
				} catch (IllegalArgumentException e) {
					Log.wtf(TAG, "Unknown control (illegal argument): " + control.getTagName());
				} catch (InvocationTargetException e) {
					Log.wtf(TAG, "Unknown control (invocation target exception): " + control.getTagName());
				}
			}
		}
		return page;
	}
}