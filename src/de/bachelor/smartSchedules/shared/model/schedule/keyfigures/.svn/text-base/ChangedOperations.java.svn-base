package de.bachelor.smartSchedules.shared.model.schedule.keyfigures;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Gibt die Anzahl der Umbelegungen an.
 * @author timo
 *
 */
public class ChangedOperations extends KeyFigure{
	/**
	 * Seriennummer
	 */
	public ChangedOperations() {
		this.id=0;
	}
	
	private static final long serialVersionUID = -6663410620995612713L;

	@Override
	public int calculate(Scenario scenario, Schedule schedule) throws NotPlannedException {
		Schedule initialSchedule = schedule.getAlgorithmInformations().getInitialSchedule(scenario);
		int tmpChanges = 0;
		
		// Ist nicht der erste Plan:
		if(initialSchedule != null) {
			// Belegungen für jede Resource vergleichen:
			for(Resource initialResource : initialSchedule.getResources(scenario).keySet()) {
				for(Allocation initalAllocation : initialSchedule.getResources(scenario).get(initialResource)) {
					// Wenn die Belegung sich verändert hat: zählen
					if(!schedule.checkForSameAllocation(scenario, initialResource, initalAllocation)) {
						tmpChanges++;
					}
				}
			}
		}
		
		return tmpChanges;
	}

	@Override
	public String getName() {
		return "Operation Changes";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public int doCompare(int thisValue, int thatValue) {
		if(thisValue < thatValue) return KeyFigure.BETTER;
		else if(thisValue > thatValue) return KeyFigure.WORSE;
		return KeyFigure.EQUAL;
	}
}
