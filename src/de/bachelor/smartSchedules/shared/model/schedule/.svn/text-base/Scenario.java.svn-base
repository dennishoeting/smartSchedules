package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.ChangedOperations;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.Lateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MeanLateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MissedDeadLines;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * Enthält alle Daten eines Scenarios.
 * @author timo
 *
 */
public class Scenario implements Serializable{
	
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 7912076415776131947L;
	
	private int authorID, scenarioID, newOrderIDCount, newProductIDCount, newVariantIDCount, newOperationIDCount, newResourceIDCount, scheduleChangeDeadline, secondsBetweenAllocations;
	private String name;
	private Map<Integer, Resource> resources;
	private Map<Integer, Product> products;
	private List<ScheduleOrder> currentOrders, doneOrders;
	private List<Schedule> currentSchedules, oldSchedules;
	private List<KeyFigure> keyFigureList;
	private Schedule chosenSchedule;
	private static final String EVENT_SERVICE_DOMAIN_PREFIX = "scenario:";
	private static final int DEFAULT_SCHEDULE_CHANGE_DEADLINE = 60;
	//private static final int MINUTE = 60000;
	private static final int SECOND = 1000;
	
	/**
	 * Default
	 */
	public Scenario() {
		this.resources = new HashMap<Integer, Resource>();
		this.products = new HashMap<Integer, Product>();
		this.currentOrders = new ArrayList<ScheduleOrder>();
		this.doneOrders = new ArrayList<ScheduleOrder>();
		this.oldSchedules = new ArrayList<Schedule>();
		this.currentSchedules = new ArrayList<Schedule>();
		this.keyFigureList = new ArrayList<KeyFigure>();
		// Standart KeyFigureList erstellen:
		this.keyFigureList.add(new Lateness());
		this.keyFigureList.add(new MeanLateness());
		this.keyFigureList.add(new MissedDeadLines());
		this.keyFigureList.add(new ChangedOperations());
		this.newOrderIDCount = -1;
		this.newProductIDCount = -1;
		this.newVariantIDCount = -1;
		this.newOperationIDCount = -1;
		this.newResourceIDCount = -1;
		this.scheduleChangeDeadline = DEFAULT_SCHEDULE_CHANGE_DEADLINE;
		this.secondsBetweenAllocations = 1;
	}
	
	public Scenario(int authorID, String name) {
		this();
		this.authorID = authorID;
		this.name = name;
	}
	
	public Scenario(int scenarioID, int authorID, String name) {
		this(authorID, name);
		this.scenarioID = scenarioID;
	}
	
	public Scenario(int scenarioID, int authorID, String name, List<KeyFigure> keyFigureList) {
		this(scenarioID, authorID, name);
		this.keyFigureList = keyFigureList;
	}

	public Scenario(int scenarioID, int authorID, String name, Map<Integer, Resource> resources, Map<Integer, Product> products) {
		this(scenarioID, authorID, name);
		this.products = products;
		this.resources = resources;
	}

	public Scenario(int scenarioID, int authorID, String name, Map<Integer, Resource> resources, Map<Integer, Product> products, List<ScheduleOrder> orders, List<KeyFigure> keyFigureList) {
		this(scenarioID, authorID, name, resources, products);
		this.currentOrders = orders;
		this.keyFigureList = keyFigureList;
	}
	
	public int getAuthorID() {
		return authorID;
	}

	public String getName() {
		return name;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public  Map<Integer, Resource> getResources() {
		return resources;
	}

	public Map<Integer, Product> getProducts() {
		return products;
	}
	
	public Product getProduct(int productID) {
		return products.get(productID);
	}

	/**
	 * Gibt die Aktuellen Orders aus.
	 * @return
	 */
	public List<ScheduleOrder> getOrders() {
		
		// Falls es schon einen Schedule gibt, schauen was bereits erfüllt ist:
		if(this.getChosenSchedule() != null) {
			// Erfüllte Allocations markieren:
			this.markDoneAllocations();
					
			// Orders durchlaufen:
			for(ScheduleOrder so : new ArrayList<ScheduleOrder>(this.currentOrders)) {
				
				List<PlannedVariant> tmpPVList = new ArrayList<PlannedVariant>(this.chosenSchedule.getPlannedVariantsForOrder(this, so));
				int tmpProductCounter = 0;
				// Prüfen, ob alle Produkte erfüllt sind:
				for(Product p : so.getProducts(this)) {
					
					for(PlannedVariant pv : tmpPVList) {
						if(pv.getProduct().equals(p) && pv.isDone()) {	
							
							tmpPVList.remove(pv);
							tmpProductCounter++;
							break;
						}
					}
				}
				// Alle Produkte sind fertig geplant -> ScheduleOrder ist erfüllt:
				if(tmpProductCounter == so.getProducts(this).size()) {
					
					so.setDone(true);
					this.currentOrders.remove(so);
					this.doneOrders.add(so);
				}
			}
		}
		
		// Alle Orders ausgeben, die noch nicht erfüllt sind:
		return currentOrders;
	}
	
	public List<ScheduleOrder> getOrdersClone() {
		List<ScheduleOrder> tmpOrderListClone = new ArrayList<ScheduleOrder>();
		
		for(ScheduleOrder so : this.getOrders()) {
			tmpOrderListClone.add(so.clone());
		}
		
		return tmpOrderListClone;
	}
	
	public void addProduct(Product product) {
		this.products.put(product.getProductID(), product);
	}
	
	public void addProducts(List<Product> productList) {
		for(Product p : productList) {
			this.products.put(p.getProductID(), p);
		}
	}
	
	public void removeProduct(Product product) {
		this.products.remove(product.getProductID());
	}
	
	public void addResource(Resource resource) {
		this.resources.put(resource.getResourceID(), resource);
	}
	
	public void addResources(List<Resource> resourceList) {
		for(Resource r : resourceList) {
			this.resources.put(r.getResourceID(), r);
		}
	}
	
	public void addOrder(ScheduleOrder order) {
		this.currentOrders.add(order);
	}
	
	public void addOrders(List<ScheduleOrder> orderList) {
		this.currentOrders.addAll(orderList);
	}
	
	public void removeOrder(int orderID) {
		for(ScheduleOrder so : currentOrders) {
			if(so.getOrderID() == orderID) {
				currentOrders.remove(so);
				break;
			}
		}
	}

	public List<Schedule> getOldSchedules() {
		return this.oldSchedules;
	}
	
	public void addOldSchedule(Schedule schedule) {
		this.oldSchedules.add(schedule);
	}
	
	public List<Schedule> getCurrentSchedules() {
		return this.currentSchedules;
	}
	
	public void setCurrentSchedules(List<Schedule> schedules) {
		this.currentSchedules = schedules;
	}
	
	public void addCurrentSchedules(List<Schedule> schedules) {
		this.currentSchedules.addAll(schedules);
	}
	
	public ScheduleOrder getOrder(int id) {
		for(ScheduleOrder so : currentOrders) {
			if(so.getOrderID() == id) {
				return so;
			}
		}
		return null;
	}
	
	public ScheduleOrder getOrder(ScheduleOrder order) {
		return this.getOrder(order.getOrderID());
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Scenario
			&& ((Scenario)other).getScenarioID() == this.scenarioID) {
				return true;
		}
		
		return false;
	}

	public List<KeyFigure> getKeyFigureList() {
		return keyFigureList;
	}

	public void setKeyFigureList(List<KeyFigure> keyFigureList) {
		this.keyFigureList = keyFigureList;
	}
	
	public void setOrders(List<ScheduleOrder> orderList) {
		this.currentOrders = orderList;
	}
	
	/**
	 * Setzt den neuen ChosenSchedule.
	 * Der alte ChosenSchedule kommt zu den alten Schedules
	 */
	public void setChosenSchedule(Schedule schedule) {
		/*
		 * Initial-Fall abfangen
		 */
		if(chosenSchedule != null)
			this.addOldSchedule(chosenSchedule);
		this.chosenSchedule = schedule;
	}
	
	public Schedule getChosenSchedule() {
		return this.chosenSchedule;
	}
	
	/**
	 * Berechnet Varianten, die verplant werde können.
	 * @return
	 */
	public List<Variant> getPlanableVariants() {
		Set<Variant> tmpVariantSet = new HashSet<Variant>();
		
		// Orders durchlaufen:
		for(ScheduleOrder so : this.getOrders()) {
			for(Integer  productID : so.getProducts()) {
				Product p = this.getProduct(productID);
				
				// Produkt nicht mehr verfügbar.
				if(p == null) {
					break;
				}
				
				for(Variant v : p.getVariants()) {	
					int operationCounter = 0;
					for(Operation op : v.getOperations()) {
						for(Integer r : op.getResourceAlternatives()) {
							if(!this.getResources().get(r).isInBreakDown()) {
								operationCounter++;
								break;
							}
						}
					}
					// Variante ist möglich:
					if(operationCounter == v.getOperations().size()) {
						tmpVariantSet.add(v);
					}
				}
			}
		}
		
		return new ArrayList<Variant>(tmpVariantSet);
	}
	
	/**
	 * Berechnet alle Varianten, die nicht geplant werden können.
	 * @return
	 */
	public List<Variant> getNotPlanableVariants() {
		List<Variant> tmpVariantList = new ArrayList<Variant>();
		
		// Alle Varianten eintragen:
		for(Integer key : this.products.keySet()) {
			tmpVariantList.addAll(products.get(key).getVariants());
		}
		
		// Planbare Varianten löschen:
		for(Variant v : this.getPlanableVariants()) {
			tmpVariantList.remove(v);
		}
		
		return tmpVariantList;
	}
	
	
	/**
	 * Berechnet Produkte, die verplant werden können.
	 * @return
	 */
	public List<Product> getPlanableProducts() {
		Set<Product> tmpProductSet = new HashSet<Product>();		

		// Aus den möglichen Varianten, die möglichen Produkte erzeugen:
		for(Variant v : this.getPlanableVariants()) {
			tmpProductSet.add(v.getProduct());
		}
		
		return new ArrayList<Product>(tmpProductSet);
	}
	
	/**
	 * Berechnet alle Produkte, die nicht geplant werden können.
	 * @return
	 */
	public List<Product> getNotPlanableProducts() {
		List<Product> tmpProductList = new ArrayList<Product>();
		
		// Alle Produkte speichern:
		for(Integer key : products.keySet()) {
			tmpProductList.add(products.get(key));
		}
		
		// Alle möglichen Produkte löschen:
		for(Product p : this.getPlanableProducts()) {
			tmpProductList.remove(p);
		}
		
		return tmpProductList;
	}
	
	/**
	 * Gibt alle Schedules aus, die nicht komplett geplant werden können.
	 * @return
	 */
	public List<ScheduleOrder> getNotCompletelyPlanableOrders() {
		List<ScheduleOrder> tmpScheduleOrderList = new ArrayList<ScheduleOrder>();
		List<Product> notPlanableProducts = this.getNotPlanableProducts();
		
		for(ScheduleOrder so : this.currentOrders) {
			for(Product p : so.getProducts(this)) {
				if(notPlanableProducts.contains(p)) {
					tmpScheduleOrderList.add(so);
					break;
				}
			}
		}
		
		return tmpScheduleOrderList;
	}
	
	/**
	 * Zählt die Varianten, die geplant werden müssen.
	 * @return
	 */
	public int getToPlannedVariantsCount() {
		int tmpVariantCount = 0;
		List<Product> notPlanableProducts = this.getNotPlanableProducts();
		
		for(ScheduleOrder so : this.currentOrders) {
			for(Product p : so.getProducts(this)) {
				if(!notPlanableProducts.contains(p)) {
					tmpVariantCount++;
				}
			}
		}
		
		return tmpVariantCount;
	}
	
	/**
	 * Gibt alle Produkte für eine order aus, die wirklich verplant werden können:
	 * auch welche die nur möglich sind, weil einzelne Operationen bereits fertig sind!
	 * @param order
	 * @return
	 */
	public List<Product> getPlanableProductsForOrder(ScheduleOrder order) {
		List<Product> tmpProductList = new ArrayList<Product>();
		List<Product> tmpNotPlanableProductList = new ArrayList<Product>(this.getNotPlanableProducts());
		List<Integer> tmpProductsForOrderCopy = new ArrayList<Integer>(this.getOrder(order.getOrderID()).getProducts());
		List<PlannedVariant> tmpPlannedVariantsForOrder = new ArrayList<PlannedVariant>();
		if(this.getChosenSchedule() != null) {
			tmpPlannedVariantsForOrder = this.getChosenSchedule().getPlannedVariantsForOrder(this, order);
		}
		
		// Erstmal alle PlannedVariants durchlaufen und konsistente speichern:
		for(PlannedVariant pv : tmpPlannedVariantsForOrder) {
			
			// Produkt/Variante nicht mehr verfügbar
			if(this.getProductByVariantID(pv.getPlannedVariant().getVariantID()) == null
			|| this.getVariant(pv.getPlannedVariant().getVariantID()) == null) {				
				tmpProductsForOrderCopy.remove(pv.getProduct());
				continue;
			}
			
			if(tmpProductsForOrderCopy.contains(pv.getProduct()) 
			&& pv.isConsistent(this, this.getChosenSchedule())) {
				
				tmpProductList.add(pv.getProduct());
				// Löschen um zu wissen welche schon drin sind.
				tmpProductsForOrderCopy.remove(pv.getProduct());
				
			}
		}
		
		// Alle noch zuplanenden Produkte durchlaufen:
		for(Integer productID : tmpProductsForOrderCopy) {
			
			Product p = this.getProducts().get(productID);
			
			// Product nicht mehr verfügbar:
			if(p == null) {
				break;
			}
			
			if(!tmpNotPlanableProductList.contains(p)) {
				tmpProductList.add(p);
			}
		}
		
		return tmpProductList;
	}
	
	/**
	 * Gibt die passende GWTEventServerceDomain für das Scenario aus.
	 * @return
	 */
	public Domain getEventServiceDomain() {
		return DomainFactory.getDomain(EVENT_SERVICE_DOMAIN_PREFIX +this.getScenarioID());
	}

	public synchronized int getNewOrderIDCount() {
		return newOrderIDCount;
	}

	public synchronized void setNewOrderIDCount(int newOrderIDCount) {
		this.newOrderIDCount = newOrderIDCount;
	}
	
	public synchronized int getNewIncreasedOrderIDCount() {
		return ++this.newOrderIDCount;
	}

	public synchronized int getNewProductIDCount() {
		return newProductIDCount;
	}
	
	public synchronized int getNewIncreasedProductIDCount() {
		return ++this.newProductIDCount;
	}

	public synchronized void setNewProductIDCount(int newProductIDCount) {
		this.newProductIDCount = newProductIDCount;
	}

	public synchronized int getNewVariantIDCount() {
		return newVariantIDCount;
	}
	
	public synchronized int getNewIncreasedVariantIDCount() {
		return ++this.newVariantIDCount;
	}

	public synchronized void setNewVariantIDCount(int newVariantIDCount) {
		this.newVariantIDCount = newVariantIDCount;
	}

	public synchronized int getNewOperationIDCount() {
		return newOperationIDCount;
	}
	
	public synchronized int getNewIncreasedOperationIDCount() {
		return ++this.newOperationIDCount;
	}

	public synchronized void setNewOperationIDCount(int newOperationIDCount) {
		this.newOperationIDCount = newOperationIDCount;
	}

	public synchronized int getNewResourceIDCount() {
		return newResourceIDCount;
	}
	
	public synchronized int getNewIncreasedResourceIDCount() {
		return ++this.newResourceIDCount;
	}

	public synchronized void setNewResourceIDCount(int newResourceIDCount) {
		this.newResourceIDCount = newResourceIDCount;
	}

	/**
	 * Gibt die erledigten Orders aus.
	 * @return
	 */
	public List<ScheduleOrder> getDoneOrders() {
		return doneOrders;
	}

	public void setDoneOrders(List<ScheduleOrder> doneOrders) {
		this.doneOrders = doneOrders;
	}
	
	/**
	 * Markiert bereits erledigte Allocations.
	 */
	public void markDoneAllocations() {
		Date tmpCurrentDate = new Date();
		
		// PlannedVariants durchlaufen:
		for(PlannedVariant pv : this.getChosenSchedule().getPlannedVariants()) {
			
			// Prüfen, ob eine Allocation erfüllt ist:
			for(Allocation a : pv.getAllocationList()) {
				
				// Wenns fertig ist, markieren:
				if(a.getFinish(this).before(tmpCurrentDate) 
				|| a.getFinish(this).equals(tmpCurrentDate)) {
					a.setDone(true);
				}
				
			}
		}
	}
	
	/**
	 * Falls das Produkt bereits angefangen wurde UND noch komplett planbar ist, wird die Variante ausgegeben.
	 * @param product
	 * @param doneAllocations
	 * @return
	 */
	public Variant getAlreadyStartedVariant(Product product, List<Allocation> doneAllocations) {
		Map<Variant, List<Allocation>> variantList = new HashMap<Variant, List<Allocation>>();
		
		// doneAllocations durchlaufen und VariantList füllen:
		for(Allocation a : doneAllocations) {
			// Falls die Allocation zum gefordeten produkt gehört, speichern:
			if(this.getProductByOperationID(a.getOperation().getOperationID()).equals(product)) {
				Variant tmpVariant = this.getVariantByOperationID(a.getOperation().getOperationID());
				if(variantList.get(tmpVariant) == null) {
					variantList.put(tmpVariant, new ArrayList<Allocation>());
				}
				variantList.get(tmpVariant).add(a);
			}
		}
		
		// Schauen, ob eine der Varianten komplett planbar ist:
		for(Variant v : variantList.keySet()) {
			List<Operation> neededOperations = new ArrayList<Operation>(v.getOperations());
			for(Allocation doneAllocation : variantList.get(v)) {
				for(int i = 0; i < neededOperations.size(); i++) {
					if(neededOperations.get(i).equals(doneAllocation.getOperation())) {
						neededOperations.remove(i);
						break;
					}
				}
			}
			
			// Schauen, ob die restlichen Operations aus neededOperations verplant werden können:
			for(Operation op : new ArrayList<Operation>(neededOperations)) {
				if(op.isPlanable(this)) {
					neededOperations.remove(op);
				}
			}
			
			// Es konnten alle verplant werden:
			if(neededOperations.isEmpty()) {
				return v;
			}
		}
		
		return null;
	}

	/**
	 * Change Deadline für die Planung in Minuten.
	 * @return
	 */
	public int getScheduleChangeDeadline() {
		return scheduleChangeDeadline;
	}

	/**
	 * Change Deadline für die Planung in Minuten.
	 * @param scheduleChangeDeadline
	 */
	public void setScheduleChangeDeadline(int scheduleChangeDeadline) {
		this.scheduleChangeDeadline = scheduleChangeDeadline;
	}
	
	/**
	 * Gibt alle Allocations aus dem ChangeDeadline Zeitraum vom
	 * ChosenSchedule aus.
	 * @return
	 */
	public List<Allocation> getAllocationsInChangeDeadline(Date scheduleChangeDeadline) {
		return this.getChosenSchedule().getAllocationsInChangeDeadline(this, scheduleChangeDeadline);
	}
	
	/**
	 * Gibt alle Allocations in der ChangeDeadLine zurück, die möglich sind.
	 * @return
	 */
	public List<Allocation> getPossibleAllocationsInChangeDeadLine(Date scheduleChangeDeadline) {
		return this.getChosenSchedule().getPossibleAllocationsInChangeDeadline(this, scheduleChangeDeadline);
	}
	
	/**
	 * Gibt alle Allocations in der ChangeDeadLine zurück, die nicht möglich sind.
	 * @return
	 */
	public List<Allocation> getImpossibleAllocationsInChangeDeadline(Date scheduleChangeDeadline) {
		return this.getChosenSchedule().getImpossibleAllocationsInChangeDeadline(this, scheduleChangeDeadline);
	}
	
	/**
	 * Gibt die PlannedVariants aus, die von der ChangeDeadline betroffen sind.
	 * @return
	 */
	public List<PlannedVariant> getPossiblePlannedVariantsInChangeDeadline(Date scheduleChangeDeadline) {
		return this.getChosenSchedule().getPossiblePlannedVariantsInChangeDeadline(this, scheduleChangeDeadline);
	}
	
	public Date getFirstPossibleDateAfterScheduleChangeDeadline() {
		return new Date((new Date().getTime()) +(this.scheduleChangeDeadline * SECOND));
	}

	public List<ScheduleOrder> getCurrentOrders() {
		return currentOrders;
	}

	public void setCurrentOrders(List<ScheduleOrder> currentOrders) {
		this.currentOrders = currentOrders;
	}

	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResources(Map<Integer, Resource> resources) {
		this.resources = resources;
	}

	public void setProducts(Map<Integer, Product> products) {
		this.products = products;
	}

	public void setOldSchedules(List<Schedule> oldSchedules) {
		this.oldSchedules = oldSchedules;
	}
	
	public Variant getVariant(int variantID) {
		for(Integer productID : this.getProducts().keySet()) {
			Variant tmpVariant = this.getProduct(productID).getVariant(variantID);
			if(tmpVariant != null) {
				return tmpVariant;
			}
		}
		
		return null;
	}
	
	public List<Variant> getVariants() {
		List<Variant> tmpVariantList = new ArrayList<Variant>();
		
		for(Integer productID : this.getProducts().keySet()) {
			tmpVariantList.addAll(this.getProduct(productID).getVariants());
		}
		
		return tmpVariantList;
	}
	
	public Variant getVariantByOperationID(int operationID) {
		for(Integer productID : this.getProducts().keySet()) {
			for(Variant v : this.getProduct(productID).getVariants()) {
				if(v.getOperation(operationID) != null) {
					return v;
				}
			}
		}
		return null;
	}
	
	public Product getProductByVariantID(int variantID) {
		for(Integer productID : this.getProducts().keySet()) {
			Product p = this.getProduct(productID);
			if(p.getVariant(variantID) != null) {
				return p;
			}
		}
		
		return null;
	}
	
	public Product getProductByOperationID(int operationID) {
		Variant tmpVariant = this.getVariantByOperationID(operationID);
		
		for(Integer productID : this.getProducts().keySet()) {
			Product p = this.getProduct(productID);
			for(Variant v : p.getVariants()) {
				if(v.equals(tmpVariant)) {
					return p;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param operationID
	 * @return Operation oder null.
	 */
	public Operation getOperation(int operationID) {
		Variant v = this.getVariantByOperationID(operationID);
		if(v != null) {
			return v.getOperation(operationID);
		}
		return null;
	}
	
	/**
	 * Klont das Scenario.
	 */
	public Scenario clone() {
		return new Scenario(scenarioID, authorID, "" +name, new HashMap<Integer, Resource>(resources), new HashMap<Integer, Product>(products), this.getOrdersClone(), new ArrayList<KeyFigure>(this.getKeyFigureList()));
	}

	public int getSecondsBetweenAllocations() {
		return secondsBetweenAllocations;
	}

	public void setSecondsBetweenAllocations(int secondsBetweenAllocations) {
		this.secondsBetweenAllocations = secondsBetweenAllocations;
	}
}
