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
	private Context context;
	private SQLiteDatabase db;

	private DatabaseHelper(Context context) {
		this.context = context;
		this.db = new OpenHelper(context).getWritableDatabase();
	}

	/**
	 * Activities should call this to get a reference to the database helper.
	 * This class is a singleton to avoid opening of the database multiple
	 * times.
	 * @param context
	 * @return the global instance of DatabaseHelper
	 */
	public DatabaseHelper getInstance(Context context) {
		if (_instance == null) {
			_instance = new DatabaseHelper(context);
		}
		return _instance;
	}
	
	public void addItem(ContentValues itemvalues, int categoryid) {
		itemvalues.put(CATEGORY_ID, categoryid);
		db.insert(ITEM_TABLE, null, itemvalues);
	}

	/**
	 * Call this to get all news items for a specified category.
	 * @param category_id
	 * @return Cursor containing the news items
	 */
	public Cursor getItemsForCategory(int category_id){
		return db.query(ITEM_TABLE, null, ITEM_CATEGORY + "=" + category_id, null, null, null, PUBDATE +" desc");
	}

	private class OpenHelper extends SQLiteOpenHelper {

		private OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			/*
			 * Be aware that the current version of sqlite on Android does NOT
			 * support foreign keys or constraints. It is not that big of a
			 * downside, as it is more a convenience to have those (e.g. cascade
			 * on delete).
			 */
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
					+ CATEGORY_NAME + " text default '',"
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
