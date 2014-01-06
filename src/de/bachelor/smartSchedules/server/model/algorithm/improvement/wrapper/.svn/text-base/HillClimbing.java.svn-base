package de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Evaluation;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Implementation des Hill Climbing Algorithmus
 * @author timo
 * TODO hinzufügen, irgendwo..
 */
public class HillClimbing extends Wrapper{
	
	private static final String name = "Hill Climbing";
	
	public HillClimbing() {
		
	}
	
	public HillClimbing(Changer changer, Scenario scenario,
			Schedule initialSchedule, int duration,
			HashMap<Integer, KeyFigure> keyFigureMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap) {
		super(changer, scenario, initialSchedule, duration, keyFigureMap,
				keyFigureFlexibilityMap);
	}

	@Override
	public boolean chooseNewSchedule(Schedule oldSchedule, Schedule newSchedule) {
		List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
		try {
			if(new Evaluation().getBestSchedule(
					scenario, tmpScheduleList).get(0).getAlgorithmInformations().getDueTime().
					equals(newSchedule.getAlgorithmInformations().getDueTime())) {
				return true;
			}
		} catch (NotPlannedException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public String getName() {
		return name;
	}
	
}
