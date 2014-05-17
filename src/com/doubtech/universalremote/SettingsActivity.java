package com.doubtech.universalremote;

import static com.doubtech.universalremote.utils.Constants.EXTRA_COLUMN_COUNT;
import static com.doubtech.universalremote.utils.Constants.EXTRA_REMOTES_DIR;
import static com.doubtech.universalremote.utils.Constants.EXTRA_TITLE;

import java.io.File;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.doubtech.universalremote.adapters.RemoteRoomAdapter.RemoteFile;
import com.doubtech.universalremote.io.RemoteFilesLoader;
import com.doubtech.universalremote.utils.Constants;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    private static final String KEY_PREF_ADD_ROOM = "key_pref_add_room";


    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private RemoteFilesLoader mRemoteFilesLoader;

    private File mDir;
    private int mColumnCount;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    private static class LongPressPreference extends Preference implements OnLongClickListener {
        OnLongClickListener mLongClickListener;
        public LongPressPreference(Context context) {
            super(context);
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != mLongClickListener) {
                return mLongClickListener.onLongClick(v);
            }
            return false;
        }

        public void setLongClickListener(OnLongClickListener listener) {
            this.mLongClickListener = listener;
        }
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_REMOTES_DIR)) {
            mDir = new File(intent.getStringExtra(EXTRA_REMOTES_DIR));
        } else {
            mDir = Constants.REMOTES_DIR;
        }

        mColumnCount = intent.getIntExtra(EXTRA_COLUMN_COUNT, 7);

        String title = intent.getStringExtra(Constants.EXTRA_ACCESSORY_NAME);
        if (null != title) {
            setTitle(title);
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        addPreferencesFromResource(R.xml.pref_rooms);

        ListView listView = getListView();
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                ListAdapter listAdapter = listView.getAdapter();
                Object obj = listAdapter.getItem(position);
                if (obj != null && obj instanceof View.OnLongClickListener) {
                    View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
                    return longListener.onLongClick(view);
                }
                return false;
            }
        });

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_rooms);
        getPreferenceScreen().addPreference(fakeHeader);
        EditTextPreference pref = (EditTextPreference) findPreference(KEY_PREF_ADD_ROOM);
        pref.setText(getString(R.string.pref_default_room_name));
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String name = "" + newValue;
                File file = new File(mDir, name + ".json");
                Uri uri = FileProvider.getUriForFile(SettingsActivity.this,
                        Constants.AUTHORITY_FILE_PROVIDER,
                        file);
                if (null == findPreference(file.toString())) {
                    addRoom(name, uri);
                }
                return true;
            }
        });

        Log.d("AARON", "mDir: " + mDir);
        mDir.mkdirs();
        mRemoteFilesLoader = new RemoteFilesLoader(mDir);
        mRemoteFilesLoader.load(SettingsActivity.this);
        for (RemoteFile file : mRemoteFilesLoader.getRemoteFiles()) {
            addRoom(file.getName(), file.getFile());
        }
    }

    public File getFileFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentUri, null, null,
                    null, null);
            cursor.moveToFirst();
            return new File(mDir, cursor.getString(0));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addRoom(String name, final Uri file) {
        final LongPressPreference newPref = new LongPressPreference(SettingsActivity.this);
        newPref.setLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ActionMode mode = startActionMode(new ActionMode.Callback() {

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch(item.getItemId()) {
                        case R.id.action_delete:
                            getPreferenceScreen().removePreference(newPref);
                            getFileFromURI(SettingsActivity.this, file).delete();
                            mode.finish();
                            return true;
                        }
                        return false;
                    }
                });
                mode.getMenuInflater().inflate(R.menu.add_item_action_mode, mode.getMenu());
                return true;
            }
        });

        Intent intent = new Intent(SettingsActivity.this, RemotePageConfiguration.class);
        intent.putExtra(EXTRA_TITLE, name);
        intent.putExtra(EXTRA_COLUMN_COUNT, mColumnCount);
        intent.putExtra(EXTRA_REMOTES_DIR, mDir.getAbsolutePath());
        mDir.mkdirs();
        intent.setData(file);
        newPref.setIntent(intent);
        newPref.setSummary(name);
        newPref.setKey(file.toString());
        getPreferenceScreen().addPreference(newPref);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    //preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone
                                .getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference
                .setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).getString(preference.getKey(),
                        ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends
            PreferenceFragment {
        private RemoteFilesLoader mRemoteFilesLoader;
        private File mDir;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);
            final ListView listView = (ListView) v.findViewById(android.R.id.list);
            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    ListAdapter listAdapter = listView.getAdapter();
                    Object obj = listAdapter.getItem(position);
                    if (obj != null && obj instanceof View.OnLongClickListener) {
                        View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
                        return longListener.onLongClick(view);
                    }
                    return false;
                }
            });
            return v;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_rooms);

            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(EXTRA_REMOTES_DIR)) {
                mDir = new File(intent.getStringExtra(EXTRA_REMOTES_DIR));
            } else {
                mDir = Constants.REMOTES_DIR;
            }


            EditTextPreference pref = (EditTextPreference) findPreference(KEY_PREF_ADD_ROOM);
            pref.setText(getString(R.string.pref_default_room_name));
            pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String name = "" + newValue;
                    File file = new File(mDir, name + ".json");
                    Uri uri = FileProvider.getUriForFile(getActivity(),
                            Constants.AUTHORITY_FILE_PROVIDER,
                            file);
                    if (null == findPreference(file.toString())) {
                        addRoom(name, uri);
                    }
                    return true;
                }
            });

            mRemoteFilesLoader = new RemoteFilesLoader(mDir);
            mRemoteFilesLoader.load(getActivity());

            for (RemoteFile file : mRemoteFilesLoader.getRemoteFiles()) {
                addRoom(file.getName(), file.getFile());
            }
        }

        private void addRoom(String name, Uri file) {
            final LongPressPreference newPref = new LongPressPreference(getActivity());
            newPref.setLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ActionMode mode = getActivity().startActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            switch(item.getItemId()) {
                            case R.id.action_delete:
                                getPreferenceScreen().removePreference(newPref);
                                new File(newPref.getKey()).delete();
                                mode.finish();
                                return true;
                            }
                            return false;
                        }
                    });
                    mode.getMenuInflater().inflate(R.menu.add_item_action_mode, mode.getMenu());
                    return true;
                }
            });

            Intent intent = new Intent(getActivity(), RemotePageConfiguration.class);
            intent.putExtra(EXTRA_TITLE, name);
            intent.setData(file);
            newPref.setIntent(intent);
            newPref.setSummary(name);
            newPref.setKey(file.toString());
            getPreferenceScreen().addPreference(newPref);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
