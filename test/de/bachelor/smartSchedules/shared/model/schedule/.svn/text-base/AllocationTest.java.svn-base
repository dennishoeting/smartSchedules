package de.bachelor.smartSchedules.shared.model.schedule;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.bachelor.smartSchedules.server.model.algorithm.predictive.EDDAlgorithmOrderOriented;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
/**
 * Testklasse für Allocation (Belegung)
 * @author timo
 *
 */
public class AllocationTest {
	/**
	 * Testet, ob ein Datum in einer Belegung liegt.
	 */
	@Test
	public void testIsDateInAllocation() {
		Date tmpDate = new Date();
		
		// Ressource erstellen:
		Resource resource = new Resource(0, "Autoreifen Maschine", 100);
		
		// Produkt erstellen:
		Product product = new Product(0, "Autoreifen", new ArrayList<Variant>());
		
		// Variante erstellen:
		product.getVariants().add(new Variant(0, new ArrayList<Operation>(), product));
		
		// Operation erstellen:
		product.getVariant(0).addOperation(
				new Operation(0, resource, 3600*24, "Reifen herstellen"));
		
		// Aufträge:
		ScheduleOrder order1 = new ScheduleOrder(1, new ArrayList<Product>(), "Auftrag: 1", new Date(tmpDate.getTime()), new Date(tmpDate.getTime()), 1);
			order1.getProducts().add(product);
		
		// Szenario erstellen:
		Map<Integer, Resource> resources = new HashMap<Integer, Resource>();
		resources.put(0, resource);
		
		Map<Integer, Product> products = new HashMap<Integer, Product>();
		products.put(0, product);
		
		List<ScheduleOrder> orders = new ArrayList<ScheduleOrder>();
		orders.add(order1);
		
		Scenario scenario1 = new Scenario(0, 0, "Elrond", resources, products, orders, new ArrayList<KeyFigure>());
		
		// Schedule nach EDD erstellen:
		Schedule scheduleEDD = new EDDAlgorithmOrderOriented().produceScheduleInternal(scenario1);
		
		// Testzeiten
		Date testDate1 = new Date(scheduleEDD.getResources().get(resource).get(0).getStart().getTime());
		Date testDate2 = new Date(tmpDate.getTime() +(3600*72*1000));
		Date testDate3 = new Date(scheduleEDD.getResources().get(resource).get(0).getFinish().getTime() +1000);
		Date testDate4 = new Date(tmpDate.getTime() -10000);
		Date testDate5 = new Date(tmpDate.getTime() +1000);
		Date testDate6 = new Date(scheduleEDD.getResources().get(resource).get(0).getFinish().getTime());

		assertTrue(scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate1));
		assertTrue(!scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate2));
		assertTrue(!scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate3));
		assertTrue(!scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate4));
		assertTrue(scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate5));
		assertTrue(scheduleEDD.getResources().get(resource).get(0).isDateInAllocation(testDate6));
	}
}
