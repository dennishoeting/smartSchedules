package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Event für gelöschte Orders
 * @author timo
 *
 */
public class RemoveOrdersEvent implements ScheduleEvent {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 7610649182932142091L;
	private List<ScheduleOrder> orderList;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public RemoveOrdersEvent() {
		orderList = new ArrayList<ScheduleOrder>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param orderList
	 * @param throwTime
	 */
	public RemoveOrdersEvent(int scenarioID, List<ScheduleOrder> orderList, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.orderList = orderList;
		this.throwTime = throwTime;
	}
	
	public RemoveOrdersEvent(int scenarioID, ScheduleOrder order, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.orderList.add(order);
		this.throwTime = throwTime;
	}
	
	/**
	 * Gibt die zu löschenden Orders aus.
	 * @return
	 */
	public List<ScheduleOrder> getRemoveOrdersList() {
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
			scenario.removeOrder(so.getOrderID());
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Remove Order";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT;
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
		scenario.addOrders(this.orderList);
	}
}
