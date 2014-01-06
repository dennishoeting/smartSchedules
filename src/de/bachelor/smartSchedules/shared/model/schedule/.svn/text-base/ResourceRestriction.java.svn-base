package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.Date;
/**
 * Gibt an wann die Ressource nicht verfürbar ist.
 * @author timo
 *
 */
public class ResourceRestriction implements Serializable {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -1254470809084512342L;
	
	private Date startTime, dueTime;
	private int resourceID, scenarioID;
	
	/**
	 * Default
	 */
	public ResourceRestriction() {
		this.scenarioID = -1;
	}
	
	/**
	 * Konstruktor
	 * @param startTime
	 * @param dueTime
	 */
	public ResourceRestriction(int scenarioID, Date startTime, Date dueTime, int resourceID) {
		this();
		this.scenarioID = scenarioID;
		this.startTime = startTime;
		this.dueTime = dueTime;
		this.resourceID = resourceID;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getDueTime() {
		return dueTime;
	}
	
	/**
	 * Gibt an, ob ein Zeitraum in eine Restriction fällt
	 * @param startTime
	 * @param dueTime
	 * @return
	 */
	public boolean isBetweenRestriction(Date startTime, Date dueTime) {
		if(startTime.before(this.dueTime)) { // Startzeit vor dem Ende
			// Genau in der Restriktion
			if(startTime.after(this.startTime)) {
				return true;
			}
			// DueTime gleich
			if(dueTime.equals(this.dueTime)) {
				return true;
			}
		} else { // Startzeit nach dem Ende der Restriktions
			// StartTime gleich
			if(startTime.equals(this.startTime)) {
				return true;
			}
			// StartTime == DueTime
			if(startTime.equals(this.dueTime)) {
				return false;
			}
		}
		return false;
	}
	
	public int getResourceID() {
		return this.resourceID;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceRestriction other = (ResourceRestriction) obj;
		if (dueTime == null) {
			if (other.dueTime != null)
				return false;
		} else if (!dueTime.equals(other.dueTime))
			return false;
		if (resourceID != other.resourceID)
			return false;
		if (scenarioID != other.scenarioID)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
}
