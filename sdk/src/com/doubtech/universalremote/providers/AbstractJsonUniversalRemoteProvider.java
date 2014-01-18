package com.doubtech.universalremote.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.providerdo.Brand;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Model;

public abstract class AbstractJsonUniversalRemoteProvider extends
        AbstractUniversalRemoteProvider {
    public static final String TAG = "UniversalRemote : AbstractJsonURP";

    @Override
    public Button[] getButtons(String brandId, String modelId) {
        try {
            Button[] buttons = Button.fromJson(
                    this,
                    getAuthority(),
                    getJsonRetreiver().getButtonsJson(brandId, modelId));
            return buttons;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new Button[0];
    }

    @Override
    public Button[] getButtons(Button[] buttons) {
        String lastBrandCache = null;
        String lastModelCache = null;

        for (int i = 0; i < buttons.length; i++) {
            Button b =  Button.getCachedButton(buttons[i]);
            if (null == b) {
                String brand = buttons[i].getBrandId();
                String model = buttons[i].getModelId();
                if (!brand.equals(lastBrandCache) && !model.equals(lastModelCache)) {
                    // TODO There is still some potential for duplicate calls to
                    // getButtons...
                    getButtons(brand, model);
                    lastBrandCache = brand;
                    lastModelCache = model;
                }
                b = Button.getCachedButton(buttons[i]);
                if (null != b) {
                    buttons[i] = b;
                }
            } else {
                buttons[i] = b;
            }
        }
        return buttons;
    }

    @Override
    public Brand[] getBrands() {
        try {
            return Brand.fromJson(getContext(), getAuthority(), getJsonRetreiver().getBrandsJson());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new Brand[0];
    }

    @Override
    public Model[] getModels(String brandId) {
        try {
            return Model.fromJson(getContext(), getAuthority(), getJsonRetreiver().getModelsJson(brandId));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new Model[0];
    }

    /**
     * Gets any extra data needed to send an ir code. This data is completely
     * internal to the provider and will not be shared back to the main app
     * @param buttonData
     * @return Returns a hash map with any extra data the provider may need later
     */
    public abstract void onPutExtras(Button button,
            JSONObject buttonData) throws JSONException;

    public abstract JsonRetreiver getJsonRetreiver();
}
