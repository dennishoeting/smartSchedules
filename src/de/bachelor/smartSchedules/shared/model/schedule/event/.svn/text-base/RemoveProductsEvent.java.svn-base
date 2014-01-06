package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Falls Produkte gelöscht wurden.
 * @author timo
 *
 */
public class RemoveProductsEvent implements ScheduleEvent {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -8475341265311616660L;
	private List<Product> products;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public RemoveProductsEvent() {
		this.products = new ArrayList<Product>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	/**
	 * 
	 * @param scenario
	 * @param products
	 * @param throwTime
	 */
	public RemoveProductsEvent(int scenarioID, List<Product> products, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.products = products;
		this.throwTime = throwTime;
	}

	public RemoveProductsEvent(int scenarioID, Product product, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.products.add(product);
		this.throwTime = throwTime;
	}
	
	public List<Product> getRemovedProducts() {
		return this.products;
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
		for(Product p : products) {
			scenario.removeProduct(p);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Remove Product";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT;
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

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		scenario.addProducts(this.products);
	}
}
