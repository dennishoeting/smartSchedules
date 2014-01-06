package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stellt eine Order dar
 * @author timo
 *
 */
public class ScheduleOrder implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 6477206520604287480L;
	private int orderID, priority;
	private List<Integer> products;
	private String name;
	private Date earlistStartTime, earlistDueTime;
	private static final int MINUTE = 60000;
	private int scenarioID;
	private boolean isDone;
	
	/**
	 * Default
	 */
	public ScheduleOrder() {
		isDone = false;
	}
	
	/**
	 * 
	 * @param orderID
	 * @param scenario
	 * @param products
	 * @param name
	 * @param earlistStartDate
	 * @param earlistDueDate
	 * @param priority
	 */
	public ScheduleOrder(int orderID, int scenarioID, List<Product> products, String name, Date earlistStartDate, Date earlistDueDate, int priority) {
		this();
		this.orderID = orderID;
		this.scenarioID = scenarioID;
		
		// Nur ProductID speichern:
		List<Integer> tmpProductList = new ArrayList<Integer>();
		for(Product p : products) {
			tmpProductList.add(p.getProductID());
		}
		this.setProducts(tmpProductList);
		
		this.name = name;
		this.earlistStartTime = earlistStartDate;
		this.earlistDueTime = earlistDueDate;
		this.priority = priority;
	}
	
	public ScheduleOrder(int orderID, int scenarioID, String name, List<Integer> products, Date earlistStartDate, Date earlistDueDate, int priority) {
		this();
		this.orderID = orderID;
		this.scenarioID = scenarioID;
		this.products = products;
		this.name = name;
		this.earlistStartTime = earlistStartDate;
		this.earlistDueTime = earlistDueDate;
		this.priority = priority;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getOrderID() {
		return orderID;
	}
	public List<Product> getProducts(Scenario scenario) {
		List<Product> tmpProductList = new ArrayList<Product>();
		for(Integer productID : products) {
			tmpProductList.add(scenario.getProduct(productID));
		}
		
		return tmpProductList;
	}
	
	public List<Integer> getProducts() {
		return this.products;
	}
	
	/**
	 * Gibt die Produkte mit Anzahl in einer Map aus.
	 * @return
	 */
	public Map<Product, Integer> getProductsMap(Scenario scenario) {
		Map<Product, Integer> tmpProductsMap = new HashMap<Product, Integer>();
		
		// Produkte zählen:
		for(Product p : this.getProducts(scenario)) {
			if(tmpProductsMap.get(p) == null) {
				tmpProductsMap.put(p, 1);
			} else {
				tmpProductsMap.put(p, tmpProductsMap.get(p) +1);
			}
		}
		
		return tmpProductsMap;
	}
	
	public String getName() {
		return name;
	}
	public int getPriority() {
		return priority;
	}
	public Date getEarlistDueTime() {
		return earlistDueTime;
	}
	public void setEarlistDueTime(Date earlistDueTime) {
		this.earlistDueTime = earlistDueTime;
	}
	
	/**
	 * Priorität zwischen 1 und 10
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = (priority<1) ? 1 : ((priority>10) ? 10 : priority);
	}
	public Date getEarlistStartTime() {
		return earlistStartTime;
	}
	public void setEarlistStartTime(Date earlistStartTime) {
		this.earlistStartTime = earlistStartTime;
	}
	
	/**
	 * Gibt die kürzest mögliche ProcessingTime in Minuten aus.
	 * Z.B: wenn der Auftrag ohne weitere Ereignisse/Aufträge
	 * direkt verplant werden kann.
	 * @return
	 */
	public int getShortestPossibleProcessingTime(Scenario scenario) {
		int tmpAllocationID = 0;
		
		// Maschinen belegung:
		Map<Resource, List<Allocation>> tmpResourceMap = new HashMap<Resource, List<Allocation>>();
		for(Integer key : scenario.getResources().keySet()) {
			tmpResourceMap.put(scenario.getResources().get(key), new ArrayList<Allocation>());
		}
		
		// Initiale Start/Endzeit
		Date startTime = new Date(Long.MAX_VALUE);
		Date finishTime = new Date();
		
		// Produkte durchlaufen:
		for(Integer productID : products) {
			Product p = scenario.getProduct(productID);
			
			// Produkt nicht mehr verfügbar.
			if(p == null || p.getVariants().isEmpty()) {
				break;
			}
			
			Date tmpPossibleStartTime = new Date();
			
			//TODO nicht immer die erste Variante?
			for(Allocation variantAllocation : p.getVariants().get(0).generateAllocations(this)) {
				
				// Mögliche Startzeit suchen:
				tmpPossibleStartTime = new Date();
				
				if(!variantAllocation.getAllocationPredecessors().isEmpty()) {
					for(Allocation variantPreAllocation : variantAllocation.getAllocationPredecessors()) {
						if(variantPreAllocation.getFinish(scenario).before(tmpPossibleStartTime)) {
							tmpPossibleStartTime = new Date(variantPreAllocation.getFinish(scenario).getTime() +MINUTE);
						}
					}
				}
				
				
				//Allocation einplanen
				Resource chosenResource = null;
				Date chosenTime = null;
				Date firstPossibleDate = null;
				
				// Frühste Startzeit der Maschine auswählen:
				for(Integer possibleResource : variantAllocation.getOperation().getResourceAlternatives()) {
					//Finde Schlupfzeit
					firstPossibleDate = new Date(scenario.getResources().get(possibleResource).findFirstPossibleAllocation(scenario, tmpPossibleStartTime, variantAllocation.getOperation(), tmpResourceMap.get(scenario.getResources().get(possibleResource))).getTime());
					
					//Wenn erste Ressource oder bisher früheste
					if(chosenTime == null || firstPossibleDate.before(chosenTime)) {
						chosenTime = firstPossibleDate;
						chosenResource = scenario.getResources().get(possibleResource);
					} 
				}
				// Operation/Allocation einplanen:
				chosenResource.addOperation(variantAllocation, 
						chosenTime, 
						tmpResourceMap.
						get
						(chosenResource));
				variantAllocation.setAllocationID(tmpAllocationID++);
			}
		}
		
		// Zeit für die Order ausgeben:
		for(Resource r : tmpResourceMap.keySet()) {
			for(Allocation a : tmpResourceMap.get(r)) {
				if(a.getFinish(scenario).after(finishTime)) {
					finishTime = a.getFinish(scenario);
				}
				if(a.getStart().before(startTime)) {
					startTime = a.getStart();
				}
			}
		}
		
		return (int)(finishTime.getTime() - startTime.getTime());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleOrder other = (ScheduleOrder) obj;
		if (orderID != other.orderID)
			return false;
		return true;
	}

	public void addProduct(Product product) {
		this.products.add(product.getProductID());
	}
	
	public Product getProduct(Scenario scenario, int productID) {
		return scenario.getProduct(productID);
	}
	
	public int getProductCountByID(int productID) {
		int tmpCount = 0;
		
		for(Integer p : this.getProducts()) {
			if(productID == p) {
				tmpCount++;
			}
		}
		
		return tmpCount;
	}
	
	public void removeProduct(Product product) {
		this.removeProduct(product.getProductID());
	}
	
	public void removeProduct(int productID) {
		for(int i = 0; i < products.size(); i++) {
			if(products.get(i) == productID) {
				products.remove(i);
				break;
			}
		}
	}
	
	public int getScenarioID() {
		return this.scenarioID;
	}

	/**
	 * Gibt an, ob die ScheduleOrder bereits erfüllt ist.
	 * @return
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * Setzt, ob die ScheduleOrder bereits erfüllt ist.
	 * @param isDone
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	
	public ScheduleOrder clone() {		
		ScheduleOrder tmpClone = new ScheduleOrder(orderID, scenarioID, name, new ArrayList<Integer>(products), new Date(earlistStartTime.getTime()), new Date(earlistDueTime.getTime()), priority);
		
		return tmpClone;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public void setProducts(List<Integer> products) {
		this.products = products;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}
}
