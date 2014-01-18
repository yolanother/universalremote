package com.doubtech.universalremote.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.RemotePageConfiguration;
import com.doubtech.universalremote.adapters.TextAdapter.SimpleCursorLoader;
import com.doubtech.universalremote.providers.BaseAbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.providerdo.ProviderDetails;

public class ProviderAdapter extends BaseAdapter {
    private static final String TAG = "UniversalRemote : ProviderAdapter";
    private ArrayList<ProviderDetails> mProviders;
    private Context mContext;

    public ProviderAdapter(Context context) {
        mContext = context;
        List<ProviderInfo> providers = context.getPackageManager()
                .queryContentProviders(null, 0, 0);
        mProviders = new ArrayList<ProviderDetails>();
        for (ProviderInfo provider : providers) {
            if ("com.doubtech.universalremote.PROVIDE_BUTTONS".equals(provider.readPermission)) {
                try {
                    ProviderDetails details = BaseAbstractUniversalRemoteProvider.queryProviderDetails(context, provider.authority);

                    Log.d("AARON", "" + details);
                    if (null != details && details.isEnabled()) {
                        mProviders.add(details);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error retreiving provider info for " + provider.authority + " (" + e.getMessage() + ")");
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mProviders.size();
    }

    @Override
    public Object getItem(int position) {
        final String authority = mProviders.get(position).getAuthority();
        SimpleCursorLoader loader = new SimpleCursorLoader() {
			@Override
			public Cursor loadCursor() {
				return BaseAbstractUniversalRemoteProvider.queryBrands(mContext, authority);
			}
		};
        return new TextAdapter(
                mContext,
                null,
                URPContract.TABLE_BRANDS,
                loader,
                URPContract.COLUMN_AUTHORITY,
                URPContract.Brands.COLUMN_BRAND_ID,
                URPContract.Brands.COLUMN_NAME,
                ((RemotePageConfiguration)mContext).getRequestChildListener());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {

        private TextView mLabel;
        private TextView mDescription;

        public ViewHolder(View convertView) {
            mLabel = (TextView) convertView.findViewById(R.id.label);
            mDescription = (TextView) convertView.findViewById(R.id.description);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.descriptive_text_view, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ProviderDetails provider = mProviders.get(position);
        holder.mLabel.setText(provider.getName());
        holder.mDescription.setText(provider.getDescription());
        return convertView;
    }

}
