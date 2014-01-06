package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;

/**
 * Ändert die Liste der Alternativen Resourcen für eine Operation.
 * @author timo
 *
 */
public class ChangeOperationResourcesEvent implements ScheduleEvent{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 5044724047016721256L;
	private Date throwTime;
	private int scenarioID, productID, variantID, operationID, duration, oldDuration;
	private List<Integer> newResourceList, oldResourceList;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	/**
	 * Default
	 */
	public ChangeOperationResourcesEvent() {
		this.oldResourceList = new ArrayList<Integer>();
		this.oldDuration = -1;
	}
	
	/**
	 * Konstruktor
	 * @param scenarioID
	 * @param operationID
	 * @param newResourceList
	 * @param throwTime
	 */
	public ChangeOperationResourcesEvent(int scenarioID, int productID, int variantID, int operationID, int duration, List<Integer> newResourceList, Date throwTime) {
		this.scenarioID = scenarioID;
		this.productID = productID;
		this.variantID = variantID;
		this.operationID = operationID;
		this.duration = duration;
		this.newResourceList = newResourceList;
		this.throwTime = throwTime;
		this.oldResourceList = new ArrayList<Integer>();
	}
	
	@Override
	public Date getThrowTime() {
		return this.throwTime;
	}

	@Override
	public int getScenarioID() {
		return this.scenarioID;
	}

	@Override
	public void changeScenario(Scenario scenario) {
		// Scenario ändern:
		
		// Alte ResourceList und duration setzen:
		if(oldResourceList.isEmpty()) {
			
			for(Integer productID : scenario.getProducts().keySet()) {
				boolean tmpBreak = false;
				for(Variant v : scenario.getProduct(productID).getVariants()) {
					Operation tmpOperation = v.getOperation(this.operationID);
					if(tmpOperation != null) {
						this.setOldResourceList(tmpOperation.getResourceAlternatives());
						this.setOldDuration(tmpOperation.getDuration());
						tmpBreak = true;
						break;
					}
				}
				if(tmpBreak) {
					break;
				}
			}
		}
		
		// Operation finden:
		for(Integer productID : scenario.getProducts().keySet()) {
			boolean tmpBreak = false;
			for(Variant v : scenario.getProduct(productID).getVariants()) {
				Operation tmpOperation = v.getOperation(this.operationID);
				if(tmpOperation != null) {
					tmpOperation.setResourceAlternatives(this.newResourceList);
					tmpOperation.setDuration(this.duration);
					tmpBreak = true;
					break;
				}
			}
			if(tmpBreak) {
				break;
			}
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}

	@Override
	public Schedule getChosenSchedule() {
		return this.chosenSchedule;
	}

	@Override
	public void setChosenSchedule(Schedule chosenSchedule) {
		this.chosenSchedule = chosenSchedule;
	}

	@Override
	public List<Schedule> getCurrentSchedules() {
		return this.currentSchedules;
	}

	@Override
	public void setCurrentSchedules(List<Schedule> currentScheduleList) {
		this.currentSchedules = currentScheduleList;
	}

	@Override
	public String getName() {
		return "Change Operation";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public int getType() {
		return TYPE_CHANGE_OPERATION_RESOURCES_EVENT;
	}

	public int getOperationID() {
		return operationID;
	}
	
	public int getProductID() {
		return productID;
	}
	
	public int getVariantID() {
		return variantID;
	}

	public List<Integer> getNewResourceList() {
		return newResourceList;
	}
	
	public int getDuration() {
		return this.duration;
	}

	public List<Integer> getOldResourceList() {
		return oldResourceList;
	}

	public void setOldResourceList(List<Integer> oldResourceList) {
		this.oldResourceList = oldResourceList;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setNewResourceList(List<Integer> newResourceList) {
		this.newResourceList = newResourceList;
	}

	public int getOldDuration() {
		return oldDuration;
	}

	public void setOldDuration(int oldDuration) {
		this.oldDuration = oldDuration;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		// Operation finden:
		for(Integer productID : scenario.getProducts().keySet()) {
			boolean tmpBreak = false;
			for(Variant v : scenario.getProduct(productID).getVariants()) {
				Operation tmpOperation = v.getOperation(this.operationID);
				if(tmpOperation != null) {
					tmpOperation.setResourceAlternatives(this.oldResourceList);
					tmpOperation.setDuration(this.oldDuration);
					tmpBreak = true;
					break;
				}
			}
			if(tmpBreak) {
				break;
			}
		}
	}
	
}
