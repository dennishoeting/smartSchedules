package de.bachelor.smartSchedules.server.model.algorithm.reactive;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOperationResourcesEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderDueTimeEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeResourceAvailabilityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MaintenancePeriodsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;

/**
 * Oberklasse für alle Reaktiven Planungsalgorithmen
 * @author timo
 *
 */
public abstract class ReactiveAlgorithm {
	
	//public final static int MINUTE = 60000;
	public final static int SECOND = 1000;
	
	/**
	 * Erzeugt eine leere Map für
	 * Ressourcen/Belegungen.
	 * @param resources
	 * @return
	 */
	protected Map<Resource, List<Allocation>> produceResourceMap(Map<Integer, Resource> resources) {
		
		Map<Resource, List<Allocation>> resourceMap = new HashMap<Resource, List<Allocation>>();
		// Resourcen Einfügen:
		for(Integer i : resources.keySet()) {
			resourceMap.put(resources.get(i), new ArrayList<Allocation>());
		}
		
		return resourceMap;
	}
	
	/**
	 * Erzeugt einen neuen Plan.
	 * @param scenario
	 * @param initialSchedule
	 * @param scheduleEvent
	 * @return
	 */
	public Schedule produceSchedule(Scenario scenario, ScheduleEvent scheduleEvent, Date scheduleChangeDeadline) {
		List<ScheduleEvent> eventList = new ArrayList<ScheduleEvent>();
		eventList.add(scheduleEvent);
		return this.produceSchedule(scenario, eventList, scheduleChangeDeadline);
	}
	
	/**
	 * Erzeugt einen neuen Plan.
	 * @param scenario
	 * @param initialSchedule
	 * @param eventList
	 * @return
	 */
	public Schedule produceSchedule(Scenario scenario, List<ScheduleEvent> eventList, Date scheduleChangeDeadline) {
		
		Schedule tmpInitialSchedule = scenario.getChosenSchedule().clone(scenario);
		Date startTime = new Date();
		// Eventliste kopieren:
		eventList = new ArrayList<ScheduleEvent>(eventList);
		
		// Solange Konsistenzverletzungen da sind, behandeln
		while(!eventList.isEmpty()) {
			if(eventList.get(0) instanceof NewOrdersEvent) {
				tmpInitialSchedule = addOrders(scenario, tmpInitialSchedule, ((NewOrdersEvent)eventList.get(0)).getNewOrdersList(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof RemoveOrdersEvent) {
				tmpInitialSchedule = removeOrders(scenario, tmpInitialSchedule, ((RemoveOrdersEvent)eventList.get(0)).getRemoveOrdersList(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof ChangeOrderDueTimeEvent) {
				tmpInitialSchedule = changeOrdersEndTimes(scenario, tmpInitialSchedule, ((ChangeOrderDueTimeEvent)eventList.get(0)).getChangedOrders(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof RemoveOrdersProductsEvent) {
				tmpInitialSchedule = removeOrdersProducts(scenario, tmpInitialSchedule, ((RemoveOrdersProductsEvent)eventList.get(0)).getRemoveMap(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof MachineBreakDownEvent) {
				tmpInitialSchedule = handleMachineBreakDowns(scenario, tmpInitialSchedule, ((MachineBreakDownEvent)eventList.get(0)).getResourceBreakDowns(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof MachineRepairedEvent) {
				tmpInitialSchedule = handleMachineRepaired(scenario, tmpInitialSchedule, ((MachineRepairedEvent)eventList.get(0)).getResourceRepairs(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof MaintenancePeriodsEvent) {
				tmpInitialSchedule = handleMaintenancePeriods(scenario, tmpInitialSchedule, ((MaintenancePeriodsEvent)eventList.get(0)).getMaintenancePeriods(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof RemoveProductsEvent) {
				tmpInitialSchedule = handleRemovedProducts(scenario, tmpInitialSchedule, ((RemoveProductsEvent)eventList.get(0)).getRemovedProducts(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof RemoveVariantsEvent) {
				tmpInitialSchedule = handleRemovedVariants(scenario, tmpInitialSchedule, ((RemoveVariantsEvent)eventList.get(0)).getRemovedVariants(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof ChangeResourceAvailabilityEvent) {
				tmpInitialSchedule = changeResourceAvailability(scenario, tmpInitialSchedule, ((ChangeResourceAvailabilityEvent)eventList.get(0)).getChangedResourceAvailabilities(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			if(eventList.get(0) instanceof ChangeOperationResourcesEvent) {
				tmpInitialSchedule = changeOperationResources(scenario, tmpInitialSchedule, ((ChangeOperationResourcesEvent)eventList.get(0)).getOperationID(), ((ChangeOperationResourcesEvent)eventList.get(0)).getNewResourceList(), scheduleChangeDeadline);
				tmpInitialSchedule.setScheduleEvent(eventList.get(0));
				eventList.remove(0);
				continue;
			}
			
			// Falls es keines der Events ist dann löschen:
			eventList.remove(0);
		}
		
		tmpInitialSchedule.setAlgorithmInformations(new AlgorithmInformation(this.getAlgorithmName(), startTime, new Date(), scenario.getChosenSchedule()));
		
		return tmpInitialSchedule;
	}
	
	/**
	 * Reaktion auf ein NewOrdersEvent
	 * @param scenario
	 * @param initialSchedule
	 * @param newOrdersList
	 * @return
	 */
	protected abstract Schedule addOrders(Scenario scenario, Schedule initialSchedule, List<ScheduleOrder> newOrdersList, Date scheduleChangeDeadline);
	
	/**
	 * Reaktion auf ein RemoveOrdersEvent
	 * @param scenario
	 * @param initialSchedule
	 * @param removeOrdersList
	 * @return
	 */
	protected abstract Schedule removeOrders(Scenario scenario, Schedule initialSchedule, List<ScheduleOrder> removeOrdersList, Date scheduleChangeDeadline);
	
	protected abstract Schedule changeOrdersEndTimes(Scenario scenario, Schedule initialSchedule, List<ScheduleOrder> orderList, Date scheduleChangeDeadline);
	
	protected abstract Schedule removeOrdersProducts(Scenario scenario, Schedule initialSchedule, Map<ScheduleOrder, List<Product>> removeMap, Date scheduleChangeDeadline);
	
	protected abstract Schedule changeOrderPriorities(Scenario scenario, Schedule initialSchedule, List<ScheduleOrder> orderList, Date scheduleChangeDeadline);
	
	protected abstract Schedule handleMachineBreakDowns(Scenario scenario, Schedule initialSchedule, List<Resource> changedResources, Date scheduleChangeDeadline);
	
	protected abstract Schedule handleMachineRepaired(Scenario scenario, Schedule initialSchedule, List<Resource> changedResources, Date scheduleChangeDeadline);
	
	protected abstract Schedule handleMaintenancePeriods(Scenario scenario, Schedule initialSchedule, List<ResourceRestriction> maintenanceList, Date scheduleChangeDeadline);
	
	protected abstract Schedule handleRemovedProducts(Scenario scenario, Schedule initialSchedule, List<Product> productList, Date scheduleChangeDeadline);
	
	protected abstract Schedule handleRemovedVariants(Scenario scenario, Schedule initialSchedule, List<Variant> variantList, Date scheduleChangeDeadline);
	
	protected abstract Schedule changeResourceAvailability(Scenario scenario, Schedule initialSchedule, Map<Integer, Integer> changedResourceMap, Date scheduleChangeDeadline);
	
	protected abstract Schedule changeOperationResources(Scenario scenario, Schedule initialSchedule, int operationID, List<Integer> newResourcesList, Date scheduleChangeDeadline);
	
	/**
	 * Gibt den Algorithm Namen aus.
	 * @return
	 */
	public abstract String getAlgorithmName();
}
