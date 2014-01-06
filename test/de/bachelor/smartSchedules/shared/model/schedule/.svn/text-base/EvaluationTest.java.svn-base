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
import de.bachelor.smartSchedules.server.model.algorithm.predictive.PredictiveAlgorithm;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.ChangedOperations;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.Lateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MeanLateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MissedDeadLines;
/**
 * Testet die Evaluierung
 * @author timo
 *
 */
public class EvaluationTest {
	
	private Scenario scenario = null;

	@Before
	public void setUp() throws Exception {
		// Ressource erstellen:
		Resource resource = new Resource(0, "Autoreifen Maschine", 100);
		Resource resource2 = new Resource(1, "Fensterscheiben Maschine", 100);
		Resource resource3 = new Resource(2, "Auspuff Maschine", 100);
		Resource resource4 = new Resource(3, "Lackier-Maschine", 100);
		Resource resource5 = new Resource(4, "Auto zusammenbauen Maschine", 100);
		
		// Produkt erstellen:
		Product product = new Product(0, "Auto", new ArrayList<Variant>());
		
		
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
		ScheduleOrder order4 = new ScheduleOrder(3, new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(System.currentTimeMillis() +9000000), 1);
		order3.getProducts().add(product);
		ScheduleOrder order5 = new ScheduleOrder(3, new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(System.currentTimeMillis() +1000000), 1);
		order3.getProducts().add(product);
		ScheduleOrder order6 = new ScheduleOrder(3, new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(System.currentTimeMillis() +30000000), 1);
		order3.getProducts().add(product);
		
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
	}
	
	@Test
	public void testEvaluation() {
		List<PredictiveAlgorithm> paList = ScenarioManagerImpl.getPredictiveAlgorithms();
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		List<KeyFigure> paretoList = new ArrayList<KeyFigure>();
		Schedule bestSchedule = null;
		
		// Schedules erzeugen:
		for(PredictiveAlgorithm pa : paList) {
			scheduleList.add(pa.produceSchedule(scenario, null));
		}
		
		// Schedules Evaluieren:
		paretoList.add(new ChangedOperations());
		paretoList.add(new Lateness());
		paretoList.add(new MeanLateness());
		paretoList.add(new MissedDeadLines());
		
		Evaluation evaluation = new Evaluation(paretoList);
		try {
			bestSchedule = evaluation.getBestSchedule(scheduleList).get(0);
		} catch (NotPlannedException e) {
			e.printStackTrace();
		}
		
		
		// Besten Schedule gegen die anderen testen:
		List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
		tmpScheduleList.addAll(scheduleList);
		
		for(int i = 0; i < paretoList.size() && tmpScheduleList.size() > 1; i++) {
			for(Schedule s : scheduleList) {
				try {
					if(paretoList.get(i).calculate(s) > paretoList.get(i).calculate(bestSchedule)) {
						tmpScheduleList.remove(s);
					}
				} catch (NotPlannedException e) {
					e.printStackTrace();
				}
			}
		}
		
		assertTrue(tmpScheduleList.contains(bestSchedule));
	}

}
