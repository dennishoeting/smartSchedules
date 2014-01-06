package de.bachelor.smartSchedules.client.view;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Slider;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.presenter.InvokeEventPresenter.EventsToInvokeRecord;
import de.bachelor.smartSchedules.client.presenter.InvokeEventPresenter.OperationRecord;
import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;
import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.FileUploader;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.TimeField;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * View für das Werfen eines Events
 * @author Dennis
 */
public class InvokeEventView extends Tab implements FileUploaderInterface {
	/**
	 * TextItems
	 */
	private final TextItem newOrderTextItem,
						   newProductTextItem,
						   newOperationName,
						   newOperationInNewProductName,
						   newResourceTextItem;
	
	/**
	 * Beschreibungsfenster
	 */
	private final Window description;
	
	/**
	 * Inhalt des Fensters
	 */
	private final HTMLPane descriptionPane;
	
	/**
	 * Buttons
	 */
	private final Button includeButton,
						 invokeButton,
						 submitButton;
	
	/**
	 * ListGrid für EventTypen
	 */
	private final EventTypeListGrid eventTypeListGrid;
	
	/**
	 * ListGrid für gewählte Events
	 */
	private final SelectedEventsListGrid selectedEventsListGrid;
	
	/**
	 * Bereich für variablen Input
	 */
	private final Canvas specialInputCanvas;
	
	/**
	 * Checkbox für sofortiges Werfen
	 */
	private final CheckboxItem throwImmediatelyCheckboxItem;
	
	/**
	 * Label bei keiner Selektion
	 */
	private final Label noSelectionLabel;

	/**
	 * Canvas, der upload-Status anzeigt (notDone, valid, invalid)
	 */
	public final Label uploadValidLabel;
	
	/**
	 * SelectItems
	 */
	private final SelectItem   orderChooserForDueTimeComboBoxItem,
							   orderChooserForProductChange,
							   resourceChooserForMachineRepair,
							   orderChooserForPriorityComboBoxItem,
							   priorityChooser,
							   newOrderPriorityChooser,
							   resourceChooserForAvaiabilityComboBoxItem,
							   orderChooserForRemovementComboBoxItem,
							   productChooserForRemovementComboBoxItem,
							   productChooserForVariantRemovementComboBoxItem,
							   variantChooserForRemovement,
							   resourceChooserForMachineBreakdown,
							   resourceChooserForMaintainacePeriod,
							   newVariantProductChooser,
							   productChooserForOperationChangeComboBoxItem,
							   variantChooserForOperationChange,
							   operationChooserForChange;
	
	/**
	 * Datumsanzeigen
	 */
	private final DateItem throwDateItem,
						   orderDueDateItem,
						   maintenaceStartDateItem,
						   maintenaceDueDateItem,
						   newOrderDueDateItem;
	
	/**
	 * Ordnung
	 */
	private final VerticalStack newOrderStack,
								newProductStack,
								newVariantStack,
								changeResourceAvaiabilityStack,
								changeOrderProductStack,
								changeOrderDueStack,
								machineBreakdownStack,
								machineRepairedStack,
								maintenacePeriodStack,
								newResourceStack;
	
	/**
	 * Ordnung
	 */
	private final HorizontalStack changeOrderPriorityStack,
								  changeOperationResourcesStack;
	
	/**
	 * Ordnung
	 */
	private final DynamicForm changeResourceAvaiabilityForm,
							  removeOrderForm,
							  removeProductForm,
							  removeVariantForm,
							  changeOrderProductForm;
	
	/**
	 * Slider
	 */
	private final Slider resourceAvaiabilitySlider,
						 resourceAwailabilitySlider2;
	
	/**
	 * ListGrids
	 */
	private final ListGrid changeOrderProductLeftListGrid,
						   changeOrderProductRightListGrid,
						   newOrderLeftListGrid,
						   newOrderRightListGrid,
						   resourceGridForOperationChange,
						   resourceListGrifForNewProduct,
						   resourceListGridForNewVariant;
	private final OperationsInNewVariantListGrid newProductRightListGrid, newVariantRightListGrid;
	
	/**
	 * TransferButtons
	 */
	private final TransferImgButton changeOrderProductArrowRight,
									changeOrderProductArrowLeft,
									newOrderArrow,
									newProductArrow,
									newVariantArrow;
	
	/**
	 * Spinner
	 */
	private final SpinnerItem newOperationDurationSpinner,
							  newOperationInNewProductDurationSpinner,
							  durationSpinnerForOperationChange;
	
	/**
	 * DetailViweer
	 */
	private final DetailViewer eventDetailViewer;
	
	/**
	 * Zeitanzeigen
	 * @see TimeField
	 */
	private final TimeField throwTimeField,
							newOrderDueTimeField,
							orderDueTimeField,
							maintenaceStartTimeField,
							maintenaceDueTimeField; 
	
	/**
	 * Upload
	 */
	private final FileUploader fileUpload;
	
	/**
	 * Größe
	 */
	private static final int LEFT_STACK_WIDTH = 650,
							 RIGHT_STACK_WIDTH = 250,
							 UPPER_STACK_HEIGHT = 30,
							 LOWER_STACK_HEIGHT = 495,
							 DESCRIPTION_HEIGHT = 100,
							 LIST_GRID_WIDTH = 200,
							 CENTER_WIDTH = LEFT_STACK_WIDTH - LIST_GRID_WIDTH -10,
							 SPECIAL_INPUT_HEIGHT = 265,
							 DETAIL_VIEWER_HEIGHT = 200;
	
	/**
	 * Hilfskonstante
	 */
	public static final int NO_SELECTION = -1;
	
	/**
	 * Konstruktor
	 * @param active true, wenn in erweiterter Sicht, sonst false
	 */
	public InvokeEventView(boolean active) {
		/*
		 * Deaktivierung aller Komponenten (Komposite) wenn nicht aktiver Nutzer
		 */
		if(!active) {
			this.setDisabled(true);
		}
		this.setTitle("Invoke Event");
		
		/*
		 * Hauptordnung
		 */
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		mainStack.setOverflow(Overflow.HIDDEN);
		
		final VerticalStack leftStack = new VerticalStack();

		/*
		 * Upload
		 */
		final HorizontalStack upperStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		this.submitButton = new Button("Submit", 200);
		this.uploadValidLabel = new Label("No upload performed yet.");
		this.uploadValidLabel.setAlign(Alignment.CENTER);
		this.uploadValidLabel.setWidth(180);
		this.uploadValidLabel.setHeight(25);
		this.uploadValidLabel.setBorder("1px solid black");
		this.uploadValidLabel.setBackgroundColor("#EEE");
		upperStack.setHeight(UPPER_STACK_HEIGHT);
		this.fileUpload = new FileUploader(this);
		upperStack.addMembers(fileUpload, uploadValidLabel, submitButton);
		final HLine line2 = new HLine(LEFT_STACK_WIDTH);

		/*
		 * Linke Seite
		 */
		final HorizontalStack lowerStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		final SectionStack eventTypeListGridStack = new SectionStack();
		eventTypeListGridStack.setWidth(LIST_GRID_WIDTH);
		eventTypeListGridStack.setHeight(LOWER_STACK_HEIGHT);
		final SectionStackSection eventTypeListGridStackSection = new SectionStackSection("Event range");
		eventTypeListGridStackSection.setCanCollapse(false);
		eventTypeListGridStackSection.setExpanded(true);
		this.eventTypeListGrid = new EventTypeListGrid(LIST_GRID_WIDTH, LOWER_STACK_HEIGHT);
		this.eventTypeListGrid.setBorder("0px");
		this.eventTypeListGrid.setShowHeader(false);
		eventTypeListGridStackSection.addItem(eventTypeListGrid);
		eventTypeListGridStack.addSection(eventTypeListGridStackSection);
		
		/*
		 * Mittlerer Bereich
		 * Event-Beschreibung
		 */
		final VerticalStack eventDetailStack = new VerticalStack();
		eventDetailStack.setMembersMargin(5);
		this.description = new Window();
		this.description.setTitle("Description");
		this.description.setWidth(CENTER_WIDTH);
		this.description.setHeight(DESCRIPTION_HEIGHT);
		this.description.setShowCloseButton(false);
		this.description.setCanDragReposition(false);
		this.description.setAnimateMinimize(true);
		this.description.setMinimizeTime(500);
		this.descriptionPane = new HTMLPane();
		this.descriptionPane.setPadding(15);
		this.description.addItem(descriptionPane);
		final HLine line3 = new HLine(CENTER_WIDTH);
		/*
		 * Zeit-Bereich
		 */
		final HorizontalStack generalInputStack = new HorizontalStack();
		generalInputStack.setHeight(50);
		final DynamicForm generalInputForm1 = new DynamicForm();
		this.throwImmediatelyCheckboxItem = new CheckboxItem("throwImmediately","Throw immediately");
		this.throwImmediatelyCheckboxItem.setValue(true);
		this.throwDateItem = new DateItem();  
		this.throwDateItem.setTitle("Throw date");
		this.throwDateItem.setDisabled(true);
		this.throwDateItem.setWrapTitle(false);
		generalInputForm1.setItems(throwDateItem, throwImmediatelyCheckboxItem);
		this.throwTimeField = new TimeField("&nbsp;&nbsp;Time");
		this.throwTimeField.setDisabled(true);
		generalInputStack.addMembers(generalInputForm1, throwTimeField);
		final HLine line4 = new HLine(CENTER_WIDTH);
		/*
		 * variabler Bereich
		 */
		this.specialInputCanvas = new Canvas();
		this.specialInputCanvas.setHeight(SPECIAL_INPUT_HEIGHT);
		this.noSelectionLabel = new Label("Please select an event");
		this.noSelectionLabel.setWrap(false);
		this.noSelectionLabel.setHeight(15);
		this.specialInputCanvas.addChild(this.noSelectionLabel);
		/*
		 * Include-Button
		 */
		this.includeButton = new Button("Include into list", CENTER_WIDTH);
		this.includeButton.setDisabled(true);
		
		/*
		 * Rechter Bereich
		 */
		final VerticalStack rightStack = new VerticalStack();
		/*
		 * ListGrid
		 */
		final SectionStack selectedEventsListGridStack = new SectionStack();
		selectedEventsListGridStack.setWidth(RIGHT_STACK_WIDTH);
		selectedEventsListGridStack.setHeight(LOWER_STACK_HEIGHT + UPPER_STACK_HEIGHT - 40 - DETAIL_VIEWER_HEIGHT);
		final SectionStackSection selectedEventsListGridStackSection = new SectionStackSection("Events to throw");
		selectedEventsListGridStackSection.setCanCollapse(false);
		selectedEventsListGridStackSection.setExpanded(true);
		this.selectedEventsListGrid = new SelectedEventsListGrid(RIGHT_STACK_WIDTH, LOWER_STACK_HEIGHT + UPPER_STACK_HEIGHT - 40 - DETAIL_VIEWER_HEIGHT);
		this.selectedEventsListGrid.setBorder("0px");
		this.selectedEventsListGrid.setShowHeader(false);
		selectedEventsListGridStackSection.addItem(selectedEventsListGrid);
		selectedEventsListGridStack.addSection(selectedEventsListGridStackSection);
		final HLine line1 = new HLine(RIGHT_STACK_WIDTH);
		/*
		 * DetailViewer
		 */
		this.eventDetailViewer = new DetailViewer();
		this.eventDetailViewer.setWidth(RIGHT_STACK_WIDTH);
		this.eventDetailViewer.setHeight(DETAIL_VIEWER_HEIGHT);
		this.eventDetailViewer.setEmptyMessage("Include an event and watch its details");
		this.eventDetailViewer.setFields(new DetailViewerField("Name"),
										 new DetailViewerField("Throw Date"),
										 new DetailViewerField("Type"));
		
		final HLine line6 = new HLine(RIGHT_STACK_WIDTH);
		/*
		 * Invoke-Button
		 */
		this.invokeButton = new Button("Invoke", RIGHT_STACK_WIDTH);
		this.invokeButton.setTop(15);
		
		/*
		 * Zusammenfügen
		 */
		eventDetailStack.addMembers(description, line3, generalInputStack, line4, specialInputCanvas, includeButton);
		lowerStack.addMembers(eventTypeListGridStack, eventDetailStack);
		leftStack.addMembers(upperStack, line2, lowerStack);
		rightStack.addMembers(selectedEventsListGridStack, line1, eventDetailViewer, line6, invokeButton);
		mainStack.addMembers(leftStack, rightStack);
		this.setPane(mainStack);
		
		
		/*
		 * Variabler Bereich: NewOrderEvent
		 */
		this.newOrderStack = new VerticalStack();
		final HorizontalStack newOrderTopHStack = new HorizontalStack();
		newOrderTopHStack.setHeight(30);
		final DynamicForm newOrderForm = new DynamicForm();
		this.newOrderTextItem = new TextItem("orderName", "Order name");
		this.newOrderTextItem.setWidth(175);
		this.newOrderTextItem.setWrapTitle(false);
		newOrderForm.setItems(newOrderTextItem);
		final DynamicForm newOrderPriorityChooserForm = new DynamicForm();
		this.newOrderPriorityChooser = new SelectItem();  
		this.newOrderPriorityChooser.setTitle("Select priority"); 
		this.newOrderPriorityChooser.setType("comboBox");
		this.newOrderPriorityChooser.setWrapTitle(false);
		this.newOrderPriorityChooser.setWidth(100);
		final LinkedHashMap<String, String> priorityMap = new LinkedHashMap<String, String>();
		priorityMap.put("1", "1 (lowest)");
		priorityMap.put("2", "2");
		priorityMap.put("3", "3");
		priorityMap.put("4", "4");
		priorityMap.put("5", "5");
		priorityMap.put("6", "6");
		priorityMap.put("7", "7");
		priorityMap.put("8", "8");
		priorityMap.put("9", "9");
		priorityMap.put("10", "10 (highest)");
		this.newOrderPriorityChooser.setValueMap(priorityMap);
		this.newOrderPriorityChooser.setValue(priorityMap.get("1"));
		newOrderPriorityChooserForm.setItems(newOrderPriorityChooser);
		newOrderTopHStack.addMembers(newOrderForm, newOrderPriorityChooserForm);
		final HorizontalStack newOrderDateHStack = new HorizontalStack();
		newOrderDateHStack.setHeight(25);
		final DynamicForm newOrderDateHStackLeftForm = new DynamicForm();
		this.newOrderDueDateItem = new DateItem();
		this.newOrderDueDateItem.setTitle("Due date");
		this.newOrderDueDateItem.setWrapTitle(false);
		newOrderDateHStackLeftForm.setItems(newOrderDueDateItem);
		final VerticalStack newOrderDateHStackRightStack = new VerticalStack();
		this.newOrderDueTimeField = new TimeField("&nbsp;&nbsp;Time");
		this.newOrderDueTimeField.setTime(new Date(new Date().getTime()+600000));
		newOrderDateHStackRightStack.addMembers(newOrderDueTimeField);
		newOrderDateHStack.addMembers(newOrderDateHStackLeftForm, newOrderDateHStackRightStack);
		final HorizontalStack newOrderHStack = new HorizontalStack();
		newOrderHStack.setMembersMargin(5);
		final SectionStack newOrderLeftListGridStack = new SectionStack();
		newOrderLeftListGridStack.setWidth(200);
		newOrderLeftListGridStack.setHeight(200);
		final SectionStackSection newOrderLeftListGridStackSection = new SectionStackSection("Product range");
		newOrderLeftListGridStackSection.setCanCollapse(false);
		newOrderLeftListGridStackSection.setExpanded(true);
		this.newOrderLeftListGrid = new NewOrderLeftListGrid();
		newOrderLeftListGridStackSection.addItem(newOrderLeftListGrid);
		newOrderLeftListGridStack.setSections(newOrderLeftListGridStackSection);
		this.newOrderArrow = new TransferImgButton(TransferImgButton.RIGHT);
		final SectionStack newOrderRightListGridStack = new SectionStack();
		newOrderRightListGridStack.setWidth(200);
		newOrderRightListGridStack.setHeight(200);
		final SectionStackSection newOrderRightListGridStackSection = new SectionStackSection("Selected products");
		newOrderRightListGridStackSection.setCanCollapse(false);
		newOrderRightListGridStackSection.setExpanded(true);
		this.newOrderRightListGrid = new NewOrderRightListGrid();
		newOrderRightListGridStackSection.addItem(newOrderRightListGrid);
		newOrderRightListGridStack.setSections(newOrderRightListGridStackSection);
		newOrderHStack.addMembers(newOrderLeftListGridStack, newOrderArrow, newOrderRightListGridStack);
		this.newOrderStack.addMembers(newOrderTopHStack, newOrderDateHStack, newOrderHStack);
		this.newOrderStack.hide();
		
		/*
		 * Variabler Bereich: NewProductEvent
		 */
		this.newProductStack = new VerticalStack();
		final DynamicForm newProductForm = new DynamicForm();
		this.newProductTextItem = new TextItem("productName", "Product name");
		this.newProductTextItem.setWidth(CENTER_WIDTH-85);
		this.newProductTextItem.setWrapTitle(false);
		newProductForm.setItems(newProductTextItem);
		final HLine line5 = new HLine(CENTER_WIDTH);
		final HTMLPane initialVariantDefinition = new HTMLPane();
		initialVariantDefinition.setContents("<b><u>Initial variant definition:</u></b>");
		initialVariantDefinition.setWidth(150);
		initialVariantDefinition.setHeight(15);
		final HorizontalStack newProductHStack = new HorizontalStack();
		newProductHStack.setMembersMargin(5);
		final VerticalStack newProductLeftStack = new VerticalStack();
		final VerticalStack newProductNewOperationStack = new VerticalStack();
		newProductNewOperationStack.setIsGroup(true);
		newProductNewOperationStack.setGroupTitle("Operation definition");
		final DynamicForm newProductLeftForm1 = new DynamicForm();
		this.newOperationInNewProductName = new TextItem("operationName", "Name");
		this.newOperationInNewProductName.setWrapTitle(false);
		this.newOperationInNewProductName.setWidth(100);
		newProductLeftForm1.setItems(newOperationInNewProductName);
		this.resourceListGrifForNewProduct = new ResourceChooserListGrid(200, 135);
		final DynamicForm newProductLeftForm2 = new DynamicForm();
		this.newOperationInNewProductDurationSpinner = new SpinnerItem("Duration");
		this.newOperationInNewProductDurationSpinner.setHint("In&nbsp;min.");
		this.newOperationInNewProductDurationSpinner.setWrapTitle(false);
		this.newOperationInNewProductDurationSpinner.setWidth(100);
		this.newOperationInNewProductDurationSpinner.setValue(0);
		newProductLeftForm2.setItems(newOperationInNewProductDurationSpinner);
		newProductNewOperationStack.addMembers(newProductLeftForm1, resourceListGrifForNewProduct, newProductLeftForm2);
		newProductLeftStack.addMembers(initialVariantDefinition, newProductNewOperationStack);
		this.newProductArrow = new TransferImgButton(TransferImgButton.RIGHT);
		final SectionStack newOperationInNewProductRightListGridStack = new SectionStack();
		newOperationInNewProductRightListGridStack.setWidth(200);
		newOperationInNewProductRightListGridStack.setHeight(225);
		final SectionStackSection newOperationInNewProductRightListGridStackSection = new SectionStackSection("Operations in initial Variant");
		newOperationInNewProductRightListGridStackSection.setCanCollapse(false);
		newOperationInNewProductRightListGridStackSection.setExpanded(true);
		this.newProductRightListGrid = new OperationsInNewVariantListGrid();
		newOperationInNewProductRightListGridStackSection.addItem(newProductRightListGrid);
		newOperationInNewProductRightListGridStack.setSections(newOperationInNewProductRightListGridStackSection);
		newProductHStack.addMembers(newProductLeftStack, newProductArrow, newOperationInNewProductRightListGridStack);
		this.newProductStack.addMembers(newProductForm, line5, newProductHStack);
		this.newProductStack.hide();

		/*
		 * Variabler Bereich: NewVariantEvent
		 */
		this.newVariantStack = new VerticalStack();
		final HorizontalStack newVariantHStack = new HorizontalStack();
		newVariantHStack.setMembersMargin(5);
		final VerticalStack newVariantLeftStack = new VerticalStack();
		final DynamicForm newVariantProductChooserForm = new DynamicForm();
		this.newVariantProductChooser = new SelectItem();
		this.newVariantProductChooser.setTitle("Select product"); 
		this.newVariantProductChooser.setType("comboBox");
		this.newVariantProductChooser.setWrapTitle(false);
		this.newVariantProductChooser.setWidth(CENTER_WIDTH - 320);
		newVariantProductChooserForm.setItems(newVariantProductChooser);
		final VerticalStack newVariantLeftStack2 = new VerticalStack();
		newVariantLeftStack2.setIsGroup(true);
		newVariantLeftStack2.setGroupTitle("Operation definition");
		final DynamicForm newVariantLeftForm1 = new DynamicForm();
		this.newOperationName = new TextItem("operationName", "Name");
		this.newOperationName.setWrapTitle(false);
		newVariantLeftForm1.setItems(newOperationName);
		this.resourceListGridForNewVariant = new ResourceChooserListGrid(200, 160);
		final DynamicForm newVariantLeftForm2 = new DynamicForm();
		this.newOperationDurationSpinner = new SpinnerItem("Duration");
		this.newOperationDurationSpinner.setHint("In&nbsp;min.");
		this.newOperationDurationSpinner.setWidth(100);
		this.newOperationDurationSpinner.setValue(0);
		newVariantLeftForm2.setItems(newOperationDurationSpinner);
		newVariantLeftStack2.addMembers(newVariantLeftForm1, resourceListGridForNewVariant, newVariantLeftForm2);
		newVariantLeftStack.addMembers(newVariantProductChooserForm, newVariantLeftStack2);
		this.newVariantArrow = new TransferImgButton(TransferImgButton.RIGHT);
		final SectionStack newOperationRightListGridStack = new SectionStack();
		newOperationRightListGridStack.setWidth(200);
		newOperationRightListGridStack.setHeight(255);
		final SectionStackSection newOperationRightListGridStackSection = new SectionStackSection("Operations in new variant");
		newOperationRightListGridStackSection.setCanCollapse(false);
		newOperationRightListGridStackSection.setExpanded(true);
		this.newVariantRightListGrid = new OperationsInNewVariantListGrid();
		newOperationRightListGridStackSection.addItem(newVariantRightListGrid);
		newOperationRightListGridStack.setSections(newOperationRightListGridStackSection);
		newVariantHStack.addMembers(newVariantLeftStack, newVariantArrow, newOperationRightListGridStack);
		this.newVariantStack.addMembers(newVariantHStack);
		this.newVariantStack.hide();

		/*
		 * Variabler Bereich: NewResourceEvent
		 */
		this.newResourceStack = new VerticalStack();
		final DynamicForm newResourceForm = new DynamicForm();
		this.newResourceTextItem = new TextItem("resourceName", "Resource name");
		this.newResourceTextItem.setWidth(CENTER_WIDTH-85);
		this.newResourceTextItem.setWrapTitle(false);
		newResourceForm.setItems(newResourceTextItem);
		this.resourceAwailabilitySlider2 = new Slider("Value");
		this.resourceAwailabilitySlider2.setVertical(false);
		this.resourceAwailabilitySlider2.setWidth(CENTER_WIDTH);
		this.resourceAwailabilitySlider2.setValue(100);
		this.newResourceStack.addMembers(newResourceForm, resourceAwailabilitySlider2);
		this.newResourceStack.hide();

		/*
		 * Variabler Bereich: ChengeOrderDueTimeEvent
		 */
		this.changeOrderDueStack = new VerticalStack();
		final DynamicForm changeOrderDueForm1 = new DynamicForm();
		this.orderChooserForDueTimeComboBoxItem = new SelectItem();  
		this.orderChooserForDueTimeComboBoxItem.setTitle("Select order"); 
		this.orderChooserForDueTimeComboBoxItem.setType("comboBox");
		this.orderChooserForDueTimeComboBoxItem.setWrapTitle(false);
		this.orderChooserForDueTimeComboBoxItem.setWidth(CENTER_WIDTH - 75);
		changeOrderDueForm1.setItems(orderChooserForDueTimeComboBoxItem);
		final HorizontalStack changeOrderDueStack2 = new HorizontalStack();
		final DynamicForm changOrderDueForm2 = new DynamicForm();
		this.orderDueDateItem = new DateItem();  
		this.orderDueDateItem.setTitle("Due date");
		this.orderDueDateItem.setWrapTitle(false);
		this.orderDueDateItem.setDisabled(true);
		changOrderDueForm2.setItems(orderDueDateItem);
		this.orderDueTimeField = new TimeField("&nbsp;&nbsp;Time");
		this.orderDueTimeField.setDisabled(true);
		changeOrderDueStack2.addMembers(changOrderDueForm2, orderDueTimeField);
		this.changeOrderDueStack.addMembers(changeOrderDueForm1, changeOrderDueStack2);
		this.changeOrderDueStack.hide();

		/*
		 * Variabler Bereich: ChangeOrderProductEvent
		 */
		this.changeOrderProductStack = new VerticalStack();
		this.changeOrderProductForm = new DynamicForm();
		this.orderChooserForProductChange = new SelectItem();  
		this.orderChooserForProductChange.setTitle("Select order"); 
		this.orderChooserForProductChange.setType("comboBox");
		this.orderChooserForProductChange.setWrapTitle(false);
		this.orderChooserForProductChange.setWidth(CENTER_WIDTH - 75);
		final HorizontalStack hStack = new HorizontalStack();
		hStack.setMembersMargin(5);
		final SectionStack productChangeLeftListGridStack = new SectionStack();
		productChangeLeftListGridStack.setWidth(200);
		productChangeLeftListGridStack.setHeight(240);
		final SectionStackSection productChangeLeftListGridStackSection = new SectionStackSection("Products in order");
		productChangeLeftListGridStackSection.setCanCollapse(false);
		productChangeLeftListGridStackSection.setExpanded(true);
		this.changeOrderProductLeftListGrid = new OrderChangeLeftListGrid();
		productChangeLeftListGridStackSection.addItem(changeOrderProductLeftListGrid);
		productChangeLeftListGridStack.setSections(productChangeLeftListGridStackSection);
		final VerticalStack arrowStack = new VerticalStack();
		arrowStack.setAutoWidth();
		final HTMLPane arrowStackSpace = new HTMLPane();
		arrowStackSpace.setContents("");
		arrowStackSpace.setHeight(100);
		this.changeOrderProductArrowRight = new TransferImgButton(TransferImgButton.RIGHT);
		this.changeOrderProductArrowRight.setDisabled(true);
		this.changeOrderProductArrowLeft = new TransferImgButton(TransferImgButton.LEFT);
		this.changeOrderProductArrowLeft.setDisabled(true);
		arrowStack.addMembers(arrowStackSpace, changeOrderProductArrowRight, changeOrderProductArrowLeft);
		final SectionStack productChangeRightListGridStack = new SectionStack();
		productChangeRightListGridStack.setWidth(200);
		productChangeRightListGridStack.setHeight(240);
		final SectionStackSection productChangeRightListGridStackSection = new SectionStackSection("Deleted products");
		productChangeRightListGridStackSection.setCanCollapse(false);
		productChangeRightListGridStackSection.setExpanded(true);
		this.changeOrderProductRightListGrid = new OrderChangeRightListGrid();
		productChangeRightListGridStackSection.addItem(changeOrderProductRightListGrid);
		productChangeRightListGridStack.setSections(productChangeRightListGridStackSection);
		hStack.addMembers(productChangeLeftListGridStack, arrowStack, productChangeRightListGridStack);
		this.changeOrderProductForm.setItems(orderChooserForProductChange);
		this.changeOrderProductStack.addMembers(changeOrderProductForm, hStack);
		this.changeOrderProductStack.hide();

		/*
		 * Variabler Bereich: ChangeOrderPriorityEvent
		 */
		this.changeOrderPriorityStack = new HorizontalStack();
		final DynamicForm changeOrderPriorityOrderChooserForm = new DynamicForm();
		this.orderChooserForPriorityComboBoxItem = new SelectItem();  
		this.orderChooserForPriorityComboBoxItem.setTitle("Select order"); 
		this.orderChooserForPriorityComboBoxItem.setType("comboBox");
		this.orderChooserForPriorityComboBoxItem.setWrapTitle(false);
		this.orderChooserForPriorityComboBoxItem.setWidth(175);
		changeOrderPriorityOrderChooserForm.setItems(orderChooserForPriorityComboBoxItem);
		final DynamicForm changeOrderPriorityForm = new DynamicForm();
		this.priorityChooser = new SelectItem();  
		this.priorityChooser.setTitle("Select priority"); 
		this.priorityChooser.setType("comboBox");
		this.priorityChooser.setWrapTitle(false);
		this.priorityChooser.setWidth(100);
		this.priorityChooser.setDisabled(true);
		this.priorityChooser.setValue(priorityMap.get("1"));
		//proprityMap aus newOrder
		this.priorityChooser.setValueMap(priorityMap);
		changeOrderPriorityForm.setItems(priorityChooser);
		this.changeOrderPriorityStack.addMembers(changeOrderPriorityOrderChooserForm, changeOrderPriorityForm);
		this.changeOrderPriorityStack.hide();

		/*
		 * Variabler Bereich: ChangeOperationEvent
		 */
		this.changeOperationResourcesStack = new HorizontalStack();
		final DynamicForm changeOperationLeftForm = new DynamicForm();
		this.productChooserForOperationChangeComboBoxItem = new SelectItem();
		this.productChooserForOperationChangeComboBoxItem.setTitle("Select product"); 
		this.productChooserForOperationChangeComboBoxItem.setType("comboBox");
		this.productChooserForOperationChangeComboBoxItem.setWrapTitle(false);
		this.productChooserForOperationChangeComboBoxItem.setWidth(150);
		this.variantChooserForOperationChange = new SelectItem();
		this.variantChooserForOperationChange.setTitle("Select variant"); 
		this.variantChooserForOperationChange.setType("comboBox");
		this.variantChooserForOperationChange.setWrapTitle(false);
		this.variantChooserForOperationChange.setWidth(150);
		this.variantChooserForOperationChange.setDisabled(true);
		this.operationChooserForChange = new SelectItem();
		this.operationChooserForChange.setTitle("Select operation"); 
		this.operationChooserForChange.setType("comboBox");
		this.operationChooserForChange.setWrapTitle(false);
		this.operationChooserForChange.setWidth(150);
		this.operationChooserForChange.setDisabled(true);
		changeOperationLeftForm.setItems(productChooserForOperationChangeComboBoxItem, variantChooserForOperationChange, operationChooserForChange);
		final VerticalStack changeOperationRightStack = new VerticalStack();
		final DynamicForm changeOperationRightForm = new DynamicForm();
		this.durationSpinnerForOperationChange = new SpinnerItem("Duration");
		this.durationSpinnerForOperationChange.setHint("in&nbsp;min.");
		this.durationSpinnerForOperationChange.setWidth(100);
		this.durationSpinnerForOperationChange.setDisabled(true);
		this.durationSpinnerForOperationChange.setValue(0);
		final SectionStack orderChangeRightListGridStack = new SectionStack();
		orderChangeRightListGridStack.setWidth(200);
		orderChangeRightListGridStack.setHeight(240);
		final SectionStackSection orderChangeRightListGridStackSection = new SectionStackSection("Resources");
		orderChangeRightListGridStackSection.setCanCollapse(false);
		orderChangeRightListGridStackSection.setExpanded(true);
		this.resourceGridForOperationChange = new ResourceChooserListGrid();
		this.resourceGridForOperationChange.setDisabled(true);
		orderChangeRightListGridStackSection.addItem(resourceGridForOperationChange);
		orderChangeRightListGridStack.setSections(orderChangeRightListGridStackSection);
		changeOperationRightForm.setItems(durationSpinnerForOperationChange);
		changeOperationRightStack.addMembers(changeOperationRightForm, orderChangeRightListGridStack);
		this.changeOperationResourcesStack.addMembers(changeOperationLeftForm, changeOperationRightStack);
		this.changeOperationResourcesStack.hide();

		/*
		 * Variabler Bereich: RemoveOrderEvent
		 */
		this.removeOrderForm = new DynamicForm();
		this.orderChooserForRemovementComboBoxItem = new SelectItem();
		this.orderChooserForRemovementComboBoxItem.setTitle("Select order"); 
		this.orderChooserForRemovementComboBoxItem.setType("comboBox");
		this.orderChooserForRemovementComboBoxItem.setWrapTitle(false);
		this.orderChooserForRemovementComboBoxItem.setWidth(CENTER_WIDTH - 75);
		this.removeOrderForm.setItems(orderChooserForRemovementComboBoxItem);
		this.removeOrderForm.hide();

		/*
		 * Variabler Bereich: RemoveProductEvent
		 */
		this.removeProductForm = new DynamicForm();
		this.productChooserForRemovementComboBoxItem = new SelectItem();
		this.productChooserForRemovementComboBoxItem.setTitle("Select product"); 
		this.productChooserForRemovementComboBoxItem.setType("comboBox");
		this.productChooserForRemovementComboBoxItem.setWrapTitle(false);
		this.productChooserForRemovementComboBoxItem.setWidth(CENTER_WIDTH - 75);
		this.removeProductForm.setItems(productChooserForRemovementComboBoxItem);
		this.removeProductForm.hide();

		/*
		 * Variabler Bereich: RemoveVariantEvent
		 */
		this.removeVariantForm = new DynamicForm();
		this.productChooserForVariantRemovementComboBoxItem = new SelectItem();
		this.productChooserForVariantRemovementComboBoxItem.setTitle("Select product"); 
		this.productChooserForVariantRemovementComboBoxItem.setType("comboBox");
		this.productChooserForVariantRemovementComboBoxItem.setWrapTitle(false);
		this.productChooserForVariantRemovementComboBoxItem.setWidth(CENTER_WIDTH - 75);
		this.variantChooserForRemovement = new SelectItem();
		this.variantChooserForRemovement.setTitle("Select variant"); 
		this.variantChooserForRemovement.setType("comboBox");
		this.variantChooserForRemovement.setWrapTitle(false);
		this.variantChooserForRemovement.setWidth(CENTER_WIDTH - 75);
		this.variantChooserForRemovement.setDisabled(true);
		this.removeVariantForm.setItems(productChooserForVariantRemovementComboBoxItem, variantChooserForRemovement);
		this.removeVariantForm.hide();

		/*
		 * Variabler Bereich: MachineBreakdownEvent
		 */
		this.machineBreakdownStack = new VerticalStack();
		final DynamicForm machineBreakdownForm1 = new DynamicForm();
		this.resourceChooserForMachineBreakdown = new SelectItem();
		this.resourceChooserForMachineBreakdown.setTitle("Select resource"); 
		this.resourceChooserForMachineBreakdown.setType("comboBox");
		this.resourceChooserForMachineBreakdown.setWrapTitle(false);
		this.resourceChooserForMachineBreakdown.setWidth(CENTER_WIDTH - 100);
		machineBreakdownForm1.setItems(resourceChooserForMachineBreakdown);
		final HorizontalStack machineBreakdownStack2 = new HorizontalStack();
		final Label machineBreakdownNoteLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Note:</b> Breakdown time equals throw time");
		machineBreakdownNoteLabel.setWrap(false);
		machineBreakdownStack2.addMembers(machineBreakdownNoteLabel);
		machineBreakdownStack.addMembers(machineBreakdownForm1, machineBreakdownStack2);
		this.machineBreakdownStack.hide();

		/*
		 * Variabler Bereich: MachineRepairedEvent
		 */
		this.machineRepairedStack = new VerticalStack();
		final DynamicForm machineRepairedForm1 = new DynamicForm();
		this.resourceChooserForMachineRepair = new SelectItem();
		this.resourceChooserForMachineRepair.setTitle("Select resource"); 
		this.resourceChooserForMachineRepair.setType("comboBox");
		this.resourceChooserForMachineRepair.setWrapTitle(false);
		this.resourceChooserForMachineRepair.setWidth(CENTER_WIDTH - 100);
		machineRepairedForm1.setItems(resourceChooserForMachineRepair);
		final HorizontalStack machineRepairedStack2 = new HorizontalStack();
		final Label machineRepairedNoteLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Note:</b> New due time equals throw time");
		machineRepairedNoteLabel.setWrap(false);
		machineRepairedStack2.addMembers(machineRepairedNoteLabel);
		this.machineRepairedStack.addMembers(machineRepairedForm1, machineRepairedStack2);
		this.machineRepairedStack.hide();

		/*
		 * Variabler Bereich: MaintenacePeriodEvent
		 */
		this.maintenacePeriodStack = new VerticalStack();
		final DynamicForm maintenacePeriodForm1 = new DynamicForm();
		this.resourceChooserForMaintainacePeriod = new SelectItem();
		this.resourceChooserForMaintainacePeriod.setTitle("Select resource"); 
		this.resourceChooserForMaintainacePeriod.setType("comboBox");
		this.resourceChooserForMaintainacePeriod.setWrapTitle(false);
		this.resourceChooserForMaintainacePeriod.setWidth(CENTER_WIDTH - 130);
		maintenacePeriodForm1.setItems(resourceChooserForMaintainacePeriod);
		final HorizontalStack maintenacePeriodStack2 = new HorizontalStack();
		maintenacePeriodStack2.setAutoHeight();
		final DynamicForm maintenacePeriodForm2 = new DynamicForm();
		this.maintenaceStartDateItem = new DateItem();  
		this.maintenaceStartDateItem.setTitle("Maintenace begin date");
		this.maintenaceStartDateItem.setWrapTitle(false);
		this.maintenaceStartDateItem.setDisabled(true);
		maintenacePeriodForm2.setItems(maintenaceStartDateItem);
		this.maintenaceStartTimeField = new TimeField("&nbsp;&nbsp;Time");
		this.maintenaceStartTimeField.setDisabled(true);
		maintenacePeriodStack2.addMembers(maintenacePeriodForm2, maintenaceStartTimeField);
		final DynamicForm maintenacePeriodForm3 = new DynamicForm();
		final HorizontalStack maintenacePeriodStack3 = new HorizontalStack();
		maintenacePeriodStack3.setAutoHeight();
		this.maintenaceDueDateItem = new DateItem();  
		this.maintenaceDueDateItem.setTitle("Maintenace due date");
		this.maintenaceDueDateItem.setWrapTitle(false);
		this.maintenaceDueDateItem.setDisabled(true);
		maintenacePeriodForm3.setItems(maintenaceDueDateItem);
		this.maintenaceDueTimeField = new TimeField("&nbsp;&nbsp;Time");
		this.maintenaceDueTimeField.setDisabled(true);
		maintenacePeriodStack3.addMembers(maintenacePeriodForm3, maintenaceDueTimeField);
		this.maintenacePeriodStack.addMembers(maintenacePeriodForm1, maintenacePeriodStack2, maintenacePeriodStack3);
		this.maintenacePeriodStack.hide();

		/*
		 * Variabler Bereich: ChangeResourceAvaiabilityEvent
		 */
		this.changeResourceAvaiabilityStack = new VerticalStack();
		this.changeResourceAvaiabilityForm = new DynamicForm();
		this.resourceChooserForAvaiabilityComboBoxItem = new SelectItem();
		this.resourceChooserForAvaiabilityComboBoxItem.setTitle("Select resource"); 
		this.resourceChooserForAvaiabilityComboBoxItem.setType("comboBox");
		this.resourceChooserForAvaiabilityComboBoxItem.setWrapTitle(false);
		this.resourceChooserForAvaiabilityComboBoxItem.setWidth(CENTER_WIDTH - 95);
		this.resourceAvaiabilitySlider = new Slider("Value");
		this.resourceAvaiabilitySlider.setVertical(false);
		this.resourceAvaiabilitySlider.setWidth(CENTER_WIDTH);
		this.resourceAvaiabilitySlider.setDisabled(true);
		this.changeResourceAvaiabilityForm.setItems(resourceChooserForAvaiabilityComboBoxItem);
		this.changeResourceAvaiabilityStack.addMembers(changeResourceAvaiabilityForm, resourceAvaiabilitySlider);
		this.changeResourceAvaiabilityStack.hide();

		/*
		 * Hinzufügen der Bereiche (anfangs alle hidden)
		 */
		this.specialInputCanvas.addChild(newOrderStack);
		this.specialInputCanvas.addChild(newProductStack);
		this.specialInputCanvas.addChild(newVariantStack);
		this.specialInputCanvas.addChild(newResourceStack);
		this.specialInputCanvas.addChild(changeOrderDueStack);
		this.specialInputCanvas.addChild(changeOrderProductStack);
		this.specialInputCanvas.addChild(changeOrderPriorityStack);
		this.specialInputCanvas.addChild(changeResourceAvaiabilityStack);
		this.specialInputCanvas.addChild(removeOrderForm);
		this.specialInputCanvas.addChild(removeProductForm);
		this.specialInputCanvas.addChild(removeVariantForm);
		this.specialInputCanvas.addChild(machineBreakdownStack);
		this.specialInputCanvas.addChild(machineRepairedStack);
		this.specialInputCanvas.addChild(maintenacePeriodStack);
		this.specialInputCanvas.addChild(changeOperationResourcesStack);
	}
	
	/**
	 * Setzt den variablen Bereich neu
	 * @param eventType Typ als int
	 */
	public void setSpecialInputCanvas(final int eventType) {
		/*
		 * Bereich verbergen (Animation)
		 */
		specialInputCanvas.animateFade(0, new AnimationCallback() {
			@Override
			public void execute(boolean earlyFinish) {
				/*
				 * Alle Bereiche verbergen
				 */
				for(int i=0; i<specialInputCanvas.getChildren().length; i++) {
					specialInputCanvas.getChildren()[i].hide();
				}
				
				/*
				 * Fallunterscheidung nach Eventtyp
				 */
				switch(eventType) {
				case InvokeEventView.NO_SELECTION:
					noSelectionLabel.show();
					break;
				case ScheduleEvent.TYPE_NEW_ORDERS_EVENT: 
					newOrderStack.show();
					break;
				case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT: 
					newProductStack.show();
					break;
				case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT: 
					newVariantStack.show();
					break;
				case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:
					newResourceStack.show();
					break;
				case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT: 
					changeOrderDueStack.show();
					break;
				case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT: 
					changeOrderPriorityStack.show();
					break;
				case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT: 
					changeOrderProductStack.show();
					break;
				case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
					changeOperationResourcesStack.show();
					break;
				case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT: 
					changeResourceAvaiabilityStack.show();
					break;
				case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT: 
					removeOrderForm.show();
					break;
				case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT: 
					removeProductForm.show();
					break;
				case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT: 
					removeVariantForm.show();
					break;
				case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT: 
					machineBreakdownStack.show();
					break;
				case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
					machineRepairedStack.show();
					break;
				case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT: 
					maintenacePeriodStack.show();
					break;
				}
				
				/*
				 * Zeigen (Animation)
				 */
				specialInputCanvas.animateFade(100, new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {}
				}, 200);
			}
		}, 200);
		
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Visualisiert die Details des übergebenen Records im DetailViewer
	 * @param record ein EventsToInvokeRecord
	 */
	public void visualizeEventDetails(EventsToInvokeRecord record) {
		this.eventDetailViewer.viewSelectedData(this.selectedEventsListGrid);
		
		/*
		 * Fallunterscheidung nach Eventtyp
		 */
		switch(record.getType()) {
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
	
	public EventTypeListGrid getEventTypeListGrid() {
		return this.eventTypeListGrid;
	}
	
	public SelectedEventsListGrid getSelectedEventsListGrid() {
		return this.selectedEventsListGrid;
	}
	
	public Window getDescriptionWindow() {
		return this.description;
	}
	
	public HTMLPane getDescriptionPane() {
		return this.descriptionPane;
	}
	
	public Button getIncludeButton() {
		return this.includeButton;
	}
	
	public CheckboxItem getThrowImmediatelyCheckboxItem() {
		return this.throwImmediatelyCheckboxItem;
	}
	
	public DateItem getThrowDateItem() {
		return this.throwDateItem;
	}
	
	public TimeField getThrowTimeField() {
		return this.throwTimeField;
	}
	
	public SelectItem getOrderChooserForDueTimeComboBoxItem() {
		return this.orderChooserForDueTimeComboBoxItem;
	}
	
	public SelectItem getOrderChooserForPriorityComboBoxItem() {
		return this.orderChooserForPriorityComboBoxItem;
	}
	
	public SelectItem getOrderChooserForRemovement() {
		return this.orderChooserForRemovementComboBoxItem;
	}

	public SelectItem getResourceChooserForAvaiabilityComboBoxItem() {
		return resourceChooserForAvaiabilityComboBoxItem;
	}

	public SelectItem getProductChooserForRemovement() {
		return productChooserForRemovementComboBoxItem;
	}

	public Button getInvokeButton() {
		return invokeButton;
	}

	public SelectItem getPriorityChooser() {
		return priorityChooser;
	}

	public SelectItem getOrderChooserForRemovementComboBoxItem() {
		return orderChooserForRemovementComboBoxItem;
	}

	public SelectItem getProductChooserForRemovementComboBoxItem() {
		return productChooserForRemovementComboBoxItem;
	}

	public DateItem getOrderDueDateItem() {
		return orderDueDateItem;
	}

	public TimeField getOrderDueTimeField() {
		return orderDueTimeField;
	}

	public Slider getResourceAwailabilitySlider() {
		return resourceAvaiabilitySlider;
	}

	public SelectItem getProductChooserForVariantRemovementComboBoxItem() {
		return productChooserForVariantRemovementComboBoxItem;
	}

	public SelectItem getOrderChooserForProductChange() {
		return orderChooserForProductChange;
	}

	public SelectItem getVariantChooserForRemovement() {
		return variantChooserForRemovement;
	}

	public ListGrid getChangeOrderProductLeftListGrid() {
		return changeOrderProductLeftListGrid;
	}

	public ListGrid getChangeOrderProductRightListGrid() {
		return changeOrderProductRightListGrid;
	}

	public TransferImgButton getChangeOrderProductArrowRight() {
		return changeOrderProductArrowRight;
	}
	
	public TransferImgButton getChangeOrderProductArrowLeft() {
		return changeOrderProductArrowLeft;
	}
	
	public SelectItem getResourceChooserForMachineRepair() {
		return resourceChooserForMachineRepair;
	}

	public SelectItem getResourceChooserForMachineBreakdown() {
		return resourceChooserForMachineBreakdown;
	}

	public SelectItem getResourceChooserForMaintainacePeriod() {
		return resourceChooserForMaintainacePeriod;
	}

	public DateItem getMaintenaceStartDateItem() {
		return maintenaceStartDateItem;
	}

	public DateItem getMaintenaceDueDateItem() {
		return maintenaceDueDateItem;
	}

	public TimeField getMaintenaceStartTimeField() {
		return maintenaceStartTimeField;
	}

	public TimeField getMaintenaceDueTimeField() {
		return maintenaceDueTimeField;
	}

	public TextItem getNewOrderTextItem() {
		return newOrderTextItem;
	}

	public TextItem getNewProductTextItem() {
		return newProductTextItem;
	}

	public ListGrid getNewOrderLeftListGrid() {
		return newOrderLeftListGrid;
	}

	public ListGrid getNewOrderRightListGrid() {
		return newOrderRightListGrid;
	}

	public OperationsInNewVariantListGrid getNewProductRightListGrid() {
		return newProductRightListGrid;
	}

	public TransferImgButton getNewOrderArrow() {
		return newOrderArrow;
	}

	public TransferImgButton getNewProductArrow() {
		return newProductArrow;
	}

	public TextItem getNewOperationName() {
		return newOperationName;
	}

	public TextItem getNewOperationInNewProductName() {
		return newOperationInNewProductName;
	}

	public OperationsInNewVariantListGrid getNewVariantRightListGrid() {
		return newVariantRightListGrid;
	}

	public TransferImgButton getNewVariantArrow() {
		return newVariantArrow;
	}

	public SpinnerItem getNewOperationDurationSpinner() {
		return newOperationDurationSpinner;
	}

	public SpinnerItem getNewOperationInNewProductDurationSpinner() {
		return newOperationInNewProductDurationSpinner;
	}

	public DetailViewer getEventDetailViewer() {
		return eventDetailViewer;
	}

	public SelectItem getNewOrderPriorityChooser() {
		return newOrderPriorityChooser;
	}

	public DateItem getNewOrderDueDateItem() {
		return newOrderDueDateItem;
	}

	public TimeField getNewOrderDueTimeField() {
		return newOrderDueTimeField;
	}

	public SelectItem getNewVariantProductChooser() {
		return newVariantProductChooser;
	}

	public TextItem getNewResourceTextItem() {
		return newResourceTextItem;
	}

	public Slider getResourceAwailabilitySlider2() {
		return resourceAwailabilitySlider2;
	}

	public SelectItem getProductChooserForOperationChange() {
		return productChooserForOperationChangeComboBoxItem;
	}

	public SelectItem getVariantChooserForOperationChange() {
		return variantChooserForOperationChange;
	}

	public SelectItem getOperationChooserForChange() {
		return operationChooserForChange;
	}

	public ListGrid getResourceListGridForOperationChange() {
		return resourceGridForOperationChange;
	}

	public SpinnerItem getDurationSpinnerForOperationChange() {
		return durationSpinnerForOperationChange;
	}

	public ListGrid getResourceListGridForNewProduct() {
		return resourceListGrifForNewProduct;
	}

	public ListGrid getResourceListGridForNewVariant() {
		return resourceListGridForNewVariant;
	}

	public FileUploader getFileUpload() {
		return fileUpload;
	}
	
	public Button getSubmitButton() {
		return submitButton;
	}

	/**
	 * ListGrid für Eventtypen
	 * @author Dennis
	 */
	public class EventTypeListGrid extends ListGrid {
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		public EventTypeListGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);

			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			this.setSelectionType(SelectionStyle.SINGLE);

			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField name = new ListGridField("name", "name");
			final ListGridField category = new ListGridField("category");
			
			this.setGroupStartOpen(GroupStartOpen.ALL);  
			this.setGroupByField("category");  
			this.setFields(name, category);
			this.hideField("category");
		}
	}
	
	/**
	 * ListGrid für gewählte Events
	 * @author Dennis
	 */
	public class SelectedEventsListGrid extends ListGrid {
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		public SelectedEventsListGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
			this.setCanRemoveRecords(true);
			this.setSelectionType(SelectionStyle.SINGLE);
			
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			
			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField name = new ListGridField("name", "name");
			
			this.setFields(name);
		}
	}
	
	public class OperationContextMenu extends Menu {
		private MenuItem deleteOperation, predecessorsItem;
		private Menu predecessorsMenu;
		private OperationRecord record;
		private Map<String, Boolean> predecessorMap;
		
		public OperationContextMenu(final OperationsInNewVariantListGrid grid, final OperationRecord record, final Map<String, Boolean> predecessorMap) {
			this.record = record;
			this.predecessorMap = predecessorMap;
			
			predecessorsItem = new MenuItem("Predecessors");
			deleteOperation = new MenuItem("Remove operation");
			predecessorsMenu = new Menu();
			MenuItem predecessorItem;
			for(String predecessorName : predecessorMap.keySet()) {
				predecessorItem = new MenuItem(predecessorName);
				predecessorItem.setChecked(predecessorMap.get(predecessorName));
				predecessorsMenu.addItem(predecessorItem);
			}
			predecessorsItem.setSubmenu(predecessorsMenu);
			this.addItem(predecessorsItem);
			this.addItem(deleteOperation);
		}
		
		public MenuItem getDeleteOperation() {
			return deleteOperation;
		}

		public MenuItem getPredecessorsItem() {
			return predecessorsItem;
		}

		public Menu getPredecessorsMenu() {
			return predecessorsMenu;
		}

		public OperationRecord getRecord() {
			return record;
		}

		public Map<String, Boolean> getPredecessorMap() {
			return predecessorMap;
		}
	}
	
	/**
	 * ListGrid für Operationen
	 * @author Dennis
	 */
	public class OperationsInNewVariantListGrid extends ListGrid {
		final Map<String, Map<String, Boolean>> predecessorMap;
		
		/**
		 * Konstruktor
		 */
		public OperationsInNewVariantListGrid() {
			this.setSelectionType(SelectionStyle.NONE);
			this.setCanAcceptDroppedRecords(true); 
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowHeader(false);
			this.setBorder("0px");
			final ListGridField newVariantRightListGridNameItem = new ListGridField("name", "Name");
			final ListGridField newVariantRightListGridIdItem = new ListGridField("id");
			newVariantRightListGridIdItem.setHidden(true);
			this.setFields(newVariantRightListGridNameItem, newVariantRightListGridIdItem);
			
			predecessorMap = new HashMap<String, Map<String, Boolean>>();
		}
		
		@Override
		public void addData(Record record) {
			Map<String, Boolean> tempMap = new HashMap<String, Boolean>();
			for(String key : predecessorMap.keySet()) {
				tempMap.put(key, false);
			}
			predecessorMap.put(((OperationRecord)record).getName(), tempMap);
			
			super.addData(record);
		}
		
		public void remove(OperationRecord record) {
			this.removeData(record);
			for(String key : predecessorMap.keySet()) {
				predecessorMap.get(key).remove(record.getName());
			}
		}
		
		public Map<String, Map<String, Boolean>> getPredecessorMap() {
			return predecessorMap;
		}
	}
	
	/**
	 * Linkes ListGrid bei newOrderEvent
	 * @author Dennis
	 */
	public class NewOrderLeftListGrid extends ListGrid {
		/**
		 * Konstruktor
		 */
		public NewOrderLeftListGrid() {
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setCanDragRecordsOut(true);  
			this.setDragDataAction(DragDataAction.COPY);
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowHeader(false);
			this.setBorder("0px");
			final ListGridField newOrderLeftListGridNameItem = new ListGridField("name", "Name");
			final ListGridField newOrderLeftListGridIdItem = new ListGridField("id");
			newOrderLeftListGridIdItem.setHidden(true);
			this.setFields(newOrderLeftListGridNameItem, newOrderLeftListGridIdItem);
		}
	}
	
	/**
	 * Rechtes ListGrid bei newOrderEvent
	 * @author Dennis
	 */
	public class NewOrderRightListGrid extends ListGrid {
		/**
		 * Konstruktor
		 */
		public NewOrderRightListGrid() {
			this.setSelectionType(SelectionStyle.NONE);
			this.setCanRemoveRecords(true);
			this.setCanAcceptDroppedRecords(true); 
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowHeader(false);
			this.setBorder("0px");
			final ListGridField newOrderRightListGridNameItem = new ListGridField("name", "Name");
			final ListGridField newOrderRightListGridIdItem = new ListGridField("id");
			newOrderRightListGridIdItem.setHidden(true);
			this.setFields(newOrderRightListGridNameItem, newOrderRightListGridIdItem);
		}
	}
	
	/**
	 * Linkes ListGrid bei ChangeOrderEvent
	 * @author Dennis
	 */
	public class OrderChangeLeftListGrid extends ListGrid {
		public OrderChangeLeftListGrid()  {
			this.setDisabled(true);
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setCanDragRecordsOut(true);  
			this.setDragDataAction(DragDataAction.MOVE); 
			this.setCanAcceptDroppedRecords(true); 
			this.setBorder("0px");
			this.setShowHeader(false);
			final ListGridField changeOrderProductLeftListGridNameItem = new ListGridField("name", "Name");
			final ListGridField changeOrderProductLeftListGridIdItem = new ListGridField("id");
			changeOrderProductLeftListGridIdItem.setHidden(true);
			this.setFields(changeOrderProductLeftListGridIdItem, changeOrderProductLeftListGridNameItem);
		}
	}
	
	/**
	 * Rechtes ListGrid bei ChangeOrderEvent
	 * @author Dennis
	 */
	public class OrderChangeRightListGrid extends ListGrid {
		public OrderChangeRightListGrid() {
			this.setDisabled(true);
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setDragDataAction(DragDataAction.MOVE); 
			this.setCanDragRecordsOut(true);  
			this.setCanAcceptDroppedRecords(true);
			this.setBorder("0px");
			this.setShowHeader(false);
			final ListGridField changeOrderProductRightListGridNameItem = new ListGridField("name", "Deleted products");
			final ListGridField changeOrderProductRightListGridIdItem = new ListGridField("id");
			changeOrderProductRightListGridIdItem.setHidden(true);
			this.setFields(changeOrderProductRightListGridIdItem, changeOrderProductRightListGridNameItem);
		}
	}
	
	/**
	 * ListGrid für Ressourcen
	 * @author Dennis
	 */
	public class ResourceChooserListGrid extends ListGrid {
		public ResourceChooserListGrid(int width, int height) {
			this();
			this.setWidth(width);
			this.setHeight(height);
		}
		public ResourceChooserListGrid() {
			this.setBorder("0px");
			this.setShowHeader(false);
			this.setSelectionType(SelectionStyle.SIMPLE);
			final ListGridField resourceNameListGridField = new ListGridField("name", "Name");
			final ListGridField isSelected = new ListGridField("isSelected", "");
			isSelected.setType(ListGridFieldType.BOOLEAN);
			isSelected.setWidth(20);
			this.setFields(resourceNameListGridField, isSelected);
		}
	}

	@Override
	public void testingUpload() {
		this.uploadValidLabel.setBackgroundColor("#FF7");
		this.uploadValidLabel.setContents("Processing...");
	}

	@Override
	public void uploadFailed() {
		this.uploadValidLabel.setBackgroundColor("#977");
		this.uploadValidLabel.setContents("File corrupt.");
	}

	@Override
	public void uploadSuccessfull() {
		this.uploadValidLabel.setBackgroundColor("#797");
		this.uploadValidLabel.setContents("Success.");
	}
}
