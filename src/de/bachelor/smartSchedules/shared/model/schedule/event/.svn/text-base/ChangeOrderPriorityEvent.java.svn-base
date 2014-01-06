package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Falls die Priorität von Ordern geändert wurde.
 * @author timo
 *
 */
public class ChangeOrderPriorityEvent implements ScheduleEvent {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2105636561371351914L;
	private List<ScheduleOrder> orderList;
	private Map<Integer, Integer> oldPriorities;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public ChangeOrderPriorityEvent() {
		this.orderList = new ArrayList<ScheduleOrder>();
		this.oldPriorities = new HashMap<Integer, Integer>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param orderList
	 * @param throwTime
	 */
	public ChangeOrderPriorityEvent(int scenarioID, List<ScheduleOrder> orderList, Date throwTime) {
		this.oldPriorities = new HashMap<Integer, Integer>();
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.orderList = orderList;
		this.throwTime = throwTime;
	}

	public ChangeOrderPriorityEvent(int scenarioID, ScheduleOrder order, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.orderList.add(order);
		this.throwTime = throwTime;
	}
	
	public List<ScheduleOrder> getChangeOrders() {
		return this.orderList;
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
		for(ScheduleOrder so : orderList) {
			ScheduleOrder tmpSO = scenario.getOrder(so.getOrderID());
			if(tmpSO != null) {
				// Alten Wert speichern:
				this.oldPriorities.put(tmpSO.getOrderID(), tmpSO.getPriority());
				
				// Ändern
				tmpSO.setPriority(so.getPriority());
			}
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Change Order Priority";
	}

	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT;
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

	/**
	 * @return Map<Integer = OrderID, Integer = Old Priority>
	 */
	public Map<Integer, Integer> getOldPriorities() {
		return oldPriorities;
	}

	public void setOldPriorities(Map<Integer, Integer> oldPriorities) {
		this.oldPriorities = oldPriorities;
	}

	public List<ScheduleOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<ScheduleOrder> orderList) {
		this.orderList = orderList;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		for(Integer orderID : this.oldPriorities.keySet()) {
			scenario.getOrder(orderID).setPriority(this.oldPriorities.get(orderID));
		}
	}
}
