package de.bachelor.smartSchedules.shared.model.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.com.bradrydzewski.gwtgantt.model.DurationFormat;
import main.java.com.bradrydzewski.gwtgantt.model.Task;
/**
 * Eine Belegung zu einer Ressource
 * @author timo
 *
 */
public class Allocation extends Task implements Comparable<Allocation>{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4042997665015132579L;
	private Operation operation;
	private int scenarioID, resourceID, orderID;
	private List<Allocation> allocationPredecessors;
	//private static int MINUTE = 60000;
	private static int SECOND = 1000;
	private boolean isDone;
	
	/**
	 * Default
	 */
	public Allocation() {
		this.allocationPredecessors = new ArrayList<Allocation>();
	}
	
	public Allocation(Operation operation, ScheduleOrder order, List<Allocation> predecessors) {
		super.setDurationFormat(DurationFormat.MINUTES);
		super.setDuration(operation.getDuration()/60);	//Seconds --> Minutes
		super.setName(operation.getName());
		super.setStart(null);
		
		this.scenarioID = operation.getScenarioID();
		this.allocationPredecessors = predecessors;
		this.operation = operation;
		this.orderID = order.getOrderID();
		this.resourceID = -1;
		this.isDone = false;
	}
	
	/**
	 * 
	 * @param allocationID
	 * @param startTime
	 * @param operation
	 * @param order
	 * @param resource
	 */
	public Allocation(int allocationID, Date startTime, Operation operation, ScheduleOrder order, int resourceID) {
		this(operation, order, new ArrayList<Allocation>());
		super.setUID(allocationID);
		super.setStart(startTime);
		this.scenarioID = operation.getScenarioID();
		this.resourceID = resourceID;
	}
	
	public Allocation(int allocationID, Date startTime, Operation operation, ScheduleOrder order, List<Allocation> predecessors, int resourceID) {
		this(allocationID, startTime, operation, order, resourceID);
		this.allocationPredecessors = predecessors;
		this.scenarioID = operation.getScenarioID();
	}
	
	public Allocation(int allocationID, Date startTime, Operation operation, ScheduleOrder order, Allocation predecessor, int resourceID) {
		this(allocationID, startTime, operation, order, resourceID);
		this.allocationPredecessors.add(predecessor);
		this.scenarioID = operation.getScenarioID();
	}
	
	public Allocation(int allocationID, Date startTime, Operation operation, int orderID, Allocation predecessor, int resourceID) {
		this();
		super.setDurationFormat(DurationFormat.MINUTES);
		super.setDuration(operation.getDuration()/60);	//Seconds --> Minutes
		super.setName(operation.getName());
		super.setStart(null);
		
		this.setUID(allocationID);
		this.setStart(startTime);
		this.scenarioID = operation.getScenarioID();
		this.allocationPredecessors.add(predecessor);
		this.operation = operation;
		this.orderID = orderID;
		this.resourceID = -1;
		this.isDone = false;
		this.resourceID = resourceID;
	}
	
	public Allocation(int allocationID, Date startTime, Operation operation, int orderID, List<Allocation> predecessors, int resourceID) {
		this();
		super.setDurationFormat(DurationFormat.MINUTES);
		super.setDuration(operation.getDuration()/60);	//Seconds --> Minutes
		super.setName(operation.getName());
		super.setStart(null);
		
		this.setUID(allocationID);
		this.setStart(startTime);
		this.scenarioID = operation.getScenarioID();
		this.allocationPredecessors = predecessors;
		this.operation = operation;
		this.orderID = orderID;
		this.resourceID = -1;
		this.isDone = false;
		this.resourceID = resourceID;
	}

	@Override
	public Date getFinish(Scenario scenario) {
		return new Date(super.getStart().getTime() + (long)(operation.getDuration() *SECOND *100.0/scenario.getResources().get(resourceID).getAvailability()));
	}

	@Override
	public int compareTo(Allocation other) {
		if (super.getStart().getTime() < other.getStart().getTime()) {
			return -1; 
		}
		
		if (super.getStart().getTime() == other.getStart().getTime()) {
			return 0; 
		}
		
		return 1;
	}
	
	/**
	 * Gibt aus, ob ein Datum in einer Belegung ist.
	 * @param date
	 * @return
	 */
	public boolean isDateInAllocation(Scenario scenario, Date date) {
		if((date.before(getFinish(scenario))) && (date.after(super.getStart()))){
			return true;
		}
		if(date.equals(getFinish(scenario))) {
			return true;
		}
		if(date.equals(super.getStart())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gibt an um wie viel Minuten diese Belegung zu spät zu ende ist.
	 * @return
	 */
	public int getLatenes(Scenario scenario) {
			return (int)((this.getFinish(scenario).getTime() - scenario.getOrder(this.orderID).getEarlistDueTime().getTime()) /SECOND);
	}
	
	public ScheduleOrder getScheduleOrder(Scenario scenario) {
		return scenario.getOrder(this.orderID);
	}
	
	public Operation getOperation() {
		return this.operation;
	}

	public List<Allocation> getAllocationPredecessors() {
		return allocationPredecessors;
	}

	public void setAllocationPredecessors(List<Allocation> predecessors) {
		this.allocationPredecessors = predecessors;
	}
	
	public void addAllocationPredecessor(Allocation predecessor) {
		this.allocationPredecessors.add(predecessor);
	}
	
	public int getResourceID() {
		return this.resourceID;
	}
	
	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}
	
	public void setAllocationID(int allocationID) {
		super.setUID(allocationID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Allocation other = (Allocation) obj;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (super.getUID() != ((Allocation)obj).getUID())
			return false;
		return true;
	}
	
	/**
	 * Prüft MachineBreakDowns, ResourceRestrictions und
	 * ob alle Vorgänger richtig verplant sind.
	 * @return
	 */
	public boolean isConsistent(Scenario scenario, Schedule schedule) {
		
		// Bereits fertig?
		if(isDone()) {
			return true;
		}
		
		// Resource Verfügbar?
		if(!scenario.getResources().get(this.getResourceID()).isAvailable(scenario, schedule.getResources(scenario).get(scenario.getResources().get(this.getResourceID())), this)) {
			
			// TODO
			System.out.println("A:-1-" +" " +this.getResourceID());
			
			return false;
		}
		
		// Alle Vorgänger beachtet?
		for(Allocation aPre : this.getAllocationPredecessors()) {
			if(this.getStart().before(aPre.getFinish(scenario))) {
				
				// TODO
				System.out.println("A:-2-");
				
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Gibt an, ob die Allocation bereits fertig bearbeitet ist.
	 * @return
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * Setzt, ob die Alloction bereits fertig bearbeitet ist.
	 * @param isDone
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}
}
