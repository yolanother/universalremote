package com.doubtech.universalremote.providers.irremotes;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DataProviderContract implements BaseColumns {
    private DataProviderContract() { }

    // The URI scheme used for content URIs
    public static final String SCHEME = "content";

    // The provider's authority
    public static final String AUTHORITY = "com.samsung.ssl.universalremote";

    /**
     * The DataProvider content URI
     */
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

    /**
     *  The MIME type for a content URI that would return multiple rows
     *  <P>Type: TEXT</P>
     */
    public static final String MIME_TYPE_ROWS =
            "vnd.android.cursor.dir/vnd.com.samsung.ssl.universalremote";

    /**
     * The MIME type for a content URI that would return a single row
     *  <P>Type: TEXT</P>
     *
     */
    public static final String MIME_TYPE_SINGLE_ROW =
            "vnd.android.cursor.item/vnd.com.samsung.ssl.universalremote";

    public static class Tables {
        public static class Brands {
            public static final String TABLE_NAME = "Brands";

            public static class Columns {
                public static String BrandID = BaseColumns._ID;
                public static String BrandName = "BrandName";

                public static final String[] ALL = new String[] {
                    BrandID,
                    BrandName
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "TEXT"
                };

                public static int PROJECTION_BRAND_ID = 0;
                public static int PROJECTION_BRAND_NAME = 1;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class Remotes {
            public static final String TABLE_NAME = "Remotes";

            public static class Columns {
                public static String RemoteId = BaseColumns._ID;
                public static String BrandId = "BrandId";
                public static String RemoteName = "RemoteName";
                public static String Frequency = "Frequency";

                public static final String[] ALL = new String[] {
                    RemoteId,
                    BrandId,
                    RemoteName,
                    Frequency
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "INTEGER",
                    "TEXT",
                    "INTEGER"
                };

                public static int PROJECTION_REMOTE_ID = 0;
                public static int PROJECTION_BRAND_ID = 1;
                public static int PROJECTION_REMOTE_NAME = 2;
                public static int PROJECTION_FREQUENCY = 3;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class Buttons {
            public static final String TABLE_NAME = "Buttons";

            public static class Columns {
                public static String ButtonId = BaseColumns._ID;
                public static String RemoteId = "RemoteId";
                public static String BrandId = "BrandId";
                public static String ButtonName = "ButtonName";
                public static String ButtonCode = "ButtonCode";
                public static String ButtonLabel = "ButtonLabel";
                public static String ButtonIcon = "ButtonIcon";

                public static final String[] ALL = new String[] {
                    ButtonId,
                    RemoteId,
                    BrandId,
                    ButtonName,
                    ButtonCode,
                    ButtonLabel,
                    ButtonIcon
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "VARCHAR(50)",
                    "VARCHAR(150)",
                    "VARCHAR(150)",
                    "VARCHAR(150)",
                    "CHAR(1)",
                    "CHAR(1)"
                };

                public static int PROJECTION_BUTTON_ID = 0;
                public static int PROJECTION_REMOTE_ID = 1;
                public static int PROJECTION_BRAND_ID = 2;
                public static int PROJECTION_BUTTON_NAME = 3;
                public static int PROJECTION_BUTTON_CODE = 4;
                public static int PROJECTION_BUTTON_LABEL = 5;
                public static int PROJECTION_BUTTON_ICON = 6;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }
    }

    public static final Uri TABLE_CONTENTURI_BRANDS = Uri.withAppendedPath(CONTENT_URI, Tables.Brands.TABLE_NAME);
    public static final Uri TABLE_CONTENTURI_SET_OF_CODES = Uri.withAppendedPath(CONTENT_URI, Tables.Remotes.TABLE_NAME);
    public static final Uri TABLE_CONTENTURI_IR_CODES = Uri.withAppendedPath(CONTENT_URI, Tables.Buttons.TABLE_NAME);
}
