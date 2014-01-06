package de.bachelor.smartSchedules.server.model.algorithm.predictive;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Earlist Start Time first
 * Order Orientiert.
 * @author timo
 *
 */
public class ESTAlgorithmOrderOriented extends AbstractOrderOriented{
		
	@Override
	public String getAlgorithmName() {
		return "ESTAlgorithmOrderOriented";
	}



	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario, 
			List<ScheduleOrder> orderList) {
		Collections.sort(orderList, new Comparator<ScheduleOrder>() {

			@Override
			public int compare(ScheduleOrder arg0, ScheduleOrder arg1) {
				if(arg0.getEarlistStartTime().before(arg1.getEarlistStartTime())) {
					return -1;
				}
				
				if(arg0.getEarlistStartTime().equals(arg1.getEarlistStartTime())) {
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
