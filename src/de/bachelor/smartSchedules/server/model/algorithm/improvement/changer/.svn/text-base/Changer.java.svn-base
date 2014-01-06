package de.bachelor.smartSchedules.server.model.algorithm.improvement.changer;

import java.util.Date;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.Wrapper;
import de.bachelor.smartSchedules.shared.model.schedule.HardConstraints;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Stellt die Abstrakte Oberklasse für alle Changer dar.
 * Diese sollen einen initialen Schedule verbessern.
 * Dazu muss die Abstrakte Methode findNeighbor überschrieben werden.
 * @author dennis/timo
 *
 */
public abstract class Changer {
	
	private static int MINUTE = 60000;
	
	/**
	 * Liefert den Namen des Algorithmus
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Verbessert einen Schedule
	 * @param wrapper
	 * @param scenario
	 * @param schedule
	 * @return
	 */
	public Schedule findNeighbor(Wrapper wrapper, Scenario scenario, Schedule initialSchedule) {
		
		Date scheduleChangeDeadline = new Date(initialSchedule.getStart().getTime() + (scenario.getScheduleChangeDeadline() * MINUTE));
		
		while(wrapper.isRunning()) {
			
			Schedule newSchedule =  findNeighbor(scenario, initialSchedule);
			
			// Wenn die Hard Constraints erfüllt sind, dnan zurück geben:
			if(HardConstraints.getInstance().testHardConstraints(scenario, newSchedule, scheduleChangeDeadline)) {
				return newSchedule;
			}
			
		}
		
		// Keinen besseren gefunden
		return initialSchedule;
	}
	
	/**
	 * Sucht irgendeine Nachbarlösung.
	 * @param scenario
	 * @param initialSchedule
	 * @return
	 */
	protected abstract Schedule findNeighbor(Scenario scenario, Schedule initialSchedule);
}
