package de.bachelor.smartSchedules.server.model.algorithm.improvement.changer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
/**
 * Ändert zufällig Allocations.
 * @author timo
 *
 */
public class AllocationSwitcher extends Changer {
	
	private static final String name = "Allocation Switcher";
	
	@Override
	public Schedule findNeighbor(Scenario scenario, Schedule schedule) {

		Schedule result = null;

		/**
		 * Belegungs Map:
		 */
		Map<Resource, List<Allocation>> allocationResourceMap = new HashMap<Resource, List<Allocation>>(schedule.getResources(scenario));

		result = schedule.clone(scenario);
		Allocation tmpAllocation1 = null;
		Allocation tmpAllocation2 = null;

		// Erste Allocation wählen:
		List<Allocation> tmpAllocationList = allocationResourceMap.get(scenario.getResources().get((int)(Math.random() * allocationResourceMap.size())));
		tmpAllocation1 = tmpAllocationList.get((int)(Math.random() * tmpAllocationList.size()));

		// Zweite Allocation wählen:
		List<Integer> tmpResourceAlternatives = tmpAllocation1.getOperation().getResourceAlternatives();
		List<Allocation> tmpAllocationList2 = schedule.getResources(scenario).
				get(scenario.getResources().get(((int)(Math.random() * tmpResourceAlternatives.size()))));
		tmpAllocation2 = tmpAllocationList2.get((int)(Math.random() * tmpAllocationList2.size()));

		// Allocations tauschen:
		Date tmpStartTime = tmpAllocation1.getStart();
		int tmpResourceID = tmpAllocation1.getResourceID();
		tmpAllocation1.setStart(tmpAllocation2.getStart());
		tmpAllocation1.setResourceID(tmpAllocation2.getResourceID());
		tmpAllocation2.setStart(tmpStartTime);
		tmpAllocation2.setResourceID(tmpResourceID);

		return result;

	}

	@Override
	public String getName() {
		return name;
	}
}
