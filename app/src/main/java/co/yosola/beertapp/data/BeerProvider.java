package co.yosola.beertapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import co.yosola.beertapp.data.BeerContract.BeerEntry;

public class BeerProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BeerProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the beers table
     */
    private static final int BEERS = 100;
    /**
     * URI matcher code for the content URI for a single beer in the beers table
     */
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
     * Database helper object
     */
    private BeerDbHelper mDbHelper;

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
                cursor = database.query(BeerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BEER_ID:
                // For the BEER_ID code, extract out the ID from the URI.
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BeerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

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
        String name = values.getAsString(BeerEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The beer requires a name");
        }

        // Sanity checking.
        // If the price is provided, check that it's greater than or equal to $0
        Integer price = values.getAsInteger(BeerEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("The beer requires valid price");
        }

        // Sanity checking.
        // check that quantity is greater than or equal to 0 beers.
        Integer quantity = values.getAsInteger(BeerEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("The beer requires valid quantity");
        }

        // Sanity checking.
        // Check that the genre of the book. It is stored as an integer.
        Integer typeofbottle = values.getAsInteger(BeerEntry.COLUMN_TYPE_BOTTLE);
        if (typeofbottle == null || !BeerEntry.isValidBootle(typeofbottle)) {
            throw new IllegalArgumentException("The beer requires valid type of Bootle");
        }

        // Check that the supplier's name is not null.
        String nameSupplier = values.getAsString(BeerEntry.COLUMN_SUPPLIER_NAME);
        if (TextUtils.isEmpty(nameSupplier)) {
            throw new IllegalArgumentException("The beer requires a supplier's name");
        }

        // Check that the supplier's phone is not null.
        String phoneSupplier = values.getAsString(BeerEntry.COLUMN_SUPPLIER_PHONE);
        if (phoneSupplier == null) {
            throw new IllegalArgumentException("The beer requires a supplier's phone");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BeerEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return updateBeer(uri, contentValues, selection, selectionArgs);
            case BEER_ID:
                // For the BER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBeer(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update beers in the database with the given content values. Apply the changes to the rows.
     * Return the number of rows that were successfully updated.
     */
    private int updateBeer(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link BeerEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BeerEntry.COLUMN_NAME)) {
            String name = values.getAsString(BeerEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("The beer requires a name");
            }
        }

        // If the {@link BeerEntry#COLUMN_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(BeerEntry.COLUMN_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer price = values.getAsInteger(BeerEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("The beer requires valid price");
            }
        }

        // If the {@link BeerEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BeerEntry.COLUMN_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(BeerEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("The beer requires valid quantity");
            }
        }

        // If the {@link BeerEntry.COLUMN_TYPE_BOTTLE)} key is present,
        // check that the type of bottle value is valide.
        if (values.containsKey(BeerEntry.COLUMN_TYPE_BOTTLE)) {
            Integer typeBottle = values.getAsInteger(BeerEntry.COLUMN_TYPE_BOTTLE);
            if (typeBottle == null || !BeerEntry.isValidBootle(typeBottle)) {
                throw new IllegalArgumentException("The beer requires valid bottle");
            }
        }

        // If the {@link BeerEntry.COLUMN_SUPPLIER_NAME} key is present,
        // check that the supplier's name value is not null.
        if (values.containsKey(BeerEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(BeerEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("The beer requires a supplier's name");
            }
        }

        // If the {@link BeerEntry.COLUMN_SUPPLIER_PHONE} key is present,
        // check that the supplier's phone value is not null.
        if (values.containsKey(BeerEntry.COLUMN_SUPPLIER_PHONE)) {
            String phone = values.getAsString(BeerEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("The beer requires a supplier's phone");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BeerEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BEER_ID:
                // Delete a single row given by the ID in the URI
                selection = BeerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEERS:
                return BeerEntry.CONTENT_LIST_TYPE;
            case BEER_ID:
                return BeerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
