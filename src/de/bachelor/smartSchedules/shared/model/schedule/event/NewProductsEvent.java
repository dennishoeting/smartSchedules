package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;

/**
 * Wenn es neue Producte gibt.
 * @author timo
 *
 */
public class NewProductsEvent implements ScheduleEvent {
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
	public NewProductsEvent() {
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
	public NewProductsEvent(int scenarioID, List<Product> products, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.products = products;
		this.throwTime = throwTime;
	}
	
	public NewProductsEvent(int scenarioID, Product product, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.products.add(product);
		this.throwTime = throwTime;
	}
	
	public List<Product> getNewProducts() {
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
		scenario.addProducts(products);
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
		
		// Größte ProductID, VariantID und OperationID finden und setzen:
		int tmpProductID = -1;
		int tmpVariantID = -1;
		int tmpOperationID = -1;
		// Produkt:
		for(Product p : products) {
			if(tmpProductID < p.getProductID()) {
				tmpProductID = p.getProductID();
			}
			// Variante:
			for(Variant v : p.getVariants()) {
				if(tmpVariantID < v.getVariantID()) {
					tmpVariantID = v.getVariantID();
				}
				// Operation:
				for(Operation op : v.getOperations()) {
					if(tmpOperationID < op.getOperationID()) {
						tmpOperationID = op.getOperationID();
					}
				}
			}
		}
		if(scenario.getNewProductIDCount() < tmpProductID) {
			scenario.setNewProductIDCount(tmpProductID);
		}
		if(scenario.getNewVariantIDCount() < tmpVariantID) {
			scenario.setNewVariantIDCount(tmpVariantID);
		}
		if(scenario.getNewOperationIDCount() < tmpOperationID) {
			scenario.setNewOperationIDCount(tmpOperationID);
		}
	}
	
	@Override
	public String getName() {
		return "New Product";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT;
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
		for(Product p : products) {
			scenario.getProducts().remove(p.getProductID());
		}
	}
}
