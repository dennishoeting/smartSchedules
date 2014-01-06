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
 * Falls die Produkte zu einer Order geändert werden.
 * @author timo
 *
 */
public class ChangeOrderDueTimeEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2105636561371351914L;
	private List<ScheduleOrder> orderList;
	private Map<Integer, Date> oldDueTimes;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public ChangeOrderDueTimeEvent() {
		this.orderList = new ArrayList<ScheduleOrder>();
		this.oldDueTimes = new HashMap<Integer, Date>();
		this.chosenSchedule = null;
		this.currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param orderList Liste mit den geänderten Aufträgen
	 * @param throwTime
	 */
	public ChangeOrderDueTimeEvent(int scenarioID, List<ScheduleOrder> orderList, Date throwTime) {
		this.chosenSchedule = null;
		this.currentSchedules = null;
		this.scenarioID = scenarioID;
		this.orderList = orderList;
		this.throwTime = throwTime;
		this.oldDueTimes = new HashMap<Integer, Date>();
	}
	
	public ChangeOrderDueTimeEvent(int scenarioID, ScheduleOrder order, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.orderList.add(order);
		this.throwTime = throwTime;
	}
	
	public List<ScheduleOrder> getChangedOrders() {
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
		
		// Gleichzeitig oldDueTimes aufbauen.
		
		for(ScheduleOrder so: orderList) {
			ScheduleOrder tmpSO = scenario.getOrder(so.getOrderID());
			if(tmpSO != null) {
				
				// Alten Wert speichen
				this.oldDueTimes.put(tmpSO.getOrderID(), new Date(tmpSO.getEarlistDueTime().getTime()));
				
				// Ändern
				tmpSO.setEarlistDueTime(so.getEarlistDueTime());
				
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
		return "Change Order Due Time";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT;
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

	public List<ScheduleOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<ScheduleOrder> orderList) {
		this.orderList = orderList;
	}

	public Map<Integer, Date> getOldDueTimes() {
		return oldDueTimes;
	}

	/**
	 * @param oldDueTimes Map<Integer = OrderID, Date = OldDueTime>
	 */
	public void setOldDueTimes(Map<Integer, Date> oldDueTimes) {
		this.oldDueTimes = oldDueTimes;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		for(Integer orderID : this.oldDueTimes.keySet()) {
			scenario.getOrder(orderID).setEarlistDueTime(this.oldDueTimes.get(orderID));
		}
	}
}
