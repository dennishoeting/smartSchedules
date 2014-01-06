package de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper;

import java.util.HashMap;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
/**
 * Nimmt nur Schedules, wo die selektierten Kennzahlen besser sind
 * und die gesperrten nicht schlechter.
 * @author dennis/timo
 *
 */
public class Greedy extends Wrapper {
	
	private static final String name = "Greedy";
	
	public Greedy() {
		
	}
	
	public Greedy(Changer changer, Scenario scenario, Schedule initialSchedule,
			int duration, HashMap<Integer, KeyFigure> keyFigureMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap) {
		super(changer, scenario, initialSchedule, duration, keyFigureMap,
				keyFigureFlexibilityMap);
	}

	@Override
	public boolean chooseNewSchedule(Schedule oldSchedule, Schedule newSchedule) {
		
		boolean isAppropriate = true;
		
		/*
		 * Jede KeyFigure testen hinsichtlich der durch den Nutzer eingestellten selection/locks
		 */
		for(Integer i : keyFigureMap.keySet()) {
			/*
			 * Wenn Selektiert ist schedule nur gültig, wenn Kennzahl besser
			 */
			if(keyFigureFlexibilityMap.get(i) == Wrapper.SELECTED) {
				isAppropriate = (keyFigureMap.get(i).compareTo(scenario, oldSchedule, newSchedule)==KeyFigure.BETTER)?true:false;
			/*
			 * Wenn Locked ist schedule nur gültig, wenn Kennzahl nicht schlechter
			 */
			} else if(keyFigureFlexibilityMap.get(i) == Wrapper.LOCKED) {
				isAppropriate = (keyFigureMap.get(i).compareTo(scenario, oldSchedule, newSchedule)==KeyFigure.WORSE)?false:true;
			}
		}
		
		return isAppropriate;
	}

	@Override
	public String getName() {
		return name;
	}

}
