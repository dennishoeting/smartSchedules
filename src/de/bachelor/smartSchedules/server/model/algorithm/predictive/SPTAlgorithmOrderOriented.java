package de.bachelor.smartSchedules.server.model.algorithm.predictive;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Shortest Processing Time first
 * Order Ordiented
 * @author timo
 *
 */
public class SPTAlgorithmOrderOriented extends AbstractOrderOriented {
	
	@Override
	public String getAlgorithmName() {
		return "SPTAlgorithmOrderOriented";
	}

	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario,
			List<ScheduleOrder> orderList) {
		final Scenario tmpScenario  = scenario;
		Collections.sort(orderList, new Comparator<ScheduleOrder>() {

			@Override
			public int compare(ScheduleOrder arg0, ScheduleOrder arg1) {
				if(arg0.getShortestPossibleProcessingTime(tmpScenario) < arg1.getShortestPossibleProcessingTime(tmpScenario)) {
					return -1;
				}
				
				if(arg0.getShortestPossibleProcessingTime(tmpScenario) == arg1.getShortestPossibleProcessingTime(tmpScenario)) {
					if(arg0.getPriority() > arg1.getPriority()) {
						return -1;
					}
					if(arg1.getPriority() < arg0.getPriority()) {
						return 1;
					}
					return 0;
				}
				
				return 1;
			}
		});
		
		return orderList;
	}
}
