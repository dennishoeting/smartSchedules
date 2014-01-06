package de.bachelor.smartSchedules.server.model.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.bachelor.smartSchedules.server.model.xmlparser.exception.AnythingNotDefinedException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.NameInUseException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.OperationNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.OrderNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.ProductNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.ResourceNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.UnknownEventException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.VariantNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.WrongAvailabilityException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.WrongMaintenancePeriodException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.WrongRemoveProductsFromOrderException;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOperationResourcesEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderDueTimeEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderPriorityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeResourceAvailabilityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MaintenancePeriodsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * XML-Parser für die Events.
 * @author timo
 *
 */
public class EventXMLParser {
	private final static String CHANGE_ORDER_DUE_TIME_EVENT = "changeorderduetimeevent";
	private final static String CHANGE_RESOURCE_AVAILABILITY = "chanceresourceavailability";
	private final static String CHANGE_ORDER_PRIORITY = "changeorderpriority";
	private final static String RESOURCE_BREAK_DOWN = "resourcebreakdown";
	private final static String RESOURCE_REPAIRED = "resourcerepaired";
	private final static String MAINTENANCE_PERIOD = "maintenanceperiod";
	private final static String NEW_ORDERS_EVENT = "newordersevent";
	private final static String NEW_PRODUCTS_EVENT = "newproductsevent";
	private final static String NEW_VARIANTS_EVENT = "newvariantsevent";
	private final static String NEW_RESOURCE_EVENT = "newresourceevent";
	private final static String REMOVE_ORDERS_EVENT = "removeordersevent";
	private final static String REMOVE_ORDERS_PRODUCTS_EVENT = "removeordersproductsevent";
	private final static String REMOVE_PRODUCTS_EVENT = "removeproductsevent";
	private final static String REMOVE_VARIANTS_EVENT = "removevariantsevent";
	private final static String CHANGE_OPERATION_RESOURCES = "changeoperationresources";
	
	/**
	 * Liest die XML ein.
	 * @param xmlFile
	 * @return
	 * @throws ProductNotExistsException 
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws OperationNotExistsException 
	 * @throws UnknownEventException 
	 * @throws NameInUseException 
	 * @throws WrongAvailabilityException 
	 * @throws WrongRemoveProductsFromOrderException 
	 * @throws VariantNotExistsException 
	 * @throws WrongMaintenancePeriodException 
	 * @throws AnythingNotDefinedException 
	 */
	public static List<ScheduleEvent> parseXML(Scenario scenario, File xmlFile) throws OrderNotExistsException, ResourceNotExistsException, ProductNotExistsException, JDOMException, IOException, OperationNotExistsException, UnknownEventException, NameInUseException, WrongAvailabilityException, WrongRemoveProductsFromOrderException, VariantNotExistsException, WrongMaintenancePeriodException, AnythingNotDefinedException{

		List<ScheduleEvent> tmpEventList = new ArrayList<ScheduleEvent>();

		/*
		 * Alles was neu durch Events aus der XML erzeugt wurde.
		 * Jeweils ID und ThrowTime.
		 */
		Map<Integer, Date> tmpNewProductsMap = new HashMap<Integer, Date>();
		Map<Integer, Date> tmpProductsRemoveMap = new HashMap<Integer, Date>();
		List<Product> tmpNewProductsList = new ArrayList<Product>();
		
		Map<Integer, Date> tmpNewScheduleOrderMap = new HashMap<Integer, Date>();
		Map<Integer, Date> tmpScheduleOrderRemoveMap = new HashMap<Integer, Date>();
		List<ScheduleOrder> tmpNewScheduleOrderList = new ArrayList<ScheduleOrder>();
		
		Map<Integer, Date> tmpNewVariantsMap = new HashMap<Integer, Date>();
		List<Variant> tmpNewVariantsList = new ArrayList<Variant>();
		
		Map<Integer, Date> tmpNewResourcesMap = new HashMap<Integer, Date>();
		List<Resource> tmpNewResourcesList = new ArrayList<Resource>();

		Document document = new SAXBuilder().build(xmlFile);
		Element events = document.getRootElement();
		if(events == null) {
			return tmpEventList;
		}

		// Events durchlaufen:
		for(Object o : events.getChildren()) {
			Element	event = (Element)o;
			String eventName = event.getAttribute("name").getValue().toLowerCase().trim();

			if(eventName.equals(CHANGE_ORDER_DUE_TIME_EVENT)) {
				
				if(event.getChild("ScheduleOrderID") == null) {
					throw new AnythingNotDefinedException("orderID", new ChangeOrderDueTimeEvent().getName());
				}
				int tmpOrderID = Integer.valueOf(event.getChild("ScheduleOrderID").getValue());
				ScheduleOrder tmpOrder = null;
				
				if(scenario.getOrder(tmpOrderID) != null) {
					tmpOrder = scenario.getOrder(tmpOrderID).clone();
				} else {
					for(ScheduleOrder so : tmpNewScheduleOrderList) {
						if(so.getOrderID() == tmpOrderID) {
							tmpOrder = so.clone();
							break;
						}
					}
				}
				
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));

				if(tmpOrder != null) {
					if(event.getChild("NewOrderDueTime") == null) {
						throw new AnythingNotDefinedException("NewOrderDueTime", new ChangeOrderDueTimeEvent().getName());
					}
					tmpOrder.setEarlistDueTime(EventXMLParser.getDate(event.getChild("NewOrderDueTime")));
					tmpEventList.add(new ChangeOrderDueTimeEvent(scenario.getScenarioID(), tmpOrder, throwTime));
					continue;
				} else {
					throw new OrderNotExistsException(Integer.valueOf(event.getChild("ScheduleOrderID").getValue()));
				}
			}
			
			if(eventName.equals(CHANGE_ORDER_PRIORITY)) {
				
				if(event.getChild("ScheduleOrderID").getValue() == null) {
					throw new AnythingNotDefinedException("orderID", new ChangeOrderPriorityEvent().getName());
				}
				
				int tmpOrderID = Integer.valueOf(event.getChild("ScheduleOrderID").getValue());
				
				if(event.getChild("ThrowTime") == null) {
					throw new AnythingNotDefinedException("ThrowTime", new ChangeOrderPriorityEvent().getName());
				}
				
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));
				ScheduleOrder tmpOrder = null;
				if(scenario.getOrder(tmpOrderID) != null) {
					tmpOrder = scenario.getOrder(tmpOrderID);
				} else {
					for(ScheduleOrder so : tmpNewScheduleOrderList) {
						if(so.getOrderID() == tmpOrderID) {
							tmpOrder = so.clone();
						}
					}
				}

				if(tmpOrder != null) {
					if(event.getChild("NewPriority") == null) {
						throw new AnythingNotDefinedException("NewPriority", new ChangeOrderPriorityEvent().getName());
					}
					tmpOrder.setPriority(Integer.valueOf(event.getChild("NewPriority").getValue()));
					tmpEventList.add(new ChangeOrderPriorityEvent(scenario.getScenarioID(), tmpOrder, throwTime));
					continue;
				} else {
					throw new OrderNotExistsException(Integer.valueOf(event.getChild("ScheduleOrderID").getValue()));
				}
			}

			if(eventName.equals(CHANGE_RESOURCE_AVAILABILITY)) {

				if(event.getChild("ResourceID") == null) {
					throw new AnythingNotDefinedException("ResourceID", new ChangeResourceAvailabilityEvent().getName());
				}
				int tmpResourceID = Integer.valueOf(event.getChild("ResourceID").getValue());
				
				Resource tmpResource = null;
				
				if(scenario.getResources().get(tmpResourceID) != null) {
					tmpResource = scenario.getResources().get(tmpResourceID).clone();
				} else {
					for(Resource r : tmpNewResourcesList) {
						if(r.getResourceID() == tmpResourceID) {
							tmpResource = r;
						}
					}
				}
				
				if(event.getChild("ThrowTime") == null) {
					throw new AnythingNotDefinedException("ThrowTime", new ChangeResourceAvailabilityEvent().getName());
				}
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));

				if(tmpResource != null) {
					if(event.getChild("NewAvailability") == null) {
						throw new AnythingNotDefinedException("NewAvailability", new ChangeResourceAvailabilityEvent().getName());
					}
					tmpResource.setAvailability(Integer.valueOf(event.getChild("NewAvailability").getValue()));
					tmpEventList.add(new ChangeResourceAvailabilityEvent(scenario.getScenarioID(), tmpResource, throwTime));
					continue;
				} else {
					throw new ResourceNotExistsException(Integer.valueOf(event.getChild("ResourceID").getValue()));
				}
			}

			if(eventName.equals(RESOURCE_BREAK_DOWN)) {

				if(event.getChild("ResourceID") == null) {
					throw new AnythingNotDefinedException("ResourceID", new MachineBreakDownEvent().getName());
				}
				
				int tmpResourceID = Integer.valueOf(event.getChild("ResourceID").getValue());
				
				Resource tmpResource = null;
				if(scenario.getResources().get(tmpResourceID) != null) {
					tmpResource = scenario.getResources().get(tmpResourceID).clone();
				} else {
					for(Resource r : tmpNewResourcesList) {
						if(r.getResourceID() == tmpResourceID) {
							tmpResource = r;
						}
					}
				}
				
				if(event.getChild("ThrowTime") == null) {
					throw new AnythingNotDefinedException("ThrowTime", new MachineBreakDownEvent().getName());
				}
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));

				if(tmpResource != null) {
					tmpResource.setBreakDown(true);
					tmpEventList.add(new MachineBreakDownEvent(scenario.getScenarioID(), tmpResource, throwTime));
					continue;
				} else {
					throw new ResourceNotExistsException(Integer.valueOf(event.getChild("ResourceID").getValue()));
				}
			}

			if(eventName.equals(RESOURCE_REPAIRED)) {

				if(event.getChild("ResourceID") == null) {
					throw new AnythingNotDefinedException("ResourceIDResourceID", new MachineRepairedEvent().getName());
				}
				
				int tmpResourceID = Integer.valueOf(event.getChild("ResourceID").getValue());
				
				Resource tmpResource = null;
				
				if(scenario.getResources().get(tmpResourceID) != null) {
					tmpResource = scenario.getResources().get(tmpResourceID).clone();
				} else {
					for(Resource r : tmpNewResourcesList) {
						if(r.getResourceID() == tmpResourceID) {
							tmpResource = r;
						}
					}
				}
				
				if(event.getChild("ThrowTime") == null) {
					throw new AnythingNotDefinedException("ThrowTime", new MachineRepairedEvent().getName());
				}
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));

				if(tmpResource != null) {
					tmpResource.setBreakDown(false);
					tmpEventList.add(new MachineBreakDownEvent(scenario.getScenarioID(), tmpResource, throwTime));
					continue;
				} else {
					throw new ResourceNotExistsException(Integer.valueOf(event.getChild("ResourceID").getValue()));
				}
			}

			if(eventName.equals(MAINTENANCE_PERIOD)) {
				
				if(event.getChild("ResourceID") == null) {
					throw new AnythingNotDefinedException("ResourceID", new MaintenancePeriodsEvent().getName());
				}
				
				int tmpResourceID = Integer.valueOf(event.getChild("ResourceID").getValue());
				
				Resource tmpResource = null;
				
				if(scenario.getResources().get(tmpResourceID) != null) {
					tmpResource = scenario.getResources().get(tmpResourceID).clone();
				} else {
					for(Resource r : tmpNewResourcesList) {
						if(r.getResourceID() == tmpResourceID) {
							tmpResource = r;
							break;
						}
					}
				}
				
				if(event.getChild("ThrowTime") == null) {
					throw new AnythingNotDefinedException("ResourceID", new MaintenancePeriodsEvent().getName()); 
				}
				Date throwTime = EventXMLParser.getDate(event.getChild("ThrowTime"));


				if(tmpResource != null) {
					Date startDate = EventXMLParser.getDate(event.getChild("StartTime"));
					Date dueDate = EventXMLParser.getDate(event.getChild("DueTime"));
					
					if(dueDate.before(startDate) || dueDate.equals(startDate)) {
						throw new WrongMaintenancePeriodException(startDate, dueDate);
					}
					
					ResourceRestriction tmpRR = new ResourceRestriction(scenario.getScenarioID(), startDate, dueDate, tmpResourceID);
					tmpEventList.add(new MaintenancePeriodsEvent(scenario.getScenarioID(), tmpRR, throwTime));
					continue;
				} else {
					throw new ResourceNotExistsException(Integer.valueOf(event.getChild("ResourceID").getValue()));
				}
			}

			if(eventName.equals(NEW_ORDERS_EVENT)) {

				List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();

				// Alle Orders durchlaufen:
				Element tmpOrdersElement = event.getChild("Orders");
				if(tmpOrdersElement == null) {
					throw new AnythingNotDefinedException("Orders", new NewOrdersEvent().getName());
				}
				for(Object order : tmpOrdersElement.getChildren("Order")) {
					Element orderElement = (Element)order;

					// Product liste erstellen:
					List<Integer> tmpProductList = new ArrayList<Integer>();

					// Products der Orders durchlaufen:
					Element tmpProducts = orderElement.getChild("Products");
					if(tmpProducts == null) {
						throw new AnythingNotDefinedException("Products", new NewOrdersEvent().getName());
					}
					for(Object product : tmpProducts.getChildren("Product")) {
						Element productElement = (Element)product;

						// Prüfen, ob es das Product gibt:
						int tmpProductID = Integer.valueOf(productElement.getAttributeValue("id"));
						if( ((tmpProductsRemoveMap.get(tmpProductID) != null) && tmpProductsRemoveMap.get(tmpProductID).before(EventXMLParser.getDate(event.getChild("ThrowTime"))))
						|| (scenario.getProduct(tmpProductID) == null
						&& (tmpNewProductsMap.get(tmpProductID) == null
						|| tmpNewProductsMap.get(tmpProductID).after(EventXMLParser.getDate(event.getChild("ThrowTime")))))) {
							throw new ProductNotExistsException(tmpProductID);
						}

						// Ansonsten in die Liste:
						for(int i = 0; i < Integer.valueOf(productElement.getValue()); i++) {
							tmpProductList.add(tmpProductID);
						}
					}

					// Gibt es den Namen schon?
					Element tmpOrderNameElement = orderElement.getChild("Name");
					if(tmpOrderNameElement == null) {
						throw new AnythingNotDefinedException("Name", new NewOrdersEvent().getName());
					}
					String tmpOrderName = orderElement.getChild("Name").getValue();
					for(ScheduleOrder so : scenario.getOrders()) {
						if(so.getName().equals(tmpOrderName)) {
							throw new NameInUseException(tmpOrderName);
						}
					}
					for(ScheduleOrder so : scenario.getDoneOrders()) {
						if(so.getName().equals(tmpOrderName)) {
							throw new NameInUseException(tmpOrderName);
						}
					}
					for(ScheduleOrder so : tmpOrderList) {
						if(so.getName().equals(tmpOrderName)) {
							throw new NameInUseException(tmpOrderName);
						}
					}
					
					// Order erstellen:
					ScheduleOrder tmpScheduleOrder = new ScheduleOrder(scenario.getNewIncreasedOrderIDCount(), 
							scenario.getScenarioID(), tmpOrderName, tmpProductList, new Date(), EventXMLParser.getDate(orderElement.getChild("EarlistDueTime")), 
							Integer.valueOf(orderElement.getChildText("Priority")));
					tmpOrderList.add(tmpScheduleOrder);
					tmpNewScheduleOrderList.add(tmpScheduleOrder);
					tmpNewScheduleOrderMap.put(tmpScheduleOrder.getOrderID(), EventXMLParser.getDate(event.getChild("ThrowTime")));
				}

				// Event erzeugen:
				tmpEventList.add(new NewOrdersEvent(scenario.getScenarioID(), tmpOrderList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(NEW_PRODUCTS_EVENT)) {
				List<Product> tmpProductList = new ArrayList<Product>();
				
				// Products durchlaufen:
				Element tmpProductsElement = event.getChild("Products");
				if(tmpProductsElement == null) {
					throw new AnythingNotDefinedException("Products", new NewProductsEvent().getName());
				}
				for(Object product : event.getChildren("Product")) {
					Element productElement = (Element)product;
					List<Variant> tmpVariantList = new ArrayList<Variant>();
					
					if(productElement.getChild("Name") == null) {
						throw new AnythingNotDefinedException("ProductName", new NewProductsEvent().getName());
					}
					
					// Prüfen, ob der Productname schon existiert:
					Element productNameElement = productElement.getChild("Name");
					if(productNameElement == null) {
						throw new AnythingNotDefinedException("ProductName", new Product().getName());
					}
					String tmpProductName = productNameElement.getValue();
					for(Integer key : scenario.getProducts().keySet()) {
						if(scenario.getProducts().get(key).getName().equals(tmpProductName)) {
							throw new NameInUseException();
						}
					}
					
					Product tmpProduct = new Product(scenario.getNewIncreasedProductIDCount(), tmpProductName, tmpVariantList);
					
					// Varianten durchlaufen:
					Element tmpVariantsElement = productElement.getChild("Variants");
					if(tmpVariantsElement == null) {
						throw new AnythingNotDefinedException("Variants", tmpProduct.getName());
					}
					for(Object variant : tmpVariantsElement.getChildren("Variant")) {
						Element variantElement = (Element)variant;
						List<Operation> tmpOperationList = new ArrayList<Operation>();
						Variant tmpVariant = new Variant(scenario.getNewIncreasedVariantIDCount(), tmpOperationList, tmpProduct);
						
						// Operationen durchlaufen:
						Element tmpOperationsElement = variantElement.getChild("Operations");
						if(tmpOperationsElement == null) {
							throw new AnythingNotDefinedException("Operations", tmpProduct.getName());
						}
						for(Object operation : tmpOperationsElement.getChildren("Operation")) {
							Element operationElement = (Element)operation;
							List<Integer> tmpResourceAlternatives = new ArrayList<Integer>();
							List<Operation> tmpPredecessorsList = new ArrayList<Operation>();
							
							
							// Resources durchlaufen:
							Element tmpResourceElement = operationElement.getChild("Resources");
							if(tmpResourceElement == null) {
								throw new AnythingNotDefinedException("Resources", tmpProduct.getName());
							}
							for(Object resourceID : tmpResourceElement.getChildren("ResourceID")) {
								
								Element resourceIDElement = (Element)resourceID;
								
								if(resourceIDElement == null) {
									throw new AnythingNotDefinedException("ResourceID", new NewProductsEvent().getName());
								}
								
								int tmpResourceID = Integer.valueOf(resourceIDElement.getValue());
								
								// Prüfen, ob es die Resource gibt:
								if(scenario.getResources().get(tmpResourceID) == null
								&& (tmpNewResourcesMap.get(tmpResourceID) == null
									|| tmpNewResourcesMap.get(tmpResourceID).after(EventXMLParser.getDate(event.getChild("ThrowTime"))))) {
									throw new ResourceNotExistsException(tmpResourceID);
								}
								
								// Resource hinzufügen:
								tmpResourceAlternatives.add(tmpResourceID);
							}
							
							// Predecessors beachten:
							Object tmpPredecessors = operationElement.getChild("Predecessors");
							if(tmpPredecessors == null) {
								throw new AnythingNotDefinedException("Predecessors" , new NewProductsEvent().getName());
							}
							for(Object predecessor : ((Element)tmpPredecessors).getChildren("Name")) {
								Element predecessorElement = (Element)predecessor;
								Operation tmpPreOperation = null;
								
								// Predecessor suchen:
								for(Operation preOp : tmpOperationList) {
									if(preOp.getName().equals(predecessorElement.getValue())) {
										tmpPreOperation = preOp;
										break;
									}
								}
								
								// Prüfen:
								if(tmpPreOperation == null) {
									throw new OperationNotExistsException(predecessorElement.getValue());
								}
								
								// hinzufügen:
								tmpPredecessorsList.add(tmpPreOperation);
							}
							
							// Prüfen, ob der Name schon besetzt ist:
							Element tmpOperationNameElement = operationElement.getChild("Name");
							if(tmpOperationNameElement == null) {
								throw new AnythingNotDefinedException("OperationName", new NewProductsEvent().getName());
							}
							String tmpOperationName = tmpOperationsElement.getValue();
							for(Operation op : tmpOperationList) {
								if(op.getName().equals(tmpOperationName)) {
									throw new NameInUseException(tmpOperationName);
								}
							}
							
							// Operation hinzufügen:
							tmpOperationList.add(new Operation(scenario.getScenarioID(), 
									scenario.getNewIncreasedOperationIDCount(), tmpResourceAlternatives, Integer.valueOf(operationElement.getChild("Duration").getValue()), 
									tmpVariant.getVariantID(), tmpOperationName, tmpPredecessorsList));
						}
						
						// Variant erstellen:
						tmpVariantList.add(tmpVariant);
					}
					
					// Product hinzufügen:
					tmpProductList.add(tmpProduct);
					tmpNewProductsMap.put(tmpProduct.getProductID(), EventXMLParser.getDate(event.getChild("ThrowTime")));
					tmpNewProductsList.add(tmpProduct);
				}
				
				// Event erzeugen:
				tmpEventList.add(new NewProductsEvent(scenario.getScenarioID(), tmpProductList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(NEW_VARIANTS_EVENT)) {
				List<Variant> tmpVariantList = new ArrayList<Variant>();
				
				// Variants durchlaufen:
				Element tmpVariantsElement = event.getChild("Variants");
				if(tmpVariantsElement == null) {
					throw new AnythingNotDefinedException("Variants", new NewVariantsEvent().getName());
				}
				for(Object tmpVariantObject : tmpVariantsElement.getChildren("Variant")) {
					Element tmpVariantElement = (Element)tmpVariantObject;
					
					// Schauen, ob es das Product gibt:
					if(tmpVariantElement.getChild("ProductID") == null) {
						throw new AnythingNotDefinedException("ProductID", new NewVariantsEvent().getName());
					}
					int tmpProductID = Integer.valueOf(tmpVariantElement.getChild("ProductID").getValue());
					Product tmpProduct = scenario.getProduct(tmpProductID);
					
					if(tmpProduct == null) {
						for(Product p : tmpNewProductsList) {
							if(p.getProductID() == tmpProductID) {
								tmpProduct = p;
							}
						}
						// Product existiert nicht:
						if(tmpProduct == null) {
							throw new ProductNotExistsException(tmpProductID);
						}
					}
					
					// Operation List erstellen:
					List<Operation> tmpOperationList = new ArrayList<Operation>();
					
					// Variante erstellen:
					Variant tmpVariant = new Variant(scenario.getNewIncreasedVariantIDCount(), tmpOperationList, tmpProduct);
					tmpNewVariantsMap.put(tmpVariant.getVariantID(), EventXMLParser.getDate(event.getChild("ThrowTime")));
					tmpNewVariantsList.add(tmpVariant);
					
					// Operationen durchlaufen:
					Element tmpOperationsElement = tmpVariantElement.getChild("Operations");
					if(tmpOperationsElement == null) {
						throw new AnythingNotDefinedException("Operations", new NewVariantsEvent().getName());
					}
					for(Object operation : tmpOperationsElement.getChildren("Operation")) {
						Element operationElement = (Element)operation;
						List<Integer> tmpResourceAlternatives = new ArrayList<Integer>();
						List<Operation> tmpPredecessorsList = new ArrayList<Operation>();
						
						
						// Resources durchlaufen:
						Element tmpResourcesElement = operationElement.getChild("Resources");
						if(tmpResourcesElement == null) {
							throw new AnythingNotDefinedException("Resources", new NewVariantsEvent().getName());
						}
						for(Object resourceID : tmpResourcesElement.getChildren("ResourceID")) {
							Element resourceIDElement = (Element)resourceID;
							
							int tmpResourceID = Integer.valueOf(resourceIDElement.getValue());
							
							// Prüfen, ob es die Resource gibt:
							if(scenario.getResources().get(tmpResourceID) == null
							&& (tmpNewResourcesMap.get(tmpResourceID) == null
								|| tmpNewResourcesMap.get(tmpResourceID).after(EventXMLParser.getDate(event.getChild("ThrowTime"))))) {
								throw new ResourceNotExistsException(tmpResourceID);
							}
							
							// Resource hinzufügen:
							tmpResourceAlternatives.add(tmpResourceID);
						}
						
						// Predecessors beachten:
						Object tmpPredecessors = operationElement.getChild("Predecessors");
						for(Object predecessor : ((Element)tmpPredecessors).getChildren("Name")) {
							Element predecessorElement = (Element)predecessor;
							Operation tmpPreOperation = null;
							
							// Predecessor suchen:
							for(Operation preOp : tmpOperationList) {
								if(preOp.getName().equals(predecessorElement.getValue())) {
									tmpPreOperation = preOp;
									break;
								}
							}
							
							// Prüfen:
							if(tmpPreOperation == null) {
								throw new OperationNotExistsException(predecessorElement.getValue());
							}
							
							// hinzufügen:
							tmpPredecessorsList.add(tmpPreOperation);
						}
						
						// Prüfen, ob es die Operation schon gibt:
						Element tmpOperationNameElement = operationElement.getChild("Name");
						if(tmpOperationNameElement == null) {
							throw new AnythingNotDefinedException("OperationName", new NewVariantsEvent().getName());
						}
						String tmpOperationName = tmpOperationNameElement.getValue();
						for(Operation op : tmpOperationList) {
							if(op.getName().equals(tmpOperationName)) {
								throw new NameInUseException(tmpOperationName);
							}
						}
						
						// Operation hinzufügen:
						tmpOperationList.add(new Operation(scenario.getScenarioID(), 
								scenario.getNewIncreasedOperationIDCount(), tmpResourceAlternatives, Integer.valueOf(operationElement.getChild("Duration").getValue()), 
								tmpVariant.getVariantID(), operationElement.getChild("Name").getValue(), tmpPredecessorsList));
						}
				}
				
				// Event erzeugen:
				tmpEventList.add(new NewVariantsEvent(scenario.getScenarioID(), tmpVariantList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(NEW_RESOURCE_EVENT)) {
				List<Resource> tmpResourceList = new ArrayList<Resource>();
				
				// Resources durchlaufen:
				for(Object tmpResourceObject : event.getChildren("Resource")) {
					
					Element tmpResourceElement = (Element)tmpResourceObject;
					String tmpResourceName = tmpResourceElement.getChild("Name").getValue();
					int tmpResourceAvailability = Integer.valueOf(tmpResourceElement.getChild("Availability").getValue());
				
					// Resource Name frei?
					for(Integer tmpResourceID : scenario.getResources().keySet()) {
						if(scenario.getResources().get(tmpResourceID).getName().equals(tmpResourceName)) {
							throw new NameInUseException(tmpResourceName);
						}
					}
					for(Resource r : tmpNewResourcesList) {
						if(r.getName().equals(tmpResourceName)) {
							throw new NameInUseException(tmpResourceName);
						}
					}
					
					// Resource Availability okay?
					if(tmpResourceAvailability < 1 || tmpResourceAvailability > 100) {
						throw new WrongAvailabilityException(tmpResourceAvailability);
					}
					
					// Resource erzeugen:
					Resource tmpResource = new Resource(scenario.getScenarioID(), 
							scenario.getNewIncreasedResourceIDCount(), tmpResourceName, tmpResourceAvailability);
					tmpResourceList.add(tmpResource);
					tmpNewResourcesList.add(tmpResource);
					tmpNewResourcesMap.put(tmpResource.getResourceID(), EventXMLParser.getDate(event.getChild("ThrowTime")));
				}
				
				// Event erzeugen:
				tmpEventList.add(new NewResourceEvent(scenario.getScenarioID(), tmpResourceList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(REMOVE_ORDERS_EVENT)) {
				List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
				
				// Orders durchlaufen:
				Element tmpOrdersElement = event.getChild("Orders");
				if(tmpOrdersElement == null) {
					throw new AnythingNotDefinedException("Orders", new RemoveOrdersEvent().getName());
				}
				for(Object tmpOrderObject : event.getChildren("OrderID")) {
					int tmpOrderID = Integer.valueOf(((Element)tmpOrderObject).getValue());
					
					ScheduleOrder tmpOrder = scenario.getOrder(tmpOrderID);
					
					if(tmpOrder == null) {
						// Order ist in der List:
						for(ScheduleOrder so : tmpNewScheduleOrderList) {
							if(so.getOrderID() == tmpOrderID) {
								tmpOrder = so;
							}
						}
						if(tmpOrder == null) {
							throw new OrderNotExistsException(tmpOrderID);
						}
					}	

					// Order hinzufügen:
					tmpOrderList.add(tmpOrder);
					tmpScheduleOrderRemoveMap.put(tmpOrder.getOrderID(), EventXMLParser.getDate(event.getChild("ThrowTime")));
				}
				// Event erzeugen:
				tmpEventList.add(new RemoveOrdersEvent(scenario.getScenarioID(), tmpOrderList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(REMOVE_ORDERS_PRODUCTS_EVENT)) {
				Map<ScheduleOrder, List<Product>> tmpRemoveMap = new HashMap<ScheduleOrder, List<Product>>();
				
				// RemoveMap erstellen:
				
				// Orders durchlaufen:
				Element tmpOrdersElement = event.getChild("Orders");
				if(tmpOrdersElement == null) {
					throw new AnythingNotDefinedException("Orders", new RemoveOrdersEvent().getName());
				}
				for(Object tmpOrderObject : tmpOrdersElement.getChildren("Order")) {
					Element tmpOrderElement = (Element)tmpOrderObject;
					int tmpOrderID = Integer.valueOf(tmpOrderElement.getChild("OrderID").getValue());
					List<Product> tmpRemoveProductList = new ArrayList<Product>();
					
					// Order suchen:
					ScheduleOrder tmpOrder = scenario.getOrder(tmpOrderID);
					
					// Order prüfen:
					if(tmpOrder == null) {
						// Order ist in der list:
						for(ScheduleOrder so : tmpNewScheduleOrderList) {
							if(so.getOrderID() == tmpOrderID) {
								tmpOrder = so;
							}
						}
						if(tmpOrder == null) {
							throw new OrderNotExistsException(tmpOrderID);
						}
					}	
						
					// Products durchlaufen:
					Element tmpProductsElement = tmpOrderElement.getChild("Products");
					if(tmpProductsElement == null) {
						throw new AnythingNotDefinedException("Products", new RemoveProductsEvent().getName());
					}
					for(Object tmpProductObject : tmpOrderElement.getChildren("Product")) {
						Element tmpProductElement = (Element)tmpProductObject;
						Element tmpProductIDElement = tmpProductElement.getChild("ProductID");
						if(tmpProductIDElement == null) {
							throw new AnythingNotDefinedException("ProductID", new RemoveOrdersProductsEvent().getName());
						}
						int tmpProductID = Integer.valueOf(tmpProductIDElement.getValue());
						Element tmpProductAmountElement = tmpProductElement.getChild("RemoveAmount");
						if(tmpProductAmountElement == null) {
							throw new AnythingNotDefinedException("Amount", new RemoveOrdersProductsEvent().getName());
						}
						int tmpProductRemoveAmount = Integer.valueOf(tmpProductAmountElement.getValue());
						Product tmpProduct = scenario.getProduct(tmpProductID);

						// Prüfen, ob es das Product gibt:
						if(tmpProduct == null) {
							for(Product p : tmpNewProductsList) {
								if(p.getProductID() == tmpProductID) {
									tmpProduct = p;
								}
							}
						}

						// Ist das Product überhaupt so oft in der Order?
						if(tmpProductRemoveAmount > tmpOrder.getProductCountByID(tmpProductID)) {
							throw new WrongRemoveProductsFromOrderException(tmpOrderID, tmpProduct.getName(), 
									tmpOrder.getProductCountByID(tmpProductID), tmpProductRemoveAmount);
						}

						// Products in die RemoveList einfügen:
						for(int i = 0; i < tmpProductRemoveAmount; i++) {
							tmpRemoveProductList.add(tmpProduct);
						}
					}
					
					// In die RemoveMap einfügen:
					tmpRemoveMap.put(tmpOrder, tmpRemoveProductList);
				}
				
				// Event erzeugen:
				tmpEventList.add(new RemoveOrdersProductsEvent(scenario.getScenarioID(), tmpRemoveMap, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(REMOVE_PRODUCTS_EVENT)) {
				List<Product> tmpRemoveProductList = new ArrayList<Product>();
				
				// Products durchlaufen:
				Element tmpProductsElement = event.getChild("Products");
				if(tmpProductsElement == null) {
					throw new AnythingNotDefinedException("Products", new RemoveProductsEvent().getName());
				}
				for(Object tmpProductObject : tmpProductsElement.getChildren("ProductID")) {
					int tmpProductID = Integer.valueOf(((Element)tmpProductObject).getValue());
					
					Product tmpProduct = scenario.getProduct(tmpProductID);
					
					// Prüfen, ob es das Product gibt:
					if(tmpProduct == null) {
						for(Product p : tmpNewProductsList) {
							if(p.getProductID() == tmpProductID) {
								tmpProduct = p;
							}
						}
						if(tmpProduct == null) {
							throw new ProductNotExistsException(tmpProductID);
						}
					}
					
					// Product in die RemoveList:
					tmpRemoveProductList.add(tmpProduct);
					tmpProductsRemoveMap.put(tmpProductID, EventXMLParser.getDate(event.getChild("ThrowTime")));
				}
				
				// Event erzeugen:
				tmpEventList.add(new RemoveProductsEvent(scenario.getScenarioID(), tmpRemoveProductList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(REMOVE_VARIANTS_EVENT)) {
				List<Variant> tmpVariantRemoveList = new ArrayList<Variant>();
				
				// Varianten durchlaufen:
				Element tmpVariantsElement = event.getChild("Variants");
				if(tmpVariantsElement == null) {
					throw new AnythingNotDefinedException("Variants", new RemoveVariantsEvent().getName());
				}
				for(Object tmpVariantObject : tmpVariantsElement.getChildren("VariantID")) {
					int tmpVariantID = Integer.valueOf(((Element)tmpVariantObject).getValue());
					Variant tmpVariant = scenario.getVariant(tmpVariantID);
					
					
					// Schauen, ob es die Variant gibt:
					tmpVariant = scenario.getVariant(tmpVariantID);
					
					if(tmpVariant == null) {
						// Variant muss in der List sein:
						for(Variant v : tmpNewVariantsList) {
							if(v.getVariantID() == tmpVariantID) {
								tmpVariant = v;
							}
						}
						if(tmpVariant == null) {
							throw new VariantNotExistsException(tmpVariantID);
						}
					}

				}
				
				// Event erzeugen:
				tmpEventList.add(new RemoveVariantsEvent(scenario.getScenarioID(), tmpVariantRemoveList, EventXMLParser.getDate(event.getChild("ThrowTime"))));
				continue;
			}
			
			if(eventName.equals(CHANGE_OPERATION_RESOURCES)) {
				
				List<Integer> tmpNewResourceList = new ArrayList<Integer>();
				Element tmpOperationIDElement = event.getChild("OperationID");
				if(tmpOperationIDElement == null) {
					throw new AnythingNotDefinedException("OperationID", new ChangeOperationResourcesEvent().getName());
				}
				int tmpOperationID = Integer.valueOf(event.getChild("OperationID").getValue());
				Variant tmpVariant = scenario.getVariantByOperationID(tmpOperationID);
				Operation tmpOperation = scenario.getOperation(tmpOperationID);
				
				// Operation prüfen:
				if(tmpOperation == null) {
					// Operations in Variant List suchen:
					for(Variant v : tmpNewVariantsList) {
						tmpOperation = v.getOperation(tmpOperationID);
						if(tmpOperation != null) {
							tmpVariant = v;
							break;
						}
					}
					
					// Falls tmpOperation immer noch null ist ->Exception
					if(tmpOperation == null) {
						throw new OperationNotExistsException(tmpOperationID);
					}
					
				}
				
				// Resources durchlaufen:
				Element resourcesElement = event.getChild("Resources");
				for(Object tmpResourceIDObject : resourcesElement.getChildren("ResourceID")) {
					int tmpResourceID = Integer.valueOf(((Element)tmpResourceIDObject).getValue());
					
					// Schauen, ob die Resource vorhanden ist:
					if((scenario.getResources() == null)) {
						throw new ResourceNotExistsException(tmpResourceID);
					}
					
					// In die Liste einfügen:
					tmpNewResourceList.add(tmpResourceID);
				}
			
				tmpEventList.add(new ChangeOperationResourcesEvent(scenario.getScenarioID(), tmpVariant.getProduct().getProductID(), 
						tmpOperation.getVariantID(), tmpOperationID, 
						Integer.valueOf(event.getChild("Duration").getValue()), tmpNewResourceList, 
						EventXMLParser.getDate(event.getChild("ThrowTime"))));
				
				continue;
			}

			// Keines der Events:
			throw new UnknownEventException(event.getAttribute("name").getValue());
		}
		return tmpEventList;
	}
	
	/**
	 * Gibt die richtige Zeit aus.
	 * @param element mit Zeitstempel
	 * @return
	 */
	private static Date getDate(Element element) {
		
		Long throwTime = Long.valueOf(element.getValue().trim());
		
		// Ist eine Absolute Zeit angabe:
		if(element.getAttribute("absoluteTime").getValue().toLowerCase().trim().equals("true")) {
			
			if(throwTime <= new Date().getTime()) {
				return new Date();
			} else {
				return new Date(throwTime);
			}
		}
		
		// Wird auf die aktuelle Systemzeit addiert:
		return new Date(new Date().getTime() +throwTime);
	}
}
