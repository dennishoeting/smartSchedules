package de.bachelor.smartSchedules.client.view;

import java.util.List;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.presenter.EventHistoryPresenter.OldEventRecord;
import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;
import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * View für alte Events
 * @author Dennis
 */
public class EventHistoryView extends Tab {
	/**
	 * Größe
	 */
	private static final int LEFT_STACK_CONTENT_WIDTH = 600,
	 						 RIGHT_STACK_CONTENT_WIDTH = 290,
	 						 CONTENT_HEIGHT = 535;
	
	/**
	 * ListGrid mit den alten Events
	 */
	private final EventHistoryGrid eventHistoryGrid;
	
	/**
	 * DetailViewer
	 */
	private final DetailViewer eventDetailViewer;
	
	private final Button addOlderEventsToListButton;
	
	/**
	 * Konstruktor
	 */
	public EventHistoryView() {
		this.setTitle("Event History");
		
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		this.eventHistoryGrid = new EventHistoryGrid(LEFT_STACK_CONTENT_WIDTH, CONTENT_HEIGHT);
		this.eventDetailViewer = new DetailViewer();
		this.eventDetailViewer.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		this.eventDetailViewer.setHeight(CONTENT_HEIGHT-50);
		this.eventDetailViewer.setEmptyMessage("Select an event to view its details"); 
		this.addOlderEventsToListButton = new Button("Add older Events to list", RIGHT_STACK_CONTENT_WIDTH);
		rightStack.addMembers(eventDetailViewer, addOlderEventsToListButton);
		mainStack.addMembers(eventHistoryGrid, rightStack);
		this.setPane(mainStack);
	}
	

	@SuppressWarnings("unchecked")
	/**
	 * Visualisiert die Details des übergebenen Records im DetailViewer
	 * @param record ein OldEventRecord
	 */
	public void visualizeEventDetails(OldEventRecord record) {
		this.eventDetailViewer.viewSelectedData(this.eventHistoryGrid);
		
		/*
		 * Fallentscheidung nach Eventtyp
		 */
		switch(record.getType()) {
		case ScheduleEvent.TYPE_SCHEDULE_CHANGE_BY_USER_EVENT:
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Change date"),
					 						 new DetailViewerField("oldSchedule", "Old Algorithm"),
					 						 new DetailViewerField("newSchedule", "New Algorithm"));
			
			break;
		/*
		 * NewOrderEvent
		 */
		case ScheduleEvent.TYPE_NEW_ORDERS_EVENT:
			String productList = "";
			for(Product p : (List<Product>)(record.getAttributeAsObject("productList"))) {
				productList += p.getName();
				productList += ", ";
			}
			productList = productList.substring(0, productList.length()-2);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("orderName", "Order name"),
					 						 new DetailViewerField("orderPriority", "Priority"),
					 						 new DetailViewerField("orderDueTime", "Due time"),
					 						 new DetailViewerField("productAmount", "# <a href='javascript:alert(\""+productList+"\")'>products</a>"));
			break;
		/*
		 * NewProductsEvent
		 */
		case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT: 
			String operationList = "";
			String resourceList = "";
			for(Operation o : (List<Operation>)(record.getAttributeAsObject("operationList"))) {
				resourceList = "";
				
				for(Integer resourceID : o.getResourceAlternatives()) {
					resourceList += ScenarioPresenter.getInstance().getScenario().getResources().get(resourceID).getName();
					resourceList += ",&nbsp;";
				}
				resourceList = resourceList.substring(0, resourceList.length()-7);
				operationList += "{name : "+o.getName()+", resource"+((o.getResourceAlternatives().size()>1)?"s":"")+" : "+resourceList+", duration : "+o.getDuration()+"}";
				operationList += ",&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			}
			operationList = operationList.substring(0, operationList.length()-43);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("productName", "Product name"),
					 						 new DetailViewerField("operationAmount", "# <a href='javascript:alert(\""+operationList+"\")'>operations</a"));
			break;
		/*
		 * NewVariantsEvent
		 */
		case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT: 
			String operationList2 = "";
			String resourceList2 = "";
			for(Operation o : (List<Operation>)(record.getAttributeAsObject("operationList"))) {
				resourceList2 = "";
				for(Integer resourceID : o.getResourceAlternatives()) {
					resourceList2 += ScenarioPresenter.getInstance().getScenario().getResources().get(resourceID).getName();
					resourceList2 += ",&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				}
				resourceList2 = resourceList2.substring(0, resourceList2.length()-43);
				operationList2 += "{name : "+o.getName()+", resource"+((o.getResourceAlternatives().size()>1)?"s":"")+" : "+resourceList2+", duration : "+o.getDuration()+"}";
				operationList2 += ",&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			}
			operationList2 = operationList2.substring(0, operationList2.length()-43);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("productName", "Product name"),
					 						 new DetailViewerField("operationAmount", "# <a href='javascript:alert(\""+operationList2+"\")'>operations</a"));

			break;
		/*
		 * NewResourceEvent
		 */
		case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("awailability", "Awailability"));
			break;
		/*
		 * ChangeOrdersDueTimesEvent
		 */
		case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT:
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("orderName", "Order name"),
					 						 new DetailViewerField("newDueDate", "New due date"));
			break;
		/*
		 * ChangeOrdersPriorityEvent
		 */
		case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
											 new DetailViewerField("throwTime", "Throw time"),
											 new DetailViewerField("typeString", "Type"),
											 new DetailViewerField("orderName", "Order name"),
											 new DetailViewerField("newPriority", "New priority"));
			break;
		/*
		 * RemoveOrdersProductsEvent
		 */
		case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT: 
			String removedProductsList = "";
			for(Product p : (List<Product>)(record.getAttributeAsObject("removedProducts"))) {
				removedProductsList += p.getName();
				removedProductsList += ", ";
			}
			productList = removedProductsList.substring(0, removedProductsList.length()-2);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("orderName", "Order name"),
					 						 new DetailViewerField("removedProductsAmount", "# <a href='javascript:alert(\""+removedProductsList+"\"'>removed products</a>"));
			break;
		/*
		 * ChangeOrdersStartTimesEvent
		 */
		case ScheduleEvent.TYPE_CHANGE_ORDERS_START_TIMES_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("orderName", "Order name"),
					 						 new DetailViewerField("newStartDate", "New start date"));
			break;
		/*
		 * ChangeResourceAvaiabilityEvent
		 */
		case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("resourceName", "Resource name"),
					 						 new DetailViewerField("newAwaiability", "New awaiability"));
			break;
		/*
		 * RemoveOrdesEvent
		 */
		case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("orderName", "Order name"));
			break;
		/*
		 * RemoveProductsEvent
		 */
		case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("resourceName", "Resource name"));
			break;
		/*
		 * RemoveVariantsEvent
		 */
		case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT: 
			String removedVariantOperationsList = "";
			for(Operation p : (List<Operation>)(record.getAttributeAsObject("operations"))) {
				removedVariantOperationsList += p.getName();
				removedVariantOperationsList += ", ";
			}
			productList = removedVariantOperationsList.substring(0, removedVariantOperationsList.length()-2);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("productName", "Product name"),
					 						 new DetailViewerField("operationAmount", "# <a href='javascript\""+removedVariantOperationsList+"\"'>operations</a>"));
			break;
		/*
		 * MachineBreakDownsEvent
		 */
		case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("machineName", "Resource"),
					 						 new DetailViewerField("breakdownDate", "Date"));
			break;
		/*
		 * MachineRepairedEvent
		 */
		case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("machineName", "Resource"),
					 						 new DetailViewerField("repairDate", "Date"));
			break;
		/*
		 * MaintenacePeriodsEvent
		 */
		case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT: 
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
					 						 new DetailViewerField("throwTime", "Throw time"),
					 						 new DetailViewerField("typeString", "Type"),
					 						 new DetailViewerField("machineName", "Resource"),
					 						 new DetailViewerField("beginDate", "Start"),
					 						 new DetailViewerField("endDate", "Ending"));
			break;
		/*
		 * ChangeOperationResourcesEvent
		 */
		case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
			String resourcesList = "";
			for(Integer id : (List<Integer>)(record.getAttributeAsObject("resourceAlternativesIDs"))) {
				resourcesList += ScenarioPresenter.getInstance().getScenario().getResources().get(id).getName();
				resourcesList += ", ";
			}
			resourcesList = resourcesList.substring(0, resourcesList.length()-2);
			this.eventDetailViewer.setFields(new DetailViewerField("name", "Name"),
											 new DetailViewerField("throwTime", "Throw time"),
											 new DetailViewerField("typeString", "Type"),
											 new DetailViewerField("productName", "Product"),
											 new DetailViewerField("variantName", "Variant"),
											 new DetailViewerField("operationName", "Operation"),
											 new DetailViewerField("duration", "Duration"),
											 new DetailViewerField("resourceAlternativesAmount", "# <a href='javascript:alert(\""+resourcesList+"\")'>ResourceAlternatives</a>"));
			break;
		}
	}
	
	public EventHistoryGrid getEventHistoryGrid() {
		return eventHistoryGrid;
	}


	public DetailViewer getEventDetailViewer() {
		return eventDetailViewer;
	}

	public Button getAddOlderEventsToListButton() {
		return addOlderEventsToListButton;
	}

	/**
	 * ListGrid für alte Events
	 * @author Dennis
	 */
	public class EventHistoryGrid extends ListGrid {
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		public EventHistoryGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
			this.setSelectionType(SelectionStyle.SINGLE);
			
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			
			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField name = new ListGridField("name", "Name");
			name.setWidth(355);
			final ListGridField throwTime = new ListGridField("throwTime", "Throw time");
			throwTime.setWidth(220);
			
			this.setFields(name, throwTime);
		}
	}
}
