package org.far.twoduiproject.measurement;


/**
 * Object for storing each time measurement
 * 
 *
 */
public class Timer {
	
	private long startTime = 0;
	private long endTime   = 0;

	/**
	 * Public getter for start time
	 * 
	 * @return long start time
	 */
	public long getStartTime(){
		return this.startTime;
	}
	
	/**
	 * Public getter for end time
	 * 
	 * @return long end time
	 */
	public long getEndTime(){
		return this.endTime;
	}
	
	/**
	 * Method for starting measurement
	 * 
	 */
	public void start(){
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Method for stopping measurement
	 * 
	 */
	public void stop() {
		this.endTime   = System.currentTimeMillis();  
	}

	/**
	 * Method for calculating total time (end time - start time)
	 * 
	 * @return long total time
	 */
	public long getTotalTime() {
		return this.endTime - this.startTime;
	}

}
