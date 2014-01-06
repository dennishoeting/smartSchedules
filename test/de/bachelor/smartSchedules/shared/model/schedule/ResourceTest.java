package de.bachelor.smartSchedules.shared.model.schedule;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import de.bachelor.smartSchedules.server.model.algorithm.predictive.EDDAlgorithmOrderOriented;

/**
 * 
 * @author timo
 *
 */
public class ResourceTest {

	/**
	 * Testet die addOperation Methode.
	 */
	@Test
	public void testAddOperation() {
		// Ressource erstellen:
		Resource resource = new Resource(0, "Autoreifen Maschine", 100);
		Resource resource2 = new Resource(1, "Fensterscheiben Maschine", 100);
		Resource resource3 = new Resource(2, "Auspuff Maschine", 100);
		Resource resource4 = new Resource(3, "Lackier-Maschine", 100);
		Resource resource5 = new Resource(4, "Auto zusammenbauen Maschine", 100);
		
		// Produkt erstellen:
		Product product = new Product(0, "Auto", new ArrayList<Variant>());
		
		
		// Variante erstellen:
		product.addVariant(new Variant(0, new ArrayList<Operation>(), product));
		
		//reihenfolge ändern
		//nur dueDate in scheduleOrderKonst
		
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
		Scenario scenario1 = new Scenario(0, 0, "Elrond");
		
		scenario1.addResource(resource);
		scenario1.addResource(resource2);
		scenario1.addResource(resource3);
		scenario1.addResource(resource4);
		scenario1.addResource(resource5);
		
		scenario1.addProduct(product);
		
		scenario1.addOrder(order1);
		scenario1.addOrder(order2);
		scenario1.addOrder(order3);
		scenario1.addOrder(order4);
		scenario1.addOrder(order5);
		scenario1.addOrder(order6);
		
		
		// Schedule nach EDD erstellen:
		Schedule scheduleEDD = new EDDAlgorithmOrderOriented().produceScheduleInternal(scenario1);
		
		boolean tmpBoolean = true;
		for(Resource r : scheduleEDD.getResources().keySet()) {
			for(int i = 1; i < scheduleEDD.getResources().get(r).size(); i++) {
				if(scheduleEDD.getResources().get(r).get(i - 1).getFinish().after(scheduleEDD.getResources().get(r).get(i).getStart())) {
					tmpBoolean = false;
					break;
				}
			}
			if(!tmpBoolean) {
				break;
			}
		}
		
		assertTrue(
				tmpBoolean
		);
	}
}
