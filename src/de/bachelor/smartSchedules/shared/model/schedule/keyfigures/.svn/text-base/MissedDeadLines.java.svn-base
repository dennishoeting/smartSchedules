package de.bachelor.smartSchedules.shared.model.schedule.keyfigures;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Gibt die Anzahl der nicht eingehaltenen
 * Termine aus.
 * @author timo
 *
 */
public class MissedDeadLines extends KeyFigure{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 7781594756140296377L;
	
	public MissedDeadLines() {
		this.id=3;
	}

	@Override
	public int calculate(Scenario scenario, Schedule schedule) throws NotPlannedException {
		return schedule.getLateOrders(scenario).size();
	}

	@Override
	public String getName() {
		return "Missed Deadlines";
	}

	@Override
	public String getDescription() {
		return "Missed Dead Lines Description. t.b.d.";
	}
	
	@Override
	public int doCompare(int thisValue, int thatValue) {
		if(thisValue < thatValue) return KeyFigure.BETTER;
		else if(thisValue > thatValue) return KeyFigure.WORSE;
		return KeyFigure.EQUAL;
	}
}
