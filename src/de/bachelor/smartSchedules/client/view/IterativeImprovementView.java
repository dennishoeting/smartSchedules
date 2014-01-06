package de.bachelor.smartSchedules.client.view;

import main.java.com.bradrydzewski.gwtgantt.gantt.GanttChart;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTask;
import main.java.com.bradrydzewski.gwtgantt.gantt.ProvidesTaskImpl;
import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tab.Tab;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.KeyFigureCanvas;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * View für iterative Verbesserung
 * @author Dennis
 */
public class IterativeImprovementView extends Tab {
	/**
	 * Größe
	 */
	public static final int LEFT_STACK_CONTENT_WIDTH = 700,
							RIGHT_STACK_CONTENT_WIDTH = 210, 
							CONTENT_HEIGHT = 550,
							RESSOURCE_LIST_WIDTH = 150, 
							GANTT_HEIGHT = 540,
							KEY_FIGURE_HEIGHT = 30, 
							DESCRIPTION_HEIGHT = 150;
	/**
	 * Farbe
	 */
	public static final String GRAY_COLOR = "#c0c3c7",
							   LIGHT_GRAY_COLOR = "#e0e3e7";

	/**
	 * Stack für KeyFigureCanvasses
	 */
	private final VerticalStack keyFigureStack;
	
	/**
	 * Beschreibungsfenster
	 */
	private final Window description;
	
	/**
	 * Beschreibung
	 */
	private final HTMLPane descriptionPane;
	
	/**
	 * Button
	 */
	private final Button startImprovementButton;

	/**
	 * Spinner
	 */
	private final SpinnerItem durationChooser;
	
	/**
	 * SelectItem
	 */
	private final SelectItem wrapperChooser, changerChooser;
	
	/**
	 * Popup
	 */
	private final IterativeImprovementPopup window;

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
	 */
	public IterativeImprovementView() {
		this.setTitle("Iterative Improvement");
		
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
		 * Popuü
		 */
		this.window = new IterativeImprovementPopup();

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
		 * Resourcenliste
		 */
		final SectionStack sectionStack = new SectionStack();
		sectionStack.setWidth(RESSOURCE_LIST_WIDTH);
		sectionStack.setHeight(GANTT_HEIGHT);
		sectionStack.setHeaderHeight(26);
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
		gantt.setHeight(GANTT_HEIGHT + "px");
		gantt.setWidth(LEFT_STACK_CONTENT_WIDTH-RESSOURCE_LIST_WIDTH+"px");
		ganttCanvas.addChild(gantt);
		ganttStack.addMembers(sectionStack, ganttCanvas);

		/*
		 * Beschreibung
		 */
		this.description = new Window();
		this.description.setTitle("Description");
		this.description.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		this.description.setHeight(DESCRIPTION_HEIGHT);
		this.description.setShowCloseButton(false);
		this.description.setCanDragReposition(false);
		this.description.setAnimateMinimize(true);
		this.description.setMinimizeTime(500);
		this.descriptionPane = new HTMLPane();
		this.descriptionPane.setPadding(15);
		this.description.addItem(descriptionPane);
		final HLine line1 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * KeyFigures
		 */
		this.keyFigureStack = new VerticalStack();
		this.keyFigureStack.setMembersMargin(10);
		this.keyFigureStack.setPadding(0);
		this.keyFigureStack.setShowEdges(false);  
		final HLine line2 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * Auswahl von Algorithmus und Dauer
		 */
		final DynamicForm chooserForm = new DynamicForm();
		chooserForm.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		this.wrapperChooser = new SelectItem("Wrapper");
		this.wrapperChooser.setWidth(RIGHT_STACK_CONTENT_WIDTH - 100);
		this.changerChooser = new SelectItem("Changer");
		this.changerChooser.setWidth(RIGHT_STACK_CONTENT_WIDTH - 100);
		this.durationChooser = new SpinnerItem("duration", "Duration");
		this.durationChooser.setHint("in&nbsp;min.");
		this.durationChooser.setWrapTitle(false);
		this.durationChooser.setWidth(RIGHT_STACK_CONTENT_WIDTH - 100);
		this.durationChooser.setValue(5);
		chooserForm.setItems(wrapperChooser, changerChooser, durationChooser);
		final HLine line3 = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		/*
		 * Button
		 */
		this.startImprovementButton = new Button("Start improvement", RIGHT_STACK_CONTENT_WIDTH);
        
		rightStack.addMembers(this.description, line1, this.keyFigureStack, line2, chooserForm, line3, this.startImprovementButton);
		mainStack.addMembers(ganttStack, rightStack);
		this.setPane(mainStack);
	}

	/**
	 * Generiert KeyFigureCanvas, der übergebene Kennzahl repräsentiert
	 * @param keyFigure Kennzahl
	 * @return KeyFigureCanvas
	 */
	public KeyFigureCanvas generateKeyFigureCanvas(KeyFigure keyFigure) {
		KeyFigureCanvas c = new KeyFigureCanvas(keyFigure, RIGHT_STACK_CONTENT_WIDTH, KEY_FIGURE_HEIGHT);
		c.setCanDragReposition(false);
		return c;
	}
	
	/**
	 * Fügt dem KeyFigureStack die übergebenen KeyFigureCanvasses hinzu
	 * @param keyFigures
	 */
	public void addKeyFigureCanvasses(KeyFigureCanvas... keyFigures) {
		for(KeyFigureCanvas kf : keyFigures) {
			this.keyFigureStack.addMember(kf);
		}
	}

	public VerticalStack getKeyFigureStack() {
		return this.keyFigureStack;
	}
	
	public GanttChart<Task> getGantt() {
		return gantt;
	}
	
	public ProvidesTask<Task> getTaskProvider() {
		return this.taskProvider;
	}
	
	public ListDataProvider<Task> getDataProvider() {
		return this.dataProvider;
	}
	
	public Button getStartImprovementButton() {
		return this.startImprovementButton;
	}

	public void setDataProvider(ListDataProvider<Task> dataProvider) {
		this.dataProvider = dataProvider;
	}

	public SelectionModel<Task> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SelectionModel<Task> selectionModel) {
		this.selectionModel = selectionModel;
	}


	public void setTaskProvider(ProvidesTask<Task> taskProvider) {
		this.taskProvider = taskProvider;
	}

	public SpinnerItem getDurationChooser() {
		return durationChooser;
	}

	public SelectItem getWrapperChooser() {
		return wrapperChooser;
	}
	
	public SelectItem getChangerChooser() {
		return changerChooser;
	}

	public IterativeImprovementPopup getPopup() {
		return this.window;
	}

	public ResourceListGrid getResourceListGrid() {
		return resourceListGrid;
	}

	/**
	 * ListGrid für Resourcen (Resourcenliste links)
	 * @author Dennis
	 */
	public class ResourceListGrid extends ListGrid {
		public ResourceListGrid() {
			this.setHeaderHeight(50);
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
