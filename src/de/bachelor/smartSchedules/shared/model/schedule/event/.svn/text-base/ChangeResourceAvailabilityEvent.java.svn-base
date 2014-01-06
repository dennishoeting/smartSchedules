package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Wenn eine Maschine kaputt ist und nicht bekannt ist,
 * wann sie wieder läuft. Oder wenn ein BreakDown aufgehoben wurde!
 * @author timo
 *
 */
public class ChangeResourceAvailabilityEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2693468932991233936L;
	private Map<Integer, Integer> resourceAvailabilityMap;
	private Map<Integer, Integer> oldResourceAvailabilityMap;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public ChangeResourceAvailabilityEvent() {
		this.resourceAvailabilityMap = new HashMap<Integer, Integer>();
		chosenSchedule = null;
		currentSchedules = null;
		oldResourceAvailabilityMap = new HashMap<Integer, Integer>();
	}
	
	/**
	 * 
	 * @param scenario
	 * @param resourceList
	 * @param throwTime
	 */
	public ChangeResourceAvailabilityEvent(int scenarioID, Map<Resource, Integer> resourceAvailabilityMap, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		Map<Integer, Integer> tmpMap = new HashMap<Integer, Integer>();
		for(Resource r : resourceAvailabilityMap.keySet()) {
			tmpMap.put(r.getResourceID(), resourceAvailabilityMap.get(r));
		}
		this.resourceAvailabilityMap = tmpMap;
		this.throwTime = throwTime;
	}
	
	public ChangeResourceAvailabilityEvent(int scenarioID, Resource resource, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.resourceAvailabilityMap.put(resource.getResourceID(), resource.getAvailability());
		this.throwTime = throwTime;
	}
	
	public Map<Integer, Integer> getChangedResourceAvailabilities() {
		return this.resourceAvailabilityMap;
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
		for(Integer resourceID : resourceAvailabilityMap.keySet()) {
			// Alten Wert speichern:
			this.oldResourceAvailabilityMap.put(resourceID, scenario.getResources().get(resourceID).getAvailability());
			
			// Ändern
			scenario.getResources().get(resourceID).setAvailability(resourceAvailabilityMap.get(resourceID));
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Change Resource Availability";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT;
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

	public Map<Integer, Integer> getResourceAvailabilityMap() {
		return resourceAvailabilityMap;
	}

	public void setResourceAvailabilityMap(
			Map<Integer, Integer> resourceAvailabilityMap) {
		this.resourceAvailabilityMap = resourceAvailabilityMap;
	}

	public Map<Integer, Integer> getOldResourceAvailabilityMap() {
		return oldResourceAvailabilityMap;
	}

	public void setOldResourceAvailabilityMap(
			Map<Integer, Integer> oldResourceAvailabilityMap) {
		this.oldResourceAvailabilityMap = oldResourceAvailabilityMap;
	}

	public void setThrowTime(Date throwTime) {
		this.throwTime = throwTime;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		for(Integer resourceID : this.oldResourceAvailabilityMap.keySet()) {
			scenario.getResources().get(resourceID).setAvailability(this.oldResourceAvailabilityMap.get(resourceID));
		}
	}
}
