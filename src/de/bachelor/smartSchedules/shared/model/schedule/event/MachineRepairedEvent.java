package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Wenn eine Maschine wieder heile ist.
 * @author timo
 *
 */
public class MachineRepairedEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2693468932991233936L;
	private List<Resource> resourceRepairs;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public MachineRepairedEvent() {
		this.resourceRepairs = new ArrayList<Resource>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param resourceList
	 * @param throwTime
	 */
	public MachineRepairedEvent(int scenarioID, List<Resource> resourceRepairs, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.resourceRepairs = resourceRepairs;
		this.throwTime = throwTime;
	}
	
	public MachineRepairedEvent(int scenarioID, Resource resourceRepairs, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.resourceRepairs.add(resourceRepairs);
		this.throwTime = throwTime;
	}
	
	public List<Resource> getResourceRepairs() {
		return this.resourceRepairs;
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
		for(Resource r : resourceRepairs) {
			scenario.getResources().get(r.getResourceID()).setBreakDown(false);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Machine Repaired";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT;
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
		for(Resource r : resourceRepairs) {
			scenario.getResources().get(r.getResourceID()).setBreakDown(true);
		}
	}
}
