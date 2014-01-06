package de.bachelor.smartSchedules.server.model.algorithm.reactive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Reactiver Order Oriented Algorithmus, welcher die Orders nach SPT sortiert.
 * @author timo
 *
 */
public class SPTReactiveAlgorithmOrderOriented extends AbstractReactiveOrderOriented {

	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario,
			List<ScheduleOrder> orderList) {
		
		final Scenario tmpScenario = scenario;
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>(orderList);
		
		// Nach SPT sortieren:
		Collections.sort(tmpOrderList, new Comparator<ScheduleOrder>() {

			@Override
			public int compare(ScheduleOrder arg0, ScheduleOrder arg1) {
				if(arg0.getShortestPossibleProcessingTime(tmpScenario) < arg1.getShortestPossibleProcessingTime(tmpScenario)) {
					return -1;
				}
				
				if(arg0.getShortestPossibleProcessingTime(tmpScenario) == arg1.getShortestPossibleProcessingTime(tmpScenario)) {
					if(arg0.getPriority() > arg1.getPriority()) {
						return -1;
					}
					if(arg1.getPriority() > arg0.getPriority()) {
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
		
		final Scenario tmpScenario = scenario;
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>(plannedVariantList);
		
		// Nach SPT sortieren:
		Collections.sort(tmpPlannedVariantList, new Comparator<PlannedVariant>() {

			@Override
			public int compare(PlannedVariant o1, PlannedVariant o2) {
				if(o1.getScheduleOrder(tmpScenario).getShortestPossibleProcessingTime(tmpScenario) < 
						o2.getScheduleOrder(tmpScenario).getShortestPossibleProcessingTime(tmpScenario)) {
					return -1;
				}
				if(o1.getScheduleOrder(tmpScenario).getShortestPossibleProcessingTime(tmpScenario) == 
						o2.getScheduleOrder(tmpScenario).getShortestPossibleProcessingTime(tmpScenario)) {
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
		return "SPTReactiveAlgorithmOrderOriented";
	}
	

}
