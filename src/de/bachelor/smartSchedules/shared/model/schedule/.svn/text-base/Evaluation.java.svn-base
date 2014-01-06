package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
/**
 * Klasse mit allen Methoden zur Evaluation
 * @author timo
 *
 */
public class Evaluation implements Serializable {
	
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4780538657796377838L;
	private List<KeyFigure> priorityList;

	
	/**
	 * Default
	 */
	public Evaluation() {
		
	}
	
	/**
	 * Mit unterschiedlicher Evaluations Reihenfolge für
	 * Priorität ergibt sich aus der reihenfolge der Liste.
	 * @param priorityList
	 */
	public Evaluation(List<KeyFigure> priorityList) {
		this.priorityList = priorityList;
	}

	/**
	 * Gibt den/die beste/n Schedule aus.
	 * @param schedule
	 * @return
	 * @throws NotPlannedException 
	 */
	public List<Schedule> getBestSchedule(Scenario scenario, List<Schedule> schedules) throws NotPlannedException {
		
		//ParetoList durchlaufen:
		for(KeyFigure kf : priorityList) {
			
			schedules = getBestSchedules(scenario, kf, schedules);
			// Falls es nur eine gibt, ist dieser der beste.
			if(schedules.size() == 1) {
				return schedules;
			}
			
		}
		
		return schedules;
	}
	
	/**
	 * Evaluiert die Pläne nach einer bestimmten Zielfunktion und gibt
	 * den/die Besten aus.
	 * @param choice
	 * @param schedules
	 * @return
	 * @throws NotPlannedException 
	 */
	private List<Schedule> getBestSchedules(Scenario scenario, KeyFigure concreteKeyFigure, List<Schedule> schedules) throws NotPlannedException {
		// Wrapper Klasse, soll eine Evaluation zu einem Schedule kurzzeitig speichern.
		class ScheduleWrapper implements Serializable{
			/**
			 * Seriennummer
			 */
			private static final long serialVersionUID = 1721432626407200777L;
			private Schedule schedule;
			private int evaluation;
			
			ScheduleWrapper(Schedule schedule, int evaluation) {
				this.schedule = schedule;
				this.evaluation = evaluation;
			}
			
			int getEvaluation() {
				return evaluation;
			}
			
			Schedule getSchedule() {
				return schedule;
			}	
		}
		
		List<ScheduleWrapper> tmpSchedules = new ArrayList<ScheduleWrapper>();
		// Für jeden Schedule die Kennzahl berechnen
		for(Schedule s : schedules) {
			tmpSchedules.add(new ScheduleWrapper(s, concreteKeyFigure.calculate(scenario, s)));
		}
		
		final KeyFigure tmpConcreteKeyFigure = concreteKeyFigure;
		
		//Liste sortieren:
		Collections.sort(tmpSchedules, new Comparator<ScheduleWrapper>() {

			@Override
			public int compare(ScheduleWrapper arg0, ScheduleWrapper arg1) { 
				if(arg0.getEvaluation() == arg1.getEvaluation()) {
					return 0;
				}
				// arg0 ist besser:
				if(arg0.getEvaluation() == tmpConcreteKeyFigure.doCompare(arg0.getEvaluation(), arg1.getEvaluation())) {
					return -1;
				// arg1 ist besser:	
				} else {
					return 1;
				}
			}
		});
		
		//Die kleinsten Werte auswählen:
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		
		//Kleinsten Wert direkt eintragen:
		scheduleList.add(tmpSchedules.get(0).getSchedule());
		
		//Schauen, ob weitere Werte gleich groß sind:
		for(int i = 1; i < tmpSchedules.size(); i++) {
			if(tmpSchedules.get(i).getEvaluation() == tmpSchedules.get(0).getEvaluation()) {
				scheduleList.add(tmpSchedules.get(i).getSchedule());
			} else {
				break;
			}
		}
		
		return scheduleList;
	}
}
