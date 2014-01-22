package com.doubtech.universalremote.providers.irremotes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.jsonretreivers.HttpJsonRetreiver;
import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.AbstractJsonIRUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.ButtonStyler;

public class RemoteCentralProvider extends AbstractJsonIRUniversalRemoteProvider {
    private static class Retreiver extends HttpJsonRetreiver {

        public Retreiver(Context context, File cache) {
            super(context, cache);
        }

        @Override
        protected URL getUrl(Parent parent) {
            try {
                return new URL("http://ir.doubtech.com/json.php/remotecentral" + parent.getPathString());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url generated. Should never get here.");
            }
        }

    }

    private Retreiver mRetreiver;

    @Override
    public String getProviderName() {
        return getContext().getString(R.string.remote_central_provider_name);
    }

    @Override
    public String getProviderDescription() {
        return getContext().getString(R.string.remote_central_provider_desc);
    }

    @Override
    public String getAuthority() {
        return "com.doubtech.universalremote.providers.irremotes.RemoteCentral";
    }

    @Override
    public JsonRetreiver getJsonRetreiver() {
        if (null == mRetreiver) {
            mRetreiver = new Retreiver(getContext(), new File(getContext().getCacheDir(), "remotecentral"));
        }
        return mRetreiver;
    }

    @Override
    public int getIconId(Parent button) {
        return ButtonStyler.getIconId(button.getName());
    }
}
