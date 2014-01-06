package de.bachelor.smartSchedules.server.model.algorithm.reactive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Reactiver Order Oriented Algorithmus, welcher die Orders nach EDD sortiert.
 * @author timo
 *
 */
public class ESTReactiveAlgorithmOrderOriented extends AbstractReactiveOrderOriented{

	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario,
			List<ScheduleOrder> orderList) {
		
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>(orderList);

		// Nach EST sortieren:
		Collections.sort(tmpOrderList, new Comparator<ScheduleOrder>() {

			@Override
			public int compare(ScheduleOrder o1, ScheduleOrder o2) {
				if(o1.getEarlistStartTime().before(o2.getEarlistStartTime())) {
					return -1;
				}
				
				if(o1.getEarlistStartTime().equals(o2.getEarlistStartTime())) {
					if(o1.getPriority() > o2.getPriority()) {
						return -1;
					}
					if(o1.getPriority() < o2.getPriority()) {
						return 1;
					}
					return 0;
				}
				
				return 1;
			}			
		});
		
		return tmpOrderList;
	}

	@Override
	protected List<PlannedVariant> getSortedPlannedVariantList(
			Scenario scenario, List<PlannedVariant> plannedVariantList) {
		
		final Scenario tmpScenario = scenario; // Scenario kopie
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>(plannedVariantList);
		
		// Nach EST sortieren:
		Collections.sort(tmpPlannedVariantList, new Comparator<PlannedVariant>() {

			@Override
			public int compare(PlannedVariant o1, PlannedVariant o2) {
				if(o1.getScheduleOrder(tmpScenario).getEarlistStartTime().before(o2.getScheduleOrder(tmpScenario).getEarlistStartTime())) {
					return -1;
				}
				if(o1.getScheduleOrder(tmpScenario).getEarlistStartTime().equals(o2.getScheduleOrder(tmpScenario).getEarlistStartTime())) {
					if(o1.getScheduleOrder(tmpScenario).getPriority() > o2.getScheduleOrder(tmpScenario).getPriority()) {
						return -1;
					}
					if(o1.getScheduleOrder(tmpScenario).getPriority() < o2.getScheduleOrder(tmpScenario).getPriority()) {
						return 1;
					}
					return 0;
				}
				return 1;
			}
			
		});
		
		return tmpPlannedVariantList;
	}

	@Override
	public String getAlgorithmName() {
		return "ESTReactiveAlgorithmOrderOriented";
	}

}
