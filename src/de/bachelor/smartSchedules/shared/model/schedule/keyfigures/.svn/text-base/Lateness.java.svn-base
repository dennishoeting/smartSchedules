package de.bachelor.smartSchedules.shared.model.schedule.keyfigures;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Gibt die summierte Verspätung eines Schedules aus.
 * @author timo
 *
 */
public class Lateness extends KeyFigure{

	/**
	 * Seriennumer
	 */
	private static final long serialVersionUID = 4706611965175964727L;
	
	public Lateness() {
		this.id=1;
	}

	@Override
	public int calculate(Scenario scenario, Schedule schedule) throws NotPlannedException {
		return (int)schedule.getSummedLateness(scenario);
	}

	@Override
	public String getName() {
		return "Summed Lateness";
	}

	@Override
	public String getDescription() {
		return "Lateness Description. t.b.d.";
	}
	
	@Override
	public int doCompare(int thisValue, int thatValue) {
		if(thisValue < thatValue) return KeyFigure.BETTER;
		else if(thisValue > thatValue) return KeyFigure.WORSE;
		return KeyFigure.EQUAL;
	}
}
