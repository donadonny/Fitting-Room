package tk.talcharnes.unborify.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tk.talcharnes.unborify.Models.DealsOptionsModel;

/**
 * This SQLite Class is for the deals options menu
 */

public class SQLiteDatabaseHandlerDeals extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =1;
    private static final String DATABASE_NAME ="DealOptionsDB";
    private static final String TABLE_NAME ="DealOptions";
    private static final String KEY_RADIUS ="radius";
    private static final String KEY_METRIC ="metric";

    private static final String [] COLUMNS ={KEY_RADIUS,KEY_METRIC};

    public SQLiteDatabaseHandlerDeals(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create Table
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE= "CREATE TABLE "+TABLE_NAME+
                "("+KEY_RADIUS + " INTEGER, "
                + KEY_METRIC + " TEXT)";
        db.execSQL(CREATE_TABLE);
        db.close();
    }

    /**
     * Drop Table and create a new one
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        this.onCreate(db);
    }

    /**
     * Get Deals from sqlLite Table will only be 1 row
     * @return
     */
    public DealsOptionsModel getDealsOptionsModel() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        DealsOptionsModel DealsOptionsModel= null;

        if (cursor.moveToFirst()) {
            do {
                DealsOptionsModel = new DealsOptionsModel();
                DealsOptionsModel.setRadius(Integer.parseInt(cursor.getString(0)));
                DealsOptionsModel.setMetric(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return DealsOptionsModel;
    }

    /**
     * Add new Deal Record to table " This should only be called once if no deals are found"
     * @param DealsOptionsModel
     */
    public void addDealOptions(DealsOptionsModel DealsOptionsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RADIUS, DealsOptionsModel.getRadius());
        values.put(KEY_METRIC, DealsOptionsModel.getMetric());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    /**
     * Update the existing Deals Option Record
     * @param DealsOptionsModel
     * @return
     */
    public int updateRecord(DealsOptionsModel DealsOptionsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RADIUS, DealsOptionsModel.getRadius());
        values.put(KEY_METRIC, DealsOptionsModel.getMetric());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "1 = 1", // selections
                new String[] {/*took it out*/});

        db.close();

        return i;
    }
}