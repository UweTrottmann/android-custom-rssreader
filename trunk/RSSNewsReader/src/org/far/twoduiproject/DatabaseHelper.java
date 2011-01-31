
package org.far.twoduiproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "rssdb";

    private static final int DATABASE_VERSION = 1;

    public static final String ITEM_TABLE = "items";

    public static final String CATEGORY_TABLE = "categories";

    public static final String PREFERENCE_TABLE = "preferences";

    public static final String PROVIDER_TABLE = "providers";

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

    public void addItem(ContentValues itemvalues, int categoryid) {
        itemvalues.put(ITEM_CATEGORY, categoryid);
        db.insert(ITEM_TABLE, null, itemvalues);
    }
    
    /**
     * Changes the enabled state of the given category for the given provider.
     * @param providerid
     * @param categoryid
     * @param isEnabled
     */
    public void changeCategoryState(int providerid, int categoryid, boolean isEnabled){
        ContentValues values = new ContentValues();
        values.put(ENABLED, isEnabled);
        db.update(PREFERENCE_TABLE, values, PREF_PROVIDERID + "=" + providerid + " AND "
                + PREF_CATEGORY_ID + "=" + categoryid, null);
    }
    
    /**
     * Returns all rows in the preferences table as a Cursor.
     * @return Cursor containing all preferences
     */
    public Cursor getPreferences(){
        return db.query(PREFERENCE_TABLE, null, null, null, null, null, null);
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
     * Call this to get all news items for a specified category.
     * 
     * @param category_id
     * @return Cursor containing the news items
     */
    public Cursor getItemsForCategory(int category_id) {
        return db.query(ITEM_TABLE, null, ITEM_CATEGORY + "=" + category_id, null, null, null,
                PUBDATE + " desc");
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

    public void clear() {
        mOpenHelper.clear();
    }

    private class OpenHelper extends SQLiteOpenHelper {

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
            db.setVersion(DATABASE_VERSION);
            onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + ITEM_TABLE + " ("
                    + ITEM_ID + " int primary key,"
                    + TITLE + " text,"
                    + LINK + " text default '',"
                    + DESCRIPTION + " text default '',"
                    + PUBDATE + " text default '',"
                    + ITEM_CATEGORY + " int references " + CATEGORY_TABLE + "(" + CATEGORY_ID + ")" + ");");
            db.execSQL("create table " + CATEGORY_TABLE + " (" + CATEGORY_ID + " int primary key,"
                    + CATEGORY_NAME + " text default ''" + ");");
            db.execSQL("create table " + PREFERENCE_TABLE + " ("
                    + PREF_PROVIDERID + " int references " + PROVIDER_TABLE + "(" + PROVIDER_ID + "),"
                    + PREF_CATEGORY_ID + " int references " + CATEGORY_TABLE + "(" + CATEGORY_ID + "),"
                    + FEEDPATH + " text default '',"
                    + ENABLED + " int default 1"
                    + ");");
            db.execSQL("create table " + PROVIDER_TABLE + " ("
                    + PROVIDER_ID + " int primary key,"
                    + PROVIDER_NAME + " text default ''"
                    + ");");
            
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
                db.setVersion(DATABASE_VERSION);
                onCreate(db);
                return;
            }
        }

    }
}
