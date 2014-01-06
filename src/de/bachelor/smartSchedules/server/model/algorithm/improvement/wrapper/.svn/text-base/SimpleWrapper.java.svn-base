package de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper;

import java.util.HashMap;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
/**
 * Der SimpleWrapper nimmt jeden Schedule
 * @author dennis/timo
 *
 */
public class SimpleWrapper extends Wrapper {
	
	private static final String name = "SimpleWrapper";
	
	public SimpleWrapper() {
		
	}
	
	public SimpleWrapper(Changer changer, Scenario scenario,
			Schedule initialSchedule, int duration,
			HashMap<Integer, KeyFigure> keyFigureMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap) {
		super(changer, scenario, initialSchedule, duration, keyFigureMap,
				keyFigureFlexibilityMap);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean chooseNewSchedule(Schedule oldSchedule, Schedule newSchedule) {
		return true;
	}
}
