package co.yosola.beertapp.data;

// The contract to create a DB. Create 14/08/18

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BeerContract {

    /**
     * The "Content authority" is a name for the entire content provider.
     */
    public static final String CONTENT_AUTHORITY = "co.yosola.beertapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which app will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://co.yosola.beertapp/beers/ is a valid path for
     * looking at beer data.
     */
    public static final String PATH_BEERS = "beers";

    // To prevent someone from accidentally instantiating here is an empty constructor
    private BeerContract() {
    }

    public static final class BeerEntry implements BaseColumns {


        /**
         * The content URI to access the beer data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BEERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of beers.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEERS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single beer.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEERS;


        public final static String TABLE_NAME = "beers";

        // Here are the columns for the beers
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_TYPE_BOTTLE = "bottle";

        // Here are the columns for the suppliers
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        // Here are the different valus for the type of bottles

        public static final int TYPE_BOTTLE_CAN = 0;     //cans
        public static final int TYPE_BOTTLE_12 = 1;     //12 oz
        public static final int TYPE_BOTTLE_16 = 2;     //16 oz
        public static final int TYPE_BOTTLE_22 = 3;     //22 oz

        /**
         * Returns whether or not the given type of bottle is valid
         */
        public static boolean isValidBootle(int typeBottle) {
            if (typeBottle == TYPE_BOTTLE_CAN || typeBottle == TYPE_BOTTLE_12 || typeBottle == TYPE_BOTTLE_16 || typeBottle == TYPE_BOTTLE_22) {
                return true;
            }
            return false;
        }

    }
}
