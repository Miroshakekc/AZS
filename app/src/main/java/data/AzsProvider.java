package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;

import org.sqlite.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AzsProvider extends ContentProvider {
    private AzsDbHelper azsDbHelper;
    SQLiteDatabase db;
    private static final int AZS_ID = 100;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(AzsContract.CONTENT_AUTHORITY, AzsContract.PATH_MARK + "/#", AZS_ID );

    }

    @Override
    public boolean onCreate() {
        azsDbHelper = new AzsDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {

        azsDbHelper = new AzsDbHelper(getContext());
        azsDbHelper.create_db();

        db = azsDbHelper.open();

//        projection = new String[]{AzsContract.AZS.COLUMN_ROWID, AzsContract.AZS.LAT, AzsContract.AZS.LON};
        selection = AzsContract.AZS._ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        Cursor cursor = db.query(
                AzsContract.AZS.TABLE_MARK,
                projection,
                selection,
                selectionArgs,
                null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
