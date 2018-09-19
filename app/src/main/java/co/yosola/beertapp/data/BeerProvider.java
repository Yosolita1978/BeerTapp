package co.yosola.beertapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BeerProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = BeerProvider.class.getSimpleName();

    /** Database helper object */
    private BeerDbHelper mDbHelper;

    /** URI matcher code for the content URI for the beers table */
    private static final int BEERS = 100;

    /** URI matcher code for the content URI for a single beer in the beers table */
    private static final int BEER_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEERS, BEERS);

        sUriMatcher.addURI(BeerContract.CONTENT_AUTHORITY, BeerContract.PATH_BEERS + "/#", BEER_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BeerDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                // For the BEERS code, query the beers table directly.
                cursor = database.query(BeerContract.BeerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BEER_ID:
                // For the BEER_ID code, extract out the ID from the URI.
                selection = BeerContract.BeerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BeerContract.BeerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return insertBeer(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a beer into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBeer(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(BeerContract.BeerEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The beer requires a name");
        }

        // Sanity checking.
        // If the price is provided, check that it's greater than or equal to $0
        Integer price = values.getAsInteger(BeerContract.BeerEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("The beer requires valid price");
        }

        // Sanity checking.
        // check that quantity is greater than or equal to 0 beers.
        Integer quantity = values.getAsInteger(BeerContract.BeerEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("The beer requires valid quantity");
        }

        // Sanity checking.
        // Check that the genre of the book. It is stored as an integer.
        Integer typeofbottle = values.getAsInteger(BeerContract.BeerEntry.COLUMN_TYPE_BOTTLE);
        if (typeofbottle == null || !BeerContract.BeerEntry.isValidBootle(typeofbottle)) {
            throw new IllegalArgumentException("The beer requires valid type of Bootle");
        }

        // Check that the supplier's name is not null.
        String nameSupplier = values.getAsString(BeerContract.BeerEntry.COLUMN_SUPPLIER_NAME);
        if (TextUtils.isEmpty(nameSupplier)) {
            throw new IllegalArgumentException("The beer requires a supplier's name");
        }

        // Check that the supplier's phone is not null.
        String phoneSupplier = values.getAsString(BeerContract.BeerEntry.COLUMN_SUPPLIER_PHONE);
        if (phoneSupplier == null) {
            throw new IllegalArgumentException("The beer requires a supplier's phone");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BeerContract.BeerEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
