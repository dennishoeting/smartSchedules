package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Die Hard Constraints müssen alle erfüllt sein.
 * Sind immer gleich -> Singleton
 * @author timo
 *
 */
public class HardConstraints implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -2675556422916328600L;
	private static HardConstraints instance = null;
	private static final int SECOND = 1000;
	
	/**
	 * Default
	 */
	private HardConstraints() {
		
	}
	
	/**
	 * Testet alle Hard Constraints für einen Plan.
	 * @param schedule
	 * @return
	 */
	public boolean testHardConstraints(Scenario scenario, Schedule schedule, Date scheduleChangeDeadline) {
		if(!isCompletlyPlanned(scenario, schedule, scheduleChangeDeadline)) {
			System.out.println("HC1");
			return false;
		}
		if(checkMachineReservations(scenario, schedule)) {
			System.out.println("HC2");
			return false;
		}
		if(checkMachineRestrictions(scenario, schedule)) {
			System.out.println("HC3");
			return false;
		}
		if(checkAllocationResources(scenario, schedule)) {
			System.out.println("HC4");
			return false;
		}
		if(checkForAllocationIdDupicates(scenario, schedule)) {
			System.out.println("HC5");
			return false;
		}
		if(checkSecondsBetweenAllocations(scenario, schedule)) {
			System.out.println("HC6");
			return false;
		}
		return true;
	}
	
	/**
	 * Singleton
	 * @return
	 */
	public static HardConstraints getInstance() {
		if(instance == null) {
			instance = new HardConstraints();
		}
		return instance;
	}
	
	/**
	 * Überprüft, ob Maschinen mehrfach belegt sind.
	 * @return
	 */
	private boolean checkMachineReservations(Scenario scenario, Schedule schedule) {
		Map<Resource, List<Allocation>> tmpResourceMap = schedule.getResources(scenario);
		for(Resource r : tmpResourceMap.keySet()) {
			for(int i = 1; i < tmpResourceMap.get(r).size(); i++) {
				// Startzeit kleiner als Ende von i-1
				if(tmpResourceMap.get(r).get(i).getStart().before(tmpResourceMap.get(r).get(i - 1).getFinish(scenario))) {
					return true;
				}
				// Startzeit i und i-1 gleich
				if(tmpResourceMap.get(r).get(i).getStart().equals(tmpResourceMap.get(r).get(i - 1).getStart())) {
					return true;
				}
				if(tmpResourceMap.get(r).get(i).getFinish(scenario).equals(tmpResourceMap.get(r).get(i - 1).getFinish(scenario))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Überprüft, ob die Maschinen Restriktionen beachtet wurden:
	 * @param schedule
	 * @return
	 */
	private boolean checkMachineRestrictions(Scenario scenario, Schedule schedule) {
		for(Resource r : schedule.getResources(scenario).keySet()) {
			for(Allocation a : schedule.getResources(scenario).get(r)) {
				for(ResourceRestriction rr : scenario.getResources().get(a.getResourceID()).getResourceRestrictions()) {
					if(rr.isBetweenRestriction(a.getStart(), a.getFinish(scenario))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Prüft, ob alle Belegungen auch auf den richtigen Ressourcen verplant sind.
	 * @param scenario
	 * @param schedule
	 * @return
	 */
	private boolean checkAllocationResources(Scenario scenario, Schedule schedule) {
		
		for(List<Allocation> aList : schedule.getAllocations(scenario)) {
			for(Allocation a : aList) {
				if(!a.getOperation().getResourceAlternatives().contains(a.getResourceID())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Prüft, ob eine Allocation ID doppelt vorhanden ist.
	 * @param scenario
	 * @param schedule
	 * @return
	 */
	private boolean checkForAllocationIdDupicates(Scenario scenario, Schedule schedule) {
		
		int allocationCount = 0;
		Set<Integer> allocationIDSet = new HashSet<Integer>();
		
		// Alle AllocationIDs ins Set:
		for(List<Allocation> aList : schedule.getAllocations(scenario)) {
			for(Allocation a : aList) {
				allocationIDSet.add(a.getUID());
				allocationCount++;
			}
		}
		
		return allocationCount > allocationIDSet.size() ? true : false;
	}
	
	/**
	 * Prüft, ob die secondsBetweenAllocations eingehalten wurden.
	 * @param scenario
	 * @param schedule
	 * @return
	 */
	private boolean checkSecondsBetweenAllocations(Scenario scenario, Schedule schedule) {
		Map<Resource, List<Allocation>> resourceAllocationMap = new HashMap<Resource, List<Allocation>>(schedule.getResources(scenario));
		
		for(Resource r : resourceAllocationMap.keySet()) {
			List<Allocation> aList = resourceAllocationMap.get(r);
			for(int i = 1; i < aList.size(); i++) {
				if((aList.get(i).getStart().getTime() - aList.get(i - 1).getFinish(scenario).getTime()) < (scenario.getSecondsBetweenAllocations() * SECOND)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Prüft, ob der Plan komplett geplant wuPrde.
	 * Es darf pro Produktbestellung nur eine Variante Produziert werden.
	 * @param schedule
	 * @return
	 */
	private boolean isCompletlyPlanned(Scenario scenario, Schedule schedule, Date scheduleChangeDeadline) {
		return schedule.isCompletelyPlanned(scenario, scheduleChangeDeadline);
	}
}
