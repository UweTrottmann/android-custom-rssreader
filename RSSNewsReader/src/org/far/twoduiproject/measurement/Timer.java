package org.far.twoduiproject.measurement;

public class Timer {
	
	private long startTime = 0;
	private long endTime   = 0;

	
	public void start(){
		this.startTime = System.currentTimeMillis();
	}

	public void stop() {
		this.endTime   = System.currentTimeMillis();  
	}

	public long getTotalTime() {
		return this.endTime - this.startTime;
	}

}
