package org.far.twoduiproject.measurement;

import java.util.Calendar;

import org.far.twoduiproject.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * This class is in charge of doing all the measurements for the usability testing.
 * See each method description for more details.
 *
 *
 */
public class MeasurementModule {
	
	public static final String SIMPLE_LIST = "SIMPLE_LIST";
	public static final String FISHEYE_LIST = "FISH_EYE_LIST";
	public static final String TREE_VIEW_LIST = "TREE_VIEW_LIST";
	
	private static final String TAG = "RSSNewsReader.MeasurementModule";
	
	
	//private static long measurementID = 0;
	private static Timer timer = null;
	private static String listType = "";
	private static DatabaseHelper dbHelper = null;
	
	//*****************************PUBLIC METHODS*****************************/
	
	/**
	 * Call this method in the onResume() method of the main activity in order to set up a new instance
	 * of the measurement module.
	 */
	public static void initializeSession(Context context){
		//we create a new id for storing the result in the database
		//MeasurementModule.measurementID = System.currentTimeMillis();
		
		//we get an instance of the database helper
		dbHelper = DatabaseHelper.getInstance(context);
		
		//create new instance of the timer
		timer = new Timer();
	}
	
	/**
	 * Call this method just before opening the corresponding list Activity.
	 * This can be called from the onClick() method of the previous Activity.
	 * 
	 * @param listType This is the list type. See the possible static values in MeasurementModule
	 */
	public static void startMeasurement(String listType){
		//check if the MeasurementModule was already initialized
		if(timer != null){
			//it's initialized so we start the timer
			timer.start();
		}
		else{
			//we create a new timer
			timer = new Timer();
			
			//start timer
			timer.start();
		}
		
		//set up list type
		MeasurementModule.listType = listType;
	}

	/**
	 * Call this method just before the application redirects the user is redirected
	 * to the article in order to complete the measurement. 
	 * After this method it's not necessary to call any other method from this class, given
	 * that this call is also in charge of storing the values in the database and destroying
	 * the current Timer session.
	 * 
	 */
	public static void stopMeasurement(String clickedHeadline){
		//we can only measure if a Timer was previously initialized
		if(timer != null){
			//stop timer
			timer.stop();
			
			//do DB call
			storeMeasurementDB(clickedHeadline);
		}
		
		destroySession();
	}
	
	/**
	 * Call this method on the onStop() method from the corresponding list Activity.
	 * For the case of FishEye and SimpleList, call this method only on the first activity
	 * This ensures that if the user exits the current Activity, the measurement is not
	 * stored after the user comes back.
	 * 
	 */
	public static void destroySession(){
		timer = null;
		MeasurementModule.listType = "";
	}
	
	//*****************************PRIVATE METHODS*****************************/
	
	/**
	 * This method is in charge of storing the measurement for this session in the database.
	 * @param clickedHeadline 
	 * 
	 */
	private static void storeMeasurementDB(String clickedHeadline){
		//get measurement
		long measurementTime = timer.getTotalTime();
		long startTime = timer.getStartTime();
		//long endTime = timer.getEndTime();
		
		if(dbHelper != null){
			//do actual transaction
			ContentValues values = new ContentValues();
			
			
			values.put(DatabaseHelper.LIST_TYPE, listType);
			values.put(DatabaseHelper.MEASUREMENT_TIME, measurementTime);
			values.put(DatabaseHelper.MEASUREMENT_ID, startTime);
			values.put(DatabaseHelper.MEASUREMENT_ITEM, clickedHeadline + " " + Calendar.getInstance().getTime().toString());
			
			dbHelper.beginTransaction();
			try{
				dbHelper.addMeasurement(values);
				dbHelper.setTransactionSuccessful();
				Log.i(TAG, "Inserted measurement in table (time, list type, startTime): " + measurementTime + ", " + listType + ", " + startTime);
			} finally {
				dbHelper.endTransaction();
	        }
			
		}
		
		
	}

}
