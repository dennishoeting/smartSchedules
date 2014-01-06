package de.bachelor.smartSchedules.client.view;

import main.java.com.bradrydzewski.gwtgantt.gantt.GanttChart;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTask;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTaskImpl;
import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für Popup bei alternativem/alten Schedule
 * @author Dennis
 */
public class SchedulePopupView extends Window {
	/**
	 * Größe
	 */
	public static final int LEFT_STACK_CONTENT_WIDTH = 610,
							RIGHT_STACK_CONTENT_WIDTH = 215, 
							CONTENT_HEIGHT = 530,
							TREE_HEIGHT = 409, 
							RESSOURCE_LIST_WIDTH = 150, 
							GANTT_HEIGHT = 520;
	
	/**
	 * Farbe
	 */
	public static final String GRAY_COLOR = "#c0c3c7",
							   LIGHT_GRAY_COLOR = "#e0e3e7",
							   LIGHT_GREEN_COLOR = "#ddffdd";

	/**
	 * Button
	 */
	private final Button drillDownButton, rollUpButton;
	
	/**
	 * Baumstruktor
	 */
	private final TreeGrid treeCanvas;

	/**
	 * DetailViewer
	 */
	private final DetailViewer keyFigureViewer,
							   algorithmInformationsViewer;
	
	/**
	 * KeyFigureTab
	 */
	private final Tab keyFigureTab;
	
	/**
	 * Checkbox
	 */
	private final CheckboxItem checkShowConnectors;
	
	/*
	 * Gantt
	 */
	/**
	 * Resourcenliste
	 */
	private final ResourceListGrid resourceListGrid;
	/**
	 * Gantt DataProvider
	 */
	private ListDataProvider<Task> dataProvider;
	/**
	 * Gantt SelectionModel
	 */
	private SelectionModel<Task> selectionModel;
	/**
	 * Gantt TaskProvider
	 */
	private ProvidesTask<Task> taskProvider;
	/**
	 * Gantt Diagramm
	 */
	private GanttChart<Task> gantt;
	
	/**
	 * Konstruktor
	 * @param title Titel
	 */
	public SchedulePopupView(String title) {
		/*
		 * Initialisierung
		 */
		this.setTitle(title);
		this.setWidth(900);
		this.setHeight(600);
		this.setTop(100);
		this.setLeft(100);
		
		/*
		 * Gantt
		 */
		this.dataProvider = new ListDataProvider<Task>();
		this.selectionModel = new SingleSelectionModel<Task>();
		this.taskProvider = new ProvidesTaskImpl();
		this.gantt = new GanttChart<Task>(taskProvider);
		this.gantt.setSelectionModel(selectionModel);
		this.dataProvider.addDataDisplay(gantt);
		
		/*
		 * Hauptordnung
		 */
		final HorizontalStack mainStack = new HorizontalStack(
				HorizontalStack.STACK_TYPE_MAIN_STACK);
		final HorizontalStack ganttStack = new HorizontalStack();
		final Canvas ganttCanvas = new Canvas();
		final VerticalStack rightStack = new VerticalStack(
				VerticalStack.STACK_TYPE_SECOUND_ORDER);
		rightStack.setPadding(0);
		
		/*
		 * Resourcenliste
		 */
		final SectionStack sectionStack = new SectionStack();
		sectionStack.setWidth(RESSOURCE_LIST_WIDTH);
		sectionStack.setHeight(GANTT_HEIGHT+10);
		final SectionStackSection timeLabelSection = new SectionStackSection("Time"); 
		timeLabelSection.setCanCollapse(false);
		timeLabelSection.setExpanded(false);
		final SectionStackSection resourceListSection = new SectionStackSection("Resources");  
		resourceListSection.setCanCollapse(false);
		resourceListSection.setExpanded(true);
		this.resourceListGrid = new ResourceListGrid();
		resourceListSection.addItem(resourceListGrid);
		sectionStack.addSection(timeLabelSection);
		sectionStack.addSection(resourceListSection);
		/*
		 * Gantt
		 */
		ganttCanvas.setOverflow(Overflow.HIDDEN);
		ganttCanvas.setWidth(LEFT_STACK_CONTENT_WIDTH-RESSOURCE_LIST_WIDTH);
		ganttCanvas.setBorder("1px solid " + GRAY_COLOR);
		this.gantt.setHeight(GANTT_HEIGHT + "px");
		this.gantt.setWidth(LEFT_STACK_CONTENT_WIDTH-RESSOURCE_LIST_WIDTH+"px");
		ganttCanvas.addChild(this.gantt);
		ganttStack.addMembers(sectionStack, ganttCanvas);

		/*
		 * Rechte Seite
		 */
		final TabSet rightTabSet = new TabSet();
		rightTabSet.setTabBarPosition(Side.TOP);
		rightTabSet.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		rightTabSet.setHeight(TREE_HEIGHT);
		/*
		 * Struktur
		 */
		final Tab structureTab = new Tab("Orders");
		this.treeCanvas = new TreeGrid();
		this.treeCanvas.setBorder("0px");
		this.treeCanvas.setShowHeader(false);
		structureTab.setPane(treeCanvas);
		rightTabSet.addTab(structureTab);
		/*
		 * KeyFigures
		 */
		this.keyFigureTab = new Tab("Key figures");
		final VerticalStack dataStack = new VerticalStack();
		dataStack.setMembersMargin(5);
		this.keyFigureViewer = new DetailViewer();
		this.algorithmInformationsViewer = new DetailViewer();
		this.algorithmInformationsViewer.setFields(new DetailViewerField("startDate", "Start"),
												   new DetailViewerField("dueDate", "Due"),
												   new DetailViewerField("duration", "Duration"),
												   new DetailViewerField("algName","Alg."));
		dataStack.addMembers(keyFigureViewer, algorithmInformationsViewer);
		keyFigureTab.setPane(dataStack);
		rightTabSet.addTab(keyFigureTab);
		final HLine line2 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * Buttons
		 */
		this.drillDownButton = new Button("Drill down",
				RIGHT_STACK_CONTENT_WIDTH);
		this.rollUpButton = new Button("Roll up", RIGHT_STACK_CONTENT_WIDTH);
		final DynamicForm checkboxForm = new DynamicForm();
		this.checkShowConnectors = new CheckboxItem("predesessorLinks");
		this.checkShowConnectors.setTitle("Show predecessor links");
		
		checkboxForm.setItems(checkShowConnectors);
		rightStack.addMembers(rightTabSet, line2, drillDownButton, rollUpButton, checkboxForm);
		mainStack.addMembers(ganttStack, rightStack);

		this.addItem(mainStack);

		this.draw();
	}

	public ListDataProvider<Task> getDataProvider() {
		return this.dataProvider;
	}
	
	public GanttChart<Task> getGantt() {
		return this.gantt;
	}

	public Button getDrillDownButton() {
		return this.drillDownButton;
	}

	public Button getRollUpButton() {
		return this.rollUpButton;
	}
	
	public CheckboxItem getCheckShowConnectors() {
		return this.checkShowConnectors;
	}
	
	public TreeGrid getTreeCanvas() {
		return this.treeCanvas;
	}

	public ResourceListGrid getResourceListGrid() {
		return this.resourceListGrid;
	}
	
	public DetailViewer getKeyFigureViewer() {
		return keyFigureViewer;
	}

	public DetailViewer getAlgorithmInformationsViewer() {
		return algorithmInformationsViewer;
	}

	/**
	 * ListGrid für Resourcen (Resourcenliste)
	 * @author Dennis
	 */
	public class ResourceListGrid extends ListGrid {
		/**
		 * Konstruktor
		 */
		public ResourceListGrid() {
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			this.setShowHeader(false);
			this.setBorder("0px");
			
			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			ListGridField name = new ListGridField("name", "Name");
			
			this.setHeaderSpans(new HeaderSpan("Resources", new String[] {"name"}));
			
			this.setFields(name);
		}
	}

}
