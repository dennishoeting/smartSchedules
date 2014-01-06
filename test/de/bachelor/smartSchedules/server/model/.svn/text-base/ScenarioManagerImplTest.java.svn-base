package de.bachelor.smartSchedules.server.model;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Sleep;
import org.junit.Before;
import org.junit.Test;

import de.bachelor.smartSchedules.server.model.algorithm.predictive.EDDAlgorithmOrderOriented;
import de.bachelor.smartSchedules.shared.model.schedule.HardConstraints;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

public class ScenarioManagerImplTest {
	
	private Scenario scenario = null;
	private Product product = null;

	@Before
	public void setUp() throws Exception {
		// Ressource erstellen:
		Resource resource = new Resource(0, "Autoreifen Maschine", 100);
		Resource resource2 = new Resource(1, "Fensterscheiben Maschine", 100);
		Resource resource3 = new Resource(2, "Auspuff Maschine", 100);
		Resource resource4 = new Resource(3, "Lackier-Maschine", 100);
		Resource resource5 = new Resource(4, "Auto zusammenbauen Maschine", 100);
		
		// Produkt erstellen:
		product = new Product(0, "Auto", new ArrayList<Variant>());
		
		
		// Variante erstellen:
		product.getVariants().add(new Variant(0, new ArrayList<Operation>(), product));
		
		// Operation erstellen:
		product.getVariant(0).addOperation(
				new Operation(0, resource, 3600*24, "Reifen herstellen"));
		product.getVariant(0).addOperation(
				new Operation(1, resource2, 3600*48, "Fensterscheiben herstellen"));
		product.getVariant(0).addOperation(
				new Operation(2, resource3, 3600*15, "Auspuff herstellen"));
		product.getVariant(0).addOperation(
				new Operation(3, resource4, 3600*96, "Auto lackieren"));
		product.getVariant(0).addOperation(
				new Operation(4, resource5, 3600*128, "Auto zusammenbauen"));
		
		// Aufträge:
		ScheduleOrder order1 = new ScheduleOrder(1, new ArrayList<Product>(), "Auftrag: 1", new Date(), new Date(System.currentTimeMillis() +4000000), 1);
		order1.getProducts().add(product);
		ScheduleOrder order2 = new ScheduleOrder(2, new ArrayList<Product>(), "Auftrag: 2", new Date(), new Date(System.currentTimeMillis() +2000000), 1);
		order2.getProducts().add(product);
		ScheduleOrder order3 = new ScheduleOrder(3, new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(System.currentTimeMillis() +3000000), 1);
		order3.getProducts().add(product);
		ScheduleOrder order4 = new ScheduleOrder(4, new ArrayList<Product>(), "Auftrag: 4", new Date(), new Date(System.currentTimeMillis() +9000000), 1);
		order4.getProducts().add(product);
		ScheduleOrder order5 = new ScheduleOrder(5, new ArrayList<Product>(), "Auftrag: 5", new Date(), new Date(System.currentTimeMillis() +1000000), 1);
		order5.getProducts().add(product);
		ScheduleOrder order6 = new ScheduleOrder(6, new ArrayList<Product>(), "Auftrag: 6", new Date(), new Date(System.currentTimeMillis() +30000000), 1);
		order6.getProducts().add(product);
		
		// Szenario erstellen:
		Map<Integer, Resource> resources = new HashMap<Integer, Resource>();
			resources.put(0, resource);
			resources.put(2, resource2);
			resources.put(3, resource3);
			resources.put(4, resource4);
			resources.put(5, resource5);
		
		Map<Integer, Product> products = new HashMap<Integer, Product>();
			products.put(0, product);
		
		List<ScheduleOrder> orders = new ArrayList<ScheduleOrder>();
			orders.add(order1);
			orders.add(order2);
			orders.add(order3);
			orders.add(order4);
			orders.add(order5);
			orders.add(order6);
			
			
		scenario = new Scenario(0, 0, "Elrond", resources, products, orders, new ArrayList<KeyFigure>());
		scenario.setChosenSchedule(new EDDAlgorithmOrderOriented().produceSchedule(scenario, null));
		
		ScenarioManagerImpl.addScenario(scenario);
	}
	
	@Test
	public void test() {
		// Events erzeugen:
		List<ScheduleOrder> orderList = new ArrayList<ScheduleOrder>();
		ScheduleOrder order7 = new ScheduleOrder(1, new ArrayList<Product>(), "Auftrag: 1", new Date(), new Date(System.currentTimeMillis() +4000000), 1);
		order7.getProducts().add(product);
		orderList.add(order7);
		ScheduleEvent event1 = new NewOrdersEvent(scenario, orderList, new Date(System.currentTimeMillis() +1));
		ScenarioManagerImpl.addEvent(event1);
		
//		assertTrue(true);
	}

}
