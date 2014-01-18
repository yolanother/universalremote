package com.doubtech.universalremote.providers.irremotes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.Uri;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.jsonretreivers.HttpJsonRetreiver;
import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.AbstractJsonIRUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.utils.ButtonStyler;

public class RemoteCentralProvider extends AbstractJsonIRUniversalRemoteProvider {
    private static class Retreiver extends HttpJsonRetreiver {

        public Retreiver(Context context, File cache) {
            super(context, cache);
        }

        @Override
        protected URL getButtonsUrl(String brandId, String modelId) {
            try {
                return new URL("http://ir.doubtech.com/json.php?provider=remotecentral&brandId=" + Uri.encode(brandId) + "&modelId=" + Uri.encode(modelId));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url generated for " + brandId + ", " + modelId);
            }
        }

        @Override
        public URL getModelsUrl(String brandId) {
            try {
                return new URL("http://ir.doubtech.com/json.php?provider=remotecentral&brandId=" + Uri.encode(brandId));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url generated for " + brandId);
            }
        }

        @Override
        public URL getBrandsUrl() {
            try {
                return new URL("http://ir.doubtech.com/json.php?provider=remotecentral");
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
    public int getIconId(Button button) {
        return ButtonStyler.getIconId(button.getName());
    }
}
