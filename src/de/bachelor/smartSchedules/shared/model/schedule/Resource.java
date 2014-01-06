package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Eine Ressource. Availability ist in % anzugeben.
 * Die Allocation Liste ist immer aufsteigend sortiert!
 * TODO: abgelaufene ResourceRestrictions irgendwann wieder löschen.
 * @author timo
 *
 */
public class Resource implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 7964585744587295146L;
	private int resourceID, availability, scenarioID;
	private String name;
	private List<ResourceRestriction> resourceRestrictions;
	private boolean breakDown;
	private int MINUTE = 60000;
	
	/**
	 * Default
	 */
	public Resource() {
		this.scenarioID = -1;
		this.resourceRestrictions = new ArrayList<ResourceRestriction>();
		this.breakDown = false;
	}
	
	public Resource(int scenarioID, int resourceID, String name, int availability) {
		this();
		this.scenarioID = scenarioID;
		this.resourceID = resourceID;
		this.name = name;
		this.availability = Math.abs(availability);
	}

	public int getResourceID() {
		return resourceID;
	}

	public String getName() {
		return name;
	}
	
	public int getAvailability() {
		return this.availability;
	}
	
	/**
	 * in Prozent, darf nicht negativ sein.
	 * @param availability
	 */
	public void setAvailability(int availability) {
		this.availability = Math.abs(availability);
	}
	
	/**
	 * Belegt die Ressource mit einer Operation
	 * Dabei wird die erst mögliche Zeit verwendet.
	 * Die Änderung findetet direkt in Allocations statt!
	 * @param startTime
	 * @param operation
	 * @return
	 */
	public void addOperation(Allocation allocation, Date startTime, List<Allocation> allocations) {		
		// Einfügen
		allocation.setStart(startTime);
		allocation.setResourceID(this.getResourceID());
		allocations.add(allocation);
		
		// Danach Allocation sortieren:
		Collections.sort(allocations);
	}
	
	/**
	 * Kann eine passende Schlupfzeit finden
	 * @param startTime
	 * @param operation
	 * @return
	 */
	public Date findFirstPossibleAllocation(Scenario scenario, Date startTime, Operation operation, List<Allocation> allocations) {
		
		// Falls allocation leer ist:
		if(allocations.isEmpty()) {
			return startTime;
		} 
		
		// Passt vor die erste Belegung:
		if(allocations.get(0).getStart().after(new Date(startTime.getTime() + (long)((operation.getDuration() * MINUTE) *100.0/this.getAvailability())))) {
			return startTime;
		}
		
		// allocation durchlaufen und freien Platz suchen:
		for(Allocation allocation : allocations) {
			if(new Date(startTime.getTime() + (long)((operation.getDuration() * MINUTE) *100.0/this.getAvailability())).before(allocation.getStart())) {
				return startTime;
			} else {
				if(allocation.getFinish(scenario).getTime() >= startTime.getTime()) {
					startTime = new Date(allocation.getFinish(scenario).getTime() +MINUTE);
				}
			}
			
			// Zusätzlich ResourceRestrictions beachten:
			for(ResourceRestriction rr : this.getResourceRestrictions()) {
				if(rr.isBetweenRestriction(startTime, 
						new Date(startTime.getTime() +((long)((operation.getDuration() *MINUTE) *(100.0 / this.getAvailability())))))) {
					startTime = new Date(rr.getDueTime().getTime() +MINUTE);
				}
			}
			
		}
			
		return startTime;
	}
	
	/**
	 * Gibt an wann die Ressource nicht verfügbar ist.
	 * @return
	 */
	public List<ResourceRestriction> getResourceRestrictions() {
		return resourceRestrictions;
	}
	
	/**
	 * Fügt eine Restriktion für die Ressource hinzu.
	 * @param resourceRestriction
	 */
	public void addRestriction(ResourceRestriction resourceRestriction) {
		this.resourceRestrictions.add(resourceRestriction);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (resourceID != other.resourceID)
			return false;
		return true;
	}

	public boolean isInBreakDown() {
		return breakDown;
	}

	public void setBreakDown(boolean breakDown) {
		this.breakDown = breakDown;
	}
	
	/**
	 * Gibt an, ob die Ressource zu einem bestimmten Zeitraum verfügbar ist.
	 * @return
	 */
	public boolean isAvailable(Date startTime, Date dueTime) {
		
		if(this.isInBreakDown()) {
			return false;
		}
		
		for(ResourceRestriction rr : this.getResourceRestrictions()) {
			if(rr.isBetweenRestriction(startTime, dueTime)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gibt an, ob die Resource Verfügbar ist und prüft zusätzlich auch
	 * Doppelbelegungen.
	 * @param allocationList
	 * @param startTime
	 * @param dueTime
	 * @return
	 */
	public boolean isAvailable(Scenario scenario, List<Allocation> allocationList, Allocation allocation) {
		if(!this.isAvailable(allocation.getStart(), allocation.getFinish(scenario))) {
			
			// TODO
			System.out.println("isAvailable: 1");
			
			return false;
		}
		
		// Doppelbelegung prüfen:
		
		// Dazu die Liste kopieren und sortieren:
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>(allocationList);
		Collections.sort(tmpAllocationList, new Comparator<Allocation>() {

			@Override
			public int compare(Allocation o1, Allocation o2) {
				if(o1.getStart().before(o2.getStart())) {
					return -1;
				}
				if(o1.getStart().equals(o2.getStart())) {
					return 0;
				}
				return 1;
			}
			
		});
		
		// Danach durchlaufen und prüfen, ob es belegt ist:
		for(Allocation a : tmpAllocationList) {
			if(a.getUID() != allocation.getUID()
			&& a.getFinish(scenario).after(allocation.getStart())
			&& a.getFinish(scenario).before(allocation.getFinish(scenario))) {
				
				// TODO
				System.out.println("isAvailable: 2");
				
				return false;
			}
		}
		
		return true;
	}
	
	public Resource clone() {
		return new Resource(this.scenarioID, this.resourceID, new String(this.name), this.availability);
	}

	public boolean isBreakDown() {
		return breakDown;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResourceRestrictions(
			List<ResourceRestriction> resourceRestrictions) {
		this.resourceRestrictions = resourceRestrictions;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}
}
