package de.bachelor.smartSchedules.shared.model.util;

import java.io.Serializable;
import java.util.Date;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Enthält wichtige Informationen über die Algorithmen.
 * @author timo
 *
 */
public class AlgorithmInformation implements Serializable {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 7360229507488494336L;
	private String algorithmName;
	private Date startTime, dueTime;
	private int initialScheduleID;
	private static final int MINUTE = 60000;
	
	/**
	 * Default
	 */
	public AlgorithmInformation() {
		initialScheduleID = -1;
	}
	
	/**
	 * 
	 * @param algorithmName
	 * @param startTime Zeit wann der Algorithmus gestartet wurde.
	 * @param dueTime Zeit wann der Algorithmus beendet wurde.
	 */
	public AlgorithmInformation(String algorithmName, Date startTime, Date dueTime) {
		this();
		this.algorithmName = algorithmName;
		this.startTime = startTime;
		this.dueTime = dueTime;
	}
	
	/**
	 * 
	 * @param algorithmName
	 * @param startTime
	 * @param dueTime
	 * @param initialSchedule Falls der Schedule durch ein Event erzeugt wurde und die Folge eines
	 * anderen Schedules ist.
	 */
	public AlgorithmInformation(String algorithmName, Date startTime, Date dueTime, Schedule initialSchedule) {
		this.algorithmName = algorithmName;
		this.startTime = startTime;
		this.dueTime = dueTime;
		this.initialScheduleID = initialSchedule.getScheduleID();
	}
	
	public AlgorithmInformation(String algorithmName, Date startTime, Date dueTime, int initialScheduleID) {
		this.algorithmName = algorithmName;
		this.startTime = startTime;
		this.dueTime = dueTime;
		this.initialScheduleID = initialScheduleID;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getDueTime() {
		return dueTime;
	}
	
	public int getDurationInMin() {
		return (int)((dueTime.getTime() - startTime.getTime()) / MINUTE);
	}
	
	public int getDurationInMS() {
		return (int)(dueTime.getTime() - startTime.getTime());
	}
	
	public Schedule getInitialSchedule(Scenario scenario) {
		for(Schedule oldSchedule : scenario.getOldSchedules()) {
			if(oldSchedule.getScheduleID() == this.initialScheduleID) {
				return oldSchedule;
			}
		}
		return null;
	}
	
	public int getInitialScheduleID() {
		return this.initialScheduleID;
	}
	
	public void setInitialScheduleID(int initialScheduleID) {
		this.initialScheduleID = initialScheduleID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgorithmInformation other = (AlgorithmInformation) obj;
		if (algorithmName == null) {
			if (other.algorithmName != null)
				return false;
		} else if (!algorithmName.equals(other.algorithmName))
			return false;
		if (dueTime == null) {
			if (other.dueTime != null)
				return false;
		} else if (!dueTime.equals(other.dueTime))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
}
