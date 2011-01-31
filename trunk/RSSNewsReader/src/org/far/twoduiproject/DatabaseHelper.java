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
	public static final String ITEM_ID = "_id";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String DESCRIPTION = "description";
	public static final String PUBDATE = "pubdate";
	public static final String CATEGORY_TABLE = "categories";
	public static final String ITEM_CATEGORY = "itemcategory";
	public static final String CATEGORY_ID = "_id";
	public static final String CATEGORY_NAME = "name";
	
	public static final String TAG = "RSSNewsReader.DatabaseHelper";

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
	 * Preliminary, may not be used in the end as id-category map
	 * could get stored in SharedPrefernces.
	 * @param name
	 * @param id
	 */
	public void addCategory(String name, int id) {
		ContentValues values = new ContentValues();
		values.put(CATEGORY_ID, id);
		values.put(CATEGORY_NAME, name);
		db.insert(CATEGORY_TABLE, null, values);
	}
	
	/**
	 * Returns all categories and their ids currently in the database.
	 * @return
	 */
	public Cursor getCategories(){
		return db.query(CATEGORY_TABLE, null, null, null, null, null, null);
	}

	/**
	 * Call this to get all news items for a specified category.
	 * @param category_id
	 * @return Cursor containing the news items
	 */
	public Cursor getItemsForCategory(int category_id){
		return db.query(ITEM_TABLE, null, ITEM_CATEGORY + "=" + category_id, null, null, null, PUBDATE +" desc");
	}

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void setTransactionSuccessful(){
		db.setTransactionSuccessful();
	}

	public void endTransaction(){
		db.endTransaction();
	}
	
	public void clear(){
		mOpenHelper.clear();
	}

	private class OpenHelper extends SQLiteOpenHelper {

		private OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void clear() {
			Log.w(TAG, "Clearing of Database was requested, dropping all tables, starting from scratch");
			db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
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
					+ ITEM_CATEGORY + " int references " + CATEGORY_TABLE + "(" + CATEGORY_ID + ")"
					+ ");");
			// TODO: store category-id-mapping in a SharedPreferences file?
			db.execSQL("create table " + CATEGORY_TABLE + " ("
					+ CATEGORY_ID + " int primary key,"
					+ CATEGORY_NAME + " text default ''"
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
			if (oldVersion > DATABASE_VERSION) {
				Log.w(TAG, "Database is newer version (" + oldVersion + ") than this app can use (" + newVersion + "), starting from scratch");
				db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
				db.setVersion(DATABASE_VERSION);
				onCreate(db);
				return;
			}
			
			/*
			 * Upgrade-Code if the need arises to change the db schema between versions
			 */
//			int currentVersion = oldVersion;
//			while (currentVersion < newVersion) {
//				switch (currentVersion) {
//				}
//				currentVersion++;
//			}
//			db.setVersion(newVersion);
		}

	}
}
