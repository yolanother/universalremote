package com.doubtech.universalremote.widget;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.json.IJsonElement;
import com.doubtech.universalremote.json.JsonObjectManager;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.ui.IRemoteView;
import com.doubtech.universalremote.ui.RemoteButton;
import com.doubtech.universalremote.ui.RemoteDpad;
import com.doubtech.universalremote.ui.RemoteNumberpad;
import com.doubtech.universalremote.ui.RemoteRocker;
import com.doubtech.universalremote.ui.RemoteToggleButton;
import com.doubtech.universalremote.utils.ButtonIdentifier;
import com.doubtech.universalremote.utils.ButtonIds;
import com.doubtech.universalremote.utils.ButtonLoaderTask;

public class RemotePage extends DropGridLayout<IRemoteView> implements IJsonElement {
    private static final String TAG = "UniversalRemote::RemotePage";

    private static final String FIELD_COLCOUNT = "colcount";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CONTROLS = "controls";
    private static final String FIELD_COLSPAN = "colspan";
    private static final String FIELD_ROWSPAN = "rowspan";
    private static final String FIELD_COL = "col";
    private static final String FIELD_ROW = "row";

    public static final String XMLTAG = "page";

    private static final HashMap<String, Class<? extends IRemoteView>> AVAILABLE_CONTROLS;




    static {
        AVAILABLE_CONTROLS = new HashMap<String, Class<? extends IRemoteView>>();
        AVAILABLE_CONTROLS.put(RemoteButton.XMLTAG.toLowerCase(Locale.getDefault()), RemoteButton.class);
        AVAILABLE_CONTROLS.put(RemoteToggleButton.XMLTAG.toLowerCase(Locale.getDefault()), RemoteToggleButton.class);
        AVAILABLE_CONTROLS.put(RemoteDpad.XMLTAG.toLowerCase(Locale.getDefault()), RemoteDpad.class);
        AVAILABLE_CONTROLS.put(RemoteNumberpad.XMLTAG.toLowerCase(Locale.getDefault()), RemoteNumberpad.class);
        AVAILABLE_CONTROLS.put(RemoteRocker.XMLTAG.toLowerCase(Locale.getDefault()), RemoteRocker.class);
    }

    private CharSequence mTitle = "";

    public RemotePage(Context context) {
        super(context);
        init();
    }

    public RemotePage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int defaultColCount = (int) Math.ceil(metrics.widthPixels / (float) metrics.xdpi * 2.5f);
        setColumnCount(defaultColCount);
        setCellSpacing(getResources().getDimensionPixelSize(R.dimen.stroke_width));
    }

    public void loadButtons(Parent parent) {
        removeAllViews();
        addView(new ProgressBar(getContext()), new ChildSpec(0, 0, getRowCount(), getColumnCount()));
        new ButtonLoaderTask(getContext()) {
            @Override
            protected void onPostExecute(Cursor result) {
                removeAllViews();
                new RemotePageBuilder(RemotePage.this).build(result);
            }
        }.execute(parent);
    }

    public static class RemotePageBuilder {
        private RemotePage mPage;
        private SparseArray<ButtonFunction> identifiedButtons;
        private ArrayList<ButtonFunction> unidentifiedButtons;
        HashMap<Integer, ButtonFunction> unusedIdentifiedButtons;

        @SuppressLint("UseSparseArrays")
        public RemotePageBuilder(Context context) {
            mPage = new RemotePage(context);
            identifiedButtons = new SparseArray<ButtonFunction>();
            unidentifiedButtons = new ArrayList<ButtonFunction>();
            unusedIdentifiedButtons = new HashMap<Integer, ButtonFunction>();
        }

        @SuppressLint("UseSparseArrays")
        public RemotePageBuilder(RemotePage existingPage) {
            mPage = existingPage;
            identifiedButtons = new SparseArray<ButtonFunction>();
            unidentifiedButtons = new ArrayList<ButtonFunction>();
            unusedIdentifiedButtons = new HashMap<Integer, ButtonFunction>();
        }

        public RemotePage build(Cursor buttonCursor) {

            int numberButtons = 0;
            int arrowButtons = 0;

            if (null != buttonCursor && buttonCursor.moveToFirst()) {
                do {
                    ButtonFunction button = new ButtonFunction(mPage.getContext(), (Button) Parent.fromCursor(buttonCursor));
                    int buttonIdentifier = button.getButtonIdentifier();
                    if (ButtonIds.BUTTON_UNKNOWN != buttonIdentifier) {
                        identifiedButtons.put(buttonIdentifier, button);
                        unusedIdentifiedButtons.put(buttonIdentifier, button);
                        if (ButtonIdentifier.isNumber(buttonIdentifier)) {
                            numberButtons++;
                        } else if (ButtonIdentifier.isArrow(buttonIdentifier)) {
                            arrowButtons++;
                        }
                    } else {
                        unidentifiedButtons.add(button);
                    }
                } while (buttonCursor.moveToNext());
            }


            final int colCount = mPage.getColumnCount();

            ChildSpec spec;

            addButton(0, ButtonIds.BUTTON_POWER_TOGGLE);

            if (arrowButtons >= 4) {
                addDpad((int) (colCount / 2.0f - 1),
                        ButtonIds.BUTTON_UP,
                        ButtonIds.BUTTON_DOWN,
                        ButtonIds.BUTTON_LEFT,
                        ButtonIds.BUTTON_RIGHT,
                        identifiedButtons.indexOfKey(ButtonIds.BUTTON_OK) >= 0 ?
                                ButtonIds.BUTTON_OK :
                                ButtonIds.BUTTON_ENTER);
            }

            if (numberButtons >= 10) {
                spec = new ChildSpec(0, (int) (colCount / 2.0f - 1), 4, 3);
                RemoteNumberpad numberPad = new RemoteNumberpad(mPage.getContext());
                for (int i = 0; i <= 10; i++) {
                    numberPad.setButtonFunction(i, identifiedButtons.get(ButtonIds.BUTTON_0 + i));
                    unusedIdentifiedButtons.remove(ButtonIds.BUTTON_0 + i);
                }
                numberPad.setTextColor(Color.WHITE);
                mPage.addView(numberPad, spec);
            }

            addButton(colCount - 1, ButtonIds.BUTTON_SOURCE);

            addRocker(1, colCount - 1, ButtonIds.BUTTON_VOLUME_UP, ButtonIds.BUTTON_VOLUME_DOWN, true);
            addButton(3, colCount - 1, ButtonIds.BUTTON_MUTE);
            addButton(colCount - 1, ButtonIds.BUTTON_CLOSED_CAPTIONS);
            addRocker(0, ButtonIds.BUTTON_CH_UP, ButtonIds.BUTTON_CH_DOWN, false);
            addRocker(0, ButtonIds.BUTTON_PAGE_UP, ButtonIds.BUTTON_PAGE_DOWN, false);
            addButton(colCount - 1, ButtonIds.BUTTON_MENU);

            addButton(1, ButtonIds.BUTTON_HOME);
            addButton(1, ButtonIds.BUTTON_GUIDE);
            addButton(colCount - 2, ButtonIds.BUTTON_BACK);
            addButton(colCount - 2, ButtonIds.BUTTON_SEARCH);

            int middle = (int) (colCount / 2.0f);
            int playctlheight = 2;
            if (identifiedButtons.indexOfKey(ButtonIds.BUTTON_PREVIOUS) >= 0 || identifiedButtons.indexOfKey(ButtonIds.BUTTON_NEXT) >= 0); {
                playctlheight = 3;
            }
            addRocker(new ChildSpec(0, middle, playctlheight, 1), ButtonIds.BUTTON_PLAY, ButtonIds.BUTTON_PAUSE, false);
            addButton(middle - 1, ButtonIds.BUTTON_REW);
            addButton(middle + 1, ButtonIds.BUTTON_FFWD);
            addButton(middle - 1, ButtonIds.BUTTON_PREVIOUS);
            addButton(middle + 1, ButtonIds.BUTTON_NEXT);
            addButton(middle - 1, ButtonIds.BUTTON_RECORD);
            addButton(middle + 1, ButtonIds.BUTTON_STOP);

            // Fill in the rest of the buttons
            int col = 0;
            int row = mPage.getMaxRow();

            // Group alpha buttons
            for (int i = ButtonIds.BUTTON_A; i <= ButtonIds.BUTTON_Z; i++) {
                ButtonFunction button = identifiedButtons.get(i);
                if (null != button) {
                    addButton(row, col, button);
                    col++;
                    if (col == mPage.getColumnCount()) {
                        row++;
                        col = 0;
                    }
                }
            }

            col = 0;
            row = mPage.getMaxRow();

            unidentifiedButtons.addAll(unusedIdentifiedButtons.values());
            Collections.sort(unidentifiedButtons, new Comparator<ButtonFunction>() {

                @Override
                public int compare(ButtonFunction lhs, ButtonFunction rhs) {
                    int cmp = Integer.valueOf(lhs.getButtonIdentifier()).compareTo(rhs.getButtonIdentifier());
                    if (0 == cmp) {
                        cmp = lhs.getLabel().compareTo(rhs.getLabel());
                    }
                    return cmp;
                }
            });
            for (ButtonFunction b : unidentifiedButtons) {
                for (; col < mPage.getColumnCount(); col++) {
                    if (!mPage.isOccupied(row, col)) {
                        break;
                    }
                    if (col + 1 == mPage.getColumnCount()) {
                        row++;
                        col = -1;
                    }
                }
                spec = new ChildSpec(row, col);
                mPage.addView(new RemoteButton(mPage.getContext(), b), spec);
                col++;
                if (col >= colCount) {
                    col = 0;
                }
            }

            return mPage;
        }

        private void addDpad(int col, int buttonUp, int buttonDown,
                int buttonLeft, int buttonRight, int buttonOk) {
            ChildSpec spec = new ChildSpec(0, col, 3, 3);
            RemoteDpad dpad = new RemoteDpad(mPage.getContext());

            dpad.setButton(RemoteDpad.BUTTON_UP, identifiedButtons.get(buttonUp));
            dpad.setButton(RemoteDpad.BUTTON_DOWN, identifiedButtons.get(buttonDown));
            dpad.setButton(RemoteDpad.BUTTON_LEFT, identifiedButtons.get(buttonLeft));
            dpad.setButton(RemoteDpad.BUTTON_RIGHT, identifiedButtons.get(buttonRight));
            dpad.setButton(RemoteDpad.BUTTON_OK, identifiedButtons.get(buttonOk));

            dpad.setTextColor(Color.WHITE);


            unusedIdentifiedButtons.remove(buttonUp);
            unusedIdentifiedButtons.remove(buttonDown);
            unusedIdentifiedButtons.remove(buttonLeft);
            unusedIdentifiedButtons.remove(buttonRight);
            unusedIdentifiedButtons.remove(buttonOk);

            mPage.addView(dpad, spec);
        }

        private boolean addRocker(int column, int upIdentifier, int downIdentifier, boolean repeating) {
            return addRocker(0, column, upIdentifier, downIdentifier, repeating);
        }

        private boolean addRocker(int row, int column, int upIdentifier, int downIdentifier, boolean repeating) {
            ChildSpec spec = new ChildSpec(row, column, 2, 1);
            return addRocker(spec, upIdentifier, downIdentifier, repeating);
        }

        private boolean addRocker(ChildSpec spec, int upIdentifier, int downIdentifier, boolean repeating) {
            ButtonFunction up = identifiedButtons.get(upIdentifier);
            ButtonFunction down = identifiedButtons.get(downIdentifier);
            addRocker(spec, up, down, repeating);
            return null != up && null != down;
        }

        public RemotePageBuilder addRocker(int column, ButtonFunction up, ButtonFunction down, boolean repeating) {
            ChildSpec spec = new ChildSpec(0, column, 2, 1);
            return addRocker(spec, up, down, repeating);
        }

        public RemotePageBuilder addRocker(ChildSpec spec, ButtonFunction up, ButtonFunction down, boolean repeating) {
            if (null != up && null != down) {
                mPage.addView(new RemoteRocker(mPage.getContext(), up, down, repeating), spec);
                unusedIdentifiedButtons.remove(up.getButtonIdentifier());
                unusedIdentifiedButtons.remove(down.getButtonIdentifier());
            }
            return this;
        }

        private boolean addButton(int column, int buttonIdentifier) {
            return addButton(0, column, buttonIdentifier);
        }

        private boolean addButton(int row, int column, int buttonIdentifier) {
            ButtonFunction button;
            button = identifiedButtons.get(buttonIdentifier);
            addButton(row, column, button);
            return null != button;
        }

        public RemotePageBuilder addButton(int column, ButtonFunction button) {
            return addButton(0, column, button);
        }

        public RemotePageBuilder addButton(int row, int column, ButtonFunction button) {
            if (null != button) {
                ChildSpec spec = new ChildSpec(row, column);
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
            if (null != toggleButton) {
                mPage.addView(new RemoteButton(mPage.getContext(), toggleButton), spec);
                unusedIdentifiedButtons.remove(toggleButton.getButtonIdentifier());
            } else {
                RemoteToggleButton button = new RemoteToggleButton(mPage.getContext());
                for (ButtonFunction state : states) {
                    if (null != state) {
                        button.addState(state);
                    }
                }
                if (button.getStateCount() > 0) {
                    mPage.addView(button, spec);
                }
            }
            for (ButtonFunction function : states) {
                if (null != function) {
                    unusedIdentifiedButtons.remove(function.getButtonIdentifier());
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

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(FIELD_TITLE, getTitle());
        object.put(FIELD_COLCOUNT, getColumnCount());
        JSONArray array = new JSONArray();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof IRemoteView) {
                JSONObject jobj = JsonObjectManager.toJson((IRemoteView) v);
                ChildSpec spec = getChildSpec(v);
                jobj.put(FIELD_ROW, spec.row);
                jobj.put(FIELD_COL, spec.col);
                jobj.put(FIELD_ROWSPAN, spec.rowspan);
                jobj.put(FIELD_COLSPAN, spec.colspan);
                array.put(jobj);
            }
        }
        object.put(FIELD_CONTROLS, array);
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        setTitle(object.getString(FIELD_TITLE));
        setColumnCount(object.getInt(FIELD_COLCOUNT));
        JSONArray controls = object.getJSONArray(FIELD_CONTROLS);
        for (int i = 0; i < controls.length(); i++) {
            JSONObject control = controls.getJSONObject(i);
            try {
                View v = (View) JsonObjectManager.fromJson(control, getContext());
                int row = 0;
                int col = 0;
                int rowspan = 1;
                int colspan = 1;
                if (control.has(FIELD_ROW)) {
                    row = control.getInt(FIELD_ROW);
                }
                if (control.has(FIELD_COL)) {
                    col = control.getInt(FIELD_COL);
                }
                if (control.has(FIELD_ROWSPAN)) {
                    rowspan = control.getInt(FIELD_ROWSPAN);
                }
                if (control.has(FIELD_COLSPAN)) {
                    colspan = control.getInt(FIELD_COLSPAN);
                }
                ChildSpec spec = new ChildSpec(row, col, rowspan, colspan);
                addView(v, spec);
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException e) {
                Log.e(TAG, "Error parsing RemotePage: " + e.getMessage(), e);
            }
        }
    }

    @Deprecated
    public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        xml.attribute("", FIELD_TITLE, "" + getTitle());
        xml.attribute("", FIELD_COLCOUNT, Integer.toString(getColumnCount()));
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof IRemoteView) {
                ((IRemoteView)v).writeXml(xml, getChildSpec(v));
            }
        }
        xml.endTag("", XMLTAG);
    }

    @Deprecated
    public static RemotePage fromXml(Context context, Element item) {
        RemotePage page = new RemotePage(context);
        page.setTitle(item.getAttribute(FIELD_TITLE));
        String rc = item.getAttribute(FIELD_COLCOUNT);
        if (null != rc && rc.length() > 0) {
            page.setColumnCount(Integer.parseInt(rc));
        }
        NodeList items = item.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i) instanceof Element) {
                Element control = (Element) items.item(i);
                try {
                    Class<? extends IRemoteView> controlClass = AVAILABLE_CONTROLS.get(control.getTagName().toLowerCase(Locale.getDefault()));
                    Method method = controlClass.getMethod("fromXml", Context.class, Element.class);
                    int col = Integer.parseInt(control.getAttribute(FIELD_COL));
                    int row = Integer.parseInt(control.getAttribute(FIELD_ROW));
                    int colspan = Integer.parseInt(control.getAttribute(FIELD_COLSPAN));
                    int rowspan = Integer.parseInt(control.getAttribute(FIELD_ROWSPAN));

                    View view = (View) method.invoke(null, context, control);
                    page.addView(view, new ChildSpec(row, col, rowspan, colspan));
                } catch (NoSuchMethodException e) {
                    Log.wtf(TAG, "Unknown control (no such method): " + control.getTagName(), e);
                } catch (IllegalAccessException e) {
                    Log.wtf(TAG, "Unknown control (illegal access): " + control.getTagName(), e);
                } catch (IllegalArgumentException e) {
                    Log.wtf(TAG, "Unknown control (illegal argument): " + control.getTagName(), e);
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Unknown control (invocation target exception): " + control.getTagName(), e);
                }
            }
        }
        return page;
    }

    @Override
    public boolean shouldForceSquare() {
        return true;
    }

    private OnLongClickListener mLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            startDrag(v);
            return true;
        }
    };


    @Override
    public void setDragEnabled(boolean dragEnabled) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnLongClickListener(dragEnabled ? mLongClickListener : null);
        }
        super.setDragEnabled(dragEnabled);
    }

    public void setEditMode(boolean editMode) {
        setDragEnabled(editMode);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof IRemoteView) {
                ((IRemoteView) v).setEditMode(editMode);
            }
        }
    }
}
