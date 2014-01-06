package de.bachelor.smartSchedules.shared.model.schedule.keyfigures;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Berechnet die mittlere Verspätung eines Schedules.
 * @author timo
 *
 */
public class MeanLateness extends KeyFigure{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -6283270220840372940L;

	public MeanLateness() {
		this.id = 2;
	}
	
	@Override
	public int calculate(Scenario scenario, Schedule schedule) throws NotPlannedException {
		if(schedule.getLateOrders(scenario).isEmpty()) {
			return 0;
		}
		
		return (int)schedule.getSummedLateness(scenario) / schedule.getLateOrders(scenario).size();
	}

	@Override
	public String getName() {
		return "Mean Lateness";
	}

	@Override
	public String getDescription() {
		return "Mean Lateness Description. t.b.d.";
	}
	
	@Override
	public int doCompare(int thisValue, int thatValue) {
		if(thisValue < thatValue) return KeyFigure.BETTER;
		else if(thisValue > thatValue) return KeyFigure.WORSE;
		return KeyFigure.EQUAL;
	}
}
