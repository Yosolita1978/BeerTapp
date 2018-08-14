package co.yosola.beertapp.data;

// The contract to create a DB. Create 14/08/18

import android.provider.BaseColumns;

public final class BeerContract {

    // To prevent someone from accidentally instantiating here is an empty constructor
    private BeerContract() {
    }

    public static final class BeerEntry implements BaseColumns {
        public final static String TABLE_NAME = "beers";

        // Here are the columns for the beers
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BEER_NAME = "name";
        public final static String COLUMN_BEER_PRICE = "price";
        public final static String COLUMN_BEER_QUANTITY = "quantity";
        public final static String COLUMN_BEER_TYPE_BOTTLE = "type_bottle";

        // Here are the columns for the suppliers
        public final static String COLUMN_BEER_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_BEER_SUPPLIER_PHONE = "supplier_phone";

        // Here are the different valus for the type of bottles

        public static final int TYPE_BOTTLE_CAN = 0;     //cans
        public static final int TYPE_BOTTLE_12 = 1;     //12 oz
        public static final int TYPE_BOTTLE_16 = 2;     //16 oz
        public static final int TYPE_BOTTLE_22 = 3;     //22 oz

    }
}
