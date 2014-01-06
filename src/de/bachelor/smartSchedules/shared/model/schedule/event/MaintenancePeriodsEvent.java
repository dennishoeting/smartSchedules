package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * 
 * @author timo
 *
 */
public class MaintenancePeriodsEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 5785139223775242462L;
	private List<ResourceRestriction> maintenancePeriods;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public MaintenancePeriodsEvent() {
		this.maintenancePeriods = new ArrayList<ResourceRestriction>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param maintenancePeriods
	 * @param throwTime
	 */
	public MaintenancePeriodsEvent(int scenarioID, List<ResourceRestriction> maintenancePeriods, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.maintenancePeriods = maintenancePeriods;
		this.throwTime = throwTime;
	}
	
	public MaintenancePeriodsEvent(int scenarioID, ResourceRestriction maintenancePeriods, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.maintenancePeriods.add(maintenancePeriods);
		this.throwTime = throwTime;
	}
	
	public List<ResourceRestriction> getMaintenancePeriods() {
		return this.maintenancePeriods;
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
		for(ResourceRestriction rr : maintenancePeriods) {
			scenario.getResources().get(rr.getResourceID()).addRestriction(rr);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Set Maintenace Period";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT;
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
		for(ResourceRestriction rr : this.maintenancePeriods) {
			scenario.getResources().get(rr.getResourceID()).getResourceRestrictions().remove(rr);
		}
	}
}
