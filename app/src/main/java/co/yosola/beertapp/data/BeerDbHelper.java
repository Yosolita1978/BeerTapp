package co.yosola.beertapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.yosola.beertapp.data.BeerContract.BeerEntry;

public class BeerDbHelper extends SQLiteOpenHelper {

    // For debugging process
    public static final String LOG_TAG = BeerDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "brewery.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;


    // Constructor
    public BeerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BEER_TABLE = "CREATE TABLE " + BeerEntry.TABLE_NAME + " ("
                + BeerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BeerEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + BeerEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BeerEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BeerEntry.COLUMN_TYPE_BOTTLE + " INTEGER NOT NULL DEFAULT 0, "
                + BeerEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BeerEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";
        db.execSQL(SQL_CREATE_BEER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
