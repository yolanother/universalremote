package com.doubtech.universalremote.providers.irremotes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.jsonretreivers.HttpJsonRetreiver;
import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.AbstractJsonUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.ButtonStyler;

public class GlobalCacheProvider extends AbstractJsonUniversalRemoteProvider {
    private static class Retreiver extends HttpJsonRetreiver {

        public Retreiver(Context context, File cache) {
            super(context, cache);
        }

        @Override
        protected URL getUrl(Parent parent) {
            try {
                return new URL("http://ir.doubtech.com/json.php/globalcache" + parent.getPathString());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url generated. Should never get here.");
            }
        }
    }

    private Retreiver mRetreiver;

    @Override
    public String getProviderName() {
        return getContext().getString(R.string.globalcache_provider_name);
    }

    @Override
    public String getProviderDescription() {
        return getContext().getString(R.string.globalcache_provider_desc);
    }

    @Override
    public String getAuthority() {
        return "com.doubtech.universalremote.providers.irremotes.GlobalCache";
    }

    @Override
    public JsonRetreiver getJsonRetreiver() {
        if (null == mRetreiver) {
            mRetreiver = new Retreiver(getContext(), new File(getContext().getCacheDir(), "globalcache"));
        }
        return mRetreiver;
    }

    @Override
    public int getIconId(Parent button) {
        return ButtonStyler.getIconId(button.getName());
    }

    @Override
    public Button[] sendButtons(Button[] buttons) {
        IrManager manager = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            String buttonData = button.getInternalData("buttonCode");
            String[] code = buttonData.split(",");
            int[] timings = new int[code.length - 1];
            for (int i = 0; i < timings.length; i++) {
                timings[i] = Integer.parseInt(code[i + 1]);
            }
            manager.transmit(Integer.parseInt(code[0]), timings);
        }
        return buttons;
    }

    @Override
    public boolean isProviderEnabled() {
        return IrManager.isSupported(getContext());
    }
}
