package de.bachelor.smartSchedules.shared.model.schedule;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.bachelor.smartSchedules.server.model.ScenarioManagerImpl;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.EDDAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.ESTAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.SPTAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.reactive.ReactiveAlgorithmImpl1;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeResourceAvailabilityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

public class ScheduleTest {

	private Schedule scheduleEDD, scheduleEST, scheduleSPT;
	private Scenario scenario;
	private Product product;
	private ScheduleOrder order1;
	Resource resource, resource2, resource3, resource4, resource5, resource6, resource7;
	
	@Before
	public void setUp() throws Exception {
		// Ressource erstellen:
		resource = new Resource(0, "Autoreifen Maschine", 100);
		resource2 = new Resource(1, "Fensterscheiben Maschine", 100);
		resource3 = new Resource(2, "Auspuff Maschine", 100);
		resource4 = new Resource(3, "Lackier-Maschine", 100);
		resource5 = new Resource(4, "Auto-zusammenbauen Maschine", 100);
		resource6 = new Resource(5, "Lackier-Maschine2", 100);
		resource7 = new Resource(6, "Auto-zusammenbauen Maschine2", 100);
		
		// Produkt erstellen:
		product = new Product(0, "Auto", new ArrayList<Variant>());
		
		
		// Variante erstellen:
		product.getVariants().add(new Variant(0, new ArrayList<Operation>(), product));
		
		// Operation erstellen:
		ArrayList<Resource> lackierResourcen = new ArrayList<Resource>();
		lackierResourcen.add(resource4);
		lackierResourcen.add(resource6);
		ArrayList<Resource> zusammenbauResourcen = new ArrayList<Resource>();
		zusammenbauResourcen.add(resource5);
		zusammenbauResourcen.add(resource7);
		Operation operation1 = new Operation(0, resource, 60*24, "Reifen herstellen");
		Operation operation2 = new Operation(1, resource2, 60*48, "Fensterscheiben herstellen", operation1);
		Operation operation3 = new Operation(2, resource3, 60*15, "Auspuff herstellen", operation2);
		Operation operation4 = new Operation(3, lackierResourcen, 60*96, "Auto lackieren", operation3);
		Operation operation5 = new Operation(4, zusammenbauResourcen, 60*128, "Auto zusammenbauen", operation4);
		product.getVariant(0).addOperation(operation1);
		product.getVariant(0).addOperation(operation2);
		product.getVariant(0).addOperation(operation3);
		product.getVariant(0).addOperation(operation4);
		product.getVariant(0).addOperation(operation5);
		
		// Szenario erstellen:
		scenario = new Scenario(0, 0, "Elrond");

		// Aufträge:
		order1 = new ScheduleOrder(1, 0, new ArrayList<Product>(), "Auftrag: 1", new Date(), new Date(System.currentTimeMillis() +4000000), 1);
		order1.addProduct(product);
		order1.addProduct(product);
		ScheduleOrder order2 = new ScheduleOrder(2, 0, new ArrayList<Product>(), "Auftrag: 2", new Date(), new Date(System.currentTimeMillis() +2000000), 1);
		order2.addProduct(product);
		ScheduleOrder order3 = new ScheduleOrder(3, 0, new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(System.currentTimeMillis() +3000000), 1);
		order3.addProduct(product);
		ScheduleOrder order4 = new ScheduleOrder(4, 0, new ArrayList<Product>(), "Auftrag: 4", new Date(), new Date(System.currentTimeMillis() +9000000), 1);
		order4.addProduct(product);
		ScheduleOrder order5 = new ScheduleOrder(5, 0, new ArrayList<Product>(), "Auftrag: 5", new Date(), new Date(System.currentTimeMillis() +1000000), 1);
		order5.addProduct(product);
		ScheduleOrder order6 = new ScheduleOrder(6, 0, new ArrayList<Product>(), "Auftrag: 6", new Date(), new Date(System.currentTimeMillis() +30000000), 1);
		order6.addProduct(product);
		
		scenario.addResource(resource);
		scenario.addResource(resource2);
		scenario.addResource(resource3);
		scenario.addResource(resource4);
		scenario.addResource(resource5);
		scenario.addResource(resource6);
		scenario.addResource(resource7);
		
		scenario.addProduct(product);

		scenario.addOrder(order1);
		scenario.addOrder(order2);
		scenario.addOrder(order3);
		scenario.addOrder(order4);
		scenario.addOrder(order5);
		scenario.addOrder(order6);
		
		scenario.setKeyFigureList(new ScenarioManagerImpl().getKeyFigures());
			
		// Schedules erstellen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null, null);
			
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null, null);
		
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null, null);
	}

	@Test
	public void testIsAllowed() {
		
//		//TODO
//		Map<Resource, List<Allocation>> tmpMap = scheduleEDD.getResources();
//		for(Resource r : tmpMap.keySet()) {
//			System.out.println("-----");
//			for(Allocation a : tmpMap.get(r)) {
//				System.out.println(r.getName() +" " +a.getStart() +" " +a.getFinish() +" " +a.getDuration());
//			}
//		}
//		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));		
		
		// New Order Event erzeugen:
		ScheduleOrder order7 = new ScheduleOrder(7, 0, new ArrayList<Product>(), "Auftrag: 7", new Date(), new Date(System.currentTimeMillis() +30000000), 1);
		order7.addProduct(product);
		
		List<ScheduleOrder> orderList = new ArrayList<ScheduleOrder>();
		orderList.add(order7);
		
		ScheduleEvent newOrdersEvent = new NewOrdersEvent(scenario.getScenarioID(), orderList, new Date());
		
		List<ScheduleEvent> eventList = new ArrayList<ScheduleEvent>();
		eventList.add(newOrdersEvent);
		
		newOrdersEvent.changeScenario(scenario);
				
		assertTrue(!scheduleEDD.isConsistent(scenario));
		assertTrue(!scheduleEST.isConsistent(scenario));
		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		Schedule reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, eventList);
				
		
//		for(Resource r : reactiveSchedule.getResources().keySet()) {
//			System.out.println("---" +r.getName());
//			for(Allocation a : reactiveSchedule.getResources().get(r)) {
//				System.out.println(a.getOperation().getName() +" " +a.getStart() +" " +a.getFinish());
//			}
//		}
		
		assertTrue(reactiveSchedule.isConsistent(scenario));

		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
		
		
		
		// Remove Order Event erzeugen:
		ScheduleEvent removeOrdersEvent = new RemoveOrdersEvent(scenario.getScenarioID(), orderList, new Date());
		
		eventList = new ArrayList<ScheduleEvent>();
		eventList.add(removeOrdersEvent);
		
		removeOrdersEvent.changeScenario(scenario);
				
		assertTrue(!scheduleEDD.isConsistent(scenario));
		assertTrue(!scheduleEST.isConsistent(scenario));
		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, eventList);
		assertTrue(reactiveSchedule.isConsistent(scenario));

		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);	
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
		
		// Remove Orders Products Event:
		Map<ScheduleOrder, List<Product>> tmpRemoveOrdersProductsMap = new HashMap<ScheduleOrder, List<Product>>();
		tmpRemoveOrdersProductsMap.put(order1, order1.getProducts(scenario));
		ScheduleEvent removeOrdersProductsEvent = new RemoveOrdersProductsEvent(scenario.getScenarioID(), tmpRemoveOrdersProductsMap, new Date());
		
	
		removeOrdersProductsEvent.changeScenario(scenario);
		
		assertTrue(!scheduleEDD.isConsistent(scenario));
		assertTrue(!scheduleEST.isConsistent(scenario));
		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, removeOrdersProductsEvent);
		assertTrue(reactiveSchedule.isConsistent(scenario));

		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);	
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
		
		// MachineBreakDownEvent:
		List<Resource> resourceListForEvent = new ArrayList<Resource>();
		// Die erste der zwei Autozusammenbau Maschinen geht kaputt:
		resourceListForEvent.add(resource5);
		ScheduleEvent machineBreakDownEvent = new MachineBreakDownsEvent(scenario.getScenarioID(), resourceListForEvent, new Date());
		
	
		machineBreakDownEvent.changeScenario(scenario);
		
		assertTrue(!scheduleEDD.isConsistent(scenario));
		assertTrue(!scheduleEST.isConsistent(scenario));
		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, machineBreakDownEvent);
		
		for(Resource r : reactiveSchedule.getResources(scenario).keySet()) {
			System.out.println(r.getName());
			for(Allocation a : reactiveSchedule.getResources(scenario).get(r)) {
				System.out.println(a.getStart() +" " +a.getFinish(scenario));
			}
		}
		
		assertTrue(reactiveSchedule.isConsistent(scenario));
		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);	
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
		
		
		
		// MachineRepairedEvent:
		List<Resource> resourceListForEvent2 = new ArrayList<Resource>();
		// Die erste der zwei Autozusammenbau Maschinen geht kaputt:
		resourceListForEvent2.add(resource5);
		ScheduleEvent machineRepairedEvent = new MachineRepairedEvent(scenario.getScenarioID(), resourceListForEvent2, new Date());
		
	
		machineBreakDownEvent.changeScenario(scenario);
		
//		assertTrue(!scheduleEDD.isConsistent(scenario));
//		assertTrue(!scheduleEST.isConsistent(scenario));
//		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, machineRepairedEvent);
		assertTrue(reactiveSchedule.isConsistent(scenario));
		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);	
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
		
		
		// ChangeResourceAvailability:
		Map<Resource, Integer> resourceAvailabilityMap = new HashMap<Resource, Integer>();
		// Die Autoreifenmaschine soll langsamer werden und alles verschieben:
		resourceAvailabilityMap.put(resource, 50);
		ScheduleEvent changeResourceAvailabilityEvent = new ChangeResourceAvailabilityEvent(scenario.getScenarioID(), resourceAvailabilityMap, new Date());
		
	
		changeResourceAvailabilityEvent.changeScenario(scenario);
		
		//TODO
//		for(Resource r : scheduleEDD.getResources().keySet()) {
//			System.out.println(r.getName());
//			for(Allocation a : scheduleEDD.getResources().get(r)) {
//				System.out.println(a.getStart() +" " +a.getFinish());
//			}
//		}
		
		assertTrue(!scheduleEDD.isConsistent(scenario));
		assertTrue(!scheduleEST.isConsistent(scenario));
		assertTrue(!scheduleSPT.isConsistent(scenario));
		
		// Reaktiv Planen:
		reactiveSchedule = new ReactiveAlgorithmImpl1().produceSchedule(scenario, scheduleEDD, changeResourceAvailabilityEvent);
		assertTrue(reactiveSchedule.isConsistent(scenario));
		
		// Neuplanen:
		scheduleEDD = new EDDAlgorithmOrderOriented().produceSchedule(scenario, null);
		scheduleEST = new ESTAlgorithmOrderOriented().produceSchedule(scenario, null);	
		scheduleSPT = new SPTAlgorithmOrderOriented().produceSchedule(scenario, null);
		
		assertTrue(scheduleEDD.isConsistent(scenario));
		assertTrue(scheduleEST.isConsistent(scenario));
		assertTrue(scheduleSPT.isConsistent(scenario));
	
	}

}
