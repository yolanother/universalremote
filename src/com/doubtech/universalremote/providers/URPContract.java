package com.doubtech.universalremote.providers;

import android.net.Uri;

public final class URPContract {
	public static final String TABLE_BRANDS_PATH = "brands";
	public static final String TABLE_MODELS_PATH = "models";
	public static final String TABLE_BUTTONS_PATH = "buttons";
	public static final String TABLE_BUTTON_LAYOUT_PATH = "layouts";

	public static final String COLUMN_AUTHORITY = "authority";
	
	public static final int TABLE_UNKNOWN = 0;
	public static final int TABLE_BRANDS = 1;
	public static final int TABLE_MODELS = 2;
	public static final int TABLE_BUTTONS = 3;
	public static final int TABLE_BUTTON_LAYOUT = 4;

	public static final String QUERY_PARAMETER_PARENT = "parent";
	public static final String QUERY_PARAMETER_BUTTON_ID = "button";

	public static final String BUTTON_COMMAND_SEND = "send";

	public static final class Brands {
		public static final int COLIDX_AUTHORITY = 0;
		public static final int COLIDX_ID = 1;
		public static final int COLIDX_NAME = 2;
		public static final int COLIDX_LOGO = 3;

		public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_LOGO = "logo";
		
		public static final String[] ALL = new String[] {
			COLUMN_AUTHORITY,
			COLUMN_ID,
			COLUMN_NAME,
			COLUMN_LOGO
		};
	}

	public static final class Models {
		public static final int COLIDX_AUTHORITY = 0;
		public static final int COLIDX_ID = 1;
		public static final int COLIDX_BRAND_ID = 2;
		public static final int COLIDX_NAME = 2;

		public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_BRAND_ID = "brand_id";
		
		public static final String[] ALL = new String[] {
			COLUMN_AUTHORITY,
			COLUMN_ID,
			COLUMN_BRAND_ID,
			COLUMN_NAME
		};
	}

	public static final class Buttons {
		public static final int ICON_RESOURCE_TYPE_NO_ICON = 0;
		/**
		 * An icon resource to be opened with package manager
		 */
		public static final int ICON_RESOURCE_TYPE_RESOURCE = 1;
		/**
		 * An icon resource to be opened with a provider and file descriptor
		 */
		public static final int ICON_RESOURCE_TYPE_PROVIDER_URI = 2;
		
		public static final int COLIDX_AUTHORITY = 0;
		public static final int COLIDX_ID = 1;
		public static final int COLIDX_MODEL_ID = 2;
		public static final int COLIDX_NAME = 3;
		public static final int COLIDX_BUTTON_IDENTIFIER = 4;

		public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_MODEL_ID = "model_id";
		public static final String COLUMN_BUTTON_IDENTIFIER = "button_identifier";
		
		public static final String[] ALL = new String[] {
			COLUMN_AUTHORITY,
			COLUMN_ID,
			COLUMN_MODEL_ID,
			COLUMN_NAME,
			COLUMN_BUTTON_IDENTIFIER
		};
	}

	public static Uri getBrandsUri(String authority) {
		return Uri.parse("content://" + authority + "/" + URPContract.TABLE_BRANDS_PATH);
	}

	public static Uri getModelsUri(String authority) {
		return Uri.parse("content://" + authority + "/" + URPContract.TABLE_MODELS_PATH);
	}

	public static Uri getButtonsUri(String authority) {
		return Uri.parse("content://" + authority + "/" + URPContract.TABLE_BUTTONS_PATH);
	}

	public static Uri getButtonUri(String authority, String id) {
		return Uri.parse("content://" + authority + "/" + URPContract.TABLE_BUTTONS_PATH)
				.buildUpon()
				.appendQueryParameter(QUERY_PARAMETER_BUTTON_ID, id)
				.build();
	}
}
