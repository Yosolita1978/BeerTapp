package co.yosola.beertapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import co.yosola.beertapp.data.BeerDbHelper;
import co.yosola.beertapp.data.BeerContract.BeerEntry;

public class MainActivity extends AppCompatActivity {

    /**
     * Adapter for the list of beers
     */
    private ListAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    

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

        displayDatabaseInfo();

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        BeerDbHelper mDbHelper = new BeerDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        if(db != null){

            // Find the layout with the image for the empty views
            ImageView emptyLayout = (ImageView) findViewById(R.id.empty_image);
            emptyLayout.setVisibility(View.GONE);
            // Perform this raw SQL query "SELECT * FROM pets"
            // to get a Cursor that contains all rows from the pets table.
            Cursor cursor = db.rawQuery("SELECT * FROM " + BeerEntry.TABLE_NAME, null);
            try {
                // Display the number of rows in the Cursor (which reflects the number of rows in the
                // pets table in the database).
                TextView displayView = (TextView) findViewById(R.id.empty_view);
                displayView.setText("Number of rows in brewery database  beer table: " + cursor.getCount());
            } finally {
                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();
            }

        } else{

            // Find the layout for the empty views
            View emptyLayout = (View) findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);

            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.empty_view);

        }

    }
}