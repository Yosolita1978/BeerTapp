package co.yosola.beertapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import co.yosola.beertapp.data.BeerContract.BeerEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Initialize value can be set as any unique integer.
     */
    private static final int BEER_LOADER = 0;

    /**
     * The adapter for the list view
     */
    BeerCursorAdapter mCursorAdapter;


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

        // Find the ListView which will be populated with the beers
        ListView beerListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_layout);
        beerListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of beer data in the Cursor.
        // There is no beer data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BeerCursorAdapter(this, null);
        beerListView.setAdapter(mCursorAdapter);

        beerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create new intent to to go to EditorActivity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //appending the "id" to the URI

                Uri currentBeerUri = ContentUris.withAppendedId(BeerEntry.CONTENT_URI, id);
                // set the Uri on the data field of the intent:
                intent.setData(currentBeerUri);
                //lunch the EditorActivity to display the data of the current beer
                startActivity(intent);
            }
        });

        // Kick off the loader that loads the list items
        getLoaderManager().initLoader(BEER_LOADER, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all beers in the database.
     */
    private void deleteAllBeers() {
        int rowsDeleted = getContentResolver().delete(BeerEntry.CONTENT_URI, null, null);

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

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_beer_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the beer.
                deleteAllBeers();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        // Define a projection that specifies the columns from the table.
        // Use only the columns needed to display the list view for better performance.
        String[] projection = {
                BeerEntry._ID,
                BeerEntry.COLUMN_NAME,
                BeerEntry.COLUMN_TYPE_BOTTLE,
                BeerEntry.COLUMN_PRICE,
                BeerEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,               // Parent activity context
                BeerEntry.CONTENT_URI,                 //Provider content URI to query
                projection,                       // Columns to include in the resulting Cursor
                null,                            // No selection clause
                null,                           // No selection arguments
                null);                             // No sort arguments



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        // Update {@link BeerCursorAdapter} with this new cursor containing updated beer data
        mCursorAdapter.swapCursor(cursorData);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}