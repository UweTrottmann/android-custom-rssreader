
package org.far.twoduiproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "rssdb";

    private static final int DATABASE_VERSION = 3;

    public static final String ITEM_TABLE = "items";

    public static final String CATEGORY_TABLE = "categories";

    public static final String PREFERENCE_TABLE = "preferences";

    public static final String PROVIDER_TABLE = "providers";
    
    public static final String MEASUREMENT_TABLE = "measurements";

    public static final String ITEM_ID = "_id";

    public static final String TITLE = "title";

    public static final String LINK = "link";

    public static final String DESCRIPTION = "description";

    public static final String PUBDATE = "pubdate";

    public static final String ITEM_CATEGORY = "itemcategory";

    public static final String CATEGORY_ID = "_id";

    public static final String CATEGORY_NAME = "name";

    public static final String PREF_PROVIDERID = "providerid";

    public static final String PROVIDER_ID = "_id";

    public static final String FEEDPATH = "feedpath";

    public static final String ENABLED = "enabled";

    public static final String PROVIDER_NAME = "providername";

    public static final String TAG = "RSSNewsReader.DatabaseHelper";

    public static final String PREF_CATEGORY_ID = "pref_categoryid";

    protected static final String PREF_ENCODING = "pref_encoding";
    
    public static final String MEASUREMENT_ID = "measurement_id";
    
    public static final String MEASUREMENT_TIME = "measurement_time";
    
    public static final String LIST_TYPE = "list_type";

    private static DatabaseHelper _instance;

    private SQLiteDatabase db;

    private OpenHelper mOpenHelper;

    private DatabaseHelper(Context context) {
        this.mOpenHelper = new OpenHelper(context);
        this.db = mOpenHelper.getWritableDatabase();
    }

    /**
     * Activities should call this to get a reference to the database helper.
     * This class is a singleton to avoid opening of the database multiple
     * times.
     * 
     * @param context
     * @return the global instance of DatabaseHelper
     */
    public static DatabaseHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new DatabaseHelper(context);
        }
        return _instance;
    }

    /**
     * Adds a news item with specified category to the item table.
     * 
     * @param itemvalues
     * @param categoryid
     */
    public void addItem(ContentValues itemvalues, int categoryid) {
        itemvalues.put(ITEM_CATEGORY, categoryid);
        db.insert(ITEM_TABLE, null, itemvalues);
    }
    
    public void addMeasurement(ContentValues measurementValues){
    	db.insert(MEASUREMENT_TABLE, null, measurementValues);
    }

    /**
     * Changes the enabled state of the given category for the given provider.
     * 
     * @param providerid
     * @param categoryid
     * @param isEnabled
     */
    public void changeCategoryState(int providerid, int categoryid, boolean isEnabled) {
        ContentValues values = new ContentValues();
        values.put(ENABLED, isEnabled);
        db.update(PREFERENCE_TABLE, values, PREF_PROVIDERID + "=" + providerid + " AND "
                + PREF_CATEGORY_ID + "=" + categoryid, null);
    }
    
    public void changeCategoryState(String enableQuery,String disableQuery){
    	db.execSQL(enableQuery);
    	db.execSQL(disableQuery);
    }

    /**
     * Returns all rows in the preferences table as a Cursor.
     * 
     * @return Cursor containing all preferences
     */
    public Cursor getPreferences() {
        return db.query(PREFERENCE_TABLE, null, null, null, null, null, null);
    }
    
    /**
     * Returns all preferences for one provider.
     * @param i
     * @return Cursor with rows with given provider id
     */
    public Cursor getPreferencesWithProviderId(int providerid) {
    	return db.query(PREFERENCE_TABLE, null, PREF_PROVIDERID + "=" + providerid, null, null, null, null);
    }

    /**
     * Returns all categories and their ids currently in the database.
     * 
     * @return
     */
    public Cursor getCategories() {
        return db.query(CATEGORY_TABLE, null, null, null, null, null, null);
    }



	/**
     * Returns all news items for a specified category in a Cursor.
     * 
     * @param category_id
     * @return Cursor containing the news items
     */
    public Cursor getItemsForCategory(int category_id) {
        return db.query(ITEM_TABLE, null, ITEM_CATEGORY + "=" + category_id, null, null, null,
                PUBDATE + " desc");
    }
    
    /**
     * Returns the measurement table as a Cursor.
     * @return
     */
    public Cursor getMeasurements(){
        return db.query(MEASUREMENT_TABLE, null, null, null, null, null, null);
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    /**
     * Drops all tables in the database and recreates them with the initial
     * dataset based on /assets/config.xml.
     */
    public void clear() {
        mOpenHelper.clear();
    }

    public void clearExistingFeeds() {
        mOpenHelper.clearExistingFeeds();
    }

    private class OpenHelper extends SQLiteOpenHelper {

        private static final String CREATE_ITEM_TABLE = "create table " + ITEM_TABLE + " (" + ITEM_ID + " int primary key," + TITLE
                            + " text," + LINK + " text default ''," + DESCRIPTION + " text default '',"
                            + PUBDATE + " text default ''," + ITEM_CATEGORY + " int references "
                            + CATEGORY_TABLE + "(" + CATEGORY_ID + ")" + ");";
        private Context mContext;

        private OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.mContext = context;
        }

        public void clear() {
            Log
                    .w(TAG,
                            "Clearing of Database was requested, dropping all tables, starting from scratch");
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PROVIDER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PREFERENCE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MEASUREMENT_TABLE); //FH
            db.setVersion(DATABASE_VERSION);
            onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create database schema
            db.execSQL(CREATE_ITEM_TABLE);
            db.execSQL("create table " + CATEGORY_TABLE + " (" + CATEGORY_ID + " int primary key,"
                    + CATEGORY_NAME + " text default ''" + ");");
            db.execSQL("create table " + PREFERENCE_TABLE + " (" + PREF_PROVIDERID
                    + " int references " + PROVIDER_TABLE + "(" + PROVIDER_ID + "),"
                    + PREF_CATEGORY_ID + " int references " + CATEGORY_TABLE + "(" + CATEGORY_ID
                    + ")," + FEEDPATH + " text default ''," + ENABLED + " int default 1,"
                    + PREF_ENCODING + " text default 'UTF_8'"
                    + ");");
            db.execSQL("create table " + PROVIDER_TABLE + " (" + PROVIDER_ID + " int primary key,"
                    + PROVIDER_NAME + " text default ''" + ");");
            
            //FH:
            db.execSQL("create table " + MEASUREMENT_TABLE 
            		+ " (" + MEASUREMENT_ID + " integer primary key,"
                    + LIST_TYPE + " text default ''," 
                    + MEASUREMENT_TIME + " int default 0"
                    + ");");

            // insert inital dataset as specified in /assets/config.xml
            FeedParser.parseInitialSetup(db, mContext);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            if (oldVersion < DATABASE_VERSION) {
                Log.w(TAG, "Database is older version (" + oldVersion + ") than this app can use ("
                        + newVersion + "), starting from scratch");
                db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + PREFERENCE_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + PROVIDER_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + MEASUREMENT_TABLE); //FH 
                db.setVersion(DATABASE_VERSION);
                onCreate(db);
                return;
            }
        }

        public void clearExistingFeeds() {
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            db.execSQL(CREATE_ITEM_TABLE);
        }

    }
}
