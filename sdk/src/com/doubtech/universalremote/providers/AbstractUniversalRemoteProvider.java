package com.doubtech.universalremote.providers;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.doubtech.universalremote.providers.URPContract.Models;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Brands;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Buttons;
import com.doubtech.universalremote.providers.providerdo.Brand;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Model;

public abstract class AbstractUniversalRemoteProvider extends BaseAbstractUniversalRemoteProvider {
	/**
	 * Send the button commands through the necessary hardware
	 * @param buttons The collection of button commands to send
	 */
	public abstract void sendButtons(Button[] buttons);

	@Override
	final public Cursor sendButtons(Cursor cursor) {
		Button[] buttons = new Button[cursor.getCount()];
		if(cursor.moveToFirst()) {
			for(int i = 0; i < buttons.length; i++) {
				buttons[i] = Button.fromCursor(this, getAuthority(), cursor);
				cursor.moveToNext();
			}
		}
		sendButtons(buttons);
		return cursor;
	}

    @Override
    final public String getButtonsColNameModelId() {
        return Buttons.Columns.RemoteId;
    }

    @Override
    final public String getButtonsColNameId() {
        return Buttons.Columns.ButtonId;
    }

    @Override
    final public String getButtonsColNameButtonName() {
        return Buttons.Columns.ButtonName;
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
		MatrixCursor buttons = new MatrixCursor(Buttons.Columns.ALL);
		Button[] btns;
		if(null != buttonIds && buttonIds.length > 0) {
			btns = new Button[buttonIds.length];
			int i = 0;
			for(String id : buttonIds) {
				btns[i++] = new Button(getAuthority(), brandId, modelId, id);
			}
			// Get the details for all requested buttons
			btns = getButtons(btns);
		} else {
			btns = getButtons(brandId, modelId);
		}

		for(Button button : getButtons(btns)) {
			buttons.addRow(button.toRow());
		}
		return buttons;
	}

	@Override
	final public String getBrandColNameBrandName() {
		return Brands.Columns.BrandName;
	}

	@Override
	final public String getBrandColNameId() {
		return Brands.Columns.BrandID;
	}
	
	public abstract Brand[] getBrands();

	@Override
	final protected Cursor getBrands(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = new MatrixCursor(Brands.Columns.ALL);
		for(Brand brand : getBrands()) {
			cursor.addRow(brand.toRow());
		}
		return cursor;
	}

	@Override
	final public String getModelColNameModelName() {
		return Models.COLUMN_NAME;
	}

	@Override
	final public String getModelColNameId() {
		return Models.COLUMN_ID;
	}

	@Override
	final public String getModelColNameBrandId() {
		return Models.COLUMN_BRAND_ID;
	}
	
	public abstract Model[] getModels(String brandId);

	@Override
	final protected Cursor getModels(String[] projection, String brandId,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = new MatrixCursor(Brands.Columns.ALL);
		for(Model brand : getModels(brandId)) {
			cursor.addRow(brand.toRow());
		}
		return cursor;
	}
}
