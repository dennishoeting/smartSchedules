package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Eine Variante
 * @author timo
 *
 */
public class Variant implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -1398027546827966688L;
	private int variantID, scenarioID;
	private List<Operation> operations;
	private Product product;
	
	/**
	 * Default
	 */
	public Variant() {
		
	}
	
	public Variant(int variantID, List<Operation> operations, Product product) {
		this.scenarioID = product.getScenarioID();
		this.variantID = variantID;
		this.operations = operations;
		
		this.product = product;
		this.sortOperations();
	}

	public int getVariantID() {
		return variantID;
	}

	public List<Operation> getOperations() {
		return operations;
	}
	
	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}
	
	public List<Allocation> generateAllocations(ScheduleOrder order) {
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
		for(Operation op : this.operations) {
			List<Allocation> tmpAllocationPredecessors = new ArrayList<Allocation>();
			for(Operation predecessorOp : op.getPredecessors()) {
				for(Allocation a : tmpAllocationList) {
					if(a.getOperation().equals(predecessorOp)) {
						tmpAllocationPredecessors.add(a);
					}
				}
			}
			tmpAllocationList.add(new Allocation(op, order, tmpAllocationPredecessors));
		}
		
		return tmpAllocationList;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	/**
	 * 
	 * @param id
	 * @return Operation oder null.
	 */
	public Operation getOperation(int id) {
		for(Operation op : this.operations) {
			if(op.getOperationID() == id) {
				return op;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Variant
				&& ((Variant)object).getVariantID() == this.variantID) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sortiert die Operationen
	 */
	private void sortOperations() {
		
		Collections.sort(this.getOperations(), new Comparator<Operation>() {

			@Override
			public int compare(Operation o1, Operation o2) {
				if(!o1.getPredecessors().contains(o2)
						&& !o2.getPredecessors().contains(o1)) {
					return 0;
				} 
				
				if(o1.getPredecessors().contains(o2)) {
					return 1;
				}
				
				return -1;
			}
			
		});
	}
	
	/**
	 * Gibt an, ob die Variante Kritische Operationen enthält.
	 * @return
	 */
	public boolean isCritical(Scenario scenario) {
		
		for(Operation op : this.operations) {
			if(op.isCritical(scenario)) {
				return false;
			}
		}
		
		return true;
	}

	public void setVariantID(int variantID) {
		this.variantID = variantID;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}
}
