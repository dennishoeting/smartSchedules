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
public class EDDReactiveAlgorithmOrderOriented extends AbstractReactiveOrderOriented{

	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario,
			List<ScheduleOrder> orderList) {
		
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>(orderList);
		// Nach EDD sortieren:
		Collections.sort(tmpOrderList, new Comparator<ScheduleOrder>() {
			
			@Override
			public int compare(ScheduleOrder o1, ScheduleOrder o2) {
				if(o1.getEarlistDueTime().before(o2.getEarlistDueTime())) {
					return -1;
				}
				if(o1.getEarlistDueTime().equals(o2.getEarlistDueTime())) {
					if(o1.getPriority() > o2.getPriority()) {
						return -1;
					} 
					if(o2.getPriority() < o2.getPriority()) {
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
		
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>(plannedVariantList);
		
		final Scenario tmpScenario = scenario; // Scenario Kopie
		// Nach EDD sortieren:
		Collections.sort(tmpPlannedVariantList, new Comparator<PlannedVariant>() {

			@Override
			public int compare(PlannedVariant o1, PlannedVariant o2) {
				if(o1.getScheduleOrder(tmpScenario).getEarlistDueTime().before(o2.getScheduleOrder(tmpScenario).getEarlistDueTime())) {
					return -1;
				}
				if(o1.getScheduleOrder(tmpScenario).getEarlistDueTime().equals(o2.getScheduleOrder(tmpScenario).getEarlistDueTime())) {
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
		return "EDDReactiveAlgorithmOrderOriented";
	}
}
