package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;

/**
 * Ein Plan
 * @author timo
 *
 */
public class Schedule implements Serializable {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -7197648032072559414L;
	private List<PlannedVariant> plannedVariants;
	private AlgorithmInformation algorithmInformations;
	private int scheduleID, scenarioID;
	private static final int MINUTE = 60000;
	private ScheduleEvent scheduleEvent;
	private Map<Integer, Integer> keyFigureValueMap;
	
	public Schedule() {
		this.scenarioID = -1;
		this.keyFigureValueMap = new HashMap<Integer, Integer>();
	}
	
	public Schedule(int scenarioID, List<PlannedVariant> plannedVariants, ScheduleEvent event) {
		this();
		this.plannedVariants = plannedVariants;
		this.scenarioID = scenarioID;
		this.scheduleEvent = event;
	}
	
	public Schedule(int scenarioID, int scheduleID, List<PlannedVariant> plannedVariants, ScheduleEvent event) {
		this(scenarioID, plannedVariants, event);
		this.scheduleID = scheduleID;
	}

	public List<ScheduleOrder> getPlannedOrders(Scenario scenario) {
		Set<ScheduleOrder> tmpOrders = new HashSet<ScheduleOrder>();
		
		// Set füllen:
		for(PlannedVariant pv : this.plannedVariants) {
			tmpOrders.add(pv.getScheduleOrder(scenario));
		}
		
		return new ArrayList<ScheduleOrder>(tmpOrders);
	}
	
	/**
	 * Gibt die OrderIDs von allen geplanten Orders aus.
	 * @return
	 */
	public List<Integer> getPlannedOrders() {
		Set<Integer> tmpOrders = new HashSet<Integer>();
		
		// Set füllen:
		for(PlannedVariant pv : this.plannedVariants) {
			tmpOrders.add(pv.getScheduleOrderID());
		}
		
		return new ArrayList<Integer>(tmpOrders);
	}


	public ScheduleOrder getOrder(Scenario scenario, int id) {
		for(ScheduleOrder os : getPlannedOrders(scenario)) {
			if(os.getOrderID() == id) {
				return os;
			}
		}
		return null;
	}

	/**
	 * Eine Map mit den Belegungen zu jeder Ressource.
	 * @return
	 */
	public Map<Resource, List<Allocation>> getResources(Scenario scenario) {
		// Leere Map für alle Ressourcen erstellen:
		Map<Resource, List<Allocation>> tmpMap = new HashMap<Resource, List<Allocation>>();
		for(Integer key : scenario.getResources().keySet()) {
			tmpMap.put(scenario.getResources().get(key), new ArrayList<Allocation>());
		}
		
		// Allocations eintragen:
		for(PlannedVariant pv : this.plannedVariants) {
			for(Allocation a : pv.getAllocationList()) {
				tmpMap.get(scenario.getResources().get(a.getResourceID())).add(a);
			}
		}
		
		// Allocations nach Startzeit sortieren:
		for(Resource r : tmpMap.keySet()) {
			Collections.sort(tmpMap.get(r), new Comparator<Allocation>() {

				@Override
				public int compare(Allocation o1, Allocation o2) {
					if(o1.getStart().before(o2.getStart())) {
						return -1;
					}
					if(o1.getStart().after(o2.getStart())) {
						return 1;
					}
					
					return 0;
				}
				
			});
		}
		
		return tmpMap;
	}
	
	public List<List<Allocation>> getAllocations(Scenario scenario) {
		ArrayList<List<Allocation>> allocations = new ArrayList<List<Allocation>>();
		for(Resource r : this.getResources(scenario).keySet()) {
			allocations.add(this.getResources(scenario).get(r));
		}
		return allocations;
	}
	
	public List<PlannedVariant> getPlannedVariants() {
		return plannedVariants;
	}
	
	public void setPlannedVariants(List<PlannedVariant> plannedVariants) {
		this.plannedVariants = plannedVariants;
	}
	
	public void addPlannedVariant(PlannedVariant plannedVariant) {
		this.plannedVariants.add(plannedVariant);
	}
	
	public void addPlannedVariants(List<PlannedVariant> plannedVariants) {
		this.plannedVariants.addAll(plannedVariants);
	}
	
	/**
	 * Gibt an, ob der Plan jedes richtig Produkt abbbildet.
	 * @return
	 */
	public boolean isCompletelyPlanned(Scenario scenario, Date scheduleChangeDeadline) {
		
		// Ist jede PlannedVariant Konsistent?
		for(PlannedVariant pv : this.plannedVariants) {
			if(!pv.isConsistent(scenario, this)) {
				
				// TODO
				System.out.println("-1-");
				
				return false;
			}
		}
		
		// Deadline nicht eingehalten:
		if(scenario.getChosenSchedule() != null) {
			List<Allocation> plannedAllocationList = this.getAllocationsInChangeDeadline(scenario, scheduleChangeDeadline);
			List<Allocation> mustAllocationList = new ArrayList<Allocation>();
			
			// MustAllocations füllen:
			for(PlannedVariant pv : scenario.getPossiblePlannedVariantsInChangeDeadline(scheduleChangeDeadline)) {
				for(Allocation a : pv.getAllocationList()) {
					if(a.getStart().before(scheduleChangeDeadline)) {
						mustAllocationList.add(a);
					}
				}
			}
			
			// Größe vergleichen:
			
			// TODO
			System.out.println("plannedAllocationList.size() " +plannedAllocationList.size() +" mustAllocationList.size() " +mustAllocationList.size());
			
			if(plannedAllocationList.size() < mustAllocationList.size()) {
				
				// TODO
				System.out.println("-2-");
				
				return false;
			}
			// Listen vergleichen:
			for(Allocation mustAllocation : mustAllocationList) {
				boolean removedFlag = false;
				for(int i = 0; i < plannedAllocationList.size(); i++) {
					if(plannedAllocationList.get(i).getStart().equals(mustAllocation.getStart())
					&& (plannedAllocationList.get(i).getResourceID() == mustAllocation.getResourceID())) {
						plannedAllocationList.remove(i);
						removedFlag = true;
					}
				}
				if(!removedFlag) {
					
					// TODO
					System.out.println("-3-");
					
					return false;
				}
			}
		}

		// Nicht alle möglichen Produkte eingeplant:
		List<Product> tmpProductsToPlan = new ArrayList<Product>();
		for(ScheduleOrder so : scenario.getOrders()) {
			tmpProductsToPlan.addAll(scenario.getPlanableProductsForOrder(so));
		}
		
		// TODO
		System.out.println("-3 1/2-" +(this.plannedVariants.size()) +" " +(tmpProductsToPlan.size()));
		
		if(this.plannedVariants.size() > tmpProductsToPlan.size()) {	
			
			System.out.println("-4-" +(this.plannedVariants.size()) +" " +(tmpProductsToPlan.size()));
			
			return false;
		}
		
		// Sind auch die richtigen Produkte verplant worden:
		
		// PlannedVariants durchlaufen und aus tmpProductsToPlan löschen:
		for(PlannedVariant pv : this.getPlannedVariants()) {
			tmpProductsToPlan.remove(pv.getProduct());
		}
		
		// Es wurden nicht alle möglichen Produkte verplant:
		if(!tmpProductsToPlan.isEmpty()) {
			
			// TODO
			System.out.println("-5-" +(this.plannedVariants.size()) +" " +(tmpProductsToPlan.size()));
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Liefert die PlannedVariants, die für eine Order geplant wurden:
	 * @param order
	 * @return
	 */
	public List<PlannedVariant> getPlannedVariantsForOrder(Scenario scenario, ScheduleOrder order) {
		List<PlannedVariant> tmpPlannedVariants = new ArrayList<PlannedVariant>();
		
		// PlannedVariants durchlaufen und alle passenden suchen:
		for(PlannedVariant pv : this.plannedVariants) {
			ScheduleOrder tmpOrder = pv.getScheduleOrder(scenario);
			if(tmpOrder != null && tmpOrder.equals(order)) {
				tmpPlannedVariants.add(pv);
			}
		}
		
		return tmpPlannedVariants;
	}
	
	/**
	 * Gibt Verzögerung des Auftrages mit maximaler Verzögerung aus.
	 * @return
	 */
	public int getMaxLateness(Scenario scenario){
		int tmpLateness = Integer.MIN_VALUE;
		
		// Orders durchlaufen:
		for(ScheduleOrder so : scenario.getOrders()) {
			int tmpOrderLateness = this.getOrderLateness(scenario, so);
			if(tmpLateness < tmpOrderLateness) {
				tmpLateness = tmpOrderLateness;
			}
		}
		
		return tmpLateness;
	}
	
	/**
	 * Summiert alle Verzögerungen in min.
	 * @return
	 */
	public int getSummedLateness(Scenario scenario){
		int tmpLateness = 0;
		
		// Orders durchlaufen und zählen:
		for(ScheduleOrder so : this.getPlannedOrders(scenario)) {
			tmpLateness += this.getOrderLateness(scenario, so);
		}
		
		return tmpLateness;
	}
	
	/**
	 * Gibt alle verspäten Orders aus.
	 * @return
	 * @throws NotPlannedException
	 */
	public List<ScheduleOrder> getLateOrders(Scenario scenario) throws NotPlannedException {
		Set<ScheduleOrder> tmpLateOdersSet = new HashSet<ScheduleOrder>();
		
		// Verspätete Orders suchen:
		for(PlannedVariant pv : this.plannedVariants) {	
			if(pv.getScheduleOrder(scenario) != null && pv.getFinish(scenario).after(pv.getScheduleOrder(scenario).getEarlistDueTime())) {
				tmpLateOdersSet.add(pv.getScheduleOrder(scenario));
			}
		}
		
		return new ArrayList<ScheduleOrder>(tmpLateOdersSet);
	}
	
	/**
	 * Gibt die Verspätung zu einer Order in Minuten an.
	 * @param order
	 * @return
	 */
	public int getOrderLateness(Scenario scenario, ScheduleOrder order) {
		
		// Falls die Order gar nicht geplant wurde.
		if(!this.getPlannedOrders().contains(order.getOrderID())) {
			return 0;
		}
		
		Date tmpFinish = new Date(0);
		// Späteste PlannedVariant suchen:
		for(PlannedVariant pv : this.getPlannedVariantsForOrder(scenario, order)) {
			if(pv.getFinish(scenario).after(tmpFinish)) {
				tmpFinish = pv.getFinish(scenario);
			}
		}
		
		return (int)((tmpFinish.getTime() - order.getEarlistDueTime().getTime()) /MINUTE);
	}
	
	/**
	 * Gibt aus, ob der Plan zulässig ist.
	 * @return
	 */
	public boolean isConsistent(Scenario scenario, Date scheduleChangeDeadline) {
		return HardConstraints.getInstance().testHardConstraints(scenario, this, scheduleChangeDeadline);
	}

	/**
	 * Gibt die geplante Startzeit einer Order an.
	 * @param order
	 * @return
	 */
	public Date getOrderStartTime(Scenario scenario, ScheduleOrder order) {
		Date tmpDate = new Date(Long.MAX_VALUE);
		
		// Frühsten Start einer Allocation suchen:
		for(PlannedVariant pv : this.getPlannedVariantsForOrder(scenario, order)) {
			if(pv.getStart().before(tmpDate)) {
				tmpDate = pv.getStart();
			}
		}
		
		return tmpDate;
	}
	
	/**
	 * Gibt die geplante DueTime einer Order an.
	 * @param order
	 * @return
	 */
	public Date getOrderDueTime(Scenario scenario, ScheduleOrder order) {
		Date tmpDate = new Date(0);
		
		// Spätestes Ende einer Allocation suchen:
		for(PlannedVariant pv : this.plannedVariants) {
			if(pv.getFinish(scenario).after(tmpDate)) {
				tmpDate = pv.getFinish(scenario);
			}
		}
		
		return tmpDate;
	}
		
	/**
	 * Kopiert den Schedule
	 */
	public Schedule clone(Scenario scenario) {
		List<PlannedVariant> tmpPlannedVariants = new ArrayList<PlannedVariant>();
		Schedule tmpScheduleClone = new Schedule(this.scenarioID, tmpPlannedVariants, this.scheduleEvent);
		
		// PlannedVariantsListe clonen:
		for(PlannedVariant thisPV : this.plannedVariants) {
			tmpPlannedVariants.add(thisPV.clone(scenario));
		}
		
		// KeyFigureMap kopieren:
		Map<Integer, Integer> tmpKeyFigureMap = new HashMap<Integer, Integer>();	
		for(Integer keyFigureID : this.keyFigureValueMap.keySet()) {
			tmpKeyFigureMap.put(keyFigureID, this.keyFigureValueMap.get(keyFigureID));
		}
		tmpScheduleClone.setKeyFigureValueMap(tmpKeyFigureMap);
		
		// AlgorithmInformations kopieren:
		tmpScheduleClone.setAlgorithmInformations(new AlgorithmInformation(this.getAlgorithmInformations().getAlgorithmName(), 
				new Date(this.getAlgorithmInformations().getStartTime().getTime()), 
				new Date(this.getAlgorithmInformations().getDueTime().getTime()),
				this.getAlgorithmInformations().getInitialScheduleID()));
		
		return tmpScheduleClone;
	}	

	public AlgorithmInformation getAlgorithmInformations() {
		return algorithmInformations;
	}

	public void setAlgorithmInformations(AlgorithmInformation algorithmInformations) {
		this.algorithmInformations = algorithmInformations;
	}
	
	/**
	 * Gibt eine Allocation zu einer Ressource und Startzeit aus.
	 * @param resource
	 * @param startTime
	 * @return Allocation, falls es eine gibt, sonst null.
	 */
	public Allocation getAllocation(Scenario scenario, Resource resource, Date startTime) {
		Map<Resource, List<Allocation>> resources = this.getResources(scenario);
		
		if(resources.get(resource) == null) {
			return null;
		}
		for(Allocation a : resources.get(resource)) {
			if(a.getStart().equals(startTime)) {
				return a;
			}
			if(a.getStart().after(startTime)) {
				return null;
			}
		}
		return null;
	}
	
	public List<Allocation> getAllocationsInOneList(Scenario scenario) {
		List<Allocation> result = new ArrayList<Allocation>();
		
		for(List<Allocation> allocationList : this.getAllocations(scenario)) {
			for(Allocation a : allocationList) {
				result.add(a);
			}
		}
		
		return result;
	}
	
	/*
	 * Liefert eine Allocation, die jetzt aktiv ist,
	 * ansonsten die erste Allocation 
	 */
	public Allocation getAllocationNow(Scenario scenario) {
		Allocation result = this.getAllocationsInOneList(scenario).get(0);
		Date now = new Date();
		
		for(Allocation a : this.getAllocationsInOneList(scenario)) {
			Date finish = a.getFinish(scenario);
			if((a.getStart().before(now) || a.getStart().equals(now))
			&& (finish.after(now) || finish.equals(now))) {
				return a;
			}
		}
		
		return result;
	}
	
	/**
	 * Sagt, ob eine Belegung für den selben Auftrag erfasst ist.
	 * Wichtig für KeyFigure: OperationChanges
	 * @param resource
	 * @param allocation
	 * @return
	 */
	public boolean checkForSameAllocation(Scenario scenario, Resource resource, Allocation allocation) {
		Allocation tmpAllocation = this.getAllocation(scenario, resource, allocation.getStart());
		
		if(tmpAllocation != null && allocation.getScheduleOrder(scenario) != null 
		&& tmpAllocation.getScheduleOrder(scenario).equals(allocation.getScheduleOrder(scenario))) {
			return true;
			
		}
		
		return false;
	}

	/**
	 *  Alle Orders die nicht komplett eingeplant werden sind.
	 * @return
	 */
	public List<ScheduleOrder> getNotCompletelyPlannedOrders(Scenario scenario) {
		Set<ScheduleOrder> tmpNotPlannedOrders = new HashSet<ScheduleOrder>();
		
		// Orders durchlaufen:
		for(ScheduleOrder so : scenario.getOrders()) {
			// Sind alle Produkte auch verplant?
			if(so.getProducts(scenario).size() != this.getPlannedVariantsForOrder(scenario, so).size()) {
				tmpNotPlannedOrders.add(so);
			}
		}
		
		return new ArrayList<ScheduleOrder>(tmpNotPlannedOrders);
	}
	
	
	/**
	 * Gibt eine Map mit eingeplanten Produkten aus. Wobei der Integer-Wert
	 * die Anzahl der eingeplanten Produkte angibt.
	 * @param order
	 * @return
	 */
	public Map<Product, Integer> getPlannedProductsForOrder(Scenario scenario, ScheduleOrder order) {
		Map<Product, Integer> tmpPlannedProductsMap = new HashMap<Product, Integer>();
		
		// Liste durchlaufen und zählen:
		for(PlannedVariant pv : this.getPlannedVariantsForOrder(scenario, order)) {
			if(tmpPlannedProductsMap.get(pv.getProduct()) == null) {
				tmpPlannedProductsMap.put(pv.getProduct(), 1);
			} else {
				tmpPlannedProductsMap.put(pv.getProduct(), tmpPlannedProductsMap.get(pv.getProduct()) +1);
			}
		}
		
		return tmpPlannedProductsMap;
	}
	
	/**
	 * Gibt eine Map mit nicht eingeplanten Produkten aus. Wobei der Integer-Wert
	 * die Anzahl der nicht eingeplanten Produkte angibt.
	 * @param order
	 * @return
	 */
	public Map<Product, Integer> getNotPlannedProductsForOrder(Scenario scenario, ScheduleOrder order) {
		Map<Product, Integer> tmpNotPlannedProductsMap = new HashMap<Product, Integer>();
		Map<Product, Integer> tmpPlannedProductsMap = this.getPlannedProductsForOrder(scenario, order);
		Map<Product, Integer> tmpProductsToPlan = order.getProductsMap(scenario);
		
		// Zu planende Produkte durchlaufen:
		for(Product productToPlan : tmpProductsToPlan.keySet()) {
			// Produkt ist gar nicht eingeplant:
			if(tmpPlannedProductsMap.get(productToPlan) == null) {
				tmpNotPlannedProductsMap.put(productToPlan, tmpProductsToPlan.get(productToPlan));
				continue;
			} 
			
			// Nicht genug Produkte eingeplant:
			int tmpToPlan = tmpProductsToPlan.get(productToPlan);
			int tmpPlanned = tmpPlannedProductsMap.get(productToPlan);
			if(tmpToPlan > tmpPlanned) {
				// Nicht geplante Anzahl merken
				tmpNotPlannedProductsMap.put(productToPlan, tmpToPlan - tmpPlanned);
				continue;
			}
		}
		
		return tmpNotPlannedProductsMap;
	}
	
	/**
	 * Gibt eine Map mit nicht geplanten Produkten aus.
	 * @return
	 */
	public Map<ScheduleOrder, List<Product>> getNotPlannedOrderProducts(Scenario scenario) {
		Map<ScheduleOrder, List<Product>> tmpNotPlannedOrderProductsMap = new HashMap<ScheduleOrder, List<Product>>();
		
		// Alle Orders durchlaufen:
		for(ScheduleOrder so : scenario.getOrders()) {
			Map<Product, Integer> tmpNotPlannedProductsForOrder = this.getNotPlannedProductsForOrder(scenario, so);
			
			// Nur beachten, wenn es nicht geplante Produkte gibt.
			if(tmpNotPlannedProductsForOrder.isEmpty()) {
				continue;
			}
			
			tmpNotPlannedOrderProductsMap.put(so, new ArrayList<Product>());
			// Nicht geplante Produkte durchlaufen:
			for(Product p : tmpNotPlannedProductsForOrder.keySet()) {
				// Anzahl der Produkte beachten:
				for(int i = 0; i < tmpNotPlannedProductsForOrder.get(p); i++) {
					tmpNotPlannedOrderProductsMap.get(so).add(p);
				}
			}
		}
		
		return tmpNotPlannedOrderProductsMap;
	}
	
	/**
	 * Gibt alle Allocations aus, die in die ChangeDeadline fallen.
	 * @param scenario
	 * @param scheduleChangeDeadline
	 * @return
	 */
	public List<Allocation> getAllocationsInChangeDeadline(Scenario scenario, Date scheduleChangeDeadline) {
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
		
		for(PlannedVariant pv : this.getPlannedVariants()) {
			for(Allocation a : pv.getAllocationList()) {
				if(a.getStart().before(scheduleChangeDeadline)) {
					Allocation tmpAllocationClone = new Allocation(a.getUID(), a.getStart(), a.getOperation(), a.getScheduleOrder(scenario), a.getResourceID());
					tmpAllocationList.add(tmpAllocationClone);
				}
			}
		}
		
		return tmpAllocationList;
	}
	
	/**
	 * Gibt alle möglichen Allocations in der scheduleChangeDeadline an.
	 * @param scenario
	 * @param scheduleChangeDeadline
	 * @return
	 */
	public List<Allocation> getPossibleAllocationsInChangeDeadline(Scenario scenario, Date scheduleChangeDeadline) {
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
		
		for(Allocation a : this.getAllocationsInChangeDeadline(scenario, scheduleChangeDeadline)) {
			if(a.isDone() || (!scenario.getResources().get(a.getResourceID()).isInBreakDown())) {
				tmpAllocationList.add(a);
			}
		}
		
		return tmpAllocationList;
	}
	
	public List<Allocation> getImpossibleAllocationsInChangeDeadline(Scenario scenario, Date scheduleChangeDeadline) {
		List<Allocation> tmpAllocationList = this.getAllocationsInChangeDeadline(scenario, scheduleChangeDeadline);
		
		tmpAllocationList.removeAll(this.getPossibleAllocationsInChangeDeadline(scenario, scheduleChangeDeadline));
		
		return tmpAllocationList;
	}
	
	/**
	 * Gibt alle PlannedVariants aus, die sich in der ChangeDeadline befinden.
	 * @param scenario
	 * @param scheduleChangeDeadline
	 * @return
	 */
	public List<PlannedVariant> getPossiblePlannedVariantsInChangeDeadline(Scenario scenario, Date scheduleChangeDeadline) {
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>();
		
		for(PlannedVariant pv : this.getPlannedVariants()) {
			
			// Order nicht mehr da:
			if(scenario.getOrder(pv.getScheduleOrderID()) == null) {
				continue;
			}
			
			// Product/Variant nicht mehr verfügbar:
			if(scenario.getProductByVariantID(pv.getPlannedVariantID()) == null
			|| scenario.getVariant(pv.getPlannedVariantID()) == null) {
				continue;
			}
			
			if(pv.getStart().before(scheduleChangeDeadline)) {
				boolean tmpPVPossibleFlag = true;
				for(Allocation a : pv.getAllocationList()) {
					if(!a.isDone() && !scenario.getResources().get(a.getResourceID()).isAvailable(a.getStart(), a.getFinish(scenario))) {
						tmpPVPossibleFlag = false;
						break;
					}
				}
				if(tmpPVPossibleFlag) {
					tmpPlannedVariantList.add(pv);
				}
			}
		}
		
		return tmpPlannedVariantList;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public ScheduleEvent getScheduleEvent() {
		return scheduleEvent;
	}

	public void setScheduleEvent(ScheduleEvent scheduleEvent) {
		this.scheduleEvent = scheduleEvent;
	}
	
	public Date getFinish(Scenario scenario) {
		Date tmpDueDate = new Date(0);
		for(PlannedVariant pv : plannedVariants) {
			Date pvFinish = new Date(pv.getFinish(scenario).getTime());
			if(pvFinish.after(tmpDueDate)) {
				tmpDueDate = pvFinish;
			}
		}
		
		return tmpDueDate;
	}
	
	public Date getStart() {
		Date tmpStartDate = new Date(Long.MAX_VALUE);
		for(PlannedVariant pv : plannedVariants) {
			if(pv.getStart().before(tmpStartDate)) {
				tmpStartDate = new Date(pv.getStart().getTime());
			}
		}
		
		return tmpStartDate;
	}

	/**
	 * 
	 * @return Map<Integer = KeyFigure Type, Integer = Value>
	 */
	public Map<Integer, Integer> getKeyFigureValueMap() {
		return keyFigureValueMap;
	}

	public void setKeyFigureValueMap(Map<Integer, Integer> keyFigureValueMap) {
		this.keyFigureValueMap = keyFigureValueMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + scheduleID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (scheduleID != other.scheduleID)
			return false;
		return true;
	}
	
	/**
	 * Gibt die höchste AllocationID aus.
	 * @return
	 */
	public int getHighestAllocationID(Scenario scenario) {
		int tmpAllocationID = 0;
		
		for(List<Allocation> aList : this.getAllocations(scenario)) {
			for(Allocation a : aList) {
				if(a.getUID() > tmpAllocationID) {
					tmpAllocationID = a.getUID();
				}
			}
		}
		return tmpAllocationID;
	}
}
