package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Falls ein User einen anderen Schedule als chosenSchedule wählt.
 * @author timo
 *
 */
public class ScheduleChangeByUserEvent implements ScheduleEvent {
	private static final long serialVersionUID = 220066448217050669L;
	
	private int scenarioID;
	private Schedule newSchedule;
	private Schedule oldSchedule;
	private Date throwTime;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	public ScheduleChangeByUserEvent() {
		this.scenarioID = -1;
		this.newSchedule = null;
		this.throwTime = null;
	}
	
	public ScheduleChangeByUserEvent(int scenarioID, Date throwTime) {
		this.scenarioID = scenarioID;
		this.oldSchedule = null;
		this.newSchedule = null;
		this.throwTime = throwTime;
	}
	
	public ScheduleChangeByUserEvent(int scenarioID, Schedule oldSchedule, Schedule newSchedule, Date throwTime) {
		this.scenarioID = scenarioID;
		this.oldSchedule = oldSchedule;
		this.newSchedule = newSchedule;
		this.throwTime = throwTime;
	}

	@Override
	public Date getThrowTime() {
		return throwTime;
	}

	@Override
	public int getScenarioID() {
		return scenarioID;
	}

	@Override
	public void changeScenario(Scenario scenario) {
		this.oldSchedule = scenario.getChosenSchedule();
		scenario.setChosenSchedule(newSchedule);
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
	public String getName() {
		return "User dependend schedule change";
	}

	@Override
	public String getDescription() {
		return "User has changed the current schedule";
	}

	@Override
	public int getType() {
		return TYPE_SCHEDULE_CHANGE_BY_USER_EVENT;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		scenario.setChosenSchedule(oldSchedule);
	}

	public Schedule getNewSchedule() {
		return newSchedule;
	}

	public Schedule getOldSchedule() {
		return oldSchedule;
	}

	public void setNewSchedule(Schedule newSchedule) {
		this.newSchedule = newSchedule;
	}

	public void setOldSchedule(Schedule oldSchedule) {
		this.oldSchedule = oldSchedule;
	}
}
