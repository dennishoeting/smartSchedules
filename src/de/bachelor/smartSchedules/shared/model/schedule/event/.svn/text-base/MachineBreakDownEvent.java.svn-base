package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Wenn eine Maschine kaputt ist und nicht bekannt ist,
 * wann sie wieder läuft. Oder wenn ein BreakDown aufgehoben wurde!
 * @author timo
 *
 */
public class MachineBreakDownEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2693468932991233936L;
	private List<Resource> resourceBreakDowns;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public MachineBreakDownEvent() {
		this.resourceBreakDowns = new ArrayList<Resource>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param resourceList
	 * @param throwTime
	 */
	public MachineBreakDownEvent(int scenarioID, List<Resource> resourceBreakDowns, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.resourceBreakDowns = resourceBreakDowns;
		this.throwTime = throwTime;
	}
	

	public MachineBreakDownEvent(int scenarioID, Resource resourceBreakDowns, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.resourceBreakDowns.add(resourceBreakDowns);
		this.throwTime = throwTime;
	}
	
	public List<Resource> getResourceBreakDowns() {
		return this.resourceBreakDowns;
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
		for(Resource r : resourceBreakDowns) {
			scenario.getResources().get(r.getResourceID()).setBreakDown(true);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Machine Break Down";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT;
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
		for(Resource r : resourceBreakDowns) {
			scenario.getResources().get(r.getResourceID()).setBreakDown(false);
		}
	}
}
