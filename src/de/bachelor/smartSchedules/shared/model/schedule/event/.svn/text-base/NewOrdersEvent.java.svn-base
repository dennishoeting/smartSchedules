package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * ScheduleEvent, falls neue Aufträge vorhanden sind.
 * @author timo
 *
 */
public class NewOrdersEvent implements ScheduleEvent{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2624695943387718362L;
	private List<ScheduleOrder> orderList;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public NewOrdersEvent() {
		orderList = new ArrayList<ScheduleOrder>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	public NewOrdersEvent(int scenarioID, List<ScheduleOrder> orderList, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.orderList = orderList;
		this.throwTime = throwTime;
	}
	
	public NewOrdersEvent(int scenarioID, ScheduleOrder order, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.orderList.add(order);
		this.throwTime = throwTime;
	}
	
	public List<ScheduleOrder> getNewOrdersList() {
		return orderList;
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
		scenario.addOrders(orderList);
		
		// Höchste orderID finden:
		int tmpOrderID = -1;
		for(ScheduleOrder so : this.orderList) {
			if(so.getOrderID() > tmpOrderID) {
				tmpOrderID = so.getOrderID();
			}
		}
		
		// Falls die OrderID im Scenario kleiner ist, erhöhen:
		if(scenario.getNewOrderIDCount() < tmpOrderID) {
			scenario.setNewOrderIDCount(tmpOrderID);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "New Order";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_NEW_ORDERS_EVENT;
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
	public void changeScenarioBackwards(Scenario scenario) {
		for(ScheduleOrder so : orderList) {
			scenario.removeOrder(so.getOrderID());
		}
	}
}
