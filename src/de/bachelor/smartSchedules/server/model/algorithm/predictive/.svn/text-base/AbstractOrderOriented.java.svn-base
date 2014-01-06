package de.bachelor.smartSchedules.server.model.algorithm.predictive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * Abstrakte Oberklasse für alle OrderOriented Algorithmen
 * @author timo
 *
 */
public abstract class AbstractOrderOriented extends PredictiveAlgorithm {

	@Override
	protected Schedule produceScheduleInternal(Scenario scenario, Date scheduleChangeDeadline, ScheduleEvent scheduleEvent) {
		
		int allocationCounter = 0;
		
		List<ScheduleOrder> orderList = this.getSortedOrderList(scenario, (new ArrayList<ScheduleOrder>(scenario.getOrdersClone())));
		
		// Ressourcen/Belegungsmap erzeugen:
		Map<Resource, List<Allocation>> tmpResourceAllocationMap = super.produceResourceMap(scenario.getResources());
		List<Allocation> allocationsInSelectedVariant;
		List<Product> planableProducts = scenario.getPlanableProducts();
		List<Variant> planableVariants = scenario.getPlanableVariants();
		List<PlannedVariant> plannedVariants = new ArrayList<PlannedVariant>();
		Date earliestStartTime = scheduleChangeDeadline;

		// Alle Produkte, die die ScheduleChangeDeadline betreffen planen:
		if(scenario.getChosenSchedule() != null) {
			for(PlannedVariant pv : scenario.getPossiblePlannedVariantsInChangeDeadline(scheduleChangeDeadline)) {
				PlannedVariant tmpPlannedVariant = pv.clone(scenario);
				plannedVariants.add(tmpPlannedVariant);
				// Allocations in die Map eintragen:
				for(Allocation a : tmpPlannedVariant.getAllocationList()) {
					scenario.getResources().get(a.getResourceID()).addOperation(a, a.getStart(), tmpResourceAllocationMap.get(scenario.getResources().get(a.getResourceID())));
				}
			}
			
			// Alle schon erfüllten Produkte löschen:
			for(PlannedVariant pv : plannedVariants) {
				for(ScheduleOrder so : orderList) {
					if(pv.getScheduleOrder(scenario).equals(so)) {
						so.removeProduct(pv.getProduct());
					}
				}
			}
		}
		
		/*
		 * Alle weiteren Aufträge/Produkte planen:
		 */
		for(ScheduleOrder scheduleOrder : orderList) {

			//Für jedes Produkt
			for(Product product : scheduleOrder.getProducts(scenario)) {
				Variant selectedVariant = null;
								
				// Kann das Produkt überhaupt verplant werden?
				if(selectedVariant == null && !planableProducts.contains(product)) {
					continue;
				}
				
				//Falls noch keine Variante gewählt wurde -> wählen:
				//TODO nicht immer die erste Variante?
				if(selectedVariant == null) {
					for(Variant planableVariant : planableVariants) {
						if(planableVariant.getProduct().getProductID() == product.getProductID()) {
							selectedVariant = planableVariant;
							break;
						}
					}
				}
				
				// Variante zu PlannedVariants adden:
				plannedVariants.add(new PlannedVariant(selectedVariant));

				//Allocations aus Variante generieren und speichern
				allocationsInSelectedVariant = selectedVariant.generateAllocations(scheduleOrder);
				
				//Für jede Allocation
				for(Allocation allocationInSelectedVariant : allocationsInSelectedVariant) {
					
					//Allocation einplanen
					Resource chosenResource = null;
					Date chosenTime = null;
					Date firstPossibleDate = null;
					earliestStartTime = new Date(scheduleChangeDeadline.getTime());

					// mit Vorgänger
					if(!allocationInSelectedVariant.getAllocationPredecessors().isEmpty()) {
						//Für jeden Vorgänger
						for(Allocation predecessorOfAllocation : allocationInSelectedVariant.getAllocationPredecessors()) {
							if(predecessorOfAllocation.getFinish(scenario).after(earliestStartTime)) {
								earliestStartTime = new Date(predecessorOfAllocation.getFinish(scenario).getTime() +(scenario.getSecondsBetweenAllocations()*SECOND));
							}
						}
					} 

					//Für jede Ressource
					for(Integer resourceID : allocationInSelectedVariant.getOperation().getResourceAlternatives()) {

						if(scenario.getResources().get(resourceID).isInBreakDown()) {
							continue;
						}

						//Finde Schlupfzeit
						firstPossibleDate = new Date(scenario.getResources().get(resourceID).findFirstPossibleAllocation(scenario, earliestStartTime, allocationInSelectedVariant.getOperation(), tmpResourceAllocationMap.get(scenario.getResources().get(resourceID))).getTime());

						//Wenn erste Ressource oder bisher früheste
						if(chosenTime == null || 
								(new Date(firstPossibleDate.getTime() +(long)((allocationInSelectedVariant.getOperation().getDuration() *SECOND) *100.0/scenario.getResources().get(resourceID).getAvailability()))).
								before(new Date(chosenTime.getTime() +(long)((allocationInSelectedVariant.getOperation().getDuration() *SECOND) *100.0/chosenResource.getAvailability())))) {

							chosenTime = firstPossibleDate;
							chosenResource = scenario.getResources().get(resourceID);
						} 
					}
					
					// Operation speichern
					chosenResource.addOperation(allocationInSelectedVariant, chosenTime, tmpResourceAllocationMap.get(chosenResource));
					allocationInSelectedVariant.setAllocationID(allocationCounter++);
					plannedVariants.get(plannedVariants.size() -1).addAllocation(allocationInSelectedVariant);
				}
			}	
		}
		
		return new Schedule(scenario.getScenarioID(), plannedVariants, scheduleEvent);
	}
	
	/**
	 * Gibt eine Liste mit bereits erledigten Allocations für eine Order aus.
	 * @param initialSchedule
	 * @return
	 */
	private List<Allocation> getDoneAllocationsListForOrder(Scenario scenario, Schedule initialSchedule, ScheduleOrder scheduleOrder) {
		List<Allocation> tmpDoneAllocationList = new ArrayList<Allocation>();
		
		if(initialSchedule != null) {
			
			// PlannedVariants durchlaufen und fertige Allocations sammeln:
			for(PlannedVariant pv : initialSchedule.getPlannedVariantsForOrder(scenario, scheduleOrder)) {
				for(Allocation a : pv.getAllocationList()) {
					if(a.isDone()) {
						tmpDoneAllocationList.add(a);
					}
				}
			}
		}
		
		return tmpDoneAllocationList;
	}
	
	/**
	 * Liefert eine nach dem jeweils gewünschten Kriterien sortierte OrderList.
	 * @param scenario
	 * @param orderList
	 * @return
	 */
	protected abstract List<ScheduleOrder> getSortedOrderList(Scenario scenario, List<ScheduleOrder> orderList);
}