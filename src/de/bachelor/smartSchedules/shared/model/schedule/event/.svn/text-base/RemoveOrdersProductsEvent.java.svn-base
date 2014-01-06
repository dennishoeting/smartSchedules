package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;

/**
 * Falls Produkte einer Bestellung gelöscht werden sollen.
 * @author timo
 *
 */
public class RemoveOrdersProductsEvent implements ScheduleEvent{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2105636561371351914L;
	private Map<ScheduleOrder, List<Product>> removeMap;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public RemoveOrdersProductsEvent() {
		this.removeMap = new HashMap<ScheduleOrder, List<Product>>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param orderList mit den zu löschenden Produkten der Bestellungen.
	 */
	public RemoveOrdersProductsEvent(int scenarioID, Map<ScheduleOrder, List<Product>> removeMap, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.removeMap = removeMap;
		this.throwTime = throwTime;
	}
	
	public RemoveOrdersProductsEvent(int scenarioID, ScheduleOrder order, List<Product> products, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		removeMap.put(order, products);
		this.throwTime = throwTime;
	}

	@Override
	public Date getThrowTime() {
		return this.throwTime;
	}

	@Override
	public int getScenarioID() {
		return this.scenarioID;
	}

	@Override
	public void changeScenario(Scenario scenario) {
		for(ScheduleOrder so : this.removeMap.keySet()) {
			// Zu löschende Produkte:
			for(Product p : this.removeMap.get(so)) {
				// Aus der order löschen:
				scenario.getOrder(so.getOrderID()).removeProduct(p.getProductID());
			}
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() { 
		return "Remove Products from order";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT;
	}

	@Override
	public Schedule getChosenSchedule() {
		return this.chosenSchedule;
	}

	@Override
	public void setChosenSchedule(Schedule chosenSchedule) {
		this.chosenSchedule = chosenSchedule;
	}

	@Override
	public List<Schedule> getCurrentSchedules() {
		return this.currentSchedules;
	}

	@Override
	public void setCurrentSchedules(List<Schedule> currentScheduleList) {
		this.currentSchedules = currentScheduleList;
	}

	public Map<ScheduleOrder, List<Product>> getRemoveMap() {
		return removeMap;
	}

	public void setRemoveMap(Map<ScheduleOrder, List<Product>> removeMap) {
		this.removeMap = removeMap;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		for(ScheduleOrder so : removeMap.keySet()) {
			for(Product p : removeMap.get(so)) {
				scenario.getOrder(so.getOrderID()).addProduct(p);
			}
		}
	}
}
