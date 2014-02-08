package com.doubtech.universalremote;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.doubtech.universalremote.adapters.ProviderAdapter;
import com.doubtech.universalremote.adapters.TextAdapter;
import com.doubtech.universalremote.adapters.TextAdapter.RequestChildAdapterListener;
import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.io.RemoteConfigurationReader.RemotesLoadedListener;
import com.doubtech.universalremote.io.RemoteConfigurationWriter;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.IOUtil;
import com.doubtech.universalremote.utils.ProviderUtils;
import com.doubtech.universalremote.widget.DynamicListView.ISwappableAdapter;
import com.doubtech.universalremote.widget.HierarchicalListView;
import com.doubtech.universalremote.widget.HierarchicalListView.OnHierarchyChangedListener;
import com.doubtech.universalremote.widget.HierarchicalListView.OnItemClickListener;
import com.doubtech.universalremote.widget.RemotePage;
import com.doubtech.universalremote.widget.RemotePageButtonSource;
import com.doubtech.universalremote.widget.ScalePreviewView;
import com.doubtech.universalremote.widget.TwoWayListView;

public class RemotePageConfiguration extends Activity {
    private static final String TAG = "UniversalRemote :: RemotePageConfiguration";
    private class ViewHolder {

        private TextView mLabel;
        private ImageView mScreenshot;
        private ScalePreviewView mContainer;
        public RemotePage mPage;

        public ViewHolder(View convertView) {
            mLabel = (TextView) convertView.findViewById(R.id.label);
            mScreenshot = (ImageView) convertView.findViewById(R.id.screenshot);
            mContainer = (ScalePreviewView) convertView.findViewById(R.id.container);
            DisplayMetrics dm = mContainer.getContext().getResources().getDisplayMetrics();
            mContainer.setRenderSize(dm.widthPixels, dm.heightPixels);
        }
    }

    private class RemotePageAdapter extends BaseAdapter implements ISwappableAdapter {

        private ArrayList<RemotePage> mRemotes = new ArrayList<RemotePage>();
        private HashMap<RemotePage, Integer> mIdMap = new HashMap<RemotePage, Integer>();

        public RemotePageAdapter() {
            int id = 0;
            for (RemotePage remote : mRemotes) {
                mIdMap.put(remote, id++);
            }
        }

        public void add(RemotePage page) {
            mRemotes.add(page);
            if (null != page) {
                mIdMap.put(page, mIdMap.size());
                mRemotePageAdapter.notifyDataSetChanged();
            }
        }

        public void remove(RemotePage page) {
            mRemotes.remove(page);
            mIdMap.remove(page);
            mRemotePageAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(RemotePageConfiguration.this).inflate(R.layout.remote, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mContainer.removeAllViews();
            if (null == mRemotes.get(position)) {
                viewHolder.mLabel.setText("Add page");
                viewHolder.mScreenshot.setImageResource(R.drawable.ic_action_add_custom);
            } else {
                RemotePage v = mRemotes.get(position);
                viewHolder.mLabel.setText(v.getTitle());
                viewHolder.mScreenshot.setImageDrawable(null);

                ViewGroup p = (ViewGroup) v.getParent();
                if (null != p) {
                    p.removeView(v);
                }

                viewHolder.mPage = v;
                viewHolder.mContainer.addView(v);
                convertView.setAlpha(v.getAlpha());
                if (v.getAlpha() < 1) {
                    viewHolder.mScreenshot.setImageResource(R.drawable.ic_action_add_custom);
                }
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            Object item = getItem(position);
            if (null == item) {
                return -1;
            }
            Integer id = mIdMap.get(item);
            return null != id ? id : -1;
        }

        @Override
        public Object getItem(int position) {
            if (position < 0 || position >= mRemotes.size()) {
                return null;
            }
            return mRemotes.get(position);
        }

        @Override
        public int getCount() {
            return mRemotes.size();
        }

        @Override
        public void swap(int a, int b) {
            RemotePage tmp = mRemotes.get(a);
            mRemotes.set(a, mRemotes.get(b));
            mRemotes.set(b, tmp);
            notifyDataSetChanged();
        }
    }

    private TwoWayListView mList;
    private HierarchicalListView mRemotes;

    private RequestChildAdapterListener mRequestChildListener = new RequestChildAdapterListener() {

        public Object onRequestChild(final Adapter parent, Parent node) {
            TextAdapter adapter = new TextAdapter(
                    RemotePageConfiguration.this,
                    node,
                    mRequestChildListener);
            adapter.setParentAdapter(parent);
            return adapter;
        };

        @Override
        public void onLoadComplete(TextAdapter adapter, Parent parent) {
            mSourcesLabel.setText(R.string.cfg_lbl_sources_label);
            if (adapter.getCount() > 0) {
                parent = ((TextAdapter) adapter).getTarget(0);
                if (null != parent) {
                    String levelName = parent.getLevelName();
                    if (null != levelName) {
                        mSourcesLabel.setText(levelName);
                    }
                }
            }
        }
    };

    private OnItemLongClickListener mRemotesOnClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view,
                final int position, long id) {
            Adapter adapter = ((ListView) adapterView).getAdapter();
            if (adapter instanceof TextAdapter) {
                final TextAdapter ta = (TextAdapter) adapter;
                Parent parent = ta.getTarget(position);
                if (parent.hasButtonSets()) {
                    ScalePreviewView v = new ScalePreviewView(RemotePageConfiguration.this);
                    RemotePage page = new RemotePage(RemotePageConfiguration.this);
                    page.loadButtons(parent);
                    page.setTitle(parent.getName());

                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    v.setRenderSize(dm.widthPixels, dm.heightPixels);
                    int w = res.getDimensionPixelSize(R.dimen.remote_preview_width);
                    int h = res.getDimensionPixelSize(R.dimen.remote_preview_height);
                    v.measure(w, h);
                    v.layout(0, 0, w, h);
                    startDrag(page, new View.DragShadowBuilder(v) {
                        @Override
                        public void onDrawShadow(Canvas canvas) {
                            Paint p = new Paint();
                            p.setColor(getResources().getColor(color.holo_blue_bright));
                            p.setAlpha(80);
                            p.setStrokeJoin(Join.ROUND);
                            canvas.drawRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), p);
                        }
                    });
                    page.setAlpha(0);
                    mNewPage = page;
                    mRemotePageAdapter.add(page);
                    v.setTag(page);
                    return true;
                }
            }
            return false;
        }
    };

    private OnItemLongClickListener mRemotePagesLongClick = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int position, long arg3) {
            mNewPage = null;
            final RemotePage view = mRemotePageAdapter.mRemotes.get(position);
            ViewGroup parent = (ViewGroup) view.getParent();
            while (R.id.remote_preview_layout != parent.getId() && null != parent) {
                parent = (ViewGroup) parent.getParent();
            }
            final View shadowView = parent;
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(shadowView) {
                @Override
                public void onDrawShadow(Canvas canvas) {
                    Paint p = new Paint();
                    p.setColor(getResources().getColor(color.holo_blue_bright));
                    p.setAlpha(80);
                    p.setStrokeJoin(Join.ROUND);
                    canvas.drawRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), p);
                    shadowView.setAlpha(.75f);
                    shadowView.draw(canvas);
                    shadowView.setAlpha(1f);
                }
            };
            startDrag(view, shadow);
            return true;
        }
    };

    private OnItemClickListener mSourcesItemClickListener = new OnItemClickListener() {

        @Override
        public boolean onItemClick(AdapterView<?> adapterView, View view,
                int position, long id) {
            Adapter adapter = ((ListView) adapterView).getAdapter();
            Object item = adapter.getItem(position);
            if (item instanceof Button) {
                ProviderUtils.sendButton(RemotePageConfiguration.this, (Button)item);
                return true;
            } else if (adapter instanceof TextAdapter) {
                final TextAdapter ta = (TextAdapter) adapter;
                Parent parent = ta.getTarget(position);

                if (parent.hasButtonSets()) {
                    mRemotes.setSelectedPosition(position);
                    RemotePageButtonSource page = new RemotePageButtonSource(RemotePageConfiguration.this);

                    ScrollView sv = new ScrollView(RemotePageConfiguration.this);
                    page.setColumnCount(page.getColumnCount() - 2);
                    page.setCellSpacing(getResources().getDimensionPixelSize(R.dimen.cell_padding));
                    sv.addView(page);
                    mRevertHierarchyLevel = mHierarchyLevel;
                    mRemotes.addHierarchyView(sv);
                    page.loadButtons(parent);
                    page.setTag(parent);
                    page.setTitle(ta.getText(position));
                    mCurrentPage = page;
                    mMenuItemAddRemote.setVisible(true);
                    return true;
                }
            }
            return false;
        }
    };

    private OnHierarchyChangedListener mSourcesHierarchyLevelChangedListener = new OnHierarchyChangedListener() {

        @Override
        public void onHierarchyChangedListener(int level) {
            mHierarchyLevel = level;
            if (level == mRevertHierarchyLevel) {
                mMenuItemAddRemote.setVisible(false);
            }

            mSourcesLabel.setText(R.string.cfg_lbl_sources_label);
            Adapter adapter = mRemotes.getCurrentAdapter();
            if (null != adapter && adapter instanceof TextAdapter) {
                Parent parent = ((TextAdapter) adapter).getParentObject();
                if (null != parent) {
                    String levelName = parent.getLevelName();
                    if (null != levelName) {
                        mSourcesLabel.setText(levelName);
                    }
                }
            } else if (null != mRemotes.getCurrentView()) {
                mSourcesLabel.setText(R.string.cfg_lbl_buttons);
            }
        }
    };

    private android.widget.AdapterView.OnItemClickListener mRemotePagesOnItemClickListener = new AbsListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                int position, long id) {
            edit(mRemotePageAdapter.mRemotes.get(position));
        }
    };

    private RemotePageAdapter mRemotePageAdapter;
    private MenuItem mMenuItemAddRemote;
    private Uri mFile;
    private RemoteConfigurationReader mReader;
    private View mTrashIcon;
    private RemotePage mCurrentPage;
    private int mHierarchyLevel;
    private int mRevertHierarchyLevel;
    private RemotePage mNewPage;
    private TextView mSourcesLabel;
    private FrameLayout mEditorContainer;
    private OnDragListener mRemotePageDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getLocalState() instanceof RemotePage) {
                RemotePage page = (RemotePage) event.getLocalState();
                switch(event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                if (page == mNewPage) {
                    page.setAlpha(.5f);
                    mRemotePageAdapter.notifyDataSetChanged();
                    break;
                }
                case DragEvent.ACTION_DRAG_EXITED:
                if (page == mNewPage) {
                    page.setAlpha(0f);
                    mRemotePageAdapter.notifyDataSetChanged();
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    float buffer = v.getWidth() / 2.0f;
                    if (event.getX() < buffer) {
                        mList.scrollBy(10);
                    } else if (event.getX() > mList.getWidth() - buffer) {
                        mList.scrollBy(-10);
                    }
                    float x = event.getX() - mList.getScrollX();

                    ViewGroup parent = getParent(page.getParent());
                    if (null != parent && (x < parent.getLeft() || x > parent.getRight())) {
                        int idx = mRemotePageAdapter.mRemotes.indexOf(page);
                        for (int i = mList.getFirstVisiblePosition(); idx >= 0 && i <= mList.getLastVisiblePosition(); i++) {
                            if (i != idx) {
                                parent = getParent(mList.getItemAtPosition(i));
                                if (x < parent.getLeft() + buffer / 2.0f && idx > i && idx > 0) {
                                    mRemotePageAdapter.swap(idx, i);
                                    break;
                                } else if (x > parent.getRight() - buffer / 2.0f && idx < i) {
                                    mRemotePageAdapter.swap(idx, i);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (page == mNewPage) {
                        if (page.getAlpha() < 1) {
                            mRemotePageAdapter.remove(page);
                        }
                        break;
                    }
                case DragEvent.ACTION_DROP:
                if (page == mNewPage) {
                        page.setAlpha(1.0f);
                        mRemotePageAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                return true;
            }
            return false;
        }

        private ViewGroup getParent(Object p) {
            ViewGroup parent = ((ViewGroup) p);
            while (null != parent && parent.getId() != R.id.remote_preview_layout)
                parent = (ViewGroup) parent.getParent();
            return parent;
        }
    };
    private TextView mActionBarTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_page_configuration);
        mList = (TwoWayListView) findViewById(R.id.remotes);
        mRemotePageAdapter = new RemotePageAdapter();
        mList.setAdapter(mRemotePageAdapter);
        mList.setOnItemLongClickListener(mRemotePagesLongClick);
        mList.setOnItemClickListener(mRemotePagesOnItemClickListener);

        mEditorContainer = (FrameLayout) findViewById(R.id.editor_container);

        mSourcesLabel = (TextView) findViewById(R.id.sources_label);

        mRemotes = (HierarchicalListView) findViewById(R.id.remote_sources);
        mRemotes.setOnItemClickListener(mSourcesItemClickListener);
        mRemotes.setOnItemLongClickListener(mRemotesOnClickListener );
        mHierarchyLevel = 0;
        mRemotes.setHierarchyLevelChangedListener(mSourcesHierarchyLevelChangedListener);

        // loaderManager = getLoaderManager();
        // loaderManager.initLoader(LOADER_BRAND, null, mLoaderHandler);
        mRemotes.addAdapter(new ProviderAdapter(this));
        mReader = new RemoteConfigurationReader(this);

        Uri uri = getIntent().getData();
        if (null != uri) {
            mReader.open(uri, new RemotesLoadedListener() {

                @Override
                public void onRemotesLoaded(Uri uri, List<RemotePage> pages) {
                    mFile = uri;
                    for (RemotePage page : pages) {
                        mRemotePageAdapter.add(page);
                    }
                }

                @Override
                public void onRemoteLoadFailed(Throwable error) {
                    if (error instanceof FileNotFoundException) {
                        // Handle new configuration.
                    } else {
                        Toast.makeText(RemotePageConfiguration.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mTrashIcon = findViewById(R.id.icon_delete);
        final View label = findViewById(R.id.pages_label);
        label.setOnDragListener(new OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getLocalState() instanceof RemotePage) {
                    switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        label.setBackgroundColor(Color.argb(0x88, 255, 0, 0));
                        break;
                    case DragEvent.ACTION_DRAG_STARTED:
                        mTrashIcon.setVisibility(View.VISIBLE);
                    case DragEvent.ACTION_DRAG_EXITED:
                        label.setBackgroundColor(Color.argb(0x44, 255, 0, 0));
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        mTrashIcon.setVisibility(View.GONE);
                        label.setBackgroundColor(Color.argb(0x44, 0xcc, 0xcc, 0xcc));
                        break;
                    case DragEvent.ACTION_DROP:
                        mRemotePageAdapter.remove((RemotePage) event.getLocalState());
                        break;
                    }
                    return true;
                }
                return false;
            }
        });

        mList.setOnDragListener(mRemotePageDragListener);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setCustomView(R.layout.title_text);
        mActionBarTitle = (TextView) actionBar.getCustomView();
        mActionBarTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActionMode(new ActionMode.Callback() {

                    private EditText mEditText;

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        setTitle(mEditText.getText().toString());
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mEditText = new EditText(RemotePageConfiguration.this);
                        mEditText.setTextColor(Color.WHITE);
                        mEditText.setText(getActiveTitle());
                        mode.setCustomView(mEditText);
                        mEditText.requestFocus();
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Log.d("AARON", "ActionMode item clicked: " + item.getItemId());
                        return false;
                    }
                });
            }
        });
        mTitle = "";
        setTitle(getString(R.string.title_activity_remote_page_configuration));
    }

    private CharSequence getActiveTitle() {
        CharSequence title = getTitle();
        if (mEditorContainer.getChildCount() > 0) {
            ((RemotePage)mEditorContainer.getChildAt(0)).getTitle();
        } else {
            title = mTitle;
        }
        return title;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mActionBarTitle.setText(title);
        if (mEditorContainer.getChildCount() > 0) {
            ((RemotePage)mEditorContainer.getChildAt(0)).setTitle(title);
        } else {
            mTitle = title;
        }
    }

    private void startDrag(RemotePage page,
            DragShadowBuilder shadow) {
        mList.startDrag(ClipData.newPlainText("label", page.getTitle()), shadow, page, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remote_page_configuration, menu);
        mMenuItemAddRemote = menu.findItem(R.id.action_remote);
        mMenuItemAddRemote.setVisible(false);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_save:
            ParcelFileDescriptor fd = null;
            FileOutputStream fos = null;
            try {
                fd = getContentResolver().openFileDescriptor(mFile, "w");
                fos = new FileOutputStream(fd.getFileDescriptor());
                RemoteConfigurationWriter writer = new RemoteConfigurationWriter(fos, "Default Configuration");
                for (RemotePage page : mRemotePageAdapter.mRemotes) {
                    writer.addPage(page);
                }
                writer.close();
            } catch (IOException e) {
                Log.d(TAG, e.getMessage(), e);
            } finally {
                IOUtil.closeQuietly(fos);
                IOUtil.closeQuietly(fd);
            }
            Intent intent = new Intent();
            intent.setData(mFile);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        case R.id.action_remote:
            if (null != mCurrentPage) {
                Parent info = (Parent) mCurrentPage.getTag();
                ScalePreviewView v = new ScalePreviewView(RemotePageConfiguration.this);
                RemotePage page = new RemotePage(RemotePageConfiguration.this);
                page.loadButtons(info);
                page.setTitle(mCurrentPage.getTitle());
                mRemotePageAdapter.add(page);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public RequestChildAdapterListener getRequestChildListener() {
        return mRequestChildListener;
    }

    @Override
    public void onBackPressed() {
        if (mEditorContainer.getVisibility() == View.VISIBLE) {
            closeEditor(true);
        } else if (!mRemotes.closeTopView()) {
            super.onBackPressed();
        }
    }

    private void closeEditor(boolean refreshData) {
        if (mEditorContainer.getChildCount() > 0) {
            if (null != mEditorContainer.getTag()) {
                ViewGroup vg = (ViewGroup) mEditorContainer.getTag();
                View cv = mEditorContainer.getChildAt(0);
                mEditorContainer.removeView(cv);
                vg.addView(cv);
            }
            mEditorContainer.setVisibility(View.GONE);
            setTitle(R.string.title_activity_remote_page_configuration);
            if (refreshData) {
                mRemotePageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void edit(RemotePage page) {
        closeEditor(false);
        mEditorContainer.setVisibility(View.VISIBLE);

        ViewGroup group = (ViewGroup) page.getParent();
        if (null != group) {
            group.removeView(page);
            mEditorContainer.setTag(group);
        }
        setTitle(page.getTitle());
        mEditorContainer.addView(page);
    }
}
