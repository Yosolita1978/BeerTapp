package co.yosola.beertapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.yosola.beertapp.data.BeerContract.BeerEntry;
import co.yosola.beertapp.data.BeerDbHelper;

public class MainActivity extends AppCompatActivity {


    /**
     * TextView that is displayed when the db is empty
     */
    private TextView mEmptyStateTextView;

    /** Database helper that will provide us access to the database */
    private BeerDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BeerDbHelper(this);

    }

    /**
     * Overrride the onStart method to display information in the screen when the editorActivity is used.
     */

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the beers database.
     */
    private void displayDatabaseInfo() {

        String[] project = {
                BeerEntry._ID,
                BeerEntry.COLUMN_NAME,
                BeerEntry.COLUMN_PRICE,
                BeerEntry.COLUMN_QUANTITY,
                BeerEntry.COLUMN_TYPE_BOTTLE,
                BeerEntry.COLUMN_SUPPLIER_NAME,
                BeerEntry.COLUMN_SUPPLIER_PHONE

        };
        Cursor cursor = getContentResolver().query(BeerEntry.CONTENT_URI, project, null, null, null);

        int rowsNumber= cursor.getCount();

        TextView displayView = (TextView) findViewById(R.id.empty_view);

        if (rowsNumber == 0){

            // Find the layout for the empty views
            View emptyLayout = (View) findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);

            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.empty_view);

        } else {

            // Find the layout with the image for the empty views
            ImageView emptyLayout = (ImageView) findViewById(R.id.empty_image);
            emptyLayout.setVisibility(View.GONE);
            try {

                displayView.setText("The beer table contains " + rowsNumber + " beers.\n\n");
                displayView.append(BeerEntry._ID + " - " +
                        BeerEntry.COLUMN_NAME + " - " +
                        BeerEntry.COLUMN_PRICE + " - " +
                        BeerEntry.COLUMN_QUANTITY + " - " +
                        BeerEntry.COLUMN_TYPE_BOTTLE + " - " +
                        BeerEntry.COLUMN_SUPPLIER_NAME + " - " +
                        BeerEntry.COLUMN_SUPPLIER_PHONE + "\n");

                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(BeerEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_NAME);
                int priceColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_QUANTITY);
                int bottleColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_TYPE_BOTTLE);
                int supplierNameColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(BeerEntry.COLUMN_SUPPLIER_PHONE);

                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()){

                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentName = cursor.getString(nameColumnIndex);
                    String currentPrice = cursor.getString(priceColumnIndex);
                    int currentQuantity = cursor.getInt(quantityColumnIndex);
                    int currentBottle = cursor.getInt(bottleColumnIndex);
                    String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                    String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append(("\n" + currentID + " - " +
                            currentName + " - " +
                            currentPrice + " - " +
                            currentQuantity + " - " +
                            currentBottle + " - " +
                            currentSupplierName + " - " +
                            currentSupplierPhone));
                }


            } finally {

                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();

            }

        }

    }
}