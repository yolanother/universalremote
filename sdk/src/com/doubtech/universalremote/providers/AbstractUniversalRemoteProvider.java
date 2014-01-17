package com.doubtech.universalremote.providers;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.doubtech.universalremote.providers.URPContract.Models;
import com.doubtech.universalremote.providers.providerdo.Brand;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Model;

public abstract class AbstractUniversalRemoteProvider extends BaseAbstractUniversalRemoteProvider {
    /**
     * Send the button commands through the necessary hardware
     * @param buttons The collection of button commands to send
     */
    public abstract Button[] sendButtons(Button[] buttons);

    @Override
    final public String getButtonsColNameModelId() {
        return URPContract.Buttons.COLUMN_MODEL_ID;
    }

    @Override
    final public String getButtonsColNameId() {
        return URPContract.Buttons.COLUMN_BUTTON_ID;
    }

    @Override
    final public String getButtonsColNameButtonName() {
        return URPContract.Buttons.COLUMN_NAME;
    }

    /**
     * Get all buttons for a particular model of a brand.
     * @param brandId The brand the model belongs to
     * @param modelId The model the buttons belong to
     * @return
     */
    public abstract Button[] getButtons(String brandId, String modelId);

    /**
     * Get full details for a set of buttons with known brand, model, and button id
     * @param buttons The set of buttons to get more details for
     * @return
     */
    public abstract Button[] getButtons(Button[] buttons);

    @Override
    final protected Cursor getButtons(String[] projection, String brandId,
            String modelId, String[] buttonIds, String sortOrder) {
        MatrixCursor buttons = new MatrixCursor(URPContract.Buttons.ALL);
        Button[] btns;
        if (null != buttonIds && buttonIds.length > 0) {
            btns = new Button[buttonIds.length];
            int i = 0;
            for (String id : buttonIds) {
                btns[i++] = new Button(getAuthority(), brandId, modelId, id);
            }
            // Get the details for all requested buttons
            btns = getButtons(btns);
        } else {
            btns = getButtons(brandId, modelId);
        }

        for (Button button : getButtons(btns)) {
            if (null != button) {
                buttons.addRow(button.toRow());
            }
        }
        return buttons;
    }

    @Override
    final public String getBrandColNameBrandName() {
        return URPContract.Brands.COLUMN_NAME;
    }

    @Override
    final public String getBrandColNameId() {
        return URPContract.Brands.COLUMN_BRAND_ID;
    }

    public abstract Brand[] getBrands();

    @Override
    final protected Cursor getBrands(String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(URPContract.Brands.ALL);
        for (Brand brand : getBrands()) {
            if (null != brand) {
                cursor.addRow(brand.toRow());
            }
        }
        return cursor;
    }

    @Override
    final public String getModelColNameModelName() {
        return Models.COLUMN_NAME;
    }

    @Override
    final public String getModelColNameId() {
        return Models.COLUMN_MODEL_ID;
    }

    @Override
    final public String getModelColNameBrandId() {
        return Models.COLUMN_BRAND_ID;
    }

    public abstract Model[] getModels(String brandId);

    @Override
    final protected Cursor getModels(String[] projection, String brandId,
            String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(URPContract.Models.ALL);
        for (Model brand : getModels(brandId)) {
            if (null != brand) {
                cursor.addRow(brand.toRow());
            }
        }
        return cursor;
    }
}
