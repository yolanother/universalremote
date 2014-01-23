package com.doubtech.universalremote.providers;

import android.net.Uri;

import com.doubtech.universalremote.providers.providerdo.Parent;

public final class URPContract {
    public static final String TABLE_BRANDS_PATH = "brands";
    public static final String TABLE_MODELS_PATH = "models";
    public static final String TABLE_BUTTONS_PATH = "buttons";
    public static final String TABLE_BUTTON_LAYOUT_PATH = "layouts";
    public static final String TABLE_PROVIDER_DETAILS_PATH = "details";

    public static final String COLUMN_AUTHORITY = "authority";

    public static final int TABLE_UNKNOWN = 0;
    public static final int TABLE_BRANDS = 1;
    public static final int TABLE_MODELS = 2;
    public static final int TABLE_BUTTONS = 3;
    public static final int TABLE_BUTTON_LAYOUT = 4;
    public static final int TABLE_PROVIDER_DETAILS = 5;

    public static final String QUERY_PARAMETER_BUTTON_ID = "button";

    public static final String BUTTON_COMMAND_SEND = "send";
    public static final String QUERY_PARAMETER_BRANDID = "brandId";
    public static final String QUERY_PARAMETER_MODELID = "modelId";

    public static final class ProviderDetails {
        public static final int COLIDX_AUTHORITY = 0;
        public static final int COLIDX_NAME = 1;
        public static final int COLIDX_DESCRIPTION = 2;
        public static final int COLIDX_IS_ENABLED = 3;

        public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_IS_ENABLED = "enabled";

        public static final String[] ALL = new String[] {
            COLUMN_AUTHORITY,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_IS_ENABLED
        };
    }

    public static final class Parents {
        public static final int COLIDX_ID = 0;
        public static final int COLIDX_AUTHORITY = 1;
        public static final int COLIDX_PATH = 2;
        public static final int COLIDX_LEVEL_NAME = 3;
        public static final int COLIDX_NAME = 4;
        public static final int COLIDX_DESCRIPTION = 5;
        public static final int COLIDX_TYPE = 6;
        public static final int COLIDX_HAS_BUTTONSETS = 7;

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_HAS_BUTTONSETS = "hasButtonSets";

        public static final String TYPE_PARENT = "parent";
        public static final String TYPE_BUTTON = "button";

        public static final String[] ALL = new String[] {
            COLUMN_ID,
            COLUMN_AUTHORITY,
            COLUMN_PATH,
            COLUMN_LEVEL,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_TYPE,
            COLUMN_HAS_BUTTONSETS
        };
    }

    public static final class Buttons {
        public static final int COLIDX_ID = 0;
        public static final int COLIDX_AUTHORITY = 1;
        public static final int COLIDX_PATH = 2;
        public static final int COLIDX_LEVEL_NAME = 3;
        public static final int COLIDX_NAME = 4;
        public static final int COLIDX_DESCRIPTION = 5;
        public static final int COLIDX_TYPE = 6;
        public static final int COLIDX_HAS_BUTTONSETS = 7;
        public static final int COLIDX_BUTTON_IDENTIFIER = 8;

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_AUTHORITY = URPContract.COLUMN_AUTHORITY;
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_BUTTON_IDENTIFIER = "button_identifier";
        public static final String COLUMN_HAS_BUTTONSETS = "hasButtonSets";

        public static final String[] ALL = new String[] {
            COLUMN_ID,
            COLUMN_AUTHORITY,
            COLUMN_PATH,
            COLUMN_LEVEL,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_TYPE,
            COLUMN_HAS_BUTTONSETS,
            COLUMN_BUTTON_IDENTIFIER
        };
    }

    public static Uri getUri(String authority, String table, String[] path) {
        Uri.Builder builder = Uri.parse("content://" + authority)
                .buildUpon();
        builder.appendPath(table);
        for (String segment : path) {
            builder.appendPath(segment);
        }
        return builder.build();
    }

    public static Uri getProviderDetailsUri(String authority) {
        return Uri.parse("content://" + authority + "/" + URPContract.TABLE_PROVIDER_DETAILS_PATH);
    }
}
