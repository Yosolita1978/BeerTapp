package co.yosola.beertapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import co.yosola.beertapp.data.BeerContract.BeerEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentBeerUri;

    /**
     * Initialize value can be set as any unique integer.
     */
    private static final int CURRENT_BEER_LOADER = 0;

    /** EditText field to enter the beer's name */
    private EditText mNameProductEditText;

    /** EditText field to enter the beer's price */
    private EditText mPriceEditText;

    /** EditText field to enter the beer's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the beer's bottles */
    private Spinner mBottleSpinner;

    /** EditText field to enter the supplier's name */
    private EditText mNameSupplierEditText;

    /** EditText field to enter the supplier's phone */
    private EditText mPhoneSupplierEditText;

    /**
     * Button to increase the quantity of a beer in store
     **/
    private Button mPlusQuantity;

    /**
     * Button to decrease the quantity of a beer in store
     **/
    private Button mMinusQuantity;

    /**
     * Button to call the supplier
     **/
    private Button mPhoneIntent;

    /**
     * Type of the bottles for the beer. The possible values are:
     * 0 for cans, 1 for 12oz, 2 for 16oz, 3 for 22oz.
     */
    private int mBottle = 0;

    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mBeerHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the product.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBeerHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons_quantity);
        mPlusQuantity = (Button) findViewById(R.id.plus_button);
        mMinusQuantity = (Button) findViewById(R.id.minus_button);

        LinearLayout buttonphone = (LinearLayout) findViewById(R.id.buttons_phone);
        mPhoneIntent = (Button) findViewById(R.id.phone_button);

        // examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pat or editing a beer
        Intent intent = getIntent();
        currentBeerUri = intent.getData();

        // IF the intent DOES NOT contain a beer content Uri,
        // then we know that we're creating a new beer

        if (currentBeerUri == null) {
            setTitle(R.string.add_new_beer);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a beer that hasn't been created yet.)
            invalidateOptionsMenu();
            buttons.setVisibility(View.GONE);
            buttonphone.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_beer));
            buttons.setVisibility(View.VISIBLE);
            buttonphone.setVisibility(View.VISIBLE);
            mMinusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    if (currentBeerUri != null) {
                        if (quantity > 0) {
                            quantity--;
                            ContentValues values = new ContentValues();
                            values.put(BeerEntry.COLUMN_QUANTITY, quantity);
                            getContentResolver().update(currentBeerUri, values, null, null);
                        }else {
                            //Quantity is zero and can not be reduced further so alert user with message
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_stock),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            mPlusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());

                    if (currentBeerUri != null) {
                        quantity++;
                        ContentValues values = new ContentValues();
                        values.put(BeerEntry.COLUMN_QUANTITY, quantity);
                        getContentResolver().update(currentBeerUri, values, null, null);
                    }
                }
            });

            mPhoneIntent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                            mPhoneSupplierEditText.getText().toString().trim(), null));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(CURRENT_BEER_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameProductEditText = (EditText) findViewById(R.id.edit_beer_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_beer_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_beer_quantity);
        mBottleSpinner = (Spinner) findViewById(R.id.spinner_bottles);
        mNameSupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mPhoneSupplierEditText = (EditText) findViewById(R.id.edit_supplier_phone);

        /**   Setup OnTouchListeners on all the input fields, so we can determine if the user
         has touched or modified them. This will let us know if there are unsaved changes
         or not, if the user tries to leave the editor without saving**/
        mNameProductEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mBottleSpinner.setOnTouchListener(mTouchListener);
        mNameSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneSupplierEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_bottles_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBottleSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBottleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.bootle_12))) {
                        mBottle = BeerEntry.TYPE_BOTTLE_12; // 12oz
                    } else if (selection.equals(getString(R.string.bootle_16))) {
                        mBottle = BeerEntry.TYPE_BOTTLE_16; // 16oz
                    } else if (selection.equals(getString(R.string.bootle_22))) {
                        mBottle = BeerEntry.TYPE_BOTTLE_22; // 22oz
                    } else {
                        mBottle = BeerEntry.TYPE_BOTTLE_CAN; // Can
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBottle = BeerEntry.TYPE_BOTTLE_CAN; // Can
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" and "Call Supplier" menu item.
        if (currentBeerUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            deleteItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //check if the beer is valid and insert the beer to database
                 saveBeer();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                if (!mBeerHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * get user input and save new beer into database
     **/
    private void saveBeer() {

        String nameString = mNameProductEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mNameSupplierEditText.getText().toString().trim();
        String phoneNumberOfSupplierString = mPhoneSupplierEditText.getText().toString().trim();

        //Validation for NO-existing Inventory item and with all the values null
        if (currentBeerUri == null &&
                (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                        TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString) ||
                        TextUtils.isEmpty(phoneNumberOfSupplierString))) {

            // Since no fields were modified, we can return early without creating a new beer.
            Toast.makeText(this, getString(R.string.editor_save_beer_failed),
                    Toast.LENGTH_SHORT).show();
            return;

        }

        //Validation for existing Inventory item but values must be not null
        if (currentBeerUri != null && TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(phoneNumberOfSupplierString)) {

            // Since value(s) are null prompt toast
            Toast.makeText(this, getString(R.string.editor_save_beer_failed),
                    Toast.LENGTH_SHORT).show();
            return;

        }

        //Validate phone entry can be null but if not empty must have 10 numbers
        if (!TextUtils.isEmpty(phoneNumberOfSupplierString) && (phoneNumberOfSupplierString.length() != 10)) {
            Toast.makeText(this, getString(R.string.phone_save_failed),
                    Toast.LENGTH_SHORT).show();
        } else {

            ContentValues values = new ContentValues();

            values.put(BeerEntry.COLUMN_NAME, nameString);
            values.put(BeerEntry.COLUMN_TYPE_BOTTLE, mBottle);

            // If the price is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            Double price = 0.0;
            if (!TextUtils.isEmpty(priceString)) {
                try {
                    price = Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                }
            }
            values.put(BeerEntry.COLUMN_PRICE, price);

            // If the quantity is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }

            values.put(BeerEntry.COLUMN_QUANTITY, quantity);
            values.put(BeerEntry.COLUMN_SUPPLIER_NAME, supplierString);
            values.put(BeerEntry.COLUMN_SUPPLIER_PHONE, phoneNumberOfSupplierString);

            // Determine if this is a new or existing beer by checking if CurrentBeerURI is null or not
            if (currentBeerUri == null) {

                Uri newUri = getContentResolver().insert(BeerEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_beer_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_beer_successful),
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                // Otherwise this is an EXISTING beer, so update the book with content URI:

                int rowsAffected = getContentResolver().update(currentBeerUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_beer_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_beer_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            //Exit activity and return to the CatalogActivity
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that shows all the columns from the table.
        String[] projection = {
                BeerEntry._ID,
                BeerEntry.COLUMN_NAME,
                BeerEntry.COLUMN_TYPE_BOTTLE,
                BeerEntry.COLUMN_PRICE,
                BeerEntry.COLUMN_QUANTITY,
                BeerEntry.COLUMN_SUPPLIER_NAME,
                BeerEntry.COLUMN_SUPPLIER_PHONE};


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,               // Parent activity context
                currentBeerUri,                 //Provider content URI to query
                projection,                       // Columns to include in the resulting Cursor
                null,                            // No selection clause
                null,                           // No selection arguments
                null);                          // No sort arguments
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        // Fail early if the cursor is null or there is less than 1 row in the cursor
        if (cursorData == null || cursorData.getCount() < 1) {
            return;
        }

        // first move the cursor to it’s first item position.
        if (cursorData.moveToFirst()) {
            //Then I’ll get the data out of the cursor by getting the index of each data item,

            int nameColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_NAME);
            int priceColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_QUANTITY);
            int bottlesColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_TYPE_BOTTLE);
            int supplierColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursorData.getColumnIndex(BeerEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursorData.getString(nameColumnIndex);
            int bottle = cursorData.getInt(bottlesColumnIndex);
            Double price = cursorData.getDouble(priceColumnIndex);
            int quantity = cursorData.getInt(quantityColumnIndex);
            String supplier = cursorData.getString(supplierColumnIndex);
            String phone = cursorData.getString(phoneColumnIndex);

            mNameProductEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mNameSupplierEditText.setText(supplier);
            mPhoneSupplierEditText.setText(phone);

            // Bootle is a dropdown spinner, so map the constant value from the database

            switch (bottle) {
                case BeerEntry.TYPE_BOTTLE_12:
                    mBottleSpinner.setSelection(1);
                    break;
                case BeerEntry.TYPE_BOTTLE_16:
                    mBottleSpinner.setSelection(2);
                    break;
                case BeerEntry.TYPE_BOTTLE_22:
                    mBottleSpinner.setSelection(3);
                    break;
                default:
                    mBottleSpinner.setSelection(0); // CANS
                    break;
            }
        }

        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        mNameProductEditText.setText("");
        mPriceEditText.setText("");
        mBottleSpinner.setSelection(0); // CANS
        mQuantityEditText.setText("");
        mNameSupplierEditText.setText("");
        mPhoneSupplierEditText.setText("");

    }

    /**
     * Prompt the user to confirm that they want to delete this beer.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the beer.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the beer.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the beer in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBeerUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.

            int rowsDeleted;
            rowsDeleted = getContentResolver().delete(currentBeerUri, null, null);
            Log.v("DeleteBeer", String.valueOf(rowsDeleted));

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {

                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_beer_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_beer_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the beer hasn't changed, continue with handling back button press
        if (!mBeerHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

}
