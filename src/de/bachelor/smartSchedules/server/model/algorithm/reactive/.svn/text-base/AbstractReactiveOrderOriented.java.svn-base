package de.bachelor.smartSchedules.server.model.algorithm.reactive;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;

/**
 * Abstrakte Oberklasse für alle Reactiven Order Oriented Algorithmen.
 * Es müssen noch noch die Comparators implementiert werden.
 * @author timo
 *
 */
public abstract class AbstractReactiveOrderOriented extends ReactiveAlgorithm{
	@Override
	protected Schedule addOrders(Scenario scenario, Schedule initialSchedule,
			List<ScheduleOrder> newOrdersList, Date scheduleChangeDeadline) {
		
		
		newOrdersList = new ArrayList<ScheduleOrder>(newOrdersList);
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		int tmpAllocationCounter = tmpSchedule.getHighestAllocationID(scenario) +1;
		
		// Neue Orders versuchen einzuplanen:

		//Sortieren:
		newOrdersList = this.getSortedOrderList(scenario, newOrdersList);
		
		// Ressourcen/Belegungsmap erzeugen:
		Map<Resource, List<Allocation>> tmpResourceAllocationMap = initialSchedule.getResources(scenario);
		List<Allocation> allocationsInSelectedVariant;
		List<Product> planableProducts = scenario.getPlanableProducts();
		List<Variant> planableVariants = scenario.getPlanableVariants();
		List<PlannedVariant> plannedVariants = new ArrayList<PlannedVariant>();
		Date earliestStartTime = new Date(scheduleChangeDeadline.getTime());
		Variant selectedVariant = null;
		
		// Aufträge einplanen:
		//Für jede Order
		for(ScheduleOrder scheduleOrder : newOrdersList) {
			//Für jedes Produkt
			for(Product product : scheduleOrder.getProducts(scenario)) {
				
				// Kann das Produkt überhaupt verplant werden?
				if(!planableProducts.contains(product)) {
					continue;
				}
				
				//Wähle Variante
				//TODO nicht immer die erste Variante?
				for(Variant planableVariant : planableVariants) {
					if(planableVariant.getProduct().getProductID() == product.getProductID()) {
						selectedVariant = planableVariant;
						plannedVariants.add(new PlannedVariant(planableVariant));
						break;
					}
				}
				 
				//Allocations aus Variante generieren und speichern
				allocationsInSelectedVariant = selectedVariant.generateAllocations(scheduleOrder);
				
				//Für jede Allocation
				for(Allocation allocationInSelectedVariant : allocationsInSelectedVariant) {					
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
					
					//Allocation einplanen
					Resource chosenResource = null;
					Date chosenTime = null;
					Date firstPossibleDate = null;
					
					//Für jede Ressource
					for(Integer resourceID : allocationInSelectedVariant.getOperation().getResourceAlternatives()) {
						
						if(scenario.getResources().get(resourceID).isInBreakDown()) {
							continue;
						}
						
						//Finde Schlupfzeit
						firstPossibleDate = new Date(scenario.getResources().get(resourceID).findFirstPossibleAllocation(scenario, earliestStartTime, allocationInSelectedVariant.getOperation(), tmpResourceAllocationMap.get(scenario.getResources().get(resourceID))).getTime());
						
						//Wenn erste Ressource oder bisher früheste
						if(chosenTime == null || 
								(new Date(firstPossibleDate.getTime() +(long)(allocationInSelectedVariant.getOperation().getDuration() *SECOND *100.0/scenario.getResources().get(resourceID).getAvailability()))).
								before(new Date(chosenTime.getTime() +(long)(allocationInSelectedVariant.getOperation().getDuration() *SECOND *100.0/chosenResource.getAvailability())))) {
							
							chosenTime = firstPossibleDate;
							chosenResource = scenario.getResources().get(resourceID);
						} 
					}
					
					// Operation speichern
					chosenResource.addOperation(allocationInSelectedVariant, chosenTime, tmpResourceAllocationMap.get(chosenResource));
					allocationInSelectedVariant.setAllocationID(tmpAllocationCounter++); 
					plannedVariants.get(plannedVariants.size() -1).addAllocation(allocationInSelectedVariant);
				}
			}	
		}

		// PlannedVariants hinzufügen:
		tmpSchedule.addPlannedVariants(plannedVariants);
		
		return tmpSchedule;
	}

	@Override
	protected Schedule removeOrders(Scenario scenario,
			Schedule initialSchedule, List<ScheduleOrder> removeOrdersList, Date scheduleChangeDeadline) {
		
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		for(ScheduleOrder so : removeOrdersList) {
			for(int i = 0; i < tmpSchedule.getPlannedVariants().size(); i++) {
				if(tmpSchedule.getPlannedVariants().get(i).getScheduleOrderID() == so.getOrderID()) {
					tmpSchedule.getPlannedVariants().remove(i);
					i = 0;
				}
			}
		}
		
		return tmpSchedule;
	}

	
	
	@Override
	public abstract String getAlgorithmName();

	@Override
	protected Schedule changeOrdersEndTimes(Scenario scenario,
			Schedule initialSchedule, List<ScheduleOrder> orderList, Date scheduleChangeDeadline) {

		return initialSchedule.clone(scenario);
	}

	@Override
	protected Schedule changeOrderPriorities(Scenario scenario,
			Schedule initialSchedule, List<ScheduleOrder> orderList, Date scheduleChangeDeadline) {

		return initialSchedule.clone(scenario);
	}



	@Override
	protected Schedule handleMaintenancePeriods(Scenario scenario,
			Schedule initialSchedule, List<ResourceRestriction> maintenanceList, Date scheduleChangeDeadline) {
		
		return this.reschedulePlannedVariants(scenario, initialSchedule, this.getInconsistentPlannedVariants(scenario, initialSchedule), scheduleChangeDeadline);
	}

	@Override
	protected Schedule handleRemovedProducts(Scenario scenario,
			Schedule initialSchedule, List<Product> productList, Date scheduleChangeDeadline) {
		
		// Kopie von PlannedVariants:
		List<PlannedVariant> tmpPlannedVariants = new ArrayList<PlannedVariant>(initialSchedule.getPlannedVariants());
		
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		// Durchlaufen und Produkte löschen, die es nicht mehr gibt.
		for(PlannedVariant pv : tmpPlannedVariants) {
			if(productList.contains(pv.getProduct())) {
				tmpSchedule.getPlannedVariants().remove(pv);
			}
		}
		
		return tmpSchedule;
	}

	@Override
	protected Schedule handleRemovedVariants(Scenario scenario,
			Schedule initialSchedule, List<Variant> variantList, Date scheduleChangeDeadline) {

		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		List<PlannedVariant> tmpPlannedVariants = new ArrayList<PlannedVariant>(tmpSchedule.getPlannedVariants());
		Map<ScheduleOrder, List<Product>> tmpOrderMap = new HashMap<ScheduleOrder, List<Product>>();
		

		
		// Gelöschte, nicht mehr vorhandene Varianten suchen, speichern und löschen:
		for(PlannedVariant pv : tmpPlannedVariants) {
			if(variantList.contains(pv.getPlannedVariant())) {
				// PV Löschen:
				tmpSchedule.getPlannedVariants().remove(pv);
				
				// Merken, dass es neu verplant werden muss:
				if(tmpOrderMap.get(pv.getScheduleOrder(scenario)) == null) {
					tmpOrderMap.put(pv.getScheduleOrder(scenario), new ArrayList<Product>());
				}
				tmpOrderMap.get(pv.getScheduleOrder(scenario)).add(pv.getProduct());
			}
		}
		
		// Aus der Map eine Liste zaubern
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
		for(ScheduleOrder so : tmpOrderMap.keySet()) {

			// Produktliste erstellen:
			List<Product> tmpProductList = new ArrayList<Product>();
			for(Product p : tmpOrderMap.get(so)) {
				tmpProductList.add(p);
			}
			
			// Order aus der Produktliste erstellen:
			tmpOrderList.add(new ScheduleOrder(so.getOrderID(), scenario.getScenarioID(), tmpProductList, so.getName(), so.getEarlistStartTime(), so.getEarlistDueTime(), so.getPriority()));
		}
		
		// Über addOrders einplanen:
		return this.addOrders(scenario, tmpSchedule, tmpOrderList, scheduleChangeDeadline);
	}

	@Override
	protected Schedule changeResourceAvailability(Scenario scenario,
			Schedule initialSchedule, Map<Integer, Integer> changedResourceMap, Date scheduleChangeDeadline) {

		return this.reschedulePlannedVariants(scenario, initialSchedule, this.getInconsistentPlannedVariants(scenario, initialSchedule), scheduleChangeDeadline);
	}

	@Override
	protected Schedule removeOrdersProducts(Scenario scenario,
			Schedule initialSchedule, Map<ScheduleOrder, List<Product>> removeMap, Date scheduleChangeDeadline) {
		
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		for(ScheduleOrder so : removeMap.keySet()) {
			// Produkt suchen und löschen:
			for(Product p : removeMap.get(so)) {
				for(int i = 0; i < tmpSchedule.getPlannedVariants().size(); i++) {
					if(tmpSchedule.getPlannedVariants().get(i).getScheduleOrderID() == so.getOrderID()
					&& tmpSchedule.getPlannedVariants().get(i).getProduct().equals(p)) {
						tmpSchedule.getPlannedVariants().remove(i);
						break;
					}
				}
			}
		}
		
		return tmpSchedule;
	}

	@Override
	protected Schedule handleMachineBreakDowns(Scenario scenario,
			Schedule initialSchedule, List<Resource> changedResources, Date scheduleChangeDeadline) {

		return this.reschedulePlannedVariants(scenario, initialSchedule, this.getInconsistentPlannedVariants(scenario, initialSchedule), scheduleChangeDeadline);
	}

	@Override
	protected Schedule handleMachineRepaired(Scenario scenario,
			Schedule initialSchedule, List<Resource> changedResources, Date scheduleChangeDeadline) {
		
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
		
		// Produkte, die vorher nicht möglich waren suchen und einplanen:
		Map<ScheduleOrder, List<Product>> tmpOrderProductMap = initialSchedule.getNotPlannedOrderProducts(scenario);
		
		// Aus der Map eine orderList erstellen: 
		for(ScheduleOrder so : tmpOrderProductMap.keySet()) {
			
			// "Neue" Order mit Produktlist erstellen:
			tmpOrderList.add(new ScheduleOrder(so.getOrderID(), scenario.getScenarioID(), tmpOrderProductMap.get(so), so.getName(), so.getEarlistStartTime(), so.getEarlistDueTime(), so.getPriority()));
			
		}
		
		// Einplanen, als wären es neue orders:
		return this.addOrders(scenario, initialSchedule, tmpOrderList, scheduleChangeDeadline);
	}
	
	@Override
	protected Schedule changeOperationResources(Scenario scenario,
			Schedule initialSchedule, int operationID,
			List<Integer> newResourcesList, Date scheduleChangeDeadline) {
		
		// 1. Step: Inkonsistente PlannedVariants neu einplanen:
		Schedule tmpSchedule = this.reschedulePlannedVariants(scenario, initialSchedule, this.getInconsistentPlannedVariants(scenario, initialSchedule), scheduleChangeDeadline);
		
		
		// 2. Step: Schauen, ob nicht mögliche Produkte jetzt möglich sind:
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
		
		// Produkte, die vorher nicht möglich waren suchen und einplanen:
		Map<ScheduleOrder, List<Product>> tmpOrderProductMap = tmpSchedule.getNotPlannedOrderProducts(scenario);
		
		// Aus der Map eine orderList erstellen: 
		for(ScheduleOrder so : tmpOrderProductMap.keySet()) {
			
			// "Neue" Order mit Produktlist erstellen:
			tmpOrderList.add(new ScheduleOrder(so.getOrderID(), scenario.getScenarioID(), tmpOrderProductMap.get(so), so.getName(), so.getEarlistStartTime(), so.getEarlistDueTime(), so.getPriority()));
			
		}
		
		// Einplanen, als wären es neue orders:
		return this.addOrders(scenario, tmpSchedule, tmpOrderList, scheduleChangeDeadline);
	}
	
	/**
	 * Plant eine inkonsistente PlannedVariant wieder konsistent einzuplanen.
	 * @param scenario
	 * @param initialSchedule
	 * @param plannedVariant
	 * @return
	 */
	private Schedule reschedulePlannedVariant(Scenario scenario, Schedule initialSchedule, PlannedVariant plannedVariant, Date scheduleChangeDeadline) {
		
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		int tmpAllocationCounter = tmpSchedule.getHighestAllocationID(scenario) +1;
		
		// Solange die PlannedVariant nicht Konsistent ist -> Belegungen ändern:
		Map<Resource, List<Allocation>> tmpResources = tmpSchedule.getResources(scenario);
		
		while(!plannedVariant.isConsistent(scenario, tmpSchedule)) {
			
			// Erste Fehlerhafte Allocation suchen und umbelegen:
			for(Allocation a : plannedVariant.getAllocationList()) {
				
				/*
				 * Nicht Konsistente Allocation suchen.
				 * Hier werden bereits fertige Allocations rausgefiltert!
				 */
				if(!a.isConsistent(scenario, tmpSchedule)) {
					// Falls die Operation gar nicht planbar ist -> Mit anderer Variante einplanen:
					if(!a.getOperation().isPlanable(scenario)) {
						return this.tryRescheduleProduct(scenario, tmpSchedule, plannedVariant, scheduleChangeDeadline);
					}
					
					//Allocation neu einplanen
					tmpResources.get(scenario.getResources().get(a.getResourceID())).remove(a); // Alte Allocation erstmal löschen
					Resource chosenResource = null;
					Date chosenTime = null;
					Date firstPossibleDate = null;
					
					// EarlistStartTime suchen:
					Date earliestStartTime = new Date(scheduleChangeDeadline.getTime());
					// mit Vorgänger
					if(!a.getAllocationPredecessors().isEmpty()) {
						//Für jeden Vorgänger
						for(Allocation predecessorOfAllocation : a.getAllocationPredecessors()) {
							if(predecessorOfAllocation.getFinish(scenario).after(earliestStartTime)) {
								earliestStartTime = new Date(predecessorOfAllocation.getFinish(scenario).getTime() +(scenario.getSecondsBetweenAllocations()*SECOND));
							}
						}
					} 
					
					//Für jede Ressource
					for(Integer resourceID : a.getOperation().getResourceAlternatives()) {
						
						if(scenario.getResources().get(resourceID).isInBreakDown()) {
							continue;
						}
						
						//Finde Schlupfzeit
						firstPossibleDate = new Date(scenario.getResources().get(resourceID).findFirstPossibleAllocation(scenario, earliestStartTime, a.getOperation(), tmpResources.get(scenario.getResources().get(resourceID))).getTime());
						
						//Wenn erste Ressource oder bisher früheste
						if(chosenTime == null || 
								(new Date(firstPossibleDate.getTime() +(long)(a.getOperation().getDuration() *SECOND *100.0/scenario.getResources().get(resourceID).getAvailability()))).
								before(new Date(chosenTime.getTime() +(long)(a.getOperation().getDuration() *SECOND *100.0/chosenResource.getAvailability())))) {
							
							chosenTime = firstPossibleDate;
							chosenResource = scenario.getResources().get(resourceID);
						} 
					}
					
					// Operation neu speichern
					chosenResource.addOperation(a, chosenTime, tmpResources.get(chosenResource));
					a.setAllocationID(tmpAllocationCounter++);				
				}
			}
		}
			
		return tmpSchedule;
	}
	
	private Schedule reschedulePlannedVariants(Scenario scenario, Schedule initialSchedule, List<PlannedVariant> plannedVariantList, Date scheduleChangeDeadline) {
		
		Schedule tmpSchedule = null;
		
		// PlannedVariantList sortieren:
		plannedVariantList = this.getSortedPlannedVariantList(scenario, plannedVariantList);
		
		// PlannedVariants durchlaufen
		for(PlannedVariant pv : plannedVariantList) {
			tmpSchedule = this.reschedulePlannedVariant(scenario, initialSchedule, pv, scheduleChangeDeadline);
		}
		
		return tmpSchedule;
	}
	
	/**
	 * Liefert alle PlannedVariants, die nicht mehr konsistent sind.
	 * @param scenario
	 * @param initialSchedule
	 * @return
	 */
	private List<PlannedVariant> getInconsistentPlannedVariants(Scenario scenario, Schedule initialSchedule) {
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>();
		
		// PlannedVariants durchlaufen, prüfen und ggf. speichern:
		for(PlannedVariant pv : initialSchedule.getPlannedVariants()) {
			if(!pv.isConsistent(scenario, initialSchedule)) {
				tmpPlannedVariantList.add(pv);
			}
		}
		
		return tmpPlannedVariantList;
	}
	
	/**
	 * Versucht das Produkt mit einer anderen Variante einzuplanen.
	 * @param product
	 */
	private Schedule tryRescheduleProduct(Scenario scenario, Schedule initialSchedule, PlannedVariant plannedVariant, Date scheduleChangeDeadline) {
		
		Schedule tmpSchedule = initialSchedule.clone(scenario);
		
		// Alte PlannedVariant löschen:
		tmpSchedule.getPlannedVariants().remove(plannedVariant);
		
		// Wie neue Order verarbeiten:
		List<Product> tmpProductList = new ArrayList<Product>();
		tmpProductList.add(plannedVariant.getProduct());
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
		tmpOrderList.add(new ScheduleOrder(plannedVariant.getScheduleOrder(scenario).getOrderID(), scenario.getScenarioID(), tmpProductList, plannedVariant.getScheduleOrder(scenario).getName(), plannedVariant.getScheduleOrder(scenario).getEarlistStartTime(), plannedVariant.getScheduleOrder(scenario).getEarlistDueTime(), plannedVariant.getScheduleOrder(scenario).getPriority()));

		return this.addOrders(scenario, tmpSchedule, tmpOrderList, scheduleChangeDeadline);
	}
	
	/**
	 * Liefert eine nach dem jeweils gewünschten Kriterien sortierte OrderList.
	 * @param scenario
	 * @param orderList
	 * @return
	 */
	protected abstract List<ScheduleOrder> getSortedOrderList(Scenario scenario, List<ScheduleOrder> orderList);

	/**
	 * Liefert eine nach dem jeweils gewünschten Kriterien sortierte PlannedVariantList.
	 * @param scenario
	 * @param plannedVariantList
	 * @return
	 */
	protected abstract List<PlannedVariant> getSortedPlannedVariantList(Scenario scenario, List<PlannedVariant> plannedVariantList);
}
