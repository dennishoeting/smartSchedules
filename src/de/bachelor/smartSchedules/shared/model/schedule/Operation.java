package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Eine Operation
 * Duration ist in Sekunden anzugeben!
 * @author timo
 *
 */
public class Operation implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 8332200843417899938L;
	private List<Integer> resourceAlternatives;
	private int operationID, duration, scenarioID, variantID;
	private String name;
	private List<Operation> predecessors;
	
	/**
	 * Default
	 */
	public Operation() {
		this.resourceAlternatives = new ArrayList<Integer>();
	}
	
	/**
	 * Konstruktor
	 * @param operationID
	 * @param variantID
	 * @param ressourceID
	 * @param duration
	 * @param name
	 */
	public Operation(int scenarioID, int operationID, int resource, int duration, int variantID ,String name) {
		this();
		this.variantID = variantID;
		this.scenarioID = scenarioID;
		this.operationID = operationID;
		this.resourceAlternatives.add(resource);
		this.duration = duration;
		this.name = name;
		this.predecessors = new ArrayList<Operation>();
	}
	
	public Operation(int scenarioID, int operationID, List<Integer> resourceAlternatives, int duration, int variantID, String name) {
		this();
		this.variantID = variantID;
		this.scenarioID = scenarioID;
		this.operationID = operationID;
		this.resourceAlternatives = resourceAlternatives;
		this.duration = duration;
		this.name = name;
		this.predecessors = new ArrayList<Operation>();
	}
	
	public Operation(int scenarioID, int operationID, int resource, int duration, int variantID, String name, List<Operation> predecessors) {
		this(scenarioID, operationID, resource, duration, variantID, name);
		this.predecessors = predecessors;
	}
	
	public Operation(int scenarioID, int operationID, int resource, int duration, int variantID, String name, Operation predecessor) {
		this(scenarioID, operationID, resource, duration, variantID, name);
		ArrayList<Operation> predecessorList = new ArrayList<Operation>();
		predecessorList.add(predecessor);
		this.predecessors = predecessorList;
	}
	
	public Operation(int scenarioID, int operationID, List<Integer> resourceAlternatives, int duration, int variantID, String name, List<Operation> predecessors) {
		this(scenarioID, operationID, resourceAlternatives, duration, variantID, name);
		this.predecessors = predecessors;
	}
	
	public Operation(int scenarioID, int operationID, List<Integer> resourceAlternatives, int duration, int variantID, String name, Operation predecessor) {
		this(scenarioID, operationID, resourceAlternatives, duration, variantID, name);
		ArrayList<Operation> predecessorList = new ArrayList<Operation>();
		predecessorList.add(predecessor);
		this.predecessors = predecessorList;
	}

	public int getOperationID() {
		return this.operationID;
	}

	public List<Integer> getResourceAlternatives() {
		return this.resourceAlternatives;
	}
	
	public void setResourceAlternatives(List<Integer> resources) {
		this.resourceAlternatives = resources;
	}
	
	public void addResourceAlternative(int resourceID) {
		this.resourceAlternatives.add(resourceID);
	}

	public int getDuration() {
		return this.duration;
	}

	public String getName() {
		return this.name;
	}

	public List<Operation> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<Operation> predecessors) {
		this.predecessors = predecessors;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Operation other = (Operation) obj;
		if (operationID != other.operationID)
			return false;
		return true;
	}
	
	/**
	 * Gibt an, ob die Operation Kritisch ist.
	 * @return
	 */
	public boolean isCritical(Scenario scenario) {
		int tmpResourceCount = 0;
		for(Integer r : this.resourceAlternatives) {
			if(!scenario.getResources().get(r).isInBreakDown()) {
				tmpResourceCount++;
			}
		}
		
		return tmpResourceCount > 1 ? true : false;
	}
	
	public boolean isPlanable(Scenario scenario) {
		for(Integer r : this.resourceAlternatives) {
			if(!scenario.getResources().get(r).isInBreakDown()) {
				return true;
			}
		}
		
		return false;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	public int getVariantID() {
		return variantID;
	}

	public void setVariantID(int variantID) {
		this.variantID = variantID;
	}
}
