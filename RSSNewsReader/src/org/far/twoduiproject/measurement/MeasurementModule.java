package org.far.twoduiproject.measurement;

/**
 * This class is in charge of doing all the measurements for the usability testing.
 * See each method description for more details.
 * 
 * @author Fernando Hernandez
 *
 */
public class MeasurementModule {
	
	public static final int SIMPLE_LIST = 0;
	public static final int FISH_EYE_LIST = 1;
	public static final int TREE_LIST = 2;
	
	private static long measurementID = 0;
	private static Timer timer = null;
	private static int listType = -1; 
	
	
	//*****************************PUBLIC METHODS*****************************/
	
	/**
	 * Call this method in the onResume() method of the main activity in order to set up a new instance
	 * of the measurement module.
	 */
	public static void initializeSession(){
		//we create a new id for storing the result in the database
		measurementID = System.currentTimeMillis();
		
		//create new instance of the timer
		timer = new Timer();
	}
	
	/**
	 * Call this method just before opening the corresponding list Activity.
	 * This can be called from the onClick() method of the previous Activity.
	 * 
	 * @param listType This is the list type. See the possible static values in MeasurementModule
	 */
	public static void startMeasurement(int listType){
		//check if the MeasurementModule was already initialized
		if(timer != null){
			//it's initialized so we start the timer
			timer.start();
		}
		else{
			//there was no previously initialized MeasurementModule so we create a new ID
			measurementID = System.currentTimeMillis();
			
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
	public static void stopMeasurement(){
		//we can only measure if a Timer was previously initialized
		if(timer != null){
			//stop timer
			timer.stop();
			
			//do DB call
			storeMeasurementDB();
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
		measurementID = 0;
		MeasurementModule.listType = -1;
	}
	
	//*****************************PRIVATE METHODS*****************************/
	
	/**
	 * This method is in charge of storing the measurement for this session in the database.
	 * 
	 */
	private static void storeMeasurementDB(){
		//get measurement
		long measurement = timer.getTotalTime();
		
		//TODO do database calls
	}

}
