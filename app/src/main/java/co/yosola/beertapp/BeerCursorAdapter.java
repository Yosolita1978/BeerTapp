package co.yosola.beertapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import co.yosola.beertapp.data.BeerContract;

public class BeerCursorAdapter extends CursorAdapter {

    /** Tag for the log messages */
    public static final String LOG_TAG = BeerCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BeerCursorAdapter}.
     */
    public BeerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the beer data (in the current row pointed to by cursor) to the given
     * list item layout.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursorData) {
        // Find saleButton
        Button saleButton = view.findViewById(R.id.sale_button);


        // Find fields to populate in inflated template
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView bottlesView = (TextView) view.findViewById(R.id.bottles);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of beers attributes that we're interested in
        int nameColumnIndex = cursorData.getColumnIndex(BeerContract.BeerEntry.COLUMN_NAME);
        int bootlesColumnIndex = cursorData.getColumnIndexOrThrow(BeerContract.BeerEntry.COLUMN_TYPE_BOTTLE);
        int priceColumnIndex = cursorData.getColumnIndexOrThrow(BeerContract.BeerEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursorData.getColumnIndexOrThrow(BeerContract.BeerEntry.COLUMN_QUANTITY);


        // Read the beers attributes from the Cursor for the current beer

        final String beerName = cursorData.getString(nameColumnIndex);
        final int beerBootle = cursorData.getInt(bootlesColumnIndex);
        final String beerPrice = cursorData.getString(priceColumnIndex);
        final int beerQuantity = cursorData.getInt(quantityColumnIndex);


        // Update the TextViews with the attributes for the current book
        nameView.setText(beerName);
        priceView.setText("Price: $ " + beerPrice);
        quantityView.setText(String.valueOf(beerQuantity));

        if(beerBootle == 0){
            bottlesView.setText(context.getString(R.string.bottle_can));
        } else if (beerBootle == 1){
            bottlesView.setText(context.getString(R.string.bootle_12));
        } else if (beerBootle == 2){
            bottlesView.setText(context.getString(R.string.bootle_16));
        } else if (beerBootle == 3){
            bottlesView.setText(context.getString(R.string.bootle_22));
        }

        // OnClickListener for Sale button
        // When clicked it reduces the number in stock by 1.

        int idColumnIndex = cursorData.getColumnIndex(BeerContract.BeerEntry._ID);
        final String id = cursorData.getString(idColumnIndex);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beerQuantity > 0) {
                    Log.e(LOG_TAG, "The beer quantity is " + String.valueOf(beerQuantity));
                    Uri currentBeerUri = ContentUris.withAppendedId(BeerContract.BeerEntry.CONTENT_URI, Long.parseLong(id));
                    ContentValues values = new ContentValues();
                    values.put(BeerContract.BeerEntry.COLUMN_QUANTITY, beerQuantity - 1);
                    context.getContentResolver().update(currentBeerUri, values, null, null);
                    swapCursor(cursorData);
                    // Check if out of stock to display toast
                    if (beerQuantity == 1) {
                        Toast.makeText(context, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}