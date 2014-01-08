package com.doubtech.universalremote.providers.irremotes.o1r;

import android.net.Uri;
import android.provider.BaseColumns;

public final class O1rDataProviderContract implements BaseColumns {
    private O1rDataProviderContract() { }

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
                    "VARCHAR(50)"
                };

                public static int PROJECTION_BRAND_ID = 0;
                public static int PROJECTION_BRAND_NAME = 1;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class CodeLink {
            public static final String TABLE_NAME = "CodeLink";

            public static class Columns {
                public static String CodeLinkID = BaseColumns._ID;
                public static String SetOfCodesID = "SetOfCodesID";
                public static String CodeID = "CodeID";
                public static String UniversalNameID = "UniversalNameID";
                public static String FunctionName = "FunctionName";
                public static String Duration = "Duration";
                public static String Ratings = "Ratings";
                public static String RepeatFlag = "RepeatFlag";

                public static final String[] ALL = new String[] {
                    CodeLinkID,
                    SetOfCodesID,
                    CodeID,
                    UniversalNameID,
                    FunctionName,
                    Duration,
                    Ratings,
                    RepeatFlag
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "VARCHAR(50)",
                    "INTEGER",
                    "CHAR",
                    "CHAR(1)"
                };

                public static int PROJECTION_CODE_LINK_ID = 0;
                public static int PROJECTION_SET_OF_CODES_ID = 1;
                public static int PROJECTION_CODE_ID = 2;
                public static int PROJECTION_UNIVERSAL_NAME_ID = 3;
                public static int PROJECTION_FUNCTION_NAME = 4;
                public static int PROJECTION_DURATION = 5;
                public static int PROJECTION_RATINGS = 6;
                public static int PROJECTION_REPEAT_FLAG = 7;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class Codes {
            public static final String TABLE_NAME = "Codes";

            public static class Columns {
                public static String CodeID = BaseColumns._ID;
                public static String Source = "Source";
                public static String IRCode = "IRCode";
                public static String SerialCode = "SerialCode";
                public static String EthernetCode = "EthernetCode";
                public static String IsRCSCode = "IsRCSCode";
                public static String SerialCodeType = "SerialCodeType";

                public static final String[] ALL = new String[] {
                    CodeID,
                    Source,
                    IRCode,
                    SerialCode,
                    EthernetCode,
                    IsRCSCode,
                    SerialCodeType
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

                public static int PROJECTION_CODE_ID = 0;
                public static int PROJECTION_SOURCe = 1;
                public static int PROJECTION_IR_CODE = 2;
                public static int PROJECTION_SERIAL_CODE = 3;
                public static int PROJECTION_ETHERNET_CODE = 4;
                public static int PROJECTION_IS_RCS_CODE = 5;
                public static int PROJECTION_SERIAL_CODE_TYPE = 6;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class DeviceTypes {
            public static final String TABLE_NAME = "DeviceTypes";

            public static class Columns {
                public static String TypeID = BaseColumns._ID;
                public static String DeviceName = "DeviceName";

                public static final String[] ALL = new String[] {
                    TypeID,
                    DeviceName
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "VARCHAR(50)"
                };

                public static int PROJECTION_DEVICE_ID = 0;
                public static int PROJECTION_DEVICE_NAME = 1;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }

        public static class SetOfCodes {
            public static final String TABLE_NAME = "SetOfCodes";

            public static class Columns {
                public static String SetOfCodesID = BaseColumns._ID;
                public static String BrandID = "BrandID";
                public static String TypeID = "TypeID";
                public static String ModelName = "ModelName";
                public static String IsModel = "IsModel";
                public static String GenericDelay = "GenericDelay";
                public static String SpecificDelay = "SpecificDelay";
                public static String StartBits = "StartBits";
                public static String StopBits = "StopBits";
                public static String Parity = "Parity";
                public static String Port = "Port";
                public static String BaudRate = "BaudRate";
                public static String Author = "Author";
                public static String CodeDate = "CodeDate";
                public static String Approved = "Approved";
                public static String Version = "Version";
                public static String IsDelete = "IsDelete";
                public static String ControlType = "ControlType";

                public static final String[] ALL = new String[] {
                    SetOfCodesID,
                    BrandID,
                    TypeID,
                    ModelName,
                    IsModel,
                    GenericDelay,
                    SpecificDelay,
                    StartBits,
                    StopBits,
                    Parity,
                    Port,
                    BaudRate,
                    Author,
                    CodeDate,
                    Approved,
                    Version,
                    IsDelete,
                    ControlType
                };

                public static final String[] ALL_TYPES = new String[] {
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "VARCHAR(50)",
                    "CHAR",
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "INTEGER",
                    "VARCHAR(15)",
                    "VARCHAR(15)",
                    "VARCHAR(25)",
                    "INTEGER",
                    "CHAR",
                    "VARCHAR",
                    "CHAR",
                    "VARCHAR(2)"
                };

                public static int PROJECTION_SET_OF_CODES_ID = 0;
                public static int PROJECTION_BRAND_ID = 1;
                public static int PROJECTION_TYPE_ID = 2;
                public static int PROJECTION_MODEL_NAME = 3;
                public static int PROJECTION_IS_MODEL = 4;
                public static int PROJECTION_GENERIC_DELAY = 5;
                public static int PROJECTION_SPECIFIC_DELAY = 6;
                public static int PROJECTION_START_BITS = 7;
                public static int PROJECTION_STOP_BITS = 8;
                public static int PROJECTION_PARITY = 9;
                public static int PROJECTION_PORT = 10;
                public static int PROJECTION_BAUD_RATE = 11;
                public static int PROJECTION_AUTHOR = 12;
                public static int PROJECTION_CODE_DATE = 13;
                public static int PROJECTION_APPROVED = 14;
                public static int PROJECTION_VERSION = 15;
                public static int PROJECTION_IS_DELETE = 16;
                public static int PROJECTION_CONTROL_TYPE = 17;
            }

            @Override
            public String toString() {
                return TABLE_NAME;
            }
        }
    }

    public static final class Views {


        public static class IRCodes {
            public static final String VIEW_NAME = "IrCodes";

            public static final String CREATION_QUERY =
                "CREATE VIEW" +
                        " IrCodes as" +
                        "    select * from Codes a, CodeLink b where IRCode is not null and a._id=CodeId;";

            public static class Columns {
                public static String CodeLinkID = BaseColumns._ID;
                public static String SetOfCodesID = "SetOfCodesID";
                public static String CodeID = "CodeID";
                public static String UniversalNameID = "UniversalNameID";
                public static String FunctionName = "FunctionName";
                public static String Duration = "Duration";
                public static String Ratings = "Ratings";
                public static String RepeatFlag = "RepeatFlag";
                public static String IRCode = "IRCode";

                public static final String[] ALL = new String[] {
                    CodeLinkID,
                    SetOfCodesID,
                    CodeID,
                    UniversalNameID,
                    FunctionName,
                    Duration,
                    Ratings,
                    RepeatFlag,
                    IRCode
                };

                public static int PROJECTION_CODE_LINK_ID = 0;
                public static int PROJECTION_SET_OF_CODES_ID = 1;
                public static int PROJECTION_CODE_ID = 2;
                public static int PROJECTION_UNIVERSAL_NAME_ID = 3;
                public static int PROJECTION_FUNCTION_NAME = 4;
                public static int PROJECTION_DURATION = 5;
                public static int PROJECTION_RATINGS = 6;
                public static int PROJECTION_REPEAT_FLAG = 7;
                public static int PROJECTION_IR_CODE = 8;
            }

            @Override
            public String toString() {
                return VIEW_NAME;
            }
        }
    }

    public static final Uri TABLE_CONTENTURI_BRANDS = Uri.withAppendedPath(CONTENT_URI, Tables.Brands.TABLE_NAME);
    public static final Uri TABLE_CONTENTURI_SET_OF_CODES = Uri.withAppendedPath(CONTENT_URI, Tables.SetOfCodes.TABLE_NAME);
    public static final Uri TABLE_CONTENTURI_IR_CODES = Uri.withAppendedPath(CONTENT_URI, Views.IRCodes.VIEW_NAME);
}
