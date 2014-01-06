package de.bachelor.smartSchedules.shared.model.schedule.keyfigures;

import java.io.Serializable;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Interface für alle Kennzahlen der Evaluation.
 * @author timo
 *
 */
public abstract class KeyFigure implements Serializable{
	
	/**
	 * Seriennummer
	 */
	protected int id;
	private static final long serialVersionUID = -2754008868862977911L;
	
	public static final int BETTER = 1,
							EQUAL = 0,
							WORSE = -1,
							NOT_COMPARABLE = -2;
	
	/**
	 * Gibt eine Kennzahl zu einem Schedule aus.
	 * @param schedule
	 * @return
	 * @throws NotPlannedException 
	 */
	public abstract int calculate(Scenario scenario, Schedule schedule) throws NotPlannedException;
	
	/**
	 * Gibt den Namen des KeyFigures aus.
	 * @return
	 */
	public abstract String getName();
	
	public abstract String getDescription();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int compareTo(Scenario scenario, Schedule schedule1, Schedule schedule2) {
		try {
			int thisValue = this.calculate(scenario, schedule1);
			int thatValue = this.calculate(scenario, schedule2);
			
			return doCompare(thisValue, thatValue);
			
		} catch (NotPlannedException e) {
			e.printStackTrace();
			return KeyFigure.NOT_COMPARABLE;
		}
	}
	
	public abstract int doCompare(int thisValue, int thatValue);
}
