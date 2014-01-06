package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.novanic.eventservice.client.event.Event;
/**
 * Interface für alle ScheduleEvents.
 * @author timo
 *
 */
public interface ScheduleEvent extends Serializable, Event {
	public static final int TYPE_NEW_ORDERS_EVENT 					= 0,
							TYPE_NEW_PRODUCTS_EVENT 				= 1,
							TYPE_NEW_VARIANTS_EVENT 				= 2,
							TYPE_NEW_RESOURCE_EVENT 				= 3,
							TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT 		= 4,
							TYPE_REMOVE_ORDERS_PRODUCTS_EVENT 		= 5,
							TYPE_CHANGE_ORDERS_PRIORITY_EVENT 		= 6,
							TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT = 7,
							TYPE_CHANGE_OPERATION_RESOURCES_EVENT 	= 8,
							TYPE_MACHINE_BREAK_DOWNS_EVENT			= 9,
							TYPE_MACHINES_REPAIRED_EVENT 			= 10,
							TYPE_MAINTENACE_PERIODS_EVENT 			= 11,
							TYPE_REMOVE_ORDERS_EVENT 				= 12,
							TYPE_REMOVE_PRODUCTS_EVENT 				= 13,
							TYPE_REMOVE_VARIANTS_EVENT 				= 14,
							TYPE_SCHEDULE_CHANGE_BY_USER_EVENT		= 15,
							TYPE_CHANGE_ORDERS_START_TIMES_EVENT 	= -100;
	
	/**
	 * Gibt die Zeit an wann das Event ausgelöst werden soll oder ausgelöst wurde.
	 * @return
	 */
	public Date getThrowTime();
	
	public int getScenarioID();
	
	/**
	 * Hier wird die Auswirkung auf das
	 * Szenario bestimmt.
	 * Client und Serverseitig aufrufbar.
	 */
	public void changeScenario(Scenario scenario);
	
	public Schedule getChosenSchedule();
	
	public void setChosenSchedule(Schedule chosenSchedule);
	
	public List<Schedule> getCurrentSchedules();
	
	public void setCurrentSchedules(List<Schedule> currentScheduleList);
	
	public String getName();

	public String getDescription();
	
	public int getType();
	
	/**
	 * Macht die Änderung dieses Event am Scenario rückgäng.
	 * Sinnvoll für die Eventhistory.
	 * @param scenario
	 */
	public void changeScenarioBackwards(Scenario scenario);
}
