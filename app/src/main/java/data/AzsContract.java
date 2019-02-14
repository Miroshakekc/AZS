package data;

import android.net.Uri;
import android.provider.BaseColumns;

public class AzsContract {
    public static final String CONTENT_AUTHORITY = "com.example.android-azs";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MARK = "Mark";

    private AzsContract(){}
    public static final class AZS implements BaseColumns{
        public static final String TABLE_MARK = "Mark";
        public static final String TABLE_MARKRTREE = "MarkRTree";
        public static final String COLUMN_NAME = "mName";
        public static final String LAT = "mBottom";
        public static final String LON = "mLeft";
        public static final String COLUMN_ROWID = "RowID";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ID = "ID";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MARK);

    }
}
