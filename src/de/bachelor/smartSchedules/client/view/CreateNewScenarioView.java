package de.bachelor.smartSchedules.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Slider;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.presenter.InvokeEventPresenter.OperationRecord;
import de.bachelor.smartSchedules.client.view.InvokeEventView.OperationsInNewVariantListGrid;
import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.FileUploader;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;

/**
 * View für Erstellung eines neuen Scenarios
 * @author Dennis
 */
public class CreateNewScenarioView extends Canvas implements FileUploaderInterface {
	/**
	 * ListGrid für Resourcen
	 */
	private final ResourceListGrid resourceListGrid;
	
	/**
	 * ListGrid für Produkte
	 */
	private final ProductListGrid productListGrid;
	
	/**
	 * ListGrid für Operationen in neuem Produkt
	 */
	private final OperationListGrid newProductRightListGrid;
	
	/**
	 * ListGrid für Operationen in neuer Variante
	 */
	private final OperationListGrid newVariantRightListGrid;
	
	/**
	 * Buttons
	 */
	private final Button finishButton,
						 abortButton,
						 resetButton,
						 includeButton,
						 submitButton;
	
	/**
	 * Mittlerer (variabler) Bereich
	 */
	private final Canvas inputCanvas;
	
	/**
	 * Menüpunkte (Rechtsklick)
	 */
	private final MenuItem addResourceMenuItem,
						   addProductMenuItem,
						   addVariantMenuItem,
						   deleteResourceMenuItem,
						   deleteProductMenuItem;
	
	/**
	 * Variable Bereiche
	 */
	private final VerticalStack newResourceStack,
								newProductStack,
								newVariantStack;
	
	/**
	 * TextItems
	 */
	private final TextItem newResourceTextItem,
						   newProductTextItem,
						   newOperationName,
						   newOperationName2,
						   scenarioNameTextItem;
	
	/**
	 * Slider
	 */
	private final Slider resourceAwailabilitySlider;
	
	/**
	 * SelectItems
	 */
	private final SelectItem newOperationResourceChooser,
							 newVariantProductChooser,
							 newOperationResourceChooser2;
	
	/**
	 * Spinner
	 */
	private final SpinnerItem newOperationDurationSpinner,
							  newOperationDurationSpinner2;
	
	/**
	 * TransferButtons
	 */
	private final TransferImgButton newProductArrow,
									newVariantArrow;
	
	/**
	 * Label, wenn keine Selektion getroffen
	 */
	private final Label noSelectionLabel;
	
	/**
	 * DetailViweer
	 */
	private final DetailViewer resourceDetailViewer,
							   productDetailViewer;
	
	private final Label uploadValidLabel;
	
	private final FileUploader fileUpload;
	
	/**
	 * Größe
	 */
	public static final int WIDTH = 940,
							HEIGHT = 500,
							LEFT_STACK_WIDTH = 450,
							GRID_WIDTH = 200,
							GRID_HEIGHT = 397,
							BUTTON_WIDTH = (LEFT_STACK_WIDTH-10) /2,
							TOP_SPACE = 20,
							RESOURCE_DETAIL_HEIGHT = 50,
							PRODUCT_DETAIL_HEIGHT = 50,
							RESOURCE_GRID_HEIGHT = GRID_HEIGHT - RESOURCE_DETAIL_HEIGHT,
							PRODUCT_GRID_HEIGHT = GRID_HEIGHT - PRODUCT_DETAIL_HEIGHT;
	
	/**
	 * Flags für aktuelle Selektion
	 */
	public static final int NO_SELECTION = -1,
							NEW_RESOURCE = 0,
							NEW_PRODUCT = 1,
							NEW_VARIANT = 2;
	
	/**
	 * Konstruktor
	 */
	public CreateNewScenarioView() {
		/*
		 * Initialisierung
		 */
		this.setTitle("Create New Scenario");
		this.setTooltip("");
		
		/*
		 * Resourcen-Menü (Rechtsklick)
		 */
		final Menu resourceMenu = new Menu();
		resourceMenu.setWidth(150);
		this.addResourceMenuItem = new MenuItem("Add resource");
		final MenuItemSeparator seperator1 = new MenuItemSeparator();
		this.deleteResourceMenuItem = new MenuItem("Remove resource");
		resourceMenu.setItems(addResourceMenuItem, seperator1, deleteResourceMenuItem);
		
		/*
		 * Produkte-Menü (Rechtsklick)
		 */
		final Menu productMenu = new Menu();
		productMenu.setWidth(150);
		this.addProductMenuItem = new MenuItem("Add product");
		final MenuItemSeparator seperator2 = new MenuItemSeparator();
		this.addVariantMenuItem = new MenuItem("Add variant");
		this.deleteProductMenuItem = new MenuItem("Remove product");
		productMenu.setItems(addProductMenuItem, seperator2, addVariantMenuItem, deleteProductMenuItem);
		
		/*
		 * Hauptaufteilung
		 */
		final VerticalStack superMainStack = new VerticalStack();
		superMainStack.setTop(23);
		final HorizontalStack mainStack = new HorizontalStack(VerticalStack.STACK_TYPE_MAIN_STACK);
		
		/*
		 * Linke Seite
		 */
		final VerticalStack leftStack = new VerticalStack();
		/*
		 * Scenarioname
		 */
		final DynamicForm nameTextItemForm = new DynamicForm();
		this.scenarioNameTextItem = new TextItem("scenarioName", "Scenario name");
		this.scenarioNameTextItem.setWrapTitle(false);
		nameTextItemForm.setItems(scenarioNameTextItem);
		this.scenarioNameTextItem.setWidth(LEFT_STACK_WIDTH-100);
		final HLine line3 = new HLine(LEFT_STACK_WIDTH);

		/*
		 * Upload
		 */
		final HorizontalStack upperStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		this.submitButton = new Button("Submit", 80);
		this.uploadValidLabel = new Label("No upload yet.");
		this.uploadValidLabel.setAlign(Alignment.CENTER);
		this.uploadValidLabel.setWidth(100);
		this.uploadValidLabel.setHeight(25);
		this.uploadValidLabel.setBorder("1px solid black");
		this.uploadValidLabel.setBackgroundColor("#EEE");
		upperStack.setHeight(40);
		this.fileUpload = new FileUploader(this);
		upperStack.addMembers(fileUpload, uploadValidLabel, submitButton);
		final HLine line = new HLine(LEFT_STACK_WIDTH);
		/*
		 * Variabler Input
		 */
		this.inputCanvas = new Canvas();
		this.inputCanvas.setWidth(LEFT_STACK_WIDTH);
		noSelectionLabel = new Label("Please add resources to your scenario first, then start constructing the products.");
		noSelectionLabel.setWidth(LEFT_STACK_WIDTH);
		this.inputCanvas.addChild(noSelectionLabel);
		final HLine line2 = new HLine(LEFT_STACK_WIDTH);
		/*
		 * Buttons
		 */
		final HorizontalStack buttonStack = new HorizontalStack();
		buttonStack.setMembersMargin(8);
		this.resetButton = new Button("Reset", BUTTON_WIDTH);
		this.includeButton = new Button("Include", BUTTON_WIDTH);
		buttonStack.addMembers(resetButton, includeButton);
		buttonStack.setHeight(30);
		leftStack.addMembers(nameTextItemForm, line3, upperStack, line, inputCanvas, line2, buttonStack);
		
		/*
		 * Resourcen-Bereich (ListGrid, DetailViewer)
		 */
		final VerticalStack resourceStack = new VerticalStack();
		final SectionStack resourceSectionStack = new SectionStack();
		resourceSectionStack.setWidth(GRID_WIDTH);
		resourceSectionStack.setHeight(RESOURCE_GRID_HEIGHT);
		final SectionStackSection resourceListSection = new SectionStackSection("Resources");  
		resourceListSection.setCanCollapse(false);
		resourceListSection.setExpanded(true);
		this.resourceListGrid = new ResourceListGrid(GRID_WIDTH, RESOURCE_GRID_HEIGHT);
		this.resourceListGrid.setContextMenu(resourceMenu);
		resourceListSection.addItem(resourceListGrid);
		resourceSectionStack.setSections(resourceListSection);
		this.resourceDetailViewer = new DetailViewer();
		this.resourceDetailViewer.setWidth(GRID_WIDTH);
		this.resourceDetailViewer.setHeight(RESOURCE_DETAIL_HEIGHT);
		this.resourceDetailViewer.setFields(new DetailViewerField("name", "Name"),
											new DetailViewerField("availability", "Availability"));
		this.resourceDetailViewer.setEmptyMessage("Select a resource.");
		resourceStack.addMembers(resourceSectionStack, resourceDetailViewer);
		
		/*
		 * Produkte-Bereich (ListGrid, DetailViewer)
		 */
		final VerticalStack productStack = new VerticalStack();
		final SectionStack productSectionStack = new SectionStack();
		productSectionStack.setWidth(GRID_WIDTH);
		productSectionStack.setHeight(PRODUCT_GRID_HEIGHT);
		final SectionStackSection productListSection = new SectionStackSection("Products");  
		productListSection.setCanCollapse(false);
		productListSection.setExpanded(true);
		this.productListGrid = new ProductListGrid(GRID_WIDTH, PRODUCT_GRID_HEIGHT);
		this.productListGrid.setContextMenu(productMenu);
		productListSection.addItem(productListGrid);
		productSectionStack.setSections(productListSection);
		this.productDetailViewer = new DetailViewer();
		this.productDetailViewer.setWidth(GRID_WIDTH);
		this.productDetailViewer.setHeight(PRODUCT_DETAIL_HEIGHT);
		this.productDetailViewer.setFields(new DetailViewerField("name", "Name"),
				new DetailViewerField("availability", "Availability"));
		this.resourceDetailViewer.setEmptyMessage("Select a resource.");
		productStack.setMembers(productSectionStack, productDetailViewer);
		
		/*
		 * Untere Buttons
		 */
		final HorizontalStack buttonStack2 = new HorizontalStack();
		buttonStack2.setMembersMargin(15);
		this.abortButton = new Button("Abort", BUTTON_WIDTH);
		this.finishButton = new Button("Finish", BUTTON_WIDTH);
		buttonStack2.setWidth(WIDTH);
		buttonStack2.setAlign(Alignment.CENTER);
		buttonStack2.addMembers(abortButton, finishButton);
		
		/*
		 * Zufammenfügen
		 */
		mainStack.addMembers(leftStack, resourceStack, productStack);
		superMainStack.addMembers(mainStack, buttonStack2);
		this.addChild(superMainStack);
		
		/*
		 * Variabler Bereich: Neue Resource
		 */
		this.newResourceStack = new VerticalStack();
		final DynamicForm newResourceForm = new DynamicForm();
		this.newResourceTextItem = new TextItem("resourceName", "Resource name");
		this.newResourceTextItem.setWidth(LEFT_STACK_WIDTH-85);
		this.newResourceTextItem.setWrapTitle(false);
		newResourceForm.setItems(newResourceTextItem);
		this.resourceAwailabilitySlider = new Slider("Value");
		this.resourceAwailabilitySlider.setVertical(false);
		this.resourceAwailabilitySlider.setWidth(LEFT_STACK_WIDTH);
		this.resourceAwailabilitySlider.setValue(100);
		this.newResourceStack.addMembers(newResourceForm, resourceAwailabilitySlider);
		this.newResourceStack.hide();

		/*
		 * Variabler Bereich: Neues Produkt
		 */
		this.newProductStack = new VerticalStack();
		this.newProductStack.setMembersMargin(5);
		final DynamicForm newProductForm = new DynamicForm();
		this.newProductTextItem = new TextItem("productName", "Product name");
		this.newProductTextItem.setWidth(LEFT_STACK_WIDTH-85);
		this.newProductTextItem.setWrapTitle(false);
		newProductForm.setItems(newProductTextItem);
		final HTMLPane initialVariantDefinition = new HTMLPane();
		initialVariantDefinition.setContents("<b><u>Initial variant definition:</u></b>");
		initialVariantDefinition.setWidth(150);
		initialVariantDefinition.setHeight(55);
		final HorizontalStack newProductHStack = new HorizontalStack();
		newProductHStack.setMembersMargin(5);
		final VerticalStack newProductLeftStack = new VerticalStack();
		final DynamicForm newProductLeftForm = new DynamicForm();
		newProductLeftForm.setIsGroup(true);
		newProductLeftForm.setGroupTitle("Operation definition");
		this.newOperationName = new TextItem("operationName", "Name");
		this.newOperationName.setWrapTitle(false);
		this.newOperationName.setWidth(100);
		this.newOperationResourceChooser = new SelectItem();
		this.newOperationResourceChooser.setTitle("Resource"); 
		this.newOperationResourceChooser.setType("comboBox");
		this.newOperationResourceChooser.setWrapTitle(false);
		this.newOperationResourceChooser.setWidth(LEFT_STACK_WIDTH - 340);
		this.newOperationDurationSpinner = new SpinnerItem("Duration");
		this.newOperationDurationSpinner.setHint("In min.");
		this.newOperationDurationSpinner.setWrapTitle(false);
		this.newOperationDurationSpinner.setWidth(50);
		this.newOperationDurationSpinner.setValue(0);
		newProductLeftForm.setItems(newOperationName, newOperationResourceChooser, newOperationDurationSpinner);
		newProductLeftStack.addMembers(initialVariantDefinition, newProductLeftForm);
		this.newProductArrow = new TransferImgButton(TransferImgButton.RIGHT);
		final SectionStack operationSectionStack = new SectionStack();
		operationSectionStack.setWidth(210);
		operationSectionStack.setHeight(215);
		final SectionStackSection operationListSection = new SectionStackSection("Products");  
		operationListSection.setCanCollapse(false);
		operationListSection.setExpanded(true);
		this.newProductRightListGrid = new OperationListGrid(); 
		operationListSection.addItem(newProductRightListGrid);
		operationSectionStack.setSections(operationListSection);
		final ListGridField newProductRightListGridNameItem = new ListGridField("name", "Operations in initial Variant");
		newProductRightListGridNameItem.setCanSort(false);
		final ListGridField newProductRightListGridIdItem = new ListGridField("id");
		newProductRightListGridIdItem.setHidden(true);
		this.newProductRightListGrid.setFields(newProductRightListGridNameItem, newProductRightListGridIdItem);
		newProductHStack.addMembers(newProductLeftStack, newProductArrow, operationSectionStack);
		this.newProductStack.addMembers(newProductForm, newProductHStack);
		this.newProductStack.hide();

		/*
		 * Variabler Bereich: Neue Variante
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
		this.newVariantProductChooser.setWidth(LEFT_STACK_WIDTH - 310);
		newVariantProductChooserForm.setItems(newVariantProductChooser);
		final HTMLPane newVariantSpaceHTMLPane = new HTMLPane();
		newVariantSpaceHTMLPane.setHeight(50);
		final DynamicForm newVariantLeftForm = new DynamicForm();
		newVariantLeftForm.setIsGroup(true);
		newVariantLeftForm.setGroupTitle("Operation definition");
		this.newOperationName2 = new TextItem("operationName", "Name");
		this.newOperationName2.setWidth(90);
		this.newOperationName2.setWrapTitle(false);
		this.newOperationResourceChooser2 = new SelectItem();
		this.newOperationResourceChooser2.setTitle("Resource"); 
		this.newOperationResourceChooser2.setType("comboBox");
		this.newOperationResourceChooser2.setWrapTitle(false);
		this.newOperationResourceChooser2.setWidth(LEFT_STACK_WIDTH - 325);
		this.newOperationDurationSpinner2 = new SpinnerItem("Duration");
		this.newOperationDurationSpinner2.setHint("In min.");
		this.newOperationDurationSpinner2.setWidth(90);
		this.newOperationDurationSpinner2.setValue(0);
		newVariantLeftForm.setItems(newOperationName2, newOperationResourceChooser2, newOperationDurationSpinner2);
		newVariantLeftStack.addMembers(newVariantProductChooserForm, newVariantSpaceHTMLPane, newVariantLeftForm);
		this.newVariantArrow = new TransferImgButton(TransferImgButton.RIGHT);
		final SectionStack operationSectionStack2 = new SectionStack();
		operationSectionStack2.setWidth(190);
		operationSectionStack2.setHeight(215);
		final SectionStackSection operationListSection2 = new SectionStackSection("Products");  
		operationListSection2.setCanCollapse(false);
		operationListSection2.setExpanded(true);
		this.newVariantRightListGrid = new OperationListGrid(); 
		operationListSection2.addItem(newVariantRightListGrid);
		operationSectionStack2.setSections(operationListSection2);
		final ListGridField newVariantRightListGridNameItem = new ListGridField("name", "Operations in new variant");
		newVariantRightListGridNameItem.setCanSort(false);
		final ListGridField newVariantRightListGridIdItem = new ListGridField("id");
		newVariantRightListGridIdItem.setHidden(true);
		this.newVariantRightListGrid.setFields(newVariantRightListGridNameItem, newVariantRightListGridIdItem);
		newVariantHStack.addMembers(newVariantLeftStack, newVariantArrow, operationSectionStack2);
		this.newVariantStack.addMembers(newVariantHStack);
		this.newVariantStack.hide();
		
		/*
		 * Einfügen (Bereiche sind anfangs hidden)
		 */
		this.inputCanvas.addChild(newResourceStack);
		this.inputCanvas.addChild(newProductStack);
		this.inputCanvas.addChild(newVariantStack);
	}
	
	/**
	 * Setzt den variablen Bereich
	 * @param type
	 */
	public void setInputCanvas(final int type) {
		/*
		 * Bereich abblenden
		 */
		inputCanvas.animateFade(0, new AnimationCallback() {
			@Override
			public void execute(boolean earlyFinish) {
				/*
				 * Alle Bereiche verbergen
				 */
				for(int i=0; i<inputCanvas.getChildren().length; i++) {
					inputCanvas.getChildren()[i].hide();
				}
				
				/*
				 * Gewünschten Bereich zeigen
				 */
				switch(type) {
				case CreateNewScenarioView.NO_SELECTION:
					noSelectionLabel.show();
					break;
				case CreateNewScenarioView.NEW_RESOURCE: 
					newResourceStack.show();
					break;
				case CreateNewScenarioView.NEW_PRODUCT: 
					newProductStack.show();
					break;
				case CreateNewScenarioView.NEW_VARIANT: 
					newVariantStack.show();
					break;
				}
				
				/*
				 * Bereich aufblenden
				 */
				inputCanvas.animateFade(100, new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {
						//nix
					}
				}, 200);
			}
		}, 200);
	}

	/**
	 * Leert alle Input-Objekte
	 */
	public void resetAll() {
		this.newResourceTextItem.setValue("");
		this.resourceAwailabilitySlider.setValue(100f);
		this.newProductTextItem.setValue("");
		this.newOperationName.setValue("");
		this.newOperationResourceChooser.setValue("");
		this.newOperationDurationSpinner.setValue(0);
		for(Record r : this.newProductRightListGrid.getDataAsRecordList().toArray()) {
			this.newProductRightListGrid.removeData(r);
		}
		this.newOperationName2.setValue("");
		this.newOperationResourceChooser2.setValue("");
		this.newOperationDurationSpinner2.setValue(0);
		for(Record r : this.newVariantRightListGrid.getDataAsRecordList().toArray()) {
			this.newVariantRightListGrid.removeData(r);
		}
	}
	
	public TextItem getScenarioNameTextItem() {
		return scenarioNameTextItem;
	}

	public OperationListGrid getNewVariantRightListGrid() {
		return newVariantRightListGrid;
	}

	public TextItem getNewOperationName2() {
		return newOperationName2;
	}

	public SelectItem getNewVariantProductChooser() {
		return newVariantProductChooser;
	}

	public SelectItem getNewOperationResourceChooser2() {
		return newOperationResourceChooser2;
	}

	public SpinnerItem getNewOperationDurationSpinner2() {
		return newOperationDurationSpinner2;
	}

	public TransferImgButton getNewVariantArrow() {
		return newVariantArrow;
	}

	public ResourceListGrid getResourceListGrid() {
		return resourceListGrid;
	}

	public ProductListGrid getProductListGrid() {
		return productListGrid;
	}

	public Button getAbortButton() {
		return abortButton;
	}

	public MenuItem getAddResourceMenuItem() {
		return addResourceMenuItem;
	}

	public MenuItem getAddProductMenuItem() {
		return addProductMenuItem;
	}

	public TextItem getNewResourceTextItem() {
		return newResourceTextItem;
	}

	public TextItem getNewProductTextItem() {
		return newProductTextItem;
	}

	public TextItem getNewOperationName() {
		return newOperationName;
	}

	public Slider getResourceAwailabilitySlider() {
		return resourceAwailabilitySlider;
	}

	public SelectItem getNewOperationResourceChooser() {
		return newOperationResourceChooser;
	}

	public SpinnerItem getNewOperationDurationSpinner() {
		return newOperationDurationSpinner;
	}

	public TransferImgButton getNewProductArrow() {
		return newProductArrow;
	}

	public OperationListGrid getNewProductRightListGrid() {
		return newProductRightListGrid;
	}

	public Button getFinishButton() {
		return finishButton;
	}

	public Button getResetButton() {
		return resetButton;
	}

	public Button getIncludeButton() {
		return includeButton;
	}

	public DetailViewer getResourceDetailViewer() {
		return resourceDetailViewer;
	}

	public DetailViewer getProductDetailViewer() {
		return productDetailViewer;
	}

	public MenuItem getAddVariantMenuItem() {
		return addVariantMenuItem;
	}

	public MenuItem getDeleteResourceMenuItem() {
		return deleteResourceMenuItem;
	}

	public MenuItem getDeleteProductMenuItem() {
		return deleteProductMenuItem;
	}
	
	public Button getSubmitButton() {
		return submitButton;
	}

	public FileUploader getFileUpload() {
		return fileUpload;
	}

	/**
	 * ListGrid für Resourcen
	 */
	public class ResourceListGrid extends ListGrid {
		private final ListGridField rowNumberField;
		
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		private ResourceListGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			this.setShowHeader(false);
			this.setBorder("0px");
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setEmptyMessage("Right click to add a new resource.");
			
			this.setShowRowNumbers(true);
			this.rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);

			final ListGridField name = new ListGridField("name", "Name");
		
			this.setFields(name);
		}
		
		public ListGridField getRowNumberField() {
			return this.rowNumberField;
		}
	}

	/**
	 * ListGrid für Produkte
	 * @author Dennis
	 */
	public class ProductListGrid extends ListGrid {
		final ListGridField rowNumberField;
		
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		private ProductListGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			this.setShowHeader(false);
			this.setBorder("0px");
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setEmptyMessage("Right click to add a new product.");
			
			this.setShowRowNumbers(true);
			this.rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);

			final ListGridField name = new ListGridField("name", "Name");
			
			this.setFields(name);
		}
		
		public ListGridField getRowNumberField() {
			return this.rowNumberField;
		}
	}

	public class OperationContextMenu extends Menu {
		public OperationContextMenu(final OperationListGrid grid, final OperationRecord record, final Map<String, Boolean> predecessorMap) {
			MenuItem predecessorsItem = new MenuItem("Predecessors");
			MenuItem deleteOperation = new MenuItem("Remove operation");
			deleteOperation.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(MenuItemClickEvent event) {
					grid.remove(record);
				}
			});
			Menu predecessorsMenu = new Menu();
			MenuItem predecessorItem;
			for(String predecessorName : predecessorMap.keySet()) {
				predecessorItem = new MenuItem(predecessorName);
				predecessorItem.setChecked(predecessorMap.get(predecessorName));
				predecessorItem.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(MenuItemClickEvent event) {
						((MenuItem)event.getSource()).setChecked(!((MenuItem)event.getSource()).getChecked());
						predecessorMap.put(((MenuItem)(event.getSource())).getTitle(), ((MenuItem)event.getSource()).getChecked());
						
						List<Operation> predecessorList = new ArrayList<Operation>();
						for(ListGridRecord r : grid.getRecords()) {
							for(String str : predecessorMap.keySet()) {
								if(predecessorMap.get(str) && str.equals(((OperationRecord)r).getName())) {
									predecessorList.add(((OperationRecord)r).getOperation());
								}
							}
						}
						record.getOperation().setPredecessors(predecessorList);
					}
				});
				predecessorsMenu.addItem(predecessorItem);
			}
			predecessorsItem.setSubmenu(predecessorsMenu);
			this.addItem(predecessorsItem);
			this.addItem(deleteOperation);
		}
	}
	
	/**
	 * ListGrid für Operationen
	 * @author Dennis
	 * FIXME: Testen vor Upload
	 */
	public class OperationListGrid extends ListGrid {
		final ListGridField rowNumberField;
		final Map<String, Map<String, Boolean>> predecessorMap;
		
		
		/**
		 * Konstruktor
		 */
		private OperationListGrid() {
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			this.setShowHeader(false);
			this.setBorder("0px");
			this.setEmptyMessage("Add operations.");
			this.setSelectionType(SelectionStyle.NONE);
			
			this.setShowRowNumbers(true);
			this.rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField name = new ListGridField("name", "Name");
			
			this.setFields(name);
			
predecessorMap = new HashMap<String, Map<String, Boolean>>();
			
			this.addCellContextClickHandler(new CellContextClickHandler() {
				@Override
				public void onCellContextClick(CellContextClickEvent event) {
					((OperationsInNewVariantListGrid)(event.getSource())).setContextMenu(new OperationContextMenu(OperationListGrid.this, (OperationRecord)(event.getRecord()), predecessorMap.get(((OperationRecord)(event.getRecord())).getName())));
				}
			});
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

		public ListGridField getRowNumberField() {
			return this.rowNumberField;
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