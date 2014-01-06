package de.bachelor.smartSchedules.client.view;

import main.java.com.bradrydzewski.gwtgantt.gantt.GanttChart;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTask;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTaskImpl;
import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
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

import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;
import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für aktuellen Ablaufplan (Hauptfenster)
 * @author Dennis
 */
public class CurrentScheduleView extends Tab {

	/**
	 * Größe
	 */
	public static final int LEFT_STACK_CONTENT_WIDTH = 700,
							RIGHT_STACK_CONTENT_WIDTH = 215, 
							CONTENT_HEIGHT = 550,
							TREE_HEIGHT = 309, 
							RESSOURCE_LIST_WIDTH = 150, 
							GANTT_HEIGHT = 540;
	
	/**
	 * Farbe
	 */
	public static final String GRAY_COLOR = "#c0c3c7",
							   LIGHT_GRAY_COLOR = "#e0e3e7",
							   LIGHT_GREEN_COLOR = "#ddffdd";

	/**
	 * Label für eingetroffene Events (Wechselt Farbe)
	 */
	private final Label eventsArrivedLabel;
	
	/**
	 * Buttons
	 */
	private final Button updateGanttButton, 
						 drillDownButton, 
						 rollUpButton;
	
	/**
	 * Baumstuktur
	 */
	private final TreeGrid treeCanvas;
	
	/**
	 * CheckBox für Konnektoren
	 */
	private final CheckboxItem checkShowConnectors;
	
	/**
	 * Resourcenliste
	 */
	private final ResourceListGrid resourceListGrid;

	/**
	 * DetailViewer
	 */
	private final DetailViewer keyFigureViewer,
							   algorithmInformationsViewer;
	/**
	 * Tab für Baumstruktor/KeyData
	 */
	private final Tab dataTab;
	
	/**
	 * Button für alternative Pläne
	 */
	private final Button checkOtherSchedulesButton;
	
	/*
	 * Gantt
	 */
	/**
	 * Gantt DataProdiver
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

	private Canvas ganttCanvas;
	
	/**
	 * Konstruktor
	 */
	public CurrentScheduleView() {
		this.setTitle("Current Schedule");

		/*
		 * Gantt definieren
		 */
		this.dataProvider = new ListDataProvider<Task>();
		this.selectionModel = new SingleSelectionModel<Task>();
		this.taskProvider = new ProvidesTaskImpl();
		this.gantt = new GanttChart<Task>(taskProvider);
		this.gantt.setSelectionModel(selectionModel);
		this.dataProvider.addDataDisplay(gantt);

		/*
		 * Hauptordnungselemente
		 */
		final HorizontalStack mainStack = new HorizontalStack(
				HorizontalStack.STACK_TYPE_MAIN_STACK);
		final HorizontalStack ganttStack = new HorizontalStack();
		ganttCanvas = new Canvas();
		final VerticalStack rightStack = new VerticalStack(
				VerticalStack.STACK_TYPE_SECOUND_ORDER);
		rightStack.setPadding(0);
		
		/*
		 * Linker Bereich
		 */
		final VerticalStack leftStack = new VerticalStack();
		/*
		 * Resourcenliste
		 */
		final SectionStack sectionStack = new SectionStack();
		sectionStack.setWidth(RESSOURCE_LIST_WIDTH);
		sectionStack.setHeight(GANTT_HEIGHT-65);
		final SectionStackSection timeLabelSection = new SectionStackSection("Time"); 
		timeLabelSection.setCanCollapse(false);
		timeLabelSection.setExpanded(false);
		final SectionStackSection resourceListSection = new SectionStackSection("Resources");  
		resourceListSection.setCanCollapse(false);
		resourceListSection.setExpanded(true);
		this.resourceListGrid = new ResourceListGrid();
		this.resourceListGrid.setOverflow(Overflow.HIDDEN);
		resourceListSection.addItem(resourceListGrid);
		sectionStack.addSection(timeLabelSection);
		sectionStack.addSection(resourceListSection);
		/*
		 * Diagramm
		 */
		ganttCanvas.setOverflow(Overflow.HIDDEN);
		ganttCanvas.setWidth(LEFT_STACK_CONTENT_WIDTH-RESSOURCE_LIST_WIDTH);
		ganttCanvas.setHeight(GANTT_HEIGHT-50);
		ganttCanvas.setBorder("1px solid " + GRAY_COLOR);
		gantt.setHeight((GANTT_HEIGHT-50) + "px");
		gantt.setWidth(LEFT_STACK_CONTENT_WIDTH-RESSOURCE_LIST_WIDTH+"px");
		ganttCanvas.addChild(gantt);
		ganttStack.addMembers(sectionStack, ganttCanvas);
		/*
		 * Unterer Bereich mit Button für alternative Pläne
		 */
		final HorizontalStack lowerStack = new HorizontalStack();
		lowerStack.setAutoHeight();
		lowerStack.setPadding(13);
		int altSize = (ScenarioPresenter.getInstance().getScenario().getCurrentSchedules().size()-1);
		this.checkOtherSchedulesButton = new Button("There "+((altSize==1)?"is":"are")+" <b>"+((altSize>0)?(altSize+"&nbsp;"):"no&nbsp;")+"other schedule"+((altSize>1)?("s"):"")+"</b>."+((altSize>0)?(" Check them."):""), 300);
		this.checkOtherSchedulesButton.setDisabled(!(altSize>0));
		lowerStack.addMembers(this.checkOtherSchedulesButton);
		leftStack.addMembers(ganttStack, lowerStack);

		/*
		 * Rechter Bereich
		 * newEvents-Label
		 */
		this.eventsArrivedLabel = new Label("0 new events arrived");
		this.eventsArrivedLabel.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		this.eventsArrivedLabel.setHeight(35);
		this.eventsArrivedLabel.setBorder("1px solid black");
		this.eventsArrivedLabel.setAlign(Alignment.CENTER);
		this.eventsArrivedLabel.setBackgroundColor(LIGHT_GRAY_COLOR);
		/*
		 * Button
		 */
		this.updateGanttButton = new Button("Update Gantt",
				RIGHT_STACK_CONTENT_WIDTH);
		final HLine line1 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * TabSet
		 */
		final TabSet rightTabSet = new TabSet();
		rightTabSet.setTabBarPosition(Side.TOP);
		rightTabSet.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		rightTabSet.setHeight(TREE_HEIGHT);
		/*
		 * Baumstruktur-Tab
		 */
		final Tab structureTab = new Tab("Orders");
		this.treeCanvas = new TreeGrid();
		this.treeCanvas.setSelectionType(SelectionStyle.SINGLE);
		this.treeCanvas.setBorder("0px");
		this.treeCanvas.setShowHeader(false);
		structureTab.setPane(treeCanvas);
		rightTabSet.addTab(structureTab);
		/*
		 * KeyData-Tab
		 */
		this.dataTab = new Tab("Key data");
		final VerticalStack dataStack = new VerticalStack();
		dataStack.setMembersMargin(5);
		this.keyFigureViewer = new DetailViewer();
		this.algorithmInformationsViewer = new DetailViewer();
		this.algorithmInformationsViewer.setFields(new DetailViewerField("startDate", "Start"),
												   new DetailViewerField("dueDate", "Due"),
												   new DetailViewerField("duration", "Duration"),
												   new DetailViewerField("algName","Alg."));
		dataStack.addMembers(keyFigureViewer, algorithmInformationsViewer);
		dataTab.setPane(dataStack);
		rightTabSet.addTab(dataTab);
		final HLine line2 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * Buttons und CheckBox
		 */
		this.drillDownButton = new Button("Drill down",
				RIGHT_STACK_CONTENT_WIDTH);
		this.rollUpButton = new Button("Roll up", RIGHT_STACK_CONTENT_WIDTH);
		final DynamicForm checkboxForm = new DynamicForm();
		this.checkShowConnectors = new CheckboxItem("predesessorLinks");
		this.checkShowConnectors.setTitle("Show predecessor links");
		
		checkboxForm.setItems(checkShowConnectors);
		rightStack.addMembers(eventsArrivedLabel, updateGanttButton, line1,
				rightTabSet, line2, drillDownButton, rollUpButton, checkboxForm);
		mainStack.addMembers(leftStack, rightStack);
		
		this.setPane(mainStack);
	}
	
	/**
	 * Setzt das newEvents-Label neu
	 * @param amount
	 */
	public void setNewEventsArived(int amount) {
		this.eventsArrivedLabel.setContents(amount + " new events arrived");
		if(amount>0) {
			this.eventsArrivedLabel.setBackgroundColor(LIGHT_GREEN_COLOR);
		} else {
			this.eventsArrivedLabel.setBackgroundColor(LIGHT_GRAY_COLOR);
		}
	}
	
	public Button getUpdateGanttButton() {
		return this.updateGanttButton;
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
	
	public ResourceListGrid getResourceListGrid() {
		return this.resourceListGrid;
	}
	
	public TreeGrid getTreeCanvas() {
		return this.treeCanvas;
	}
	
	public GanttChart<Task> getGantt() {
		return this.gantt;
	}
	
	public ListDataProvider<Task> getDataProvider() {
		return this.dataProvider;
	}
	
	public ProvidesTask<Task> getTaskProvider() {
		return this.taskProvider;
	}
	
	public Button getCheckOtherSchedulesButton() {
		return this.checkOtherSchedulesButton;
	}
	
	public DetailViewer getKeyFigureViewer() {
		return keyFigureViewer;
	}
	
	public DetailViewer getAlgorithmInformationsViewer() {
		return algorithmInformationsViewer;
	}

	public Tab getKeyFigureTab() {
		return this.dataTab;
	}
	
	public SelectionModel<Task> getSelectionModel() {
		return selectionModel;
	}

	public Canvas getGanttCanvas() {
		return ganttCanvas;
	}

	/**
	 * ListGrid für die Resourcen
	 * @author Dennis
	 */
	public class ResourceListGrid extends ListGrid {
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
