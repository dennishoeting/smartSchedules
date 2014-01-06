package de.bachelor.smartSchedules.server.model.algorithm.improvement.changer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Nimmt eine PlannedVariant aus dem Schedule und plant diese
 * neu ein.
 * @author timo
 *
 */
public class PlannedVariantReScheduler extends Changer{

	private static final int MINUTE = 60000;
	private static final String NAME = "PlannedVariantRescheduler";
	
	@Override
	public Schedule findNeighbor(Scenario scenario, Schedule schedule) {

		Schedule tmpSchedule = schedule.clone(scenario);
		Date scheduleChangeDeadline = new Date(schedule.getStart().getTime() + (scenario.getScheduleChangeDeadline() * MINUTE));

		// Zufällige PV wählen:
		PlannedVariant tmpPlannedVariant = tmpSchedule.getPlannedVariants().get((int)(Math.random() * (tmpSchedule.getPlannedVariants().size())));

		// PV löschen:
		tmpSchedule.getPlannedVariants().remove(tmpPlannedVariant);

		Map<Resource, List<Allocation>> tmpAllocationResourceMap = tmpSchedule.getResources(scenario);

		// Allocations neuplanen:
		for(Allocation a : tmpPlannedVariant.getAllocationList()) {
			//Allocation einplanen
			Resource chosenResource = null;
			Date chosenTime = null;
			Date firstPossibleDate = null;
			Date earliestStartTime = new Date(scheduleChangeDeadline.getTime());

			// mit Vorgänger
			if(!a.getAllocationPredecessors().isEmpty()) {
				//Für jeden Vorgänger
				for(Allocation predecessorOfAllocation : a.getAllocationPredecessors()) {
					if(predecessorOfAllocation.getFinish(scenario).after(earliestStartTime)) {
						earliestStartTime = new Date(predecessorOfAllocation.getFinish(scenario).getTime() +MINUTE);
					}
				}
			} 

			//Für jede Ressource
			for(Integer resourceID : a.getOperation().getResourceAlternatives()) {

				if(scenario.getResources().get(resourceID).isInBreakDown()) {
					continue;
				}

				//Finde Schlupfzeit
				firstPossibleDate = new Date(scenario.getResources().get(resourceID).findFirstPossibleAllocation(scenario, earliestStartTime, a.getOperation(), tmpAllocationResourceMap.get(scenario.getResources().get(resourceID))).getTime());

				//Wenn erste Ressource oder bisher früheste
				if(chosenTime == null || 
						(new Date(firstPossibleDate.getTime() +(long)((a.getOperation().getDuration() *MINUTE) *100.0/scenario.getResources().get(resourceID).getAvailability()))).
						before(new Date(chosenTime.getTime() +(long)((a.getOperation().getDuration() *MINUTE) *100.0/chosenResource.getAvailability())))) {

					chosenTime = firstPossibleDate;
					chosenResource = scenario.getResources().get(resourceID);
				} 
			}

			// Operation speichern
			chosenResource.addOperation(a, chosenTime, tmpAllocationResourceMap.get(chosenResource));
			tmpSchedule.getPlannedVariants().get(tmpSchedule.getPlannedVariants().size() -1).addAllocation(a);
		}


		return tmpSchedule;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
