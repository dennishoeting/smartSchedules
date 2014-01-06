package de.bachelor.smartSchedules.server.model.algorithm.predictive;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;

/**
 * Oberklasse für alle Prädiktiven Algorithmen
 * @author timo
 *
 */
public abstract class PredictiveAlgorithm {
	
	protected static final int SECOND = 1000;
	//protected static final int MINUTE = 60000;
	
	/**
	 * 
	 * @param scenario
	 * @param initialSchedule. Falls es keinen gibt: null
	 * @return
	 */
	public Schedule produceSchedule(Scenario scenario, ScheduleEvent scheduleEvent, Date scheduleChangeDeadline) {
		// Startzeit festhalten:
		Date startTime = new Date();

		Schedule tmpSchedule = produceScheduleInternal(scenario, scheduleChangeDeadline, scheduleEvent);
		
		if(scenario.getChosenSchedule() == null) {
			tmpSchedule.setAlgorithmInformations(new AlgorithmInformation(this.getAlgorithmName(), startTime, new Date()));
		} else {
			tmpSchedule.setAlgorithmInformations(new AlgorithmInformation(this.getAlgorithmName(), startTime, new Date(), scenario.getChosenSchedule()));
		}

		return tmpSchedule;
	}
	
	/**
	 * Erzeugt eine leere Map für
	 * Ressourcen/Belegungen.
	 * @param resources
	 * @return
	 */
	protected Map<Resource, List<Allocation>> produceResourceMap(Map<Integer, Resource> resources) {
		
		Map<Resource, List<Allocation>> resourceMap = new HashMap<Resource, List<Allocation>>();
		// Resourcen Einfügen:
		for(Integer i : resources.keySet()) {
			resourceMap.put(resources.get(i), new ArrayList<Allocation>());
		}
		
		return resourceMap;
	}
		
	/**
	 * Die jeweilige Programmlogik.
	 * @param orderList
	 * @return
	 */
	protected abstract Schedule produceScheduleInternal(Scenario scenario, Date scheduleChangeDeadline, ScheduleEvent scheduleEvent);
	
	/**
	 * Namen vom Algorithmus.
	 * @return
	 */
	public abstract String getAlgorithmName();
}
