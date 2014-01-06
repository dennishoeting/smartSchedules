package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Wenn es neue Varianten gibt.
 * @author timo
 *
 */
public class NewResourceEvent implements ScheduleEvent {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -8475341265311616660L;
	private List<Resource> resources;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public NewResourceEvent() {
		this.resources = new ArrayList<Resource>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param resources
	 * @param throwTime
	 */
	public NewResourceEvent(int scenarioID, List<Resource> resources, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.resources = resources;
		this.throwTime = throwTime;
	}
	
	public NewResourceEvent(int scenarioID, Resource resource, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.resources.add(resource);
		this.throwTime = throwTime;
	}
	
	public List<Resource> getNewResources() {
		return this.resources;
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
		for(Resource r : resources) {
			scenario.addResource(r);
		}
		
		// Größte ResourceID finden und setzen:
		int tmpResourceID = -1;
		for(Resource r : resources) {
			if(r.getResourceID() > tmpResourceID) {
				tmpResourceID = r.getResourceID();
			}
		}
		if(scenario.getNewResourceIDCount() < tmpResourceID) {
			scenario.setNewResourceIDCount(tmpResourceID);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "New Resource";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_NEW_RESOURCE_EVENT;
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
		for(Resource r : resources) {
			scenario.getResources().remove(r.getResourceID());
		}
	}
}
