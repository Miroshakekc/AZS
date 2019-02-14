package com.example.android.azs;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.sqlite.database.sqlite.SQLiteDatabase;

import java.util.List;

import data.AzsContract;
import data.AzsContract.AZS;
import data.AzsDbHelper;

//import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <Cursor> {

    private AzsDbHelper azsDbHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private Cursor userCursor2;
    private EditText userFilter;
    private AzsCursorAdapter azsCursorAdapter;
    private Uri mCurrentAzsUri;
    private String idItem;
    private boolean isFragmentDisplayed = false;
    public static String LAT;
    public static String LOG;
    public static List<ResolveInfo> infos;
    LinearLayout fragmenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary( "sqliteX" );
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.list_view);
        userFilter = findViewById(R.id.userFilter);

        azsDbHelper = new AzsDbHelper(getApplicationContext());
        azsDbHelper.create_db();
        db = azsDbHelper.open();

        String[] projection = {AZS._ID, AZS.COLUMN_NAME};
        userCursor = db.query(
                AZS.TABLE_MARK,
                projection,
                null,
                null,
                null, null, null);
        azsCursorAdapter = new AzsCursorAdapter(this, userCursor);
        listView.setAdapter(azsCursorAdapter);
// фильтрация из адаптера
        if(!userFilter.getText().toString().isEmpty())
            azsCursorAdapter.getFilter().filter(userFilter.getText().toString());
//слушатель для изменения EditorText
        userFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                azsCursorAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//логика фильтрации
        azsCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint != null || constraint.length() != 0) {

                    return db.rawQuery("select * from " + AZS.TABLE_MARK + " where " +
                            AZS.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
                return null;
            }
        });


// при нажатии на listView иницилизирую загрузчик и передаю ему элемент
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//  закрываю клавиатуру при выобре
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                mCurrentAzsUri = ContentUris.withAppendedId(AZS.CONTENT_URI, id);
                if (mCurrentAzsUri != null){
                    getLoaderManager().initLoader(0, null, MainActivity.this);

                    getLoaderManager().restartLoader(0, null, MainActivity.this);

                }


            }
        });
//        для закрытия фрагмента при нажатии на свободное место
        fragmenLayout = findViewById(R.id.linear);
        fragmenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
            }
        });
//      проверка установлен ли яндекс
        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
        intent.setPackage("ru.yandex.yandexnavi");
        PackageManager pm = getPackageManager();
        infos = pm.queryIntentActivities(intent, 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {AzsContract.AZS._ID, AzsContract.AZS.COLUMN_NAME, AzsContract.AZS.COLUMN_ID };
        return new CursorLoader(this, mCurrentAzsUri, projection, null, null, null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int columnID = cursor.getColumnIndex(AzsContract.AZS.COLUMN_ID);
            idItem = cursor.getString(columnID);
            getGeo(idItem);
            displayFragment();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void getGeo (String idItem){
// запрос к базе с айдишкой на которую нажали
        String[] projection2 = {AZS.COLUMN_ROWID, AZS.LAT , AZS.LON};
        String selection2 = AZS.COLUMN_ROWID + "=?";
        String [] selectionArgs2 = new String[]{idItem};
        userCursor2 = db.query(
                AZS.TABLE_MARKRTREE,
                projection2,
                selection2,
                selectionArgs2,
                null, null, null);
        userCursor2.moveToFirst();
        int colomnLAT = userCursor2.getColumnIndex(AZS.LAT);
        int colomnLOG = userCursor2.getColumnIndex(AZS.LON);

        LAT = userCursor2.getString(colomnLAT);
        LOG = userCursor2.getString(colomnLOG);



    }
    public void displayFragment() {
        fragmenLayout = findViewById(R.id.linear);
        GeoFragment geoFragment = GeoFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,geoFragment).addToBackStack(null).commit();
        fragmenLayout.setVisibility(View.VISIBLE);
        isFragmentDisplayed = true;


    }
    public void closeFragment() {
        fragmenLayout = findViewById(R.id.linear);
        FragmentManager fragmentManager = getSupportFragmentManager();
        GeoFragment geoFragment = (GeoFragment)fragmentManager.findFragmentById(R.id.fragment_container);
        if (geoFragment != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(geoFragment).commit();
        }
        fragmenLayout.setVisibility(View.GONE);
        isFragmentDisplayed = false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
        userCursor2.close();
    }

}
