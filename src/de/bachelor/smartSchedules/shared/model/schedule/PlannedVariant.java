package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Eine konkret einegeplante Variante mit seinen Allocations
 * @author timo
 *
 */
public class PlannedVariant implements Serializable {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 8452268059140503306L;
	private List<Allocation> allocationList;
	private Variant plannedVariant;
	private static final int MINUTE = 60000;
	private int plannedVariantID, scenarioID, scheduleID;
	
	/**
	 * Default
	 */
	public PlannedVariant() {
		this.plannedVariantID = -1;
		this.scenarioID = -1;
		this.scheduleID = -1;
	}
	
	public PlannedVariant(Variant plannedVariant) {
		this();
		this.plannedVariant = plannedVariant;
		this.allocationList = new ArrayList<Allocation>();
		this.scenarioID = plannedVariant.getScenarioID();
	}
	
	public PlannedVariant(Variant plannedVariant, List<Allocation> allocationList) {
		this(plannedVariant);
		this.setAllocationList(allocationList);
	}
	
	public PlannedVariant(Variant plannedVariant, Allocation allocation) {
		this(plannedVariant);
		this.addAllocation(allocation);
		this.scenarioID = plannedVariant.getScenarioID();
	}

	public List<Allocation> getAllocationList() {
		return allocationList;
	}

	public void setAllocationList(List<Allocation> allocationList) {
		this.allocationList = allocationList;
		this.sortAllocations();
	}
	
	public void addAllocation(Allocation allocation) {
		this.allocationList.add(allocation);
		this.sortAllocations();
	}

	public Variant getPlannedVariant() {
		return plannedVariant;
	}

	public void setPlannedVariant(Variant plannedVariant) {
		this.plannedVariant = plannedVariant;
	}
	
	public ScheduleOrder getScheduleOrder(Scenario scenario) {
		if(!allocationList.isEmpty()) {
			return allocationList.get(0).getScheduleOrder(scenario);
		}
		
		return null;
	}
	
	public Integer getScheduleOrderID() {
		if(!allocationList.isEmpty()) {
			return allocationList.get(0).getOrderID();
		}
		
		return null;
	}
	
	/**
	 * Gibt die Verspätung des Auftrages in Minuten zurück:
	 * @return
	 */
	public int getLateness(Scenario scenario) {
		return (int)((this.getScheduleOrder(scenario).getEarlistDueTime().getTime() - this.getFinish(scenario).getTime()) /MINUTE);
	}
	
	/**
	 * Prüft die Konsistenz
	 * @return
	 */
	public boolean isConsistent(Scenario scenario, Schedule schedule) {
		
		if(isDone()) {
			return true;
		}
		
		if(this.allocationList.isEmpty()) {
			
			// TODO
			System.out.println("PV:-1-");
			
			return false;
		}
		
		// Alle von der gleichen Order?
		ScheduleOrder tmpScheduleOrder = this.allocationList.get(0).getScheduleOrder(scenario);
		if(tmpScheduleOrder == null) {
			return false;
		}
		for(Allocation a : this.allocationList) {
			ScheduleOrder tmpScheduleOrder2 = a.getScheduleOrder(scenario);
			if(a.getScheduleOrder(scenario) == null || !tmpScheduleOrder2.equals(tmpScheduleOrder)) {
				
				// TODO
				System.out.println("PV:-2-");
				
				return false;
			}
		}
		
		// Alle Allocations beachtet?
		if(this.allocationList.size() != this.plannedVariant.getOperations().size()) {
			
			// TODO
			System.out.println("PV:-3-");
			
			return false;
		}
		
		// Sind die richtigen Operations/Allocations beachtet?
		for(int i = 0; i < this.allocationList.size(); i++) {
			if(!this.allocationList.get(i).getOperation().equals(this.plannedVariant.getOperations().get(i))) {
				
				// TODO
				System.out.println("PV:-4-");
				
				return false;
			}
		}
		
		// ResourceBreakDown, ResourceRestrictions und Vorgänger beachtet?
		for(Allocation a : this.allocationList) {
			if(!a.isConsistent(scenario, schedule)) {		
				
				// TODO
				System.out.println("PV:-5-");
				
				return false;
			}
		}

		
		return true;
	}
	
	public Date getStart() {
		Date tmpStart = new Date(Long.MAX_VALUE);
		
		// Frühsten Start suchen:
		for(Allocation a : this.allocationList) {
			if(a.getStart().before(tmpStart)) {
				tmpStart = a.getStart();
			}
		}
		
		return tmpStart;
	}
	
	public Date getFinish(Scenario scenario) {
		Date tmpFinish = new Date(0);
		
		// Spätestes finish suchen:
		for(Allocation a : this.allocationList) {
			if(a.getFinish(scenario).after(tmpFinish)) {
				tmpFinish = a.getFinish(scenario);
			}
		}
		
		return tmpFinish;
	}
	
	/**
	 * Kopiert die PlannedVariant
	 */
	public PlannedVariant clone(Scenario scenario) {
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
		
		// AllocationListe kopieren:
		for(Allocation thisAllocation : this.getAllocationList()) {
			// Vorgängerliste für die Clone erstellen:
			List<Allocation> tmpPredecessors = new ArrayList<Allocation>();
			for(Allocation thisAllocationPre : thisAllocation.getAllocationPredecessors()) {
				// Clon-Vorgänger suchen und speichern:
				for(Allocation cloneAllocationPre : tmpAllocationList) {
					if(cloneAllocationPre.equals(thisAllocationPre)) {
						tmpPredecessors.add(cloneAllocationPre);
					}
				}
			}
			// Clon erstellen:
			tmpAllocationList.add(new Allocation(thisAllocation.getUID(), new Date(thisAllocation.getStart().getTime()), thisAllocation.getOperation(), thisAllocation.getOrderID(), tmpPredecessors, thisAllocation.getResourceID()));
			tmpAllocationList.get(tmpAllocationList.size() -1).setDone(thisAllocation.isDone());
		}
		
		
		return new PlannedVariant(this.getPlannedVariant(), tmpAllocationList);
	}
	
	public Product getProduct() {
		return this.plannedVariant.getProduct();
	}
	
	private void sortAllocations() {
		Collections.sort(allocationList, new Comparator<Allocation>() {

			@Override
			public int compare(Allocation o1, Allocation o2) {
				if(!o1.getAllocationPredecessors().contains(o2)
				&& !o2.getAllocationPredecessors().contains(01)) {
					return 0;
				}
				
				if(o2.getAllocationPredecessors().contains(o1)) {
					return -1;
				}
				
				return 1;
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlannedVariant other = (PlannedVariant) obj;
		if (allocationList == null) {
			if (other.allocationList != null)
				return false;
		} else if (!this.getStart().equals(other.getStart())
				|| !(this.getAllocationList().get(0).getResourceID() == other.getAllocationList().get(0).getResourceID()))
			return false;
		return true;
	}
	
	/**
	 * Gibt an, ob die PlannedVariant komplett bearbeitet wurde.
	 * @return
	 */
	public boolean isDone() {
		// Alle Allocations prüfen:
		for(Allocation a : allocationList) {
			if(!a.isDone()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Nicht die ID von der Variante!
	 * @return
	 */
	public int getPlannedVariantID() {
		return plannedVariantID;
	}

	public void setPlannedVariantID(int plannedVariantID) {
		this.plannedVariantID = plannedVariantID;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

}
