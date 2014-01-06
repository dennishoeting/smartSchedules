package de.bachelor.smartSchedules.server.model.algorithm.predictive;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
/**
 * Kann einen Schedule auftragsorientiert
 * nach E(arlist) D(ue) D(ate) erzeugen.
 * @author timo
 *
 */
public class EDDAlgorithmOrderOriented extends AbstractOrderOriented{
	
	/**
	 * Default
	 */
	public EDDAlgorithmOrderOriented() {

	}

	@Override
	public String getAlgorithmName() {
		return "EDDAlgorithmOrderOriented";
	}



	@Override
	protected List<ScheduleOrder> getSortedOrderList(Scenario scenario, List<ScheduleOrder> orderList) {
		Collections.sort(orderList, 
				new Comparator<ScheduleOrder>() {
					@Override
					public int compare(ScheduleOrder arg0, ScheduleOrder arg1) {
						if(arg0.getEarlistDueTime().before(arg1.getEarlistDueTime())) {
							return -1;
						}
						
						if(arg0.getEarlistDueTime().equals(arg1.getEarlistDueTime())) {
							if(arg0.getPriority() > arg1.getPriority()) {
								return -1;
							}
							if(arg1.getPriority() > arg1.getPriority()) {
								return 1;
							}
							return 0;
						}
						
						return 1;
					}
				}
		);
		return orderList;
	}
}
