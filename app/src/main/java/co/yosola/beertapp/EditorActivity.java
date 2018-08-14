package co.yosola.beertapp;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

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
     * Type of the bottles for the beer. The possible values are:
     * 0 for cans, 1 for 12oz, 2 for 16oz, 3 for 22oz.
     */
    private int mBottle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameProductEditText = (EditText) findViewById(R.id.edit_beer_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_beer_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_beer_quantity);
        mBottleSpinner = (Spinner) findViewById(R.id.spinner_bottles);
        mNameSupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mPhoneSupplierEditText = (EditText) findViewById(R.id.edit_supplier_phone);


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
                        mBottle = 1; // 12oz
                    } else if (selection.equals(getString(R.string.bootle_16))) {
                        mBottle = 2; // 16oz
                    } else if (selection.equals(getString(R.string.bootle_22))) {
                        mBottle = 3; // 22oz
                    } else {
                        mBottle = 0; // Can
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBottle = 0; // Can
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
