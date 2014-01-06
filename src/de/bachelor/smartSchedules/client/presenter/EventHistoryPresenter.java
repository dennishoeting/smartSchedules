package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import de.bachelor.smartSchedules.client.view.EventHistoryView;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
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
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * Presenter für die EventHistory
 * @author Dennis
 */
public class EventHistoryPresenter {
	/**
	 * zugehöriger View
	 */
	private final EventHistoryView view;
	
	private int offset, limit = 10;
	
	/**
	 * Liste mit den alten Schedules
	 * Zusammengesetzt aus 
	 * 		ScenarioPresenter.getInstance().getScheduleEvents
	 * 			(Events, die zur Laufzeit gekommen sind)
	 * 	und Älteren Events aus der Datenbank
	 */
	private List<ScheduleEvent> oldScheduleEventStack;
	
	/**
	 * Konstruktor
	 */
	public EventHistoryPresenter() {
		view = new EventHistoryView();
		oldScheduleEventStack = new ArrayList<ScheduleEvent>();

		this.addListeners();
	}
	
	public void insertFromScenario(List<Schedule> result) {
		for(Schedule schedule : result) {
			if(schedule.getScheduleEvent()!=null) {
				oldScheduleEventStack.add(schedule.getScheduleEvent());
			} else {
				GWT.log("fromScenario: "+schedule.getAlgorithmInformations().getAlgorithmName() + " from "+schedule.getAlgorithmInformations().getDueTime()+" has no evnt");
			}
		}
		
		updateListGrid();
	}
	
	public void insertFromDB(List<Schedule> result) {		
		for(Schedule schedule : result) {
			if(schedule.getScheduleEvent()!=null) {
				oldScheduleEventStack.add(schedule.getScheduleEvent());
			} else {
				GWT.log("fromDatabase: "+schedule.getAlgorithmInformations().getAlgorithmName() + " from "+schedule.getAlgorithmInformations().getDueTime()+" has no evnt");
			}
		}
		
		updateListGrid();
	}
	
	/**
	 * Das neue Event oben auf den Stack packen
	 */
	public void insertIntoListGrid(ScheduleEvent event) {
		oldScheduleEventStack.add(event);
		
		updateListGrid();
	}
	
	/**
	 * Überführt die ScheduleEvents aus dem oldSchedulesStack in das ListGrid
	 */
	public void updateListGrid() {
		/*
		 * Leeren
		 */
		for(Record r : this.view.getEventHistoryGrid().getRecords()) {
			this.view.getEventHistoryGrid().removeData(r);
		}
		
		/*
		 * Sortieren
		 */
		Collections.sort(oldScheduleEventStack, new Comparator<ScheduleEvent>() {
			@Override
			public int compare(ScheduleEvent o1, ScheduleEvent o2) {
				if(o1.getThrowTime().before(o2.getThrowTime())) return 1;
				if(o1.getThrowTime().after(o2.getThrowTime())) return -1;
				return 0;
			}
		});
		
		/*
		 * Neu füllen
		 */
		for(ScheduleEvent event : this.oldScheduleEventStack) {
			this.view.getEventHistoryGrid().addData(new OldEventRecord(event, event.getType()));
		}
	}

	private void addListeners() {
		this.view.getAddOlderEventsToListButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				offset = oldScheduleEventStack.size();	
				
				ServerCommunicationsManager.getInstance().getSchedules(ScenarioPresenter.getInstance().getScenario().getScenarioID(), offset, limit, new AsyncCallback<List<Schedule>>() {
					@Override
					public void onSuccess(List<Schedule> result) {
						insertFromDB(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SC.say("Old schedules not received from database.");
					}
				});
			}
		});
		
		/**
		 * Bei Selektierung eines Events in rechter ListGrid entsprechend Details anzeigen
		 */
		this.view.getEventHistoryGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if(event.getState()) {
					view.visualizeEventDetails((OldEventRecord) event.getSelectedRecord());
				}
			}
		});
	}
	
	public EventHistoryView getView() {
		return view;
	}
	/**
	 * Record für zu werfende Ereignisse
	 * @author Dennis
	 */
	public class OldEventRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param event Ereignis
		 * @param eventType Typ als int
		 * @see ScheduleEvent
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private OldEventRecord(ScheduleEvent event, int eventType) {
			this.setEvent(event);
			this.setType(eventType);
			this.setTypeString(event.getName());
			this.setAttribute("throwTime", event.getThrowTime());
			
			/*
			 * Definition des Records je nach EventTyp
			 */
			switch(eventType) {
			case ScheduleEvent.TYPE_SCHEDULE_CHANGE_BY_USER_EVENT:
				GWT.log((((ScheduleChangeByUserEvent)event)==null)+"");
				GWT.log((((ScheduleChangeByUserEvent)event).getOldSchedule()==null)+"");
				GWT.log((((ScheduleChangeByUserEvent)event).getOldSchedule().getAlgorithmInformations()==null)+"");
				GWT.log((((ScheduleChangeByUserEvent)event).getOldSchedule().getAlgorithmInformations().getAlgorithmName()==null)+"");
				
				this.setAttribute("name", ((ScheduleChangeByUserEvent)event).getName());
				this.setAttribute("oldSchedule", ((ScheduleChangeByUserEvent)event).getOldSchedule().getAlgorithmInformations().getAlgorithmName());
				this.setAttribute("newSchedule", ((ScheduleChangeByUserEvent)event).getNewSchedule().getAlgorithmInformations().getAlgorithmName());
				break;
			case ScheduleEvent.TYPE_NEW_ORDERS_EVENT: 
				ScheduleOrder order = ((NewOrdersEvent)event).getNewOrdersList().get(0);
				this.setAttribute("name", "New order ("+order.getName()+")");
				this.setAttribute("orderName", order.getName());
				this.setAttribute("orderPriority", order.getPriority());
				this.setAttribute("orderDueTime", order.getEarlistDueTime());
				this.setAttribute("productAmount", order.getProducts(ScenarioPresenter.getInstance().getScenario()).size());
				this.setAttribute("productList", order.getProducts(ScenarioPresenter.getInstance().getScenario()));
				break;
			case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT: 
				Product product = ((NewProductsEvent)event).getNewProducts().get(0);
				this.setAttribute("name", "New product ("+product.getName()+")");
				this.setAttribute("productName", product.getName());
				this.setAttribute("operationAmount", product.getVariants().size());
				this.setAttribute("operationList", product.getVariants().get(0).getOperations());
				break;
			case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT: 
				Variant variant = ((NewVariantsEvent)event).getNewVariants().get(0);
				this.setAttribute("name", "New variant for "+variant.getProduct().getName());
				this.setAttribute("productName", variant.getProduct().getName());
				this.setAttribute("operationAmount", variant.getOperations().size());
				this.setAttribute("operationList", variant.getOperations());
				break;
			case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:
				Resource resource2 = ((NewResourceEvent)event).getNewResources().get(0);
				this.setAttribute("name", "New resource ("+resource2.getName()+")");
				this.setAttribute("awailability", resource2.getAvailability()+"%");
				break;
			case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT:
				ScheduleOrder order2 = ((ChangeOrderDueTimeEvent)event).getChangedOrders().get(0);
				this.setAttribute("name", "Due date change for "+order2.getName());
				this.setAttribute("orderName", order2.getName());
				this.setAttribute("newDueDate", order2.getEarlistDueTime());
				break;
			case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT: 
				ScheduleOrder order3 = ((ChangeOrderPriorityEvent)event).getChangeOrders().get(0);
				this.setAttribute("name", "Priority change for "+order3.getName());
				this.setAttribute("orderName", order3.getName());
				this.setAttribute("newPriority", order3.getPriority());
				break;
			case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT: 
				ScheduleOrder order7 = (ScheduleOrder) new ArrayList(((RemoveOrdersProductsEvent)event).getRemoveMap().keySet()).get(0);
				this.setAttribute("name", "Removement of products of "+order7.getName());
				this.setAttribute("orderName", order7.getName());
				this.setAttribute("removedProductsAmount", ((RemoveOrdersProductsEvent)event).getRemoveMap().values().size());
				this.setAttribute("removedProducts", new ArrayList(((RemoveOrdersProductsEvent)event).getRemoveMap().values()));
				break;
			case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT: 
				Resource resource = (Resource) ScenarioPresenter.getInstance().getScenario().getResources().get(((ChangeResourceAvailabilityEvent)event).getChangedResourceAvailabilities().keySet().toArray()[0]);
				this.setAttribute("name", "Change of awailability of "+resource.getName());
				this.setAttribute("resourceName", resource.getName());
				this.setAttribute("newAwailability", (int)resource.getAvailability()+"%");
				break;
			case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT: 
				ScheduleOrder order6 = ((RemoveOrdersEvent)event).getRemoveOrdersList().get(0);
				this.setAttribute("name", "Removement of "+order6.getName());
				this.setAttribute("orderName", order6.getName());
				break;
			case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT: 
				Product product2 = ((RemoveProductsEvent)event).getRemovedProducts().get(0);
				this.setAttribute("name", "Removement of "+product2.getName());
				this.setAttribute("productName", product2.getName());
				break;
			case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT: 
				Variant variant2 = ((RemoveVariantsEvent)event).getRemovedVariants().get(0);
				this.setAttribute("name", "Removement of variant of "+variant2.getProduct());
				this.setAttribute("productName", variant2.getProduct().getName());
				this.setAttribute("operationAmount", variant2.getOperations().size());
				this.setAttribute("operations", variant2.getOperations());
				break;
			case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT: 
				Resource resource3 = ((MachineBreakDownEvent)event).getResourceBreakDowns().get(0);
				this.setAttribute("name", "Machine breakdown ("+resource3.getName()+")");
				this.setAttribute("machineName", resource3.getName());
				this.setAttribute("breakdownDate", ((MachineBreakDownEvent)event).getThrowTime());
				break;
			case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
				Resource resource4 = ((MachineRepairedEvent)event).getResourceRepairs().get(0);
				this.setAttribute("name", "Machine repair ("+resource4.getName()+")");
				this.setAttribute("machineName", resource4.getName());
				this.setAttribute("repairDate", ((MachineRepairedEvent)event).getThrowTime());
				break;
			case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT: 
				Resource resource5 = ScenarioPresenter.getInstance().getScenario().getResources().get(((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getResourceID());
				this.setAttribute("name", "Maintenace period ("+resource5.getName()+")");
				this.setAttribute("machineName", resource5.getName());
				this.setAttribute("beginDate", ((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getStartTime());
				this.setAttribute("endDate", ((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getDueTime());
				break;
			case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
				ChangeOperationResourcesEvent changeOperationResourcesEvent = (ChangeOperationResourcesEvent)event;
				Product productForOperation = ScenarioPresenter	.getInstance().getScenario().getProduct(changeOperationResourcesEvent.getProductID());
				Variant variantForOperation = productForOperation.getVariant(changeOperationResourcesEvent.getVariantID());
				Operation operation = variantForOperation.getOperation(changeOperationResourcesEvent.getOperationID());
				this.setAttribute("name", "Operation Change ("+operation.getName()+")");
				this.setAttribute("productName", productForOperation.getName());
				this.setAttribute("variantName", "Variant"+variantForOperation.getVariantID());
				this.setAttribute("operationName", operation.getName());
				this.setAttribute("duration", changeOperationResourcesEvent.getDuration());
				this.setAttribute("resourceAlternativesAmount", changeOperationResourcesEvent.getNewResourceList().size());
				this.setAttribute("resourceAlternativesIDs", changeOperationResourcesEvent.getNewResourceList());
				break;
			}
		}
		
		public void setEvent(ScheduleEvent event) {
			this.setAttribute("event", event);
		}
		
		public ScheduleEvent getEvent() {
			return (ScheduleEvent) this.getAttributeAsObject("event");
		}
		
		public void setType(int type) {
			this.setAttribute("type", type);
		}
		
		public int getType() {
			return this.getAttributeAsInt("type");
		}
		
		public void setTypeString(String name) {
			this.setAttribute("typeString", name);
		}
		
		public String getName() {
			return this.getAttributeAsString("typeString");
		}
	}
}
